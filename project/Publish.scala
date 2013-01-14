import sbt._
import sbt.Keys._
import sbt.Project.Initialize
import scala.xml.{ XML, NodeSeq }

/** Manage publishing to Sonatype repos */
object Publish {
  lazy val developers = SettingKey[Seq[Developer]]("developers", "Project's developers") 
  lazy val checkPom = TaskKey[Unit]("check-pom", "Checks if pom contains essential elemets for central")

  lazy private[this] val centralElements = Seq(
    Seq("modelVersion"), 
    Seq("groupId"),
    Seq("artifactId"),
    Seq("version"),
    Seq("packaging"),
    Seq("name"),
    Seq("description"),
    Seq("url"),
    Seq("licenses"),
    Seq("scm", "url"),
    Seq("scm", "connection"),
    Seq("developers"))

  def checkePomTask = (makePom, streams) map { (f, s) => 
    val pom = XML.loadFile(f)
    val pomNodes = centralElements map ( ns => ns.foldLeft[NodeSeq](pom)(_ \ _))
    val notSetNodes = centralElements.zip(pomNodes).filter(_._2.isEmpty)
    notSetNodes.map(p => s.log.warn(p._1.mkString("\\") + " not set in pom"))
    if (!notSetNodes.isEmpty) sys.error("pom doesn't satisfy central requirements")
  }

  def settings = Seq(
    checkPom <<= checkePomTask,
    publishMavenStyle := true,
    publishTo <<= sonatypeRepo,
    mappings in (Compile, packageBin) <++= mapTo("META-INF", "LICENSE", "NOTICE"),
    mappings in (Compile, packageSrc) <++= mapTo("META-INF", "LICENSE", "NOTICE"),
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    pomExtra <<= developers(makeDevelopersXml)) // if scmInfo is defined only developers are missing

  lazy val nexus = "https://oss.sonatype.org/"

  def sonatypeRepo: Initialize[Option[Resolver]] = isSnapshot { s =>
    if (s) Some("snapshots" at nexus + "content/repositories/snapshots")
    else Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  }

  def mapTo(to: String, filenames: String*) = 
    baseDirectory map { root =>
    val path = to + (if (to.isEmpty || to.endsWith("/")) "" else "/")
    filenames.map( filename => (root / filename) -> ( path + filename))
  }

  def makeDevelopersXml(devs: Seq[Developer]) =
    <developers>
      {devs.map { dev =>
        <developer>
          <id>{dev.id}</id>
          <name>{dev.name}</name>
          {dev.page match {
            case Some(p) => <url>{p}</url>
            case _ => scala.xml.NodeSeq.Empty
          }}
        </developer>
      }}
    </developers>
}

case class Developer(id: String, name: String, page: Option[String])
