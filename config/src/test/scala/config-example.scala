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

trait Config {
  val dbUser: String
  val dbPassword: String
}

object Env {
  private val env = Environment("whitepaper",
    "dev" -> new Config {
      val dbUser = "dbUser"
      val dbPassword = "dbPassword"
    },
    "prod" -> new Config {
      val dbUser = "dbUser"
      val dbPassword = "dbPassword"
    }
  )

  lazy val get: Config = env.current
}

class A {
  def a {
    println(Env.get.dbUser)
  }
}

class B {
  def b {
    println(Env.get.dbPassword)
  }
}


