import sbt._
import sbt.Keys._

object Settings {
  val buildOrganization = "gr.jkl"
  val buildVersion      = "1.0-SNAPSHOT"
  val buildScalaVersion =  Version.scala

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion)

  val defaultSettings = buildSettings ++ Seq(
    resolvers ++= DefaultOptions.resolvers(true))
}

object Version {
  val scala     = "2.10.0"
  val scalaStm  = "0.7"
  val scalaTest = "2.0.M5b"
}

object Dependency {
  val scalaStm  = "org.scala-stm" %% "scala-stm" % Version.scalaStm
  val scalaTest = "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
}

object Dependencies {
  import Dependency._

  val core = Seq(scalaStm, scalaTest)
}

object UIDBuild extends Build {
  import Settings._

  lazy val uid = Project(
    id = "uid",
    base = file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.core))
}