import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification{

  "Application" should {

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      home.map(s => println(s.body))
      contentAsString(home) must contain ("Original URL")
    }

    "create and browse" in new WithApplication {

      val originalUrl = "https://www.apple.com/ipad-pro/specs/"

      val result = route(FakeRequest(POST, "/tinyUrl").
        withHeaders("Content-Type" -> "application/x-www-form-urlencoded").
        withFormUrlEncodedBody(("originalUrl", originalUrl))).get

      status(result) must equalTo(CREATED)
      contentAsString(result) must contain("http://localhost:9000/1")

      val tinyUrl = "http://localhost:9000/1"

      val result2= route(
        FakeRequest(POST, "/redirect").
          withHeaders("Content-Type" -> "application/x-www-form-urlencoded").
          withFormUrlEncodedBody(("tinyUrl", tinyUrl))).get
      status(result2) must equalTo(SEE_OTHER)

    }


    "throw NOT_FOUND" in new WithApplication {

      val tinyUrl = "http://localhost:9000/2"

      val result= route(
        FakeRequest(POST, "/redirect").
          withHeaders("Content-Type" -> "application/x-www-form-urlencoded").
          withFormUrlEncodedBody(("tinyUrl", tinyUrl))).get
      status(result) must equalTo(NOT_FOUND)

    }

    "throw BAD_REQUEST" in new WithApplication {

      val originalUrl = " "

      val result = route(FakeRequest(POST, "/tinyUrl").
        withHeaders("Content-Type" -> "application/x-www-form-urlencoded").
        withFormUrlEncodedBody(("originalUrl", originalUrl))).get

      status(result) must equalTo(BAD_REQUEST)

    }
  }
}
