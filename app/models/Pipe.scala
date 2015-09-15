package models
import scalikejdbc._
import com.github.nscala_time.time.Imports._
import ModelHelper._

case class Pipe(date: DateTime,
                name: String,
                CtrlSerial: Option[String],
                PsSerial: Option[String],
                PipeSerial: Option[String],
                Source: Option[String],
                Equipment: Option[String],
                FPM: Option[Float],
                CPM: Option[Float],
                TSP: Option[Float],
                F_ion: Option[Float],
                CL_ion: Option[Float],
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
object Pipe {
  def getPipeByName(name: String)(implicit session: DBSession = AutoSession) = {
    sql"""
      Select *
      From Pipe
      Where name like ${'%' + name + '%'}
      """.map { r =>
      Pipe(r.timestamp(1), r.string(2), r.stringOpt(3),r.stringOpt(4),r.stringOpt(5),r.stringOpt(6),r.stringOpt(7),
          r.floatOpt(8),r.floatOpt(9),r.floatOpt(10),
          r.floatOpt(11),r.floatOpt(12),r.floatOpt(13),r.floatOpt(14),r.floatOpt(15),
          r.floatOpt(16),r.floatOpt(17),r.floatOpt(18),r.floatOpt(19),r.floatOpt(20),
          r.floatOpt(21),r.floatOpt(22),r.floatOpt(23),r.floatOpt(24),r.floatOpt(25),
          r.floatOpt(26),r.floatOpt(27),r.floatOpt(28),r.floatOpt(29),r.floatOpt(30),
          r.floatOpt(31),r.floatOpt(32),r.floatOpt(33),r.floatOpt(34),r.floatOpt(35))
    }.list().apply()
  }
}