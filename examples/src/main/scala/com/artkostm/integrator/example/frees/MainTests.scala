package com.artkostm.integrator.example.frees

import freestyle._

object MainTests extends App {
    val emailSlot = Option(new EditText("artkostm@gmail.com"))
    val passwordSlot = Option(new EditText("12302"))

    import freestyle.implicits._
    import cats.instances.option._
    implicit val h = handlersOption.validatorHandler
    
    val result: Option[Boolean] = program.validate[LoginViewValidation.Op](emailSlot, passwordSlot).interpret[Option]
    
    println(result)

  println("": String)
}

class EditText(text: String) {
  def isEmpty: Boolean = text.isEmpty()
  def getText: String = text
}

@free trait Validator {
  def validateEmail(email: String): FS[Boolean]
  def validatePassword(password: String): FS[Boolean]
}

@module trait LoginViewValidation {
  val validator: Validator
}

object program {
  def validate[F[_]](emailSlot: Option[EditText], passwordSlot: Option[EditText])(implicit LVV: LoginViewValidation[F]): FreeS[F, Boolean] = {
    import LVV._
    
    for {
      isEmailValid <- validator.validateEmail(emailSlot.get.getText)
      isPasswordValid <- validator.validatePassword(passwordSlot.get.getText)
    } yield isEmailValid && isPasswordValid
  }
}

trait handlersOption {
  implicit val validatorHandler = new Validator.Handler[Option] {
    def validateEmail(email: String): Option[Boolean] = Option(!email.isEmpty())
    def validatePassword(password: String): Option[Boolean] = Option(!password.isEmpty() && password.length > 4)
  }
}

object handlersOption extends handlersOption