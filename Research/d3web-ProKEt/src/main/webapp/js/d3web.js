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
var warningRecieved = false;


$(function() {
        
    /* LOGIN DIALOG */
    if(usrdatLogin){
        window.setTimeout("logintimeout()", 57000 * 60);

        var link = $.query.set("action", "checkUsrDatLogin").toString();
        link = window.location.href.replace(window.location.search, "") + link;
		
        $.ajax({
            type: "GET",
            async: false,
            cache : false, // needed for IE, call is not made otherwise
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
        goon = "Cancel";
    } else if(language=="de"){
        save = "Speichern";
        goon = "Abbrechen";
    }
	
    /* SAVE CASE DIALOG */
    $(function() {

        var opts = {
            autoOpen: false,
            position: [ 0, getHeaderHeight()],
            modal: false,
            buttons: [{
                id: "saveOK",
                text: save,
                click: function(){
                    d3web_sendSave(warningRecieved);
                }
            },
            {
                id: "saveCancel",
                text: goon,
                click: function(){
                    $('#jqConfirmDialog').dialog('close');
                    warningRecieved = false;
                }
            }]
        };
        $("#jqConfirmDialog").dialog(opts);
    });

    var load, cancelload, deleteCase;
    if(language=="en"){
        load = "OK";
        cancelload = "Cancel";
        deleteCase = "Delete";
    } else if(language=="de"){
        load = "OK";
        cancelload = "Abbrechen";
        deleteCase = "Löschen";
    }
	
    /* LOAD CASE DIALOG */
    $(function() {

        var opts = {
            autoOpen: false,
            position: [ 0, getHeaderHeight()],
            modal: false,
            buttons: [{
                id: "loadOK",
                text: load,
                click: function(){
                    d3web_loadSelectedFile();
                }
            },
            {
                id: "loadCancel",
                text: cancelload,
                click: function(){
                    $('#jqLoadCaseDialog').dialog('close');
                }
            },
            {
                id: "deleteCase",
                text: deleteCase,
                click: function(){
                    d3web_deleteSelectedFile();
                }
            }]
        };
        var loadCaseDialog = $("#jqLoadCaseDialog");
        loadCaseDialog.dialog(opts);
        loadCaseDialog.find("option").unbind("dblclick").dblclick(function(){
            d3web_loadSelectedFile();
        });
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
	
    /* SUMMARY DIALOG */
    $(function() {

        var opts = {
            autoOpen: false,
            position: [ 0, getHeaderHeight()],
            modal: false,
            width: 450,
            height: 550,
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
                    $('#jqSummaryDialog').dialog('close');
                }
            }]
        };
        $("#jqSummaryDialog").dialog(opts);
    });
	
    /* FOLLOW UP DIALOG */
    $(function() {

        var opts = {
            autoOpen: false,
            position: [ 0, getHeaderHeight()],
            modal: false,
            width: 750,
            height: 650,
            buttons: [{
                id: "loadOK",
                text: load,
                click: function(){
                    $('#jqFollowUpDialog').dialog('close');
                }
            }]
        };
        $("#jqFollowUpDialog").dialog(opts);
    });

    /* Initialize the JS binding to the dialog elements */
    initFunctionality();

    $().ready(function() {
        // enable buttons in save case and load case dialogs to
        // react on pressing enter
        $(document).keypress(function(e) {
            if ((e.which && e.which == 13)
                || (e.keyCode && e.keyCode == 13)) {
                submitLoadAndSaveDialog();
            }
        });
        
        // PROBLEM: so wird direkt am Anfang, also auch VOR dem
        // initialisieren, schonmal versucht das Ende zu loggen
        // FEHLER! 
        /*$(window).unload( function () {
           
            d3web_ue_logEnd();
                
            d3web_resetSession();
           
            
        } );*/
    });
	
    // check browser and warn if the wrong one is used
    handleUnsupportedBrowsers();
	
    var browser = retrieveBrowserVal();    
    var user = retrieveUserVal();
    if (logging) {
    	d3web_ue_logBrowserAndUser(browser, user);
    }
    // 
    // 
    // move the content below the header
    moveContentPart();
});


/**
 * Set a click function to all form elements for enabling the sending of values
 * if either a radio-button has been clicked, or one or more num vals have been
 * entered followed by pressing enter.
 */
function initFunctionality() {
        
    $(window).resize(function() {
        moveContentPart();
    });
	
    $(document).ready(function() {
        $("#jqSummaryDialog").tabs();
    });
	
    /*
     * bind "get selected facts" method to radio buttons, checkboxes and
     * textareas
     */
    $('[type=radio]').unbind('click').click(function() {
        d3web_storeQuestionOC($(this));
        d3web_addFacts();
    });
	
	
    /* TODO MC QUESTIONS */
    /* the following ensures, that MC questions behave like OC questions,
     * i.e., a v*/
    $('[type=checkbox]').unbind('click').click(function() {
        d3web_storeQuestionMC($(this));
        d3web_addFacts();
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
                d3web_addFacts();
            }
        });
    });
	
    $('[type=text]').unbind('focusout').focusout(function() {
        d3web_storeQuestionText($(this));
        d3web_addFacts();
    });
    
    $('[type=num]').unbind('click').click(function() {
        var thisEl = $(this);
        thisEl.bind('keydown', function(e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                d3web_storeQuestionNum($(this));
                d3web_addFacts();
            }
        });
    });
	
    $('[type=num]').unbind('focusout').focusout(function() {
        d3web_storeQuestionNum($(this));
        d3web_addFacts();
    });
	
    $('[type=textselect]').unbind('change').change(function() {
        d3web_storeQuestionText($(this));
        d3web_addFacts();
    });
	
    $("[type=Yearselect]," +
        "[type=Monthselect]," +
        "[type=Dayselect]," +
        "[type=Hourselect]," +
        "[type=Minuteselect]," +
        "[type=Secondselect]"
        ).unbind('change').change(function() {
        d3web_storeQuestionDate($(this));
        d3web_addFacts();
    });

    $('[type=textarea]').unbind('click').click(function() {
        var thisEl = $(this);
        thisEl.bind('keydown', function(e) {
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                d3web_storeQuestionText($(this));
                d3web_addFacts();
            }
        });
    });
	
    // bind reset button to resetSession() function
    $('#reset').unbind('click').click(function() {
        logWidgetClicked($(this));
        d3web_resetSession();
    });

    // bind send/save button to sendexit function
    $('#savecase').unbind('click').click(function(event) {
        logWidgetClicked($(this));
        d3web_addFacts();
        d3web_prepareSave();
    });

    // bind the loadcase button to making the fileselect list visible
    $('#loadcase').unbind('click').click(function(event) {
        logWidgetClicked($(this));
        $("#jqLoadCaseDialog").dialog("open");
    });
	
    $('#followupbutton').unbind('click').click(function(event) {
        logWidgetClicked($(this));
        $("#jqFollowUpDialog").dialog("open");
    });
	
    $('#summary').unbind('click').click(function(event){
        logWidgetClicked($(this));
        d3web_updateSummary();
        $("#jqSummaryDialog").dialog("open");
    });
	
    $('#statistics').unbind('click').click(function(event){
        logWidgetClicked($(this));
        gotoStatistics();
    });
    
    // click on language toggle
    $('img[id*="lang"]').unbind('click').click(function(event){
        logLanguageWidgetClicked($(this));
        toggleLanguage($(this));
    });
    
    // mouseover on image answer
    $('[type=imageAnswer]').unbind('mouseenter').mouseenter(function() {
    	var poly = $("#polygon-" + $(this).attr("id"));
    	poly.attr("oldmouseoveropacity", poly.css("opacity"));
    	poly.css("opacity", poly.attr("mouseoveropacity"));
    }).unbind('mouseleave').mouseleave(function() {
    	var poly = $("#polygon-" + $(this).attr("id"));
    	poly.css("opacity", poly.attr("oldmouseoveropacity"));
    })
    // click on image answer
   .find('input').unbind('change').change(function() {
    	var poly = $("#polygon-" + $(this).parents('[type=imageAnswer]').attr("id"));
    	if ($(this).attr("checked") == "checked") {
        	poly.css("opacity",  poly.attr("clickedopacity"));
    		poly.attr("oldmouseoveropacity", poly.attr("clickedopacity"));
    	} else {
        	poly.css("opacity",  poly.attr("unclickedopacity"));
    		poly.attr("oldmouseoveropacity", poly.attr("unclickedopacity"));
    	}
    });
    
    // mouseover on image answer
    $('[type=imagepolygon]').unbind('mouseenter').mouseenter(function() {
    	d3web_IQMouseOver($(this).attr("answerid"), true);
    }).unbind('mouseleave').mouseleave(function() {
    	d3web_IQMouseOver($(this).attr("answerid"), false);
    }).unbind('click').click(function() {
    	d3web_IQClicked($(this).attr("answerid"));
    });
    
}

function getHeaderHeight() {
    var head = $('#head');
    return head.height() + parseInt(head.css("padding-top")) + parseInt(head.css("padding-bottom"));
}

function getDate(dateSelect) {
	  var answer = $(dateSelect).parents(".answer").first();
	    var yearSelect = answer.find("[type=Yearselect]");
	    var monthSelect = answer.find("[type=Monthselect]");
	    var daySelect = answer.find("[type=Dayselect]");
	    var hourSelect = answer.find("[type=Hourselect]");
	    var minuteSelect = answer.find("[type=Minuteselect]");
	    var secondSelect = answer.find("[type=Secondselect]");

		
	    var second = getDateValue(secondSelect, "00");
	    var minute = getDateValue(minuteSelect, "00");
	    var hour = getDateValue(hourSelect, "00");
	    var day = getDateValue(daySelect, "01");
	    var month = getDateValue(monthSelect, "01") - 1;
	    var year = getDateValue(yearSelect, new Date().getFullYear());
	    
	    return new Date(year, month, day, hour, minute, second);
}

function d3web_storeQuestionDate(dateSelect) {

	var date = getDate(dateSelect);
	
    var tooSoon = "";
    var tooLate = "";
    var beforeId = $("#" + getQuestionId(dateSelect)).attr("before");
    var afterId = $("#" + getQuestionId(dateSelect)).attr("after");
    
    var beforeQuestion = $("#" + beforeId);
    var afterQuestion = $("#" + afterId);
    var beforeDate = getDate(beforeQuestion.find("select").first());
    var afterDate = getDate(afterQuestion.find("select").first());
    
    var beforeText = $.trim($("#text-" + beforeId).text());
    var afterText = $.trim($("#text-" + afterId).text());
    
    if(language=="de"){
    	tooLate = "Erwartet wird ein Datum früher als bei Frage '" + beforeText + "'.";
    	tooSoon = "Erwartet wird ein Datum später als bei Frage '" + afterText + "'.";
    } else if(language=="en"){
    	tooLate = "Expected is a date earlier than the one given in question '" + beforeText + "'.";
    	tooSoon = "Expected is a date later than the one given in question '" + afterText + "'.";
    }
    
    var errorWid = getErrorPlaceholder(dateSelect);
    if (afterId != undefined && isAnsweredQuestion(afterQuestion) && date.getTime() <= afterDate.getTime()) {
    	errorWid.html(tooSoon);
    }
    else if (beforeId != undefined && isAnsweredQuestion(beforeQuestion) && date.getTime() >= beforeDate.getTime()) {   
        errorWid.html(tooLate);
    } 
    else {
        errorWid.html("");
    }
    var question = getQuestionId(dateSelect);
    dateStore[question] = date.getTime();
}

function isAnsweredQuestion(question) {
	return question.parent().children("[class$=\"question-d\"]").length > 0;
}

function getDateValue(select, def) {
    return select.length == 1 && select.val() != "" ? select.val() : def;
}

function d3web_storeQuestionText(textInput) {
    var textQuestion = getQuestionId(textInput);
    textStore[textQuestion] = $(textInput).val();
}

function d3web_storeQuestionNum(numInput) {
    var tooHigh = "";
    var tooLow = "";
    var errorWid;
    var val = parseInt($(numInput).val());
    var left = parseInt($(numInput).attr("left"));
    var right = parseInt($(numInput).attr("right"));
    
    if(language=="de"){
        tooHigh = "Der eingegebene Wert ist zu hoch. Zulässig ist das Intervall: [" + left + " " + right + "]";
        tooLow = "Der eingegebene Wert ist zu niedrig. Zulässig ist das Intervall: [" + left + " " + right + "]";
    } else if(language=="en"){
        tooHigh = "The allowed range is: [" + left + " " + right + "]";
        tooLow = "The allowed range is: [" + left + " " + right + "]";
    }
    
    errorWid = getErrorPlaceholder(numInput);
    if (val < left) {
        errorWid.html(tooLow);
    } else if (val > right) {   
        errorWid.html(tooHigh);
    } else {
        errorWid.html("");
    }
    
    var numQuestion = getQuestionId(numInput);
    textStore[numQuestion] = $(numInput).val();
}

/**
 * Assembles and returns the error-placeholder element depending
 * on the given widget ID
 */
function getErrorPlaceholder(input){
	var questionId = getQuestionId(input);
	return $("#error-" + questionId);
}


function d3web_storeQuestionOC(ocInput) {
    var ocQuestion = getQuestionId(ocInput);
    ocStore[ocQuestion] = getAnswerId(ocInput);
}

function d3web_storeQuestionMC(mcCheckBox) {
	
    var mcQParent = $(mcCheckBox.parents("[id^=q_]"));
    var mcQuestion = getQuestionId(mcCheckBox);
    var checkBoxes = mcQParent.find(":checkbox");

    // get the question-content-parent element and go through all its
    // checkbox-children
    var checkedBoxes = new Array();
    checkBoxes.each(function() {
        if ($(this).prop("checked") == true) {
            checkedBoxes.push(getAnswerId($(this)));
        }
    });
	
    mcStore[mcQuestion] = checkedBoxes;
}

function getQuestionId(input) {
    return getTerminologyId(input, "q");
}

function getAnswerId(input) {
    return getTerminologyId(input, "a");
}

function getTerminologyId(input, prefix) {
    var parent = $(input.parents("[id^=" + prefix + "_]"));
    return parent.attr("id");
}

function d3web_addFacts() {

    var link = $.query.set("action", "addFacts");

    var i = 0;
    for (var qid in mcStore) {
    	var mcAnswerString = "";
    	var mcAnswerSeparator = "##mcanswer##";
    	for (var j in mcStore[qid]) {
    		if (mcAnswerString != "") {
    			mcAnswerString += mcAnswerSeparator;
    		}
    		mcAnswerString += mcStore[qid][j];
    	}
        link = link.set("question" + i, qid).set("value" + i, mcAnswerString);
        i++;
    }
	
    for (var qid in ocStore) {
        link = link.set("question" + i, qid).set("value" + i, ocStore[qid]);
        i++;
    }
	
    for (var qid in dateStore) {
        link = link.set("question" + i, qid).set("value" + i, dateStore[qid]);
        i++;
    }
	
    for (var qid in textStore) {
        link = link.set("question" + i, qid).set("value" + i, textStore[qid]);
        i++;
    }
	
    for (var qid in numStore) {
        link = link.set("question" + i, qid).set("value" + i, numStore[qid]);
        i++;
    }
    
   
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success : function(html) {
            if (html.startsWith("##missingfield##")) {
               
                var field =    html.replace("##missingfield##", "");     
                field = field.replace(" ", "_");
                var errorPlaceholder = "#error-q_" + field;
                var warning = "Required Field";
                
                $(errorPlaceholder).html("<font color=\"red\">" + warning + "</font>");
                alert("Please fill in the required, marked field(s) first!");
   
            } else {
                updateDialog(html);
                setup();
                initFunctionality();
            }
        },
        error : function(html) {
            alert("ajax error add facts");
        }
    });

    
    
        
    // create new stores for next call
    mcStore = new Object();
    ocStore = new Object();
    dateStore = new Object();
    textStore = new Object();
    numStore = new Object();
}

function gotoStatistics() {
    var link = $.query.set("action", "gotoStatistics");
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success : function(url) {
            window.location.href = url;
        }
    });
}


function handleUnsupportedBrowsers() {
    var browser;
    if($.browser.msie)
        browser = "Internet Explorer";
    else
        browser = $.browser.name;
    $("#unsupportedbrowserwarning").remove();
    if (!($.browser.webkit
        || $.browser.opera 
        || $.browser.mozilla)) {
        $('#head').children("table").children("tbody").append(
            "<tr id='unsupportedbrowserwarning'><td colspan='3' style='color:red; font-variant:normal' >Sie benutzen " +
            "den Browser '" + browser + "'. Dieser Browser wird von dieser Seite " +
            "noch nicht vollständig unterstützt. Bitte nutzen sie stattdessen " +
            "<a href='http://www.mozilla-europe.org/de/'>Mozilla Firefox</a> " +
            "oder " +
            "<a href='http://www.google.com/chrome/'>Google Chrome</a>!</td></tr>");
    }
}

function moveContentPart() {
    $('#content').css("margin-top", (getHeaderHeight() + 10) + "px");
}

function d3web_updateSummary() {
    var link = $.query.set("action", "updatesummary");
	
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success : function(html) {
            updateDialog(html);
        },
        error : function(html) {
            alert("ajax error update summary");
        }
    });
	
}


function updateDialog(html) {
    if (html.startsWith("##replaceid##")) {
        var updateArray = html.split(/##replaceid##|##replacecontent##/);
        for (var i = 0; i < updateArray.length - 1; i+=2) {
            if (updateArray[i].length == 0) {
                i--;
                continue;
            }
            $("#" + updateArray[i]).replaceWith(updateArray[i + 1]);
        }
    }
}

function getUrlParameter(name) {
    name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
    var regexS = "[\\?&]"+name+"=([^&#]*)";
    var regex = new RegExp( regexS );
    var results = regex.exec( window.location.href );
    if( results == null )
        return "";
    else
        return results[1];
}


function submitLoadAndSaveDialog() {
    if ($('#jqConfirmDialog').dialog('isOpen'))
        $('[aria-labelledby$=jqConfirmDialog]').find(
            ":button:contains('Speichern')").click();
	
    if ($('#jqLoadCaseDialog').dialog('isOpen'))
        $('[aria-labelledby$=jqLoadCaseDialog]').find(
            ":button:contains('OK')").click();
}

function escapeExpression(str) {
    return str.replace(/([#;&,\.\+\*\~':"\!\^$\[\]\(\)=>\|])/g, "\\$1");
}

function d3web_prepareSave() {
    $('#confirmFilename').val($("#" + $("[useasfilename=true]").first().attr("id")).val());
    $('#jqConfirmDialog').dialog("open");
}

function d3web_IQClicked(id) {
	var poly = $('#' + "polygon-" + id);
	var opacity = poly.css("opacity");
	if (opacity == "0.5") {
		poly.css("opacity", "0");		
	} else {
		poly.css("opacity", "0.5");	
	}
    var target = $("#" + id);	// get the clicked element
    var selected = target.find(":input:checked");	// find clicked input 
    var deselected = target.find(":input:not(:checked)"); // find not clicked inputs
    selected.attr('checked', false);
    deselected.attr('checked', true);
    d3web_storeQuestionMC(target);
    d3web_addFacts($(this));
    

}

function d3web_IQMouseOver(id, isOver) {
	if (isOver) {
		var box = $("#f_" + id);
		box.focus();
	}
	else {
		$("#f_" + id).blur();
	}
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
        cache : false, // needed for IE, call is not made otherwise
        success : function() {
            window.location.reload(true);
            initFunctionality();
        }
    });
}

function d3web_sessionForNewUser() {

    var link = $.query.set("action", "resetNewUser");

    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function() {

            d3web_show();
        }
    });
}


function d3web_sendSave(force) {

    // d3web_getRemainingFacts();

    var confirmFilename = $('#confirmFilename').val();

    var link = $.query.set("action", "savecase").set("userfn", confirmFilename).set("force", force.toString());
	
    // new jquery 1.5 syntax
    $.get(link, function(data) {
        if (data == "exists") {
            var warning = "File already exists. Do you want to overwrite?";
            if(language=="de"){
                warning = "Die Datei existiert bereits. Möchten sie überschreiben?";
            }
            $('#confirmMessage').html("<font color=\"red\">" + warning + "</font>");
            warningRecieved = true;
        } else {
            d3web_show();
        }
    });
}

function d3web_show() {

    var link = $.query.set("action", "show").toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        async : false,
        cache : false, // needed for IE, call is not made otherwise
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
function d3web_loadSelectedFile() {

    // get selected text = filename
    var filename = d3web_getSelectedFile();

    if (filename != "") {
        d3web_loadCase(filename);
    }
}

function d3web_deleteSelectedFile() {

    // get selected text = filename
    var filename = d3web_getSelectedFile();

    if (filename != "") {
        d3web_deleteCase(filename);
    }
}

function d3web_getSelectedFile() {
    return $('#caseSelect :selected').text();
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
        cache : false, // needed for IE, call is not made otherwise
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

function d3web_deleteCase(filename) {
	
    var areYouSure = "";
    if(language=="en"){
        areYouSure = "Click 'OK' to delete the selected case '" + filename + "'.";
    } else if(language=="de"){
        areYouSure = "Klicken sie 'OK' um den ausgewählten Fall '" + filename + "' zu löschen.";
    }
	
    if (!confirm(areYouSure)) return;

    var link = $.query.set("action", "deletecase").set("fn", filename);

    $.ajax({
        type : "GET",
        async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            window.location.reload();
            initFunctionality();
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
        cache : false, // needed for IE, call is not made otherwise
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
        cache : false, // needed for IE, call is not made otherwise
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
        cache : false, // needed for IE, call is not made otherwise
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
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            var elements = html.split(";");
            for (var i = 0; i < elements.length; i++) {
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
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            $('#' + target_id).html(html).fadeIn(3000);
        }
    });
}

/**
* Retrieve the browser information, i.e. the type and version. 
* Returns a String in the form "<type> <version>"
*/
function retrieveBrowserVal(){
    var val = "";
    // retrieve the browser used
    jQuery.each(jQuery.browser, function(i, value) {
        var v = "";
        if(i=="version"){
            v = value + " ";
        } else{
            v = i + " ";
        }
        val += v;
    });
    return val;
}

function retrieveUserVal(){
    return "user";
}

/**
* Send Ajax request for logging basic information, currently browser information
* type and version, as well as user info
*/
function d3web_ue_logBrowserAndUser(browser, user){
    
    var link = $.query.set("action", "logInit").set("browser", browser)
    .set("user", user).toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function() {
        // no action needed
        }
    });
}

// log clicks on widgets that do NOT set values in d3web, e.g. info button,
// reset, save case etc
function logWidgetClicked(el){
    if (logging) {
	    var link = $.query.set("action", "logWidget").set("widget", el.attr("id")).toString();
	    link = window.location.href.replace(window.location.search, "") + link;
	
	    $.ajax({
	        type : "GET",
	        // async : false,
	        cache : false, // needed for IE, call is not made otherwise
	        url : link,
	        success : function() {
	        // no action needed
	        }
	    });
    }
}

function logLanguageWidgetClicked(el){
    
    var id = el.attr("id");
    var lang;
    
    if(id.indexOf("de") != -1){
        lang = "DE";
    } else if (id.indexOf("en") != -1){
        lang = "EN";
    } else if (id.indexOf("es") != -1){
        lang = "ES";
    } else if (id.indexOf("it") != -1){
        lang = "IT";
    } else if (id.indexOf("fr") != -1){
        lang = "FR";
    } else if (id.indexOf("pl") != -1){
        lang = "PL";
    }
    
    var link = $.query.set("action", "logLanguageWidget").set("widget", el.attr("id")).set("language", lang).toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function() {
        // no action needed
        }
    });
}


function toggleLanguage(el){
    
    var id = el.attr("id");
    var langID;
    
    if(id.indexOf("de") != -1){
        langID = 1;
    } else if (id.indexOf("en") != -1){
        langID = 2;
    } else if (id.indexOf("es") != -1){
        langID = 3;
    } else if (id.indexOf("it") != -1){
        langID = 4;
    } else if (id.indexOf("fr") != -1){
        langID = 5;
    } else if (id.indexOf("pl") != -1){
        langID = 6;
    }
     
    var link = $.query.set("action", "language").set("langID", langID);
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            window.location.reload(true);
            initFunctionality();
        }
    });
}

function logLanguageWidgetClicked(el){
    
    var id = el.attr("id");
    var lang;
    
    if(id.indexOf("de") != -1){
        lang = "DE";
    } else if (id.indexOf("en") != -1){
        lang = "EN";
    } else if (id.indexOf("es") != -1){
        lang = "ES";
    } else if (id.indexOf("it") != -1){
        lang = "IT";
    } else if (id.indexOf("fr") != -1){
        lang = "FR";
    } else if (id.indexOf("pl") != -1){
        lang = "PL";
    }
    
    var link = $.query.set("action", "logLanguageWidget").set("widget", el.attr("id")).set("language", lang).toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function() {
        // no action needed
        }
    });
}


/**
 * Called from d3webBasic
 * logs the info popups, on mousout.
 */

function logInfoPopup(ID, prefix, timestring){
    
    var link = $.query.set("action", "logInfoPopup").set("widget", ID).set("prefix", prefix).set("timestring", timestring);
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function() {
        // no action needed
        }
    });
}

/**
* Send the end-of-logging command
 */
function d3web_ue_logEnd(){
    
            
    var link = $.query.set("action", "logEnd");
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function() {
        // no action needed
        }
    });
}