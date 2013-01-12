import sbt._
import sbt.Keys._
import com.typesafe.sbt.SbtSite.SiteKeys.makeSite
import com.typesafe.sbt.SbtGhPages.GhPagesKeys.pushSite
import Site._
import Publish._

/** Release processes
  * @todo Add process for stable versions with implicit.ly update
  */
object Release {
  lazy val stage = TaskKey[Unit]("stage", "Produces the files for the snapshot and prints the pom")
  lazy val snapshotCheck = TaskKey[Unit]("snapshot-check", "Checks if a snapshot is ready for release")
  lazy val snapshotRelease = TaskKey[Unit]("snapshot-release", "Release a snapshot version")

  def settings = Seq(
    snapshotCheck <<= snapshotCheckTask,
    stage <<= stageTask,
    snapshotRelease <<= snapshotReleaseTask)

  def stageTask = (makeSite, makePom, streams, packagedArtifacts) map { (_, p, s, _) =>
    IO.readLines(p).foreach(s.log.info(_))
  }

  def snapshotCheckTask = (isSnapshot, version, compareVersions, checkPom, (test in Test).task) flatMap { (snap, v, cv, p, t) =>
    if (!snap) sys.error("Version " + v + " is not a snapshot")
    if (!cv) sys.error("Different versions in build and in template properties. Run site-update-version")
    if (!p) sys.error("Pom doesn't conform central requirements")
    t // throws error on failure
  }

  def snapshotReleaseTask = (snapshotCheck, version, stage.task, publish.task, streams) flatMap { (c, v, st, p, s) =>
    st flatMap { _ =>
      SimpleReader.readLine("Release " + v + " (y, n)? [n]") match {
        case Some("y") => p
        case _ => task(s.log.info("Release aborted"))
      }
    }   
  }
}
