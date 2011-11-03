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

