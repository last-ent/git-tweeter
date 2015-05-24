package gittweet

object GitTweet {
  def main(args: Array[String]): Unit = {
    val xyz = new Gitter().getCommitHash(System.getProperty("user.dir") + "/.git").toList
    println(xyz)
    println((new Gitter).getCommitMessage())
    val twitter = Tweeter.getInstance
    //    val status = twitter.updateStatus("Hello World from git-tweeter")
    //    println(status)
  }
}