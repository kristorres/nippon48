//==============================================================================
// FILE:   nippon48-ws/test/controllers/Nippon48ControllerSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes all the unit tests for the Nippon48 application.
//==============================================================================

package controllers

import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._

/**
 * The unit specification class for the [[controllers.Nippon48Controller]]
 * singleton object. It contains unit tests for rendering the index page,
 * rendering an idol girl group’s page, sending HTTP status 404 on a nonexistent
 * group, and detecting a nonexistent HTTP route. In order to execute the unit
 * tests, run the following command in the root directory of this project:
 *
 * {{{
 * sbt "project nippon48-ws" "test-only controllers.Nippon48ControllerSpec"
 * }}}
 */
class Nippon48ControllerSpec extends Specification {

  "Nippon48" should {

    "render the index page" in new WithApplication {
      val home = route(FakeRequest(GET, "/")).get
      status(home) mustEqual OK
      contentType(home) must beSome[String].which(_ mustEqual "text/html")
      contentAsString(home) must contain("<title>Nippon48</title>")
      contentAsString(home) must contain("<h1>Welcome to Nippon48!</h1>")
    }

    "render the page related to Nogizaka46" in new WithApplication {
      val group = route(FakeRequest(GET, "/groups/nogizaka46")).get
      status(group) mustEqual OK
      contentType(group) must beSome[String].which(_ mustEqual "text/html")
      contentAsString(group) must contain("<title>Nogizaka46 – Nippon48</title>")
      contentAsString(group) must contain("<h1>Nogizaka46</h1>")
      contentAsString(group) must contain("<h2>What Is Nogizaka46?</h2>")
    }

    "send HTTP status 404 on a nonexistent group" in new WithApplication {
      val error = route(FakeRequest(GET, "/groups/MNL48")).get
      status(error) mustEqual NOT_FOUND
      contentType(error) must beSome[String].which(_ mustEqual "text/html")
      contentAsString(error) must contain("<title>Group Not Found – Nippon48</title>")
      contentAsString(error) must contain("<h1>Group Not Found</h1>")
      contentAsString(error) must contain("“MNL48”")
    }

    "detect a nonexistent HTTP route" in new WithApplication {
      route(FakeRequest(GET, "/foo")) must beNone
    }
  }
}