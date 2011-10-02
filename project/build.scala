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

  override val settings = super.settings :+ 
    (shellPrompt := { s => Project.extract(s).currentProject.id + "> " })

  lazy val cache = Project(id("cache"), file("cache"),
    settings = buildSettings ++ Seq(
      libraryDependencies <++= scalaVersion(_ => Seq(
        "org.scalatest" %% "scalatest" % "1.6.1",
         "net.sf.ehcache" % "ehcache" % "1.5.0" withSources(),
        // Logging
        "ch.qos.logback" % "logback-classic" % "0.9.25" withSources(),
        "org.codehaus.groovy" % "groovy" % "1.8.0" withSources(),
        "org.slf4j" % "slf4j-api" % "1.6.2" withSources(),
        "org.clapper" %% "grizzled-slf4j" % "0.6.6"
      ))
    )
  )

}

