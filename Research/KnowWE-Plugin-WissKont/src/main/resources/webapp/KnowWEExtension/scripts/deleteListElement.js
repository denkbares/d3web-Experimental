jq$(document).ready(function() {
	initAllDeleteItem();
});

function initAllDeleteItem() {
    hideDeleteIcons();

    initDeleteIconHovers();
    
    initDeleteButtons();
    
}

function initDeleteButtons() {
	  jq$(".deleteButton").each(function() {
	    	initClickEvent(jq$(this));
	   });
}

function hideDeleteIcons() {
	jq$(".deleteButton").each(function() {
    	jq$(this).hide();
    });
}

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
	var entryKdomid = entry.attr('id');
	var markupKdomid = entry.parent().parent().parent().parent().attr('id');
	
	var params = {
			action : 'DeleteListEntryAction',
			kdomid   : entryKdomid 
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function(){
				 // todo: smooth reload by section update
				 // hint: reinit listeners etc.
				 //location.reload();
				 rerenderSection(markupKdomid, this.responseText);
			 }
		 },
	}
	
	 new _KA(options).send();
	
}