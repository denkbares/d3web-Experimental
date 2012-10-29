Baseline = {};

Baseline.create = function(){
	
	var params = {
			action : 'CreateBaselineAction',
			baselineName: "test"
		};
		
		var options = {
			url: KNOWWE.core.util.getURL( params )
			
	    };
		
		KNOWWE.core.util.updateProcessingState(1);
		try{
			new _KA(options).send();
			
		} catch(e) {}
		KNOWWE.core.util.updateProcessingState(-1);
	
	
}
Baseline.loadDiff = function(selectorPanel){
	var selects = selectorPanel.find('select');
	var base1 = jq$(selects[0]).val();
	var base2 = jq$(selects[1]).val();
	
	var params = {
		action : 'CompareBaselinesAction',
		baseline1: base1,
		baseline2: base2

	};
	
	var options = {
		url: KNOWWE.core.util.getURL( params ),
		fn: function(){
			Baseline.showTable(selectorPanel.closest('.baselineParent'), this.responseText);
		}
		
    };
	
	KNOWWE.core.util.updateProcessingState(1);
	try{
		new _KA(options).send();
		
	} catch(e) {}
	KNOWWE.core.util.updateProcessingState(-1);
	
}

Baseline.showTable = function(parent, html){
	parent.find('.baselineCompareResult').empty().append(html);
	var tables = parent.find('table');
	tables.find('.detailsRow').hide();
	tables.find('.detailsArrow').on('click', function(){
		jq$(this).closest('tr').next('tr').toggle();
		jq$(this).toggleClass('up');
	});
	
	
}

jq$(document).ready(function(){
	jq$(".baselineSelection select").on('change', function(event){
		Baseline.loadDiff(jq$(this).closest('.baselineSelection'));
	});
	
	
});