/*
 * Copyright 2011 Akira Ueda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package whitepaper.sql

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException }
import scala.util.control.Exception._  

case class TooManyRowsException(expected: Int, actual: Int) extends Exception

class Session(conn: Connection) extends Using {
  def select[A](sql: String, params: Any*)(f: Row => A): List[A] = {
    using(conn.prepareStatement(sql)) { stmt =>
      updateParams(stmt, params: _*)
      using(stmt.executeQuery) { new ResultSetIterator(_).map(rs => f(new Row(this, rs))).toList }
    }
  }

  def selectOne[A](sql: String, params: Any*)(f: Row => A): Option[A] = {
    val result = select(sql, params: _*)(f)
    result match {
      case x :: Nil => Some(x)
      case Nil => None
      case _ => throw new TooManyRowsException(1, result.size)
    }
  }

  def execute(sql: String, params: Any*) {
    using(conn.prepareStatement(sql)) { stmt =>
      updateParams(stmt, params: _*)
      stmt.executeUpdate()
    }
  }

  private def updateParams(stmt: PreparedStatement, params: Any*) {
    for (pair <- params.zipWithIndex) {
      pair match {
        case (param: String, n) => stmt.setString(n + 1, param)
        case (param: Int, n) => stmt.setInt(n + 1, param)
        case (param: Long, n) => stmt.setLong(n + 1, param)
        case _ => throw new IllegalArgumentException
      }
    }
  }
}

class Row(session: Session, rs: ResultSet) {
  def int(n: Int): Int = rs.getInt(n)
  def string(n: Int): String = rs.getString(n)
  def manyToOne[A, PK](n: Int)(select: (Session, PK) => A): ManyToOne[A, PK] =
    new ManyToOne[A, PK](session, rs.getObject(n).asInstanceOf[PK], select)
}

trait Using {
  def using[A <: { def close() }, B](resource: A)(f: A => B): B = try { f(resource) } finally { resource.close }
}

class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
  def hasNext: Boolean = rs.next
  def next(): ResultSet = rs
}

class ManyToOne[A, PK](session: Session, fk: PK, select: (Session, PK) => A) {
  def get: A = select(session, fk)
}

