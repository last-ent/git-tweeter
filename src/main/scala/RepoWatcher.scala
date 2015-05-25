package gittweet

import java.io.File
import scala.io.Source
import util.control.Breaks._
import collection.JavaConversions._
import scala.util.control.NonFatal
import akka.actor.{ActorRef, Actor}
import java.nio.file.{Path, FileSystems}
import java.nio.file.StandardWatchEventKinds._
import gittweet.SafeIO.{readSafely, writeSafely}

case object MonitorRepos
case class RegisterRepo(path: String)
case class Tweet(commitMessage: Vector[String])
case class RegisterRepos(repos: Vector[String])

/**
  * RepoWatcher =>
 *  - Register new & existing set of Repos -> Repo Snooper
 *  - Watch for changes in the Repos -> Repo Snooper
 *  - Fire a message to tweet when a new commit is made -> Commit Tweeter
 *  - Repo Snooper & Commit Tweeter are two Actors
 *
 * Repo Snooper =>
 *  - Check if each path is a valid git repo and register for directory notification only if it is a git repo
 *  - Check if .git/objects/ has changed, if it has then check for latest commit
 *  ? Check if latest commit hash is different from previous tweeted commit for the given branch
 *  - Tweet the latest commit by sending it to Commit Tweeter
 *
 * Commit Tweeter =>
 *  - Define Tweet template
 *  - Receive commits to be tweeted
 *  - Check if network is working to tweet
 *  - If not able to tweet after 3 retries, signal the system.
 *  ? Store tweet timestamp & commit hash
 *  - Tweet to Twitter as per defined template
 */

class RepoCataloguer(catalogPath: String, repoSnooper: ActorRef) extends Actor {
  def receive = {
    case RegisterRepo(path) => {
      val repoCatalogue = new File(catalogPath)
      try {
        if(new File(catalogPath).exists && repoCatalogue.exists) {
          writeSafely(repoCatalogue, catalogPath+"\n")
          repoSnooper ! RegisterRepo(path)
        }
      } catch {
        case NonFatal(ex) => println(s"Non-Fatal Exception: $ex")
      }
    }
  }
}

class RepoSnooper(twitterClient: ActorRef, cataloguePath:String) extends Actor {
  val watcher = FileSystems.getDefault.newWatchService

  def addToWatcher(path: String) = {
    try{
      FileSystems.getDefault.getPath(path)
        .register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
    } catch {
      case NonFatal(ex) => println(s"Non-Fatal Exception: $ex")
    }
  }

  def detectChanges = {
    val watchKey = watcher.take()
    watchKey.pollEvents() foreach {event =>
      breakable{
        if (event.context.toString != ".git" || event.kind == OVERFLOW)
          break

        val relativePath = event.context.asInstanceOf[Path]
        val path = watchKey.watchable.asInstanceOf[Path].resolve(relativePath)
        val commitDetails: Vector[String] = (new Gitter).getCommitMessage(path.toString)
        self ! Tweet(commitDetails)
      }
    }
    watchKey.reset
  }

  override def preStart = {
    val catalogue = new File(cataloguePath)
    if (catalogue.exists){
      val repos = readSafely(cataloguePath) { Source.fromFile } {_.getLines.toVector}
      self ! RegisterRepos(repos)
    }
  }

  def receive = {
    case RegisterRepo(path) => {
      addToWatcher(path)
      self ! MonitorRepos
    }
    case MonitorRepos => {
      detectChanges
      self ! MonitorRepos
    }
    case RegisterRepos(repos) => {
      if(repos.isEmpty)
        self ! MonitorRepos
      else{
        addToWatcher(repos.head)
        self ! RegisterRepos(repos.tail)
      }
    }
    case Tweet(commitDetails) =>
      self ! Tweet(commitDetails)
  }
}

