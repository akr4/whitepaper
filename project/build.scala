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
    organization := "net.physalis",
    name := "whitepaper",
    version := "0.1-SNAPSHOT",
    crossScalaVersions := Seq("2.9.0", "2.9.0-1", "2.9.1"),
    scalaVersion := "2.9.1",
    scalacOptions ++= Seq("-Xcheckinit", "-encoding", "utf8")
  )

  val localResolver = "Local Maven Repository" at "file:///" + System.getProperty("user.home") + "/.m2/repository/"

  val loggingDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % "0.9.25" withSources(),
    "org.codehaus.groovy" % "groovy" % "1.8.0" withSources(),
    "org.slf4j" % "slf4j-api" % "1.6.2" withSources(),
    "org.clapper" %% "grizzled-slf4j" % "0.6.6"
  )

  val testDependencies = Seq(
    "org.scalatest" %% "scalatest" % "1.6.1" % "test"
  )


  override val settings = super.settings :+ 
    (shellPrompt := { s => Project.extract(s).currentProject.id + "> " })

  lazy val cache = Project(id("cache"), file("cache"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
         "net.sf.ehcache" % "ehcache" % "1.5.0" withSources()
      ) ++ loggingDependencies ++ testDependencies)
    )
  )

  lazy val sql = Project(id("sql"), file("sql"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
        "org.scala-tools.time" %% "time" % "0.5",
        "commons-dbcp" %% "commons-dbcp" % "1.4"
      ) ++ loggingDependencies ++ testDependencies)
    ) ++ Seq(resolvers += localResolver)
  )

}

