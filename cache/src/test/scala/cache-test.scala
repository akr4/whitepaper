package whitepaper.cache

import grizzled.slf4j.Logging
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class CacheSuite extends FunSuite {
  test("caches value if there's no cache") {
    val cache = MapCache[String, String, String](ToStringCacheKeyGenerator)
    cache.withCache("key")("value")
    val result = cache.get("key").get
    assert(result === "value")
  }

  test("returns new value if there's no cache") {
    val cache = new MapCache[String, String, String](ToStringCacheKeyGenerator)
    val result = cache.withCache("key")("value")
    assert(result === "value")
  }

  test("returns cached value if there's cached one") {
    val cache = new MapCache[String, String, String](ToStringCacheKeyGenerator)
    cache.put("key", "value1")
    val result = cache.withCache("key")("value2")
    assert(result === "value1")
  }
}

