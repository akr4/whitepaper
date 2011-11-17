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
package whitepaper.sql.jta

import whitepaper.sql._
import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalamock.scalatest.MockFactory
import org.scalamock.ProxyMockFactory
import javax.transaction.UserTransaction

class TestingJndiFinder[A](a: A) extends JndiFinder {
  def find[B](name: String): B = a.asInstanceOf[B]
}


class JtaSuite extends FunSuite with BeforeAndAfter with MockFactory with ProxyMockFactory {

  test("commit when no error") {
    val tx = mock[UserTransaction]
    tx expects 'begin
    tx expects 'commit
    
    val tm = new JtaTransactionManager(TestingHsqldbConnectionFactory, new TestingJndiFinder(tx))
    val result = tm.withTransaction { _ => true }

    assert(result === true)
  }

  test("rollback when error") {
    val tx = mock[UserTransaction]
    tx expects 'begin
    tx expects 'rollback
    
    val tm = new JtaTransactionManager(TestingHsqldbConnectionFactory, new TestingJndiFinder(tx))
    intercept[RuntimeException] {
      tm.withTransaction { _ => if (1 == 1) throw new RuntimeException; true }
    }
  }
}

