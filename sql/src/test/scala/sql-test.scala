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

class SqlSuite extends FunSuite with BeforeAndAfter {

  val db = new Database(new LocalTransactionManager(new TestingHsqldbConnectionFactory))

  before {
    db.ddl("create table emp (id integer primary key, name varchar(30))")
    db.withTransaction { s =>
      s.execute("insert into emp (id, name) values (?, ?)", 1, "name1")
      s.execute("insert into emp (id, name) values (?, ?)", 2, "name2")
    }
  }

  after {
    db.ddl("drop table emp")
  }

  test("select should return existing records") {
    val result = db.withTransaction { _.select("select id, name from emp") { _.int(1)}}
    assert(result.size === 2)
    assert(result(0) === 1)
    assert(result(1) === 2)
  }

  test("selectOne should return None if not found") {
    val result = db.withTransaction { _.selectOne("select * from emp where id = -1") { _ => }}
    assert(result === None)
  }

  test("selectOne should return Some only if one record found") {
    val result = db.withTransaction { _.selectOne("select id from emp where id = 1") { _.int(1) }}
    assert(result === Some(1))
  }

  test("selectOne should throw TooManyRowsException if more than one records found") {
    intercept[TooManyRowsException] {
      db.withTransaction { _.selectOne("select id from emp") { _.int(1) }}
    }
  }

  test("execute can insert") {
    db.withTransaction { _.execute("insert into emp (id, name) values (?, ?)", 100, "name100") }
    val name = db.withTransaction { _.selectOne("select name from emp where id = ?", 100) { _.string(1) }}.get
    assert(name === "name100")
  }

  test("execute can update") {
    db.withTransaction { _.execute("update emp set name = ? where id = 1", "name999") }
    val name = db.withTransaction { _.selectOne("select name from emp where id = ?", 1) { _.string(1) }}.get
    assert(name === "name999")
  }

  test("execute can delete") {
    db.withTransaction { _.execute("delete from emp where id = 1") }
    val name = db.withTransaction { _.selectOne("select name from emp where id = ?", 1) { _.string(1) }}
    assert(name === None)
  }
}

