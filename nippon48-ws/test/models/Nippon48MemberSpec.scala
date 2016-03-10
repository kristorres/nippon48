//==============================================================================
// FILE:   nippon48-ws/test/models/Nippon48MemberSpec.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/01
//
// This file includes all the unit tests for the `Nippon48Member` model class.
//==============================================================================

package models

import models.forms.Nippon48MemberData
import org.specs2.mutable.Specification

/**
 * The unit specification class for the [[models.Nippon48Member]] model class.
 * It contains unit tests for creating various Nippon48 members. In order to
 * execute the unit tests, run the following command in the root directory of
 * this project:
 *
 * {{{
 * sbt "project nippon48-ws" "test-only models.Nippon48MemberSpec"
 * }}}
 */
class Nippon48MemberSpec extends Specification {

  "Nippon48" should {

    "create an AKB48 member named Yui Yokoyama" in {

      val data = Nippon48MemberData("Yui", "Yokoyama", Some("横山由依"),
        "12/08/1992", "AKB48", None, Some("A"), None, "Yes")
      val member = Nippon48Member(data)
      member setRevision "..."

      val json = """|{
                    |  "_id": "yokoyama-yui",
                    |  "_rev": "...",
                    |  "name_en": "Yui Yokoyama",
                    |  "name_jp": "横山由依",
                    |  "birthdate": "12/08/1992",
                    |  "groups": ["AKB48"],
                    |  "teams": ["A"],
                    |  "captain": true
                    |}""".stripMargin

      member.toJSON mustEqual json
      member.fullBirthdate mustEqual "December 8, 1992"
    }

    "create an NMB48/AKB48 member named Sayaka Yamamoto" in {

      val data = Nippon48MemberData("Sayaka", "Yamamoto", Some("山本彩"),
        "07/14/1993", "NMB48", Some("AKB48"), Some("N"), Some("K"), "Yes")
      val member = Nippon48Member(data)
      member setRevision "..."

      val json = """|{
                    |  "_id": "yamamoto-sayaka",
                    |  "_rev": "...",
                    |  "name_en": "Sayaka Yamamoto",
                    |  "name_jp": "山本彩",
                    |  "birthdate": "07/14/1993",
                    |  "groups": ["NMB48", "AKB48"],
                    |  "teams": ["N", "K"],
                    |  "captain": true
                    |}""".stripMargin

      member.toJSON mustEqual json
      member.fullBirthdate mustEqual "July 14, 1993"
    }

    "create a Nogizaka46 member named Rina Ikoma" in {

      val data = Nippon48MemberData("Rina", "Ikoma", Some("生駒里奈"),
        "12/29/1995", "Nogizaka46", None, None, None, "No")
      val member = Nippon48Member(data)
      member setRevision "..."

      val json = """|{
                    |  "_id": "ikoma-rina",
                    |  "_rev": "...",
                    |  "name_en": "Rina Ikoma",
                    |  "name_jp": "生駒里奈",
                    |  "birthdate": "12/29/1995",
                    |  "groups": ["Nogizaka46"],
                    |  "teams": [],
                    |  "captain": false
                    |}""".stripMargin

      member.toJSON mustEqual json
      member.fullBirthdate mustEqual "December 29, 1995"
    }
  }
}