package com.artkostm.integrator.example.frees

import com.artkostm.integrator.example.frees.VkJsoup.Account
import freestyle._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.StdIn

@module trait FlymerLoginModule {
  val loader: PageLoader
  val dataExtractor: PageDataExtractor
  val keyResolver: KeyResolver
}

class Programs[F[_]](implicit FLM: FlymerLoginModule[F]) {
  import FLM._

  type FS[A] = FreeS[F, A]

  def loginViaFlymer(account: Account): FS[LoginInfo] =
    for {
      connection <- loader.loadLoginPage()
      document <- dataExtractor.extractDocument(connection)
      cookies <- dataExtractor.extractCookies(connection)
      fkey <- keyResolver.getFkey(document)
      lkey <- keyResolver.getLkey(document)
      dkey <- keyResolver.getDkey(fkey)
      ac <- loader.loadAccount(account, (fkey, lkey, dkey), cookies)
    } yield LoginInfo(ac, fkey, cookies(Flymer.Sid))
}

object Fl extends App {
  import freestyle.implicits._
  import cats.instances.option._
  implicit val loader = flymerOptions.loaderHandler
  implicit val extractor = flymerOptions.pageDataExtractorHandler
  implicit val resolver = flymerOptions.keyResolverHandler

  val program = new Programs[FlymerLoginModule.Op]()

  import scala.concurrent.ExecutionContext.Implicits.global

  List("lol", "lal", "lil", "lel").map(_.toUpperCase).flatMap(_.toCharArray).foreach(println)

  println("Press Enter to shutdown server")
  StdIn.readLine()

  var start = System.currentTimeMillis()

  val res: Option[LoginInfo] = program.loginViaFlymer(("m", "0")).interpret[Option]

  println(res)

  println(s"Time: ${System.currentTimeMillis() - start}")
}