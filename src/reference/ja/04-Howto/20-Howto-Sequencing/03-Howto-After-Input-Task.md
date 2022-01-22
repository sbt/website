---
out: Howto-After-Input-Task.html
---

  [Input-Tasks]: Input-Tasks.html

### インプットタスクの後で何かする

ここまでタスクに焦点を当ててみてきた。タスクには他にインプットタスクというものがあって、これはユーザからの入力をシェル上で受け取る。
典型的な例としては `Compile / run` タスクがある。`scalastyle` タスクも実はインプットタスクだ。インプットタスクの詳細は [Input Task][Input-Tasks] 参照。

ここで、`Compile / run` タスクの実行後にテスト用にブラウザを開く方法を考えてみる。

#### src/main/scala/Greeting.scala

```scala
object Greeting {
  def main(args: Array[String]): Unit = {
    println("hello " + args.toList)
  }
}
```

#### build.sbt v1

```scala
lazy val runopen = inputKey[Unit]("run and then open the browser")

lazy val root = (project in file("."))
  .settings(
    runopen := {
      (Compile / run).evaluated
      println("open browser!")
    }
  )
```

ここでは、ブラウザを本当に開く代わりに副作用のある `println` で例示した。シェルからこのタスクを呼び出してみよう:

```
> runopen foo
[info] Compiling 1 Scala source to /x/proj/...
[info] Running Greeting foo
hello List(foo)
open browser!
```

#### build.sbt v2

この新しいインプットタスクを `Compile / run` に再配線することで、実は `runopen` キーを外すことができる:

```scala
lazy val root = (project in file("."))
  .settings(
    Compile / run := {
      (Compile / run).evaluated
      println("open browser!")
    }
  )
```
