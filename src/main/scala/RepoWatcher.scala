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
 * - Register new & existing set of Repos -> Repo Snooper
 * - Watch for changes in the Repos -> Repo Snooper
 * - Fire a message to tweet when a new commit is made -> Commit Tweeter
 * - Repo Snooper & Commit Tweeter are two Actors
 *
 * Repo Snooper =>
 * - Check if each path is a valid git repo and register for directory notification only if it is a git repo
 * - Check if .git/objects/ has changed, if it has then check for latest commit
 * ? Check if latest commit hash is different from previous tweeted commit for the given branch
 * - Tweet the latest commit by sending it to Commit Tweeter
 *
 * Commit Tweeter =>
 * - Define Tweet template
 * - Receive commits to be tweeted
 * - Check if network is working to tweet
 * - If not able to tweet after 3 retries, signal the system.
 * ? Store tweet timestamp & commit hash
 * - Tweet to Twitter as per defined template
 */

/**
 * Helps register new git repo from the given path as well as keep the path persistent for restart of application.
 * @param catalogPath - Path of new git repo to be added.
 * @param repoSnooper - Needs our main RepoSnooper Actor to register it for watching.
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

/**
 *
 * @param twitterClient - Used for tweeting tweets.
 * @param cataloguePath - Loads up all given paths in file cataloguePath for watching at start of application.
 */
class RepoSnooper(twitterClient: ActorRef, cataloguePath: String) extends Actor {
  val watcher = FileSystems.getDefault.newWatchService

  /**
   * We use this method to load all given paths.
   */
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
      self ! MonitorRepos
  }

  /**
   *
   * For any given repo, we monitor path/.git/objects/ for changes.
   * Logic being that if a new commit is made, it will add new folder and we can hence detect changes.
   *
   * NOTE: There might be a chance that the actual commit files being generated inside `objects/xyz/`
   * are not being notified for change. As a result certain commits might be lost.
   *
   * @param path - path to git repo we want to monitor
   * @return - Unit
   */
  def addToWatcher(path: String) = {
    try {
      FileSystems.getDefault.getPath(path + "/.git/objects")
        .register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
    } catch {
      case NonFatal(ex) => println(s"Non-Fatal Exception(RepoSnooper): $ex")
    }
  }

  /**
   * Main chunk of logic that goes over all registered paths looking for changes.
   * If there are any changes in any of the registered paths, retrieves the latest commit
   * and messages it to twitterClient for tweeting to Twitter
   */
  def detectChanges = {
    val watchKey = watcher.take()
    var commits = scala.collection.mutable.Set[Vector[String]]()
    watchKey.pollEvents() foreach { event =>
      breakable {
        if (event.kind == OVERFLOW)
          break

        val relativePath = event.context.asInstanceOf[Path]
        val path = watchKey.watchable.asInstanceOf[Path].resolve(relativePath)
        println(s"Context: $path")
        val sPath = path.toString.split("/.git/")(0)

        val commitDetails: Vector[String] = (new Gitter).getCommitMessage(sPath + "/.git")
        commits += commitDetails
      }
    }
    watchKey.reset
    println(s"Commits for tweeting:\n$commits")
    commits.foreach(twitterClient ! Tweet(_))
  }
}

/**
 * Tweets to Twitter.
 * Also ensures that duplicate Tweets are not sent.
 */
class CommitTweeter extends Actor {
  val tweeter = Tweeter.getInstance
  val branchCommits = collection.mutable.Map[String, String]()

  def receive = {
    case Tweet(commitMessage) => breakable {
      println(s"Tweeting... $commitMessage")

      var Vector(branch: String, msg: String, hash: String) = commitMessage

      if (branchCommits.getOrElse(branch, "New Branch") == hash) {
        println(s"New Branch: $branch or the commit already exists: $hash")
        break
      }

      branchCommits(branch) = hash
      msg = s"Tweeted by: https://github.com/last-ent/git-tweeter/\nBranch `$branch`\nCommit Message: $msg"

      if (msg.length > 139)
        msg = msg.substring(0, 139)

      tweeter.updateStatus(msg)
    }
  }
}