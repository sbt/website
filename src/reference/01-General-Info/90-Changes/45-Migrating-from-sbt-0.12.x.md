---
out: Migrating-from-sbt-012x.html
---

## Migrating from sbt 0.12.x

### Introduction

Before sbt 0.13 (sbt 0.9 to 0.12) it was very common to see in builds the usage of two aspects of sbt:

* the key dependency operators: `<<=`, `<+=`, `<++=`
* the tuple enrichments (apply and map) for TaskKey's and SettingKey's (eg. `(foo, bar) map { (f, b) => ... }`)

The release of sbt 0.13 (which was over 3 years ago!) introduced the `.value` macro which allowed for much
easier to read and write code, effectively making both aspects redudant and they were removed from the official
documentation.

As they will be removed in upcoming release of sbt 1.0.0 we've deprecated them in sbt 0.13.13, and here we'll
help guide you to how to migrate your code.

### Migrating simple expressions

With simple expressions such as:

    a  <<= aTaskDef
    b  <+= bTaskDef
    c <++= cTaskDefs

it is sufficient to replace them with the equivalent:

    a  := aTaskDef.value
    b  += bTaskDef.value
    c ++= cTaskDefs.value

### Migrating from the tuple enrichments

As mentioned above, there are two tupe enrichments `.apply` and `.map`. The difference used to be for whether
you're defining a setting for a `SettingKey` or a `TaskKey`, you use `.apply` for the former and `.map` for the
latter:

    val sett1 = settingKey[String]("SettingKey 1")
    val sett2 = settingKey[String]("SettingKey 2")
    val sett3 = settingKey[String]("SettingKey 3")

    val task1 = taskKey[String]("TaskKey 1")
    val task2 = taskKey[String]("TaskKey 2")
    val task3 = taskKey[String]("TaskKey 3")
    val task4 = taskKey[String]("TaskKey 4")

    sett1  := "s1"
    sett2  := "s2"
    sett3 <<= (sett1, sett2)(_ + _)

    task1  := { println("t1"); "t1" }
    task2  := { println("t2"); "t2" }
    task3 <<= (task1, task2) map { (t1, t2) => println(t1 + t2); t1 + t2 }
    task4 <<= (sett1, sett2) map { (s1, s2) => println(s1 + s2); s1 + s2 }

(Remember you can define tasks in terms of settings, but not the other way round)

With the `.value` macro you don't have to know or remember if your key is a `SettingKey` or a `TaskKey`:

    sett1 := "s1"
    sett2 := "s2"
    sett3 := sett1.value + sett2.value

    task1 := { println("t1"); "t1" }
    task2 := { println("t2"); "t2" }
    task3 := { println(task1.value + task2.value); task1.value + task2.value }
    task4 := { println(sett1.value + sett2.value); sett1.value + sett2.value }

### Migrating when using `.dependsOn`

When instead calling `.dependsOn`, instead of:

    a <<= a dependsOn b

define it as:

    a := (a dependsOn b).value

### Migrating when you need to set `Task`s

For keys such as `sourceGenerators` and `resourceGenerators` which use sbt's Task type:

    val sourceGenerators = SettingKey[Seq[Task[Seq[File]]]]("source-generators", "List of tasks that generate sources.", CSetting)
    val resourceGenerators = SettingKey[Seq[Task[Seq[File]]]]("resource-generators", "List of tasks that generate resources.", CSetting)

Where you previous would define things as:

    sourceGenerators in Compile <+= buildInfo

now you define them as:

    sourceGenerators in Compile += buildInfo.taskValue

### Migrating with `InputKey`

When using `InputKey` instead of:

    run <<= docsRunSetting

when migrating you mustn't use `.value` but `.evaluated`:

    run := docsRunSetting.evaluated
