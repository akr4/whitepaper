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
package whitepaper.config

import scala.util.Properties
import java.net.InetAddress
import grizzled.slf4j.Logger

/** provides environment specified configuration
 *
 * @tparam A type of configuration
 * @param appName application name which system property key is made from
 * @param configs config definitions. env name -> config instance pair
 */
class Environments[A](appName: String, configs: Pair[String, A]*) {

  /** returns a config of current environment or throw exception if not found
   *
   * @throw IllegalStateException when no appropriate config found
   */
  lazy val current: A = {
    val e = (fromSystemProperty orElse fromHostname orElse error).get
    logger.info("current Envoronment: %s".format(e.name))
    e.config
  }

  private val logger = Logger[this.type]
  private val configMap = configs.toMap
  private val envPropKey = appName + ".env"
  private lazy val envPropName = Properties.propOrNone(envPropKey)

  private def fromHostname: Option[Environment[A]] = {
    val h = InetAddress.getLocalHost.getHostName
    configMap.get(h).collect { case c => new Environment(h, c) }
  }

  private def fromSystemProperty: Option[Environment[A]] = envPropName match {
    case Some(n) => configMap.get(n).collect { case c => new Environment(n, c) }
    case None => None
  }

  private def error: Option[Environment[A]] = {
    val m = "cannot determin environment"
    logger.error(m)
    logger.info("""
      |*********************************************************
      |Please check you set sytem property "%s"
      |ex) java -D%s=prod Class...
      |
      |available values: %s
      |*********************************************************
      """.stripMargin.format(envPropKey, envPropKey, configs.map(_._1).mkString(", ")))
    throw new IllegalStateException(m)
  }

}

/** Factory of Environment */
object Environments {
  def apply[A](appName: String, configs: Pair[String, A]*): Environments[A] = new Environments(appName, configs: _*)
}

private class Environment[A](val name: String, val config: A)
