package com.artkostm.integrator

package object clump {

  private[clump]type Try[+T] = scala.util.Try[T]
  private[clump] val Try = scala.util.Try
  private[clump] val Success = scala.util.Success
  private[clump] val Failure = scala.util.Failure

  private[clump]type Promise[T] = scala.concurrent.Promise[T]
  private[clump] val Promise = scala.concurrent.Promise

  private[clump]type Future[+T] = scala.concurrent.Future[T]
  private[clump] val Future = scala.concurrent.Future

  private[clump] def awaitResult[T](future: Future[T]) =
    scala.concurrent.Await.result(future, scala.concurrent.duration.Duration.Inf)
}