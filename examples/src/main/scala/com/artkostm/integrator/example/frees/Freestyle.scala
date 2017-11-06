package com.artkostm.integrator.example.frees

import freestyle._
import freestyle.implicits._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import cats.implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.Await

object Freestyle extends App {
  implicit val validationHandler = new Validation.Handler[Future] {
    override def minSize(s: String, n: Int): Future[Boolean] = Future(s.size >= n)
    override def hasNumber(s: String): Future[Boolean] = Future(s.exists(c => "0123456789".contains(c)))
  }

  implicit val interactionHandler = new Interaction.Handler[Future] {
    override def tell(s: String): Future[Unit] = Future.successful(println(s))
    override def ask(s: String): Future[String] = Future.successful { println(s); "This could have been user input 1" }
  }

  def program[F[_]](implicit A: Application[F]) = {
    import A._
    import cats.implicits._
  
    for {
      userInput <- interaction.ask("Give me something with at least 3 chars and a number on it")
      valid <- (validation.minSize(userInput, 3), validation.hasNumber(userInput)).mapN(_ && _).freeS
      _ <- if (valid)
              interaction.tell(s"awesomesauce! '$userInput' is completely valid!") 
           else
              interaction.tell(s"$userInput is not valid")
    } yield ()
  }
  
  val futureValue = program[Application.Op].interpret[Future]

  Await.result(futureValue, Duration.Inf) //blocking only for demo purposes. Don't do this at home.

}

@free trait Validation {
  def minSize(s: String, n: Int): FS[Boolean]
  def hasNumber(s: String): FS[Boolean]
}

@free trait Interaction {
  def tell(msg: String): FS[Unit]
  def ask(prompt: String): FS[String]
}

@module trait Application {
  val validation: Validation
  val interaction: Interaction
}

