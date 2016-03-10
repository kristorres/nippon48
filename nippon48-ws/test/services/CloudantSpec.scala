//==============================================================================
// FILE:   nippon48-ws/test/services/CloudantSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/01
//
// This file includes all the unit tests for the `Cloudant` service singleton
// object.
//==============================================================================

package services

import models.Nippon48Member
import models.forms.Nippon48MemberData
import org.specs2.mutable.Specification
import play.api.test.WithApplication
import scala.collection.JavaConverters._

/**
 * The unit specification class for the [[services.Cloudant]] singleton object.
 * It contains unit tests for the CRUD operations that are defined in that
 * object. In order to execute the unit tests, run the following command in the
 * root directory of this project:
 *
 * {{{
 * sbt "project nippon48-ws" "test-only services.CloudantSpec"
 * }}}
 */
class CloudantSpec extends Specification {

  "Cloudant" should {

    "add a Nippon48 member" in new WithApplication {

      val data = Nippon48MemberData("Atsuko", "Maeda", Some("前田敦子"),
        "07/10/1991", "AKB48", None, Some("A"), None, "No")

      Cloudant add Nippon48Member(data)
      Nippon48Member("maeda-atsuko") must beSome[Nippon48Member]
    }

    "fetch a Nippon48 member" in new WithApplication {

      val member = Nippon48Member("maeda-atsuko").get
      val revision = member.getRevision

      val json = s"""|{
                     |  "_id": "maeda-atsuko",
                     |  "_rev": "$revision",
                     |  "name_en": "Atsuko Maeda",
                     |  "name_jp": "前田敦子",
                     |  "birthdate": "07/10/1991",
                     |  "groups": ["AKB48"],
                     |  "teams": ["A"],
                     |  "captain": false
                     |}""".stripMargin

      member.toJSON mustEqual json
      member.fullBirthdate mustEqual "July 10, 1991"
    }

    "update a Nippon48 member" in new WithApplication {

      val member = Nippon48Member("maeda-atsuko").get
      val teams = List[String]().asJava

      Cloudant.update("maeda-atsuko", member.groups, teams, member.isCaptain)
      Nippon48Member("maeda-atsuko") must beSome[Nippon48Member]
        .which(_.teams mustEqual List[String]().asJava)
    }

    "remove a Nippon48 member" in new WithApplication {
      Cloudant remove "maeda-atsuko"
      Nippon48Member("maeda-atsuko") must beNone
    }
  }
}