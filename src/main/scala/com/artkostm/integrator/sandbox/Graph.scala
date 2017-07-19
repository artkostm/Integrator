import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl.{Balance, Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

/**
  * Created by artsiom.chuiko on 18/07/2017.
  */
object Main extends App {
  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  implicit val dispatcher = system.dispatcher

  /**
    * http://doc.akka.io/docs/akka/current/images/compose_graph.png
    *            ________
    *            |      |
    *            | C -> |
    *            |      |
    *   A -> B ->|-------
    *            |      |     F
    *            | D -> | E ->
    *            |______|         G
    *
    *
    */
  import akka.stream.scaladsl.GraphDSL.Implicits._
  val graph = GraphDSL.create() { implicit builder =>
    val B: FlowShape[Int, Int]          = builder.add(Flow[Int].map(_ * 2))
    val D: FlowShape[Int, Int]          = builder.add(Flow[Int].map(_ + 1))
    val E: UniformFanOutShape[Int, Int] = builder.add(Balance[Int](2))
    val G: UniformFanInShape[Int, Int]  = builder.add(Merge[Int](2))


            B  ~>  D  ~>  E  ~>  G
                          E  ~>  G
    FlowShape(B.in, G.out)
  }.named("Koko")

  val future = Source(1 to 10).via(graph).runForeach(println)

  future.onComplete(_ => system.terminate())
}