package gittweet

import java.io.{File, FileInputStream}
import java.util.zip.InflaterInputStream
import scala.io.Source

class Gitter {
  def getCommitMessage(path: String = "/Users/Ent/Documents/pynik/ScalaBase/git-tweeter") = {
    val gitPath = path +"/.git"
    val headPath = Source.fromFile(s"$gitPath/HEAD").mkString.split(" ")(1).trim
    val commitHash = Source.fromFile(s"$gitPath/$headPath").mkString.trim
    val commitFile = new File(s"$gitPath/objects/" + commitHash.slice(0,2)+"/"+commitHash.drop(2))
    val commitCompressed = new InflaterInputStream(new FileInputStream(commitFile))

    List(
      // headPath = refs/heads/<branch-name>
      headPath.split("/").drop(2).mkString("/"),
      Source.fromInputStream(commitCompressed)
        .getLines()
        .drop(4)
        .mkString("\n")
        .trim
    )
  }
}