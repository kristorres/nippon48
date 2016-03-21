//==============================================================================
// FILE:   nippon48-ws/app/controllers/Nippon48Controller.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes the `Nippon48Controller` singleton object.
//==============================================================================

package controllers

import java.text.SimpleDateFormat
import models.Nippon48Member
import models.forms.Nippon48MemberData
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import scala.collection.JavaConverters._
import scala.util.Try
import services.Cloudant

/**
 * '''Nippon48''' is an interactive web application that lets the user
 * add/delete Nippon48 members in the database, and also update their stats.
 */
object Nippon48Controller extends Controller {

  //============================= Private mapping ==============================

  /** The mapping for a valid date in the format MM/DD/YYYY. */
  private val validDate = text.verifying { date =>
    Try {
      val dateFormatter = new SimpleDateFormat("MM/dd/yyyy")
      dateFormatter setLenient false
      dateFormatter parse date
    }.isSuccess
  }

  //=============================== Private form ===============================

  /** The form used to add a Nippon48 member to the database. */
  private val form = Form {
    mapping(
      Nippon48MemberData.FIRST_NAME_KEY -> nonEmptyText,
      Nippon48MemberData.LAST_NAME_KEY -> nonEmptyText,
      Nippon48MemberData.NAME_JP_KEY -> optional(nonEmptyText),
      Nippon48MemberData.BIRTHDATE_KEY -> validDate,
      Nippon48MemberData.PRIMARY_GROUP_KEY -> nonEmptyText,
      Nippon48MemberData.SECONDARY_GROUP_KEY -> optional(nonEmptyText),
      Nippon48MemberData.PRIMARY_TEAM_KEY -> optional(nonEmptyText),
      Nippon48MemberData.SECONDARY_TEAM_KEY -> optional(nonEmptyText),
      Nippon48MemberData.CAPTAIN_STATUS_KEY -> text
    )(Nippon48MemberData.apply)(Nippon48MemberData.unapply)
  }

  //================================== Pages ===================================

  /**
   * Renders the index page of this application.
   *
   * @return the GET action
   */
  def index = Action { Ok(views.html.index("Welcome to Nippon48!")) }

  /**
   * Renders the page of this application where the user can add a Nippon48
   * member to the database.
   *
   * @return the GET action
   */
  def newMember = Action { Ok(views.html.member(form, None)) }

  //========================== RESTful API endpoints ===========================

  /**
   * Adds a Nippon48 member to the database, then reloads the index page of this
   * application.
   *
   * @return the POST action
   */
  def addMember() = Action { implicit request =>

    // User input
    val input = form.bindFromRequest

    input.fold(
      formWithErrors => BadRequest(views.html.member(formWithErrors, None)),
      value => {

        val minAge = Nippon48Member.MIN_AGE
        val maxAge = Nippon48Member.MAX_AGE
        val group1 = value.primaryGroup
        val group2 = value.secondaryGroup
        val team1 = value.primaryTeam
        val team2 = value.secondaryTeam
        val member = Nippon48Member(value)
        val age = member.age

        if (age < minAge || age > maxAge) {
          val error = "Nippon48 members must currently be between " +
            s"$minAge and $maxAge, inclusively. " +
            s"(${member.name} is currently $age.)"
          BadRequest(views.html.member(input, Some(error)))
        } else if (group2.isDefined && group1 == group2.get) {
          val error = "The primary and secondary idol girl groups " +
            "cannot be the same."
          BadRequest(views.html.member(input, Some(error)))
        } else if (team2.isDefined && team1.isEmpty) {
          val error = s"The secondary team ${value.secondaryTeam.get} " +
            "is selected, but the primary team is not selected."
          BadRequest(views.html.member(input, Some(error)))
        } else if (team2.isDefined && team1.get == team2.get) {
          val error = s"The primary and secondary teams cannot be the same."
          BadRequest(views.html.member(input, Some(error)))
        } else if (Nippon48Member(member.getId).isDefined) {
          val error = s"${member.name} is already in the database."
          BadRequest(views.html.member(input, Some(error)))
        } else {
          Cloudant add member
          Redirect(routes.Nippon48Controller.index)
        }
      }
    )
  }

  /**
   * Renders the page of this application that lists all the Nippon48 members of
   * the specified idol girl group and their stats in a table. '''Note that the
   * supplied argument in this method is __case-sensitive__.'''
   *
   * @param name  the group (`"all"` to list ''all'' the members in the
   *              database)
   *
   * @return the GET action
   */
  def getGroup(name: String) = Action {
    if (name equalsIgnoreCase "all")
      Redirect(routes.Nippon48Controller.index)
    else {
      Nippon48Member.validGroups.find(_ equalsIgnoreCase name) match {
        case Some(group) => Ok(views.html.group(group))
        case None => NotFound(views.html.error(name))
      }
    }
  }

  /**
   * Lists all the Nippon48 members in the database in JSON format.
   *
   * @return the GET action
   */
  def getMembers = Action {
    Ok(Json.toJson(Cloudant getMembers None)(Nippon48MemberWriterList))
  }

  /**
   * Removes the Nippon48 member associated with the specified ID from the
   * database.
   *
   * @param id  the ID of the Nippon48 member to be removed
   *
   * @return the DELETE action
   */
  def removeMember(id: String) = Action { Cloudant remove id; Ok }

  //=========================== Private JSON writers ===========================

  /** The inner singleton object that writes a Nippon48 member as a JSON. */
  private object Nippon48MemberWriter extends Writes[Nippon48Member] {

    override def writes(member: Nippon48Member) = {

      val groups = member.groups.asScala.map(Json toJson _)
      val teams = member.teams.asScala.map(Json toJson _)

      JsObject(Seq("name_en" -> JsString(member.name),
                   "name_jp" -> JsString(member.nameInJapanese),
                   "birthdate" -> JsString(member.birthdate),
                   "groups" -> JsArray(groups),
                   "teams" -> JsArray(teams),
                   "isCaptain" -> JsBoolean(member.isCaptain)))
    }
  }

  /**
   * The inner singleton object that writes a list of Nippon48 members as a
   * JSON.
   */
  private object Nippon48MemberWriterList extends Writes[List[Nippon48Member]] {

    override def writes(members: List[Nippon48Member]) = {
      JsArray(members.map(Json.toJson(_)(Nippon48MemberWriter)))
    }
  }
}