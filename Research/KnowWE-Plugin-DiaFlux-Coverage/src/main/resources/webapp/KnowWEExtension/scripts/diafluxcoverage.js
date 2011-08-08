DiaFlux.Coverage = {};

DiaFlux.Coverage.refresh = function(el) {
	
	var selected = el.options[el.selectedIndex];
	
	//remove last Flowchart
	$('coverageContent').innerHTML ="<div id='" + selected.text + "'></div>";
	
	Flowchart.loadFlowchart(selected.value,$('coverageContent').firstChild);
	
	
}


DiaFlux.Coverage.highlight = function(){
	
//	DiaFlux.Highlight.getHighlights('GetCoverageHighlightAction');
	DiaFlux.Highlight.getHighlights.call(this, 'GetCoverageHighlightAction', $('coveragemaster').value);
	
}

KNOWWE.helper.observer.subscribe("flowchartrendered", DiaFlux.Coverage.highlight);