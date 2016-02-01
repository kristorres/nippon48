//==============================================================================
// FILE:   build.sbt
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes all the build settings, library dependencies, and
// resolvers for the Nippon48 application.
//==============================================================================

//=============================== Build settings ===============================

name := "nippon48"

version := "1.0-β1"

scalaVersion := "2.11.7"

description := "The official Play web application dedicated to AKB48!"

//============================ Library dependencies ============================

libraryDependencies += "org.ektorp" % "org.ektorp" % "1.4.2"

//================================= Resolvers ==================================

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

//============================= Project definition =============================

lazy val root = (project in file(".")) enablePlugins PlayScala