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
 * Startup code, executed on page load, ONLY on the very first,
 * initial page load!
 */
$(function() {

	// on page load, init the JS binding to the dialog elements
	d3web_init();
	
	// make navigation work on d3web level
	//$(".navigation-item").each(function() {
		//var id = $(this).attr('id');
		//$(this).children("a").bind("click", function() {
			//d3web_selectQuestionnaire(id.replace(/^nav-/, ""));
		//});
	//});
});


/**
 * Set a click function to all form elements for enabling the
 * sending of values if either a radio-button has been clicked,
 * or one or more num vals have been entered followed by pressing enter.
 */
function d3web_init(){
	
	/* bind "get selected facts" method to radio buttons, checkboxes
	 * and textareas
	 */
	$('[type=radio]').unbind('click').click(function() {
		d3web_getSelectedFacts($(this));
	});
	
	$('[type=text]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				d3web_getSelectedFacts(thisEl);
			}
		});
	});
	
	$('[type=textarea]').unbind('click').click(function() {
		var thisEl = $(this);
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				d3web_getSelectedFacts(thisEl);
			}
		});	
	});
	
	// bind reset button to resetSession() function
	$('#reset').unbind('click').click(function(){
		d3web_resetSession();
	});
	
	// bind send/save button to sendexit function
	$('#savecase').unbind('click').click(function(event){
		d3web_saveCase(event);
	});
	
	// bind send/save button to sendexit function
	$('#loadcase').unbind('click').click(function(event){
		//d3web_loadCase(event);
	});
}


/**
 * Calculating the set facts for the parent question of a clicked element.
 * Thereby, all input fields (num/text/date) are checked and their values
 * denoted in a store. This ensures, that changed values, even for already 
 * answered questions, are correctly propagated. Also, it is ensured, that 
 * values of ALL input fields are propagated, regardless whether enter was
 * clicked each time.
 * After storing the input field values, the dialog is checked also for 
 * radio boxes and checkboxes input. Thereby, unknown option is checked 
 * first as to ensure that its value is correctly toggled.
 * 
 * @param clickedItem the latest item clicked in the dialog
 */
function d3web_getSelectedFacts(clickedItem) {
	
	var numStore=""; var txtStore=""; var datStore=""; var store = "";
	
	/* The next 3 code blocks fetch all input for input fields such as
	 * num, text, and date fields. This input is assembled into one data
	 * store in the form 
	 * 		numvals&&&&textvals&&&&datevals
	 * thereby the 3 val arrays are of the form
	 * 		id###value;id###value; (= numvals)
	 */
	// all num questions 
	numqs = $('#content [id^="q_"]').filter('[class*="question-num"]');
	numqs.each(function() {
		input = $(this).find(":text,textarea").filter(":first");
		if (input.size() == 1) {
			if($(this).attr('class').indexOf('abstract') < 0
					&& input.attr('value') != ""){
				numStore += $(this).attr('id') + "###" + input.val() + ";"
			}
		}
	});
	// all text questions
	txtqs = $('#content [id^="q_"]').filter('[class*="question-text"]');
	txtqs.each(function() {
		input = $(this).find(":text,textarea").filter(":first");
		if (input.size() == 1) {
			if($(this).attr('class').indexOf('abstract') < 0
					&& input.attr('value') != ""
					&& input.attr('value') != " "){
				txtStore += $(this).attr('id') + "###" + input.val() + ";"
			}
		}
	});
	// all date questions
	datqs = $('#content [id^="q_"]').filter('[class*="question-date"]');
	datqs.each(function() {
		input = $(this).find(":text,textarea").filter(":first");
		if (input.size() == 1) {
			if($(this).attr('class').indexOf('abstract') < 0
					&& input.attr('value') != ""
					&& input.attr('value') != " "){
				datStore += $(this).attr('id') + "###" + input.val() + ";"
			}
		}
	});
	
	/* Now the dialog is checkedradio button / checkbox input
	 */
	// "^=" means begins with, jquery expression
	question = clickedItem.closest('[id^="q_"]');
	unknown = clickedItem.closest('[id^="i_unknown"]');
	 
	// if unknown has been clicked, it is primarily saved
	if(clickedItem.attr('id').indexOf('i_unknown') >= 0){
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

	/* Check, whether there have been values for num/text/date fields
	 * if not so, add an " " to enable better array splitting when 
	 * getting the values later 
	 */ 
	if(numStore==""){
			numStore = " ";
	} 
	if(txtStore==""){
		txtStore = " ";
	}
	if(datStore==""){
		datStore = " ";
	}
	// assemble complete text/textinput value store
	store += numStore + "&&&&" + txtStore + "&&&&" + datStore;
	
	/* AJAX call to send both the value store as well as a potential 
	 * single radio/checkbox input
	 */
	d3web_addfactsRemembering(store, question.attr('id'), pos);
}


/**
 * Transfer a selected or removed fact to the d3web dialog via an ajax call.
 * This is a special function for sending a whole store of "remembered"
 * answers, e.g., num answers that have not yet been confirmed
 */
function d3web_addfactsRemembering(store, qid, pos) {
	
	var link = 
		$.query.set("action", "addFact").set("qid", qid).set("pos", pos).set("store", store).toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			//d3web_nextform();

		//if (html != "same") {			// replace target id of content if not the same
			window.location.reload();
			d3web_init();
		//}
		}
	});
}


/**
 * Function for resetting a d3web Session, i.e., for creating a new session
 * and thus starting a new problem solving case/session.
 * */
function d3web_resetSession() {
	var link = $.query.set("action", "show").set("reset", "true").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		url : link,
		success : function(html) {
			window.location.reload(true);
			d3web_init();
		}
	});
}


/**
 * Function called when finally saving a d3web case.
 */
function d3web_saveCase(id, event) {

	// get number of unanswered questions
	questionsAll = $('#content [id^="q_"]');
	questionsAnswered = questionsAll.filter('[class$="question-d"]');
	questionsUnanswered = questionsAll.size() - questionsAnswered.size();

	// display popup TODO
	alert("Es sind noch " + questionsUnanswered + " Fragen unbeantwortet.");
	
	var link = 
		$.query.set("action", "savecase").toString();
	link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		url : link,
		success : function(html) {
			//d3web_nextform();

		//if (html != "same") {			// replace target id of content if not the same
			window.location.reload();
			d3web_init();
		//}
		}
	});

	/*var sendexit_button = $(id);

	var divHTML = '<b>Test:</b><br />';
	divHTML = divHTML + ('Es sind noch ' + questionsUnanswered + 'Fragen im Dialog unbeantwortet.');
	divHTML = divHTML + ('Um diese noch zu beantworten, klicken Sie auf \'Zurück zum Dialog\'.');
	divHTML = divHTML + ('Andernfalls klicken Sie auf \'Daten abschicken\' <br><br>');

	var send_popup = $("#send_popup");
	// try to get existing popup window, else create it
	if (send_popup.size() == 0) {
		$("body").append('<div id="send_popup"></div>');
		send_popup = $("#send_popup");
	}
	// Set the basic HTML
	send_popup.attr('innerHTML', divHTML);

	send_popup.position({
		"my" : "left top",  // which position of popup to align to target
		"at" : "left top", // which position of target popup is aligned to
		"of" : "#sendexit",
		"offset" : "0 0",
		"collision" : "fit flip",
		"bgiframe" : false
	});

	send_popup.show(0);

	// show div
	send_popup.fadeIn(250);*/
}

function d3web_loadCase(id, event) {
	
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
	//var link = $.query.set("action", "show").toString();
	
	// assemble the new link; replace the parts of the query string after ? with
	// nothing ("") and add the action nextform string part. result e.g.:
	// http://localhost:8080/d3web-ProKEt/D3webDialog?src=HeartMed2010&action=nextform
	//link = window.location.href.replace(window.location.search, "") + link;

	$.ajax({
		type : "GET",
		async : false,
		//url : link,
		success : function(html) {			// compare resulting html to former
			if (html != "same") {			// replace target id of content if not the same
				window.location.reload();
				d3web_init();
			}
		}
	});
	
	//d3web_show_solutions("right"); // show solutions
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
//			d3web_nextform();
		}
	});
}

/**
 * Transfer children from d3web.
 */
function d3web_getChildren(pid) {
	var link = $.query.set("action", "getChildren").set("pid", pid)
			.toString();
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
			for(i = 0; i < elements.length; i++){
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
