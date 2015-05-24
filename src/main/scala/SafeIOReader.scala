package gittweet.SafeIO

import java.io.{FileNotFoundException, File}

import scala.util.control.NonFatal

object readSafely {
  def apply[R <: {def close() : Unit}](path: String)(read: File => R)(f: R => List[String]) = {
    var data: List[String] = List[String]()
    var source: Option[R] = None
    try {
      val filePath = new File(path)
      source = Some(read(filePath))
      data = f(source.get)
    } catch {
      case NonFatal(ex) => {
        source = None
        println(s"Non-Fatal Exception: $ex")
      }
    } finally {
      if (source != None)
        source.get.close()
    }
    data
  }
}
