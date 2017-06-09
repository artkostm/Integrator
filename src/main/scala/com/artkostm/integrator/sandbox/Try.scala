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
}
