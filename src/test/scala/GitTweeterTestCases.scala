
import org.scalatest.FlatSpec

class GitTweeterTestCases extends FlatSpec {
  "DataConfig" should "read from Secrets.secrets" in {
    import gittweet.DataConfig

    val confMap = (new DataConfig).getConf("src/test/secrets.txt")
    assert(confMap("consumerKey") ==  "cons-key")
    assert(confMap("consumerSecret") ==  "cons-sec")
    assert(confMap("accessToken") ==  "accTok")
    assert(confMap("accessSecret") ==  "accSec")
  }

}
