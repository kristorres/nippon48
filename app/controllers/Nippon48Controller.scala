//==============================================================================
// FILE:   app/controllers/Nippon48Controller.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes the `Nippon48Controller` singleton object.
//==============================================================================

package controllers

import play.api.mvc.{Action, Controller}

/**
 * '''Nippon48''' is an interactive web application that lets the user
 * add/delete Nippon48 members in the database, and also update their stats.
 */
object Nippon48Controller extends Controller {

  /**
   * Renders the index page of this application.
   *
   * @return the GET action
   */
  def index = Action { Ok(views.html.index("Welcome to Nippon48!")) }
}