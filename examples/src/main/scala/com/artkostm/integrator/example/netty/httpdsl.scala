package com.artkostm.integrator.example.netty

import scala.concurrent.Promise
import com.bfil.scalext.ContextualDsl
import scala.concurrent.Future
import scala.concurrent.Future._
import scala.concurrent.ExecutionContext.global

object httpdsl extends App {
  val calc = new Calculator
  
  calc.performCalculation
  
  Thread.sleep(2000)
}

case class ArithmeticContext(resultPromise: Promise[Double], value: Double)

trait CalculatorDsl extends ContextualDsl[ArithmeticContext] {

  def startWith(initialValue: Long)(action: Action) = {
    val p = Promise[Double]
    action(ArithmeticContext(p, initialValue))
    p.future
  }
  
  def add(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value + value))
  def subtract(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value - value))
  def multiplyBy(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value * value))
  def divideBy(value: Double) = mapContext(ctx => ctx.copy(value = ctx.value / value))
  
  def returnResult = ActionResult { ctx => ctx.resultPromise.completeWith(Future { ctx.value }) }
}

class Calculator extends CalculatorDsl {
  def performCalculation: Future[Double] =
    startWith(2) {
      add(3) {
        multiplyBy(3) {
          subtract(5) {
            divideBy(2) {
              returnResult
            }
          }
        }
      }
    }
}