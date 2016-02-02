//==============================================================================
// FILE:   test/MemberSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/01
//
// This file includes all the unit tests for the `Nippon48Member` model class.
//==============================================================================

import java.util.Calendar
import models.Nippon48Member
import org.specs2.mutable.Specification
import scala.collection.JavaConverters._

/**
 * The unit specification class for the [[models.Nippon48Member]] model class.
 * It contains unit tests for creating various Nippon48 members. In order to
 * execute the unit tests, run the following command in the root directory of
 * this project:
 *
 * {{{
 * sbt "test-only MemberSpec"
 * }}}
 */
class MemberSpec extends Specification {

  "Nippon48" should {

    "create an AKB48 member named Yui Yokoyama" in {

      val calendar = Calendar.getInstance
      calendar.set(Calendar.YEAR, 1992)
      calendar.set(Calendar.MONTH, Calendar.DECEMBER)
      calendar.set(Calendar.DAY_OF_MONTH, 8)

      val birthdate = calendar.getTime
      val groups = List("AKB48").asJava
      val teams = List("A").asJava
      val member = Nippon48Member("Yui", "Yokoyama", "横山由依", birthdate,
        groups, teams, isCaptain = true, 9)
      member setRevision "..."

      val json = """|{
                    |  "_id": "yokoyama-yui",
                    |  "_rev": "...",
                    |  "name_en": "Yui Yokoyama",
                    |  "name_jp": "横山由依",
                    |  "birthdate": "1992-12-08",
                    |  "groups": ["AKB48"],
                    |  "teams": ["A"],
                    |  "captain": true,
                    |  "generation": 9
                    |}""".stripMargin

      member.toJSON mustEqual json
    }

    "create an NMB48/AKB48 member named Sayaka Yamamoto" in {

      val calendar = Calendar.getInstance
      calendar.set(Calendar.YEAR, 1993)
      calendar.set(Calendar.MONTH, Calendar.JULY)
      calendar.set(Calendar.DAY_OF_MONTH, 14)

      val birthdate = calendar.getTime
      val groups = List("NMB48", "AKB48").asJava
      val teams = List("N", "K").asJava
      val member = Nippon48Member("Sayaka", "Yamamoto", "山本彩", birthdate,
        groups, teams, isCaptain = true, 1)
      member setRevision "..."

      val json = """|{
                    |  "_id": "yamamoto-sayaka",
                    |  "_rev": "...",
                    |  "name_en": "Sayaka Yamamoto",
                    |  "name_jp": "山本彩",
                    |  "birthdate": "1993-07-14",
                    |  "groups": ["NMB48", "AKB48"],
                    |  "teams": ["N", "K"],
                    |  "captain": true,
                    |  "generation": 1
                    |}""".stripMargin

      member.toJSON mustEqual json
    }

    "create a Nogizaka46 member named Rina Ikoma" in {

      val calendar = Calendar.getInstance
      calendar.set(Calendar.YEAR, 1995)
      calendar.set(Calendar.MONTH, Calendar.DECEMBER)
      calendar.set(Calendar.DAY_OF_MONTH, 29)

      val birthdate = calendar.getTime
      val groups = List("Nogizaka46").asJava
      val teams = List[String]().asJava
      val member = Nippon48Member("Rina", "Ikoma", "生駒里奈", birthdate, groups,
        teams, isCaptain = false, 1)
      member setRevision "..."

      val json = """|{
                    |  "_id": "ikoma-rina",
                    |  "_rev": "...",
                    |  "name_en": "Rina Ikoma",
                    |  "name_jp": "生駒里奈",
                    |  "birthdate": "1995-12-29",
                    |  "groups": ["Nogizaka46"],
                    |  "teams": [],
                    |  "captain": false,
                    |  "generation": 1
                    |}""".stripMargin

      member.toJSON mustEqual json
    }
  }
}