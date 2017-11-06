package com.artkostm.integrator.example.frees

import com.artkostm.integrator.example.frees.VkJsoup.{Account, Keys}
import org.jsoup.nodes.Document
import org.jsoup.{Connection, Jsoup}

import scala.concurrent.Future

trait flymerHandlersOption {
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.collection.JavaConverters._
  implicit val loaderHandler = new PageLoader.Handler[Option] {
    override protected[this] def loadLoginPage =
      Option(Jsoup.connect(Flymer.BaseUrl).userAgent(Flymer.UserAgent).method(Connection.Method.GET))

    override protected[this] def loadAccount(account: Account, keys: Keys, cookies: Map[String, String]) = {
      Option(Jsoup.connect(Flymer.LoginUrl(System.currentTimeMillis)).
        data(Flymer.Pass, account._2).data(Flymer.Email, account._1).
        data(Flymer.Fkey, keys._1).data(Flymer.Lkey, keys._2).
        data(Flymer.Dkey, keys._3).
        header("Content-Type", "application/x-www-form-urlencoded").
        header("Connection", "keep-alive").
        cookies(cookies.asJava).
        method(Connection.Method.POST).
        execute().
        cookie(Flymer.Ac))
    }
  }

  implicit val pageDataExtractorHandler = new PageDataExtractor.Handler[Option] {
    override protected[this] def extractDocument(connection: Connection) = Option(connection.get)

    override protected[this] def extractCookies(connection: Connection) =
      Option(connection.response.cookies.asScala.toMap)
  }

  implicit val keyResolverHandler = new KeyResolver.Handler[Option] {
    override protected[this] def getDkey(fkey: String) = Option(fkey.foldLeft(0) { (n, g) =>
      val m = (n << 5) - n + g
      m & m
    }.toString)

    override protected[this] def getFkey(doc: Document) = Option(VkJsoup.extractAttribute(doc, Flymer.FkeyCssSelector, "value"))

    override protected[this] def getLkey(doc: Document) = Option(VkJsoup.extractAttribute(doc, Flymer.LkeyCssSelector, "value"))
  }
}

object flymerOptions extends flymerHandlersOption