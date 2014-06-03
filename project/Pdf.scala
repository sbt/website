import sbt._
import Keys._


object Pdf {
	lazy val pandocLatexEngine = settingKey[String]("Engine to use when generating PDFs")
	lazy val pandocCommand = settingKey[String]("Location of pandoc for running.")
	lazy val pandoc = settingKey[Pandoc]("A runner to execute Pandoc")
	lazy val generatePdf = taskKey[Seq[File]]("Create the tutorial pdf file")

	def settings: Seq[Setting[_]] = 
		Seq(
			pandocLatexEngine in Global := "xelatex",
			pandocCommand in Global := "pandoc",
			pandoc in Global := new Pandoc((pandocCommand in Global).value)
		)

	def settingsFor(config: Configuration, pdfName: String): Seq[Setting[_]] =
	  inConfig(config)(Seq(
        generatePdf := Pdf.makeCombinedPdf(config, pdfName).value,
        mappings in generatePdf := {
        	generatePdf.value x relativeTo(target.value)
        }
	  ))


    def makeCombinedPdf(config: Configuration, name: String): Def.Initialize[Task[Seq[File]]] = Def.task {
      val log = streams.value.log
  	  (mappings in config).value collect {
        case (file, fname) if (fname contains "Combined+Pages.md") && !(fname contains "offline/") =>
          val t = (target in config).value
          val language = getPdfLanguage(file, t)
          // TODO - Create front matter for the file.
          log.info(s"Generating pdf $language/$name...")
          val p = pandoc.value
	      val cwd = file.getParentFile
	      val args = latexArgs(language) ++ Seq("--toc")
	      val pdf = cwd / s"${name}.pdf"
	      p(cwd)(file, pdf, log)
	      pdf
	  }
    }

    def getPdfLanguage(file: File, target: File): String =
    	IO.relativize(target, file.getParentFile) match {
    		case Some(language) => language
    		case None => "en"
    	}

    def latexArgs(language: String): Seq[String] = 
      language match {
      	 case "jp" => Seq("-V", "documentclass=ltjarticle", "--latex-engine=lualatex")
         case _ => Seq("--latex-engine=xelatex")
      }

}

/** Helper to run pandoc. */
class Pandoc(cmd: String) {
	/** Runs pandoc and returns the error code. */
	def apply(cwd: File)(input: File, output: File, log: Logger, extraArgs: String*): Int = {
		Process(command = Seq(cmd) ++ extraArgs ++ Seq(
			input.getAbsolutePath,
			"-o", output.getAbsolutePath
		), cwd = cwd) ! log
	}
}