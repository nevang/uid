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
    resolvers ++= DefaultOptions.resolvers(true),
    scalacOptions in compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.6", "-deprecation", "-feature", "-unchecked"))
}

object Version {
  val scala     = "2.10.0"
  val scalaTest = "2.0.M5b"
  val caliper   = "0.5-rc1"
  val eaio      = "3.2"
}

object Dependency {
  val scalaTest = "org.scalatest"      %% "scalatest" % Version.scalaTest % "test"
  val caliper   = "com.google.caliper" %  "caliper"   % Version.caliper
  val eaio      = "com.eaio.uuid"      %  "uuid"      % Version.eaio
}

object Dependencies {
  import Dependency._

  val core = Seq(scalaTest)

  val benchmark = Seq(caliper, eaio)
}

object UIDBuild extends Build {
  import Settings._

  lazy val uid = Project(
    id = "uid",
    base = file("."),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.core,
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("-oDF")))

  lazy val benchmark = Project(
    id = "benchmark",
    base = file("benchmark"),
    settings = defaultSettings ++ Seq(
      libraryDependencies ++= Dependencies.benchmark,
      fork in run := true,
      javaOptions in run <++= (fullClasspath in Runtime) map { cp => Seq(
        "-cp", sbt.Build.data(cp).mkString(":"), "-server", "-Xms128m", "-Xmx128m") })
    ).dependsOn(uid)
}
