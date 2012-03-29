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
    

    if(hierarchy){
        // remove yn panel etc for first question
        removeInputFacilitiesForFirst();
        
        expandFirstmostElement();
    
        exchangeReadflowTextFirstSubQuestion();
        
        // initialize mechanism for num fields to check entered values etc
        initializeNumfields();
        
        // initialize mechanism for dropdwons to check entered values etc
        initializeDropdownSelects();
        
        // remove the field containing the propagation value info for 1st q
        removePropagationInfoInQuestionForFirst();
        
        alwaysExpandDummyNodes();
        
    // some more styling of diagnosis element
    }
    
    // handle one question dialog
    if(oqd) {
        // init oqd stuff
        hideTopElement();
        expandFirstQuestion();
       
    }
    
    if(logging){
        
        handleLogging();
    }
    
    if(ueq){
        handleUEQ();
        
        initUEQFreeFeedback();
    }
    
    if(feedback){
        handleFB();
    }
    
}

function initUEQFreeFeedback(){

    $('#UE_QFreeFeedback').unbind('focus').focus(function(event) {
        var text = $("#UE_QFreeFeedback").html();
        if(text.indexOf("z.B.: was hat mir gut gefallen, was hat mir nicht gefallen, sind Probleme aufgetreten (technische, bez. der Interaktion oder Verständnis)...")
            !=-1){
            $('#UE_QFreeFeedback').html("");
        }
    
        $('#UE_QFreeFeedback').addClass("longTextareaInputColor");
    });
    
    $('#UE_QUIRating').unbind('focus').focus(function(event) {
        var text = $("#UE_QUIRating").val();
        if(text.indexOf("Hierarchisch ODER Einfrage ODER Beide gleich")
            !=-1){
            $('#UE_QUIRating').val("");
        }
    
        $('#UE_QUIRating').addClass("longTextfieldInputColor");
    });
}


function handleUEQ(){
    $('#UEQButton').unbind('click').click(function(event) {
        $("#jqUEQDialog").dialog("open");
    });
    
    if($('#EndStudySessionButton')!=undefined){
        $('#EndStudySessionButton').unbind('click').click(function(event) {
            $("#jqUEQDialog").dialog("open");
        });
    }
}

function handleFB(){
    $('#FFButton').unbind('click').click(function(event) {
        $("#jqFFDialog").dialog("open");
    });
}

/* Logging initialization for prototypes */
function handleLogging(){
   
    var link = $.query.set("action", "logInit").toString();
    link = window.location.href.replace(window.location.search, "") + link;
	
    $.ajax({
        type: "GET",
        async: false,
        cache : false, // needed for IE, call is not made otherwise
        url: link,
        success : function(html) {
            if (html == "firsttime") {
                var browser = retrieveBrowserVal();    
                var user = retrieveUserVal();
                ue_logBrowserAndUser(browser, user);
            } 
        // otherwise, no reload because this would cause endless loop
        }
    });
        
    
}

/**
 * Retrieve the topmost element in hierarchically specified XML prototypes.
 * Usually this is the first <question> element.
 */
function retrieveRootQuestionIdInHierarchyPrototype(){
    
    var first; // get question id
    
    $("[id^=dialog] > [id^=q_]").each(function(){  // check all question elements     
        if($(this).attr("id")!=undefined){  
            first = $(this).attr("id");   // expand the first element
        }   
    });
    return first;
}

/**
 * Retrieve the first element in hierarchical dialogs and expand it on startup
 * Used e.g. in hierarchy (legal) dialog
 */
function expandFirstmostElement(){
    
    var rootId = retrieveRootQuestionIdInHierarchyPrototype();

    $("#" + rootId).addClass('solutiontext');
    toggle_sub_4boxes(rootId);   // expand the first element
}

/*Helper function to exchange the text of the first subquestion - usually 
 *Oder and Und - of a set of subquestions to the given text, e.g. "Wenn" 
 */
function exchangeReadflowTextFirstSubQuestion(){
    
    $("[id^=sub-] [id^=readFlow]:first-child img").each(function(){     
        $(this).attr('src', 'img/If.png');
    });
}


    function alwaysExpandDummyNodes(){
    $(".dummy").each(function(){
        toggle_sub_4boxes($(this).attr("id"));
    });
};


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
	
    toggle_hide("sub-" + id); 
    toggle_folder_image_4boxes(id);
    hide_all_tooltips();
}

/**
 * Toggle folder image (open/close) for the legal dialog style
 */
function toggle_folder_image_4boxes(id) {
	
    // ids of the arrow/folder image is 2-typeimg, when question is q_2    
    var typeimgID = id.replace("q_", "") + '-typeimg';
    
    // get the div of the arrow/folder image
    var imgDiv = $("[id^="+ typeimgID + "]");
    
    var qtext = $('#solutiontitle-' + id).html();
    
    var topelem = false;  
    if($('#' + id.replace("q_", "") + "-imagebox").html().indexOf("solutionboxtext") != -1){
        topelem = true;
    };
    
    if (imgDiv.attr('src') == 'img/openedArrow.png') {
        
        imgDiv.attr('src', 'img/closedArrow.png');
        if(logging){
            ue_logQuestionToggle(qtext, "SHUT");
        }
        
    } else if (imgDiv.attr('src') == 'img/closedArrow.png') {
        imgDiv.attr('src', 'img/openedArrow.png');
        if(logging && !topelem){
            ue_logQuestionToggle(qtext, "EXPAND");
        }
    } 
}

/**
 * IN ColorHIerarchMcQuestion.st or LegalQuestion.st
 * Color background and mark selected answer in Legal style dialogs
 * TODO factor out d3web
 * @param id the id of the item to mark
 * @param value the value that indicates in what regard the item is marked
 */
function h4boxes(value, id) {
    	
    ue_logDialogType("ClariHIE");    
    // get dialog item
    var item = $("#" + id);
	
    // get the first ancestor, i.e. the first upper question
    var target = $(item).closest("div[id^='q_']");
        
    // set image attribute to the correctly selected one
    item.attr('src', "img/pane" + value + ".png");

    // check whether the calculated rating contradicts the user-chosen rating
    // VORLÄUFIG RAUSLASSEN
    /*if(hasChildrenHierachical(target)){
            if(!equalUserAndKBSRating(target, value)){
                alert("Ratings stimmen nicht überein!")
            }
        }*/
        
    prepareQuestionLogging(id, value);
    
    //alert(target.attr("id") + " " + value);    
    setColorForQuestion(target, item, value);
    
    // store user set value as uservalue attribute
    storeUserVal(target, value);
    
    // set color of propagation-color indicator
    setPropagationColor(target, value);
        
    // also mark parents of the target while excluding target
    h4boxes_mark(target, true);
		
    
}


function prepareQuestionLogging(question, value){
    if(question.indexOf("ynNo-" != -1)){
        question = question.replace("ynNo-", "");
    } 
    if(question.indexOf("ynYes-" != -1)){
        question = question.replace("ynYes-", "");
    } 
    if(question.indexOf("ynUn-" != -1)){
        question = question.replace("ynUn-", "");
    }
    if(question.indexOf("ynNan-" != -1)){
        question = question.replace("ynNan-", "");
    }
    if(question.indexOf("panel-" != -1)){
        question = question.replace("panel-", "");
    }
    
    var qtext = $('#solutiontitle-' + question).html();
    var qtextSplit = qtext.split(" ");
    var questiontext = qtext.replace(qtextSplit[0] + " ", "");
    
    var valueVerbalization = "";
    
    switch(value){
        case "1":
            valueVerbalization = "Ja";
            break;
        case "2":
            valueVerbalization = "Unentschieden";
            break;
        case "3":
            valueVerbalization = "Nein";
            break;
        case "0":
            valueVerbalization = "Unbewertet";
            break;
    }
    
    ue_logQuestionValueClariPrototype(questiontext, valueVerbalization);
}
  
function storeUserVal(question, value){
    
    switch(value){
        case "1":
            $(question).addClass("uv1").removeClass("uv2").removeClass("uv3").removeClass("uv0");
            break;
        case "2":
            $(question).addClass("uv2").removeClass("uv1").removeClass("uv3").removeClass("uv0");
            break;
        case "3":
            $(question).addClass("uv3").removeClass("uv2").removeClass("uv1").removeClass("uv0");
            break;
        case "0":
            $(question).addClass("uv0").removeClass("uv2").removeClass("uv3").removeClass("uv1");
            break;
        
    }
        
}

// set the color for the area indicating the system-propagated value
function setPropagationColor(question, userval){
    
    var prop = $("#propagation-"+question.attr("id"));
    var propColor = calculateRatingForQuestion(question);
    
    setPropColor(prop, propColor);
    if(userval != propColor && propColor != 0 && userval != undefined){
        prop.removeClass("hide");
        prop.addClass("show");
    } else {
        prop.removeClass("show");
        prop.addClass("hide");
    }
    removePropagationInfoInQuestionForFirst();
  
}

function setPropColor(target, color){
    // remove existing rating=coloring classes
    target.removeClass('rating-low rating-medium rating-high');
    switch (color) {
        case "1": // approve --> green
            target.addClass("rating-high");
            //imgTarget.attr('src', "img/panel1.gif");
            break;
            
        // in case the user retracts the answer (gray) check, if 
        // there is a calculated rating. If not, set gray
        case "0": // undecided --> transparent
            break;
        case "2": // suggested  --> yellow
            target.addClass("rating-medium");
            //imgTarget.attr('src', "img/panel2.gif");
            break;
        case "3": // rejected -_> red
            target.addClass("rating-low");
            //imgTarget.attr('src', "img/panel3.gif");
            break;
    }
}

/*
 * Sets the given rating for the given target question
 */
function setColorForQuestion(target, imgTarget, color){
    
    // remove existing rating=coloring classes
    target.removeClass('rating-low rating-medium rating-high');
    
    switch (color) {
        case "1": // approve --> green
            target.addClass("rating-high");
            //imgTarget.attr('src', "img/panel1.gif");
            break;
            
        // in case the user retracts the answer (gray) check, if 
        // there is a calculated rating. If not, set gray
        case "0": // undecided --> transparent
            
            var checkCol = calculateRatingForQuestion(target);
            setColorForQuestionHelper(target, imgTarget, checkCol);
            break;
        case "2": // suggested  --> yellow
            target.addClass("rating-medium");
            //imgTarget.attr('src', "img/panel2.gif");
            break;
        case "3": // rejected -_> red
            target.addClass("rating-low");
            //imgTarget.attr('src', "img/panel3.gif");
            break;
    }
}

// similar to setColorForQuestion, except that no 0-option exists
function setColorForQuestionHelper(target, imgTarget, color){
    
    // remove existing rating=coloring classes
    target.removeClass('rating-low rating-medium rating-high');
    
    switch (color) {
        case "1": // approve --> green
            target.addClass("rating-high");
            //imgTarget.attr('src', "img/panel1.gif");
            break;
        case "2": // suggested  --> yellow
            target.addClass("rating-medium");
            //imgTarget.attr('src', "img/panel2.gif");
            break;
        case "3": // rejected -_> red
            target.addClass("rating-low");
            //imgTarget.attr('src', "img/panel3.gif");
            break;
    }
}



/**
* Calculates the rating for the given question and returns
* the corresponding color/rating value: 0=undecided, 1=approve, 2=suggest, 3=reject
*/
function calculateRatingForQuestion(question){
    
    var ocPar = question.hasClass('AND');    // parent has AND connection
    var mcPar = question.hasClass('OR');    // parent has OR connection
    var color; 
        
    // go through all child-questions and read their ratings into a var
    var ratings = "";
        
    $("#sub-" + question.attr('id')).children("div[id^='q_']").each(
        function() {
            var high = $(this).hasClass("rating-high");
            var med = $(this).hasClass("rating-medium");    
            var low = $(this).hasClass("rating-low");
                
            if(high){
                ratings += "1 ";
            } else if (med){
                ratings += "2 ";
            } else if (low){
                ratings += "3 ";
            } else {
                ratings += "0 ";
            }
        });
          
    // AND case
    if(ocPar){
       
        // handle OC questions here, i.e. questions where all children
        // need to be confirmed to get the parent confirmed
        if(ratings.indexOf("1") != -1 &&
            ratings.indexOf("0")==-1 && ratings.indexOf("2")==-1 && ratings.indexOf("3")==-1){
            color = "1";  // all approved, then parent approve
        } else if (ratings.indexOf("2") != -1 && 
            ratings.indexOf("0")==-1 && ratings.indexOf("3")==-1){
            color = "2";  // all known and at least one suggested, suggest parent
        } else if(ratings.indexOf("2") != -1 && ratings.indexOf("0") != -1 
            && ratings.indexOf("3")==-1 ){
            color = 2;
        } else if (ratings.indexOf("3") != -1 ){
            color = "3";    // one rejected, reject parent
        } else {
            color = "0";
        }
           
    } 
    // OR case: here, one single confirmed (1) child is enough to get the
    // parent confirmed
    else if (mcPar){
        
        if(ratings.indexOf("1") != -1){ 
            color = "1";    // one confirmed, confirm par
        }
        else if (ratings.indexOf("2") != -1 && ratings.indexOf("3") != -1 
            && ratings.indexOf("1") == -1 && ratings.indexOf("0") == -1){
            color = "2";    // some undecided and some rejected, undecide parent
        } else if(ratings.indexOf("2") != -1 && ratings.indexOf("1") == -1
            && ratings.indexOf("0") == -1 && ratings.indexOf("3") == -1){
            color = "2";
        }        
        else if (ratings.indexOf("3") != -1 && ratings.indexOf("2") == -1
            && ratings.indexOf("1") == -1 && ratings.indexOf("0") == -1){
            color = "3";    // all rejected, reject parent
        } else {
            color = "0";
        }
    }
    
    return color;
}

/*
* Compares the user-chosen and kbs-calculated color/rating values
*/
function equalUserAndKBSRating(question, chosenColor){
    
    var col = chosenColor+"";
    if(col != calculateRatingForQuestion(question)){
        return false;
    }
    
    
    return true;
}

function hasChildrenHierachical(question){
    
    var ret = false;
    question.children().each(function(){
        if($(this).attr("id").indexOf("sub-")==0){
            ret = true;
        }
    });
    
    return ret;
}

/**
* Initialize Num fields as to react on enter-press or focusout to check whether
* question should be established due to the rating given
*/
function initializeNumfields(){
    $('[type=num]').unbind("keydown").keydown(function(e) { 
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code == 13) {
            handleNumFields_proto($(this));
        }
    }).unbind("focusout").focusout(function() {
        handleNumFields_proto($(this));
    });
}

/**
* Checks the defined range for num questions. If in defined value range for
* establishing question: establish, otherwise remove rating
*/
function handleNumFields_proto(el){
    
    var val = parseInt($(el).val());
    var min = parseInt($(el).attr("min"));
    var max = parseInt($(el).attr("max"));
    
    // get parent question element
    var name = $(el).attr("name");
    var idnum = name.split("-")[0];
    var par = $(el).parents("[id^=" + "q_" + idnum + "]");
        
    if(val >= min && val <= max){
        // set color of question --> high rating, as defining = establish
        setColorForQuestion(par, par, "1");
    } else {
        // remove rating if value outside range
        par.removeClass("rating-high");
    }

    $(el).blur();
    setPropagationColor(par);
    h4boxes_mark(par, true);
}

/**
* Initialize Dropdwons as to check whether the chosen value is the defined
* establish-value when dropdown input changes.
*/
function initializeDropdownSelects(){
    $('select').unbind('change').change(function() { 
        handleDropdwonSelects_proto($(this));
    });
}

/**
* Checks the defined range for dropdown questions. If the defined-->establish 
* value is selected, establish question, otherwise remove rating
*/
function handleDropdwonSelects_proto(el){
    
    var val = el.val();
    
    // get parent question element
    var id = $(el).attr("id");
    var idnum = name.split("-")[0];
    var par = $(el).parents("[id^=" + "q_" + idnum + "]");
    var defining = par.attr("defining");
    
    if(defining.indexOf(val)!=-1){
        // set color of question --> high rating, as defining = establish
        setColorForQuestion(par, par, "1");
    } else {
        // remove rating if value outside range
        par.removeClass("rating-high");
    }

    $(el).blur();
    setPropagationColor(par);
    h4boxes_mark(par, true);
}

/**
* Show the auxiliary information for element with id "id" in the infopanel
* element
*/
function showAuxInfo(id, title){
    
    // get infotext stored in additional, invisible sub-element of current q
    var infoid = "#bonus-"+id;
    if(title==undefined){
        title = "";
    }
    var auxHeader = "<b>Informationen zu [" + title + "]:</b> <br /><br />";
    var auxinfo = $(infoid).html();
    
    // rewrite inner HTML of infopanel widget with info content
    if(auxinfo==""){
        auxinfo = "-";
    } 
    
    $("#auxHeader").html(auxHeader);
    $("#auxInfo").html(auxinfo);
}

function hideAuxInfo(){
    
    // clear infopanel
    $("#auxHeader").html("<b>Informationen zu []:</b> <br /><br />");
    $("#auxInfo").html("-");
}

function showAuxPropInfo(){
    
    $("#auxPropagationInfo").html("<b>Beantwortung der Detailfragen widerspricht der Antwort dieser Frage!</b> <br /><br />");
}

function hideAuxPropInfo(){
    $("#auxPropagationInfo").html("");
}

function removePropagationInfoInQuestionForFirst(){
    $("[id^=dialog] > [id^=q_]").each(function(){  // check all question elements
        
        var first = $(this).attr("id"); // get question id
        
        if($(this).attr("id")!=undefined){  
            var prop = $("#propagation-"+$(this).attr("id"));
    
            prop.removeClass("show");
            prop.addClass("hide");
        }
      
    });
    
}

// TODO check if that works for num and oc q
function removeInputFacilitiesForFirst(){
    $("[id^=dialog] > [id^=q_]").each(function(){  // check all question elements
        
        if($(this).attr("id")!=undefined){  
            var id = "#" + $(this).attr("id").replace("q_", "") + "-imagebox";
            
            // the imagebox div, that contains input buttons normally
            var prop = $(id);
            
            prop.html("<div id='solutionboxtext'>Hauptfrage</div>");
        }
    });
}

/**
* Function for toggling the display of the info panel.
*/
function toggleAuxInfoPlacing(){
    
    var html = $("#auxpanelToggle").html();
    
    // info currently displayed at the right side, i.e. is to be toggled to bottom
    if(html.indexOf("Nach unten")!=-1){
        $("#auxpanelToggle").html("Zur Seite");
        $("#auxpanel").removeClass("auxpanelRight");
        $("#auxpanel").addClass("auxpanelBottom");
        $("#dialog").addClass("dialogCompleteWidth");
        $("#dialog").removeClass("dialog75Width");
    } 
    // info displayed at the bottom
    else {
        $("#auxpanelToggle").html("Nach unten");
        $("#auxpanel").removeClass("auxpanelBottom");
        $("#auxpanel").addClass("auxpanelRight");
        $("#dialog").addClass("dialog75Width");
        $("#dialog").removeClass("dialogCompleteWidth");
    }
    
}

/**
* Transfer coloring (in hierarchy dialog) also to parent quesitons
* @param object the object from where to start marking parents
* @param skip_self flag indicating whether element itself should
* 		also be processed
*/
function h4boxes_mark(object, skip_self) {

    // check object itself unless skip_self
    if (!skip_self) {
		
        var color; 
        
        // retrieve target element and target image
        var target = $("#" + $(object).attr('id'));
        
        var imgTarget = $("#panel-" + $(object).attr('id'));
        
        color = calculateRatingForQuestion(object);
        
       
        if(color=="0"){
            
            if(object.hasClass("uv1")){
                color = "1"; 
            } else if(object.hasClass("uv2")){
                color = "2"; 
            } else if(object.hasClass("uv3")){
                color = "3"; 
            } else if(object.hasClass("uv0")){
                color = "0"; 
            } 
            
        }
        
        setColorForQuestion(target, imgTarget, color);
        setPropagationColor(target);
        // set image attribute to the correctly selected one
        imgTarget.attr('src', "img/pane.png");
    }

    // get first parent div
    var walking = $(object).parent("div:first");
    var reg = new RegExp(/^sub-.*$/);
    var counter = 0;
	
    // solange die ID von walking nicht dem Pattern sub- s.o. entspricht    
    while (!reg.test($(walking).attr('id'))) {
        // gehe weiter und ... zähle hoch
        counter += 1;
        if (counter > 6) // falls mehr als 6 mal probiert (dann kommt nix mehr)
            break; // break if there is no more parent question
        // ... setze das zu testende div auf das parent div
        walking = $(walking).parent("div");
    }

    // get first of walking's parents of element and call 
    // recursively, also resetting the parents coloring
    $(walking).parent(":first").each(function() {
        h4boxes_mark($(this), false);
    });
}



/**
* REMOVE?!
* IN ColorHierarchyMCQuestion.st and LegalQuestion.st
* @param sourceId the ID of the source element
* @param destId the ID of the destination element
*/
function copy_div(sourceId, destId) {
	
    // get content, which is the innerHTML of the source
    var newContent = $("#" + sourceId).attr('innerHTML');
    //alert(newContent);
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

/*----------------------One Question Dialog----------*/
function toggle_sub_4oqd(id){
    toggle_hide("sub-" + id); 
}

function hideTopElement(){
    
    $("[id^=dialog] > [id^=q_]").each(function(){  // check all question elements
        if($(this).attr("id")!=undefined){  
               
            $(this).addClass("show");
            $(this).children().first().addClass("hide");
        }
    });
}

function expandFirstQuestion(){
    
    $("[id^=dialog] > [id^=q_]").each(function(){  // check all question elements
        if($(this).attr("id")!=undefined){  
               
            var children = $(this).children();
            children.each(function(){
                if($(this).attr("id").indexOf("sub-")!=-1){
                    qchildren = $(this).children();
                    qchildren.first().addClass("show");
                    qchildren.first().removeClass("hide");
                    qchildren.parents().closest('[id^=sub-q_]').addClass("show");
                    qchildren.parents().closest('[id^=sub-q_]').removeClass("hide");
                }
            }
            );
        }
    });
}

/**
* Show the auxiliary information for element with id "id" in the infopanel
* element
*/
function showAuxInfoOQD(id, title){
    
    // get infotext stored in additional, invisible sub-element of current q
    var infoid = "#bonus-"+id;
    if(title==undefined){
        title = "";
    }
    var auxinfo = $(infoid).html();
    
    // rewrite inner HTML of infopanel widget with info content
    if(auxinfo==""){
        auxinfo = "";
    } 
    
    $("#auxInfo-"+id).html(auxinfo);
}

/**
 * Handle everything there is to do when a y/n question in the OQDialog is clicked,
 * i.e., and answer is provided
 */
function handleOQYNQuestions(fullId, rating){
    ue_logDialogType("ClariOQD");    
    
    // retrieve follow up element to current element
    var splitID = fullId.split("-")[1];
    var questionEl = $("#"+splitID);
    
    prepareQuestionLogging(fullId, rating);
    
    setColorForQuestion(questionEl, questionEl, rating);
    setPropagationColor(questionEl, rating);
    // also mark parents of the target while excluding target
    h4boxes_mark(questionEl, true);
    
    //get follow up question
    var followUp = retrieveFollowUpForQuestion(questionEl);
    
    //if no follow up sibling try to retrieve next follow up for parent
    if(followUp == undefined || followUp.attr("id")==undefined){
        followUp = retrieveFollowUpParent(questionEl);
    }
    
    // the top question = solution
    var solQuestion = $("#dialog").children().first();  
    var rating = calculateRatingForQuestion($(solQuestion));
    // color solution panel indicator according to solution rating
    switch (rating) {
        case "1": // approve --> green
            $("#solHigh").addClass("show");
            $("#solHigh").removeClass("hide");
            $("#solUn").addClass("hide");
            $("#solUn").removeClass("show");
            $("#solMed").addClass("hide");
            $("#solMed").removeClass("show");
            $("#solLow").addClass("hide");
            $("#solLow").removeClass("show");
            //var baseText = $("#solHigh").html();
            //baseText += ",<br /> weil Frage 1 oder Frage 2 nicht bestätigt werden konnte.";
            //$("#solHigh").html(baseText);
            break;
            
        case "0": // undecided --> transparent
            $("#solUn").addClass("show");
            $("#solUn").removeClass("hide");
            $("#solHigh").addClass("hide");
            $("#solHigh").removeClass("show");
            $("#solMed").addClass("hide");
            $("#solMed").removeClass("show");
            $("#solLow").addClass("hide");
            $("#solLow").removeClass("show");
            break;
        
        case "2": // suggested  --> yellow
            $("#solMed").addClass("show");
            $("#solMed").removeClass("hide");
            $("#solUn").addClass("hide");
            $("#solUn").removeClass("show");
            $("#solHigh").addClass("hide");
            $("#solHigh").removeClass("show");
            $("#solLow").addClass("hide");
            $("#solLow").removeClass("show");
            break;
        case "3": // rejected -_> red
            $("#solLow").addClass("show");
            $("#solLow").removeClass("hide");
            $("#solUn").addClass("hide");
            $("#solUn").removeClass("show");
            $("#solMed").addClass("hide");
            $("#solMed").removeClass("show");
            $("#solHigh").addClass("hide");
            $("#solHigh").removeClass("show");
            //var baseText = $("#solLow").html();
            //baseText += ",<br /> weil Frage 1 oder Frage 2 nicht bestätigt werden konnte.";
            //$("#solLow").html(baseText);
            break;
    }
    
       
    // close all child questions (aux info etc) if a question is answered   
    closeAllChildren(questionEl);
        
    // toggle follow up element to be visible by toggling its super-sub element
    // and then toggling the follow up itself
    if(followUp != undefined && followUp.attr("id") != undefined){
        var fpar = followUp.parents().closest('[id^="sub-q_"]');
        fpar.removeClass("hide");
        fpar.addClass("show");
        followUp.removeClass("hide");
        followUp.addClass("show");
    }
       
    // toggle auxinfo and styling for current element
    questionEl.children().first().children().closest('[id^=auxpanel]').addClass("hide");
    questionEl.children().first().children().closest('[id^=detail]').addClass("hide");
    questionEl.children().first().children().closest('[id^=auxpanel]').removeClass("show");
    questionEl.children().first().children().closest('[id^=detail]').removeClass("show");
}

/**
 * Toggle all sub-elements of the given question --> also sub-questions, again
 */
function closeAllChildren(question){
    // get child questions for current question
    $(question).children().each(function(){
        if($(this).attr("id").indexOf("sub-q")!=-1){
            $(this).children().each(function(){
                
                // toggle auxpanel and detail button visibility
                $(this).children().first().children().closest('[id^=auxpanel]').addClass("hide");
                $(this).children().first().children().closest('[id^=detail]').addClass("hide");
                closeAllChildren($(this)); 
            });
            
        }
    });
}

/* Retrieve the follow up element for a question: the next sibling */
function retrieveFollowUpForQuestion(questionEl){
    return questionEl.next();  
}

/* Retrieve follow up parent: called when no direct f-u sibling for a 
 * question is available, then the dialog tries to get the next f-u for parent */
function retrieveFollowUpParent(questionEl){
    
    var parent = questionEl.closest('[id^="sub-q_"]').closest('[id^="q_"]');
    var fup = parent.next();
    
    if(parent.attr("id") != undefined && fup.attr("id") == undefined){
        return retrieveFollowUpParent(parent);
    }
   
    else if(parent.attr("id") == undefined && fup.attr("id") == undefined){
        return undefined;
    }
    
    return fup;
}

/* Make first detail-sub question visible for the given question */
function stepIntoDetail(questionId){
    
    var qtext = $('#solutiontitle-' + questionId).html();
    var qtextSplit = qtext.split(" ");
    qtext = qtext.replace(qtextSplit[0] + " ", "");
    
    ue_logQuestionToggle(qtext, "EXPAND");
    
    var question = $("#"+questionId);
    
    // get corresponding sub- and question elements and make them visible 
    var sub = $("#sub-"+questionId);
    
    if(sub != undefined && sub.attr("id")!= undefined){
   
        var firstChild = sub.children().first();
    
        firstChild.addClass("show");
        firstChild.removeClass("hide");
        sub.addClass("show");
        sub.removeClass("hide");
    
        //make siblings of original question invisible
        makeInvisibleSiblings(question);
    
        // toggle auxinfo and styling for current element
        question.children().first().children().closest('[id^=auxpanel]').addClass("hide");
        question.children().first().children().closest('[id^=detail]').addClass("hide");
        question.children().first().children().closest('[id^=auxpanel]').removeClass("show");
        question.children().first().children().closest('[id^=detail]').removeClass("show");
    }
}

/* Make siblings of original question invisible */
function makeInvisibleSiblings(original){
    
    if(original.next() != undefined && original.next().attr("id") != undefined){
        original.next().addClass("hide");
        original.next().removeClass("show");
        makeInvisibleSiblings(original.next());
    }
}

function toggleDetails(q){
    question =  $(q);
    question.children().first().children().closest('[id^=auxpanel]').addClass("show");
    question.children().first().children().closest('[id^=detail]').addClass("show");
    question.children().first().children().closest('[id^=auxpanel]').removeClass("hide");
    question.children().first().children().closest('[id^=detail]').removeClass("hide");
}
