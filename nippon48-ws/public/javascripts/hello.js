//===-- nippon48-ws/public/javascripts/hello.js ---------------*- Scala -*-===//
//
// This source file is part of the Nippon48 web application project
//
// Created by Kris Torres on Saturday, January 30, 2016
//
//===----------------------------------------------------------------------===//
///
/// This file contains all the JavaScript functions for the Nippon48
/// application.
///
//===----------------------------------------------------------------------===//

//===----------------------------- Functions ------------------------------===//

/**
 * Sets the census statement, which reports the number of Nippon48 members in
 * the database. This function also hides the table of Nippon48 members and
 * their stats if there are currently no members in the database.
 */
function setCensusStatement() {

  var census = $("#census-statement");
  var count = $(".table tr").length - 1;
  var header = $(".jumbotron .container").text();
  var allMembers = header.includes("Nippon48");

  if (count == 0) {
    $(".table").hide();
    census.text("There are currently no members in the database"
      + (allMembers ? "." : " that belong to " + header + "."));
  } else if (count == 1) {
    census.text("There is currently 1 member in the database"
      + (allMembers ? "." : " that belongs to " + header + "."));
  } else {
    census.text("There are currently " + count + " members in the database"
      + (allMembers ? "." : " that belong to " + header + "."));
  }
}

/**
 * Sets the date picker for the birthdate text field in the form that is used to
 * add a Nippon48 member to the database. The minimum date is set to 30 years
 * before today’s date, while the maximum date is set to 12 years before today’s
 * date.
 */
function setDatePicker() {

  var today = new Date();
  var year = today.getFullYear();
  var month = today.getMonth() + 1;
  var day = today.getDate();

  if (month < 10) month = "0" + month;
  if (day < 10) day = "0" + day;

  var minYear = year - 30;
  var maxYear = year - 12;

  $("#birthdate").datepicker({
    changeMonth: true,
    changeYear: true,
    yearRange: minYear + ":" + maxYear,
    minDate: month + "/" + day + "/" + minYear,
    maxDate: month + "/" + day + "/" + maxYear
  });
}

/**
 * Sets the background and foreground colors of the jumbotron.
 *
 * @param {String} background  the background color
 * @param {String} foreground  the foreground color
 */
function setJumbotronColors(background, foreground) {
  $(".jumbotron").css("background-color", background);
  $(".jumbotron h1").css("color", foreground);
  $(".jumbotron p").css("color", foreground);
}

//===---------------------------- Main driver -----------------------------===//

$(document).ready(function() {

  console.log("Welcome to Nippon48!");
  setCensusStatement();
  setDatePicker();

  switch ($(".jumbotron .container h1").text()) {
    case "AKB48": setJumbotronColors("#EE92B0", "white"); break;
    case "SKE48": setJumbotronColors("#EDA72B", "white"); break;
    case "NMB48": setJumbotronColors("#F48700", "white"); break;
    case "HKT48": setJumbotronColors("black", "white"); break;
    case "NGT48": setJumbotronColors("white", "red"); break;
    case "Nogizaka46": setJumbotronColors("#7E0E85", "white"); break;
    case "Keyakizaka46": setJumbotronColors("#1AB631", "white"); break;
    default: setJumbotronColors("deeppink", "white");
  }

  $(".editable").click(function() { window.location = $(this).data("href"); });

  // Removes the table row that contains the Nippon48 member.
  $("#delete-button").click(function() {
    var $button = $(this);
    var id = $button.data("id");
    $.ajax({
      url: "/members/" + id,
      type: "DELETE"
    }).done(function() {
      window.location = "/";
    });
  });
});