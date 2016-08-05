//===-- project/plugins.sbt -----------------------------------*- Scala -*-===//
//
// This configuration file is part of the Nippon48 web application project
//
// Created by Kris Torres on Saturday, January 30, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains all the plugins for the Nippon48 application.
///
//===----------------------------------------------------------------------===//

//===---------------------------- Play plugin -----------------------------===//

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.10")

//===---------------------------- Web plugins -----------------------------===//

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.2")