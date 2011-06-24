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
var mcStore = new Object();
var ocStore = new Object();
var dateStore = new Object();
var textStore = new Object();
var numStore = new Object();
var headerHeight = -1;



$(function() {
	
	// if login should be enabled: set in minimal XML and JSCodeContainer
	if(login){
		window.setTimeout("logintimeout()", 57000 * 60);

		var link = $.query.set("action", "checkLogin").toString();
		link = window.location.href.replace(window.location.search, "") + link;
		
		$.ajax({
			type: "GET",
			async: false,
			url: link,
			success : function(html) {
				if (html == "NLI") {
					$("#jqLoginDialog").dialog("open");
				} else {
					$("#jqLoginDialog").dialog("close");
				}
			}
		});
	}

	var save, goon;
	if(language=="en"){
		save = "Save";
		goon = "Continue";
	} else if(language=="de"){
		save = "Speichern";
		goon = "Nein, weiter eingeben";
	}
	
	/* creating and configuring the jquery UI save-case dialog */
	$(function() {

		var opts = {
			autoOpen: false,
			position: top,
			modal: false,
			width: 410,
			height: 210,
			minWidth: 410,
			minHeight: 210,
			buttons: [{
				id: "saveOK",
				text: save,
				click: function(){
					d3web_sendSave();
				}
				},
				{
				id: "saveCancel",
				text: goon,
				click: function(){
					 closeJQConfirmDialog();
					}
				}]
		};
		$("#jqConfirmDialog").dialog(opts);
	});

	var load, cancelload;
	if(language=="en"){
		load = "OK";
		cancelload = "Cancel";
	} else if(language=="de"){
		load = "OK";
		cancelload = "Abbrechen";
	}
	
	/* creating and configuring the jquery UI load-case dialog */
	$(function() {

		var opts = {
			autoOpen: false,
			position: [ "left", "top" ],
			modal: false,
			width: 180,
			height: 360,
			minWidth: 180,
			minHeight: 360,
			buttons: [{
				id: "loadOK",
				text: load,
				click: function(){
					d3web_getSelectedCaseFileAndLoad();
				}
				},
				{
				id: "loadCancel",
				text: cancelload,
				click: function(){
					closeJQLoadCaseDialog();
					}
				}]
		};
		$("#jqLoadCaseDialog").dialog(opts);
	});
	
	var close;
	if(language=="en"){
		close = "Close";
	} else if(language=="de"){
		close = "Schließen";
	}
	
	var print;
	if(language=="en"){
		print = "Print";
	} else if(language=="de"){
		print = "Drucken";
	}
	
	/* creating and configuring the jquery UI summary dialog */
	$(function() {

		var opts = {
			autoOpen: false,
			position: [ "center", "top" ],
			modal: false,
			width: 900,
			height: 700,
			minWidth: 900,
			minHeight: 700,
			buttons: [{
				id: "sumPrint",
				text: print,
				click: function(){
					$("#jqSummaryDialog").jqprint();
				}
			},
			{
				id: "sumClose",
				text: close,
				click: function(){
					closeJQSummaryDialog();
				}
			}]
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

	// check browser and warn if the wrong one is used
	if (!(BrowserDetect.browser == "Chrome" || BrowserDetect.browser == "Firefox")) {
		$('[id=head]').children("table").children("tbody").append(
				"<tr><td colspan='3' style='color:red; font-variant:normal' >Sie benutzen " +
				"den Browser '" + BrowserDetect.browser + "'. Dieser Browser wird von dieser Seite " +
				"noch nicht unterstützt. Bitte nutzen sie stattdessen " +
				"<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox</a> " +
				"oder " +
				"<a href='http://www.google.com/chrome/'>Google Chrome</a>!</td></tr>");
	}
	
	// move the content below the header
	var content = $('[id=content]');
	var head = $('[id=head]');
	
	if (headerHeight == -1) {
		headerHeight = getHeaderHeight(head) - 15;
	}
	content.css("margin-top", headerHeight + "px");
	
	$(window).resize(function() {
		headerHeight = getHeaderHeight(head) + 9;
		content.css("margin-top", headerHeight + "px");
	});
	
	/*
	 * bind "get selected facts" method to radio buttons, checkboxes and
	 * textareas
	 */
	$('[type=radio]').unbind('click').click(function() {
		d3web_storeQuestionOC($(this));
		d3web_addFacts($(this));
	});
	
	
	/* TODO MC QUESTIONS */
	/* the following ensures, that MC questions behave like OC questions,
	 * i.e., a v*/
	$('[type=checkbox]').unbind('click').click(function() {
		d3web_storeQuestionMC($(this));
	});

	$('[id^=ok-]').unbind('click').click(function(event) {
		d3web_addFacts($(this));
	});
	
	
	$('[type=text]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13) {
				d3web_storeQuestionText($(this));
				d3web_addFacts($(this));
			}
		});
	});
	$('[type=text]').blur(function() {
		d3web_storeQuestionText($(this));
	});

	$('[type=textarea]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if (code == 13) {
				d3web_storeQuestionText($(this));
				d3web_addFacts($(this));
			}
		});
	});
	$('[type=text]').blur(function() {
		d3web_storeQuestionText($(this));
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
		$("#jqSummaryDialog").dialog("open");
	});
}

function getHeaderHeight(head) {
	return head.height() + parseInt(head.css("padding-top")) + parseInt(head.css("padding-bottom"));
}

function d3web_storeQuestionText(textInput) {
	var textQID = $(textInput.parents("[id^=q_]")).attr("id").replace("q_", "");
	textStore[textQID] = $(textInput).val();
}

function d3web_storeQuestionNum(numInput) {
	var numQID = $(numInput.parents("[id^=q_]")).attr("id").replace("q_", "");
	numStore[numQID] = $(numInput).val();
}

function d3web_storeQuestionDate(dateInput) {
	var dateQID = $(dateInput.parents("[id^=q_]")).attr("id").replace("q_", "");
	dateStore[dateQID] = $(dateInput).val();
}

function d3web_storeQuestionOC(ocInput) {
	var ocQID = $(ocInput.parents("[id^=q_]")).attr("id").replace("q_", "");
	ocStore[ocQID] = $(ocInput).attr('title');
}

function d3web_storeQuestionMC(mcCheckBox) {
	
	var mcQParent = $(mcCheckBox.parents("[id^=q_]"));
	var mcQID = mcQParent.attr("id").replace("q_", "");
	var checkBoxes = mcQParent.find(":checkbox");

	// get the question-content-parent element and go through all its
	// checkbox-children
	var checkedBoxes = new Array();
	checkBoxes.each(function() {
		inputid = $(this).attr("id");
		if ($(this).attr("checked") == true) {
			checkedBoxes.push($(this).attr("id").replace("f_", ""));
		}
	});
	
	mcStore[mcQID] = checkedBoxes;
}

function d3web_addFacts() {

	var link = $.query.set("action", "addFacts");

	var i = 0;
	for (var qid in mcStore) {
		link = link.set("mcq" + i, qid).set("mcchoices" + i, mcStore[qid].toString());
		i++;
	}
	
	i = 0;
	for (var qid in ocStore) {
		link = link.set("ocq" + i, qid).set("occhoice" + i, ocStore[qid]);
		i++;
	}
	
	i = 0;
	for (var qid in dateStore) {
		link = link.set("dateq" + i, qid).set("date" + i, dateStore[qid]);
		i++;
	}
	
	for (var qid in textStore) {
		link = link.set("textq" + i, qid).set("text" + i, textStore[qid]);
		i++;
	}
	
	i = 0;
	for (var qid in numStore) {
		link = link.set("numq" + i, qid).set("num" + i, numStore[qid]);
		i++;
	}
	
	
	link = window.location.href.replace(window.location.search, "") + link.toString();

	$.ajax({
		type : "GET",
		url : link,
		success : function(html) {
			if (html.startsWith("##missingfield##")) {
				// Error message and reset session so user can provide input
				// first
				var errMsg = "Das Feld '" + html.replace("##missingfield##", "")
						+ "' muss immer zuerst ausgefüllt werden!";
				alert(errMsg);
				d3web_resetSession();
			} else if (html.startsWith("##replaceid##")) {
				var updateArray = html.split(/##replaceid##|##replacecontent##/);
				for (var i = 1; i < updateArray.length - 1; i+=2) {
					$("#" + updateArray[i]).replaceWith(updateArray[i + 1]);
				}
				setup();
				initFunctionality();
//				d3web_show();
			}
			
		}
	});
}

////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////// OLD STUFF, NOT SURE IF NEEDED ///////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////

function escapeExpression(str) {
    return str.replace(/([#;&,\.\+\*\~':"\!\^$\[\]\(\)=>\|])/g, "\\$1");
}

function d3web_IQClicked(id) {
	//alert("image answer " + id + " was clicked");
	// d3web_getSelectedFacts($('#' + id));
	var target = $("#" + escapeExpression(id));	// get the clicked element
	var selected = target.find(":input:checked");	// find clicked input 
	var deselected = target.find(":input:not(:checked)"); // find not clicked inputs
	selected.attr('checked', false);
	deselected.attr('checked', true);
	d3web_storeQuestionMC(target);
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

var BrowserDetect = {
		init: function () {
			this.browser = this.searchString(this.dataBrowser) || "An unknown browser";
			this.version = this.searchVersion(navigator.userAgent)
				|| this.searchVersion(navigator.appVersion)
				|| "an unknown version";
			this.OS = this.searchString(this.dataOS) || "an unknown OS";
		},
		searchString: function (data) {
			for (var i=0;i<data.length;i++)	{
				var dataString = data[i].string;
				var dataProp = data[i].prop;
				this.versionSearchString = data[i].versionSearch || data[i].identity;
				if (dataString) {
					if (dataString.indexOf(data[i].subString) != -1)
						return data[i].identity;
				}
				else if (dataProp)
					return data[i].identity;
			}
		},
		searchVersion: function (dataString) {
			var index = dataString.indexOf(this.versionSearchString);
			if (index == -1) return;
			return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
		},
		dataBrowser: [
			{
				string: navigator.userAgent,
				subString: "Chrome",
				identity: "Chrome"
			},
			{ 	string: navigator.userAgent,
				subString: "OmniWeb",
				versionSearch: "OmniWeb/",
				identity: "OmniWeb"
			},
			{
				string: navigator.vendor,
				subString: "Apple",
				identity: "Safari",
				versionSearch: "Version"
			},
			{
				prop: window.opera,
				identity: "Opera"
			},
			{
				string: navigator.vendor,
				subString: "iCab",
				identity: "iCab"
			},
			{
				string: navigator.vendor,
				subString: "KDE",
				identity: "Konqueror"
			},
			{
				string: navigator.userAgent,
				subString: "Firefox",
				identity: "Firefox"
			},
			{
				string: navigator.vendor,
				subString: "Camino",
				identity: "Camino"
			},
			{		// for newer Netscapes (6+)
				string: navigator.userAgent,
				subString: "Netscape",
				identity: "Netscape"
			},
			{
				string: navigator.userAgent,
				subString: "MSIE",
				identity: "Internet Explorer",
				versionSearch: "MSIE"
			},
			{
				string: navigator.userAgent,
				subString: "Gecko",
				identity: "Mozilla",
				versionSearch: "rv"
			},
			{ 		// for older Netscapes (4-)
				string: navigator.userAgent,
				subString: "Mozilla",
				identity: "Netscape",
				versionSearch: "Mozilla"
			}
		],
		dataOS : [
			{
				string: navigator.platform,
				subString: "Win",
				identity: "Windows"
			},
			{
				string: navigator.platform,
				subString: "Mac",
				identity: "Mac"
			},
			{
				   string: navigator.userAgent,
				   subString: "iPhone",
				   identity: "iPhone/iPod"
		    },
			{
				string: navigator.platform,
				subString: "Linux",
				identity: "Linux"
			}
		]

	};
	BrowserDetect.init();