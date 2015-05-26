package gittweet

import java.io.File

import gittweet.SafeIO.readSafely

import scala.io.Source

/**
 * DataConfig's main method is `getConf`, which on being given a string path will return
 * a Map of Twitter's OAuth credentials.
 */
class DataConfig {
  /**
   *
   * @param path - defaults to "src/main/Secrets/secrets.txt" where user has stored his/her Twitter credentials.
   * @return - Returns Map of Twitter OAuth Credientials with following keys
   *         -> { consumerKey, consumerSecret, accessToken, accessSecret }
   */
  def getConf(path: String = "src/main/Secrets/secrets.txt"): Map[String, String] = {
    val secretLines = readSafely(new File(path)) {
      Source.fromFile
    } {
      _.getLines.toVector
    }
    (secretLines(0).split(","), secretLines(1).split(",")).zipped.map(_.trim -> _.trim).toMap
  }
}
