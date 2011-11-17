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

import scala.util.control.Exception._  

trait TransactionManager {
  def withTransaction[A](f: Session => A): A
  //def withNewTransaction[A](f: () => A): A
}

class LocalTransactionManager(connectionFactory: ConnectionFactory) extends TransactionManager with Using {
  def withTransaction[A](f: Session => A): A = {
    val conn = connectionFactory.newConnection
    val session = new Session(conn)
    try {
      val result = f(session)
      conn.commit
      result
    } catch {
      case e =>
        allCatch { conn.rollback }
        throw e
    } finally {
      allCatch { conn.close }
    }
  }

  //def withNewTransaction[A](f: () => A): A
}

