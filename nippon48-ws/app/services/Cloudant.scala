//==============================================================================
// FILE:   nippon48-ws/app/services/Cloudant.scala
// AUTHOR: Kris Torres
// DATE:   2016/01/31
//
// This file includes the `Cloudant` service singleton object.
//==============================================================================

package services

import com.typesafe.config.ConfigFactory
import models.Nippon48Member
import org.ektorp.{CouchDbConnector, ViewQuery}
import org.ektorp.http.StdHttpClient
import org.ektorp.impl.StdCouchDbInstance
import play.api.Logger
import play.api.Play.current
import play.api.libs.concurrent.Akka
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.Try

/**
 * <a href="https://cloudant.com"><b>Cloudant</b></a> is an IBM open source
 * database service that is heavily based on CouchDB. This singleton object
 * provides service for reading and writing to the database of Nippon48 members
 * on Cloudant.
 */
object Cloudant {

  //============================== Private fields ==============================

  /** The loaded configuration for Cloudant. */
  private lazy val configuration = ConfigFactory.load

  /** The query for all the Nippon48 members in the Cloudant database. */
  private lazy val query = new ViewQuery designDocId "_design/members"

  /** The Cloudant account. */
  private var account: StdHttpClient.Builder = _

  /** The Cloudant database of Nippon48 members. */
  private var database: Option[CouchDbConnector] = None

  load()

  //============================= CRUD operations ==============================

  /**
   * Adds a Nippon48 member to the Cloudant database.
   *
   * @param member  the Nippon48 member to be added
   *
   * @throws org.ektorp.UpdateConflictException if there is a Nippon48 member in
   *         the database associated with the same ID as the specified Nippon48
   *         member
   */
  def add(member: Nippon48Member): Unit = {
    if (database.isEmpty) connectToDatabase()
    database.get create member
    Logger debug s"Added Nippon48 member ${member.name} to Cloudant."
  }

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
    if (database.isEmpty) connectToDatabase()
    Try { database.get.get(classOf[Nippon48Member], id) }.toOption
  }

  /**
   * Removes the Nippon48 member associated with the specified ID from the
   * Cloudant database. This method does nothing if there is no such member in
   * the database.
   *
   * @param id  the ID of the Nippon48 member to be removed
   */
  def remove(id: String): Unit = {
    if (database.isEmpty) connectToDatabase()
    fetch(id) match {
      case Some(member) =>
        database.get delete member
        Logger debug s"Removed Nippon48 member ${member.name} from Cloudant."
      case None =>
    }
  }

  /**
   * Updates the idol girl groups, teams, and captain status of the Nippon48
   * member associated with the specified ID. This method does nothing if there
   * is no such member in the Cloudant database.
   *
   * @param id          the ID of the Nippon48 member to be updated
   * @param groups      the new groups
   * @param teams       the new teams
   * @param isCaptain   `true` if the Nippon48 member is a captain, or `false`
   *                    otherwise
   */
  def update(id: String, groups: java.util.List[String],
    teams: java.util.List[String], isCaptain: Boolean): Unit = {

    if (database.isEmpty) connectToDatabase()

    fetch(id) match {
      case Some(member) =>
        member.groups = groups
        member.teams = teams
        member.isCaptain = isCaptain
        database.get update member
        Logger debug s"Updated Nippon48 member ${member.name}."
      case None =>
    }
  }

  //============================== Other methods ===============================

  /** Connects to the configured Cloudant database. */
  def connectToDatabase(): Unit = {

    val initialTime = System.nanoTime
    val username = configuration getString "cloudant.username"
    val url = s"https://$username.cloudant.com"
    Logger info s"Cloudant database URL: $url"

    account = new StdHttpClient.Builder url url
    setConnectionTimeout()
    setSocketTimeout()
    setMaximumObjectSize()
    setMaximumNumberOfConnections()
    cleanUpIdleConnections()

    database = Some(new StdCouchDbInstance(account.build)
      .createConnector(configuration getString "cloudant.database", false))

    val finalTime = System.nanoTime
    val timeInSeconds = (finalTime - initialTime) / 10e8
    Logger debug f"Connected in $timeInSeconds%.3f s."
  }

  /**
   * Accesses all the Nippon48 members of the specified idol girl group in the
   * Cloudant database.
   *
   * @param group  an ''optional'' that contains the group, or `None` to query
   *               all the members in the database
   *
   * @return the list of Nippon48 members
   *
   * @throws java.util.NoSuchElementException if there is no active connection
   *         to the database
   */
  def getMembers(group: Option[String]): List[Nippon48Member] = {
    if (database.isEmpty) connectToDatabase()
    val result = database.get.queryView(query viewName "all")
    var members = result.getRows.asScala.toList.map(row => fetch(row.getId).get)
    if (group.isDefined) members = members.filter(_.groups.contains(group.get))
    members
  }

  //============================= Private methods ==============================

  /** Cleans up any idle connections if the configuration is set to `true`. */
  private def cleanUpIdleConnections(): Unit = {
    val key = "cloudant.build.clean-up-idle-connections"
    val cleanUpIdleConnections = Try { configuration getBoolean key }.toOption
    cleanUpIdleConnections.map { ans =>
      val answer = if (ans) "Yes" else "No"
      Logger info s"Clean up idle connections? $answer"
      account cleanupIdleConnections ans
    }
  }

  /**
   * Loads the Cloudant database, then constantly scans it every minute to make
   * sure the connection is still up.
   */
  private def load(): Unit = {
    if (database.isEmpty) connectToDatabase()
    Logger info "Validating CouchDB view..."
    Akka.system.scheduler.schedule(0.minutes, 1.minute, CouchDBViewRunnable)
  }

  /** Sets the configured connection timeout. */
  private def setConnectionTimeout(): Unit = {
    val key = "cloudant.build.connection-timeout"
    val connectionTimeout = Try { configuration getInt key }.toOption
    connectionTimeout.map { t =>
      Logger info s"Connection timeout: $t ms"
      account connectionTimeout t
    }
  }

  /** Sets the configured maximum number of connections. */
  private def setMaximumNumberOfConnections(): Unit = {
    val key = "cloudant.build.max-num-connections"
    val maxNumConnections = Try { configuration getInt key }.toOption
    maxNumConnections.map { n =>
      Logger info s"Maximum number of connections: $n"
      account maxConnections n
    }
  }

  /** Sets the configured maximum object size. */
  private def setMaximumObjectSize(): Unit = {
    val key = "cloudant.build.max-object-size"
    val maxObjectSize = Try { configuration getInt key }.toOption
    maxObjectSize.map { n =>
      Logger info s"Maximum object size: $n B"
      account maxObjectSizeBytes n
    }
  }

  /** Sets the configured socket timeout. */
  private def setSocketTimeout(): Unit = {
    val key = "cloudant.build.socket-timeout"
    val socketTimeout = Try { configuration getInt key }.toOption
    socketTimeout.map { t =>
      Logger info s"Socket timeout: $t ms"
      account socketTimeout t
    }
  }

  //============================= Private runnable =============================

  /**
   * The inner singleton object that validates the CouchDB view of all the
   * Nippon48 members in the Cloudant database.
   */
  private object CouchDBViewRunnable extends Runnable {

    override def run(): Unit = {

      try {
        getMembers(None)
      } catch {
        case _: NoSuchElementException =>
          Logger error "Unable to validate CouchDB view."
          Logger error "Attempting to reconnect to Cloudant..."
          connectToDatabase()
      }
    }
  }
}