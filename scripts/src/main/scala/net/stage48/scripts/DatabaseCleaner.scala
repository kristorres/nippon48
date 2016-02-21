//==============================================================================
// FILE:   scripts/src/main/scala/net/stage48/scripts/DatabaseCleaner.scala
// AUTHOR: Kris Torres
// DATE:   2016/02/21
//
// This file includes the `DatabaseCleaner` singleton object.
//==============================================================================

package net.stage48.scripts

import java.io.File
import play.api.{DefaultApplication, Mode, Play}
import services.Cloudant

/**
 * The application where the developer can remove all the Nippon48 members from
 * the Cloudant database. In order to execute the program, run the following
 * command in the root directory of this project:
 *
 * {{{
 * sbt "project scripts" "run-main net.stage48.scripts.DatabaseCleaner"
 * }}}
 */
object DatabaseCleaner {

  def main(args: Array[String]): Unit = {
    val directory = new File("scripts")
    val classLoader = this.getClass.getClassLoader
    Play start new DefaultApplication(directory, classLoader, null, Mode.Dev)
    Cloudant.getMembers.foreach(Cloudant remove _.getId)
    Play.stop()
  }
}