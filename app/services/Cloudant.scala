//==============================================================================
// FILE:   app/services/Cloudant.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/31
//
// This file includes the `Cloudant` service singleton object.
//==============================================================================

package services

import com.typesafe.config.ConfigFactory
import models.Nippon48Member
import org.ektorp.ViewQuery
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import scala.collection.JavaConverters._
import scala.util.Try

/**
 * <a href="https://cloudant.com"><b>Cloudant</b></a> is an IBM open source
 * database service that is heavily based on CouchDB. This singleton object
 * provides service for reading and writing to the database of Nippon48 members
 * on Cloudant.
 */
object Cloudant {

  //============================== Private fields ==============================

  /** The Cloudant account. */
  private lazy val account = {
    val username = ConfigFactory.load getString "cloudant.username"
    new StdHttpClient.Builder().url(s"https://$username.cloudant.com").build
  }

  /** The Cloudant database of Nippon48 members. */
  private lazy val database = new StdCouchDbInstance(account)
    .createConnector(ConfigFactory.load getString "cloudant.database", false)

  /** The query for all the Nippon48 members in the Cloudant database. */
  private lazy val query = new ViewQuery designDocId "_design/members"

  //============================= CRUD operations ==============================

  /**
   * Adds a Nippon48 member to the Cloudant database.
   *
   * @param member  the Nippon48 member to be added
   */
  def add(member: Nippon48Member): Unit = { database create member }

  /**
   * Optionally returns the Nippon48 member associated with the specified ID in
   * the Cloudant database.
   *
   * @param id  the ID of the Nippon48 member to be accessed
   *
   * @return an ''optional'' that contains the Nippon48 member, or `None` if
   *         there is no member in the database associated with the ID
   */
  def fetch(id: String): Option[Nippon48Member] = {
    Try { database.get(classOf[Nippon48Member], id) }.toOption
  }

  /**
   * Removes the Nippon48 member associated with the specified ID from the
   * Cloudant database. This method does nothing if there is no such member in
   * the database.
   *
   * @param id  the ID of the Nippon48 member to be removed
   */
  def remove(id: String): Unit = {
    fetch(id) match {
      case Some(member) => database delete member
      case None =>
    }
  }

  /**
   * Updates the idol girl groups, teams, captain status, and generation number
   * of the Nippon48 member associated with the specified ID. This method does
   * nothing if there is no such member in the Cloudant database.
   *
   * @param id          the id of the Nippon48 member to be updated
   * @param groups      the new groups
   * @param teams       the new teams
   * @param isCaptain   the new captain status
   * @param generation  the new generation number
   */
  def update(id: String, groups: java.util.List[String],
    teams: java.util.List[String], isCaptain: Boolean,
    generation: Int): Unit = {

    fetch(id) match {
      case Some(member) =>
        member.groups = groups
        member.teams = teams
        member.isCaptain = isCaptain
        member.generation = generation
        database update member
      case None =>
    }
  }

  //============================ View result method ============================

  /**
   * Accesses all the Nippon48 members of the specified idol girl group in the
   * Cloudant database.
   *
   * @return the list of Nippon48 members
   */
  def getMembers: List[Nippon48Member] = {
    val result = database.queryView(query viewName "all")
    result.getRows.asScala.toList.map(row => fetch(row.getId).get)
  }
}