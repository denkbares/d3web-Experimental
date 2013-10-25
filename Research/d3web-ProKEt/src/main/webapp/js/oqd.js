//
// THIS FILE CONTAINS ALL NECESSARY JAVASCRIPT FOR ONE QUESTION DIALOGS
//

/**
 * Color background and mark selected answer in Legal style dialogs
 * TODO factor out d3web
 * This is ONLY for prototypes!
 * 
 * @param id the id of the item to mark
 * @param value the value that indicates in what regard the item is marked
 */
function updateOneQuestionDialog(html) {
    	
	var splitHtml;
	var id;
	var value;
	
	if (html.indexOf("UpdateOQD###")!= -1) {
	    splitHtml = html.split("###");
	    id = splitHtml[1];
	    value = splitHtml[2];
	    
	    $("#" + splitHtml[1].replaceWith(value));
	}
	
    
}


  
