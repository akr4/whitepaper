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

trait JdbcDriverConnectionFactory extends ConnectionFactory {
  protected val url: String
  protected val driverClass: Class[_ <: Driver]
  protected val username: String
  protected val password: String

  final def newConnection: Connection = {
    Class.forName(driverClass.getName)
    val conn = DriverManager.getConnection(url)
    conn.setAutoCommit(false)
    afterConnect(conn)
    conn
  }

  protected def afterConnect(conn: Connection) {}
}

