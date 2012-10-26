/**
 * Title: KnowWE-Plugin-Semantic Contains all javascript functions concerning
 * the KnowWE-Plugin-SemanticCore.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
	/**
	 * The KNOWWE global namespace object. If KNOWWE is already defined, the
	 * existing KNOWWE object will not be overwritten so that defined namespaces
	 * are preserved.
	 */
	var KNOWWE = {};
}
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
	/**
	 * The KNOWWE.plugin global namespace object. If KNOWWE.plugin is already
	 * defined, the existing KNOWWE.plugin object will not be overwritten so
	 * that defined namespaces are preserved.
	 */
	KNOWWE.plugin = function() {
		return {}
	}
}
/**
 * Namespace: KNOWWE.plugin.semantic The KNOWWE plugin d3web namespace.
 * Initialized empty to ensure existence.
 */
KNOWWE.plugin.semantic = function() {
	return {}
}();

/**
 * Namespace: KNOWWE.plugin.semantic.action The namespace of the semantic things
 * in KNOWWE.
 */
KNOWWE.plugin.semantic.actions = function() {

	return {
		refreshSparqlRenderer : function() {
        	var fromLine = jq$('#fromLine').val();
			var search = /^\d+$/;
			var found = search.test(fromLine);
			if(!(found)){
				jq$('#fromLine').val('');
				return;
			}
			document.cookie = "FromLine=" + fromLine;
			var showLines = jq$('#showLines').val();
			document.cookie = "ShowLines=" + showLines;
			KNOWWE.plugin.d3webbasic.actions.updateNode(jq$('.type_sparql').first().attr("id"), KNOWWE.helper.gup('page'), null);
		},
		
		forward : function(){
			var showLines = jq$('#showLines').val();
			document.cookie = "ShowLines=" + showLines;
			var fromLine = jq$('#fromLine').val();
			
			var newFromLine;
			if (showLines == "All"){
				newFromLine = 1;
			}
			else {
				newFromLine = parseInt(fromLine)+parseInt(showLines);
			}
			document.cookie = "FromLine=" + newFromLine;
			KNOWWE.plugin.d3webbasic.actions.updateNode(jq$('.type_sparql').first().attr("id"), KNOWWE.helper.gup('page'), null);
		},  
		
		back : function(){
			var showLines = jq$('#showLines').val();
			document.cookie = "ShowLines=" + showLines;
			var fromLine = jq$('#fromLine').val();
			var newFromLine;
			if (showLines == "All"){
				newFromLine = 1;
			}
			else {
				if(parseInt(fromLine)-parseInt(showLines) < 1){
					newFromLine = 1;
				}
				else {
			 		newFromLine = parseInt(fromLine)-parseInt(showLines);
				}
			}
			
			document.cookie = "FromLine=" + newFromLine;
			KNOWWE.plugin.d3webbasic.actions.updateNode(jq$('.type_sparql').first().attr("id"), KNOWWE.helper.gup('page'), null);
		},
		
		begin : function(){
			var showLines = jq$('#showLines').val();
			document.cookie = "ShowLines=" + showLines;
			var fromLine = 1;
			document.cookie = "FromLine=" + fromLine;
			KNOWWE.plugin.d3webbasic.actions.updateNode(jq$('.type_sparql').first().attr("id"), KNOWWE.helper.gup('page'), null);
		},  
	} 
}();

(function init() {
	if (KNOWWE.helper.loadCheck([ 'Wiki.jsp' ])) {
		window.addEvent('domready', function() {

			var ns = KNOWWE.plugin.semantic;
			for ( var i in ns) {
				if (ns[i].init) {
					ns[i].init();
				}
			}

		});
	}
}());