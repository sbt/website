import sbt._
import Keys._
import scala.sys.process.Process

object Pdf {
  lazy val pandocLatexEngine = settingKey[String]("Engine to use when generating PDFs")
  lazy val pandocCommand = settingKey[String]("Location of pandoc for running.")
  lazy val pandoc = settingKey[Pandoc]("A runner to execute Pandoc")
  lazy val generatePdf = taskKey[Seq[File]]("Create the tutorial pdf file")
  lazy val detectPdf = taskKey[Seq[File]]("Detect the tutorial pdf file")

  def failOnPdfError: Boolean = sys.props("ignore.pdf") != "true"

  def settings: Seq[Setting[_]] =
    Seq(
      Global / pandocLatexEngine := "xelatex",
      Global / pandocCommand := "pandoc",
      Global / pandoc := new Pandoc((Global / pandocCommand).value)
    )

  def settingsFor(config: Configuration, pdfName: String): Seq[Setting[_]] =
    inConfig(config)(
      Seq(
        generatePdf := Pdf.makeCombinedPdf(config, pdfName).value,
        generatePdf / mappings := {
          generatePdf.value pair Path.relativeTo(target.value)
        }
      )
    )

  def cleanupCombinedPages(xs: Seq[(File, String)]): Seq[(File, String)] =
    xs collect {
      case (file, fname) if (fname contains "Combined+Pages.md") && !(fname contains "offline/") =>
        cleanupHeader(file) -> fname.replaceAllLiterally(
          "Combined+Pages.md",
          "Combined+Pages+Pdf.md"
        )
    }

  def makeCombinedPdf(config: Configuration, name: String): Def.Initialize[Task[Seq[File]]] =
    Def.task {
      val log = streams.value.log
      (config / mappings).value collect {
        case (file, fname)
            if (fname contains "Combined+Pages.md") && !(fname contains "offline/") =>
          val t = (config / target).value
          val language = getPdfLanguage(file, t)
          // TODO - Create front matter for the file.
          log.info(s"Generating pdf $language/$name...")
          val p = pandoc.value
          val cleanedUp = cleanupHeader(file)
          val cwd = cleanedUp.getParentFile
          val args = latexArgs(language) ++ Seq("--toc")
          val pdf = cwd / s"${name}.pdf"
          p(cwd)(cleanedUp, pdf, log, args: _*) match {
            case 0 => ()
            case n =>
              if (failOnPdfError)
                sys.error(
                  s"Failed to run pandoc($cwd)($cleanedUp, $pdf, ${args mkString ", "}) - Exit code $n"
                )
          }
          pdf
      }
    }

  private def getFirstTitle(lines: List[String]): (Option[String], List[String]) = {
    def findTitle(
        title: Option[String],
        output: List[String],
        remaining: List[String]
    ): (Option[String], List[String]) =
      remaining match {
        case Nil => (title, output.reverse)
        case t :: line :: rest if title.isEmpty && line.matches("[\\=]+") =>
          findTitle(Some(t), output, rest)
        case line :: rest => findTitle(title, line :: output, rest)
      }
    findTitle(None, Nil, lines)
  }

  def cleanupHeader(file: File): File = {
    val cleanedUp = file.getAbsoluteFile.getParentFile / "Combined+Pages+Pdf.md"
    val origLines = IO readLines file
    getFirstTitle(IO readLines file) match {
      case (Some(title), lines) =>
        IO.write(
          cleanedUp,
          s"""---
  title: ${title}
  ntags: [scala, sbt]
---

Preface
-------
${lines mkString "\n"}""".stripMargin
        )
      case _ => sys.error(s"Could not find title in document ${file}")
    }
    cleanedUp
  }

  def getPdfLanguage(file: File, target: File): String =
    IO.relativize(target, file.getParentFile) match {
      case Some(language) => language
      case None           => "en"
    }

  def latexArgs(language: String): Seq[String] =
    language match {
      case "ja" => Seq("-V", "documentclass=ltjarticle", "--pdf-engine=lualatex")
      case _    => Seq("--pdf-engine=xelatex")
    }

}

/** Helper to run pandoc. */
class Pandoc(cmd: String) {

  /** Runs pandoc and returns the error code. */
  def apply(cwd: File)(input: File, output: File, log: Logger, extraArgs: String*): Int = {
    val cmdSeq: Seq[String] = (
      Seq(cmd) ++
        extraArgs ++
        Seq(input.getAbsolutePath, "-o", output.getAbsolutePath)
    )
    log.debug(s"Running: ${cmdSeq mkString " "}")
    Process(command = cmdSeq, cwd = cwd) ! log
  }
}
