import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.SbtSite.SiteKeys.siteMappings
import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.site.PamfletSupport.Pamflet
import com.typesafe.sbt.SbtGit.GitKeys.{ gitRemoteRepo, gitRunner }
import scala.xml.{ XML, NodeSeq }
import GitHub._

/** Creates a site with Pamflet via SbtSite plugin and publish it to GitHub Pages. */
object Site {
  def settings = site.settings ++ site.pamfletSupport() ++ ghpages.settings ++ Seq(
    homepage <<= GitHub.ghPage(Some(_)),
    gitRemoteRepo <<= ghSsh,
    synchLocal <<= newSynchLocal,
    siteMappings <++= scalaDocSiteMappings)

  // override the synchLocal task to avoid removing the existing files (from specs2)
  def newSynchLocal = 
    (privateMappings, updatedRepository, gitRunner, version, crossScalaVersions) map { (mappings, repo, git, v, cv) =>
      val betterMappings = mappings map { case (file, target) => (file, repo / target) }
      IO.copy(betterMappings)
      updateSiteVersion(repo / "releases.xml", v, cv.map(CrossVersion.binaryScalaVersion))
      repo
    }

  // depending on the version, copy the api files to a different directory
  def scalaDocSiteMappings = 
    (mappings in packageDoc in Compile, version, isSnapshot) map { (m, v, s) =>
      val apiRoot = if (s) "api/latest" else "api/" + v
      for((f, d) <- m) yield (f, apiRoot + "/" + d)
    }

  def updateSiteVersion(f: File, version: String, scala: Seq[String]) {
    val xml = XML.loadFile(f)
    if (!containsVersion(xml, version)) {
      val a = removeSnapshots(xml)
      val b = addVersion(a, version, scala)
      IO.write(f, b.toString)
    }
  }

  private[this] def containsVersion(releases: NodeSeq, version: String) = 
    (releases \ "release" \ "version").exists(_.text == version)

  private[this] def addVersion(releases: NodeSeq, version: String, scala: Seq[String]) = 
    <releases>
      { releaseElem(version, scala) :+ (releases \ "release") }
    </releases>

  private[this] def removeVersion(releases: NodeSeq, version: String) = 
   removeByVersionPredicate(releases, _ != version)

  private[this] def removeSnapshots(releases: NodeSeq) = 
    removeByVersionPredicate(releases, ! _.endsWith("-SNAPSHOT"))

  private[this] def removeByVersionPredicate(releases: NodeSeq, predicate: String => Boolean) = 
     <releases>
      { (releases \ "release").filter(r => (r \ "version").forall(n => predicate(n.text))) }
    </releases>

  private[this] def releaseElem(version: String, scala: Seq[String]) =
    <release>
      <version>{version}</version>
      <scala-versions>
        {scala.map(s => <value>{s}</value>)}
      </scala-versions>
    </release>
}
