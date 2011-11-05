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

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers

class PoolSuite extends FunSuite with BeforeAndAfter {

  val TEST_SQL = "SELECT 1 FROM INFORMATION_SCHEMA.SYSTEM_USERS"

  test("get should return a connection") {
    val pool = new PoolingConnectionFactory(new TestingHsqldbConnectionFactory, 1, 1, TEST_SQL)

    val conn = pool.newConnection
    assert(conn != null)
  }

  test("get should fail if there's no connection") {
    val pool = new PoolingConnectionFactory(new TestingHsqldbConnectionFactory, 1, 1, TEST_SQL)

    pool.newConnection
    intercept[org.apache.commons.dbcp.SQLNestedException] { pool.newConnection }
  }
}

