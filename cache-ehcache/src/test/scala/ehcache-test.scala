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
package whitepaper.cache.ehcache

import whitepaper.cache._

import net.sf.ehcache.{ CacheManager, Cache => ECache, Element }

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.ShouldMatchers

class EhcacheSuite extends FunSuite with BeforeAndAfter {

  implicit val cacheKeyGenerator = ToStringCacheKeyGenerator

  val manager = CacheManager.getInstance()
  manager.addCache("cache1")

  before {
    manager.clearAll
  }

  test("caches value if there's no cache") {
    val cache = Ehcache[String, String]("cache1")
    cache.withCache("key")("value")
    val result = cache.get("key").get
    assert(result === "value")
  }

  test("returns new value if there's no cache") {
    val cache = Ehcache[String, String]("cache1")
    val result = cache.withCache("key")("value")
    assert(result === "value")
  }

  test("returns cached value if there's cached one") {
    val cache = Ehcache[String, String]("cache1")
    cache.put("key", "value1")
    val result = cache.withCache("key")("value2")
    assert(result === "value1")
  }

}

