DiaFlux.Anomalies = {}

DiaFlux.Anomalies.highlight = function(){
	DiaFlux.Highlight.getHighlights.call(this, 'GetAnomaliesHighlightAction', {});
	
}


KNOWWE.helper.observer.subscribe("flowchartrendered", DiaFlux.Anomalies.highlight);
