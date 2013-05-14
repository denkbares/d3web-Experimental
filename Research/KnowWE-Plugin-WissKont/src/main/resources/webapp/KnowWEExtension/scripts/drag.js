jq$(document).ready(function() {
    jq$(".termline").each(activateDraggables);
        
    jq$(".relationMarkup").each(function() {
    	jq$(this).droppable({
    			drop: function(event, ui) { 
    				var termElement = ui.draggable;
    				var termnameDiv = termElement.find("div.termname");
    				var termname = termnameDiv.html();
    				termname = termname.replace(/<wbr>/g, "");
    				var markupElement = jq$(this).find("div.defaultMarkupFrame");
    				var markupID = markupElement.attr('id');
    				//alert('dropped: '+ termname + ' on id: '+markupID  );
    				sendAddedTerm(termname, markupID);
    				},
    			hoverClass: "drophover",
    	})
    });

    initAllIconHovers();
    
    initAllBrowserActionEvents();
    
    initCollapseTermBrowser();
});


function initCollapseTermBrowser() {
	jq$(".showList").each(function() {
		jq$(this).bind('click', function() {
			jq$(".showList").hide();
			jq$(".hideList").show();
			jq$(".termlist").show('50');
			// stores user's collapse state on server
			handleTermActionEvent(jq$(this));
		});
		
	});
	jq$(".hideList").each(function() {
		jq$(this).bind('click', function() {
			jq$(".hideList").hide();
			jq$(".showList").show();
			jq$(".termlist").hide('50');
			// stores user's collapse state on server
			handleTermActionEvent(jq$(this));
		});
		
	});
	
}



function initAllBrowserActionEvents() {
	jq$(".removeConcept").each(function() {
    	initClickEvents(jq$(this));
    });
    jq$(".openConcept").each(function() {
    	initClickEvents(jq$(this));
    });
    jq$(".expandConcept").each(function() {
    	initClickEvents(jq$(this));
    });
    jq$(".collapseConcept").each(function() {
    	initClickEvents(jq$(this));
    });
    initCollapseTermBrowser();

}

function initClickEvents(element) {
	element.bind("click", function() {
		handleTermActionEvent(jq$(this));
	});
}

function handleTermActionEvent(element) {
	var command = 'noop'
	if(element.hasClass('removeConcept')) {
		command = 'remove';
	}
	if(element.hasClass('hideList')) {
		command = 'collapseList';
	}
	if(element.hasClass('showList')) {
		command = 'openList';
	}
	if(element.hasClass('hideGraph')) {
		command = 'collapseGraph';
	}
	if(element.hasClass('showGraph')) {
		command = 'openGraph';
	}
	if(element.hasClass('expandConcept')) {
		command = 'expand';
	}
	if(element.hasClass('collapseConcept')) {
		command = 'collapse';
	}
	if(element.hasClass('openConcept')) {
		command = 'open';
	}
	
	var line = element.parent().parent().parent().parent().parent().parent();
	var termnameElement = line.find('div.termname');
	var term = termnameElement.html();
	if(term) {
		term = term.replace(/<wbr>/g, "");
	}
	
	sendTermBrowserAction(term, command);
	
}

function sendTermBrowserAction(term, command) {
	var params = {
			action : 'TermBrowserAction',
			term   : term,
			command   : command,
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function(){
				 // insert new browser data
				 jq$('.termbrowserframe').replaceWith(this.response);
				 
				 // re-init js features
				 jq$(".termline").each(activateDraggables);
				 initAllIconHovers();
				 initAllBrowserActionEvents();
			 }
		 },
	}
	
	 new _KA(options).send();
}

function initAllIconHovers() {
	jq$(".termline").each(function() {
    	initIconHover(jq$(this));
    });
}

function initIconHover(element) {
	
	element.find('.hoverAction').each(
			function() {
				var icon = jq$(this);
				element.hover(
						function () {
							icon.show();
						}, 
						function () {
							icon.hide();
						}
				);
			}
	);
}


function activateDraggables() {
	jq$(this).draggable( 
			{
    		distance: 20,
    		opacity: 0.55,
    		revert: true,
			}
	);
	
}

function sendAddedTerm(term, oldTargetID) {
		
		var params = {
				action : 'InsertListEntryAction',
				termname   : term,
				targetID   : oldTargetID,
	    }; 
		var options = {
			url : KNOWWE.core.util.getURL(params),
			 response : {
				 fn : function(){
					 rerenderSection(oldTargetID, this.responseText);
				 }
			 },
		}
		
		 new _KA(options).send();
	}

function rerenderSection(oldTargetID, newTargetID) {
	
	// clean ID that for some reason might have some linebreaks appended
	var replacer = new RegExp("\r\n", "g");
	newTargetID = newTargetID.replace(replacer, "");
	
	var params = {
			action : 'ReRenderContentPartAction',
			KdomNodeId   : newTargetID,
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function() {
					jq$('#'+oldTargetID).replaceWith(this.response);
					initAllDeleteItem();
				},
		 },
	}
	
	 new _KA(options).send();
}

function updateTermBrowser(event, ui) {
	var term = ui.item.value;
	sendTermBrowserAction(term, 'searched');
}
	

