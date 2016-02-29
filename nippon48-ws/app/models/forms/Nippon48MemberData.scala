//==============================================================================
// FILE:   nippon48-ws/app/models/forms/Nippon48MemberData.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/20
//
// This file includes the `Nippon48MemberData` model case class and its
// companion object.
//==============================================================================

package models.forms

/**
 * The HTML form data for a Nippon48 member.
 *
 * @param firstName       the given name
 * @param lastName        the family name
 * @param nameInJapanese  an ''optional'' that contains the name in Japanese
 *                        characters, or `None` otherwise
 * @param birthdate       the birthdate in the format MM/DD/YYYY
 * @param primaryGroup    the primary group
 * @param secondaryGroup  an ''optional'' that contains the secondary group, or
 *                        `None` otherwise
 * @param primaryTeam     an ''optional'' that contains the primary team, or
 *                        `None` otherwise
 * @param secondaryTeam   an ''optional'' that contains the secondary team, or
 *                        `None` otherwise
 * @param isCaptain       `"Yes"` if the Nippon48 member is a captain, or `"No"`
 *                        otherwise
 */
case class Nippon48MemberData(firstName: String, lastName: String,
  nameInJapanese: Option[String], birthdate: String, primaryGroup: String,
  secondaryGroup: Option[String], primaryTeam: Option[String],
  secondaryTeam: Option[String], isCaptain: String)

/**
 * The companion object of the [[models.forms.Nippon48MemberData]] case class.
 * It contains keys for the elements in the form that is used to add a Nippon48
 * member to the database.
 */
object Nippon48MemberData {
  final val BIRTHDATE_KEY = "birthdate"
  final val CAPTAIN_STATUS_KEY = "captainStatus"
  final val FIRST_NAME_KEY = "firstName"
  final val LAST_NAME_KEY = "lastName"
  final val NAME_JP_KEY = "name_jp"
  final val PRIMARY_GROUP_KEY = "primaryGroup"
  final val PRIMARY_TEAM_KEY = "primaryTeam"
  final val SECONDARY_GROUP_KEY = "secondaryGroup"
  final val SECONDARY_TEAM_KEY = "secondaryTeam"
}