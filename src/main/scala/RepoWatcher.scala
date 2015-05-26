package gittweet

import java.io.File
import java.nio.file.StandardWatchEventKinds._
import java.nio.file.{FileSystems, Path}

import akka.actor.{Actor, ActorRef}
import gittweet.SafeIO.{readSafely, writeSafely}

import scala.collection.JavaConversions._
import scala.io.Source
import scala.util.control.Breaks._
import scala.util.control.NonFatal

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
        val newPath = new File(path)
        if (newPath.exists && repoCatalogue.exists) {
          writeSafely(repoCatalogue, path + "\n")
          repoSnooper ! RegisterRepo(path)
        }
      } catch {
        case NonFatal(ex) => println(s"Non-Fatal Exception(RepoCataloguer): $ex")
      }
    }
  }
}

class RepoSnooper(twitterClient: ActorRef, cataloguePath: String) extends Actor {
  val watcher = FileSystems.getDefault.newWatchService

  override def preStart = {
    val catalogue = new File(cataloguePath)
    if (catalogue.exists) {
      val repos = readSafely(catalogue) {
        Source.fromFile
      } {
        _.getLines.toVector
      }
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
      if (repos.isEmpty)
        self ! MonitorRepos
      else {
        addToWatcher(repos.head)
        self ! RegisterRepos(repos.tail)
      }
    }
    case Tweet(commitDetails) =>
      twitterClient ! Tweet(commitDetails)
  }

  def addToWatcher(path: String) = {
    try {
      FileSystems.getDefault.getPath(path + "/.git/objects")
        .register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
    } catch {
      case NonFatal(ex) => println(s"Non-Fatal Exception(RepoSnooper): $ex")
    }
  }

  def detectChanges = {
    val watchKey = watcher.take()
    watchKey.pollEvents() foreach { event =>
      breakable {
        if (event.kind == OVERFLOW)
          break

        val relativePath = event.context.asInstanceOf[Path]
        val path = watchKey.watchable.asInstanceOf[Path].resolve(relativePath)
        val sPath = path.toString.split("/.git/objects")(0)

        val commitDetails: Vector[String] = (new Gitter).getCommitMessage(sPath+"/.git")
        self ! Tweet(commitDetails)
      }
    }
    watchKey.reset
  }
}

class CommitTweeter extends Actor {
  val tweeter = Tweeter.getInstance
  def receive = {
    case Tweet(commitMessage) => {
      val Vector(branch: String, commMessage: String, hash: String) = commitMessage
      tweeter.updateStatus(s"New commit on branch $branch. Commit Message: $commMessage".substring(0, 139))
    }
  }
}