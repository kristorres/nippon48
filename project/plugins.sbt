//==============================================================================
// FILE:   project/plugins.sbt
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes all the plugins for the Nippon48 application.
//==============================================================================

//================================ Play plugin =================================

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.3.10")

//================================ Web plugins =================================

addSbtPlugin("com.typesafe.sbt" % "sbt-coffeescript" % "1.0.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.0.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-jshint" % "1.0.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-rjs" % "1.0.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-mocha" % "1.1.0")