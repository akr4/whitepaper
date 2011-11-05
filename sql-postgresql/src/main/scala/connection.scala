package whitepaper.sql.postgresql

import whitepaper.sql._

class PostgresqlConnectionFactory(host: String, database: String) extends JdbcDriverConnectionFactory {
  override val url = "jdbc:postgresql://%s/%s".format(host, database)
  override val driverClass = classOf[org.postgresql.Driver]
}

