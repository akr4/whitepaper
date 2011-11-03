package whitepaper.cache

import net.sf.ehcache.{ CacheManager, Cache => ECache, Element }
import grizzled.slf4j.Logging

/** Cache implementation using Ehcache */
private class EhcacheCache[K, V, CK](
  underlying: ECache, val cacheKeyGenerator: CacheKeyGenerator[CK]
) extends Cache[K, V, CK] with Logging {
  def get(key: CK): Option[V] = {
    val e = underlying.get(key)
    if (e != null && e.getValue() != null) Some(e.getValue().asInstanceOf[V])
    else None
  }

  def put(key: CK, value: V) {
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
  def apply[K, V, CK <: Serializable]
    (name: String)(implicit cacheKeyGenerator: CacheKeyGenerator[CK]): Cache[K, V, CK] = {
    val c = manager.getCache(name)
    if (c == null) throw new IllegalArgumentException("no cache %s found".format(name))
    else new EhcacheCache(c, cacheKeyGenerator)
  }
}

