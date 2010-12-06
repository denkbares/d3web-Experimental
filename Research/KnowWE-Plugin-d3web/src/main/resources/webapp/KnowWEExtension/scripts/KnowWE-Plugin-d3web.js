/**
 * Title: KnowWE-Plugin-d3web
 * Contains all javascript functions concerning the KnowWE-Plugin-d3web.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    /**
     * The KNOWWE global namespace object.  If KNOWWE is already defined, the
     * existing KNOWWE object will not be overwritten so that defined
     * namespaces are preserved.
     */
    var KNOWWE = {};
}
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
 /**
     * The KNOWWE.plugin global namespace object. If KNOWWE.plugin is already defined, the
     * existing KNOWWE.plugin object will not be overwritten so that defined namespaces
     * are preserved.
     */
    KNOWWE.plugin = function(){
         return {  }
    }
}
/**
 * Namespace: KNOWWE.plugin.d3web
 * The KNOWWE plugin d3web namespace.
 * Initialized empty to ensure existence.
 */
KNOWWE.plugin.d3web = function(){
    return {
    }
}();

/**
 * Namespace: KNOWWE.plugin.d3web.imagequestion
 * Used for functionality of ImageQuestion in the
 * ImageQuestionHandler and ImageQuestionAction.
 */
KNOWWE.plugin.d3web.imagequestion = function() {
	    return {
        	/**
        	 * Function: init
        	 * Initialiases the AnswerRegions and
        	 * Buttons for ImageQuestionHandler
         	 */ 
        	init : function() {
           		 if(_KS('.answerRegion').length != 0){
                		_KS('.answerRegion').each(function( element ){
                   	 	_KE.add('click', element, KNOWWE.plugin.d3web.imagequestion.answerClicked);
                	});
            	}
        	},
        	
        	/**
        	 * Function initAction
        	 * Same as init. Used after rerendering.
        	 */
        	initAction : function() {
           		 if(_KS('.answerRegion').length != 0){
                		_KS('.answerRegion').each(function( element ){
                   	 	_KE.add('click', element, KNOWWE.plugin.d3web.imagequestion.answerClicked);
                	});
            	}
        	},
        	
            /**
         	 * Function: answerClicked
         	 * Copied from the Dialog.
         	 * Stores the user selected answer in the session.
        	 * 
        	 * See Also:
         	 *     <updateImageQuestionPanel>
        	 * 
         	 * Parameters:
         	 *     e - The user click event on an answer.
         	 */
       		answerClicked : function( e ) {
           		 var el = _KE.target(e);
            	
            	 var rel = eval("(" + el.getAttribute('rel') + ")");
            	 _KE.cancel( e );
            	 if( !rel ) return;
           		 var answerID = rel.oid;
           		 
            	 KNOWWE.plugin.d3web.imagequestion.updateImageQuestionPanel(rel.web, rel.ns, rel.qid, 'undefined', {ValueID: answerID});
       		},
        	
        	/**
         	 * Function: updateImageQuestionPanel
         	 * Updates the ImageQuestion Panel.
        	 * 
        	 * See Also:
         	 *     <initAction>
        	 * 
         	 * Parameters:
         	 *     questionID - The ID of the Question answered.
         	 */
    		updateImageQuestionPanel : function(web, namespace, questionID, termName, params) {
     			
            	var pDefault = {
            		action : 'ImageQuestionAction',
               		QuestionID : questionID,
               		KWikiWeb : web,
                	namespace : namespace,
                	ObjectID : questionID,
                	TermName : termName
            	}
        		pDefault = KNOWWE.helper.enrich( params, pDefault );
        		
        		var panelTable = 'imagetable_'+questionID;
            	var options = {
                	url : KNOWWE.core.util.getURL( pDefault ),
                	response : {
                    	action: 'replace',
                    	ids : [panelTable],
                    	fn : function() {
                    		KNOWWE.plugin.d3web.imagequestion.initAction();
                    		KNOWWE.helper.observer.notify('update');
                    	}
                	}
            	}
            	new _KA( options ).send();
			}
	}
}();

/**
 * Namespace: KNOWWE.plugin.d3web.sessionvalues
 * The sessionvalues namespace.
 */
KNOWWE.plugin.d3web.sessionvalues = function(){
    return {
        /**
         * Function: init
         * Initializes the SessionValues functionality. 
         */
        init : function(){
            KNOWWE.helper.observer.subscribe( 'update', this.updateSessionValues );
        },
        /**
         * Function: updateSessionValues
         * Updates the values of the questions in the session values panel.
         */
        updateSessionValues : function(){

            // Check the existence of the panel
            if(!_KS('#sessionvalues-panel')) return;
            
            // Necessary attributes for the action
            var user = _KS('#sessionvalues-user').value;
            var topic = _KS('#sessionvalues-topic').value;

            var params = {
                    action : 'SessionValuesViewAction',
                    KWikiUser: user,
                    KWiki_Topic: topic,
                    KWikiWeb : 'default_web'
            }

            // Check existence of result div
            var resultID = 'sessionvalues-result';
            if (!_KS('#' + resultID)) return;
            
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : [ resultID ]
                }
            }
            
            new _KA( options ).send(); 
              
        }
    }   
}();

/**
 * Namespace: KNOWWE.plugin.d3web.qcvalues
 * The qcvalues namespace.
 */
KNOWWE.plugin.d3web.qcvalues = function(){
    return {
        /**
         * Function: init
         * Initializes the QuestionnaireValues functionality. 
         */
        init : function(){
            KNOWWE.helper.observer.subscribe( 'update', this.updateQuestionnaireValues );
        },
        /**
         * Function: updateQuestionnaireValues
         * Updates the values of the questions in the questionnaires values panel.
         */
        updateQuestionnaireValues : function(){
            
            // There can be more than one QuestionnaireValuesViewHandler
            // TODO: this is more or less a hack, because it limits the amount of taghandlers to 100
            for (i = 0; i < 100; i++) {
                
                // Create the unique id of each panel
                var panelID = '#qcvalues-panel' + i;
                
                // Check the existence of the panel
                if(!_KS(panelID)) continue;
                
                // Find the childNode containing the name of the Questionnaire
                for (j = 0; j < _KS(panelID).childNodes.length; j++) {
                    if(_KS(panelID).childNodes[j].className == 'qcname') {
                        
                        var questionnaireName = _KS(panelID).childNodes[j].value;
                        
                        var params = {
                                action : 'QuestionnaireValuesViewAction',
                                questionnaire: questionnaireName,
                                KWikiWeb : 'default_web'
                        }

                        // Create the unique id of each result p
                        var resultID = 'qcvalues-result' + i;
                        
                        if (!_KS('#' + resultID)) break;
                        
                        var options = {
                            url : KNOWWE.core.util.getURL( params ),
                            response : {
                                action : 'insert',
                                ids : [ resultID ]
                            }
                        }
                        
                        new _KA( options ).send(); 
                        
                        // If we have done this, we can leave THIS loop!
                        break;
                    
                    
                    }
                }
            }
        }
    }   
}();


/**
 * Namespace: KNOWWE.plugin.d3web.rerenderquestionsheet
 * Contains some functions to update the question sheet after the user selected
 * an answer in the dialog or in the question sheet itself.
 */
KNOWWE.plugin.d3web.rerenderquestionsheet = function() {
    return {
        /**
         * Function: init
         */
        init : function(){
            KNOWWE.helper.observer.subscribe( 'update', this.update );
        },      
        /**
         * Function: update
         * Updates the question sheet after the user selected an answer in the
         * pop-up window.
         */
        update : function( ) {
            var topic = KNOWWE.helper.gup('page');
            var params = {
                action : 'ReRenderQuestionSheetAction',
                KWikiWeb : 'default_web',
                KWiki_Topic : topic
            }
            var url = KNOWWE.core.util.getURL( params );
            KNOWWE.plugin.d3web.rerenderquestionsheet.execute(url, 'questionsheet');
        },
        /**
         * Function: execute
         * Executes the update question sheet AJAX request
         * 
         * Parameters:
         *     url - The URL for the AJAX request
         *     id - The id of the DOM Element that should be updated.
         */
        execute : function( url, id ) {
            if(!_KS('#questionsheet-panel')) return ;
            var options = {
                url : url,
                response : {
                    action : 'insert',
                    ids : [ id ],
                    fn : function(){
                    	try {
                    		KNOWWE.core.util.addCollabsiblePluginHeader('#questionsheet-panel');
                    		KNOWWE.plugin.semantic.init();
                    	} 
                    	catch (e) { /*ignore*/ }
                    	KNOWWE.core.util.updateProcessingState(-1);
                    }
                }
            }
            KNOWWE.core.util.updateProcessingState(1);
            new _KA( options ).send();
        }
    }
}();

(function init(){ 
    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
            
            var ns = KNOWWE.plugin.d3web;
            for(var i in ns){
                if(ns[i].init){
                    ns[i].init();
                }
            }
            
        });
    }
}());