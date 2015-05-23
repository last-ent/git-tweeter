package gittweet.SafeIO

import java.io.File

object readSafely {
  def apply[R <: {def close() : Unit}, T](path: String)(read: File => R)(f: R => T) = {
    val filePath = new File(path)
    val source = read(filePath)
    val data = f(source)
    source.close()

    data
  }
}
