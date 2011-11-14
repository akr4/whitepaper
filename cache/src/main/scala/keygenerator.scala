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
package whitepaper.cache

/**
 * @tparam A type of cache key
 */
trait CacheKeyGenerator[A] {
  def generate(data: Any*): A
}

/** Generator which returns input value as is */
object NoOpCacheKeyGenerator extends CacheKeyGenerator[Any] {
  def generate(data: Any*): Any = data
}

/** Generator which concatenate each parameter */
object ToStringCacheKeyGenerator extends CacheKeyGenerator[String] {
  def generate(data: Any*): String = data.toArray.deep.mkString(",")
}

/** Generator which uses hashCode */
object HashCodeCacheKeyGenerator extends CacheKeyGenerator[Int] {
  def generate(data: Any*): Int = data.toArray.deep.hashCode
}

