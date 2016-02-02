//==============================================================================
// FILE:   test/CloudantSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/01
//
// This file includes all the unit tests for the `Cloudant` service singleton
// object.
//==============================================================================

import java.util.Calendar
import models.Nippon48Member
import org.specs2.mutable.Specification
import scala.collection.JavaConverters._
import services.Cloudant

/**
 * The unit specification class for the [[services.Cloudant]] singleton object.
 * It contains unit tests for the CRUD operations that are defined in that
 * object. In order to execute the unit tests, run the following command in the
 * root directory of this project:
 *
 * {{{
 * sbt "test-only CloudantSpec"
 * }}}
 */
class CloudantSpec extends Specification {

  "Cloudant" should {

    "add a Nippon48 member" in {

      val calendar = Calendar.getInstance
      calendar.set(Calendar.YEAR, 1991)
      calendar.set(Calendar.MONTH, Calendar.JULY)
      calendar.set(Calendar.DAY_OF_MONTH, 10)

      val birthdate = calendar.getTime
      val groups = List("AKB48").asJava
      val teams = List("A").asJava
      Cloudant add Nippon48Member("Atsuko", "Maeda", "前田敦子", birthdate,
        groups, teams, isCaptain = false, 1)

      Nippon48Member("maeda-atsuko") must beSome[Nippon48Member]
    }

    "fetch a Nippon48 member" in {

      val member = Nippon48Member("maeda-atsuko").get
      val revision = member.getRevision

      val json = s"""|{
                     |  "_id": "maeda-atsuko",
                     |  "_rev": "$revision",
                     |  "name_en": "Atsuko Maeda",
                     |  "name_jp": "前田敦子",
                     |  "birthdate": "1991-07-10",
                     |  "groups": ["AKB48"],
                     |  "teams": ["A"],
                     |  "captain": false,
                     |  "generation": 1
                     |}""".stripMargin

      member.toJSON mustEqual json
    }

    "update a Nippon48 member" in {

      val member = Nippon48Member("maeda-atsuko").get
      val teams = List[String]().asJava
      Cloudant.update("maeda-atsuko", member.groups, teams, member.isCaptain,
        member.generation)

      Nippon48Member("maeda-atsuko") must beSome[Nippon48Member]
        .which(_.teams mustEqual List[String]().asJava)
    }

    "remove a Nippon48 member" in {
      Cloudant remove "maeda-atsuko"
      Nippon48Member("maeda-atsuko") must beNone
    }
  }
}