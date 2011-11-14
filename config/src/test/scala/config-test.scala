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

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter
import scala.util.Properties

class ConfigSuite extends FunSuite with BeforeAndAfter {

  before { Properties.clearProp("whitepaper.env") } 
  after { Properties.clearProp("whitepaper.env") } 

  test("should throw exception when no env property") {
    val env = Environment("whitepaper",
      "dev" -> "1",
      "prod" -> "2"
    )

    intercept[IllegalStateException] {
      env.current
    }
  }

  test("should return appropriate config") {
    trait Config { val name: String }

    val env = Environment("whitepaper",
      "dev" -> new Config { val name = "dev" },
      "prod" -> new Config { val name = "prod" }
    )

    Properties.setProp("whitepaper.env", "dev")
    val config = env.current
    assert(config.name === "dev")
  }

}

