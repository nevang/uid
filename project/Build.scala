import sbt._
import sbt.Keys._
import ls.Plugin.{ lsSettings, LsKeys }
import com.typesafe.sbt.SbtScalariform
import scalariform.formatter.preferences._

object Settings {
  lazy val buildOrganization = "gr.jkl"
  lazy val buildScalaVersion = Version.scala

  def buildSettings = Defaults.defaultSettings ++ Seq(
    organization := buildOrganization,
    scalaVersion := buildScalaVersion,
    crossVersion := CrossVersion.binary)

  def reformSettings = SbtScalariform.scalariformSettings ++ Seq(
    SbtScalariform.ScalariformKeys.preferences := FormattingPreferences.
      setPreference(DoubleIndentClassDeclaration, true).
      setPreference(MultilineScaladocCommentsStartOnFirstLine, true).
      setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true). 
      setPreference(AlignSingleLineCaseStatements, true))

  def defaultSettings = buildSettings ++ reformSettings ++ Seq(
    resolvers ++= DefaultOptions.resolvers(true),
    scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.6", "-deprecation", "-feature", "-unchecked"),
    parallelExecution in Test := false,
    testOptions in Test += Tests.Argument("-oDF"),
    scalacOptions in (Compile, doc) ++= Seq("-groups"),
    shellPrompt <<= version(v => DefaultOptions.shellPrompt(v)))
}

object Version {
  lazy val scala     = "2.10.0"
  lazy val scalaTest = "2.0.M5b"
  lazy val caliper   = "0.5-rc1"
  lazy val eaio      = "3.2"
}

object Dependency {
  lazy val scalaTest = "org.scalatest"      %% "scalatest" % Version.scalaTest % "test"
  lazy val caliper   = "com.google.caliper" %  "caliper"   % Version.caliper
  lazy val eaio      = "com.eaio.uuid"      %  "uuid"      % Version.eaio
}

object Dependencies {
  import Dependency._

  lazy val core = Seq(scalaTest)

  lazy val benchmark = Seq(caliper, eaio)
}

object UIDBuild extends Build {
  import Settings._

  lazy val developers = Seq(Developer("nevang", "Nikolas Evangelopoulos", Some("https://github.com/nevang")))

  def extraSettings = GitHub.settings ++ Site.settings ++ Publish.settings ++ Release.settings ++ lsSettings ++ Seq( 
    startYear := Some(2012),
    organizationName := "jkl",
    organizationHomepage := None,
    licenses <<= homepage ( _.map( h => "Simplified BSD License" -> url(h + "License.html")).toSeq),
    Publish.developers :=developers)

  lazy val sonatype = "https://oss.sonatype.org/content/repositories/"

  lazy val uid = Project(
    id = "uid",
    base = file("."),
    settings = defaultSettings ++ extraSettings ++ Seq(
      description := "Library for 64-bit unique Id generation and handling",
      LsKeys.tags in LsKeys.lsync := Seq("id", "uid", "64-bit"),
      LsKeys.docsUrl in LsKeys.lsync <<= homepage(_.map(h => url(h + "Documentation.html"))),
      externalResolvers in LsKeys.lsync <<= isSnapshot map { s =>
        if (s) Seq("sonatype-snapshots" at sonatype + "snapshots")
        else Seq("sonatype-releases"  at sonatype + "releases")
      },
      libraryDependencies ++= Dependencies.core,
      scalacOptions in (Compile, doc) <++= version map (v => DefaultOptions.scaladoc("UID", v))))

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
