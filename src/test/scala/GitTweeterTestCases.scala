import org.junit.Assert._
import org.scalatest.FlatSpec
import org.junit.{Test, Before}
import gittweet.{DataConfig, Gitter}
import org.scalatest.junit.JUnitSuite


class GitTweeterBehaviourTests extends FlatSpec {
  "DataConfig" should "read from Secrets.secrets" in {
    val confMap = (new DataConfig).getConf("src/test/secrets.txt")
    assertEquals(confMap("consumerKey"), "cons-key")
    assertEquals(confMap("consumerSecret"), "cons-sec")
    assertEquals(confMap("accessToken"), "accTok")
    assertEquals(confMap("accessSecret"), "accSec")
  }

  "Gitter's getCommitHash" should "return correct hash and branch" in {
    val hash = "PCLOADLETTER!!!"
    val path: String = System.getProperty("user.dir") + "/src/test/git-repo-mock/.gitmock"
    val List(head:String, commitHash: String) = new Gitter().getCommitHash(path).toList
    assertEquals(hash, commitHash)
    assertEquals(head, "refs/heads/hash/slinging/slasher")
  }

  "Gitter's getCommitMessage" should "return correct head" in {
    val path: String = System.getProperty("user.dir") + "/src/test/git-repo-mock/.gitmock"
    val List(head:String, _) = new Gitter().getCommitMessage(path)
    assertEquals(head, "hash/slinging/slasher")
  }
  ignore should "return correct message" in {
    val path: String = System.getProperty("user.dir") + "/src/test/git-repo-mock/.gitmock"
    val List(_, commitHash: String) = new Gitter().getCommitMessage(path)
        assertEquals("asfs", commitHash)
  }

}

class GitTweeterUnitTests extends JUnitSuite {
  @Test def verifyWorks(): Unit = {
    assertEquals(1, 1)
  }

}