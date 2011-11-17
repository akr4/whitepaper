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

import java.sql.Connection
import javax.naming.InitialContext
import javax.sql.DataSource

trait JndiFinder {
  def find[A](name: String): A
}  

private[sql] object DefaultJndiFinder extends JndiFinder {
  def find[A](name: String): A = { 
    val ic = new InitialContext
    ic.lookup(name).asInstanceOf[A]
  } 
} 

class DataSourceConnectionFactory(name: String, jndiFinder: JndiFinder = DefaultJndiFinder) extends ConnectionFactory {
  def newConnection: Connection = {
    val ds = jndiFinder.find[DataSource](name)
    ds.getConnection
  }
}

