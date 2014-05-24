#!/usr/bin/env scalas
 
/***
scalaVersion := "2.10.4"

resolvers += Resolver.url("typesafe-ivy-repo", url("http://typesafe.artifactoryonline.com/typesafe/releases"))(Resolver.ivyStylePatterns)
 
libraryDependencies += "org.scala-sbt" % "io" % "0.13.5-RC2"
*/
 
import sbt._, Path._
import java.io.File
import java.net.{URI, URL}
def file(s: String): File = new File(s)
def uri(s: String): URI = new URI(s)

def detectHeader1(line1: String, line2: String): Boolean = """^=+\s*$""".r.findFirstIn(line2).isDefined
def detectHeader2(line1: String, line2: String): Boolean = """^\-+\s*$""".r.findFirstIn(line2).isDefined
def detectHeader3(line1: String): Boolean = """^###\s+\w+""".r.findFirstIn(line1).isDefined
def detectHeader4(line1: String): Boolean = """^####\s+\w+""".r.findFirstIn(line1).isDefined
def demoteHeader1(line1: String, line2: String): (String, String) =
  (line1, line2.replaceAll("=", "-"))
def demoteHeader2(line1: String, line2: String): String = s"### $line1"
def demoteHeader3(line1: String): String = s"#$line1"
def demoteHeader4(line1: String): String = s"#$line1"

def processLine(num: Int, prevOpt: Option[String], current: String, nextOpt: Option[String]): Option[String] =
  (prevOpt, current, nextOpt) match {
    case (Some(prev), _, Some(next)) if num > 3 =>
      (prev, current, next) match {
        case (p, x, n) if detectHeader1(p, x) => Some(demoteHeader1(p, x)._2)
        case (p, x, n) if detectHeader2(x, n) => Some(demoteHeader2(x, n))
        case (p, x, n) if detectHeader2(p, x) => None
        case (p, x, n) if detectHeader3(x)    => Some(demoteHeader3(x))
        case (p, x, n) if detectHeader4(x)    => Some(demoteHeader4(x))
        case _ => Some(current)
      }
    case _ => Some(current)
  }

def processFile(f: File): Unit = {
  if (!f.exists) sys.error(s"$f does not exist!")

  val lines0: Vector[String] = IO.readLines(f).toVector
  val size = lines0.size
  val xs: Vector[String] = (0 to size - 1).toVector flatMap { i =>
    processLine(i,
      if (i == 0) None else Some(lines0(i - 1)),
      lines0(i),
      if (i == size - 1) None else Some(lines0(i + 1)))
  }
  IO.writeLines(f, xs)
  println(s"demoted $f")
}

args foreach { x => processFile(file(x)) }
