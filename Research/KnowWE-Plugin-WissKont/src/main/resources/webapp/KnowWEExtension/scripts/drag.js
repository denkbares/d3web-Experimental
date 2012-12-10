jq$(document).ready(function() {
    jq$(".termline").each(activateDraggables);
        
    jq$(".relationMarkup").each(function() {
    	jq$(this).droppable({
    			drop: function(event, ui) { 
    				var termElement = ui.draggable;
    				var termnameDiv = termElement.find("div.termname");
    				var termname = termnameDiv.html();
    				
    				var markupElement = jq$(this).find("div.defaultMarkupFrame");
    				var markupID = markupElement.attr('id');
    				//alert('dropped: '+ termname + ' on id: '+markupID  );
    				sendAddedTerm(termname, markupID);
    				},
    			hoverClass: "drophover",
    	})
    });


    jq$(".termline").each(function() {
    	initIconHover(jq$(this));
    });

});

function initIconHover(element) {
	element.find('.ui-icon').each(
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
				action : 'InsertTermRelationAction',
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
	
	var params = {
			action : 'ReRenderContentPartAction',
			KdomNodeId   : newTargetID,
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function() {
					jq$('#'+oldTargetID).replaceWith(this.response);
				},
		 },
	}
	
	 new _KA(options).send();
}

function updateTermBrowser(event, ui) {
	var term = ui.item.value;
	var params = {
			action : 'RerenderTermBrowserAction',
			term   : term,
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function() {
					jq$('.termbrowserframe').replaceWith(this.response);
				    jq$(".termline").each(activateDraggables);
				},
		 },
	}
	
	 new _KA(options).send();
	
}
	

