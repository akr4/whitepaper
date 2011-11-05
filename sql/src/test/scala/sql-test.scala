package whitepaper.sql

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers

class SqlSuite extends FunSuite with BeforeAndAfter {

  val db = new Database(new TestingHsqldbConnectionFactory)

  before {
    db.ddl("create table emp (id integer primary key, name varchar(30))")
    db.withSession { s =>
      s.execute("insert into emp (id, name) values (?, ?)", 1, "name1")
      s.execute("insert into emp (id, name) values (?, ?)", 2, "name2")
    }
  }

  after {
    db.ddl("drop table emp")
  }

  test("select should return existing records") {
    val result = db.withSession { _.select("select id, name from emp") { _.int(1)}}
    assert(result.size === 2)
    assert(result(0) === 1)
    assert(result(1) === 2)
  }

  test("selectOne should return None if not found") {
    val result = db.withSession { _.selectOne("select * from emp where id = -1") { _ => }}
    assert(result === None)
  }

  test("selectOne should return Some only if one record found") {
    val result = db.withSession { _.selectOne("select id from emp where id = 1") { _.int(1) }}
    assert(result === Some(1))
  }

  test("selectOne should throw TooManyRowsException if more than one records found") {
    intercept[TooManyRowsException] {
      db.withSession { _.selectOne("select id from emp") { _.int(1) }}
    }
  }

  test("execute can insert") {
    db.withSession { _.execute("insert into emp (id, name) values (?, ?)", 100, "name100") }
    val name = db.withSession { _.selectOne("select name from emp where id = ?", 100) { _.string(1) }}.get
    assert(name === "name100")
  }

  test("execute can update") {
    db.withSession { _.execute("update emp set name = ? where id = 1", "name999") }
    val name = db.withSession { _.selectOne("select name from emp where id = ?", 1) { _.string(1) }}.get
    assert(name === "name999")
  }

  test("execute can delete") {
    db.withSession { _.execute("delete from emp where id = 1") }
    val name = db.withSession { _.selectOne("select name from emp where id = ?", 1) { _.string(1) }}
    assert(name === None)
  }
}

