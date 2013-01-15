import sbt._
import sbt.Keys._

/** Release processes */
object Release {
  lazy val snapshotCheck = TaskKey[Unit]("snapshot-check", "Checks if version is snapshot")
  lazy val stableCheck = TaskKey[Unit]("stable-check", "Checks if version is stable")

  def settings = Seq(
    snapshotCheck <<= snapshotCheckTask,
    stableCheck <<= stableCheckTask) ++
    addCommandAlias("release-snapshot", ";snapshot-check ;clean ;check-pom ;+test ;+publish ;ghpages-push-site") ++ 
    addCommandAlias("release-stable", ";stable-check ;clean ;check-pom ;+test ;+publish ;ghpages-push-site ;ls-write-version ;lsync")

  def snapshotCheckTask = (isSnapshot) map { snap =>
    if (!snap) sys.error("Not a snapshot version")
  }

  def stableCheckTask = (isSnapshot) map { snap =>
    if (snap) sys.error("Not a stable version")
  }
}
