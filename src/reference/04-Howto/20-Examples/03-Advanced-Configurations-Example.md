---
out: Advanced-Configurations-Example.html
---

  [Basic-Def]: Basic-Def.html

Advanced configurations example
-------------------------------

This is an example [.sbt build definition][Basic-Def]
that demonstrates using configurations to group dependencies.

The `utils` module provides utilities for other modules. It uses
configurations to group dependencies so that a dependent project doesn't
have to pull in all dependencies if it only uses a subset of
functionality. This can be an alternative to having multiple utilities
modules (and consequently, multiple utilities jars).

In this example, consider a `utils` project that provides utilities
related to both Scalate and Saxon. It therefore needs both Scalate and
Saxon on the compilation classpath and a project that uses all of the
functionality of 'utils' will need these dependencies as well. However,
project `a` only needs the utilities related to Scalate, so it doesn't
need Saxon. By depending only on the `scalate` configuration of `utils`,
it only gets the Scalate-related dependencies.

```scala
/********* Configurations *******/

// Custom configurations
lazy val Common = config("common") describedAs("Dependencies required in all configurations.")
lazy val Scalate = config("scalate") extend(Common) describedAs("Dependencies for using Scalate utilities.")
lazy val Saxon = config("saxon") extend(Common) describedAs("Dependencies for using Saxon utilities.")

// Define a customized compile configuration that includes
//   dependencies defined in our other custom configurations
lazy val CustomCompile = config("compile") extend(Saxon, Common, Scalate)

/********** Projects ************/

// factor out common settings into a sequence
lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "2.10.4"
)

// An example project that only uses the Scalate utilities.
lazy val a = (project in file("a")).
  dependsOn(utils % "compile->scalate").
  settings(commonSettings: _*)

// An example project that uses the Scalate and Saxon utilities.
// For the configurations defined here, this is equivalent to doing dependsOn(utils),
//  but if there were more configurations, it would select only the Scalate and Saxon
//  dependencies.
lazy val b = (project in file("b")).
  dependsOn(utils % "compile->scalate,saxon").
  settings(commonSettings: _*)

// Defines the utilities project
lazy val utils = (project in file("utils")).
  settings(commonSettings: _*).
  settings(inConfig(Common)(Defaults.configSettings): _*).  // Add the src/common/scala/ compilation configuration.
  settings(addArtifact(artifact in (Common, packageBin), packageBin in Common): _*). // Publish the common artifact
  settings(
      // We want our Common sources to have access to all of the dependencies on the classpaths
      //   for compile and test, but when depended on, it should only require dependencies in 'common'
    classpathConfiguration in Common := CustomCompile,
      // Modify the default Ivy configurations.
      //   'overrideConfigs' ensures that Compile is replaced by CustomCompile
    ivyConfigurations := overrideConfigs(Scalate, Saxon, Common, CustomCompile)(ivyConfigurations.value),
      // Put all dependencies without an explicit configuration into Common (optional)
    defaultConfiguration := Some(Common),
      // Declare dependencies in the appropriate configurations
    libraryDependencies ++= Seq(
       "org.fusesource.scalate" % "scalate-core" % "1.5.0" % Scalate,
       "org.squeryl" %% "squeryl" % "0.9.5-6" % Scalate,
       "net.sf.saxon" % "saxon" % "8.7" % Saxon
    )
  )
```
