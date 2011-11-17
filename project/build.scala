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
import sbt._
import sbt.Keys._

object Whitepaper extends Build {

  def id(name: String) = "whitepaper-%s" format name

  val buildSettings = Defaults.defaultSettings ++ Seq(
    version := "0.3",
    organization := "net.physalis",
    crossScalaVersions := Seq("2.9.0", "2.9.0-1", "2.9.1"),
    scalaVersion := "2.9.1",
    scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8"),
    publishTo <<= (version) { version: String =>
      val local = Path.userHome / "projects" / "akr4.github.com" / "mvn-repo"
      val path = local / (if (version.trim.endsWith("SNAPSHOT")) "snapshots" else "releases")
      Some(Resolver.file("Github Pages", path)(Patterns(true, Resolver.mavenStyleBasePattern)))
    },
    publishMavenStyle := true
  )

  val localResolver = "Local Maven Repository" at "file:///" + System.getProperty("user.home") + "/.m2/repository/"

  val loggingDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.25" withSources,
    "org.codehaus.groovy" % "groovy" % "1.8.0" withSources,
    "org.slf4j" % "slf4j-api" % "1.6.2" withSources,
    "org.clapper" %% "grizzled-slf4j" % "0.6.6"
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "1.6.1" % "test",
    "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration" % "test"
  )


  override val settings = super.settings :+ 
    (shellPrompt := { s => Project.extract(s).currentProject.id + "> " })

  lazy val whitepaper = Project("whitepaper", file("."),
    settings = buildSettings
  ) aggregate(cache, cacheEhcache, sql, sqlPostgresql, config)

  lazy val cache = Project(id("cache"), file("cache"),
    settings = buildSettings ++ Seq(
      libraryDependencies := loggingDependencies ++ testDependencies
    )
  )

  lazy val cacheEhcache = Project(id("cache-ehcache"), file("cache-ehcache"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
         "net.sf.ehcache" % "ehcache" % "1.5.0" withSources
      ) ++ loggingDependencies ++ testDependencies)
    )
  ) dependsOn(cache)

  lazy val sql = Project(id("sql"), file("sql"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
        "org.scala-tools.time" %% "time" % "0.5",
        "commons-dbcp" % "commons-dbcp" % "1.4",
        "org.hsqldb" % "hsqldb" % "[2,)" % "test"
      ) ++ loggingDependencies ++ testDependencies)
    ) ++ Seq(resolvers += localResolver)
  )

  lazy val sqlJta = Project(id("sql-jta"), file("sql-jta"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
        "javax.transaction" % "jta" % "1.1",
        "org.hsqldb" % "hsqldb" % "[2,)" % "test"
      ) ++ loggingDependencies ++ testDependencies)
    )
  ) dependsOn(sql)

  lazy val sqlPostgresql = Project(id("sql-postgresql"), file("sql-postgresql"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
        "postgresql" % "postgresql" % "8.4-701.jdbc4"
      ) ++ loggingDependencies ++ testDependencies)
    )
  ) dependsOn(sql)

  lazy val config = Project(id("config"), file("config"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
      ) ++ loggingDependencies ++ testDependencies)
    )
  )

}

