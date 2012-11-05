var Baseline = {};

Baseline.create = function(){
	var filename = jq$('<input>', {type: 'text'});
	var div = jq$('<div>').append(jq$('<span>').text('Name: '));
	div.append(filename);
	
	var action = function(){
	
		var params = {
			action : 'CreateBaselineAction',
			baselineName: filename.val()
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
	
	
	KNOWWE.helper.message.showOKCancelDialog(div, 'Create new baseline', action);
		
}
	
Baseline.loadDiff = function(selectorPanel){
	var selects = selectorPanel.find('select');
	var base1 = jq$(selects[0]).val();
	var base2 = jq$(selects[1]).val();
	
	var params = {
		action : 'CompareBaselinesAction',
		SectionID : selectorPanel.find('input').val(),
		baseline1: base1,
		baseline2: base2

	};
	
	var options = {
		url: KNOWWE.core.util.getURL( params ),
		fn: function(){
			Baseline.showDiff(selectorPanel.closest('.baselineParent'), this.responseText);
		}
		
    };
	
	KNOWWE.core.util.updateProcessingState(1);
	try{
		new _KA(options).send();
		
	} catch(e) {}
	KNOWWE.core.util.updateProcessingState(-1);
	
}

Baseline.showDiff = function(parent, html){
	parent.find('.baselineCompareResult').empty().append(html);
	var tables = parent.find('table');
	Baseline.prepareTables(tables);
	
}

Baseline.prepareTables = function(tables){
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
	jq$(".baselineParent table").each(function(index, elem){
		Baseline.prepareTables(jq$(elem));
	});
	
	
});