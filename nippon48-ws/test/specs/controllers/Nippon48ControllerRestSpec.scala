//===-- nippon48-ws/test/specs/controllers/Nippon48Controlle… -*- Scala -*-===//
//
// This source file is part of the Nippon48 web application project
//
// Created by Kris Torres on Thursday, April 21, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains the `Nippon48ControllerRestSpec` class, which
/// encapsulates all the RESTful API unit tests for the Nippon48 application.
///
//===----------------------------------------------------------------------===//

package specs.controllers

import org.specs2.mutable.Specification
import play.api.libs.json.Json
import play.api.test.{FakeRequest, WithApplication}
import play.api.test.Helpers._

/**
 * A unit specification class for the [[controllers.Nippon48Controller]]
 * singleton object. It contains unit tests for adding, updating, and removing a
 * Nippon48 member. In order to execute the unit tests, run the following
 * command in the root directory of this project:
 *
 * {{{
 * sbt "project nippon48-ws" "test-only specs.controllers.Nippon48ControllerRestSpec"
 * }}}
 */
final class Nippon48ControllerRestSpec extends Specification {

  "Nippon48" should {

    //===------------------- Member addition unit tests -------------------===//

    "fail to add a member (invalid age)" in new WithApplication {

      val json = """|{
                    |  "firstName": "Mariko",
                    |  "lastName": "Tsukamoto",
                    |  "name_jp": "塚本まり子",
                    |  "birthdate": "07/16/1976",
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "4",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Add a New Member – Nippon48</title>")
      content must contain("<h1>Add a New Member</h1>")
      content must contain("Mariko Tsukamoto is currently")
    }

    "fail to add a member (duplicate groups)" in new WithApplication {

      val json = """|{
                    |  "firstName": "Nako",
                    |  "lastName": "Yabuki",
                    |  "name_jp": "矢吹奈子",
                    |  "birthdate": "06/18/2001",
                    |  "primaryGroup": "HKT48",
                    |  "secondaryGroup": "HKT48",
                    |  "primaryTeam": "H",
                    |  "secondaryTeam": "B",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Add a New Member – Nippon48</title>")
      content must contain("<h1>Add a New Member</h1>")
      content must contain("groups cannot be the same")
    }

    "fail to add a member (secondary team only)" in new WithApplication {

      val json = """|{
                    |  "firstName": "Natsuki",
                    |  "lastName": "Kojima",
                    |  "name_jp": "小嶋菜月",
                    |  "birthdate": "03/08/1995",
                    |  "primaryGroup": "AKB48",
                    |  "secondaryTeam": "A",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Add a New Member – Nippon48</title>")
      content must contain("<h1>Add a New Member</h1>")
      content must contain("The secondary team A is selected")
    }

    "fail to add a member (duplicate teams)" in new WithApplication {

      val json = """|{
                    |  "firstName": "Mako",
                    |  "lastName": "Kojima",
                    |  "name_jp": "小嶋真子",
                    |  "birthdate": "05/30/1997",
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "4",
                    |  "secondaryTeam": "4",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Add a New Member – Nippon48</title>")
      content must contain("<h1>Add a New Member</h1>")
      content must contain("teams cannot be the same")
    }

    "fail to add a member (kenkyuusei as captain)" in new WithApplication {

      val json = """|{
                    |  "firstName": "Minami",
                    |  "lastName": "Minegishi",
                    |  "name_jp": "峯岸みなみ",
                    |  "birthdate": "11/15/1992",
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "Kenkyuusei",
                    |  "captainStatus": "Yes"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Add a New Member – Nippon48</title>")
      content must contain("<h1>Add a New Member</h1>")
      content must contain("A kenkyuusei cannot be a captain.")
    }

    "successfully add a member" in new WithApplication {

      val json = """|{
                    |  "firstName": "Aimi",
                    |  "lastName": "Eguchi",
                    |  "name_jp": "江口愛実",
                    |  "birthdate": "02/11/1995",
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "Kenkyuusei",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      status(result) mustEqual SEE_OTHER
    }

    "fail to add a member (already in the database)" in new WithApplication {

      val json = """|{
                    |  "firstName": "Aimi",
                    |  "lastName": "Eguchi",
                    |  "name_jp": "江口愛実",
                    |  "birthdate": "02/11/1995",
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "Kenkyuusei",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val request = FakeRequest(POST, "/member").withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Add a New Member – Nippon48</title>")
      content must contain("<h1>Add a New Member</h1>")
      content must contain("Aimi Eguchi is already in the database.")
    }

    "render the new page related to Aimi Eguchi" in new WithApplication {
      val result = route(FakeRequest(GET, "/edit-member/eguchi-aimi")).get
      val content = contentAsString(result)
      val title = "<title>Edit Stats for Aimi Eguchi – Nippon48</title>"
      val opt1 = """<option value="AKB48" selected>AKB48</option>"""
      val opt2 = """<option value="A" selected>A</option>"""
      val opt3 = """<option value="Kenkyuusei" selected>Kenkyuusei</option>"""
      val opt4 = """<option value="">Please select one</option>"""
      status(result) mustEqual OK
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain(title)
      content must contain("<h1>Edit Stats for Aimi Eguchi</h1>")
      content must contain(opt1)
      content must not contain opt2
      content must contain(opt3)
      content must contain(opt4)
    }

    //===-------------------- Member update unit tests --------------------===//

    "fail to update a member (duplicate groups)" in new WithApplication {

      val json = """|{
                    |  "primaryGroup": "AKB48",
                    |  "secondaryGroup": "AKB48",
                    |  "primaryTeam": "Kenkyuusei",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val urlSuffix = "/member/eguchi-aimi"
      val request = FakeRequest(POST, urlSuffix).withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      val title = "<title>Edit Stats for Aimi Eguchi – Nippon48</title>"
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain(title)
      content must contain("<h1>Edit Stats for Aimi Eguchi</h1>")
      content must contain("groups cannot be the same")
    }

    "fail to update a member (secondary team only)" in new WithApplication {

      val json = """|{
                    |  "primaryGroup": "AKB48",
                    |  "secondaryTeam": "Kenkyuusei",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val urlSuffix = "/member/eguchi-aimi"
      val request = FakeRequest(POST, urlSuffix).withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      val title = "<title>Edit Stats for Aimi Eguchi – Nippon48</title>"
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain(title)
      content must contain("<h1>Edit Stats for Aimi Eguchi</h1>")
      content must contain("The secondary team Kenkyuusei is selected")
    }

    "fail to update a member (duplicate teams)" in new WithApplication {

      val json = """|{
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "Kenkyuusei",
                    |  "secondaryTeam": "Kenkyuusei",
                    |  "captainStatus": "No"
                    |}""".stripMargin

      val urlSuffix = "/member/eguchi-aimi"
      val request = FakeRequest(POST, urlSuffix).withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      val title = "<title>Edit Stats for Aimi Eguchi – Nippon48</title>"
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain(title)
      content must contain("<h1>Edit Stats for Aimi Eguchi</h1>")
      content must contain("teams cannot be the same")
    }

    "fail to update a member (kenkyuusei as captain)" in new WithApplication {

      val json = """|{
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "Kenkyuusei",
                    |  "captainStatus": "Yes"
                    |}""".stripMargin

      val urlSuffix = "/member/eguchi-aimi"
      val request = FakeRequest(POST, urlSuffix).withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      val title = "<title>Edit Stats for Aimi Eguchi – Nippon48</title>"
      status(result) mustEqual BAD_REQUEST
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain(title)
      content must contain("<h1>Edit Stats for Aimi Eguchi</h1>")
      content must contain("A kenkyuusei cannot be a captain.")
    }

    "successfully update a member" in new WithApplication {

      val json = """|{
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "A",
                    |  "captainStatus": "Yes"
                    |}""".stripMargin

      val urlSuffix = "/member/eguchi-aimi"
      val request = FakeRequest(POST, urlSuffix).withJsonBody(Json parse json)
      val result = route(request).get
      status(result) mustEqual SEE_OTHER
    }

    "render the updated page related to Aimi Eguchi" in new WithApplication {
      val result = route(FakeRequest(GET, "/edit-member/eguchi-aimi")).get
      val content = contentAsString(result)
      val title = "<title>Edit Stats for Aimi Eguchi – Nippon48</title>"
      val opt1 = """<option value="AKB48" selected>AKB48</option>"""
      val opt2 = """<option value="A" selected>A</option>"""
      val opt3 = """<option value="Kenkyuusei" selected>Kenkyuusei</option>"""
      val opt4 = """<option value="">Please select one</option>"""
      status(result) mustEqual OK
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain(title)
      content must contain("<h1>Edit Stats for Aimi Eguchi</h1>")
      content must contain(opt1)
      content must contain(opt2)
      content must not contain opt3
      content must contain(opt4)
    }

    //===------------------- Member removal unit tests --------------------===//

    "successfully remove a member" in new WithApplication {
      val result = route(FakeRequest(DELETE, "/members/eguchi-aimi")).get
      status(result) mustEqual OK
    }

    "fail to update a member (not in the database)" in new WithApplication {

      val json = """|{
                    |  "primaryGroup": "AKB48",
                    |  "primaryTeam": "A",
                    |  "captainStatus": "Yes"
                    |}""".stripMargin

      val urlSuffix = "/member/eguchi-aimi"
      val request = FakeRequest(POST, urlSuffix).withJsonBody(Json parse json)
      val result = route(request).get
      val content = contentAsString(result)
      status(result) mustEqual NOT_FOUND
      contentType(result) must beSome[String].which(_ mustEqual "text/html")
      content must contain("<title>Member Not Found – Nippon48</title>")
      content must contain("<h1>Member Not Found</h1>")
      content must contain("The Nippon48 member has recently been removed")
    }
  }
}