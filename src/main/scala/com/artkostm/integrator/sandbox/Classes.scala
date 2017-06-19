import akka.actor.Actor
import org.clapper.classutil.{ClassFinder, MapToBean, ScalaObjectToBean, ClassUtil => _c}

/**
  * Created by artsiom.chuiko on 19/06/2017.
  */

class MyActor {
  def receive = ""
}
object Classes extends App {
  println(_c.classSignature(classOf[MyActor]))
  val actor = new MyActor
  val map = Map("first" -> 1, "second" -> "2")
  val bean = MapToBean(map, true)
  println(bean.getClass)
}
