package com.artkostm.integrator.example.frees

import freestyle._
import org.jsoup.Connection
import org.jsoup.nodes.Document

object VkJsoup {
  type Keys = (String, String, String)
  type Account = (String, String)

  def extractAttribute(doc: Document, cssSelector: String, attrName: String): String =
    doc.select(cssSelector).first.attr(attrName)
}

import VkJsoup._
@free trait PageLoader {
  def loadLoginPage(): FS[Connection]
  def loadAccount(account: Account, keys: Keys, cookies: Map[String, String]): FS[String]
}

@free trait PageDataExtractor {
  def extractDocument(connection: Connection): FS[Document]
  def extractCookies(connection: Connection): FS[Map[String, String]]
}

@free trait KeyResolver {
  def getDkey(fkey: String): FS[String]
  def getFkey(doc: Document): FS[String]
  def getLkey(doc: Document): FS[String]
}

case class LoginInfo(ac: String, fkey: String, sid: String)

object Flymer {
  val Domain = "flymer.ru"
  val BaseUrl = s"https://$Domain"
  def LoginUrl(currentTime: Long) = s"$BaseUrl/req/login?ts=$currentTime"

  val Fkey = "fkey"
  val FkeyCssSelector = s"input[name='$Fkey']"
  val Lkey = "lkey"
  val Dkey = "dkey"
  val Sid = "sid"
  val LkeyCssSelector = s"input[name='$Lkey']"
  val Pass = "pass"
  val Email = "email"
  val Ac = "ac"

  val UserAgent = "Mozilla"

  private val responseType = "code"
  private val oauthVersion = "5.4"
  private val clientId = 3206293
  val OAuthUrl = s"https://oauth.vk.com/authorize?client_id=$clientId&scope=&redirect_uri=https%3A%2F%2F$Domain%2Foauth%2Fvk&response_type=$responseType&v=$oauthVersion"

  val NotificationId = 1
}

