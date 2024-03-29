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
import grizzled.slf4j.Logging

/** Cache implementation using Ehcache */
private class EhcacheCache[K, V](
  underlying: ECache, override val cacheKeyGenerator: CacheKeyGenerator[_]
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
  /** Returns Cache instance
   * 
   * @tparam K type of key
   * @tparam V type of value
   * @param name cache name
   */
  def apply[K, V](name: String)(
    implicit cacheManager: CacheManager,
    cacheKeyGenerator: CacheKeyGenerator[_] = NoOpCacheKeyGenerator
  ): Cache[K, V] = {
    val c = cacheManager.getCache(name)
    if (c == null) throw new IllegalArgumentException("no cache %s found".format(name))
    else new EhcacheCache(c, cacheKeyGenerator)
  }
}

