package models
import scalikejdbc._

case class Factory(id: String, name: String)

/**
 * @author user
 */
object Factory {
    def getFactoryByName(name: String)(implicit session: DBSession = AutoSession) = {
    sql"""
      Select *
      From Factory
      Where name like ${'%' +name +'%'}
      """.map { r =>
      Factory(r.string(1), r.string(2))
    }.list().apply()
  }
}