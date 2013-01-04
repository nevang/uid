import sbt._
import sbt.Keys._

object Settings {
  val buildOrganization = "gr.jkl"
  val buildVersion      = "1.0-SNAPSHOT"
  val buildScalaVersion =  Version.scala

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    crossVersion := CrossVersion.binary)

  val defaultSettings = buildSettings ++ Seq(
    resolvers ++= DefaultOptions.resolvers(true),
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.6", "-deprecation", "-feature", "-unchecked"),
    shellPrompt := DefaultOptions.shellPrompt(buildVersion))
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

object Publish {
  val nexus = "https://oss.sonatype.org/"

  val publishSettings = Seq(
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    publishArtifact in Test := false,
    licenses := Seq("Simplified BSD License" -> url("http://opensource.org/licenses/BSD-2-Clause")),
    homepage := Some(url("https://github.com/nevang/uid")),
    pomIncludeRepository := { _ => false },
    pomExtra := (
      <scm>
        <connection>scm:git:git@github.com:nevang/uid.git</connection>
        <developerConnection>scm:git:git@github.com:nevang/uid.git</developerConnection>
        <url>git@github.com:nevang/uid.git</url>
      </scm>
      <developers>
        <developer>
          <id>nevang</id>
          <name>Nikolas Evangelopoulos</name>
          <url>http://github.com/nevang</url>
        </developer>
      </developers>
    )
  )
}

object UIDBuild extends Build {
  import Settings._
  import Publish._

  lazy val uid = Project(
    id = "uid",
    base = file("."),
    settings = defaultSettings ++ publishSettings ++ Seq(
      libraryDependencies ++= Dependencies.core,
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("-oDF"),
      scalacOptions in (Compile, doc) ++= DefaultOptions.scaladoc("UID", buildVersion) ++ Seq("-groups")))

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
