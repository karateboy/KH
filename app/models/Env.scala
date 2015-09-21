package models
import scalikejdbc._
import com.github.nscala_time.time.Imports._
import ModelHelper._

case class Env(date: DateTime,
                name: String,
                sampleHour:Option[Int],
                sampleMethod:Option[String],
                PM25: Option[Float],                
                F_ion: Option[Float],
                CL_ion: Option[Float],
                Br_ion: Option[Float],
                NO2_ion: Option[Float],
                NO3_ion: Option[Float],
                SO4_ion: Option[Float],
                K_ion: Option[Float],
                Na_ion: Option[Float],
                NH4_ion: Option[Float],
                Ca_ion: Option[Float],
                Mg_ion: Option[Float],
                Cr: Option[Float],
                Mn: Option[Float],
                Fe: Option[Float],
                Ni: Option[Float],
                Zn: Option[Float],
                Cd: Option[Float],
                Pb: Option[Float],
                Mg: Option[Float],
                k: Option[Float],
                V: Option[Float],
                Ca: Option[Float],
                Ti: Option[Float],
                Al: Option[Float],
                Arsenic: Option[Float],
                ElemCarbon: Option[Float],
                OrgCarbon: Option[Float],
                TotalCarbon: Option[Float])

/**
 * @author user
 */
object Env {
  def getEnvByName(name: String)(implicit session: DBSession = AutoSession) = {
    sql"""
      Select *
      From Environment
      Where Location like ${'%' + name + '%'}
      """.map { r =>
      Env(r.timestamp(2), r.string(1), r.intOpt(3), r.stringOpt(4), r.floatOpt(5), 
          r.floatOpt(6),r.floatOpt(7),r.floatOpt(8),r.floatOpt(9),r.floatOpt(10),
          r.floatOpt(11),r.floatOpt(12),r.floatOpt(13),r.floatOpt(14),r.floatOpt(15),
          r.floatOpt(16),r.floatOpt(17),r.floatOpt(18),r.floatOpt(19),r.floatOpt(20),          
          r.floatOpt(21),r.floatOpt(22),r.floatOpt(23),r.floatOpt(24),r.floatOpt(25),
          r.floatOpt(26),r.floatOpt(27),r.floatOpt(28),r.floatOpt(29),r.floatOpt(30),
          r.floatOpt(31),r.floatOpt(32),r.floatOpt(33))
    }.list().apply()
  }
  
  def formatS(v:Option[String])={
    if(v.isEmpty)
      "-"
    else
      v.get
  }
  
  def format(v:Option[Float], p:Int=0)={
    if(v.isEmpty)
      "-"
    else if(v.get == 0)
      "ND"
    else
      s"%.${p}f".format(v.get)
  }
}