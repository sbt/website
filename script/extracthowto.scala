#!/usr/bin/env scalas
 
/***
scalaVersion := "2.10.4"

resolvers += Resolver.url("typesafe-ivy-repo", url("http://typesafe.artifactoryonline.com/typesafe/releases"))(Resolver.ivyStylePatterns)
 
libraryDependencies += "org.scala-sbt" % "io" % "0.13.5-RC2"
*/

// $ script/extracthowto.scala ../sbt/src/sphinx/Howto/*.rst

import sbt._, Path._
import java.io.File
import java.net.{URI, URL}
def file(s: String): File = new File(s)
def uri(s: String): URI = new URI(s)

/*
A how to tag looks like this:

.. howto::
   :id: unmanaged-base-directory
   :title: Change the default (unmanaged) library directory
   :type: setting

   unmanagedBase := baseDirectory.value / "jars"
*/

def extractId(line: String): String = line.replaceAll(":id:", "").trim
def extractTitle(line: String): String = line.replaceAll(":title:", "").trim

def processLine(num: Int, line1: String, line2: String, line3: String): Option[String] =
  line1 match {
    case x if x.trim == ".. howto::" =>
      Some(s"""<a name="${extractId(line2)}"></a>
### ${extractTitle(line3)}""")
    case _ => None
  }

def processFile(f: File): Unit = {
  if (!f.exists) sys.error(s"$f does not exist!")

  val lines0: Vector[String] = IO.readLines(f).toVector
  val size = lines0.size
  val xs: Vector[String] = (0 to size - 3).toVector flatMap { i =>
    processLine(i, lines0(i), lines0(i + 1), lines0(i + 2))
  }
  println("-------------------\n")
  println(xs.mkString("\n\n"))
  println("\n")
  // IO.writeLines(f, xs)
  // println(s"extracted $f")
}

args foreach { x => processFile(file(x)) }
