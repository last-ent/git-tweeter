package gittweet

import java.io.{File, FileInputStream}
import java.util.zip.InflaterInputStream

import gittweet.SafeIO.readSafely

import scala.io.Source

/**
 * Has two main methods:
 * => getCommitMessage - The main method. Returns a Vector of head, commit message & commit hash.
 * => getCommitHash - A helper method. Returns a Vector of complete head & commit hash.
 */
class Gitter {
  /**
   * Main Method.
   * @param gitPath - Path to git repo with suffix "/.git" otherwise method will fail to retrieve data.
   * @return - For a given directory, it will return a Vector[String] of head, commit message & commit hash.
   */
  def getCommitMessage(gitPath: String = System.getProperty("user.dir") + "/.git"): Vector[String] = {
    // TODO: Add logic so that when gitPath doesn't exist, rest of the code isn't executed.
    val Vector(headPath: String, commitHash: String) = getCommitHash(gitPath)
    if (headPath == "") {
      return Vector("", "")
    }

    val commitFilePath = s"$gitPath/objects/" + commitHash.slice(0, 2) + "/" + commitHash.drop(2)

    val commitFile = new File(commitFilePath)
    val compressedCommit = readSafely(commitFile) {
      new FileInputStream(_)
    } { source =>
      Source.fromInputStream(new InflaterInputStream(source)).getLines.toVector
    }

    Vector(
      // headPath = refs/heads/<branch-name>
      headPath.replace("refs/heads/", ""),
      // 5th line onwards is the commit message
      compressedCommit.drop(4).mkString("\n").trim,
      commitHash
    )
  }

  /**
   * Helper Method.
   * @param gitPath - Path to git repo with suffix "/.git" otherwise method will fail to retrieve data.
   * @return - Vector[String] of complete head & commit hash.
   */
  def getCommitHash(gitPath: String) = {
    val gitHead = new File(s"$gitPath/HEAD")
    val headPath = readSafely(gitHead) {
      Source.fromFile
    } { line =>
      Vector(line.mkString.split(" ")(1).trim)
    }.headOption.mkString

    val commitHashFile = new File(s"$gitPath/$headPath")
    val commitHash = readSafely(commitHashFile) {
      Source.fromFile
    } { line =>
      Vector(line.mkString.trim)
    }.headOption.mkString

    Vector(headPath, commitHash)
  }
}
