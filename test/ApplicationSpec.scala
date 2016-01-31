//==============================================================================
// FILE:   test/ApplicationSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes all the unit tests for the Nippon48 application.
//==============================================================================

import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._

/**
 * The unit specification class for the [[controllers.Nippon48Controller]]
 * singleton object. It contains unit tests for rendering the index page and
 * detecting a nonexistent HTTP route. In order to execute the unit tests, run
 * the following command in the root directory of this project:
 *
 * {{{
 * sbt "test-only ApplicationSpec"
 * }}}
 */
class ApplicationSpec extends Specification {

  "Nippon48" should {

    "render the index page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get
      status(home) mustEqual OK
      contentType(home) must beSome[String].which(_ mustEqual "text/html")
      contentAsString(home) must contain("<title>Nippon48</title>")
      contentAsString(home) must contain("<h1>Welcome to Nippon48!</h1>")
    }

    "detect a nonexistent HTTP route" in new WithApplication {
      route(FakeRequest(GET, "/foo")) must beNone
    }
  }
}