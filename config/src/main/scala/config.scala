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
class Environment[A](appName: String, configs: Pair[String, A]*) {

  /** returns a config of current environment or throw exception if not found
   *
   * @throw IllegalStateException when no appropriate config found
   */
  lazy val current: A = {
    (configFromProp orElse configFromHostname orElse error).get
  }


  private val logger = Logger[this.type]
  private val configMap = configs.toMap
  private lazy val envKey = appName + ".env"

  private def configFromHostname: Option[A] = {
    configMap.get(InetAddress.getLocalHost.getHostName) 
  }

  private def configFromProp: Option[A] = {
    Properties.propOrNone(envKey).flatMap(configMap.get(_))
  }

  private def error: Option[A] = {
    val m = "cannot determin environment"
    logger.error(m)
    logger.info("""
      |*********************************************************
      |Please check you set sytem property "%s"
      |ex) java -D%s=prod Class...
      |
      |available values: %s
      |*********************************************************
      """.stripMargin.format(envKey, envKey, configs.map(_._1).mkString(", ")))
    throw new IllegalStateException(m)
  }

}

/** Factory of Environment */
object Environment {
  def apply[A](appName: String, configs: Pair[String, A]*): Environment[A] = new Environment(appName, configs: _*)
}

