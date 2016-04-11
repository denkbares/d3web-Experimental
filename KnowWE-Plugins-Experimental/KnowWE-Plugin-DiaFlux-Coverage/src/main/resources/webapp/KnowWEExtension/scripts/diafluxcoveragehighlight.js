if (typeof DiaFlux == "undefined" || !DiaFlux) {
    var DiaFlux = {};
}

if (typeof DiaFlux.Coverage == "undefined" || !DiaFlux.Coverage) {
    DiaFlux.Coverage = {};
}

DiaFlux.Coverage.highlight = function(){
	
	if ($('coveragesection')) {
		DiaFlux.Highlight.getHighlights.call(this, 'GetCoverageHighlightAction', {coveragesection: $('coveragesection').value});
	} else {
		DiaFlux.Highlight.getHighlights.call(this, 'GetCoverageHighlightAction');
	} 
	
}

KNOWWE.helper.observer.subscribe("flowchartrendered", DiaFlux.Coverage.highlight);