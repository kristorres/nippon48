//==============================================================================
// FILE:   nippon48-ws/public/javascripts/hello.js
// AUTHOR: Kris Torres
// DATE:   2016/01/30
//
// This file includes all the Javascript functions for the Nippon48 application.
//==============================================================================

//================================== Function ==================================

/**
 * Sets the datepicker for the birthdate text field in the form that is used to
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

//================================ Main driver =================================

$(document).ready(function() {
  console.log("Welcome to Nippon48!");
  setDatePicker();
});