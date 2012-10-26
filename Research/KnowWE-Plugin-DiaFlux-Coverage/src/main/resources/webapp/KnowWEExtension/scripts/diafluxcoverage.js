if (typeof DiaFlux == "undefined" || !DiaFlux) {
    var DiaFlux = {};
}

if (typeof DiaFlux.Coverage == "undefined" || !DiaFlux.Coverage) {
    DiaFlux.Coverage = {};
}

DiaFlux.Coverage.calculateCoverage = function(kdomid){
	var params = {
		action : 'CalculateCoverageAction',
        SectionID: kdomid
	};
	
	var options = {
		url: KNOWWE.core.util.getURL( params ),
        response : {
            fn: function(){window.location.reload();}
        }
    };
    new _KA(options).send();
			
}


DiaFlux.Coverage.refresh = function(el) {
	
	var selected = el.options[el.selectedIndex];
	
	//remove last Flowchart
	$('coverageContent').innerHTML ="<div id='" + selected.text + "'></div>";
	
	Flowchart.loadFlowchart(selected.value,$('coverageContent').firstChild);
	
	
}
