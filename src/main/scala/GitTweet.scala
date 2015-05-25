package gittweet

import akka.actor.{Props, ActorSystem}

object GitTweet {
  def main(args: Array[String]): Unit = {
    val xyz = (new Gitter).getCommitHash(System.getProperty("user.dir") + "/.git1").toList
    println(xyz)
    println((new Gitter).getCommitMessage())
    val system = ActorSystem("HelloSystem")
    val helloActor = system.actorOf(Props[RepoSnooper], name="RepoSnooper")

//    val twitter = Tweeter.getInstance
    //    val status = twitter.updateStatus("Hello World from git-tweeter")
    //    println(status)
  }
}