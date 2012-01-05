var weekday = new Array("So", "Mo", "Di", "Mi", "Do", "Fr", "Sa");

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
    if(parentid.indexOf("text-") != -1){
        parentid = parentid.replace("text-", "");
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
   
   // TODO log not allowed input
    //alert("log not allowed also?")
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
    var datestring = weekday[date.getDay()] + " " + 
        date.getFullYear() + "_" + date.getMonth()+1 + "_" + date.getDate() + " " + 
        date.getHours() +  ":" + date.getMinutes() + ":" + date.getSeconds();
    return datestring;
}