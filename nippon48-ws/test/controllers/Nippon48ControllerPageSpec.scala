//==============================================================================
// FILE:   nippon48-ws/test/controllers/Nippon48ControllerPageSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes all the page rendering unit tests for the Nippon48
// application.
//==============================================================================

package controllers

import org.specs2.mutable.Specification
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._

/**
 * A unit specification class for the [[controllers.Nippon48Controller]]
 * singleton object. It contains unit tests for rendering the index page,
 * rendering an idol girl group’s page, sending HTTP status 404 on a nonexistent
 * group or Nippon48 member, and detecting a nonexistent HTTP route. In order to
 * execute the unit tests, run the following command in the root directory of
 * this project:
 *
 * {{{
 * sbt "project nippon48-ws" "test-only controllers.Nippon48ControllerPageSpec"
 * }}}
 */
class Nippon48ControllerPageSpec extends Specification {

  "Nippon48" should {

    "render the index page" in new WithApplication {
      val result = route(FakeRequest(GET, "/")).get
      val content = contentAsString(result)
      status(result) mustEqual OK
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Nippon48</title>")
      content must contain("<h1>Welcome to Nippon48!</h1>")
    }

    "render the page related to Nogizaka46" in new WithApplication {
      val result = route(FakeRequest(GET, "/groups/nogizaka46")).get
      val content = contentAsString(result)
      status(result) mustEqual OK
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Nogizaka46 – Nippon48</title>")
      content must contain("<h1>Nogizaka46</h1>")
      content must contain("<h2>What Is Nogizaka46?</h2>")
    }

    "send HTTP status 404 on a nonexistent group" in new WithApplication {
      val result = route(FakeRequest(GET, "/groups/MNL48")).get
      val content = contentAsString(result)
      status(result) mustEqual NOT_FOUND
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Group Not Found – Nippon48</title>")
      content must contain("<h1>Group Not Found</h1>")
      content must contain("“MNL48”")
    }

    "send HTTP status 404 on a nonexistent member" in new WithApplication {
      val result = route(FakeRequest(GET, "/edit-member/eguchi-aimi")).get
      val content = contentAsString(result)
      status(result) mustEqual NOT_FOUND
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Member Not Found – Nippon48</title>")
      content must contain("<h1>Member Not Found</h1>")
      content must contain("The Nippon48 member does not exist")
    }

    "detect a nonexistent HTTP route" in new WithApplication {
      route(FakeRequest(GET, "/foo")) must beNone
    }
  }
}