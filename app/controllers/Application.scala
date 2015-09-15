package controllers

import play.api._
import play.api.mvc._
import play.api.Logger
import models._
import com.github.nscala_time.time.Imports._
import models.ModelHelper._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Json
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import scala.concurrent.Future
import play.api.libs.json._
import play.api.libs.functional.syntax._
object Application extends Controller {

  val title = "麥寮廠區空氣品質及氣象監測系統"
  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  case class EpaRealtimeData(
    siteName: String,
    county: String,
    psi: String,
    so2: String,
    co: String,
    o3: String,
    pm10: String,
    pm25: String,
    no2: String,
    windSpeed: String,
    windDir: String,
    publishTime: String)

  implicit val epaRealtimeDataRead: Reads[EpaRealtimeData] =
    ((__ \ "SiteName").read[String] and
      (__ \ "County").read[String] and
      (__ \ "PSI").read[String] and
      (__ \ "SO2").read[String] and
      (__ \ "CO").read[String] and
      (__ \ "O3").read[String] and
      (__ \ "PM10").read[String] and
      (__ \ "PM2.5").read[String] and
      (__ \ "NO2").read[String] and
      (__ \ "WindSpeed").read[String] and
      (__ \ "WindDirec").read[String] and
      (__ \ "PublishTime").read[String])(EpaRealtimeData.apply _)

  def index = Security.Authenticated.async {
    implicit request =>
      {
        val url = "http://opendata.epa.gov.tw/ws/Data/AQX/?$orderby=SiteName&$skip=0&$top=1000&format=json"
        WS.url(url).get().map {
          response =>
            val epaData = response.json.validate[Seq[EpaRealtimeData]]
            epaData.fold(
              error => {
                Logger.error(JsError.toFlatJson(error).toString())
                BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toFlatJson(error)))
              },
              data => {
                val kh_data = data.filter { d => d.county == "高雄市" }
                Ok(views.html.index(kh_data))
              })
        }
      }
  }

  def factory = Security.Authenticated {
    implicit request =>
       Ok(views.html.framework("", views.html.factory("")))
  }
  
  def factoryReport(name:String) = Security.Authenticated {
    implicit request =>
      val factoryList = Factory.getFactoryByName(name)
      Ok(views.html.framework("", views.html.factoryReport(factoryList)))
  }
}
