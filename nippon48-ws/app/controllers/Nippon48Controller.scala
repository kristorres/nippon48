//===-- nippon48-ws/app/controllers/Nippon48Controller.scala --*- Scala -*-===//
//
// This source file is part of the Nippon48 web application project
//
// Created by Kris Torres on Saturday, January 30, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains the `Nippon48Controller` singleton object, which is the
/// '''main controller''' of the Nippon48 application.
///
//===----------------------------------------------------------------------===//

package controllers

import models.Nippon48Member
import models.forms.{Nippon48MemberData, Nippon48MemberUpdateData}
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import scala.collection.JavaConverters._
import services.Cloudant

/**
 * '''Nippon48''' is an interactive web application that lets the user
 * add/delete Nippon48 members in the database, and also edit their stats.
 */
object Nippon48Controller extends Controller {

  //===-------------------------- Private forms ---------------------------===//

  /** The form used to add a Nippon48 member to the database. */
  private val addMemberForm = Form {
    mapping(
      Nippon48MemberData.FIRST_NAME_KEY -> nonEmptyText,
      Nippon48MemberData.LAST_NAME_KEY -> nonEmptyText,
      Nippon48MemberData.NAME_JP_KEY -> optional(nonEmptyText),
      Nippon48MemberData.BIRTHDATE_KEY -> date,
      Nippon48MemberData.PRIMARY_GROUP_KEY -> nonEmptyText,
      Nippon48MemberData.SECONDARY_GROUP_KEY -> optional(nonEmptyText),
      Nippon48MemberData.PRIMARY_TEAM_KEY -> optional(nonEmptyText),
      Nippon48MemberData.SECONDARY_TEAM_KEY -> optional(nonEmptyText),
      Nippon48MemberData.CAPTAIN_STATUS_KEY -> text
    )(Nippon48MemberData.apply)(Nippon48MemberData.unapply)
  }

  /** The form used to edit the stats of a Nippon48 member in the database. */
  private val editMemberForm = Form {
    mapping(
      Nippon48MemberData.PRIMARY_GROUP_KEY -> nonEmptyText,
      Nippon48MemberData.SECONDARY_GROUP_KEY -> optional(nonEmptyText),
      Nippon48MemberData.PRIMARY_TEAM_KEY -> optional(nonEmptyText),
      Nippon48MemberData.SECONDARY_TEAM_KEY -> optional(nonEmptyText),
      Nippon48MemberData.CAPTAIN_STATUS_KEY -> text
    )(Nippon48MemberUpdateData.apply)(Nippon48MemberUpdateData.unapply)
  }

  //===------------------------------ Pages -------------------------------===//

  /**
   * Creates an `Action` to render the page that lists all the Nippon48 members
   * of the specified idol girl group and their stats in a table. This method
   * will be called when the application receives a `GET` request with a path of
   * `/groups/:name`.<br/><br/>
   *
   * '''Note that the supplied argument in this method is __case-sensitive__.'''
   *
   * @param name  the group (`"all"` to list ''all'' the Nippon48 members in the
   *              database)
   *
   * @return the `Action` to handle the `GET` request
   */
  def groupPage(name: String) = Action {
    if (name equalsIgnoreCase "all") {
      Redirect(routes.Nippon48Controller.index)
    } else {
      Nippon48Member.validGroups.find(_ equalsIgnoreCase name) match {
        case Some(group) => Ok(views.html.group(group))
        case None => NotFound(views.html.groupError(name))
      }
    }
  }

  /**
   * Creates an `Action` to render the application’s home page. This method will
   * be called when the application receives a `GET` request with a path of `/`.
   *
   * @return the `Action` to handle the `GET` request
   */
  def index = Action { Ok(views.html.index()) }

  /**
   * Creates an `Action` to render the page where the user can edit the stats of
   * the Nippon48 member associated with the specified ID. This method will be
   * called when the application receives a `GET` request with a path of
   * `/edit-member/:id`.
   *
   * @param id  the ID of the Nippon48 member to be updated
   *
   * @return the `Action` to handle the `GET` request
   */
  def memberUpdatePage(id: String) = Action {
    Nippon48Member(id) match {
      case Some(member) =>
        Ok(views.html.edit(member, editMemberForm) )
      case None =>
        NotFound(views.html.memberError("Sorry. " +
          "The Nippon48 member does not exist in the database."))
    }
  }

  /**
   * Creates an `Action` to render the page where the user can add a Nippon48
   * member to the database. This method will be called when the application
   * receives a `GET` request with a path of `/add-member`.
   *
   * @return the `Action` to handle the `GET` request
   */
  def newMemberPage = Action { Ok(views.html.add(addMemberForm, None)) }

  //===---------------------- RESTful API endpoints -----------------------===//

  /**
   * Creates an `Action` to add a Nippon48 member to the database, then redirect
   * to the application’s home page. This method will be called when the
   * application receives a `POST` request with a path of `/member`.
   *
   * @return the `Action` to handle the `POST` request
   */
  def addMember() = Action { implicit request =>

    val input = addMemberForm.bindFromRequest

    input.fold(
      formWithErrors => BadRequest(views.html.add(formWithErrors, None)),
      value => {

        val minAge = Nippon48Member.MIN_AGE
        val maxAge = Nippon48Member.MAX_AGE

        val group1 = value.primaryGroup
        val group2 = value.secondaryGroup
        val team1 = value.primaryTeam
        val team2 = value.secondaryTeam

        val member = Nippon48Member(value)
        val age = member.age
        val isCaptain = member.isCaptain
        val isKenkyuusei = member.teams == List("Kenkyuusei").asJava

        if (age < minAge || age > maxAge) {
          val error = "Nippon48 members must currently be between " +
            s"$minAge and $maxAge, inclusively. " +
            s"(${member.name} is currently $age.)"
          BadRequest(views.html.add(input, Some(error)))
        } else if (group2.isDefined && group1 == group2.get) {
          val error = "The primary and secondary idol girl groups " +
            "cannot be the same."
          BadRequest(views.html.add(input, Some(error)))
        } else if (team2.isDefined && team1.isEmpty) {
          val error = s"The secondary team ${value.secondaryTeam.get} " +
            "is selected, but the primary team is not selected."
          BadRequest(views.html.add(input, Some(error)))
        } else if (team2.isDefined && team1.get == team2.get) {
          val error = s"The primary and secondary teams cannot be the same."
          BadRequest(views.html.add(input, Some(error)))
        } else if (isKenkyuusei && isCaptain) {
          val error = "A kenkyuusei cannot be a captain."
          BadRequest(views.html.add(input, Some(error)))
        } else if (Nippon48Member(member.getId).isDefined) {
          val error = s"${member.name} is already in the database."
          BadRequest(views.html.add(input, Some(error)))
        } else {
          Cloudant add member
          Redirect(routes.Nippon48Controller.index)
        }
      }
    )
  }

  /**
   * Creates an `Action` to edit the stats of the Nippon48 member associated
   * with the specified ID. This method will be called when the application
   * receives a `POST` request with a path of `/member/:id`.
   *
   * @param id  the ID of the Nippon48 member to be updated
   *
   * @return the `Action` to handle the `POST` request
   */
  def editMember(id: String) = Action { implicit request =>

    Nippon48Member(id) match {

      case Some(member) =>

        val input = editMemberForm.bindFromRequest

        input.fold(
          formWithErrors =>
            BadRequest(views.html.editWithError(member, formWithErrors, None)),
          value => {

            val group1 = value.primaryGroup
            val group2 = value.secondaryGroup
            val team1 = value.primaryTeam
            val team2 = value.secondaryTeam

            if (group2.isDefined && group1 == group2.get) {
              val error = "The primary and secondary idol girl groups " +
                "cannot be the same."
              BadRequest(views.html.editWithError(member, input, Some(error)))
            } else if (team2.isDefined && team1.isEmpty) {
              val error = s"The secondary team ${value.secondaryTeam.get} " +
                "is selected, but the primary team is not selected."
              BadRequest(views.html.editWithError(member, input, Some(error)))
            } else if (team2.isDefined && team1.get == team2.get) {
              val error = s"The primary and secondary teams cannot be the same."
              BadRequest(views.html.editWithError(member, input, Some(error)))
            } else {

              val isCaptain = value.isCaptain == "Yes"
              var groups = List(value.primaryGroup)
              var teams = List[String]()

              if (value.secondaryGroup.isDefined)
                groups = groups :+ value.secondaryGroup.get
              if (value.primaryTeam.isDefined)
                teams = teams :+ value.primaryTeam.get
              if (value.secondaryTeam.isDefined)
                teams = teams :+ value.secondaryTeam.get

              if (teams == List("Kenkyuusei") && isCaptain) {
                val error = "A kenkyuusei cannot be a captain."
                BadRequest(views.html.editWithError(member, input, Some(error)))
              } else {
                Cloudant.update(id, groups.asJava, teams.asJava, isCaptain)
                Redirect(routes.Nippon48Controller.index)
              }
            }
          }
        )

      case None => NotFound(views.html.memberError("Sorry. " +
        "The Nippon48 member has recently been removed from the database."))
    }
  }

  /**
   * Creates an `Action` to list all the Nippon48 members in the database in
   * JSON format. This method will be called when the application receives a
   * `GET` request with a path of `/members`.
   *
   * @return the `Action` to handle the `GET` request
   */
  def getMembers = Action {
    Ok(Json.toJson(Cloudant getMembers None)(Nippon48MemberWriterList))
  }

  /**
   * Creates an `Action` to remove the Nippon48 member associated with the
   * specified ID from the database. This method will be called when the
   * application receives a `DELETE` request with a path of `/members/:id`.
   *
   * @param id  the ID of the Nippon48 member to be removed
   *
   * @return the `Action` to handle the `DELETE` request
   */
  def removeMember(id: String) = Action { Cloudant remove id; Ok }

  //===----------------------- Private JSON writers -----------------------===//

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