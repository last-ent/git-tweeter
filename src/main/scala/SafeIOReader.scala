package gittweet.SafeIO

import java.io.{FileWriter, FileNotFoundException, File}

import scala.util.control.NonFatal

object readSafely {
  def apply[R <: {def close() : Unit}](path: String)(fileReader: File => R)(dataExtractor: R => Vector[String]) = {
    // TODO: Convert the code so that only `File` object is required
    var data: Vector[String] = Vector[String]()
    var source: Option[R] = None
    try {
      val filePath = new File(path)
      source = Some(fileReader(filePath))
      data = dataExtractor(source.get)
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

object writeSafely {
  def apply(filePath: File, data: String, append: Boolean=false) = {
    var writer: Option[FileWriter] = None
    try {
      writer = Some(new FileWriter(filePath, append))
      writer.get.write(data)
    } catch {
      case NonFatal(ex) => println(s"Non-Fatal Exception: $ex")
    } finally {
      if (writer != None)
        writer.get.close()
    }
  }
}