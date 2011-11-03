package whitepaper.sql

import java.sql.{ Connection, Driver, DriverManager }

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

