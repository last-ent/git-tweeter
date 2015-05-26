package gittweet

import akka.actor.{ActorSystem, Props}

object GitTweet {
  def main(args: Array[String]): Unit = {
    val xyz = (new Gitter).getCommitHash(System.getProperty("user.dir") + "/.git1").toList
    println(xyz)
    println((new Gitter).getCommitMessage())
    val system = ActorSystem("GitTweeterSystem")
    val tweeter = system.actorOf(Props[CommitTweeter], name = "TweeterActor")
    val snooper = system.actorOf(
      Props(new RepoSnooper(tweeter, System.getProperty("user.dir")+"/src/main/Secrets/repoCatalogs")),
      name = "RepoSnooper"
    )
    val cataloguer = system.actorOf(
      Props(new RepoCataloguer(System.getProperty("user.dir") + "/src/main/Secrets/repoCatalogs", snooper)),
      name = "CataloguerActor"
    )

    cataloguer ! RegisterRepo(System.getProperty("user.dir"))


    //    val twitter = Tweeter.getInstance
    //    val status = twitter.updateStatus("Hello World from git-tweeter")
    //    println(status)
  }
}