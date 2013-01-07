import sbt._
import sbt.Keys._

object Settings {
  val buildOrganization = "gr.jkl"
  val buildVersion      = "1.0-SNAPSHOT"
  val buildScalaVersion = Version.scala

  val buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    crossVersion := CrossVersion.binary,
    crossScalaVersions := Seq("2.10.0", "2.9.3-RC1", "2.9.2", "2.9.1-1", "2.9.1", "2.9.0-1", "2.9.0", "2.8.2", "2.8.1"))

  val defaultSettings = buildSettings ++ Seq(
    resolvers ++= DefaultOptions.resolvers(true),
    scalacOptions in (Compile, compile) ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked"),
    shellPrompt := DefaultOptions.shellPrompt(buildVersion))
}

object Version {
  def scalaTest(scalaVersion: String) = scalaVersion match {
    case "2.10.0" => "1.9.1"
    case _        => "1.8"
  } 

  val scala   = "2.9.2"
  val caliper = "0.5-rc1"
  val eaio    = "3.2"
}

object Dependency {
  def scalaTest(scalaVersion: String) = 
    "org.scalatest" %% "scalatest" % Version.scalaTest(scalaVersion) % "test"

  val caliper = "com.google.caliper" %  "caliper" % Version.caliper
  val eaio    = "com.eaio.uuid"      %  "uuid"    % Version.eaio

}

object Dependencies {
  import Dependency._

  def core(scalaVersion: String) = Seq(scalaTest(scalaVersion))

  val benchmark = Seq(caliper, eaio)
}

object Publish {
  val nexus = "https://oss.sonatype.org/"

  val root = file(".")

  def mapToBase(base: String, filenames: String*): Seq[(File,String)] = {
    val path = base + (if (base.isEmpty || base.endsWith("/")) "" else "/")
    filenames.map( filename => (root / filename) -> ( path + filename))
  }

  val publishSettings = Seq(
    startYear := Some(2012),
    organizationName := "jkl",
    organizationHomepage := None,
    homepage := Some(url("https://github.com/nevang/uid")),
    licenses := Seq("Simplified BSD License" -> url("http://opensource.org/licenses/BSD-2-Clause")),
    publishMavenStyle := true,
    publishTo <<= version { (v: String) =>
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    },
    mappings in (Compile, packageBin) ++= mapToBase("META-INF", "LICENSE", "NOTICE"),
    mappings in (Compile, packageSrc) ++= mapToBase("META-INF", "LICENSE", "NOTICE"),
    publishArtifact in Test := false,
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
      description := "64-bit Ids for Scala",
      libraryDependencies <++= scalaVersion(Dependencies.core(_)),
      parallelExecution in Test := false,
      testOptions in Test += Tests.Argument("-oDF"),
      scalacOptions in (Compile, doc) ++= DefaultOptions.scaladoc("UID", buildVersion)))

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
