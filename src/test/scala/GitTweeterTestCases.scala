import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.Test
import org.junit.Before
import gittweet.DataConfig
import gittweet.Gitter


class GitTweeterBehaviourTests extends FlatSpec {
  "DataConfig" should "read from Secrets.secrets" in {

    val confMap = (new DataConfig).getConf("src/test/secrets.txt")
    assert(confMap("consumerKey") == "cons-key")
    assert(confMap("consumerSecret") == "cons-sec")
    assert(confMap("accessToken") == "accTok")
    assert(confMap("accessSecret") == "accSec")
  }
  "Gitter's getCommitHash" should "return correct hash and branch" in {

    val hash = "PCLOADLETTER!!!"
    val path:String = System.getProperty("user.dir")+"/src/test/git-repo-mock/.gitmock"
    val List(head:String, commitHash:String) = new Gitter().getCommitHash(path).toList

    assertEquals(hash, commitHash)
    assertEquals(head, "hash/slinging/slasher")
  }
//  "Gitter's getCommitMessage" should "return correct message & head" in {
//    val path:String = System.getProperty("user.dir")+"/src/test/git-repo-mock/.gitmock"
//    try {
//    val List(head, commitMessage) = new Gitter().getCommitMessage(path)
//    assertEquals(head, "hash/slinging/slasher")
//    }
//  }
}

class GitTweeterUnitTests extends JUnitSuite {
  @Test def verifyWorks(): Unit = {
    assertEquals(1, 1)
  }

}