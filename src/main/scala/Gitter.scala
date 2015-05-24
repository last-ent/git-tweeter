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
      List(line.mkString.split(" ")(1).trim)
    }(0)
    val commitHash = readSafely(s"$gitPath/$headPath") {
      Source.fromFile
    } { line =>
      List(line.mkString.trim)
    }(0)

    Array(headPath, commitHash)
  }

  def getCommitMessage(gitPath: String = {
    System.getProperty("user.dir") + "/.git"
  }) = {
    val Array(headPath: String, commitHash: String) = getCommitHash(gitPath)
    val commitFilePath = s"$gitPath/objects/" + commitHash.slice(0, 2) + "/" + commitHash.drop(2)

    val compressedCommit = readSafely(commitFilePath) {
      new FileInputStream(_)
    } { source =>
      Source.fromInputStream(new InflaterInputStream(source)).getLines().toList
    }
    List(
      // headPath = refs/heads/<branch-name>
      headPath.replace("refs/heads/", ""),
      // 5th line onwards is the commit message
      compressedCommit.drop(4).mkString("\n").trim
    )
  }
}
