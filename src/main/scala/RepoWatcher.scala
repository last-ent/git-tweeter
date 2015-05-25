package gittweet

import java.nio.file.{Path, WatchEvent, FileSystems}
import java.nio.file.StandardWatchEventKinds._
import util.control.Breaks._
import collection.JavaConversions._

import scala.util.control.NonFatal

/**
 * RepoSnooper should be able to
 *  - Register new & existing set of Repos -> Repo Watcher
 *  - Watch for changes in the Repos -> Repo Watcher
 *  - Fire a message to tweet when a new commit is made -> Commit Tweeter
 *  - Repo Watcher & Commit Tweeter are two Actors
 *
 * Repo Watcher =>
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
class RepoWatcher {
  def repo
  /**
   * watchDir is a base template based on which we need to develop our Actor Model for Repo Watcher
   */
  def watchDir(path: String = System.getProperty("user.dir")) = {
    val watcher = FileSystems.getDefault.newWatchService
    val dir = FileSystems.getDefault.getPath(path)

    try {
      val key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY)
    } catch {
      case NonFatal(ex) => {
        println(s"Failed to register: $ex")
      }
    }
    breakable {
      while (true) {
        val watchKey = watcher.take()

        watchKey.pollEvents() foreach { event =>
          val relativePath = event.context.asInstanceOf[Path]
          val path = watchKey.watchable.asInstanceOf[Path].resolve(relativePath)
          event.kind match {
            case ENTRY_CREATE => println(s"CREATED: $path")
            case ENTRY_MODIFY => {
              println(s"MODIFIED: $path")
              println("File: "+ event.context)
            }
            case x => println(s"OTHERWISE: $x")
          }
        }
        watchKey.reset
      }
    }
  }
}
