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
var goon = true;

$(function() {
   
    
    // check browser and warn if the wrong one is used
    
    var link = $.query.set("action", "checkHandleBrowsers").toString();
        
    link = window.location.href.replace(window.location.search, "") + link;
        
    /*$.ajax({
            type : "GET",
            url : link,
            asynch: true,
            cache : false, // needed for IE, call is not made otherwise
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            success : function(html) {
                if(html.indexOf("true")!=-1){
                    handleUnsupportedBrowsers();
                } 
            }
        });
    */
    
        
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
            minWidth: 320,
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
	
    var print, dl;
    if(language=="en"){
        print = "Print";
        dl = "Download .txt"
    } else if(language=="de"){
        print = "Drucken";
        dl = ".txt herunterladen"
    }
	
    /* SUMMARY DIALOG */
    $(function() {

        var opts = {
            autoOpen: false,
            position: [ 0, getHeaderHeight()],
            modal: false,
            width: 550,
            height: 600,
            minWidth : 550,
            minHeight : 600,
            buttons: [{
                id: "sumDLTxt",
                text: dl,
                click: function(){
                    var link = $.query.set("action", "goToTxtDownload");
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
            },
            {
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
            }],
            // when opening the dialog, apply styling for the download button
            open: function(){
                $("#sumDLTxt").addClass("hidden");
            }
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
    
    
    var endSess;
    var goOn;
    if(language=="en"){
        endSess = "Yes, end session";
        goOn = "No, go on"
    } else if(language=="de"){
        endSess = "Ja, Session beenden";
        goOn = "Nein, Eingabe fortsetzen"
    }
    
    /* End of Session Confirmation Dialog */
    $(function() {

        var opts = {
            autoOpen: false,
            position : top,
            width : 480,
            height : 200,
            minWidth : 480,
            minHeight : 200,
            draggable : false,
            resizable : false,
            modal : true,
            // do NOT close dialog when hitting escape
            closeOnEscape : false,
            // for NOT showing the default close button/cross of the dialog
            open : function(event, ui) {
            //$(".ui-dialog-titlebar-close").hide();
            },
            // two custom buttons
            buttons : [{
                id: "endSession",
                text: endSess,
                click: function(){
                    ue_logEnd();
                    
                    $('#jqConfirmEoSDialog').dialog("close");
                    d3web_resetSession();
                }
            },
            {
                id: "goOn",
                text: goOn,
                click: function(){
                    $('#jqConfirmEoSDialog').dialog("close");
                }
            }]
        };
        $("#jqConfirmEoSDialog").dialog(opts);
    });

   

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
    
    });
	
    if (logging) {
        
        link = $.query.set("action", "checkInitialLoggingReload").toString();
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
                    if (logging) ue_logBrowserAndUser(browser, user);
                } 
            // otherwise, no reload because this would cause endless loop
            }
        });
        
    }
    // 
    
    /* Initialize the JS binding to the dialog elements */
    initFunctionality();
    
    // move the content below the header
    moveContentPart();
   
});


/**
 * Set a click function to all form elements for enabling the sending of values
 * if either a radio-button has been clicked, or one or more num vals have been
 * entered followed by pressing enter.
 */
function initFunctionality() {
    
    // per default, assume that NO widget but question or answer o.a. dialog 
    // elements were clicked
    markIsWidget("false");
    
    if(logging){
        link = $.query.set("action", "checkWidgetClicked").toString();
        link = window.location.href.replace(window.location.search, "") + link;
		
        $.ajax({
            type: "GET",
            async: false,
            cache : false, // needed for IE, call is not made otherwise
            url: link,
            success : function(html) {
                if (html == "true") {
                // a widget was clicked, thus we need no logging of end session
                // value
                } else {
                
                    ue_logEnd();
                // potential end session value is only logged if there was some
                // data entry before, NOT if there was sthg like "save" button
                // clicked
                }
            }
        });
    }
   
        
    $(window).resize(function() {
        moveContentPart();
    });
	
    $(document).ready(function() {
        $("#jqSummaryDialog").tabs();
        $("#jqTabbedButtons").tabs();
    });
	
    /*
     * bind "get selected facts" method to radio buttons, checkboxes and
     * textareas
     */
    $('[type=radio]').unbind('click').click(function() {
        
        if($(this).attr("id").indexOf("UE_")==-1){
            d3web_storeQuestionOC($(this));
            d3web_addFacts();
        }
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
	
        
    $('[type=text]').unbind('keydown').keydown(function(e) {
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code == 13) {
            handleTextFields($(this))
        }
    }).unbind('focusout').focusout(function() {
        handleTextFields($(this));
    }).each(function() {
        handleUrlInput($(this));
    });
    
    $('[type=num]').unbind("keydown").keydown(function(e) { 
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code == 13) {
            
            handleNumFields($(this));
        }
    }).unbind("focusout").focusout(function() {
        handleNumFields($(this));
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
    }).each(function() {
        d3web_handleQuestionDate($(this));
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
        if (logging) {
            markIsWidget("true");
            ue_logWidgetClicked($(this));
        }
        //checkSessionStillValid();
        d3web_resetSession();
    });

    // bind send/save button to sendexit function
    $('#savecase').unbind('click').click(function(event) {
        if (logging) {
            markIsWidget("true");
            ue_logWidgetClicked($(this));
        }
        if(!itree){
            d3web_addFacts();
        // TODO: we need a final save for ITree too one time!
        }
        checkSessionStillValid();
        if(!redirectFlag){
            d3web_prepareSave();
        }
    });

    // bind the loadcase button to making the fileselect list visible
    $('#loadcase').unbind('click').click(function(event) {
        if (logging) {
            markIsWidget("true");
            ue_logWidgetClicked($(this));
        }
        checkSessionStillValid();
        if(!redirectFlag){
            $("#jqLoadCaseDialog").dialog("open");
        }
    });
    
    $("[name^=fileClearname]").unbind("keydown").keydown(function(e) {
        
        var code = (e.keyCode ? e.keyCode : e.which);
        if (code == 13) {
            
            d3web_loadFileClearname($(this));
        }
    }) .unbind("focusout").focusout(function() {
        d3web_loadFileClearname($(this));
    });
    
   
    
	
    $('#followupbutton').unbind('click').click(function(event) {
        if (logging) {
            markIsWidget("true"); 
            ue_logWidgetClicked($(this));
        }
        checkSessionStillValid();
        if(!redirectFlag){
            $("#jqFollowUpDialog").dialog("open");
        }
    });
	
    $('#summary').unbind('click').click(function(event){
        if (logging) {
            markIsWidget("true"); 
            ue_logWidgetClicked($(this));
        }
        checkSessionStillValid();
        if(!redirectFlag){
            d3web_updateSummary();
            $("#jqSummaryDialog").dialog("open");
        }
    });
	
     
    $('#statistics').unbind('click').click(function(event){
        if (logging) {
            markIsWidget("true"); 
            ue_logWidgetClicked($(this));
        }
        checkSessionStillValid();
        if(!redirectFlag){
            gotoStatistics();
        }
    });
    
    // TODO REFACTOR: is used both here and in Code.js
    $('#FFButton').unbind('click').click(function(event) {
        if (logging) {
            markIsWidget("true"); 
            ue_logWidgetClicked($(this));
        }
        checkSessionStillValid();
        if(!redirectFlag){
            $("#jqFFDialog").dialog("open");
        }
    });
    
    $('#UEQButton').unbind('click').click(function(event) {
        if (logging) {
            markIsWidget("true"); 
            ue_logWidgetClicked($(this));
        }
        checkSessionStillValid();
        if(!redirectFlag){
            $("#jqUEQDialog").dialog("open");
        }
    });
    
$('#newcaseintrobutton').unbind('click').click(function(event) {
    if (logging) {
        markIsWidget("true"); 
        ue_logWidgetClicked($(this));
    }
    ehs_handleintrobuttons("newcase");
});
    
$('#loadcaseintrobutton').unbind('click').click(function(event) {
    if (logging) {
        markIsWidget("true"); 
        ue_logWidgetClicked($(this));
    }
    $("#jqLoadCaseDialog").dialog("open");
});
    
   
    
$('#creategroupsintrobutton').unbind('click').click(function(event) {
    if (logging) {
        markIsWidget("true"); 
        ue_logWidgetClicked($(this));
    }
    ehs_handleintrobuttons("groups");
});
    
$('#statisticsintrobutton').unbind('click').click(function(event) {
    if (logging) {
        markIsWidget("true"); 
        ue_logWidgetClicked($(this));
    }
    ehs_handleintrobuttons("statistics");
});
    
// click on language toggle
$('img[id*="lang"]').unbind('click').click(function(event){
    if (logging){
        markIsWidget("true"); 
        ue_logLanguageWidgetClicked($(this));
    } 
        
    toggleLanguage($(this));
});
    
// mouseover on image answer --> MC Checkboxes
$('[type=imageAnswer]').unbind('mouseenter').mouseenter(function() {
    var poly = $("#polygon-IMG_" + $(this).attr("id"));
    poly.attr("oldmouseoveropacity", poly.css("opacity"));
    poly.css("opacity", poly.attr("mouseoveropacity"));
}).unbind('mouseleave').mouseleave(function() {
    var poly = $("#polygon-IMG_" + $(this).attr("id"));
    poly.css("opacity", poly.attr("oldmouseoveropacity"));
})
// click on image answer --> MC Checkboxes
.find('input').unbind('change').change(function() {
    var poly = $("#polygon-IMG_" + $(this).parents('[type=imageAnswer]').attr("id"));
    if ($(this).attr("checked") == "checked") {
        poly.css("opacity",  poly.attr("clickedopacity"));
        poly.attr("oldmouseoveropacity", poly.attr("clickedopacity"));
    } else {
        poly.css("opacity",  poly.attr("unclickedopacity"));
        poly.attr("oldmouseoveropacity", poly.attr("unclickedopacity"));
    }
});
    
// mouseover on image answer --> IMAGE-PARTS
$('[type=imagepolygon]').unbind('mouseenter').mouseenter(function() {
    d3web_IQMouseOver($(this).attr("answerid"), true);
}).unbind('mouseleave').mouseleave(function() {
    d3web_IQMouseOver($(this).attr("answerid"), false);
}).unbind('click').click(function() {
    d3web_IQClicked($(this).attr("answerid"));
});
    
// check which pane of the summary dialog is selected as download button
// is to be shown ONLY for the plain summary
$('#jqSummaryDialog').bind('tabsselect', function(event, ui) {
    if (logging) {
        markIsWidget("true"); 
    }
        
    if(ui.index==0){
        $("#sumDLTxt").addClass("hidden");
        $("#sumDLTxt").removeClass("visible");
    } else {
        $("#sumDLTxt").addClass("visible");
        $("#sumDLTxt").removeClass("hidden");
    }
});
    
    
/* Clarification Hierarchy Dialog */
// bind yes/no/?/retract buttons to d3web corresponding function
$('.ynbutton').unbind('click').click(function(event) {
    if($(this).attr("id").indexOf("ynYes")!=-1){
        if($(this).hasClass("swap")){
            d3web_addFactsITree($(this), "3");
        } else {
            d3web_addFactsITree($(this), "1");
        }
    } else
    if($(this).attr("id").indexOf("ynNo")!=-1){
        if($(this).hasClass("swap")){
            d3web_addFactsITree($(this), "1");
        } else {
            d3web_addFactsITree($(this), "3");
        }
    } else 
    if($(this).attr("id").indexOf("ynUn")!=-1){
        d3web_addFactsITree($(this), "2");
    }
    else if($(this).attr("id").indexOf("ynNan")!=-1){
        d3web_addFactsITree($(this), "0");
    }
       
});
     
/* SingleForm Dialog */
$('[class^=answerFlat]').unbind('click').click(function(event) {
    if($(this).attr("class").indexOf("answer-oc")!=-1){
            d3web_storeQuestionOC($(this).attr("id"));
            d3web_addFacts();
    }
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

		
    var second = d3web_getDateValue(secondSelect, "00");
    var minute = d3web_getDateValue(minuteSelect, "00");
    var hour = d3web_getDateValue(hourSelect, "00");
    var day = d3web_getDateValue(daySelect, "01");
    var month = d3web_getDateValue(monthSelect, "01") - 1;
    var year = d3web_getDateValue(yearSelect, new Date().getFullYear());
	    
    return new Date(year, month, day, hour, minute, second);
}



/**
 * Assembles and returns the error-placeholder element depending
 * on the given widget ID
 */
function getErrorPlaceholder(input){
    var questionId = getQuestionId(input);
    return $("#error-" + questionId);
}

// input needs to be the jquery element, NOT an id
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

function gotoGroups() {
    var link = $.query.set("action", "gotoGroups");
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

function handleUrlInput(input) {
    var urlRegex = /(ftp|https?|file):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?/;
    var text = input.val();
    if (urlRegex.test(text)) {
        if (input.next().length == 0) {			
            input.after(" <a href='" + text + "'>Follow Link</a>");
        }
    }
    else {
        input.next().remove();
    }
}


function handleTextFields(field){
    // workaround for IE: if login dialog is opened, no sending of
    // textfield/numfield data should take place as this leads to a
    // required-check fail (works in other browsers without that if)
    if(usrdatLogin==true){
        if(!$("#jqLoginDialog").dialog("isOpen")){
            d3web_storeQuestionText(field);
            d3web_addFacts();
        }
    } else {
        d3web_storeQuestionText(field);
        d3web_addFacts();
    }
    
}

function handleNumFields(field){
    // workaround for IE: if login dialog is opened, no sending of
    // textfield/numfield data should take place as this leads to a
    // required-check fail (works in other browsers without that if)
    if(usrdatLogin==true){
        if(!$("#jqLoginDialog").dialog("isOpen")){
            d3web_storeQuestionNum(field);
            d3web_addFacts();
        }
    } else {
        d3web_storeQuestionNum(field);
        d3web_addFacts();
    }
}

/* move the content part of the dialog further down --> not in hierarchy */
function moveContentPart() {
    if(!itree){
        $('#content').css("margin-top", (getHeaderHeight() + 10) + "px");
    }
}



function updateDialog(html) {
    if (html.indexOf("##replaceid##")!= -1) {
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
            
    if ($('#jqConfirmDialog').dialog('isOpen'))
        $('[aria-labelledby$=jqConfirmDialog]').find(
            ":button:contains('Save')").click();
	
    if ($('#jqLoadCaseDialog').dialog('isOpen'))
        $('[aria-labelledby$=jqLoadCaseDialog]').find(
            ":button:contains('OK')").click();
}

function escapeExpression(str) {
    return str.replace(/([#;&,\.\+\*\~':"\!\^$\[\]\(\)=>\|])/g, "\\$1");
}

function d3web_addFacts() {

alert(ocStore);
    var link = $.query.set("action", "addFacts");
	
    if (logging) {
        var now = ue_getCurrentDate();		
        link = link.set("timestring", now);
    }

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
        link = link.set("question" + i, qid).set("value" + i, encodeURI(textStore[qid]));
        i++;
    }
	
    for (var qid in numStore) {
        link = link.set("question" + i, qid).set("value" + i, encodeURI(numStore[qid]));
        i++;
    }
    
    
    // check current state: is session still active? Then go on, otherwise
    // redirect to login page
    checkSessionStillValid();
    alert(link);
    
    if(!redirectFlag){
        $.ajax({
            type : "GET",
            url : link,
            cache : false, // needed for IE, call is not made otherwise
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            dataType: 'html',
            success : function(html) {
                if (html.indexOf("ITreeSUCCESS")==0){
                    window.location.reload();
                    initFunctionality();
                } 
                else if (html.startsWith("##missingfield##")) {
               
                    var field =    html.replace("##missingfield##", "");     
                    field = field.replace(" ", "_");
                    var errorPlaceholder = "#error-q_" + field;
                    var warning = "Required Field";
                
                    $(errorPlaceholder).html("<font color=\"red\">" + warning + "</font>");
                    alert("Please fill in the required, marked field(s) first!");
   
                } else {
                    updateDialog(html);
                    //window.location.reload(true);
                    //init_all();
                    setup();
                    initFunctionality();
                
                }
            },
            error : function(html) {
           
                alert("ajax error add facts");
            }
        });
    }
        
    // create new stores for next call
    mcStore = new Object();
    ocStore = new Object();
    dateStore = new Object();
    textStore = new Object();
    numStore = new Object();
}




function d3web_storeQuestionOC(ocInput) {
    var ocQuestion;
    // in flatanswer dialog style, id is defined otherwisely
    if(ocInput.indexOf("a_q_")!=-1){
        ocAnswerElement = $("#" + ocInput);
        ocQuestion = getQuestionId(ocAnswerElement);
        ocStore[ocQuestion] = ocInput;
    } else {
        ocQuestion = getQuestionId(ocInput);
        ocStore[ocQuestion] = getAnswerId(ocInput);
    }
    
    
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

function d3web_isAnsweredQuestion(question) {
    return question.parent().children("[class$=\"question-d\"]").length > 0;
}

function d3web_getDateValue(select, def) {
    return select.length == 1 && select.val() != "" ? select.val() : def;
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

function d3web_storeQuestionText(textInput) {
    var textQuestion = getQuestionId(textInput);
    textStore[textQuestion] = $(textInput).val();
}

function d3web_storeQuestionNum(numInput) {
   
    if(d3web_checkQuestionNum(numInput)){
        var numQuestion = getQuestionId(numInput);
        textStore[numQuestion] = $(numInput).val();
    //alert(numQuestion + " " + $(numInput).val());
    } else {
    // alert("dont store");
    }
}


/**
 * Checks the given numInput value whether it is within the given bounds 
 * (as stored in the attributes left and right)
 * If yes, returns TRUE, else FALSE
 */
function d3web_checkQuestionNum(numInput) {
    
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
        if (logging) ue_logNotAllowedInput(numInput);
        return false;
    } else if (val > right) {   
        errorWid.html(tooHigh);
        if (logging) ue_logNotAllowedInput(numInput);
        return false;
    } else {
        errorWid.html("");
    }
    
    return true;
}

function d3web_storeQuestionDate(dateSelect) {
    var question = getQuestionId(dateSelect);
    dateStore[question] = getDate(dateSelect).getTime();
}

function d3web_handleQuestionDate(dateSelect) {
    var date = getDate(dateSelect);
		
    var tooSoon = "";
    var tooLate = "";
    var beforeId = $("#" + getQuestionId(dateSelect)).attr("before");
    var afterId = $("#" + getQuestionId(dateSelect)).attr("after");
    
    var beforeQuestion = $("#" + beforeId);
    var afterQuestion = $("#" + afterId);
    var beforeDate = getDate(beforeQuestion.find("select").first());
    
    var afterDate = getDate(afterQuestion.find("select").first());
    
    var tqpopup1 = "To avoid mistakes, timepoints must be filled in completely (Hour:Minute).";
    var tqpopup2 = "Otherwise, missing values are automatically added by the system.";
    var tqpopup3 = "Attention: End of operation must be after start of operation!";

    var tqpopup4 = "To avoid mistakes, dates must be filled in completely (Day:Month:Year)."; 
    var tqpopup5 = "Otherwise, missing values are automatically added by the system.";

    // trim messages that are displayed when after-timepoints are not AFTER before-timepoints
    // in dialog (as user entered)
    var afterText = $.trim($("#text-" + afterId).text());
    if(afterText.indexOf(tqpopup1) != -1){
        afterText = afterText.replace(tqpopup1, "").replace(tqpopup2, "").replace(tqpopup3, "");
    }
    if(afterText.indexOf(tqpopup4) != -1){
        afterText = afterText.replace(tqpopup4, "").replace(tqpopup5, "");
    }
    
    
    if(language=="de"){
        tooSoon = "Erwartet wird ein Datum bzw. Zeitpunkt später als '" + afterText + "'.";
    } else if(language=="en"){
        tooSoon = "Expected is a date / timepoint later than '" + afterText + "'.";
    }
    
    // currently, no check of earlier-date questions needed
    tooLate = "";
    
    var errorWid = getErrorPlaceholder(dateSelect);
    if (afterId != undefined && d3web_isAnsweredQuestion(afterQuestion) && date.getTime() < afterDate.getTime()) {
        errorWid.html(tooSoon);
    }
    else if (beforeId != undefined && d3web_isAnsweredQuestion(beforeQuestion) && date.getTime() > beforeDate.getTime()) {   
        errorWid.html(tooLate);
    } 
    else {
        errorWid.html("");
    }
    
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
    var idClean = id.replace("IMG_", "");
    
    var target = $("#" + idClean);	// get the clicked element
    var selected = target.find(":input:checked");	// find clicked input 
    var deselected = target.find(":input:not(:checked)"); // find not clicked inputs
    selected.attr('checked', false);
    deselected.attr('checked', true);
    d3web_storeQuestionMC(target);
    d3web_addFacts($(this));
    

}

function d3web_IQMouseOver(id, isOver) {
    var idClean = id.replace("IMG_", "");
    if (isOver) {
        var box = $("#f_" + idClean);
        box.focus();
    }
    else {
        $("#f_" + idClean).blur();
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
            $('#jqConfirmDialog').dialog('close');
            window.location.reload();
            initFunctionality();
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
            //window.location.reload();
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
    } else if (id.indexOf("nl") != -1){
        langID = 7;
    } else if (id.indexOf("sv") != -1){
        langID = 8;
    } else if (id.indexOf("pt") != -1){
        langID = 9;
    } else if (id.indexOf("braz") != -1){
        langID = 10;
    }
     
    checkSessionStillValid();
    
    if(!redirectFlag){
    
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
}
    
function markIsWidget(isWidget){
    
    var link = $.query.set("action", "markWidget").set("isWidget", isWidget);
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        success : function(html) {
            
        }
    });
}

function ehs_handleintrobuttons(whichbutton){
    
    if(whichbutton.indexOf("newcase")!=-1){
        d3web_resetSession();
        
    } else if (whichbutton.indexOf("statistics")!=-1){
        gotoStatistics();
        
    } else if (whichbutton.indexOf("groups")!=-1){
        gotoGroups();
    }
    
}

function d3web_loadFileClearname(whichbutton){
    
    filename = whichbutton.val();
    
    if (filename != "") {
        d3web_loadCase(filename, true);
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
function d3web_loadCase(filename, clear) {
    var link;
    
    if(clear != null && clear != false){
       
        if(logging){
            var now = ue_getCurrentDate();
            link = $.query.set("action", "loadcaseClear").set("fn", filename).set("timestring", now).toString();
        } else {
            link = $.query.set("action", "loadcaseClear").set("fn", filename).toString();
        }
    
    } else {
       
        if(logging){
            var now = ue_getCurrentDate();
            link = $.query.set("action", "loadcase").set("fn", filename).set("timestring", now).toString();
        } else {
            link = $.query.set("action", "loadcase").set("fn", filename).toString();
        }
    }
    
   
    link = window.location.href.replace(window.location.search, "") + link;

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



