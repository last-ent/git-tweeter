package gittweet

import java.io.File

import gittweet.SafeIO.readSafely

import scala.io.Source

class DataConfig {
  def getConf(path: String = "src/main/Secrets/secrets.txt"): Map[String, String] = {
    val secretLines = getSecretLines(path)
    (secretLines(0).split(","), secretLines(1).split(",")).zipped.map(_.trim -> _.trim).toMap
  }

  def getSecretLines(path: String) = {
    val file = new File(path)
    readSafely(file) {
      Source.fromFile
    } {
      _.getLines.toVector
    }
  }
}
