package gittweet

import twitter4j._

object GitTweet {
  def main(args: Array[String]): Unit = {
    println(new Gitter().getCommitMessage(System.getProperty("user.dir")))
    val twitter = new TwitterFactory(Tweeter.config).getInstance
    // val status = twitter.updateStatus("Hello World from git-tweeter")
    // println(status)
  }
}
