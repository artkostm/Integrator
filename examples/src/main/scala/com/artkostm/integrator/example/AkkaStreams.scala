package com.artkostm.integrator.example

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.reactivestreams.Publisher
import org.reactivestreams.Subscriber
import akka.stream.scaladsl.Source
import akka.stream.scaladsl.Sink

object AkkaStreams extends App {
  
  val publisher = new Publisher[Int] {
    var subscriber: Subscriber[_ >: Int] = _
    override def subscribe(sub: Subscriber[_ >: Int]): Unit = {
      subscriber = sub
    }
  }
  
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  
//  Source(1 to 6)
//      .map(_.toString)
//      .map(println(_))
//      .to(Sink.head).run
  
  val source = Source.fromPublisher(publisher)
      .map(_.toString)
      .to(Sink.foreach(println(_))).run
      
  Thread.sleep(1000);
  publisher.subscriber.onNext(1)
  Thread.sleep(1000);
  publisher.subscriber.onNext(2)
  Thread.sleep(1000);
  publisher.subscriber.onNext(3)
  Thread.sleep(1000);
  publisher.subscriber.onNext(4)
}