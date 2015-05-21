package gittweet

import twitter4j._
import twitter4j.conf.ConfigurationBuilder

class Tweeter {
  def getTwitterClient = {
    val confMap = new DataConfig
//    val confs = Array("consumerKey", "consumerSecret", "accessToken", "accessSecret")
    new ConfigurationBuilder()
      .setOAuthConsumerKey(confMap.getConf("consumerKey"))
      .setOAuthConsumerSecret(confMap.getConf("consumerSecret"))
      .setOAuthAccessToken(confMap.getConf("accessToken"))
      .setOAuthAccessTokenSecret(confMap.getConf("accessSecret"))
  }
}

