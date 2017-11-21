package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

case class Url(urlId: Long, originalUrl: String)

object Url {

  val baseUrl = "http://localhost:9000/"

  val url = {
    get[Long]("urlId") ~
      get[String]("originalUrl") map {
      case id ~ longUrl => Url(id, longUrl)
    }
  }

  def getTinyUrl(originalUrl: String): String = {

    DB.withConnection { implicit conn =>
      SQL(s"select * from tinyUrl where originalUrl = '$originalUrl'").as(url *).headOption match {
        case Some(x) => baseUrl + x.urlId.toString
        case _ => SQL("insert into tinyUrl (originalUrl) values ({ogUrl})").on(
          'ogUrl -> originalUrl
        ).executeUpdate()
          baseUrl + SQL(s"select * from tinyUrl where originalUrl = '$originalUrl'").as(url *).head.urlId.toString
      }
    }
  }

  def getOriginalUrl(tinyUrl: String): Option[String] = {

    DB.withConnection { implicit conn =>
      SQL(s"select * from tinyUrl where urlId = '${tinyUrl.substring(baseUrl.length)}'").as(url *).headOption match {
        case Some(og) => Some(og.originalUrl)
        case _ => None
      }
    }
  }

}