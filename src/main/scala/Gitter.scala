package gittweet

import java.io.{File, FileInputStream}
import java.util.zip.InflaterInputStream
import scala.io.Source

class Gitter {
  def getCommitHash(gitPath:String) = {
    val headPath = Source.fromFile(s"$gitPath/HEAD").mkString.split(" ")(1).trim
    Array(headPath, Source.fromFile(s"$gitPath/$headPath").mkString.trim)
  }

  def getCommitMessage(gitPath: String = {System.getProperty("user.dir") + "/.git"}) = {
    val Array(headPath:String, commitHash:String) = getCommitHash(gitPath)
    val commitFile = new File(s"$gitPath/objects/" + commitHash.slice(0, 2) + "/" + commitHash.drop(2))
    val commitCompressed = new InflaterInputStream(new FileInputStream(commitFile))
    List(
      // headPath = refs/heads/<branch-name>
      headPath.replace("refs/heads/",""),
      Source.fromInputStream(commitCompressed)
        .getLines()
        .drop(4)
        .mkString("\n")
        .trim
    )
  }
}