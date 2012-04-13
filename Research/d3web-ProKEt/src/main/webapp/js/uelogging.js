var weekday = new Array("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa");

$(function(){
    
    /* Usability Extension: Feedback Form */
    $(function() {

        var sendFF, cancelFF;
        if(!study==true && language=="en"){
            sendFF = "Send";
            cancelFF = "Cancel";
        } else if(language=="de" || study==true){
            sendFF = "Abschicken";
            cancelFF = "Abbrechen";
        }
        var opts = {
            autoOpen: false,
            position : top,
            width : 480,
            height : 350,
            minWidth : 480,
            minHeight : 300,
            draggable : false,
            resizable : false,
            modal : false,
            // two custom buttons
            buttons : [{
                id: "sendFF",
                text: sendFF,
                click: function(){
                    ue_sendFF();
                }
            },
            {
                id: "cancelFF",
                text: cancelFF,
                click: function(){
                    $('#jqFFDialog').dialog("close");
                }
            }]
        };
        $("#jqFFDialog").dialog(opts);
    });
    
    /* Usability Extension: Usability Questionnaire */
    $(function() {
        
        var sendUEQ, cancelUEQ;
        if(!study==true && language=="en"){
            sendUEQ = "Send";
            cancelUEQ = "Cancel";
        } else if(language=="de" || study==true){
            sendUEQ = "Abschicken";
            cancelUEQ = "Abbrechen";
        }
        
        
        var opts = {
            autoOpen: false,
            position : top,
            width : 720,
            height : 520,
            minWidth : 720,
            minHeight : 520,
            draggable : false,
            resizable : false,
            modal : false,
            // two custom buttons
            buttons : [{
                id: "sendUEQ",
                text: sendUEQ,
                click: function(){
                    if(study && logging){    
                        ue_logDiagnosis();
                        ue_sendUEQ();
                    }
                }
            },
            {
                id: "cancelUEQ",
                text: cancelUEQ,
                click: function(){
                    $('#jqUEQDialog').dialog("close");
                }
            }]
        };
        $("#jqUEQDialog").dialog(opts);
    });
});


/**
 * Retrieve the browser information, i.e. the type and version. 
 * Returns a String in the form "<type> <version>"
 */
function retrieveBrowserVal(){
    
    return BrowserDetect.browser + " " + BrowserDetect.version;
}

function retrieveUserVal(){
    // TODO get the correct logged in user here
    return "user";
}

/**
 * Send Ajax request for logging basic information, currently browser information
 * type and version, as well as user info
 */
function ue_logBrowserAndUser(browser, user){
    
    var now = ue_getCurrentDate();
      
    var link = $.query.set("action", "logInit").set("timestring", now).set("browser", browser)
    .set("user", user).toString();
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function() {
        //d3web_resetSession();
        //window.location.reload(true);
        //initFunctionality();
        }
    });
}

// log clicks on widgets that do NOT set values in d3web, e.g. info button,
// reset, save case etc
function ue_logWidgetClicked(el){
    
    var now = ue_getCurrentDate();
    if (logging) {
        var link = $.query.set("action", "logWidget").set("timestring", now).set("widget", el.attr("id")).toString();
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

function ue_logLanguageWidgetClicked(el){
    
    var now = ue_getCurrentDate();
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
    
    var link = $.query.set("action", "logWidget").set("timestring", now).set("widget", el.attr("id")).set("language", lang).toString();
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
function ue_logEnd(){
    
    var now = ue_getCurrentDate();
    var link = $.query.set("action", "logEnd").set("timestring", now);
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
 * Log call time (start), time difference (duration), and id
 * of the parent widget of an info popup.
 */
function ue_logInfoPopup(starttime, endtime, widget){
    
    var start = ue_formatDateString(new Date(starttime));
    
    var diffDate = new Date(endtime-starttime);
    var timediff = diffDate.getHours()-1 +  ":" 
    + diffDate.getMinutes() + ":" 
    + diffDate.getSeconds();
    
    var parentid = widget.parent().attr("id");
    if(parentid != undefined){
        if(parentid.indexOf("text-") != -1){
            parentid = parentid.replace("text-", "");
        }
    } else {
        parentid = $(widget).attr("id");
       
    }
    
    var id = parentid + "_" + "INFOPOPUP";
 
    var link = $.query.set("action", "logInfoPopup").set("id", id).set("value", timediff).set("timestring", start);
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
* log attempts to set unallowed inputs
*/
function ue_logNotAllowedInput(numInput){
   
    var now = ue_getCurrentDate();       
    var link = $.query.set("action", "logNotAllowed").set("timestring", now)
    .set("id", getQuestionId(numInput)).set("value", parseInt($(numInput).val()));
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
* Retrieves the current date and returns it in the form
* Mo 2012_01_24 02:30:59
*/
function ue_getCurrentDate(){
    
    var now = new Date(); // retrieve the current date
    return ue_formatDateString(now);
}

/**
* Formats a given date object in the form and res
* Mo 2012_01_24 02:30:59
*/
function ue_formatDateString(date){
    var month = 1+date.getMonth();
    
    var datestring = weekday[date.getDay()] + " " + 
    date.getFullYear() + "_" + month + "_" + date.getDate() + " " + 
    date.getHours() +  ":" + date.getMinutes() + ":" + date.getSeconds();
    return datestring;
}

/**
* Sends an ajax request to deliver an automatical email with user feedback
* to the developers
*/
function ue_sendFF(){
    
    var user = $('#ffname').val();
    var contact = $('#ffmail').val();
    var feedback = $('#fffeedback').val();
    var now = ue_getCurrentDate();
		
    var link = $.query.set("action", "sendFeedbackMail")
    .set("user", user)
    .set("contact", contact)
    .set("feedback", feedback).toString();
    
    var message = "";
    
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            if(html=="success"){
                // display success message to user?"  
                $("#ffmessage").html("");
                $("#ffmessage").removeClass("errorRed");
                $('#jqFFDialog').dialog("close");
                ue_logUEFeedback(user, contact, feedback, now);
        
            } else if(html=="nofeedback"){
                // display message that feedback is NOT optional 
                message = "Please fill in the field 'Feedback' before sending the form!";
                $("#ffmessage").html(message);
                $("#ffmessage").addClass("errorRed");
            }
        }
    });
// TODO: remove mail stuff later
    
}


/**
* Sends an ajax request to deliver an automatical email with user feedback
* to the developers
*/
function ue_sendUEQ(){
    
    var user = $('#ueqname').val();
    if(user==undefined){
        user = "";
    }

    var contact = $('#ueqmail').val();
    if(contact==undefined){
        contact = "";
    }
        
    var questionnaireData = ue_retrieveQuestionnaireData();
    /*if(!ue_dataComplete(questionnaireData)){
        
        // display message that feedback is NOT optional 
        message = "Please fill in the complete survey!";
        $("#ueqMessage").html(message);
        $("#ueqMessage").addClass("errorRed");
        
    } else { */
        
    var link = $.query.set("action", "sendUEQMail")
    .set("user", user)
    .set("contact", contact)
    .set("questionnaireData", questionnaireData).toString();
    
    var message = "";
    
    link = window.location.href.replace(window.location.search, "") + link;

    $.ajax({
        type : "GET",
        async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            if(html=="success"){
                // display success message to user?"  
                $("#ueqMessage").html("");
                $("#ueqMessage").removeClass("errorRed");
                $('#jqUEQDialog').dialog("close");
            } 
        }
    });
    
    ue_logUEQData(user, contact, questionnaireData); 
//}

}

/**
* Retrieves the data = question-value-pairs of the currently integrated
* usability questionnaire in div "ueq"
* returns Questionnaire Data in format: 
*  questionID1***value1###questionID2***value2###
*  TODO: retrieve also not answered questions for logging
*/
function ue_retrieveQuestionnaireData(){
    
    var qData = "";
    
    $("#ueq input:radio:checked").each(function(){
        qData += $(this).attr("id").replace("UE_", "") + "---" + $(this).attr("value") + "###"; 
    });
    
    var freeFeedback = "/";
    var uiRating = $("#UE_QUIRating"); 
    if(uiRating.val()!=undefined && uiRating.val()!=""){
        freeFeedback = uiRating.val();
    }
    qData += uiRating.attr("id").replace("UE_", "") + "---" + freeFeedback + "###";
    
    
    var freeFBField = $("#UE_QFreeFeedback"); 
    if(freeFBField.val()!=undefined && freeFBField.val()!=""){
        freeFeedback = freeFBField.val();
    }
    qData += freeFBField.attr("id").replace("UE_", "") + "---" + freeFeedback + "###";
    
    return qData;
}

/**
* Checks whether the given questionnaire data (q-a-pairs) reflect the complete
* questionnaire or whether some questions hadn't been answered
*/
function ue_dataComplete(qData){
    
    var testid;
    var flag = true;
    
    $("#ueq input:radio").each(function(){
        testid = $(this).attr("id").replace("UE_", "");
        if(qData.indexOf(testid)==-1){
            flag = false;
        }
    });
    return flag;
}

function ue_logDiagnosis(){
    
    var now = ue_getCurrentDate();  
    var rating;
    var id;
    var rootId = retrieveRootQuestionIdInHierarchyPrototype()
    
    // TODO: refactor that both dialogs have "solutiontext"; then
    // the if/else needs to be queried otherwise 
    // and StringTemplate needs adaption
    // 
    // in OQD Dialog 
    if($("#solutiontext").attr("id") != undefined){
        rating = ue_getSolutionRating($("#solutiontext"));
        id = $("#solutiontext").html();
    } else 
    
    // in HierarchyDialog
    if($("#" + rootId).hasClass("solutiontext"))
    {
        if($("#" + rootId).hasClass("rating-high")){
            rating = "1";
        } else if ($("#" + rootId).hasClass("rating-med")){
            rating = "2";
        } else if ($("#" + rootId).hasClass("rating-low")){
            rating = "3";
        } else if (!$("#" + rootId).hasClass("rating-high")  
            && !$("#" + rootId).hasClass("rating-med") 
            && !$("#" + rootId).hasClass("rating-low") ){
            rating = "0";
        }
        
        id = $("#solutiontitle-" + rootId).html();
    }
    
    
    var link = $.query.set("action", "logDiagnosis")
    .set("id", id).set("rating", rating);
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

function ue_getSolutionRating(solutiontextid){
    var rating = "-1";
    $(solutiontextid).siblings().each(function(){
        
        if($(this).attr("id").indexOf("solHigh")!=-1 
            && !($(this).hasClass("hide"))){
            rating = "1";
        } else 
        if($(this).attr("id").indexOf("solMed")!=-1 
            && !($(this).hasClass("hide"))){
            rating = "2";
        } else 
        if($(this).attr("id").indexOf("solLow")!=-1 
            && !($(this).hasClass("hide"))){
            rating = "3";
        } else 
        if($(this).attr("id").indexOf("solUn")!=-1 
            && !($(this).hasClass("hide"))){
            rating = "0";
        } 
    });
    return rating;
}

function ue_logUEFeedback(user, contact, feedback, now){
    var link = $.query.set("action", "logUEFeedback")
    .set("user", user)
    .set("contact", contact)
    .set("ueFeedback", feedback)
    .set("timestring", now).toString();
    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            if(html=="success"){
               
            } 
        }
    });
}

function ue_logUEQData(user, contact, questionnaireData){
    var link = $.query.set("action", "logUEQuestionnaire")
    .set("user", user)
    .set("contact", contact)
    .set("ueQData", questionnaireData).toString();
    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
            if(html=="success"){
                ue_logEnd();
            } 
        }
    });
}

function ue_logQuestionValueClariPrototype(questiontext, value){
    
    var now = ue_getCurrentDate();
        
    var link = $.query.set("action", "logQuestion")
    .set("qtext", questiontext)
    .set("qvalue", value)
    .set("timestring", now).toString();
    
    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
             
        }
    });
}

function ue_logDialogType(type){
    
    var now = ue_getCurrentDate();
        
    var link = $.query.set("action", "logDialogType")
    .set("type", type).toString();
    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
             
        }
    });
}

function ue_logQuestionToggle(qtext, ttype){
    
    var now = ue_getCurrentDate();
        
    var link = $.query.set("action", "logQuestionToggle")
    .set("ttype", ttype).set("qtext", qtext).set("timestring", now).toString();
    $.ajax({
        type : "GET",
        // async : false,
        cache : false, // needed for IE, call is not made otherwise
        url : link,
        success : function(html) {
             
        }
    });
}