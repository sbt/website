package example.core

import gigahorse._, support.okhttp.Gigahorse
import scala.concurrent._
import play.api.libs.json._

object Weather {
  lazy val http = Gigahorse.http(Gigahorse.config)
  def weather: Future[String] = {
    val r = Gigahorse.url("https://query.yahooapis.com/v1/public/yql").get.
      addQueryString(
        "q" -> """select item.condition
                 |from weather.forecast where woeid in (select woeid from geo.places(1) where text='New York, NY')
                 |and u='c'""".stripMargin,
        "format" -> "json"
      )

    import ExecutionContext.Implicits._
    for {
      f <- http.run(r, Gigahorse.asString)
      x <- parse(f)
    } yield x
  }

  def parse(rawJson: String): Future[String] = {
    val js = Json.parse(rawJson)
    (js \\ "text").headOption match {
      case Some(JsString(x)) => Future.successful(x.toLowerCase)
      case _                 => Future.failed(sys.error(rawJson))
    }
  }
}
