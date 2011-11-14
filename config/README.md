Example
--------------

```scala

import whitepaper.config.Environment

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
```
