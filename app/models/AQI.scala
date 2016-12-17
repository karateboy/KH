package models

object AQI {
  def getAQIstyle(aqiStr:String)={
    val aqi = aqiStr.toInt
    if(aqi < 51)
      "Color:Green"
    else if(aqi < 101)
      "Color:Yellow"
    else if(aqi < 151)
      "Color:Orange"
    else if(aqi < 201)
      "Color:Red"
    else
      "Color:Purple"
  }
  
  def format(v:Option[String])={
    if(v.isEmpty)
      "-"
    else
      v.get
  }
}