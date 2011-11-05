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

