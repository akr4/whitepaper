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

import java.sql.{ Connection, Driver, DriverManager }
import org.apache.commons.dbcp.{ ConnectionFactory => CF, PoolableConnectionFactory, PoolingDataSource }

// TODO: move validationQuery to ConnectionFactory or something to know about DB
class PoolingConnectionFactory(underlying: ConnectionFactory, validationQuery: String) extends ConnectionFactory {

  val pool = new GenericObjectPool(null)
  val size = 5
  pool.setInitialSize(size)
  pool.setMaxActive(size)
  pool.setMinIdle(size)
  pool.setMaxIdle(size)
  pool.setMaxWait(5000)
  pool.setTestOnBorrow(true)

  val connectionFactory = new CommonsDbcpConnectionFactoryAdapter(underlying)
  val poolableConnectionFactory = new PoolableConnectionFactory(
    connectionFactory, pool, null, validationQuery, false, false 
  )
  val dataSource = new PoolingDataSource(connectionPool)
  
  final def newConnection: Connection = dataSource.getConnection()
}

class CommonsDbcpConnectionFactoryAdapter(underlying: ConnectionFactory) extends CF {
  def createConnection(): Connection = underlying.newConnection
}

