/*--------------------*/
 /* Global variables */
/*-------------------*/
var rootQuestionId = "";

/*------------------------------------------*/
 /* Initialization for clarihie prototypes */
/*-----------------------------------------*/
function clariHieInit() {
    
    generate_tooltip_functions_ynbuttons();
        
    setRootQuestionIdInHierarchyPrototype();
        
    expandAndStyleFirstElement();
    
    exchangeReadflowTextFirstSubQuestion();
    
    initializeSolutionBox();
        
    // initialize mechanism for num fields to check entered values etc
    initializeNumfields();
        
    // initialize mechanism for dropdwons to check entered values etc
    initializeDropdownSelects();
        
    alwaysExpandDummyNodes();    

    hide_all_tooltips(); // hide tooltips
 
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

function initializeSolutionBox(){
    
    var rootSolId = "[id^='solutiontitle-" + rootQuestionId + "']";
    // get text of the root element of the tree = the solution
    var soltext = $("#"+rootSolId).html();
    // set the solutiontext in the box
    $("#solutiontext").html(soltext);
}


/* Generate tooltips for the answer buttons of yes no questions */
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

/* Hide the tooltip */
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
 * Retrieve the topmost element in hierarchically specified XML prototypes.
 * Usually this is the first <question> element.
 */
function setRootQuestionIdInHierarchyPrototype(){   
    rootQuestionId = $("[id^=dialog] > [id^=q_]").first().attr("id");
}

/**
 * Retrieve the first element in hierarchical dialogs and expand it on startup
 * Used e.g. in hierarchy (legal) dialog
 */
function expandAndStyleFirstElement(){
    
    $("#" + rootQuestionId).addClass('solutiontext'); // style first element
    
    // reset contents of input facilities part of first element
    var inputFacilitiesElement = 
    $("#" + rootQuestionId.replace("q_", "") + "-imagebox");
    inputFacilitiesElement.html("<div id='solutionboxtextInTree'>Hauptfrage</div>");
            
    toggle_sub_4boxes(rootQuestionId);   // expand the first element
    
    // remove propagation info fiel for first element
    var prop = $("#propagation-" + rootQuestionId);
    prop.removeClass("show");
    prop.addClass("hide");
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
    }
    else {
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
    var typeimgID = id + '-typeimg';
    
    // get the div of the arrow/folder image
    var imgDiv = $("[id^="+ typeimgID + "]");
    
    var qtext = $('#solutiontitle-' + id).html();
    
    var topelem = false;  
    if($('#' + id.replace("q_", "") + "-imagebox").html().indexOf("solutionboxtext") != -1){
        topelem = true;
    };
    
    if (imgDiv.attr('src') == 'img/openedArrow.png') {
        
        imgDiv.attr('src', 'img/closedArrow.png');
       
        if(logging && !$("#" + id).hasClass("dummy")){
            ue_logQuestionToggle(qtext, "SHUT");
        }
        
    } else if (imgDiv.attr('src') == 'img/closedArrow.png') {

        imgDiv.attr('src', 'img/openedArrow.png');
        if(logging && !topelem && !$("#" + id).hasClass("dummy")){
            ue_logQuestionToggle(qtext, "EXPAND");
        }
    } 
}


/**
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
		
    calculateAndHandleSolutionRating();
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

/*--------------------------*/
 /* clarihie d3web dialogs */
/*--------------------------*/
function d3web_answerYesNoHierarchyQuestions(buttonId, rating){
    
    // handle logging if activated
    if (logging) {
        ue_logDialogType("ClariHIE"); 
        
        var now = ue_getCurrentDate();		
    }
    
    // assemble ajax call
    var link = $.query.set("action", "addFacts");
    link = link.set("timestring", now);
    
    var qid = $(buttonId).closest("[id^=q_]").attr("id").replace("-imagebox", "");
    
    link = link.set("question", qid).set("value", rating);
    alert(qid);
    $.ajax({
        type : "GET",
        url : link,
        cache : false, // needed for IE, call is not made otherwise
        contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
        success : function(html) {
            
            // TODO: cookies for storing expand state of dialog!
            window.location.reload();
            initFunctionality();
        },
        error : function(html) {
            alert("ajax error add facts");
        }
    });
}