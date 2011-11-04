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

trait ConnectionFactory {
  def newConnection: Connection
}

class Session(conn: Connection) extends Using {
  def select[A](sql: String)(f: Row => A): Seq[A] = {
    using(conn.prepareStatement(sql)) { stmt =>
      using(stmt.executeQuery) { new ResultSetIterator(_).map(rs => f(new Row(this, rs))).toSeq }
    }
  }
  def selectOne[A](sql: String)(f: Row => A): Option[A] = select(sql)(f).headOption
}

class Database(connectionFactory: ConnectionFactory) extends Using {
  def inTransaction[A](f: Session => A): A = {
    val conn = connectionFactory.newConnection
    val session = new Session(conn)
    try {
      val result = f(session)
      conn.commit
      result
    } catch {
      case e: SQLException =>
        ignoring(classOf[SQLException]) { conn.rollback }
        throw e
    } finally {
      ignoring(classOf[SQLException]) opt conn.close
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

