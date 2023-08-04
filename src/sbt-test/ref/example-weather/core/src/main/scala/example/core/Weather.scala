package example.core

import sttp.client4.quick._
import sttp.client4.Response

object Weather {
  def weather() = {
    val response: Response[String] = quickRequest
      .get(
        uri"https://api.open-meteo.com/v1/forecast?latitude=$newYorkLatitude&longitude=$newYorkLongitude&current_weather=true"
      )
      .send()
    val json = ujson.read(response.body)
    json.obj("current_weather")("temperature").num
  }

  private val newYorkLatitude: Double = 40.7143
  private val newYorkLongitude: Double = -74.006
}
