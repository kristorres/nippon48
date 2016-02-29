//==============================================================================
// FILE:   nippon48-ws/app/controllers/Nippon48Controller.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes the `Nippon48Controller` singleton object.
//==============================================================================

package controllers

import models.Nippon48Member
import models.forms.Nippon48MemberData
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import scala.collection.JavaConverters._
import services.Cloudant

/**
 * '''Nippon48''' is an interactive web application that lets the user
 * add/delete Nippon48 members in the database, and also update their stats.
 */
object Nippon48Controller extends Controller {

  //=============================== Private form ===============================

  /** The form used to add a Nippon48 member to the database. */
  private val form = Form {
    mapping(
      Nippon48MemberData.FIRST_NAME_KEY -> nonEmptyText,
      Nippon48MemberData.LAST_NAME_KEY -> nonEmptyText,
      Nippon48MemberData.NAME_JP_KEY -> optional(nonEmptyText),
      Nippon48MemberData.BIRTHDATE_KEY -> nonEmptyText,
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
  def newMember = Action { Ok(views.html.member(form)) }

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
      formWithErrors => BadRequest(views.html.member(formWithErrors)),
      value => {

        val badRequest = BadRequest(views.html.member(input))
        val minAge = Nippon48Member.MIN_AGE
        val maxAge = Nippon48Member.MAX_AGE
        val member = Nippon48Member(value)
        val age = member.age

        if (age < minAge || age > maxAge) {
          Logger.error(s"Age must be between $minAge and $maxAge, " +
            s"inclusively (invalid age: $age)")
          badRequest
        } else if (value.secondaryTeam.isDefined && value.primaryTeam.isEmpty) {
          Logger.error(s"Secondary team ${value.secondaryTeam.get} " +
            "is selected, but primary team is not selected")
          badRequest
        } else {
          Cloudant add member
          Redirect(routes.Nippon48Controller.index)
        }
      }
    )
  }

  /**
   * Lists all the Nippon48 members in the database in JSON format.
   *
   * @return the GET action
   */
  def getMembers = Action {
    Ok(Json.toJson(Cloudant.getMembers)(Nippon48MemberWriterList))
  }

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