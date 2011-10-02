package whitepaper.cache

import grizzled.slf4j.Logging

/**
 * @tparam K type of key
 * @tparam V type of value
 */
trait Cache[K, V] {
  self: Logging => // TODO: eliminate logger specific dependency

  /** Returns value corresponding to the given key
   *
   * @param key key
   * @return value
   */
  def get(key: K): Option[V]

  /** Sets cache value
   *
   * @param key key
   * @param value value
   */
  def put(key: K, value: V): Unit

  /** Returns value if a corresponding value exists or returns the result of given f after saving it
   *
   * @param key key
   * @param f function to get new value
   * @return cached value or the result of given f
   */
  final def caching(key: K)(f: => V): V = {
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
}

/** Cache implementation using Map
 *
 * Note: This holds all values in memory and is rot suitable for non-small values
 */
class MapCache[K, V] extends Cache[K, V] with Logging {
  import scala.collection.mutable.HashMap
  val map = HashMap[K, V]()

  def get(key: K): Option[V] = {
    map.get(key)
  }
  def put(key: K, value: V) {
    map += (key -> value)
  }

  override def toString(): String = "MapCache(%s)".format(map)
}

