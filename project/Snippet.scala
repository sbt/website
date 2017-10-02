import sbt._
import Keys._
import Path.rebase

object Snippet {

  /**
   * Processes all files under base directory, and outputs to newBase.
   * Markdown files are snippet-processed, and the rest are just copied.
   */
  def processDirectory(base: File, newBase: File): File = {
    IO.createDirectory(newBase)
    val files: Seq[File] = (base ** (-DirectoryFilter)).get
    val isMarkdown = Set("markdown", "md")
    (files pair rebase(base :: Nil, newBase)) foreach {
      case (x, newFile) if isMarkdown(x.ext) => processFile(x, newFile)
      case (x, newFile)                      => IO.copyFile(x, newFile)
    }
    newBase
  }

  def processFile(baseFile: File, newFile: File): File = {
    val xs0 = IO.readLines(baseFile)
    val xs: List[String] = xs0 flatMap {
      case x if x.trim.startsWith("@@snip") => snippet(x, baseFile)
      case x                                => List(x)
    }
    IO.writeLines(newFile, xs)
    newFile
  }

  /**
   * Use the Lightbend Paradox syntax for snippet inclusion.
   * `@@snip [example.log](example.log) { #example-log type=text }`
   */
  lazy val Snippet = ("""\@\@snip\s*\[[^\]]+\]\(([^)]*)\)\s*"""
    + """\{\s*(\#[\w-]+)?(\s+type\=[\w-]+)?.*\}\s*""").r
  def snippet(line: String, baseFile: File): List[String] = {
    def readFromRef(ref: File, tagOpt: Option[String], ty: String): List[String] = {
      tagOpt match {
        case Some(tag) =>
          val xs0 = IO.readLines(ref)
          val xs = xs0
            .dropWhile({ x =>
              !x.contains(tag)
            })
            .drop(1)
            .takeWhile({ x =>
              !x.contains(tag)
            })
          if (xs.isEmpty) {
            sys.error(s"@@snip was detected for $ref with tag $tag, but the code was empty!")
          }
          List(s"```$ty") ::: xs ::: List("```")
        case None => List(s"```$ty") ::: IO.readLines(ref) ::: List("```")
      }
    }
    line match {
      case Snippet(path0, tag0, ty0) =>
        val tag = Option(tag0).map(_.trim)
        val ty = Option(ty0).map(_.trim).getOrElse("scala")
        val ref = resolvePath(path0, baseFile)
        if (!ref.exists) {
          sys.error(s"@@snip was detected, but $ref was not found!")
        }
        readFromRef(ref, tag, ty)
      case _ => sys.error(s"Invalid snippet notation: $line")
    }
  }

  /**
   * If the path starts from `$root$/` then use the path as is from root.
   * Otherwise, treat it as a relative path from the markdown file.
   */
  def resolvePath(p: String, baseFile: File): File = {
    if (p.startsWith("$root$/")) file(p.drop(7))
    else new File(uri(baseFile.getParentFile.toURI.toString + p))
  }
}
