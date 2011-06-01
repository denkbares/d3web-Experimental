/**
 * Contains all javascript functions concerning the
 * KnowWE plugin CaseTrain.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
 /**
     * The KNOWWE global namespace object. If KNOWWE is already defined, the
     * existing KNOWWE object will not be overwritten so that defined namespaces
     * are preserved.
     */
 var KNOWWE = {};
}

/**
 * Namespace: KNOWWE.plugin.casetrain The KNOWWE plugin d3web namespace.
 * Initialized empty to ensure existence.
 */
KNOWWE.plugin.casetrain = function(){
 return {
    init : function(){
    	var ele = _KS('#xmlctbutton');
        _KE.add('click', _KS('#xmlctbutton'), KNOWWE.plugin.casetrain.parseXML);
    },
     
    /**
     * Function: parseXML
     * 
     */
     parseXML : function(){
        var params = {
                action : 'ParseXMLAction'
        }
        var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : ['casetrainparseresult']
                }
        }
       new _KA( options ).send();
     }
 }
	
}();

(function init(){ 
     window.addEvent( 'domready', function(){
            KNOWWE.plugin.casetrain.init();
     });
}());