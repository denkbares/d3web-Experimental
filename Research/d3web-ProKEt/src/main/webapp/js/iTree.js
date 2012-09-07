//
// THIS FILE CONTAINS ALL NECESSARY JAVASCRIPT NEEDED FOR ITREE 
// PROTOTYPES AND D3WEB SYSTEMS
//



/* Global variables */
/*-------------------*/
var rootQuestionId = "";


/* Initialization for iTree prototypes */
/*-----------------------------------------*/
function iTreeInit() {
    
    // browserInfo.js
    handleUnsupportedBrowsers();
    
    if(logging){
        ue_logDialogType("ITree");    
    }
    // this file
    generate_tooltip_functions_ynbuttons();
    generate_tooltip_functions_propagation();
        
    // this file    
    setRootQuestionIdInHierarchyPrototype();    
    expandAndStyleFirstElement_iTree();
    exchangeReadflowTextFirstSubQuestion();
    initializeSolutionBox();
    initializeNumfields();
    initializeDropdownSelects();   
    initializeDateDropdowns();
    
    alwaysExpandDummyNodes();    

    // global.js
    hide_all_tooltips(); 
 
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


/* INITIALIZATION FUNCTIONS */
function initializeSolutionBox(){
    
    var rootSolId = "[id^='solutiontitle-" + rootQuestionId + "']";
    // get text of the root element of the tree = the solution
    var soltext = $("#"+rootSolId).html();
    // set the solutiontext in the box
    $("#solutiontext").html(soltext);
}




/**
 * Retrieve the topmost element in itree dialogs.
 * Usually this is the first <question> element.
 */
function setRootQuestionIdInHierarchyPrototype(){   
    
    rootQuestionId = $("[id^=dialog] > [id^=q_]").first().attr("id");
}

/**
 * Retrieve the first element in hierarchical dialogs and expand it on startup
 * Used e.g. in hierarchy (legal) dialog
 */
function expandAndStyleFirstElement_iTree(){
    
    $("#" + rootQuestionId).addClass('solutiontext'); // style first element
    
    // reset contents of input facilities part of first element
    var inputFacilitiesElement = 
    $("#" + rootQuestionId.replace("q_", "") + "-imagebox");
    inputFacilitiesElement.html("<div id='solutionboxtextInTree'>Hauptfrage</div>");
            
    // remove propagation info field for first element
    var prop = $("#propagation-" + rootQuestionId);
    prop.removeClass("show");
    prop.addClass("hide");
    
    // only expand by javascript if not d3web, in d3web this is done by the
    // dialog/renderer
    if(!d3web){
        toggle_sub_4boxes(rootQuestionId);
    }
    
}

/*Helper function to exchange the text of the first subquestion - usually 
 *Oder and Und - of a set of subquestions to the given text, e.g. "Wenn" 
 */
function exchangeReadflowTextFirstSubQuestion(){
    
    
    $("[id^=sub-] [id^=readFlow]:first-child img").each(function(){     
        if(!($(this).attr("qtype")!=null && $(this).attr("qtype")=="ruletype") &&
            !($(this).attr("qtype")!=null && $(this).attr("qtype")=="scoretype")){
            
            $(this).attr('src', 'img/If.png');
        }
    });
}

/**
* Initialize Num fields as to react on enter-press or focusout to check whether
* question should be established due to the rating given
*/
function initializeNumfields(){
    if(!d3web){
        $('[type=num]').unbind("keydown").keydown(function(e) { 
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                
                handleNumFields_itreePrototype($(this));
               
            }
        }).unbind("focusout").focusout(function() {
           
            handleNumFields_itreePrototype($(this));
           
        });
    } else {
        $('[type=num]').unbind("keydown").keydown(function(e) { 
            var code = (e.keyCode ? e.keyCode : e.which);
            if (code == 13) {
                d3web_addValueFactITree(getQuestionId($(this)), $(this).val());
               
            }
        }).unbind("focusout").focusout(function() {
           
            d3web_addValueFactITree(getQuestionId($(this)), $(this).val());
           
        });
        
    }   
        
}

/**
* Checks the defined range for num questions. If in defined value range for
* establishing question: establish, otherwise remove rating
*/
function handleNumFields_itreePrototype(el){
    
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



function initializeDateDropdowns(){
    $("[type=Yearselect]," +
        "[type=Monthselect]," +
        "[type=Dayselect]," +
        "[type=Hourselect]," +
        "[type=Minuteselect]," +
        "[type=Secondselect]"
        ).unbind('change').change(function() {
            
        d3web_addValueFactITree(getQuestionId($(this)), getDate($(this)).getTime());
    });
}


/**
* Initialize Dropdwons as to check whether the chosen value is the defined
* establish-value when dropdown input changes.
*/
function initializeDropdownSelects(){
    $('select').unbind('change').change(function() { 
        if(!d3web){
            handleDropdwonSelects_itreePrototype($(this));
        }
    });
}

/**
* Checks the defined range for dropdown questions. If the defined-->establish 
* value is selected, establish question, otherwise remove rating
*/
function handleDropdwonSelects_itreePrototype(el){
    
    var val = el.val();
    
    // get parent question element
    var id = $(el).attr("id");
    var idnum = name.split("-")[0];
    var par = $(el).parents("[id^=" + "q_" + idnum + "]");
    var defining = par.attr("defining");
    
    if(defining.indexOf(val)!=-1){
        // set color of question --> high rating, as defining = establish
        setColorForQuestion(par, par, "1");
    }
    else {
        // remove rating if value outside range
        par.removeClass("rating-high");
    }

    $(el).blur();
    setPropagationColor(par);
    h4boxes_mark(par, true);
}



 


/* OTHER FUNCTIONALITY */

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

function alwaysExpandDummyNodes(){
    $(".dummy").each(function(){
        toggle_hide_no_alternating_colors("sub-" + $(this).attr("id"));
        // ids of the arrow/folder image is 2-typeimg, when question is q_2    
        var typeimgID = $(this).attr("id") + '-typeimg';
    
        // get the div of the arrow/folder image
        var imgDiv = $("[id^="+ typeimgID + "]");
        imgDiv.attr('src', 'img/openedArrow.png');
    });
};

function toggle_hide_no_alternating_colors(id){
    $("#" + id).toggle(0);
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
    var auxHeader = "<b>FRAGE:</b> " + title + "<br /><br />";
    var auxinfo = $(infoid).html();
    
    // rewrite inner HTML of infopanel widget with info content
    if(auxinfo==""){
        auxinfo = "-";
    } 
    
    $("#auxHeader").html(auxHeader);
    $("#auxInfo").html(auxinfo);
    
    
    // similarly write potentially linked resources
    var lrid = "#lr-" + id;
    var resinfo = $(lrid).html();
    //var resif = $(lrid).attr("showif");
    
    $("#linkedResources").html(resinfo);
}

function hideAuxInfo(){
    
    // clear infopanel
    $("#auxHeader").html("<b>FRAGE: </b> <br /><br />");
    $("#auxInfo").html("-");
}

function showAuxPropInfo(){
    var info = "<b>Gewählte Antwort widerspricht der aus den Detailfragen hergeleiteten Bewertung. ";
    info += "<br />Löschen Sie mindestens eine Antwort durch Klick auf den X-Button der jeweiligen Detailfrage, ";
    info += "wenn Sie eine andere als die bisher hergeleitete Bewertung setzen möchten.";
    $("#auxPropagationInfo").html(info);
}

function hideAuxPropInfo(){
    $("#auxPropagationInfo").html("");
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
 * Toggle the subelements for special hierarchical dialogs
 * @param id
 */
function toggle_sub_4boxes_clariHie(id) {
	
    toggle_hide_ITree("sub-" + id); 
   
}

function toggle_hide_ITree(id) {
   
    if(d3web){
        id = id.replace("sub-", "");
        
        toggleShowStatus_ITree(id); // toggle folder image
        hide_all_tooltips();	// on toggling all tooltips should disappear
    } 
}


/**
 * Toggle folder image (open/close) for the legal dialog style
 */
function toggleShowStatus_ITree(id) {
	
   
    // ids of the arrow/folder image is 2-typeimg, when question is q_2    
    var typeimgID = id + '-typeimg';
    
    // get the div of the arrow/folder image
    var imgDiv = $("[id^="+ typeimgID + "]");
    
    
    var qtext = $('#solutiontitle-' + id).html();
    
    // CLOSE the element and children
    if (imgDiv.attr('src') == 'img/openedArrow.png') {
        
        imgDiv.attr('src', 'img/closedArrow.png');
       
        if(logging && !$("#" + id).hasClass("dummy")){
            ue_logQuestionToggle(qtext, "SHUT");
        }
        
        deleteExpandCookie(id);
        writeExpandCookie(id, "C");
    } 
    
    // OPEN the element and children
    else if (imgDiv.attr('src') == 'img/closedArrow.png') {

        imgDiv.attr('src', 'img/openedArrow.png');
        if(logging && !$("#" + id).hasClass("dummy")){
            ue_logQuestionToggle(qtext, "EXPAND");
        }
        //alert("toggle: " + id + " - opened");
        deleteExpandCookie(id);
        writeExpandCookie(id, "O");
    } 
    // re-render only changed question
    d3web_show();
}


/**
 * Color background and mark selected answer in Legal style dialogs
 * TODO factor out d3web
 * This is ONLY for prototypes!
 * 
 * @param id the id of the item to mark
 * @param value the value that indicates in what regard the item is marked
 */
function handleITreeYNQuestions(fullId, rating) {
    	
    // retrieve follow up element to current element
    var splitID = fullId.split("-")[1];
    var questionEl = $("#"+splitID);
   
    prepareQuestionLogging(fullId, rating);
    
    setColorForQuestion(questionEl, questionEl, rating);
    setPropagationColor(questionEl, rating);
    // also mark parents of the target while excluding target
    h4boxes_mark(questionEl, true);
    
    // calculate solution rating and display
    calculateAndHandleSolutionRating();
 
    hide_all_tooltips();
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




// TODO: refactor this one, it is similarly used in code.js and clariHie.js
// only in code.js more generally
function generate_tooltip_functions_propagation() {
	
    var triggers = $("[class*='-tt-trigger-prop']");
           
    // if mouse is moved over an element define potential tooltips position
    $(document).mousemove(function(e) {
        tooltip_move(e);
    });
	
    // go through all existing tooltip triggers
    triggers.each(function() {
        	
        var id = $(this).attr("id").replace("propagation-", "");
        
        var ttstart, ttend;
        var now;
                
        $(this).unbind('mouseover').mouseover(function() {
            //if logging is activated get the time tooltip is triggered
            if(logging){
                now = new Date();
                ttstart = now.getTime();
            }
            
            tooltip_over_prop(id);
        });
     
        $(this).unbind('mouseout').mouseout(function() {
            //if logging is activated get the time tooltip is deactivated again
            if(logging){
                now = new Date();
                ttend = now.getTime();
                ue_logInfoPopup(ttstart, ttend, $(this));
            }
          
            tooltip_out_prop(id);
            
        });
    });
}

function tooltip_over_prop(id) {
    targetid = "tt-propagation-" + id;
    	
    var target = $("[id$=" + targetid + "]");
             
    if (target.size() == 0) {
        return;
    }
	
    // if target element is not currently shown
    if (target !== tooltipShown) {
		
        // hide old tooltip if existing
        if (tooltipShown !== undefined) {
            tooltip_out(tooltipShown);
        }
		
        // store currently shown tooltip and tooltipShownTrigger
        tooltipShown = target;
		
        target.css("position", "absolute");
        var height = target.height();
        var width = target.width();
        if (height > 0 && width > 0 && height > width) {
            target.css("width", height);
            target.css("height", width);
        }
        //tooltip_move(element);

        target.fadeIn(300);
        setLeftOffset(target);
    }
}

function tooltip_out_prop(object) {
	
    // if a jquery tooltip or
    if (object instanceof jQuery) {
        target = object;
    } else {
		
        // a specifically marked element
        target = $("#tt-propagation-" + object);
    }

    target.hide(500);
    tooltipShown = undefined;
//tooltipShownTrigger = undefined;
}

/*--------------------------*/
/* clarihie d3web dialogs */
/*--------------------------*/
function d3web_answerYesNoHierarchyQuestions(buttonId, rating){
    
    // handle logging if activated
    if (logging) {
        var now = ue_getCurrentDate();		
    }
    
    
    // assemble ajax call
    var link = $.query.set("action", "addFactsYN");
    //link = link.set("timestring", now);
    
    var qid = $(buttonId).closest("[id^=q_]").attr("id").replace("-imagebox", "");
    
    link = link.set("question", qid).set("value", rating);
    
    
    link = window.location.href.replace(window.location.search, "") + link;
        
  
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success : function(html) {
            if(html.indexOf("ITreeSUCCESS")==0){
                window.location.reload();
                initFunctionality();
            }
        },
        error : function(html) {
            alert("ajax error add facts");
        }
    });
}


/* SAVE DATE QUESTIONS FOR ITREE */
function d3web_addValueFactITree(qid, value){
   
    var link = $.query.set("action", "addFactITree");
    link = link.set("question", qid).set("value", value);
    link = window.location.href.replace(window.location.search, "") + link;
     
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success : function(html) {
            
            window.location.reload(true);
            initFunctionality();
        },
        error : function(html) {
            alert("ajax error add facts itree date");
        }
    });
}

