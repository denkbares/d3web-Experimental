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
    	jq$(this).css("visibility", "hidden" );
    });
}

function initDeleteIconHovers() {
	
	jq$(".deletableListElement").each(
			function() {
				var icon = jq$(this);
				jq$(this).hover(
						function () {
							icon.find(".deleteButton").each( 
									function() {
										jq$(this).css("visibility", "visible" );
									});
						}, 
						function () {
							icon.find(".deleteButton").each( 
									function() {
										jq$(this).css("visibility", "hidden" );
									});
						}
				);
			}
	);
}


function initClickEvent(element) {
	element.bind("click", function() {
		sendDeleteAction(jq$(this));
	});
}


function sendDeleteAction(entryKdomid) {

	var entry = jq$("#" + entryKdomid);
	var markupKdomid = entry.parent().parent().parent().parent().parent().attr('id');
	
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
				 
				 rerenderOverviewGraph();
			 }
		 },
	}
	
	 new _KA(options).send();
	
}