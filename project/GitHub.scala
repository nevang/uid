import sbt._
import sbt.Keys.scmInfo

/** Fills ssmInfo and generates some Setting Keys used around the build. */
object GitHub {
  lazy val user    = "nevang"
  lazy val project = "uid"

  lazy val ghUser = SettingKey[String]("gh-user", "GitHub user")
  lazy val ghProject = SettingKey[String]("gh-project", "GitHub project")
  lazy val ghSsh = SettingKey[String]("gh-ssh", "GitHub SSH connection")
  lazy val ghPage = SettingKey[URL]("gh-page", "GitHub project's page")
  
  def settings = Seq(
    ghUser := user,
    ghProject := project,
    ghSsh <<= (ghUser, ghProject) ((u, p) =>  "git@github.com:" + u + "/" + p + ".git"),
    ghPage <<= (ghUser, ghProject) ((u, p) =>  url("http://" + u + ".github.com/" + p + "/")),
    scmInfo <<= (ghUser, ghProject, ghSsh) { (u, p, s) => 
      val connection = "scm:git:" + s // scm:[provider]:[provider_specific]
      val address = url("https://github.com/" + u + "/" + p)
      Some(ScmInfo(address, connection, Some(connection)))
    })
}
