package com.artkostm.integrator.example.frees

import org.jsoup.nodes.Document
import org.jsoup.{Connection, Jsoup}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object plainFlymer extends App {
  import scala.concurrent.ExecutionContext.Implicits.global
  def Dkey(fkey: String): Int = {
    fkey.foldLeft(0)( (n, g) => {
      val m = (n << 5) - n + g
      m & m
    })
  }

  def attemptLogin(email: String, pass: String): Future[Try[LoginInfo]] = Future {
    Try({
      val con = requestLoginPage()
      val doc = con.get
      val cookies = con.response.cookies
      val fkey = getAttr(doc, Flymer.FkeyCssSelector, "value")
      val lkey = getAttr(doc, Flymer.LkeyCssSelector, "value")
      val dkey = Dkey(fkey)
      cookies.put(Flymer.Fkey, fkey)
      val ac = requestAccount(email, pass, fkey, lkey, dkey, cookies)
      new LoginInfo(ac, fkey, cookies.get(Flymer.Sid))
    })
  }

  protected def requestLoginPage(): Connection = Jsoup.connect(Flymer.BaseUrl).userAgent(Flymer.UserAgent).method(Connection.Method.GET)

  protected def requestAccount(email: String, pass: String, fkey: String, lkey: String,
                               dkey: Int, cookies: java.util.Map[String, String]): String = {
    Jsoup.connect(Flymer.LoginUrl(System.currentTimeMillis)).
      data(Flymer.Pass, pass).data(Flymer.Email, email).
      data(Flymer.Fkey, fkey).data(Flymer.Lkey, lkey).
      data(Flymer.Dkey, String.valueOf(dkey)).
      header("Content-Type", "application/x-www-form-urlencoded").
      header("Connection", "keep-alive").
      cookies(cookies).
      method(Connection.Method.POST).
      execute().
      cookie(Flymer.Ac)
  }

  protected def getAttr(doc: Document, cssSelector: String, attrName: String): String = doc.select(cssSelector).first.attr(attrName)

  protected def getAttr(cookies: java.util.Map[String, String], name: String): String = cookies.get(name)

  val start = System.currentTimeMillis()

  val future = attemptLogin("@gmail", "")

  println(Await.result(future, Duration.Inf))

  println(s"Time: ${System.currentTimeMillis() - start}")
}
