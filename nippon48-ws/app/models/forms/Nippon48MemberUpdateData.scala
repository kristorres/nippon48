//==============================================================================
// FILE:   nippon48-ws/app/models/forms/Nippon48MemberUpdateData.scala
// AUTHOR: Kris Torres
// DATE:   2016/03/27
//
// This file includes the `Nippon48MemberUpdateData` model case class.
//==============================================================================

package models.forms

/**
 * The HTML form data for editing the stats of a Nippon48 member.
 *
 * @param primaryGroup    the new primary group
 * @param secondaryGroup  an ''optional'' that contains the new secondary group,
 *                        or `None` otherwise
 * @param primaryTeam     an ''optional'' that contains the new primary team, or
 *                        `None` otherwise
 * @param secondaryTeam   an ''optional'' that contains the new secondary team,
 *                        or `None` otherwise
 * @param isCaptain       `"Yes"` if the Nippon48 member is now a captain, or
 *                        `"No"` otherwise
 */
case class Nippon48MemberUpdateData(primaryGroup: String,
  secondaryGroup: Option[String], primaryTeam: Option[String],
  secondaryTeam: Option[String], isCaptain: String)