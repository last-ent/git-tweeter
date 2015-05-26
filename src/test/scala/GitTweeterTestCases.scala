import gittweet.{DataConfig, Gitter}
import org.junit.Assert._
import org.junit.Test
import org.scalatest.FlatSpec
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
    val Vector(head: String, commitHash: String) = (new Gitter).getCommitHash(path)
    assertEquals(hash, commitHash)
    assertEquals(head, "refs/heads/hash/slinging/slasher")
  }


  "Gitter's getCommitMessage" should "return correct head" in {
    val path: String = System.getProperty("user.dir") + "/src/test/git-repo-mock/.gitmock"
    val Vector(head: String, _) = (new Gitter).getCommitMessage(path)
    assertEquals(head, "hash/slinging/slasher")
  }

  ignore should "return correct message" in {
    val path: String = System.getProperty("user.dir") + "/src/test/git-repo-mock/.gitmock"
    val Vector(_, commitHash: String) = (new Gitter).getCommitMessage(path)
    assertEquals("asfs", commitHash)
  }


  "SafeIO's readSafely" should "return Empty List for wrong path" in {
    val path: String = "invalid/path"
    val Vector(head: String, commitHash: String) = (new Gitter).getCommitMessage(path)
    assertEquals("", commitHash)
    assertEquals("", head)
  }

}

class GitTweeterUnitTests extends JUnitSuite {
  @Test def verifyWorks(): Unit = {
    assertEquals(1, 1)
  }
}