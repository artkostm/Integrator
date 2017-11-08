package com.artkostm.integrator.example

import spray.json._
import spray.json.DefaultJsonProtocol

object SprayExample extends App {
  implicit val fl = FlymerJsonProtocol.FlymerResponseFormat
  
//  val reply = FlymerResponse(Some(FlymerReplies("1", Some("url"))), None)
//  println(reply.toJson)
  println("""{"replies":{"num":"1","url":"url"}}""".parseJson.convertTo[FlymerResponse])
  
}

case class FlymerError(`type`: String)
case class FlymerReplies(num: String, url: Option[String])
case class FlymerResponse(replies: Option[FlymerReplies], error: Option[FlymerError])

object FlymerJsonProtocol extends DefaultJsonProtocol {
  implicit val FlymerErrorFormat = jsonFormat(FlymerError.apply _, "type")
  implicit val FlymerRepliesFormat = jsonFormat(FlymerReplies.apply, "num", "url")
  implicit val FlymerResponseFormat = jsonFormat(FlymerResponse.apply, "replies", "error")
}

//{"replies":{"num":"1","url":"url"}}
//{"error":{"type":"auth"}}