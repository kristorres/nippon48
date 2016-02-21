//==============================================================================
// FILE:   nippon48-ws/app/models/forms/Nippon48MemberData.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/20
//
// This file includes the `Nippon48MemberData` model case class.
//==============================================================================

package models.forms

import java.util.Date

/**
 * The HTML form data for a Nippon48 member.
 *
 * @param firstName       the given name
 * @param lastName        the family name
 * @param nameInJapanese  an ''optional'' that contains the name in Japanese
 *                        characters, or `None` otherwise
 * @param birthdate       the birthdate
 * @param primaryGroup    the primary group
 * @param secondaryGroup  an ''optional'' that contains the secondary group, or
 *                        `None` otherwise
 * @param primaryTeam     an ''optional'' that contains the primary team, or
 *                        `None` otherwise
 * @param secondaryTeam   an ''optional'' that contains the secondary team, or
 *                        `None` otherwise
 * @param isCaptain       `"Yes"` if the Nippon48 member is a captain, or `"No"`
 *                        otherwise
 * @param generation      the generation number
 */
case class Nippon48MemberData(firstName: String, lastName: String,
  nameInJapanese: Option[String], birthdate: Date, primaryGroup: String,
  secondaryGroup: Option[String], primaryTeam: Option[String],
  secondaryTeam: Option[String], isCaptain: String, generation: Int)