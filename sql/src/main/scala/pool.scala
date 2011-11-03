package whitepaper.sql

import java.sql.{ Connection, Driver, DriverManager }
import org.apache.commons.dbcp.{ ConnectionFactory => CF, PoolableConnectionFactory, PoolingDataSource }

// TODO: move validationQuery to ConnectionFactory or something to know about DB
class PoolingConnectionFactory(underlying: ConnectionFactory, validationQuery: String) extends ConnectionFactory {

  val connectionPool = new GenericObjectPool(null)
  val connectionFactory = new CommonsDbcpConnectionFactoryAdapter(underlying)
  val poolableConnectionFactory = new PoolableConnectionFactory(
    connectionFactory, connectionPool, null, validationQuery, false, false 
  )
  val dataSource = new PoolingDataSource(connectionPool)
  
  final def newConnection: Connection = dataSource.getConnection()
}

class CommonsDbcpConnectionFactoryAdapter(underlying: ConnectionFactory) extends CF {
  def createConnection(): Connection = underlying.newConnection
}

