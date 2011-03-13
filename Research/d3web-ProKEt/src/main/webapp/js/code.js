/* Some global variables first */
var clariPopupShowing = false;			// stores flag whether popup in clarification dialog is currently showing
var lastPopup;							// storage for the question-id of the last displayed popup
var questionnaires;						// stores the questionnaires
var questionsDirty;						// stores ALL questions
var questions;							// stores questiona without zero choice questions
var markingQuestions;			
var building = true;					// flag, whether JS is currently still liading
var freshlyDoneQ;						// the last, successfully processed question
var currentQ;							// the currently active question
var renderCurrentDone = true;			// flag if current and done are rendered
var tooltipShown = undefined;			// stores the currently shown tooltip
var alternatingColors = false;		
var showAllQuestionnaires = false;		// flag whether ALL questionnaires should be visible
var stopMarking = false;
var initialization = true;

/****************************************************************************
* FUNCTIONS needed for the basic initial setting up of all js functionality *
* e.g., marking questions (normal case) , making navigaion etc.             *
****************************************************************************/


function setup() {
	
	// load questionnaires and questions into memory
	questionnaires = $('#content [id^="qu_"]');
	questionsDirty = $('#content [id^="q_"]');
	
	// clean question set of zero choice questions
	questions = questionsDirty.filter(':not([id$="-trigger"])').filter(
			function(index) {
				return !$(this).hasClass("question-zc");
			}
	);
	
	// TODO sind das alle oc etc die OHNE button Druck gehen müssen?!
	markingQuestions = questions.filter(function(index) {
		return $(this).find(".ok-button").size() == 0;
	});

	// setup popup stuff
	// each time the document is clicked somewhere
	$(document).click(function() {	
		if (!clariPopupShowing) { // TODO try out 
			hide_popup(250); // fade out popup in 250 ms
		}
		clariPopupShowing = false;	
	});
	hide_popup(0); // initially hiding popup per default

	// if alternating colors flag set, e.g., for Hierarchy dialog
	if (alternatingColors) {
		alternating_colors();	// create alternating colors
	}

	// set numeric separator for num-questions
	$(".answer-num").numeric(".");
	
	// in case of dateanswers
	if (dateanswer) {
		
		// call remark_selectively if date is chosen
		$(".answer-date").find(":text").datepick({
			onSelect : function(dates) {
				remark_selectively($(this));
			}
		});
	}

	prepare_question_marking(); // prepare marking of questions
	hide_all_tooltips(); // hide tooltips
	generate_tooltip_functions(); // generate tooltip functions
	hide_all_subquestions(); // hide subquestions
	make_sidenav();	// make sidenavigation
	
	// if not d3web and not explicitly all questionnaires are
	// defined to be shown 
	// TODO define d3web stuff
	if (!d3web && !showAllQuestionnaires) {
		show_first_questionnaire();
	}
	highlight_sidenav();	// style sidenav 
}

/**
 * Scroll to the top of the page by setting jquery attribute 
 * scrollTop to 0
 */
function scroll_up() {
	$("html:not(:animated),body:not(:animated)").animate({
		scrollTop : 0
	}, 450);
}

/**
 * Show everything
 */
function show_everything() {
	$("*").show(0);
}

/**
 * Simulates sending the complete form, i.e. renders all questions
 * as done
 * In d3web, all questions would have to be parsed and if no answer
 * is provided, answer unknown should be set
 */
function sendall(){
	
	// check oc questions whether answered
	questions.find(":input:not(:checked)").each(function() {
		
		//alert($(this).attr("id"));
		// get the element with title "unbekannt" and set
		// it selected
		if($(this).attr("title")=="unbekannt"){		
			$(this).attr('checked', true);
		}
	});
	
	questions.find("select").each(function(){
		if($(this).attr("value") == "-Antworten-"){
			$(this).attr(
				{
					"value" :  "unbekannt"
				}
			);
		}
	});
	
	mark_done();
}

/**
 * Hide all the questionnaires
 */
function hide_all_questionnaires() {
	questionnaires.each(function() {
		$(this).hide(0);
	});
}

/**
 * Hide all the questions
 */
function hide_all_questions() {
	questions.each(function() {
		$(this).hide(0);
	});
}

/**
 * Show the first questionnaire that is not hidden
 */
function show_first_questionnaire() {
	var id = questionnaires.filter(":not([title='hidden-questionnaire'])")
			.filter(":first").attr('id');
	if (id != undefined) {
		show_questionnaire(id);
	}
}

/**
 * Set highlighting styles for sidenav
 */
function highlight_sidenav() {
	items = $("[class*='navigation-item']");
	items
			.each(function() {
				
				// if the object that corresponds to the navigation item
				// is already answered also mark navigation item as answered
				if (parseDone($("#" + $(this).attr('id').replace(/nav-/, "")),
						"true")) {
					// set to navigation-done
					$(this).addClass("navigation-item-d");
				} else {
					// set to navigation not-yet-done
					$(this).removeClass("navigation-item-d");
				}
			});
}

/**
 * Show a questionnaire by id
 */
function show_questionnaire(id) {
	hide_all_questionnaires();	// hide all questionnaires
	target = $("#" + id);		// get target element
	target.parents().show(0);   // ensure visibility: check parents' visibility
	hide_all_questions();		// hide all the questions
	target.siblings("[id^='qu_']").hide(0);	// hide all siblings of the questionnaire
	target.find("[id^='q_']").show(0);	// show all questions of the questionnaire
	target.fadeIn(700);			// fade it in again
	target.find("[id^='qu_']").hide(0);	// hide all questionnaires beneath

	// highlight current-marker from all existing navigation items
	$(".navigation-item").removeClass("navigation-item-c");	
	navTarget = $("#nav-" + id);	// get navigation item
	navTarget.addClass("navigation-item-c"); // add current marker to actual item
	// show element by opening parent node
	var parent = navTarget;
	do {
		parent = jQuery.jstree._reference($("#navigation-list"))._get_parent(
				parent);
		$("#navigation-list").jstree("open_node", parent);
	} while (parent !== -1);
	highlight_sidenav();	// highlight side-navigation accordingly
	scroll_up();			// scroll to page top
	mark_current();			// mark currently active element
}

/**
 * Construct the side navigation by converting the existing navigation
 * list into a jquery jstree
 */
function make_sidenav() {
	// convert navigation-list to tree with jquery plugin
	$("#navigation-list").jstree({
		"plugins" : [ "themes", "html_data" ],

		"themes" : {
			"theme" : "default", // or apple
			"dots" : true,
			"icons" : true
		}
	});
}

/**
 * Style parent question of a given object
 * @param object the object the parent of which is styled
 * @param suffix the suffix
 * @param blockingSuffix the blocking suffix
 * @param removeSuffix the suffix to be removed
 */
function styleParentQ(object, suffix, blockingSuffix, removeSuffix) {
	// searching parent
	var parent = object.closest('[id^="q_"]');
	// set style
	style(parent, suffix, blockingSuffix, removeSuffix, true);
}

/**
 * Style object by adding a suffixed class
 * @param object the object to style
 * @param suffix the attribute to be added
 * @param blockingSuffix the inside character
 * @param removeSuffix the attribute to be removed
 * @param recurse recursion flag
 */
function style(object, suffix, blockingSuffix, removeSuffix, recurse) {
	
	// if currently active and done questions are rendered
	if (renderCurrentDone) {
		if (object == undefined) {
			return;
		}
		var nowClassString = object.attr('class'); // actual class string
		if (nowClassString == undefined) {
			return;
		}
		var nowClassArray = nowClassString.split(" ");	// class string split up
		for ( var i = 0; i < nowClassArray.length; i++) {
			var nowClass = nowClassArray[i]; // get one class part after another
			var newClass = nowClass;		 // copy
			// if suffix to remove is part of currently processed class part
			if ((nowClass.substring(nowClass.length - 2, nowClass.length)) == '-'
					+ removeSuffix) {
				// remove the suffix
				newClass = nowClass.substring(0, nowClass.length - 2);
			}

			if (nowClass.length > 0) {
				// if nowClass is not already styled with new attribute or the blocking suffix
				if ((nowClass.substring(nowClass.length - 2, nowClass.length) !== '-'
						+ suffix)
						&& (nowClass.substring(nowClass.length - 2,
								nowClass.length) !== '-' + blockingSuffix)) {
					// add new styling
					object.addClass(newClass + '-' + suffix);
				}
			}
		}

		if (recurse) { // if recursion is desired
			object.find(
					// find all answers, g??, solutions, ??, tooltips
					'[id^="a_"],[id^="g_"],[id^="s_"],[id^="t-"],[id^="tt-"]')
					.each(
							function() {
								// recursive styling, only one time, then recursion stops
								style($(this), suffix, blockingSuffix,
										removeSuffix, false);
							});
		}
	}
}

/**
 * remove special style (-c/-d) of parent question
 */
function destyleParentQ(object) {
	destyle(object.closest('[id^="q_"]'));
}

/**
 * Remove current/done marks of a given object
 * @param object
 */
function destyle(object) {
	if (renderCurrentDone) { // current and done are rendered
		var nowClassString = object.attr('class');	// get current classstring
		if (nowClassString !== undefined) {
			var nowClassArray = nowClassString.split(" ");	// split string parts
			for ( var i = 0; i < nowClassArray.length; i++) {
				var nowClass = nowClassArray[i];			// if any of those parts is
				if (endsWith(nowClass, '-c') || endsWith(nowClass, '-d')) {
					object.removeClass(nowClass); // -c/-d, remove this class
				}
			}
		}
		// recursion for all children of this object
		object.children().each(function() {
			destyle($(this));
		});
	}
}

/**
 * Check whether a String ends with a given suffix
 * and a line/String ending ($)
 * @param fullString the String to test
 * @param testString the suffix
 * @returns {Boolean} TRUE if it ends with, FALSE otherwise
 */
function endsWith(fullString, testString) {
	var regexp = new RegExp(testString + "$");
	if (regexp.test(fullString))
		return true;
	else
		return false;
}

/**
 * Hide all the tooltip elements, starting with an "tt-"
 */
function hide_all_tooltips() {
	$("[id^='tt-']").hide(0);
	tooltipShown = undefined;
	tooltipShownTrigger = undefined;
}

/**
 * Hide all the sub-questions, i.e., elements starting with sub-
 */
function hide_all_subquestions() {
	$("[id^='sub-']").hide(0);
}

/**
 * If not initially setting up (building) the JS code, mark given 
 * object as done.
 * 
 * @param object Object to be marked
 */
function remark_selectively(object) {
	if (!building) {
		mark_object_done(object);
	}
}

/**
 * Checks, whether a given object is already processed (set to done)
 * @param object the checked object
 * @param options 
 * @returns {Boolean} true if object is processed
 */
function parseDone(object, options) {
	
	var result = true;
	// question itself?
	if (object.attr('id') != undefined && object.attr('id').indexOf("q_") == 0) {
		if (!object.hasClass("question-d")) {
			result = false;
		}
	} else {
		if (object.find("[id^='q_']").filter(function() {
			return !($(this).hasClass("question-d"));
		}).size() > 0) {
			result = false;
		}
	}

	if (options == "false") {
		result = !result;
	}
	return result;
}

/**
 * Remove all done-marks of done questions
 */
function unmark_done() {
	$("#content [class$='-d']").each(function() {
		destyle($(this));
	});
}

/**
 * Mark all active question(s)
 */
function mark_current() {
	
	if (renderCurrentDone && !building) {
		var found = false;
	
		// get all questions NOT currently done and still visible
		var temp = questions.filter(":not([class$='-d'])").filter(":visible");

		// if there is a freshly done question, filter it out
		if (freshlyDoneQ != undefined) {
			temp = temp.filter("[id!='" + freshlyDoneQ.attr('id') + "']");
		}
		
		// get the next first question 
		temp.filter(":first").each(function() {
			
			
			var id = $(this).attr('id');
			
			// destyle the previous currentQuestion
				if (currentQ != undefined) {
					destyle(currentQ);
				}
				// style next current question in row (next first) new
				style($(this), "c", "-", "d", true);
				currentQ = $(this);
				var input = $(this).find(":input").first();
				input.focus();
			

			// style freshly done question from c to d
			if (freshlyDoneQ != undefined) {
				style(freshlyDoneQ, "d", "", "c", true);
				freshlyDoneQ = undefined;
			}

			found = true;
		});

		// no appropriate element for styling as current was found
		if (!found) {
			// style freshly done question from c to d
			if (freshlyDoneQ != undefined) {
				style(freshlyDoneQ, "d", "", "c", true);
				freshlyDoneQ = undefined; // reset currently freshly done
			} else {
				if (currentQ != undefined) {
					destyle(currentQ); // remove style of current q
				}
			}
			currentQ = undefined;
		}
	}
}

/**
 * Mark ALL questions that are already answered
 */
function mark_done() {
	
	// when current and done are rendered and no setting up process running
	if (renderCurrentDone && !building) {
		
		// radio: find checked radio buttons and mark parent as done
		markingQuestions.find(':radio:checked').each(function() {
			styleParentQ($(this), "d", "-", "c");
		});

		// checkbox: find checked checkbox and mark parent as done
		markingQuestions.find(':checkbox:checked').each(function() {
			styleParentQ($(this), "d", "-", "c");
		});

		// text/input fields: find text and mark parent as done
		markingQuestions.find(':text').each(function() {
			if (this.value.length !== 0 && this.value !== '--,--') {
				styleParentQ($(this), "d", "-", "c");
			}
		});

		// textarea: find textarea with value and mark parent as done
		markingQuestions.find('textarea').each(function() {
			if (this.value.length !== 0) {
				styleParentQ($(this), "d", "-", "c");
			}
		});

		// lists: find all not non-selected lists and mark parent as done 
		//markingQuestions.find('select').each(function() {
			//if ($(this).selectedIndex != -1) {
				//styleParentQ($(this), "d", "-", "c");
			//}
		//});
		
		markingQuestions.find('select').each(function() {
			if ($(this).attr("value") != "-Antworten-") {
				styleParentQ($(this), "d", "-", "c");
			}
		});

		// "labels"
		markingQuestions.find("[id^='f_a_']").each(function() {
			if ($(this).hasClass('selected')) {
				styleParentQ($(this), "d", "-", "c");
			}
		});
	}
}

/**
 * Mark a specific object as done / answered
 * @param object the object to be marked
 */
function mark_object_done(object) {
	
	// if rendering of current & done is defined and no building/setting up
	if (renderCurrentDone && !building) {

		// the closest parent of the given object
		var parent = object.closest('[id^="q_"]');
		var finished = false;

		// get children, i.e. answers/text
		parent.find('[id^="f_"]').each(
				function() {
					if (finished) {
						return;
					}

					// radio: found a checked, set parent as the
					// freshliest done question and finish
					if ($(this).attr('checked')) {
						freshlyDoneQ = parent;
						finished = true;
						$(this).blur();
					}
					if (finished) {
						return;
					}

					// text: found some text, set parent as the 
					// freshliest done question and finish
					if ($(this).attr('type') == 'text'
							|| $(this).attr('nodeName') == 'TEXTAREA') {
						if ($(this).val().length !== 0
								&& $(this).val() !== '--,--') {
							freshlyDoneQ = parent;
							finished = true;
							$(this).blur();
						}
					}
					if (finished) {
						return;
					}

					// "labels": the same for labels
					if ($(this).hasClass('selected')) {
						freshlyDoneQ = parent;
						finished = true;
						$(this).blur();
					}
					if (finished) {
						return;
					}
					
					
					// select boxes:
					if ($(this).attr('value')!="-Antworten-") {
						freshlyDoneQ = parent;
						finished = true;
						$(this).blur();
					}
					if (finished) {
						return;
					}
					
				});

		// no answer or else found, so destyle the parent 
		// as it shouldn't be marked done
		if (!finished) {
			destyle(parent);
		}

		// update current active question
		mark_current();
	}
}

/**
 * Fades out the currently displayed popup in the given time.
 * @param time the time the popup should take to fade out
 */
function hide_popup(time) {
	$("#__popup").fadeOut(time);
	lastPopup = null;
}

/**
 * Prepares the marking of questions in binding the remark_selectively function
 * to necessary elements and events 
 */
function prepare_question_marking() {
	
	// bind remark_selectively function to all OK buttons
	$(".ok-button").unbind("click").bind("click", function() {
		remark_selectively($(this));
	});

	// bind input fields on keyup event to remarking function
	markingQuestions.find('[type=radio]').click(function() {
		remark_selectively($(this));
	});
	
	// and also input fields on confirm button click 
	markingQuestions.find('[type=text]').click(function() {
		var thisEl = $(this);
		thisEl.focus();
		thisEl.bind('keydown', function(e) {
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				remark_selectively(thisEl);
			}
		});
		
		//alert(currentQ.find(":input").first().attr("id"));
		//currentQ.find(":input").first().blur();
		//$(this).focus();
		//remark_selectively($(this));
		//currentQ.find(":input").filter(":first").focus();
	});
	
	markingQuestions.find('[type=text]').keydown(function(e) {
		var thisEl = $(this);
		thisEl.focus();
		
			var code = (e.keyCode ? e.keyCode : e.which);
			if(code == 13) {
				remark_selectively(thisEl);
			}
		
	});
	
	markingQuestions.find("select").bind("change", function(){
		remark_selectively($(this));
		//styleParentQ($(this), "d", "-", "c");
		//$(this).blur();
		//currentQ.focus();
	});
}

/**
 * Defines actions when a tooltip is triggered for an element 
 * 
 * @param id
 * @param element
 */
function tooltip_over(id, element) {
	
	// get target element
	target = $("#tt-" + id).filter(":not(:animated)");

	if (target.size() == 0) {
		return;
	}
	// if target element is not currently shown
	if (target !== tooltipShown) {
		
		// get the triggering element
		targetTrigger = $("." + id + "-tt-trigger");

		// hide old tooltip if existing
		if (tooltipShown !== undefined) {
			tooltip_out(tooltipShown);
		}
		
		// store currently shown tooltip and tooltipShownTrigger
		tooltipShown = target;
		// REM tooltipShownTrigger = targetTrigger;

		target.css({
			position : "absolute"
		});
		tooltip_move(element);

		// show for 500 ms if not moved anymore
		target.show(500);
	}
}

/**
 * Define tooltip's position relative to calling element
 * @param e
 */
function tooltip_move(e) {
	if (tooltipShown != undefined) {
		tooltipShown.position({
			"my" : "left top",
			"at" : "right bottom",
			"of" : e,
			"offset" : "15 15",
			"collision" : "fit flip",
			"bgiframe" : false
		});
	}
}

/**
 * Hide the tooltip
 * 
 * @param object
 */
function tooltip_out(object) {
	
	// if a jquery tooltip or
	if (object instanceof jQuery) {
		target = object;
	} else {
		
		// a specifically marked element
		target = $("#tt-" + object);
	}

	target.hide(500);
	tooltipShown = undefined;
	//tooltipShownTrigger = undefined;
}

/**
 * Generate functionality for tooltip elements
 */
function generate_tooltip_functions() {
	
	// get all triggering elements
	triggers = $("[class*='-tt-trigger']");
	
	// if mouse is moved over an element define potential tooltips position
	$(document).mousemove(function(e) {
		tooltip_move(e);
	});
	
	// go through all existing tooltip triggers
	triggers.each(function() {
		
		// TODO try this out for curiosity
		var classes = /(\w*)-tt-trigger/;	// regex
		var result = classes.exec($(this).attr('class'));
		var id = result[1];	// id of the tooltip trigger
		$(this).mouseover(function(e) {
			tooltip_over(id, e);
		});
		$(this).mouseout(function() {
			tooltip_out(id);
		});
	});
}


/**********************************************************
* FUNCTIONS added to StringTemplates or JS CodeContainers *
**********************************************************/

/*** BASIC STUFF ***/

/**
 * IN Dialog.st for adding functionality to the "next" button
 * Gets and show next questionnaire
 */
function next_questionnaire() {
	// get the inner-most visible questionnaire in hierarchy
	var visibleQuestionnaire = questionnaires.filter(":visible:last");

	// get the next Questionnaire by reducing the set of all 
	// questionnaires with jquery .eq to the one at the next index
	// after the previous one
	var pos = questionnaires.index(visibleQuestionnaire);
	var nextQuestionnaire = questionnaires.eq(pos + 1);

	// show this next questionnaire
	if (nextQuestionnaire.attr('id') !== undefined) {
		show_questionnaire(nextQuestionnaire.attr('id'));
	}
}

/**
 * IN DialogRenderer/D3webDialogRenderer
 * 
 * Function for resetting current marking, i.e., 
 * unmark currently done, mark currently done new,
 * mark current questions new
 */
function remark() {
	if (!building) {
		unmark_done();
		mark_done();
		mark_current();
	}
}


/*** IMAGE QUESTIONS ***/

/** 
 * IN ImageAnswer.st 
 * TODO find out what happens exactly here
 * Defines the action after an image area was clicked
 * @param id ID of the image area clicked
 */
function click_box(id) {
	var target = $("#" + id);	// get the clicked element
	var selected = target.find(":input:checked");	// find clicked input 
	var deselected = target.find(":input:not(:checked)"); // find not clicked inputs
	selected.attr('checked', false);
	deselected.attr('checked', true);
	remark_selectively(target);	// mark question accordingly
}


/*** HIERARCHY ***/

/**
 * IN HierarchySolution.st and HierarchyAnswer.st
 * Toggle subquestions and subanswers of an element with given ID
 * meaning if element is shown, it will be hidden, if its hidden, it
 * will be shown
 */
function toggle_sub(id) {
	
	// TODO get d3web-related stuff out here
	if (d3web) {
		d3web_getChildren(id);
	}
	toggle_hide("sub-" + id); // toggle sub-elements
	toggle_folder_image(id); // toggle folder image
	hide_all_tooltips();	// on toggling all tooltips should disappear
}

/**
 * toggle element's visibility by ID
 */
function toggle_hide(id) {
	
	// toggle (0 means no animation as time=0 for animation and on
	// callback (animation complete) the function is processed
	$("#" + id).toggle(0, function() {
		alternating_colors();	// update alternating color scheme
		checkAnswers();			// check on answers
	});
}

/**
 * Generate alternating colors for all elements
 */
function alternating_colors() {
	$(function() {
		
		// counts the elements level
		var pos = 0;
		$(		// select all questions, answers, solutions visible
				"div[id^='t-q_']:visible,div[id^='t-a_']:visible,div[id^='t-s_']:visible")
				.each(
						// set coloring style accordingly
						function() {
							if (pos % 2 !== 1) {
								$(this).removeClass("color-even").addClass(
										"color-odd");
							} else {
								$(this).removeClass("color-odd").addClass(
										"color-even");
							}
							pos = pos + 1;
						}
					);
	});
}

/**
 * Retrieves all answers that are currently visible
 * and remvoes coloring styles (alternating)
 * TODO try out if it is reasonable
 */
function checkAnswers() {
	$(function() {
		var pos = 0;
		
		// for each visible answer element remove 
		// coloring scheme
		$("div[id^='a_']:visible").each(function() {
			if ($(this).hasClass("color-odd")) {
				$(this).removeClass("color-odd");
			}
			if ($(this).hasClass("color-even")) {
				$(this).removeClass("color-even");
			}
		});
	});
}

/**
 * Toggle folder image (open/close) (needed in hierarchy dialog)
 */
function toggle_folder_image(id) {
	var target = $("#" + id);
	if (target.attr('class').indexOf("question-open") != -1) {
		target.removeClass("question-open").addClass("question-closed");
	} else if (target.attr('class').indexOf("question-closed") != -1) {
		target.removeClass("question-closed").addClass("question-open");
	} else if (target.attr('class').indexOf("answer-open") != -1) {
		target.removeClass("answer-open").addClass("answer-closed");
	} else if (target.attr('class').indexOf("answer-closed") != -1) {
		target.removeClass("answer-closed").addClass("answer-open");
	}

}

/**
 * Toggle folder image (open/close) (needed in hierarchy dialog)
 */
function toggle_folder_image(id) {
	var target = $("#" + id);
	if (target.attr('class').indexOf("question-open") != -1) {
		target.removeClass("question-open").addClass("question-closed");
	} else if (target.attr('class').indexOf("question-closed") != -1) {
		target.removeClass("question-closed").addClass("question-open");
	} else if (target.attr('class').indexOf("answer-open") != -1) {
		target.removeClass("answer-open").addClass("answer-closed");
	} else if (target.attr('class').indexOf("answer-closed") != -1) {
		target.removeClass("answer-closed").addClass("answer-open");
	}

}


/*** LEGAL STYLE ***/

/**
 * IN ColorHierarchyMCQuestion.st or LegalQuestion.st
 * Toggle the subelements for special hierarchical dialogs
 * @param id
 */
function toggle_sub_4boxes(id) {
	
	// get d3web stuff out of here
	if (d3web) {
		d3web_getChildren(id);
		/*
		 * get new ratings, they changed at least for the element we are looking
		 * at
		 */
		var ids = "";
		$("[id^='q_']").each(function() {
			ids = ids + $(this).attr('id') + ",";
		});
		d3web_getRatings(ids);
	}
	toggle_hide("sub-" + id); 
	toggle_folder_image_4boxes(id);
	hide_all_tooltips();
}

/**
 * Toggle folder image (open/close) for the legal dialog style
 */
function toggle_folder_image_4boxes(id) {
	
	var count = id.replace("q_", "");
	var typeimgID = count+'-typeimg';
	
	//var temp = $("#" + id + "-folderimg");
	var temp2 = $("[id*="+ typeimgID + "]");
	
	/*if (temp.attr('src') !== 'img/closedArrow.png') {
		temp.attr('src', 'img/closedArrow.png');
	} else {
		temp.attr('src', 'img/openedArrow.gif');
	}*/
	
	//alert(temp2.attr('id'));
	/*if(temp2.attr('src') == 'img/transpSquare.png'){
		temp2.attr('src', 'img/transpSquare.png');
	} else {

		alert(temp2.attr('src'));
		if (temp2.attr('src') == 'img/closedArrow.png') {
			temp2.attr('src', 'img/openedArrow.gif');
		} else if (temp2.attr('src') == 'img/openedArrow.gif'){		
			temp2.attr('src', 'img/closedArrow.png');
		} else {
			temp2.attr('src', 'img/transpSquare.png');
		}
	}*/
}

/**
 * IN ColorHIerarchMcQuestion.st or LegalQuestion.st
 * Color background and mark selected answer in Legal style dialogs
 * TODO factor out d3web
 * @param id the id of the item to mark
 * @param value the value that indicates in what regard the item is marked
 */
function h4boxes(value, id) {
	if (!d3web) {
		
		// get dialog item
		var item = $("#" + id);
		
		// set image attribute to the correctly selected one
		item.attr('src', "img/panel" + value + ".gif");
		
		// get the first ancestor, i.e. the first upper question
		var target = $(item).closest("div[id^='q_']");

		// remove existing classes
		target.removeClass('rating-low rating-medium rating-high');

		// set classes new according to given value
		switch (value) {
		case "1": // yes
			target.addClass('rating-high');
			break;
		case "2": // no
			target.addClass('rating-low');
			break;
		case "3": // undecided
			target.addClass('rating-medium');
			break;
		case "4": // nothing, default val, undecided
			break;
		}
		// also mark parents of the target while excluding target
		h4boxes_mark(target, true);
		
	} else {
		// d3web specific toolchain
		if (value < 4) {
			
			// add fact in d3web
			d3web_addfact(id, value - 1); // zero-based
		} else {
			// set default empty fact
			d3web_addfact(id, "[empty]");
		}
		// get new ratings, changed at least for the element we are looking at
		var ids = "";
		$("[id^='q_']").each(function() {
			ids = ids + $(this).attr('id') + ",";
		});
		d3web_getRatings(ids);
	}
}

/**
 * Transfer coloring (in hierarchy dialog) also to parent quesitons
 * @param object the object from where to start marking parents
 * @parents skip_self flag indicating whether element itself should
 * 		also be processed
 */
function h4boxes_mark(object, skip_self) {

	// TODO refactor algorithm and mapping between 0-3 and image 1-4 numbers
	// check object itself unless skip_self
	if (!skip_self) {
		
		// we need this as a coloring flag
		var oc = object.hasClass('oc');
		var color; 
		
		if (oc) {	// in oc questions, rating default for parents
					// is always low
			color = 3;
		} else { // otherwise it is high (green) per default
			color = 0;
		}

		// for each of the children questions
		$("#sub-" + object.attr('id')).children("div[id^='q_']").each(
				function() {
					
					// if child is rated medium
					if ($(this).hasClass("rating-medium")) {
						
						// if higher rating and mc question
						if (color < 2 && !oc) {
							color = 2; // set medium rating
						}
						// if lower rating and oc question
						// leave medium
						if (color > 2 && oc) {
							color = 2;
						}
						
					// if child is rated low	
					} else if ($(this).hasClass("rating-low")) {
						
						if (color < 3 && !oc) {
							color = 3;
						}
					
					} else if ($(this).hasClass("rating-high")) {
						if (color > 0 && oc) {
							color = 0;
						}
					} else {
						// set color to transparent in case of undecided questions
						// if ($(this).hasClass("question-unanswered")) {
						if (color < 1 && !oc) {
							color = 1;
						}
						if (color > 1 && oc) {
							color = 1;
						}
					}
				});

		// retrieve target element and target image
		var target = $("#" + $(object).attr('id'));
		var imgTarget = $("#panel-" + $(object).attr('id'));
		// remove old classes
		target.removeClass('rating-low rating-medium rating-high');
		// set new class
		switch (color) {
		case 0: // green
			target.addClass("rating-high");
			imgTarget.attr('src', "img/panel1.gif");
			break;
		case 1: // transparent
			imgTarget.attr('src', "img/panel4.gif");
			break;
		case 2: // yellow
			target.addClass("rating-medium");
			imgTarget.attr('src', "img/panel3.gif");
			break;
		case 3: // red
			target.addClass("rating-low");
			imgTarget.attr('src', "img/panel2.gif");
			break;
		}
	}

	// get first parent div
	var walking = $(object).parent("div:first");
	var reg = new RegExp(/^sub-.*$/);
	var counter = 0;
	
	while (!reg.test($(walking).attr('id'))) {
		counter += 1;
		if (counter > 6) // why 6?! what about larger dialogs?! TODO
			break; // break if there is no more parent question
		walking = $(walking).parent("div");
	}

	// get first parent of element and call 
	// recursively, also resetting the parents coloring
	$(walking).parent(":first").each(function() {
		h4boxes_mark($(this), false);
	});
}

/**
 * IN ColorHierarchyMCQuestion.st and LegalQuestion.st
 * @param sourceId the ID of the source element
 * @param destId the ID of the destination elmenet
 */
function copy_div(sourceId, destId) {
	
	// get content, which is the innerHTML of the source
	var newContent = $("#" + sourceId).attr('innerHTML');
	var dest = $("#" + destId);
	if (newContent.length == 0) {
		dest.hide(0); // if nothing to copy, hide new element
	} else {
		// else set content to new element and show it animated
		dest.attr('innerHTML', newContent).show(1000);
	}
}


/*** CLARIFICATION STYLE ***/

/**
 * IN ClarificationMCAnswer.st and EulenspiegelMCAnswer.st
 * Toggle the selection state of the clarification dialogs flat answers
 * @param id the ID of the element to toggle
 */
function flat_button_toggle(id) {
	
	// get target element
	var target = $("#f_" + id);
	
	// check if oc question
	var oc = target.closest("div[id^='q_']").hasClass("question-oc")
			|| target.parent().hasClass("oc");
	
	// check selection state
	var selected = target.hasClass("selected");

	// Toggle selection: if selected, deselect, otherwise vice versa
	if (selected) {
		class_deselect(target);
	} else {
		
		// standard coloring case
		target.removeClass('not-selected').addClass('selected');
		
		// check if currently activ and not selected
		if (target.hasClass('not-selected-c')) {
			target.removeClass('not-selected-c').addClass('selected-c');
		}
		
		// check if currently done and not selected
		if (target.hasClass('not-selected-d')) {
			target.removeClass('not-selected-d').addClass('selected-d');
		}
	}

	// oc questions: deselect all other answer alternatives
	var parent = target.closest("div[id^='q_']");
	if (oc) {
		var children = parent.find("div[id^='f_a_']");
		children.each(function() {
			if ($(this).attr('id') != target.attr('id')) {
				class_deselect($(this));
			}
		});
	}

	// do the color-marking
	remark_selectively(target);
}

/**
 * Set a target element to deselected
 * @param target the target element
 */
function class_deselect(target) {
	target.removeClass('selected').addClass('not-selected');
	if (target.hasClass('selected-c')) {
		target.removeClass('selected-c').addClass('not-selected-c');
	}
	if (target.hasClass('selected-d')) {
		target.removeClass('selected-d').addClass('not-selected-d');
	}
}

/**
 * Create the additional-answers popup in clarification dialog
 * (or Eulenspiegel)
 * @param question_id the ID of the question that needs the popup
 * @param e the clicked-evebt
 */
function show_clarification_popup(question_id, e) {
	var counter = 0;

	if (lastPopup != question_id) {
		var divHTML = '<b>Weitere Optionen:</b><br />';
		var popup = $("#__popup");
		// try to get existing popup window, else create it
		if (popup.size() == 0) {
			$("body").append('<div id="__popup"></div>');
			popup = $("#__popup");
		}
		// Set the basic HTML
		popup.attr('innerHTML', divHTML);

		// find answers to show in popup, i.e., currently (by XML) hidden answers
		var hiddenAnswers = $("#" + question_id).find("div[id^='a_']:hidden");

		if (hiddenAnswers.size() > 0) {
			var divWidth = 0; // determine width (for IE)
			hiddenAnswers.each(function() {
				// fix to make selectable
				var thisWidth = $(this).width();
				divWidth = Math.max(divWidth, thisWidth);
			});

			// make the div
			hiddenAnswers.each(function() {
				var result = $(this).clone(false); // remove handlers and data
				result.unbind();
				result.attr('onclick', ''); // remove click events
				answer_id = $(this).attr('id');
				jQuery.data(result, 'targetId', answer_id); // add answer ID as data
				result.click(function() {
					// bind function to it so that answer can be selected
					select_clarification(jQuery.data(result, 'targetId'));
				});
				popup.css('position', 'absolute').css('display', 'block');
				result.attr('id', 'pop_' + result.attr('id'));
				result.show(0);
				result.appendTo(popup); // append new answer value to popup
			});

			// IE specific
			if ($.browser.msie) {
				var height = popup.height();
				var width = popup.width();

				var scrOfY = 0;
				if (typeof (window.pageYOffset) == 'number') {
					// Netscape compliant
					yOffset = window.pageYOffset;
				} else if (document.body
						&& (document.body.scrollLeft || document.body.scrollTop)) {
					// DOM compliant
					yOffset = document.body.scrollTop;
				} else if (document.documentElement
						&& (document.documentElement.scrollLeft || document.documentElement.scrollTop)) {
					// IE6 standards compliant mode
					yOffset = document.documentElement.scrollTop;
				}

				leftVal = (($(window).width() - width) / 2) + "px";
				topVal = ((($(window).height() - height) / 2) + yOffset) + "px";
				// show the popup message and hide with fading effect
				popup.css('left', leftVal);
				popup.css('top', topVal);
			} else {
				// set popup position for other than IE based browsers
				popup.position({
					"my" : "left top",
					"at" : "right bottom",
					"of" : e,
					"offset" : "15 15",
					"collision" : "fit flip",
					"bgiframe" : false
				});
			}

			// show div
			popup.fadeIn(250);

			// set flag to say that popup is showing currently
			clariPopupShowing = true;
			
			// add to lastPopup storage
			lastPopup = question_id;
		}
	}
}


/**
 * Selection of clarification answers from the additional-answers popup
 * @param answer_id id of the newly to add answer
 */
function select_clarification(answer_id) {
	var answer = $("#" + answer_id);
	answer.show();	// show the answer element
	hide_popup(250); // hide popup again
	flat_button_toggle(answer_id); // toggle answer selection state
}


/** IN ClarificationQuestionnaire.st 
 * Toggles the specified element in 500 ms
 * @param id the ID of the element to toggle
 */
function toggle(id) {
	$("#" + id).toggle(500);
}

/**
 * Prevent errors in browsers NOT supporting the FIREBUG CONSOLE
 * (http://blog.t8d.de/2008/11/03/firebug-windowconsole-is-undefined-verhindern/)
 */
if (typeof window.loadFirebugConsole == "undefined"
		|| typeof window.console == 'undefined') {
	var names = [ "log", "debug", "info", "warn", "error", "assert", "dir",
			"dirxml", "group", "groupEnd", "time", "timeEnd", "count", "trace",
			"profile", "profileEnd" ];
	window.console = {};
	
	// create for each of above console printing keywords an empty dummy-function
	for ( var i = 0; i < names.length; ++i) {
		window.console[names[i]] = function() {
		};
	}
}

/* 
REM return n-th closest parent in hierarchy, n being levels parameter
function getParentTag(object, selector, levels) {
	var result = object;
	for ( var i = 0; i < levels; i += 1) {
		result = result.closest(selector);
	}
	return result;
}
REM function connect_divs_in_height(sourceId, targetId) {
	$("#" + targetId).height($("#" + sourceId).height());
	$("#" + sourceId).resize(function() {
		$("#" + targetId).height($("#" + sourceId).height());
	});
}
REM  check if an element is really visible (with checking parents)
function isReallyVisible(object) {
	var result = true;
	object.parent("[id^='q_'],[id^='a_']").filter(":hidden").each(function() {
		result = false;
	}).end().filter(":visible").each(function() {
		result = isReallyVisible($(this));
	});
	return result;
}
REM function startsWith(fullString, testString) {
return fullString.indexOf(testString) == 0;
}
REM
function generate_weighting_functions() {
	$("div[id^='weight-']").click(function() {
		click_weight($(this));
	});
}
REM function click_weight(id) {
	var impDiv = id;
	var text = impDiv.text();
	if (text.indexOf("!") == -1) {
		text = "!";
	} else {
		text += "!";
		if (text.length == 4) {
			text = "&nbsp;";
		}
	}

	impDiv.html(text);
}*/