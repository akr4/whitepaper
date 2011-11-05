package whitepaper.sql

class TestingHsqldbConnectionFactory extends JdbcDriverConnectionFactory {
  override val url = "jdbc:hsqldb:mem:hsqldb:test"
  override val driverClass = classOf[org.hsqldb.jdbc.JDBCDriver]
}

