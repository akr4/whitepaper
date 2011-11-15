Example
--------------

```scala

import whitepaper.config.Environments

trait Config {
  val dbUser: String
  val dbPassword: String
}

object Environment {
  private val env = Environments("whitepaper",
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
    println(Environment.get.dbUser)
  }
}

class B {
  def b {
    println(Env.get.dbPassword)
  }
}
```
