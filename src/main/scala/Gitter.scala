package gittweet

import scala.io.Source
import SafeIO.readSafely
import java.io.FileInputStream
import java.util.zip.InflaterInputStream

class Gitter {
  def getCommitHash(gitPath: String) = {
    val headPath = readSafely(s"$gitPath/HEAD") {
      Source.fromFile
    } { line =>
      Vector(line.mkString.split(" ")(1).trim)
    }.headOption.mkString

    val commitHash = readSafely(s"$gitPath/$headPath") {
      Source.fromFile
    } { line =>
      Vector(line.mkString.trim)
    }.headOption.mkString

    Vector(headPath, commitHash)
  }

  def getCommitMessage(gitPath: String = System.getProperty("user.dir") + "/.git"): Vector[String] = {
    // TODO: Add logic so that when gitPath doesn't exist, rest of the code isn't executed.
    val Vector(headPath: String, commitHash: String) = getCommitHash(gitPath)
    if (headPath == "") {
      return Vector("", "")
    }

    val commitFilePath = s"$gitPath/objects/" + commitHash.slice(0, 2) + "/" + commitHash.drop(2)

    val compressedCommit = readSafely(commitFilePath) {
      new FileInputStream(_)
    } { source =>
      Source.fromInputStream(new InflaterInputStream(source)).getLines.toVector
    }
    Vector(
      // headPath = refs/heads/<branch-name>
      headPath.replace("refs/heads/", ""),
      // 5th line onwards is the commit message
      compressedCommit.drop(4).mkString("\n").trim
    )
  }
}
