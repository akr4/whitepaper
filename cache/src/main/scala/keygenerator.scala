package whitepaper.cache

/**
 * @tparam A type of cache key
 */
trait CacheKeyGenerator[A] {
  def generate(data: Any*): A
}

/** Generator which concatenate each parameter */
object ToStringCacheKeyGenerator extends CacheKeyGenerator[String] {
  def generate(data: Any*): String = data.toArray.deep.mkString(",")
}

/** Generator which uses hashCode */
object HashCodeCacheKeyGenerator extends CacheKeyGenerator[Int] {
  def generate(data: Any*): Int = data.toArray.deep.hashCode
}

