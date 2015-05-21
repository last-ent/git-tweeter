package gittweet

import twitter4j._
import twitter4j.conf.ConfigurationBuilder

class Tweeter {
  def getTwitterClient = {
    val confMap = (new DataConfig).getConf()
    new ConfigurationBuilder()
      .setOAuthConsumerKey(confMap("consumerKey"))
      .setOAuthConsumerSecret(confMap("consumerSecret"))
      .setOAuthAccessToken(confMap("accessToken"))
      .setOAuthAccessTokenSecret(confMap("accessSecret"))
  }
}

