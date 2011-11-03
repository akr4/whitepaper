package whitepaper.sql

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException, DriverManager, Driver }
import scala.util.control.Exception._  

object Types {
  type PK = Int
}

import Types._

trait ConnectionDefinition[A] {
  val url: String
  val driverClass: Class[A]
}

class PostgreSqlConnectionDefinition(host: String, database: String) extends ConnectionDefinition[org.postgresql.Driver] {
  val url = "jdbc:postgresql://%s/%s".format(host, database)
  override val driverClass = classOf[org.postgresql.Driver]
}

class Database[A <: Driver](connDef: ConnectionDefinition[A]) extends Using {
  def inTransaction[A](f: Query => A): A = {
    Class.forName(connDef.driverClass.getName())
    val conn = DriverManager.getConnection(connDef.url)
    conn.setAutoCommit(false)
    val query = new Query(conn)
    try {
      val result = f(query)
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

class Query(conn: Connection) extends Using {
  def select[A](sql: String)(f: Row => A): Iterator[A] = {
    // how to close stmt and rs when iterator is not used?
    val stmt = conn.prepareStatement(sql)
    val rs = stmt.executeQuery
    new ResultSetIterator(rs).map(rs => f(new Row(this, rs)))
  }
}

class Row(query: Query, rs: ResultSet) {
  def int(n: Int): Int = rs.getInt(n)
  def string(n: Int): String = rs.getString(n)
  def manyToOne[A](n: Int)(select: (Query, PK) => A): ManyToOne[A] = new ManyToOne[A](query, rs.getInt(n), select)
}

trait Using {
  def using[A <: { def close }, B](resource: A)(f: A => B): B = try { f(resource) } finally { resource.close }
}

class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
  def hasNext: Boolean = rs.next
  def next(): ResultSet = rs
}

class ManyToOne[A](query: Query, fk: PK, select: (Query, PK) => A) {
  def get: A = select(query, fk)
}

