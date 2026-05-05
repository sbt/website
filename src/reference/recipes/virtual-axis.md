Cross building on a virtual axis
================================

~~~admonish note
The recipe section of the documentation focuses on the objectives
with minimal explanations.
~~~

Objective
---------

I want to cross build on a virtual axis that represents Apache Spark version.

Steps
-----

1. Define a weak axis locally:

~~~admonish example title="project/Axis.scala"
```scala
import sbt.*

case class SparkAxis(idSuffix: String, directorySuffix: String)
  extends VirtualAxis.WeakAxis
```
~~~

2. Define a project matrix with custom rows. The following builds Scala 2.12 and 2.13 for Spark 3, and Scala 2.13 and 3 for Spark 4:

~~~admonish example title="build.sbt"
```scala
lazy val scala212 = "{{scala2_12_example_version}}"
lazy val scala213 = "{{scala2_13_example_version}}"
lazy val scala3 = "{{scala3_example_version}}"
lazy val spark3 = SparkAxis(idSuffix = "Spark3", directorySuffix = "spark3")
lazy val spark4 = SparkAxis(idSuffix = "Spark4", directorySuffix = "spark4")

organization := "com.example"
version := "0.1.0-SNAPSHOT"

lazy val app = (projectMatrix in file("app"))
  .settings(
    name := "app"
  )
  .customRow(
    scalaVersions = Seq(scala212, scala213),
    axisValues = Seq(spark3, VirtualAxis.jvm),
    settings = Seq(
      moduleName := name.value + "_spark3",
      libraryDependencies += "org.apache.spark" %% "spark-core" % "3.5.8",
    )
  )
  .customRow(
    scalaVersions = Seq(scala213, scala3),
    axisValues = Seq(spark4, VirtualAxis.jvm),
    settings = Seq(
      moduleName := name.value + "_spark4",
      libraryDependencies += ("org.apache.spark" %% "spark-core" % "4.1.0")
        .cross(CrossVersion.for3Use2_13),
    )
  )
```
~~~

3. Place the source code under `app/src/main/scala`:


~~~admonish example title="app/src/main/scala/example/A.scala"
```scala
package example

import org.apache.spark.SparkContext

object A {
  def main(args: Array[String]): Unit = {
    val sc = new SparkContext("local", "test")
    sc.stop()
  }
}
```
~~~

Confirm that this runs:

```bash
sbt:virtual-axis-root> appSpark4/run
[info] running (fork) example.A
Using Spark's default log4j profile: org/apache/spark/log4j2-defaults.properties
26/05/05 00:00:00 INFO SparkContext: Running Spark version 4.1.0
```
