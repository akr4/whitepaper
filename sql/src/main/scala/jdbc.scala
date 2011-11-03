package whitepaper.sql

import java.sql.{ Connection, PreparedStatement, ResultSet, SQLException, DriverManager, Driver }
import scala.util.control.Exception._  

trait ConnectionFactory {
  def newConnection: Connection
}

trait JdbcDriverConnectionFactory extends ConnectionFactory {
  protected val url: String
  protected val driverClass: Class[_ <: Driver]
  final def newConnection: Connection = {
    Class.forName(driverClass.getName)
    val conn = DriverManager.getConnection(url)
    conn.setAutoCommit(false)
    afterConnect(conn)
    conn
  }

  protected def afterConnect(conn: Connection) {}
}

class PostgresqlConnectionFactory(host: String, database: String) extends JdbcDriverConnectionFactory {
  override val url = "jdbc:postgresql://%s/%s".format(host, database)
  override val driverClass = classOf[org.postgresql.Driver]
}

class Session(conn: Connection) extends Using {
  def select[A](sql: String)(f: Row => A): Iterator[A] = {
    // how to close stmt and rs when iterator is not used?
    val stmt = conn.prepareStatement(sql)
    val rs = stmt.executeQuery
    new ResultSetIterator(rs).map(rs => f(new Row(this, rs)))
  }
}

class Database[A <: Driver](connectionFactory: ConnectionFactory) extends Using {
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

class Row(query: Session, rs: ResultSet) {
  def int(n: Int): Int = rs.getInt(n)
  def string(n: Int): String = rs.getString(n)
  def manyToOne[A, PK](n: Int)(select: (Session, PK) => A): ManyToOne[A, PK] =
    new ManyToOne[A, PK](query, rs.getObject(n).asInstanceOf[PK], select)
}

trait Using {
  def using[A <: { def close }, B](resource: A)(f: A => B): B = try { f(resource) } finally { resource.close }
}

class ResultSetIterator(rs: ResultSet) extends Iterator[ResultSet] {
  def hasNext: Boolean = rs.next
  def next(): ResultSet = rs
}

class ManyToOne[A, PK](query: Session, fk: PK, select: (Session, PK) => A) {
  def get: A = select(query, fk)
}

