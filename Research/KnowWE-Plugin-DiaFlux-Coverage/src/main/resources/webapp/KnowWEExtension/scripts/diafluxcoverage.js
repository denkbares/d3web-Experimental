DiaFlux.Coverage = {};

DiaFlux.Coverage.refresh = function(el) {
	
	var selected = el.options[el.selectedIndex];
	
	//remove last Flowchart
	$('coverageContent').innerHTML ="<div id='" + selected.text + "'></div>";
	
	Flowchart.loadFlowchart(selected.value,$('coverageContent').firstChild);
	
	
}


DiaFlux.Coverage.highlight = function(){
	
	if ($('coveragemaster')) {
		DiaFlux.Highlight.getHighlights.call(this, 'GetCoverageHighlightAction', {master: $('coveragemaster').value, coveragesection: $('coveragesection').value});
	} else {
		DiaFlux.Highlight.getHighlights.call(this, 'GetCoverageHighlightAction', {});
	} 
	
}

DiaFlux.Coverage.calculateCoverage = function(kdomid){
	var params = {
		action : 'CalculateCoverageAction',
        kdomid: kdomid
	};
	
	var options = {
		url: KNOWWE.core.util.getURL( params ),
        response : {
            action: 'none'
        }
    };
    new _KA(options).send();
		
			
}

KNOWWE.helper.observer.subscribe("flowchartrendered", DiaFlux.Coverage.highlight);