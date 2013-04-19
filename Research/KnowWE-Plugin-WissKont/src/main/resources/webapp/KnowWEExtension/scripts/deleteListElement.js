jq$(document).ready(function() {
    jq$(".deleteButton").each(function() {
    	jq$(this).hide();
    });

    initDeleteIconHovers();
    
    jq$(".deleteButton").each(function() {
    	initClickEvent(jq$(this));
    });
    
});

function initDeleteIconHovers() {
	jq$(".deletableListElement").each(function() {
    	initIconHover(jq$(this));
    });
}

function initClickEvent(element) {
	element.bind("click", function() {
		sendDeleteAction(jq$(this));
	});
}


function sendDeleteAction(element) {

	var entry = element.parent().parent().parent().parent().parent();
	var kdomid = entry.attr('id');
	
	var params = {
			action : 'DeleteListEntryAction',
			kdomid   : kdomid 
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function(){
				 // todo: smooth reload by section update
				 // hint: reinit listeners etc.
				 location.reload();
			 }
		 },
	}
	
	 new _KA(options).send();
	
}