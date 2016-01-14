Define Custom Tasks 
-------------------

### Define a Task that runs tests in specific sub-projects

Defines a task `myTestTask` that will run the `test` Task in specific subprojects  `abc` and `def` but no others:

```
lazy val myTestTask = TaskKey[Unit]("my-test-task")

myTestTask <<= Seq(
  test in (abc, Test)
  test in (def, Test)
).dependOn
```