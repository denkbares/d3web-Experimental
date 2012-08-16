// 
// THIS FILE CONTAINS ALL FUNCTIONALITY THAT IS NEEDED BY ALL PROTOTYPES,
// E.G. GENERAL TOOLTIP STUFF

var tooltipShown = undefined;


/* TOOLTIP STUFF - Tooltips are always defined by JS so they
 * are needed globally by d3web and prototype dialogs 
 */
/* Generate tooltips for the answer buttons of yes no questions */
// needed in iTree and conversational OQD
function generate_tooltip_functions_ynbuttons(){
    triggers = $("[class*='-tt-trigger-ynbutton']");
	
    // if mouse is moved over an element define potential tooltips position
    $(document).mousemove(function(e) {
        tooltip_move(e); // in code.js
    });
	
    // go through all existing tooltip triggers
    triggers.each(function() {
		
        var ttstart, ttend;
        var now;
                
        $(this).unbind('mouseover').mouseover(function() {
            //if logging is activated get the time tooltip is triggered
            if(logging){
                now = new Date();
                ttstart = now.getTime();
            }
            
            var fullId = $(this).attr("id");
           
            if(fullId.indexOf("ynYes")!=-1){
                id = $(this).attr("id").replace("ynYes-", "");
                tooltip_over_hierarchy_buttons(id, "1");
            } else if(fullId.indexOf("ynNo")!=-1){
                id = $(this).attr("id").replace("ynNo-", "");
                tooltip_over_hierarchy_buttons(id, "3");
            } else if(fullId.indexOf("ynUn")!=-1){
                id = $(this).attr("id").replace("ynUn-", "");
                tooltip_over_hierarchy_buttons(id, "2");
            } else if(fullId.indexOf("ynNan")!=-1){
                id = $(this).attr("id").replace("ynNan-", "");
                tooltip_over_hierarchy_buttons(id, "0");
            }
        });

        $(this).unbind('mouseout').mouseout(function() {
            //if logging is activated get the time tooltip is deactivated again
            if(logging){
                now = new Date();
                ttend = now.getTime();
                ue_logInfoPopup(ttstart, ttend, $(this));
            }
            
            var fullId = $(this).attr("id");
            if(fullId.indexOf("ynYes")!=-1){
                id = $(this).attr("id").replace("ynYes-", "");
                tooltip_out_hierarchy_buttons(id, "1");
            } else if(fullId.indexOf("ynNo")!=-1){
                id = $(this).attr("id").replace("ynNo-", "");
                tooltip_out_hierarchy_buttons(id, "3");
            } else if(fullId.indexOf("ynUn")!=-1){
                id = $(this).attr("id").replace("ynUn-", "");
                tooltip_out_hierarchy_buttons(id, "2");
            } else if(fullId.indexOf("ynNan")!=-1){
                id = $(this).attr("id").replace("ynNan-", "");
                tooltip_out_hierarchy_buttons(id, "0");
            }
        });
    });
}



/* Hide the tooltip, specifically for itree dialogs */
// TODO: rename hierarchy to iTree
function tooltip_out_hierarchy_buttons(object, button) {
	
    switch(button){
        case "1":
            targetid = "tt-" + object + "-Y";
            break;
        case "2":
            targetid = "tt-" + object + "-U";
            break;
        case "3":
            targetid = "tt-" + object + "-NO";
            break;
        case "0":
            targetid = "tt-" + object + "-NAN";
            break;
    }

    // if a jquery tooltip or
    if (object instanceof jQuery) {
        target = object;
    } else {
		
        // a specifically marked element
        target = $("#" + targetid);
    }

    target.hide(500);
    tooltipShown = undefined;
}

/* Show the tooltips for buttons in yn questions in clarihie */
function tooltip_over_hierarchy_buttons(id, button) {
   
    switch(button){
        case "1":
            targetid = "tt-" + id + "-Y";
            break;
        case "2":
            targetid = "tt-" + id + "-U";
            break;
        case "3":
            targetid = "tt-" + id + "-NO";
            break;
        case "0":
            targetid = "tt-" + id + "-NAN";
            break;
    }
    
   	
    //target = $("#tt-" + id).filter(":not(:animated)");
    var target = $("[id^=" + targetid + "]");
        
        
        
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
 * Hide all the tooltip elements, starting with an "tt-"
 */
function hide_all_tooltips() {
    $("[id^='tt-']").hide(0);
    tooltipShown = undefined;
    tooltipShownTrigger = undefined;
}

function setLeftOffset(target) {
    
    // get current coordinates of element relative to the document
    var pOffset = target.parent().offset();
   
    // width of current element
    var width = target.width();
    var height = target.height();
    
    // window width
    var widthW = $(window).width() - 25; // remove some for the scrollbar
    // real height of the displayed dialog inside the browser window
    var heightW = document.documentElement.clientHeight;
    
    // calculate appropriate distance to left border
    var overlap = pOffset.left + width - widthW;
    var leftOffset = pOffset.left
    if (overlap > 0){
        leftOffset = pOffset.left - overlap;
        if (leftOffset < 0) {
            leftOffset = 0;
        }
    }
   
    /* display all popups that are too far down in the dialog relatively above
    * the parent element */
    var sizeToEnd = heightW - pOffset.top;
    if(sizeToEnd < 400){
        target.offset({
            top: pOffset.top - height - 20, 
            left: leftOffset
        });
    } 
    
    /* display popups normally underneath the parent element */
    else {
        target.offset({
            top: pOffset.top + target.parent().height() + 15, 
            left: leftOffset
        });
    }
    target.width(width);
}


/**
 * TOGGLING in iTREE STYLE
 */

/**
 * Toggle the subelements for special hierarchical dialogs
 * @param id
 */
function toggle_sub_4boxes(id) {
    toggle_hide("sub-" + id); 
    toggle_folder_image_4boxes(id);
    hide_all_tooltips();
}


//// USABILITY STUFF FOR PROTOTYPES
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
        $("#fffeedback").val("");
        $("#jqFFDialog").dialog("open");
    });
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
        
        if(text.indexOf("Dialog 1 ODER Dialog 2 ODER Beide gleich")
            !=-1){
            $('#UE_QUIRating').val("");
        }
    
        $('#UE_QUIRating').addClass("longTextfieldInputColor");
    });
}


/* Make first detail-sub question visible for the given question */
// only for prototypes iTree and OQD
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
        
        hide_all_tooltips();
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

// needed by iTree and OQD
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

/*
 * Sets the given rating for the given target question
 * iTree and OQD
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


// set the color for the area indicating the system-propagated value
function setPropagationColor(question, userval){
    
    var prop = $("#propagation-"+question.attr("id"));
    var propColor = calculateRatingForQuestion(question);
    
    if(propColor != 0 && propColor != userval){
        
        // visually present the "error" to the user
        if((propColor=="1" && userval=="3") || (propColor=="3" && userval=="1")){
            prop.addClass("prop1and3");
            prop.removeClass("prop1and2");
            prop.removeClass("prop2and3");
        } 
        else if((propColor=="1" && userval=="2") || (propColor=="2" && userval=="1")){
            prop.addClass("prop1and2");
            prop.removeClass("prop1and3");
            prop.removeClass("prop2and3");
        } 
        else if((propColor=="2" && userval=="3") || (propColor=="3" && userval=="2")){
            prop.addClass("prop2and3");
            prop.removeClass("prop1and2");
            prop.removeClass("prop1and3");
        } 
       
        // set the calculated value of the inner questions as question main rating
        // for further calculation
        setColorForQuestion($("#" + question.attr("id")), question, propColor);
    }
    if(userval != propColor && propColor != 0 && userval != undefined && userval != 0){
        prop.removeClass("hide");
        prop.addClass("show");
        // needed to keep the propagation tooltip hidden!
        prop.children().first().addClass("hide");
        
    } else {
        prop.removeClass("show");
        prop.addClass("hide");
    }
//removePropagationInfoInQuestionForFirst();
  
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
            color = "2"; // TODO: check if works
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

// for itree and oqd
function calculateAndHandleSolutionRating(){
    
    var solQuestion;  
    if(itree){
        solQuestion = $("#dialog").children().first().next();
    } else {
        solQuestion = $("#dialog").children().first();
    }
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
}


/************************/
/* COOKIE RELATED STUFF */
/************************/

/* write expand state cookie for a given question qname */
function writeExpandCookie(qname, qExpandState){
    
    var cookieString = qname + "=" + qExpandState + ";";
    document.cookie = cookieString;
}

/* Delete expand-state cookie for a question qname*/
function deleteExpandCookie(qname){
    document.cookie = qname + "=; expires=Thu, 01-Jan-70 00:00:01 GMT;";
}