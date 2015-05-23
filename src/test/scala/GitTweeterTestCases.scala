import org.scalatest.FlatSpec
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.Test
import org.junit.Before


class GitTweeterBehaviourTests extends FlatSpec {
  "DataConfig" should "read from Secrets.secrets" in {
    import gittweet.DataConfig

    val confMap = (new DataConfig).getConf("src/test/secrets.txt")
    assert(confMap("consumerKey") == "cons-key")
    assert(confMap("consumerSecret") == "cons-sec")
    assert(confMap("accessToken") == "accTok")
    assert(confMap("accessSecret") == "accSec")
  }
}

class GitTweeterUnitTests extends JUnitSuite {
  @Test def verifyWorks(): Unit = {
    assertEquals(1, 1)
  }

}