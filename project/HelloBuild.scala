//===-- project/HelloBuild.scala ------------------------------*- Scala -*-===//
//
// This configuration file is part of the Nippon48 web application project
//
// Created by Kris Torres on Saturday, February 20, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains the `HelloBuild` singleton object, which encapsulates all
/// the build settings and definitions of the root project and sub-projects.
///
//===----------------------------------------------------------------------===//

import play.PlayScala
import sbt._
import Keys._

object HelloBuild extends Build {

  //===------------------------ Library dependency ------------------------===//

  private val libraryDependency = "org.ektorp" % "org.ektorp" % "1.4.2"

  //===----------------------------- Resolver -----------------------------===//

  private val resolver = {
    "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
  }

  //===----------------------- Project identifiers ------------------------===//

  private val NIPPON48_ID = "nippon48-ws"
  private val SCRIPTS_ID = "scripts"

  //===----------------------- Project descriptions -----------------------===//

  lazy val NIPPON48_DESCRIPTION = {
    "The official Play web application dedicated to AKB48!"
  }

  lazy val SCRIPTS_DESCRIPTION = {
    "Should only be run locally on the developers’ workstation."
  }

  //===----------------------- Project definitions ------------------------===//

  lazy val root = Project("root", file("."))

  lazy val nippon48 = Project(NIPPON48_ID, file(NIPPON48_ID))
    .enablePlugins(PlayScala)
    .settings(name := NIPPON48_ID,
              organization := Settings.ORGANIZATION,
              version := Settings.VERSION,
              scalaVersion := Settings.SCALA_VERSION,
              description := NIPPON48_DESCRIPTION,
              libraryDependencies += libraryDependency,
              resolvers += resolver)

  lazy val scripts = Project(SCRIPTS_ID, file(SCRIPTS_ID))
    .settings(name := SCRIPTS_ID,
              organization := Settings.ORGANIZATION,
              version := Settings.VERSION,
              scalaVersion := Settings.SCALA_VERSION,
              description := SCRIPTS_DESCRIPTION)
    .dependsOn(nippon48)
    .aggregate(nippon48)

  //===-------------------------- Build settings --------------------------===//

  object Settings {
    val ORGANIZATION = "net.stage48"
    val VERSION = "1.0-β1"
    val SCALA_VERSION = "2.11.7"
  }
}