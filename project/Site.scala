import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtSite.site
import com.typesafe.sbt.SbtSite.SiteKeys.siteMappings
import com.typesafe.sbt.SbtGhPages.ghpages
import com.typesafe.sbt.SbtGhPages.GhPagesKeys._
import com.typesafe.sbt.site.PamfletSupport.Pamflet
import com.typesafe.sbt.SbtGit.GitKeys.{ gitRemoteRepo, gitRunner }
import GitHub._

/** Creates a site with Pamflet via SbtSite plugin and publish it to GitHub Pages.
  * @todo Manage release notes and manipulate auto and setup pages.
  */
object Site {
  lazy val templateProperties = SettingKey[File]("site-properties", "File for pamflet template properties")
  lazy val updateVersion = TaskKey[File]("site-update-version", "Writes the build version in pamflet template properties")
  lazy val siteVersion = TaskKey[String]("site-version", "Reads the version in site's template properties")
  lazy val compareVersions = TaskKey[Boolean]("compare-versions", "Compares the build and the template properties versions")

  def settings = site.settings ++ site.pamfletSupport() ++ ghpages.settings ++ Seq(
    homepage <<= GitHub.ghPage(Some(_)),
    gitRemoteRepo <<= ghSsh,
    synchLocal <<= newSynchLocal,
    siteMappings <++= scalaDocSiteMappings,
    templateProperties <<= (sourceDirectory in Pamflet) (s => file(s + "/template.properties")),
    updateVersion <<= updateVersionTask,
    siteVersion <<= siteVersionTask,
    compareVersions <<= compareVersionsTask)

  // override the synchLocal task to avoid removing the existing files (from specs2)
  def newSynchLocal = 
    (privateMappings, updatedRepository, gitRunner, streams) map { (mappings, repo, git, s) =>
      val betterMappings = mappings map { case (file, target) => (file, repo / target) }
      IO.copy(betterMappings)
      repo
    }

  // depending on the version, copy the api files to a different directory
  def scalaDocSiteMappings = 
    (mappings in packageDoc in Compile, version, isSnapshot) map { (m, v, s) =>
      val apiRoot = if (s) "api/latest" else "api/" + v
      for((f, d) <- m) yield (f, apiRoot + "/" + d)
    }

  private[this] lazy val vPrfx = "version="

  def updateVersionTask = 
    (version, compareVersions, templateProperties, streams) map { (v, cv, f, s) =>
      if (cv) {
        s.log.info("Template properties up to date")
      } else {
        val newLines = vPrfx + v.trim :: IO.readLines(f).map(_.trim).
          filterNot(_.startsWith(vPrfx))
        IO.writeLines(f, newLines)
        s.log.info("Version in template properties updated to build version")
      }
      f
    }

  def siteVersionTask = (templateProperties) map { f =>
    IO.readLines(f).map(_.trim).filter(_.startsWith(vPrfx)) match {
      case v :: Nil => v.diff(vPrfx)
      case _ => sys.error("Can't find version in template properties")
    }
  }

  def compareVersionsTask = (version, siteVersion) map ( _.trim == _ )
}
