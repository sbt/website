#!/usr/bin/env scalas
 
/***
scalaVersion := "2.10.4"

resolvers += Resolver.url("typesafe-ivy-repo", url("http://typesafe.artifactoryonline.com/typesafe/releases"))(Resolver.ivyStylePatterns)
 
libraryDependencies += "org.scala-sbt" % "io" % "0.13.5-RC2"
*/

// $ script/extractcode.scala ../sbt/src/sphinx/**/**.rst > code.txt

import sbt._, Path._
import java.io.File
import java.net.{URI, URL}
import collection.mutable.ListBuffer
def file(s: String): File = new File(s)
def uri(s: String): URI = new URI(s)

/*
A code looks like this:

::

    sbtPlugin := true

    name := "sbt-sample"

    organization := "org.example"

Normal texts starts here, and code ends.

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

def isCodeBlockBegin(line: String): Boolean = """^::\s*""".r.findFirstIn(line).isDefined

def isCodeBlockEnd(line: String): Boolean = (line.trim != "") && 
  ((line(0) != ' ')
   || (line.size > 1 && (line(1) != ' '))
   || (line.size > 2 && (line(2) != ' '))
   || (line.size > 3 && (line(3) != ' ')))

def verticalTrim(block0: List[String]): List[String] = {
  val block1 = if (block0.size > 1 && (block0.head.trim == "")) block0.tail
               else block0
  val block2 = if (block1.size > 1 && (block1.last.trim == "")) block1.init
               else block1
  val block3 = if (block2.size > 1 && (block2.last.trim == "")) block2.init
               else block2
  block3
}

def unindent(line: String): String =
  line match {
    case xs if xs.trim == "" => ""
    case xs if xs startsWith "    " => xs.drop(4)
    case _ => line
  }

def processCodeBlock(block0: List[String]): Unit = {
  val block = verticalTrim(block0)
  println("--------")
  println("")
  println("```scala")
  println(block.map(unindent).mkString("\n"))
  println("```")
  println("")
}

def processFile(f: File): Unit = {
  if (!f.exists) sys.error(s"$f does not exist!")

  println("=========")
  println(f.toString)

  val lines0: Vector[String] = IO.readLines(f).toVector
  val size = lines0.size
  var i = 0
  val buffer = ListBuffer[String]()
  var isCodeBlock = false
  while (i < size - 1) {
    val line0 = lines0(i)
    if (!isCodeBlock) {
      if (isCodeBlockBegin(line0)) {
        isCodeBlock = true
      } // if
    } else {
      if (isCodeBlockEnd(line0)) {
        isCodeBlock = false
        processCodeBlock(buffer.toList)
        buffer.clear()
      } else {
        buffer append line0
      }
    } // if-else
    i = i + 1
  } // while
  // EOF terminates a code block
  if (isCodeBlock) {
    isCodeBlock = false
    processCodeBlock(buffer.toList)
    buffer.clear()
  } // if
}

args foreach { x => processFile(file(x)) }
