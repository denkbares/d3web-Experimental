if (typeof DiaFlux == "undefined" || !DiaFlux) {
    var DiaFlux = {};
}

if (typeof DiaFlux.Coverage == "undefined" || !DiaFlux.Coverage) {
    DiaFlux.Coverage = {};
}

DiaFlux.Coverage.highlight = function(){
	
	DiaFlux.Highlight.getHighlights.call(this, 'PathCoverageHighlightAction', {coveragesection: $('coveragesection').value, nodeid: $('nodeid').value});
	
}

KNOWWE.helper.observer.subscribe("flowchartrendered", DiaFlux.Coverage.highlight);