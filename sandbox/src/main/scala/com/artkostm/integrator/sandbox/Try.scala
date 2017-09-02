import java.nio.file.{Files, Paths}

/**
  * Created by artsiom.chuiko on 09/06/2017.
  */
object Tr extends App {
  def use[A <: { def close(): Unit }, B](res: A)(code: A => B): Unit = {
    try {
      code(res)
    } finally {
      res.close()
    }
  }

  val path = Paths get ""
  use(Files.newInputStream(path)) { in =>
    //read from stream
  }

  val scores: List[(Int, Int)] = List((5, 3), (6, 6), (7, 10))
  val alice = scores.fold(0)((acc, entry) => entry match {
    case (a: Int, b: Int) if a > b => acc.asInstanceOf[Int] + 1
    case _ => acc.asInstanceOf[Int]
  })
  println(s"$alice")
}
