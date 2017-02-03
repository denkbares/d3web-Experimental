jq$(document).ready(function() {
	
	initOverviewGraphCollapse();

	KNOWWE.plugin.termbrowserautocompletion.addListenerFunctionForDropEditUpdate(rerenderOverviewGraph);

});

function initOverviewGraphCollapse() {
	jq$(".showGraph").each(function() {
		jq$(this).bind('click', function() {
			jq$(".showGraph").hide();
			jq$(".hideGraph").show();
			jq$(".termgraphcontent").show('50');
			handleTermActionEvent(jq$(this));
		});
	});
	jq$(".hideGraph").each(function() {
		jq$(this).bind('click', function() {
			jq$(".hideGraph").hide();
			jq$(".showGraph").show();
			jq$(".termgraphcontent").hide('50');
			handleTermActionEvent(jq$(this));
		});
	});
	
	jq$(".toggleGraph").each(function() {
		jq$(this).bind('click', function() {
			jq$(".hideGraph").toggle();
			jq$(".showGraph").toggle();
			jq$(".termgraphcontent").toggle('50');
			handleTermActionEvent(jq$(this));
		});
	});	
}


function rerenderOverviewGraph() {
	var params = {
			action : 'RerenderConceptOverviewAction',
    }; 
	var options = {
		url : KNOWWE.core.util.getURL(params),
		 response : {
			 fn : function() {
				 	// insert re-rendered graph block
				 	var termGraphElement = jq$('.termgraph');
					termGraphElement.replaceWith(this.response);
					initOverviewGraphCollapse();
			},
		 },
	}
	
	new _KA(options).send();

}
