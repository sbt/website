Define Custom Tasks
-------------------

### Define a Task that runs tests in specific sub-projects

Consider a hypothetical multi-build project with 3 subprojects. The following defines a task `myTestTask` that will
run the `test` Task in specific subprojects  `core` and `tools` but not `client`:

```scala
lazy val core = project.in(file("./core"))
lazy val tools = project.in(file("./tools"))
lazy val client = project.in(file("./client"))

lazy val myTestTask = taskKey[Unit]("my test task")

myTestTask := {
  (core / Test / test).value
  (tools / Test / test).value
}
```
