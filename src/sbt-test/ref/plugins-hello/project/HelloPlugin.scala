package sbthello

import sbt.*
import Keys.*

object HelloPlugin extends AutoPlugin:
  override def trigger = allRequirements

  object autoImport:
    val helloGreeting = settingKey[String]("greeting")
    val hello = taskKey[Unit]("say hello")
  end autoImport

  import autoImport.*
  override lazy val globalSettings: Seq[Setting[?]] = Seq(
    helloGreeting := "hi",
  )

  override lazy val projectSettings: Seq[Setting[?]] = Seq(
    hello := {
      val s = streams.value
      val g = helloGreeting.value
      s.log.info(g)
    }
  )
end HelloPlugin
