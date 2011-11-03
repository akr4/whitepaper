package whitepaper.cache

/**
 * @tparam A type of cache key
 */
trait CacheKeyGenerator[A] {
  def generate(data: Any*): A
}

object ToStringCacheKeyGenerator extends CacheKeyGenerator[String] {
  def generate(data: Any*): String = data.toArray.deep.mkString(",")
}

