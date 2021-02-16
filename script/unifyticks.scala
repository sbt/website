#!/usr/bin/env scalas
 
/***
// $ cs sbt/sbt --branch 0.13.2b
// $ export CONSCRIPT_OPTS="-XX:MaxPermSize=512M -Dfile.encoding=UTF-8"
// $ chmod -x scripts.scala
scalaVersion := "2.10.4"
 
resolvers += Resolver.url("typesafe-ivy-repo", url("http://typesafe.artifactoryonline.com/typesafe/releases"))(Resolver.ivyStylePatterns)
 
libraryDependencies += "org.scala-sbt" % "io" % "0.13.5-RC2"
*/
 
import sbt._, Path._
import java.io.File
import java.net.{URI, URL}
import sys.process._
def file(s: String): File = new File(s)
def uri(s: String): URI = new URI(s)
 
def removeRole(role: String): String => String =
  _.replaceAll("""(:""" + role + """:)(\`[^`]+\`)""", """$2""")
def nTicks(n: Int): String = """(\`{""" + n.toString + """})"""
def toSingleTicks: String => String = 
  _.replaceAll(nTicks(2), "`")
def toDoubleTicks: String => String =
  _.replaceAll(nTicks(1), "``")
val preprocessRest: String => String =
  removeRole("doc") andThen removeRole("key") andThen removeRole("ref") andThen toSingleTicks andThen toDoubleTicks
val isVerboseLine: String => Boolean = _.startsWith("  ")
 
val processLine: String => String = {
  case s if isVerboseLine(s) => identity(s)
  case s => preprocessRest(s)
}
 
// https://www.scala-lang.org/api/2.10.4/index.html#scala.sys.process.ProcessBuilder
def runPandoc(f: File): Seq[String] =
  Seq("pandoc", "-f", "rst", "-t", "markdown", f.toString).lines.toSeq
 
val outDir = file("./target/")
val srcDir = file("./src/")
val toOut = rebase(srcDir, outDir)
 
def processFile(f: File): Unit = {
  val (base, ext) = f.baseAndExt
  val newParent = toOut(f.getParentFile) getOrElse {sys.error("wat")}
  val file1 = newParent / (base + "." + ext)
  val file2 = newParent / (base + ".md")
  println(s"""$f => $file2""")
  val xs = IO.readLines(f) map { processLine }
  IO.writeLines(file1, xs)
  val mdLines = runPandoc(file1)
  IO.delete(file1)
  IO.writeLines(file2, mdLines)
}
 
val fs: Seq[File] = (srcDir ** "*.rst").get
fs foreach { processFile }
