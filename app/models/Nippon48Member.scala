//==============================================================================
// FILE:   app/models/Nippon48Member.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/31
//
// This file includes the `Nippon48Member` model class and its companion object.
//==============================================================================

package models

import com.fasterxml.jackson.annotation._
import com.fasterxml.jackson.annotation.JsonInclude.Include
import java.util.{Calendar, Date}
import org.ektorp.support.CouchDbDocument
import org.joda.time.{LocalDate, Years}
import scala.collection.JavaConverters._

/**
 * A '''Nippon48Member''' is a member of the Japanese idol girl group ''AKB48''
 * (est. 2005 from Akihabara, Tokyo), one of its domestic sister groups, or one
 * of its rival groups. AKB48 consists of five teams: Team A, Team B, Team K,
 * Team 4, and Team 8.<br/><br/>
 *
 * '''Domestic sister groups:'''
 * <ul>
 *   <li>
 *     ''SKE48'' (est. 2008 from Sakae, Nagoya) consists of three teams:
 *     Team S, Team KII, and Team E.
 *   </li>
 *   <li>
 *     ''NMB48'' (est. 2010 from Namba, Osaka) consists of three teams:
 *     Team N, Team M, and Team BII.
 *   </li>
 *   <li>
 *     ''HKT48'' (est. 2011 from Hakata, Fukuoka) consists of two teams:
 *     Team H and Team KIV.
 *   </li>
 *   <li>
 *     ''NGT48'' (est. 2015 from Niigata) consists of one team: Team NIII.
 *   </li>
 * </ul>
 *
 * '''Rival groups:'''
 * <ul>
 *   <li>''Nogizaka46'' (est. 2010 from Tokyo)</li>
 *   <li>''Keyakizaka46'' (est. 2015 from Tokyo)</li>
 * </ul>
 *
 * All idol girl groups have “teams” dedicated for their ''kenkyuusei''
 * (trainees, lit. “research students” in Japanese). There is also a Team
 * Unknown, a temporary team created for Nippon48 members that are promoted or
 * transferred, but not to a specific team. Finally, members may have a
 * concurrent position in two different groups and teams at the same time.
 * <br/><br/>
 *
 * The concept of a captain is used to describe a Nippon48 member that is the
 * “authority” of a certain team. There is also the concept of a co-captain, but
 * for the sake of simplicity, a co-captain is also considered a captain.
 * <br/><br/>
 *
 * AKB48 and its domestic sister groups have their own theater managers.
 * Currently, Rino Sashihara is the only Nippon48 member to have a concurrent
 * position as a theater manager (of HKT48).<br/><br/>
 *
 * The `Nippon48Member` class is intended to behave like a JSON that can be
 * added as a document to a CouchDB database. It defines mappings for the
 * `name_en`, `name_jp`, `birthdate`, `age`, list of `groups`, list of `teams`,
 * captain status (denoted by `isCaptain`), and `generation`. Therefore, the
 * JSON takes on the following format:
 *
 * {{{
 * &#123;
 *   &quot;_id&quot;: &quot;yokoyama-yui&quot;,
 *   &quot;_rev&quot;: …,
 *   &quot;name_en&quot;: &quot;Yui Yokoyama&quot;,
 *   &quot;name_jp&quot;: &quot;横山由依&quot;,
 *   &quot;birthdate&quot;: &quot;1992-12-08&quot;,
 *   &quot;age&quot;: 23,
 *   &quot;groups&quot;: [&quot;AKB48&quot;],
 *   &quot;teams&quot;: [&quot;A&quot;],
 *   &quot;isCaptain&quot;: true,
 *   &quot;generation&quot;: 9
 * &#125;
 * }}}
 */
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(Array("_attachments", "_conflicts", "_revisions"))
class Nippon48Member extends CouchDbDocument {

  //============================= JSON properties ==============================

  /**
   * The “romanized” version of the name of this Nippon48 member. It consists of
   * a given name and a family name (surname).
   */
  @JsonProperty("name_en") private var _name_en: String = _

  /** The name of this Nippon48 member in Japanese characters. */
  @JsonProperty("name_jp") var nameInJapanese: String = _

  /** The birthdate of this Nippon48 member as a string. */
  @JsonProperty("birthdate") private var _birthdate: String = _

  /**
   * Gets the age of this Nippon48 member.
   *
   * @return the age
   */
  @JsonProperty("age")
  def age: Int = {

    val dashIndex1 = _birthdate indexOf '-'
    val dashIndex2 = _birthdate lastIndexOf '-'

    val year = _birthdate.substring(0, dashIndex1).toInt
    val month = _birthdate.substring(dashIndex1 + 1, dashIndex2).toInt
    val day = _birthdate.substring(dashIndex2 + 1).toInt

    val today = new LocalDate
    val birthdate = new LocalDate(year, month, day)
    Years.yearsBetween(birthdate, today).getYears
  }

  /** The list of idol girl groups that this Nippon48 member is currently in. */
  @JsonProperty("groups") var groups = List[String]().asJava

  /** The list of teams that this Nippon48 member is currently in. */
  @JsonProperty("teams") var teams = List[String]().asJava

  /** The captain status of this Nippon48 member. */
  @JsonProperty("isCaptain") var isCaptain = false

  /** The cardinal number of the generation of this Nippon48 member. */
  @JsonProperty("generation") var generation = 1

  //================================= Mutators =================================

  /**
   * Sets the birthdate of this Nippon48 member.
   *
   * @param birthdate  the new birthdate
   */
  @JsonIgnore
  def setBirthdate(birthdate: Date): Unit = {

    val calendar = Calendar.getInstance
    calendar setTime birthdate

    val year = calendar get Calendar.YEAR
    val month = calendar.get(Calendar.MONTH) + 1
    val day = calendar get Calendar.DAY_OF_MONTH

    val monthAsString = if (month < 10) s"0$month" else s"$month"
    val dayAsString = if (day < 10) s"0$day" else s"$day"

    _birthdate = s"$year-$monthAsString-$dayAsString"
  }

  /**
   * Sets the given name and family name (surname) of this Nippon48 member.
   *
   * @param firstName  the new given name (e.g., `"Yui"`)
   * @param lastName   the new family name (e.g., `"Yokoyama"`)
   */
  @JsonIgnore
  def setName(firstName: String, lastName: String): Unit = {
    _name_en = firstName + " " + lastName
  }

  //================================ Accessors =================================

  /**
   * Gets the birthdate of this Nippon48 member as a string in the format
   * `"YYYY-MM-DD"`.
   *
   * @return the birthdate as a string
   */
  @JsonIgnore
  def birthdate: String = _birthdate

  /**
   * Gets the “romanized” version of the name of this Nippon48 member in the
   * format `"&lt;CAPITALIZED GIVEN NAME&gt; &lt;CAPITALIZED FAMILY NAME&gt;"`.
   *
   * @return the name
   */
  @JsonIgnore
  def name: String = _name_en

  /**
   * Gets the JSON of this Nippon48 member as a string.
   *
   * @return the JSON
   */
  @JsonIgnore
  def toJSON: String = {

    s"""|{
        |  "_id": "$getId",
        |  "_rev": "$getRevision",
        |  "name_en": "$name",
        |  "name_jp": "$nameInJapanese",
        |  "birthdate": "$birthdate",
        |  "age": $age,
        |  "groups": $groups,
        |  "teams": $teams,
        |  "isCaptain": $isCaptain,
        |  "generation": $generation
        |}""".stripMargin
  }

  /**
   * Gets the string representation of the JSON of this Nippon48 member.
   *
   * @return the JSON as a string
   */
  @JsonIgnore
  override def toString: String = toJSON

  /**
   * Gets the URL of this Nippon48 member in the Stage48 Wiki.
   *
   * @return the Stage48 Wiki URL
   */
  @JsonIgnore
  def wikiURL: String = {
    val firstName = name.substring(0, name indexOf ' ')
    val lastName = name.substring(name.indexOf(' ') + 1)
    s"http://stage48.net/wiki/index.php/${lastName}_$firstName"
  }
}

/**
 * The companion object of the [[models.Nippon48Member]] class. It contains
 * constants for the minimum age and maximum age requirements for a Nippon48
 * member, as well as lists of all the Japanese idol girl groups and teams that
 * are produced by Yasushi Akimoto. The object also contains an `apply` method
 * for creating a Nippon48 member.
 */
object Nippon48Member {

  /** The minimum age requirement for a Nippon48 member. */
  val minAge = 12

  /** The maximum age requirement for a Nippon48 member. */
  val maxAge = 30

  /** All the Japanese idol girl groups that are produced by Yasushi Akimoto. */
  val validGroups = Seq("AKB48", "SKE48", "NMB48", "HKT48", "NGT48",
    "Nogizaka46", "Keyakizaka46")

  /**
   * All the teams in the Japanese idol girl groups that are produced by Yasushi
   * Akimoto.
   */
  val validTeams = Seq("A", "K", "B", "4", "8", "S", "KII", "E", "N", "M",
    "BII", "H", "KIV", "NIII", "Theater Manager", "Unknown", "Kenkyuusei")

  /**
   * Creates a Nippon48 member with the specified given name, family name
   * (surname), name in Japanese characters, birthdate, idol girl groups, teams,
   * captain status, and generation number.
   *
   * @param firstName       the given name
   * @param lastName        the family name
   * @param nameInJapanese  the name in Japanese characters
   * @param birthdate       the birthdate
   * @param groups          the groups
   * @param teams           the teams
   * @param isCaptain       the captain status
   * @param generation      the generation number
   *
   * @return
   */
  def apply(firstName: String, lastName: String, nameInJapanese: String,
    birthdate: Date, groups: java.util.List[String],
    teams: java.util.List[String], isCaptain: Boolean,
    generation: Int): Nippon48Member = {

    val member = new Nippon48Member
    member setId s"$lastName-$firstName"
    member.setName(firstName, lastName)
    member.nameInJapanese = nameInJapanese
    member setBirthdate birthdate
    member.groups = groups
    member.teams = teams
    member.isCaptain = isCaptain
    member.generation = generation
    member
  }
}