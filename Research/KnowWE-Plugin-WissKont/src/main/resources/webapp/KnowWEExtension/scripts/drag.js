jq$(document).ready(function() {
    jq$(".termline").each(function () {
    	jq$(this).draggable( 
    			{
        		distance: 20,
        		opacity: 0.55,
        		revert: true,
    			}
    	);
    });
        
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
  });

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
	

