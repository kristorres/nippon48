//===-- scripts/src/main/scala/net/stage48/scripts/DatabaseC… -*- Scala -*-===//
//
// This source file is part of the Nippon48 web application project
//
// Created by Kris Torres on Sunday, February 21, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains the `DatabaseCleaner` singleton object.
///
//===----------------------------------------------------------------------===//

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
    Cloudant.getMembers(None).foreach(Cloudant remove _.getId)
    Play.stop()
  }
}