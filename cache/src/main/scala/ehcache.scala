package whitepaper.cache

import net.sf.ehcache.{ CacheManager, Cache => ECache, Element }
import grizzled.slf4j.Logging

/** Cache implementation using Ehcache */
private class EhcacheCache[K, V](
  underlying: ECache, val cacheKeyGenerator: CacheKeyGenerator[_]
) extends Cache[K, V] with Logging {
  def _get(key: Any): Option[V] = {
    val e = underlying.get(key)
    if (e != null && e.getValue() != null) Some(e.getValue().asInstanceOf[V])
    else None
  }

  def _put(key: Any, value: V) {
    underlying.put(new Element(key, value))
  }
}

/** Cache factory using Ehcache */
object Ehcache {
  private val manager = CacheManager.getInstance()

  /** Returns Cache instance
   * 
   * @tparam K type of key
   * @tparam V type of value
   * @param name cache name
   */
  def apply[K, V](name: String)(implicit cacheKeyGenerator: CacheKeyGenerator[Serializable]): Cache[K, V] = {
    val c = manager.getCache(name)
    if (c == null) throw new IllegalArgumentException("no cache %s found".format(name))
    else new EhcacheCache(c, cacheKeyGenerator)
  }
}

