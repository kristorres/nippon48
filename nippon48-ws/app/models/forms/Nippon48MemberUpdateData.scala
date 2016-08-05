//===-- nippon48-ws/app/models/forms/Nippon48MemberUpdateDat… -*- Scala -*-===//
//
// This source file is part of the Nippon48 web application project
//
// Created by Kris Torres on Sunday, March 27, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains the `Nippon48MemberUpdateData` case class, which is a
/// '''model''' of the Nippon48 application.
///
//===----------------------------------------------------------------------===//

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
final case class Nippon48MemberUpdateData(primaryGroup: String,
  secondaryGroup: Option[String], primaryTeam: Option[String],
  secondaryTeam: Option[String], isCaptain: String)