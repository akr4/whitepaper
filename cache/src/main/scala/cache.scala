package whitepaper.cache

import grizzled.slf4j.Logging

/**
 * @tparam K type of key
 * @tparam V type of value
 */
trait Cache[K, V] {
  self: Logging => // TODO: eliminate logger implementation dependency

  protected val cacheKeyGenerator: CacheKeyGenerator[_]

  /** Returns value corresponding to the given key */
  final def get(key: K): Option[V] = _get(cacheKey(key))

  /** Sets cache value */
  final def put(key: K, value: V) { _put(cacheKey(key), value) }

  /** Returns cached value by using cache key */
  protected def _get(cacheKey: Any): Option[V]

  /** Sets cache value by using cache key */
  protected def _put(cacheKey: Any, value: V): Unit

  /** Returns value if a corresponding value exists or returns the result of given f after saving it
   *
   * @param key a value which is used to generate/find return value by f.
   *   This is different from a cache key in almost cases.
   * @param f function to get new value
   * @return cached value or the result of given f
   */
  final def withCache(key: K)(f: => V): V = {
    get(key) match {
      case Some(value) =>
        debug("cache hit: %s".format(key))
        value
      case None =>
        val newValue = f
        info("caching: %s".format(key))
        put(key, newValue)
        newValue
    }
  }

  private def cacheKey(key: K): Any = cacheKeyGenerator.generate(key)
}

/** Cache implementation using Map
 *
 * Note: This holds all values in memory and is not suitable for non-small values
 */
class MapCache[K, V](val cacheKeyGenerator: CacheKeyGenerator[_]) extends Cache[K, V] with Logging {
  private val map = scala.collection.mutable.Map.empty[Any, V]

  def _get(key: Any): Option[V] = map.get(key)
  def _put(key: Any, value: V) { map += (key-> value) }

  override def toString(): String = "MapCache(%s)".format(map)
}
object MapCache {
  def apply[K, V](cacheKeyGenerator: CacheKeyGenerator[_]) = new MapCache[K, V](cacheKeyGenerator)
}

