import com.typesafe.sbt.site.PamfletSupport
import com.typesafe.sbt.git.GitRunner

lazy val Tutorial = config("tutorial")
lazy val Ref = config("reference")
lazy val targetSbtBinaryVersion = "0.13"

site.settings

site.nanocSupport()

PamfletSupport.settings(Tutorial)

site.addMappingsToSiteDir(mappings in Tutorial, s"""$targetSbtBinaryVersion/tutorial""")

PamfletSupport.settings(Ref)

site.addMappingsToSiteDir(mappings in Ref, s"""$targetSbtBinaryVersion/docs""")


ghpages.settings

git.remoteRepo := "git@github.com:sbt/website.git"

def gitRemoveFiles(dir: File, files: List[File], git: GitRunner, s: TaskStreams): Unit = {
  if(!files.isEmpty)
    git(("rm" :: "-r" :: "-f" :: "--ignore-unmatch" :: files.map(_.getAbsolutePath)) :_*)(dir, s.log)
  ()
}

def siteInclude(f: File) = true

GhPagesKeys.synchLocal := {
  val repo = GhPagesKeys.updatedRepository.value
  val versioned = repo / targetSbtBinaryVersion
  val git = GitKeys.gitRunner.value
  val s = streams.value
  gitRemoveFiles(repo, IO.listFiles(versioned).toList, git, s)
  gitRemoveFiles(repo, (repo * "*.html").get.toList, git, s)
  val mappings =  for {
    (file, target) <- SiteKeys.siteMappings.value if siteInclude(file)
  } yield (file, repo / target)
  IO.copy(mappings)
  repo
}
