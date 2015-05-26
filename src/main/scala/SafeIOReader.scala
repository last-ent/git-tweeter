package gittweet.SafeIO

import java.io.{File, FileWriter}

import scala.util.control.NonFatal

/**
 * Source.fromFile doesn't close the file after reading.
 * This object ensures it happens.
 * Note: Is it possible that using this class as an `object` is returning older commit messages?
 */
object readSafely {
  def apply[R <: {def close() : Unit}](filePath: File)(fileReader: File => R)(dataExtractor: R => Vector[String]) = {
    // TODO: Convert the code so that only `File` object is required
    var data: Vector[String] = Vector[String]()
    var source: Option[R] = None
    try {
      source = Some(fileReader(filePath))
      data = dataExtractor(source.get)
    } catch {
      case NonFatal(ex) => {
        source = None
        println(s"Non-Fatal Exception(readSafely): $ex")
        //        val x = ex.getStackTrace.toVector
        //        println(s"StackTrace (readSafely): $x")
      }
    } finally {
      if (source != None)
        source.get.close()
    }
    data
  }
}

/**
 * For consistency and removing boilerplate code,
 * we use writeSafely to write into files.
 */
object writeSafely {
  def apply(filePath: File, data: String, append: Boolean = true) = {
    var writer: Option[FileWriter] = None
    try {
      writer = Some(new FileWriter(filePath, append))
      writer.get.write(data)
    } catch {
      case NonFatal(ex) => println(s"Non-Fatal Exception(writeSafely): $ex")
    } finally {
      if (writer != None)
        writer.get.close()
    }
  }
}