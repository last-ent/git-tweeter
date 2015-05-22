package gittweet

import twitter4j._
import twitter4j.conf.ConfigurationBuilder

object Tweeter {
  val config = {
    val confMap = new DataConfig().getConf()
    new ConfigurationBuilder()
      .setOAuthConsumerKey(confMap("consumerKey"))
      .setOAuthConsumerSecret(confMap("consumerSecret"))
      .setOAuthAccessToken(confMap("accessToken"))
      .setOAuthAccessTokenSecret(confMap("accessSecret"))
      .build
  }
  // def simpleStatusListener = new StatusListener() {
  //   def onStatus(status:Status) { println(status.getText)}
  //   def onDeletionNotice(statusDeleteNotice: StatusDeletionNotice) {}
  //   def onTrackLimitationNotice(numberOfLimitedStatuses: Int) {}
  //   def onException(ex: Exception) { ex.printStackTrace }
  //   def onScrubGeo(arg0: Long, arg1: Long) {}
  //   def onStallWarning(warning: StallWarning) {}
  // }
}

