package example

import example.core.Weather

object Hello {
  def main(args: Array[String]): Unit = {
    val temp = Weather.weather()
    println(s"Hello! The current temperature in New York is $temp C.")
  }
}
