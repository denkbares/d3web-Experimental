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
    alert(rating);
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