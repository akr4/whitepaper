package whitepaper.cache

import grizzled.slf4j.Logging

/**
 * @tparam CK type of cache key
 * @tparam K type of key
 * @tparam V type of value
 */
trait Cache[K, V, CK] {
  self: Logging => // TODO: eliminate logger implementation dependency

  protected val cacheKeyGenerator: CacheKeyGenerator[CK]

  /** Returns value corresponding to the given key
   *
   * @param key key
   * @return value
   */
  def get(key: CK): Option[V]

  /** Sets cache value
   *
   * @param key key
   * @param value value
   */
  def put(key: CK, value: V): Unit

  /** Returns value if a corresponding value exists or returns the result of given f after saving it
   *
   * @param key a value which is used to generate/find return value by f.
   *   This is different from a cache key in almost cases.
   * @param f function to get new value
   * @return cached value or the result of given f
   */
  final def withCache(key: K)(f: => V): V = {
    get(cacheKey(key)) match {
      case Some(value) =>
        debug("cache hit: %s".format(key))
        value
      case None =>
        val newValue = f
        info("caching: %s".format(key))
        put(cacheKey(key), newValue)
        newValue
    }
  }

  final protected def cacheKey(key: K): CK = cacheKeyGenerator.generate(key)
}

/** Cache implementation using Map
 *
 * Note: This holds all values in memory and is rot suitable for non-small values
 */
class MapCache[K, V, CK](val cacheKeyGenerator: CacheKeyGenerator[CK]) extends Cache[K, V, CK] with Logging {
  private val map = scala.collection.mutable.Map.empty[CK, V]

  def get(key: CK): Option[V] = {
    map.get(key)
  }
  def put(key: CK, value: V) {
    map += (key-> value)
  }

  override def toString(): String = "MapCache(%s)".format(map)
}
object MapCache {
  def apply[K, V, CK](cacheKeyGenerator: CacheKeyGenerator[CK]) = new MapCache[K, V, CK](cacheKeyGenerator)
}

