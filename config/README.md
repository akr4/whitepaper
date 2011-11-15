Example
--------------

```scala

import whitepaper.config.Environments

trait Config {
  val dbUser: String
  val dbPassword: String
}

object Env {
  private val env = Environments("whitepaper",
    "dev" -> new Config {
      val dbUser = "dbUser_dev"
      val dbPassword = "dbPassword_dev"
    },
    "prod" -> new Config {
      val dbUser = "dbUser_prod"
      val dbPassword = "dbPassword_prod"
    }
  )

  lazy val get: Config = env.current
}

class A {
  def a {
    // if JVM started with -Dwhitepaper.env=dev or the hostname is "dev", this will be "dbUser_dev"
    println(Env.get.dbUser)
  }
}

class B {
  def b {
    println(Env.get.dbPassword)
  }
}
```
