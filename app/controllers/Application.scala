package controllers

import play.api._
import play.api.mvc._
import play.api.Logger
import models._
import com.github.nscala_time.time.Imports._
import models.ModelHelper._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.ws.ning.NingAsyncHttpClientConfigBuilder
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent._

object QueryType extends Enumeration {
  val Mass = Value
  val Ion = Value
  val Metal = Value
  val Carbon = Value
  val descMap = Map(Mass -> "質量濃度分析", Ion -> "離子成分分析", Metal -> "金屬元素成分分析", Carbon -> "碳成分分析")
}

case class RealtimeAQI(success: Boolean, result: AQIResult)
case class AQIResult(records: Seq[AQIrecord])
case class AQIrecord(SiteName: String, County: String, AQI: String, Pollutant: String,
                     Status: String, PM25: String,
                     PublishTime: String)

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

object Application extends Controller {

  val title = "高雄PM2.5監測系統"

//  implicit val aqiRecordRead: Reads[AQIrecord] =
//    ((__ \ "SiteName").read[String] and
//      (__ \ "County").read[String] and
//      (__ \ "AQI").read[String] and
//      (__ \ "Pollutant").read[String] and
//      (__ \ "Status").read[String] and
//      (__ \ "PM2.5").read[String] and
//      (__ \ "PublishTime").read[String])(AQIrecord.apply _)

  implicit val aqiRecordReads = Json.reads[AQIrecord]
  implicit val aqiResult = Json.reads[AQIResult]
  implicit val realtimAQIread = Json.reads[RealtimeAQI]
  implicit val epaRealtimeRead = Json.reads[EpaRealtimeData]
  
  def index = Action {
    implicit request =>
      Redirect("/epbkcg/index.html")
  }

  def commonIndex = Action.async {
    implicit request =>
      {
        val url = "https://opendata.epa.gov.tw/webapi/api/rest/datastore/355000000I-000259/?format=json&token=00k8zvmeJkieHA9w13JvOw"
        WS.url(url).get().map {
          response =>
            try {
              val aqiResult = response.json.validate[RealtimeAQI]
              aqiResult.fold(
                error => {
                  Logger.error(JsError.toJson(error).toString())
                  Ok(views.html.index(Seq.empty[AQIrecord]))
                },
                aqiData => {
                  val kh_data = aqiData.result.records.filter { d => d.County == "高雄市" }
                  Ok(views.html.index(kh_data))
                })
            } catch {
              case ex: Exception =>
                Logger.error(ex.toString())
                Ok(views.html.index(Seq.empty[AQIrecord]))
            }
        }
      }
  }

  def dbQueryIndex = Security.Authenticated.async {
    implicit request =>
      {
        val url = "http://opendata2.epa.gov.tw/AQX.json"
        WS.url(url).get().map {
          response =>
            val epaData = response.json.validate[Seq[EpaRealtimeData]]
            epaData.fold(
              error => {
                Logger.error(JsError.toJson(error).toString())
                BadRequest(Json.obj("ok" -> false, "msg" -> JsError.toJson(error)))
              },
              data => {
                val kh_data = data.filter { d => d.county == "高雄市" }
                Ok(views.html.dbIndex(kh_data))
              })
        }
      }
  }

  def factory = Security.Authenticated {
    implicit request =>
      Ok(views.html.framework("", views.html.factory("")))
  }

  def factoryReport(name: String) = Security.Authenticated {
    implicit request =>
      val factoryList = Factory.getFactoryByName(name)
      Ok(views.html.framework("", views.html.factoryReport(factoryList)))
  }

  def queryPipe(queryTypeStr: String) = Security.Authenticated {
    implicit request =>
      val queryType = QueryType.withName(queryTypeStr)
      val title = "管道" + QueryType.descMap(queryType)
      Ok(views.html.framework(title, views.html.pipe(queryType.toString)))
  }

  def pipeReport(queryTypeStr: String, name: String) = Security.Authenticated {
    implicit request =>
      val queryType = QueryType.withName(queryTypeStr)
      val pipeList = Pipe.getPipeByName(name)
      val title = "管道" + QueryType.descMap(queryType)
      Ok(views.html.framework(title, views.html.pipeReport(queryType, pipeList)))
  }

  def queryEnv(queryTypeStr: String) = Security.Authenticated {
    implicit request =>
      val queryType = QueryType.withName(queryTypeStr)
      val title = "周界" + QueryType.descMap(queryType)
      Ok(views.html.framework(title, views.html.env(queryType.toString)))
  }

  def envReport(queryTypeStr: String, name: String) = Security.Authenticated {
    implicit request =>
      val queryType = QueryType.withName(queryTypeStr)
      val envList = Env.getEnvByName(name)
      val title = "周界" + QueryType.descMap(queryType)
      Ok(views.html.framework(title, views.html.envReport(queryType, envList)))
  }

  def epbkcg(filePath: String) = Action {
    implicit request =>
      Ok("")
  }
}
