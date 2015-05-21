package gittweet

import scala.io.Source

class DataConfig {
  def getConf(path:String = "src/main/Secrets/secrets.txt"):Map[String, String] = {
    val secretLines = Source.fromFile(path).mkString.split("\n")
    (secretLines(0).split(","), secretLines(1).split(",")).zipped.map(_.trim -> _.trim).toMap
  }
}
