package gittweet

import scala.io.Source

class DataConfig {
  def getSecretLines(path:String) = Source.fromFile(path).mkString.split("\n")

  def getConf(path:String = "src/main/Secrets/secrets.txt"):Map[String, String] = {
    val secretLines = getSecretLines(path)
    (secretLines(0).split(","), secretLines(1).split(",")).zipped.map(_.trim -> _.trim).toMap
  }
}
