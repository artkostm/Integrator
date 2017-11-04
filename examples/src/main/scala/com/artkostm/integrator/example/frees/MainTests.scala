package com.artkostm.integrator.example.frees

import freestyle._

object MainTests extends App {
    val emailSlot = Option(new EditText("artkostm@gmail.com"))
    val passwordSlot = Option(new EditText("12345"))
    
    import freestyle.implicits._
    import handlersOption._
    
    val result: Option[Boolean] = program.validate[LoginViewValidation.Op](emailSlot, passwordSlot).interpret[Option]
    
    println(result)
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
    import cats.implicits._
    
    for {
//      email <- FreeS.liftFA(emailSlot)
//      password <- FreeS.liftFA(passwordSlot)
      result <- (validator.validateEmail(emailSlot.get.getText), validator.validatePassword(passwordSlot.get.getText)).mapN(_ && _).freeS
    } yield result
  }
}

trait handlersOption {
  implicit val validatorHandler = new Validator.Handler[Option] {
    def validateEmail(email: String): Option[Boolean] = Option(!email.isEmpty())
    def validatePassword(password: String): Option[Boolean] = Option(!password.isEmpty() && password.length > 4)
  }
}

object handlersOption extends handlersOption