/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

/**
 * Startup code, executed on page load
 */

$(function() {
	
	// if login should be enabled: set in minimal XML and JSCodeContainer
	if(login){
		window.setTimeout("logintimeout()", 57000 * 60);

		var link = $.query.set("action", "checkLogin").toString();
		link = window.location.href.replace(window.location.search, "") + link;

		$.ajax({
			type : "GET",
			async : false,
			url : link,
			success : function(html) {
				if (html == "NLI") {
					$("#jqLoginDialog").dialog("open");
				} else {
					$("#jqLoginDialog").dialog("close");
				}
			}
		});
	}

	/* creating and configuring the jquery UI save-case dialog */
	$(function() {

		var opts = {
			autoOpen : false,
			position : top,
			modal : true,
			width : 410,
			height : 210,
			minWidth : 410,
			minHeight : 210,
			buttons : {
				"Speichern" : d3web_sendSave,
				"Nein, weiter eingeben" : closeJQConfirmDialog
			}
		};
		$("#jqConfirmDialog").dialog(opts);
	});

	/* creating and configuring the jquery UI load-case dialog */
	$(function() {

		var opts = {
			autoOpen : false,
			position : [ "left", "top" ],
			modal : false,
			width : 180,
			height : 360,
			minWidth : 180,
			minHeight : 360,
			buttons : {
				"OK" : d3web_getSelectedCaseFileAndLoad,
				"Abbrechen" : closeJQLoadCaseDialog
			}
		};
		$("#jqLoadCaseDialog").dialog(opts);
	});
	
	/* creating and configuring the jquery UI summary dialog */
	$(function() {

		var opts = {
			autoOpen : false,
			position : [ "left", "top" ],
			modal : false,
			width : 400,
			height : 400,
			minWidth : 400,
			minHeight : 400,
			buttons : {
				"Schließen" : closeJQSummaryDialog
			}
		};
		$("#jqSummaryDialog").dialog(opts);
	});

	/* Initialize the JS binding to the dialog elements */
	initFunctionality();

	$()
			.ready(
					function() {

						// enable buttons in save case and load case dialogs to
						// react on pressing enter
						$(document)
								.keypress(
										function(e) {
											if ((e.which && e.which == 13)
													|| (e.keyCode && e.keyCode == 13)) {
												if ($('#jqConfirmDialog')
														.dialog('isOpen'))
													$(
															'[aria-labelledby$=jqConfirmDialog]')
															.find(
																	":button:contains('Speichern')")
															.click();
												if ($('#jqLoadCaseDialog')
														.dialog('isOpen'))
													$(
															'[aria-labelledby$=jqLoadCaseDialog]')
															.find(
																	":button:contains('OK')")
															.click();
												return false;
											}
										});

					});
});

/**
 * Set a click function to all form elements for enabling the sending of values
 * if either a radio-button has been clicked, or one or more num vals have been
 * entered followed by pressing enter.
 */
function initFunctionality() {

	/*
	 * bind "get selected facts" method to radio buttons, checkboxes and
	 * textareas
	 */
	$('[type=radio]').unbind('click').click(function() {
		d3web_getSelectedFacts($(this));
	});
	
	
	/* TODO MC QUESTIONS */
	/* the following ensures, that MC questions behave like OC questions,
	 * i.e., a v*/
	$('[type=checkbox]').unbind('click').click(function() {
		d3web_getSelectedFacts($(this));
	});

	$('[type=text]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13) {
				d3web_getSelectedFacts(thisEl);
			}
		});
	});

	$('[type=textarea]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13) {
				d3web_getSelectedFacts(thisEl);
			}
		});
	});

	// bind reset button to resetSession() function
	$('#reset').unbind('click').click(function() {
		d3web_resetSession();
	});

	// bind send/save button to sendexit function
	$('#savecase').unbind('click').click(function(event) {
		d3web_prepareSave();
	});

	// bind the loadcase button to making the fileselect list visible
	$('#loadcase').unbind('click').click(function(event) {

		$("#jqLoadCaseDialog").dialog("open");

		// make selectbox visible
		// var filesel = $('#fileselect');
		// filesel.attr("style", "display:block");
	});
	
	$('#summary').unbind('click').click(function(event){
		alert("click");
		$("#jqSummaryDialog").dialog("open");
	});
}

/**
 * Send an AJAX request to check, whether possibly answered num questions
 * have a certain value-range specified, and if yes, return the respective
 * question-ids and ranges for checking the user provided value against the
 * range.
 */
function d3web_checkNumRanges(qid, value, store, numStore) {

	/*
	 * FIRST assemble all questions that might be a num-q with range to a String
	 * representation: <qid>%<value>;<qid>%<value>; This can be the single
	 * clicked question, or those in the numStore TODO: maybe adapt later for
	 * handling also text etc questions
	 */
	var qids = "";
	if(qid !== undefined && value !== undefined){
		qids = qid + "%" + value + ";";
	} 
	
	if(numStore !== undefined && numStore !== " "){
		var storeVals = numStore.split(";");
		for (i = 0; i < storeVals.length; i++) {
			 var idVal = storeVals[i].split("###");
			 
			 if(idVal[0] !== "" || idVal[1] !== undefined){
				 if(!(idVal[0]==qid)){
					 var add = idVal[0] + "%" + idVal[1] + ";";
					 qids += add;
				 }
			 }
		 }	
	}
	
	/* create query string for calling the checkrange method of the servlet */
	var link = $.query.set("action", "checkRange").set("qids", qids).toString();
	link = window.location.href.replace(window.location.search, "") + link;
	
	// checked on rangeRequest complete; TRUE means, every provided value is
	// also within the value range (if one existed)
	var checkRangeOK = true;

	// send the request; on success, check whether qid/range pairs are given
	// back
	$
			.get(
					link,
					function(data) {

						alert(data);
						if (data !== "") { // range(s) exist(s)

							// split the ID/val/range complete String into
							// ID/val/range value triples
							var idValRange = data.split(";");

							for (i = 0; i < idValRange.length - 1; i++) { // for
								// each
								// triple

								// split the triple String into three distinct
								// vals
								var triple = idValRange[i].split("%");

								var id = triple[0];
								var value = triple[1];
								var range = triple[2];

								// get and parse range values
								var rangeVals = range.split("-");
								var min = parseFloat(rangeVals[0]);
								var max = parseFloat(rangeVals[1]);

								// if provided value not within specified range
								if (value < min || value > max) {

									// input fields have id prefix "f_"
									var idplus = "#f_" + id;
									var qerrid = "#error-q_" + id;

									// set error message for question
									$(qerrid).html(
											"Zulässiger Wertebereich: ["
													+ range + "]");

									// remove wront value
									$(idplus).val("");
									checkRangeOK = false; // set flag for not
									// all vals OK
								}
							}
							if (!checkRangeOK) {
								alert("Bitte überprüfen Sie die mit roter Fehlermeldung gekennzeichneten Fragen!");
							}
						}
					}).complete(function() {

				// if val was in provided range add value
				if (checkRangeOK) {

					// reset error messages
					$("[id*=error]").html("");
					d3web_addfactsRemembering(store, qid, value);
				}
				// otherwise do nothing
			});

}


function d3web_IQClicked(id) {
	alert("image answer " + id + " was clicked");
	d3web_getSelectedFacts($('#' + id));
	/*var target = $("#" + id);	// get the clicked element
	var selected = target.find(":input:checked");	// find clicked input 
	var deselected = target.find(":input:not(:checked)"); // find not clicked inputs
	selected.attr('checked', false);
	deselected.attr('checked', true);
	remark_selectively(target);	// mark question accordingly*/
}



function d3web_getRemainingFacts() {

	// some variables for storing multiple question/answer pairs
	var numStore = "";
	var txtStore = "";
	var datStore = "";
	var store = "";

	/*
	 * The next 3 code blocks fetch all input for input fields such as num,
	 * text, and date fields. This input is assembled into one data store in the
	 * form numvals&&&&textvals&&&&datevals thereby the 3 val arrays are of the
	 * form id###value;id###value; (= numvals)
	 */
	// all num questions
	numqs = $('#content [id^="q_"]').filter('[class*="question-num"]');
	numqs.each(function() {
		input = $(this).find(":text,textarea").filter(":first");
		if (input.size() == 1) {
			if ($(this).attr('class').indexOf('abstract') < 0
					&& input.attr('value') != "") {
				numStore += $(this).attr('id') + "###" + input.val() + ";"
			}
		}
	});
	// all text questions
	txtqs = $('#content [id^="q_"]').filter('[class*="question-text"]');
	txtqs
			.each(function() {
				input = $(this).find(":text,textarea").filter(":first");
				if (input.size() == 1) {
					if ($(this).attr('class').indexOf('abstract') < 0
							&& input.attr('value') != ""
							&& input.attr('value') != " ") {
						txtStore += $(this).attr('id') + "###" + input.val()
								+ ";"
					}
				}
			});
	// all date questions
	datqs = $('#content [id^="q_"]').filter('[class*="question-date"]');
	datqs
			.each(function() {
				input = $(this).find(":text,textarea").filter(":first");
				if (input.size() == 1) {
					if ($(this).attr('class').indexOf('abstract') < 0
							&& input.attr('value') != ""
							&& input.attr('value') != " ") {
						datStore += $(this).attr('id') + "###" + input.val()
								+ ";"
					}
				}
			});

	/*
	 * Check, whether there have been values for num/text/date fields if not so,
	 * add an " " to enable better array splitting when getting the values later
	 */
	if (numStore == "") {
		numStore = " ";
	}
	if (txtStore == "") {
		txtStore = " ";
	}
	if (datStore == "") {
		datStore = " ";
	}
	// assemble complete text/textinput value store
	store += numStore + "&&&&" + txtStore + "&&&&" + datStore;

	/*
	 * AJAX call to send both the value store as well as a potential single
	 * radio/checkbox input
	 */
	d3web_addfactsBeforeSave(store);
}

/**
 * Transfer a selected or removed fact to the d3web dialog via an ajax call.
 * This is a special function for sending a whole store of "remembered" answers,
 * e.g., num answers that have not yet been confirmed
 */
function d3web_addfactsRemembering(store, qid, pos) {

	var link = $.query.set("action", "addFact").set("qid", qid).set("pos", pos)
			.set("store", store).toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		url : link,
		success : function(html) {
			if (html !== "") {
				// Error message and reset session so user can provide input
				// first
				var errMsg = "Das Feld '" + html
						+ "' muss immer zuerst ausgefüllt werden!";
				alert(errMsg);
				d3web_resetSession();
			} else {
				d3web_show();
			}
		}
	});
}

function d3web_addfactsBeforeSave(store) {

	var link = $.query.set("action", "addFact").set("qid", "").set("pos", "")
			.set("store", store).toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			// d3web_nextform();

			// if (html != "same") { // replace target id of content if not the
			// same

			d3web_sendSave();
			// }
		}
	});
}

/**
 * Function for resetting a d3web Session, i.e., for creating a new session and
 * thus starting a new problem solving case/session.
 */
function d3web_resetSession() {
	var link = $.query.set("action", "reset").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		url : link,
		success : function() {
			window.location.reload(true);
			initFunctionality();
		}
	});
}

function d3web_sessionForNewUser() {

	var link = $.query.set("action", "resetNewUser").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		url : link,
		success : function() {

			d3web_show();
		}
	});
}

function d3web_prepareSave() {

	// get number of unanswered questions
	questionsAll = $('#content [id^="q_"]');
	questionsAnswered = questionsAll.filter('[class$="question-d"]');
	questionsUnanswered = questionsAll.size() - questionsAnswered.size();

	var qua = "<b> " + questionsUnanswered + " </b>";
	$('#numberUnanswered').html(qua);

	if (questionsUnanswered !== 0) {
		$('#jqConfirmDialog').dialog("open");
	}

}

function d3web_sendSave() {

	// d3web_getRemainingFacts();

	var confirmFilename = "";
	confirmFilename = $('#confirmFilename').val();

	var link = $.query.set("action", "savecase").set("userfn", confirmFilename)
			.toString();

	link = window.location.href.replace(window.location.search, "") + link;

	// new jquery 1.5 syntax
	$
			.get(
					link,
					function(data) {
						if (data == "exists") {
							$('#confirmError')
									.html(
											"<font color=\"red\">Dateiname exisitiert bereits. Bitte anderen Namen wählen.</font>");
						} else {

							d3web_show();
							// window.location.reload();
							// initFunctionality();
						}
					});
}

function d3web_show() {

	var link = $.query.set("action", "show").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			// d3web_nextform();

			// if (html != "same") { // replace target id of content if not the
			// same
			window.location.reload();
			initFunctionality();
			// }
		}
	});
}

/**
 * Retrieves the currently selected value (as text) from the case-loading
 * selectbox. This function is directly defined/linked in the FileSelect.st
 * StringTemplate.
 */
function d3web_getSelectedCaseFileAndLoad() {

	// get selected text = filename
	var filename = $('#caseSelect :selected').text();

	if (filename !== "") {

		// call AJAX function for loading the case with specified filename.
		d3web_loadCase(filename);
	} else {
		closeJQLoadCaseDialog();
	}
}

/**
 * Sends the name of the case/file that is to be loaded into the dialog via AJAX
 * so it can be processed by the dialog servlet (by reading the corresponding
 * "fn" request parameter).
 * 
 * @param filename
 *            Name of the file to be laoded.
 */
function d3web_loadCase(filename) {

	var link = $.query.set("action", "loadcase").set("fn", filename).toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			// d3web_nextform();

			// if (html != "same") { // replace target id of content if not the
			// same
			window.location.reload();
			initFunctionality();
			// }
		}
	});
}

/**
 * Use an ajax call for requesting the nextForm in the dialog flow from d3web.
 * The old form is being replaced by the new content without reloading the whole
 * page. Important: resetup the click functions on the OK buttons and form
 * elements for sending the facts.
 */
function d3web_nextform() {

	// changes the querystring part of action into nextform
	// eg: formerly D3webDialog?src=HeartMed2010&action=show
	// --> &action=nextform
	// var link = $.query.set("action", "show").toString();

	// assemble the new link; replace the parts of the query string after ? with
	// nothing ("") and add the action nextform string part. result e.g.:
	// http://localhost:8080/d3web-ProKEt/D3webDialog?src=HeartMed2010&action=nextform
	// link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		// url : link,
		success : function(html) { // compare resulting html to former
			if (html != "same") { // replace target id of content if not the
				// same
				window.location.reload();
				initFunctionality();
			}
		}
	});

	// d3web_show_solutions("right"); // show solutions
}

/**
 * Select a questionnaire in d3web.
 */
function d3web_selectQuestionnaire(qid) {
	var link = $.query.set("action", "selectQuestionnaire").set("qid", qid)
			.toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			// d3web_nextform();
		}
	});
}

/**
 * Transfer children from d3web.
 */
function d3web_getChildren(pid) {
	var link = $.query.set("action", "getChildren").set("pid", pid).toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			$("#sub-" + pid).html(html);
		}
	});
}

/**
 * Get new ratings.
 */
function d3web_getRatings(list_of_ids) {
	var link = $.query.set("action", "getRatings").set("ids", list_of_ids)
			.toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			var elements = html.split(";");
			for (i = 0; i < elements.length; i++) {
				var elementId = elements[i].split(",")[0];
				var clazz = elements[i].split(",")[1];
				element = $("[id='" + elementId + "']");
				element.removeClass("rating-high rating-medium rating-low");
				if (clazz !== "remove") {
					element.addClass(clazz);
				}
			}
		}
	});
}

/**
 * Gets the solutions currently indicated by the underlying d3web dialog via an
 * ajax call to the D3WebDialog servlet
 */
function d3web_show_solutions(target_id) {

	var link = $.query.set("action", "solutions").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		// async : false,
		url : link,
		success : function(html) {
			$('#' + target_id).html(html).fadeIn(3000);
		}
	});
}

/**
 * Calculating the set facts for the parent question of a clicked element.
 * Thereby, all input fields (num/text/date) are checked and their values
 * denoted in a store. This ensures, that changed values, even for already
 * answered questions, are correctly propagated. Also, it is ensured, that
 * values of ALL input fields are propagated, regardless whether enter was
 * clicked each time. After storing the input field values, the dialog is
 * checked also for radio boxes and checkboxes input. Thereby, unknown option is
 * checked first as to ensure that its value is correctly toggled.
 * 
 * @param clickedItem
 *            the latest item clicked in the dialog
 */
function d3web_getSelectedFacts(clickedItem) {

	
	// some variables for storing multiple question/answer pairs
	var numStore = "";
	var txtStore = "";
	var datStore = "";
	var store = "";
	var hasNumFirst = false;

	/*
	 * The next 3 code blocks fetch all input for input fields such as num,
	 * text, and date fields. This input is assembled into one data store in the
	 * form numvals&&&&textvals&&&&datevals thereby the 3 val arrays are of the
	 * form id###value;id###value; (= numvals)
	 */
	// all num questions
	numqs = $('#content [id^="q_"]').filter('[class*="question-num"]');
	numqs.each(function() {
		input = $(this).find(":text,textarea").filter(":first");
		if (input.size() == 1) {
			if ($(this).attr('class').indexOf('abstract') < 0
					&& input.attr('value') != "") {
				numStore += $(this).attr('id') + "###" + input.val() + ";"
			}
		}
	});
	// all text questions
	txtqs = $('#content [id^="q_"]').filter('[class*="question-text"]');
	txtqs
			.each(function() {
				input = $(this).find(":text,textarea").filter(":first");
				if (input.size() == 1) {
					if ($(this).attr('class').indexOf('abstract') < 0
							&& input.attr('value') != ""
							&& input.attr('value') != " ") {
						txtStore += $(this).attr('id') + "###" + input.val()
								+ ";"
					}
				}
			});
	// all date questions
	datqs = $('#content [id^="q_"]').filter('[class*="question-date"]');
	datqs
			.each(function() {
				input = $(this).find(":text,textarea").filter(":first");
				if (input.size() == 1) {
					if ($(this).attr('class').indexOf('abstract') < 0
							&& input.attr('value') != ""
							&& input.attr('value') != " ") {
						datStore += $(this).attr('id') + "###" + input.val()
								+ ";"
					}
				}
			});

	/*
	 * Now the dialog is checkedradio button / checkbox input
	 */
	// "^=" means begins with, jquery expression
	question = clickedItem.closest('[id^="q_"]');
	unknown = clickedItem.closest('[id^="i_unknown"]');

	// if unknown has been clicked, it is primarily saved
	if (clickedItem.attr('id').indexOf('i_unknown') >= 0) {
		pos = clickedItem.attr("id");
		
	} 
	
	// image questions
	else if  (clickedItem.attr('id').indexOf('img_') >= 0) {
		pos = clickedItem.attr("id");
	}

	// otherwise check for "not-unknown" radio buttons and checkboxes
	else {
		var counter = 0, pos = "";
		/* text */
		if (pos == "") {
			items = question.find(":text,textarea").filter(":first");
			if (items.size() == 1) {
				pos = items.attr('value');
				hasNumFirst = true;
			}
		}
		/* radio buttons */
		if (pos == "") {
			items = question.find('[type=radio]');
			items.each(function() {
				if ($(this).attr('checked')) {
					pos = $(this).attr("id").substring(2);
				}
			});
		}
		/* checkboxes */
		if (pos == "") {
			items = question.find(":checkbox");
			items.each(function() {
				if ($(this).attr('checked')) {
					pos = $(this).attr("id").substring(2);
				}
			});
		}
		// no radio- or checkbox value set, e.g if only some nums are given
		if (pos == "") {
			pos = "EMPTY";
		}
	}

	/*
	 * Check, whether there have been values for num/text/date fields if not so,
	 * add an " " to enable better array splitting when getting the values later
	 */
	if (numStore == "") {
		numStore = " ";
	}
	if (txtStore == "") {
		txtStore = " ";
	}
	if (datStore == "") {
		datStore = " ";
	}

	// make sure, also "," is accepted as float separator
	pos = pos.replace(",", ".");
	numStore = numStore.replace(",", ".");
	// assemble complete text/textinput value store
	store += numStore + "&&&&" + txtStore + "&&&&" + datStore;

	
	d3web_checkNumRanges(question.attr('id'), pos, store, numStore);

}

function closeJQConfirmDialog() {
	$('#jqConfirmDialog').dialog('close');
}


function closeJQSummaryDialog() {
	$('#jqSummaryDialog').dialog('close');
}
// function closeJQLoginDialog() {
// $('#jqLoginDialog').dialog('close');
// }

function closeJQLoadCaseDialog() {
	$('#jqLoadCaseDialog').dialog('close');
}