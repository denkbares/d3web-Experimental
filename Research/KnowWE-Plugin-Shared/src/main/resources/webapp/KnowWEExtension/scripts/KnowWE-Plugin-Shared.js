/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.shared global namespace object. If KNOWWE.shared is already defined, the
 * existing KNOWWE.shared object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE.shared == "undefined" || !KNOWWE.shared) {
        KNOWWE.shared = function(){
        	return {
                /**
                 * Function: init
                 * Shared init functions.
                 */
                init : function(){
                	// This method is in KnowWE.js
                    KNOWWE.core.util.form.addFormHints('knoffice-panel');
                }
            }
        }();
}

/**
 * Namespace: KNOWWE.shared.actions
 * The KNOWWE actions namespace object.
 * Contains all actions that can be triggered in KnowWE per javascript.
 */
KNOWWE.shared.actions = function(){
    return {
        /**
         * Function: init
         * Core KnowWE actions.
         */
        init : function(){       
            //init parseAll action
            _KS('.parseAllButton').each(function(element){
                    _KE.add('click', element, KNOWWE.shared.actions.parseAll); 
            });
            
           
        },
        
        /**
         * Function: parseAll
         * parses all pages
         */  
        parseAll : function(){
            var params = {
                action : 'ParseWebOfflineRenderer',
                KWiki_Topic : KNOWWE.helper.gup('page')
            }   
            
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : ['parseAllResult']
                }
            }
            new _KA( options ).send();
        }
            
    }
}();

/**
 * Namespace: KNOWWE.shared.typebrowser
 * The KNOWWE typebrowser namespace.
 */
KNOWWE.shared.typebrowser = function(){
    return {
        /**
         * Function: init
         * The typebrowser init function. Enables the typebrowser and type 
         * activator buttons.
         */
        init : function(){
            var bttn = _KS('#KnowWEObjectTypeBrowser input[type=button]')[0];
            if( bttn )_KE.add('click', bttn, KNOWWE.shared.typebrowser.searchTypes );
            
            bttn = _KS('#KnowWEObjectTypeActivator input[type=button]')[0];   
            if( bttn ) _KE.add('click', bttn, KNOWWE.shared.typebrowser.switchTypeActivation );
        },
        /**
         * Function: getAdditionalMatchTextTypeBrowser
         * 
         * Gets additional context around the typebrowser finding. Used to view the
         * context in which the type occurs.
         * 
         * Parameters:
         *     atmUrl - An special URL. Used o transport the position of the finding and how many context elements should be displayed.
         *     query - The query string  for the TypeBrowser action
         */
        getAdditionalMatchTextTypeBrowser : function( event ){
            var el = KNOWWE.helper.event.target( event );
            var rel = el.getAttribute('rel');
            if(!rel) return;             
            rel = eval("(" + rel + ")" );

            var id = rel.direction + rel.index;
            var atmUrl = rel.article + ":" + rel.section + ":" + rel.index + ":" + rel.words + ":"
                + rel.direction + ":" + rel.wordCount;
            
            var params = {
                TypeBrowserQuery : rel.queryLength, //queryLength
                action : 'KnowWEObjectTypeBrowserAction',
                ATMUrl :  atmUrl
            };
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : [ id ]
                }
            }
            var request = new _KA( options );
            request.send();
        },
        /**
         * Function: switchTypeActivation
         * Switches the status of the selected type. 
         * It either enables or disables one type.
         */
        switchTypeActivation : function() {
            if(!_KS('#KnowWEObjectTypeActivator')) return;           
            var params = {
                action : 'KnowWEObjectTypeActivationAction',
                KnowWeObjectType : (function () {
                    var ob = _KS('#KnowWEObjectTypeActivator select')[0];
                    if(ob.selectedIndex){
                        return ob[ob.selectedIndex].value;
                    }
                    return "";
                })()
            }
                
            var options = {
                url : KNOWWE.core.util.getURL ( params ),
                response : {
                    action : 'update',
                    fn : function() {
                            var ob = _KS('#KnowWEObjectTypeActivator select')[0];
                            ob = ob[ob.selectedIndex];
                            if ( ob.style.color == "red") {
                                ob.style.color = "green";
                            } else {
                                ob.style.color = "red";
                            }
                         }
                }
            }
            new _KA( options ).send();
        },
        /**
         * Function: searchTypes
         * Searches for selected type and returns the result that can be viewed
         * in the Wiki page.
         */
        searchTypes : function() {
            if(!document.typebrowser) return;
            var params = {
                action : 'KnowWEObjectTypeBrowserAction',
                TypeBrowserQuery : (function () {
                    var box = document.typebrowser.Auswahl;
                    
                    if(box.selectedIndex){
                        return box.options[box.selectedIndex].value;
                    }
                    return "";
                })()
            }
            var options = {
                url : KNOWWE.core.util.getURL ( params ),
                response : {
                    action : 'insert',
                    ids : ['TypeSearchResult'],
                    fn : function(){
                        _KS('.show-additional-text').each(function(){
                             _KE.add('click', this, KNOWWE.shared.typebrowser.getAdditionalMatchTextTypeBrowser);
                        });
                    }
                }
            }
            new _KA( options ).send();
        }        
    }
}();

/**
 * Namespace: KNOWWE.shared.template
 * The KNOWWE template namespace.
 */
KNOWWE.shared.template = function(){
    return {
        /**
         * Function: init
         * The template init function. Enables the
         * templatetaghandler and templatetaghandler
         * buttons.
         */
        init : function(){
            //init TemplateTagHandler
            if(_KS('#TemplateTagHandler')) {
                var els = _KS('#TemplateTagHandler input[type=button]');
                for (var i = 0; i < els.length; i++){
                    _KE.add('click', els[i], KNOWWE.shared.template.doTemplate); 
                }
            }
            //add generate template button action
            if(_KS('.generate-template').length != 0){
                _KS('.generate-template').each(function( element ){
                    _KE.add('click', element, KNOWWE.shared.template.doTemplate);
                });
            }          
        },
        
        /**
         * Function: doTemplate
         * Creates a new Wikipage out of a templateType.
         * 
         * Parameters:
         *     event - The event from the create knowledgebase button. 
         */
        doTemplate : function( event ) {
            var pageName = eval( "(" + _KE.target(event).getAttribute('rel') + ")").jar;

            var params = {
                action : 'TemplateGenerationAction',
                NewPageName : _KS('#' + pageName ).value,
                TemplateName : pageName
            }
            
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    ids : ['TemplateGeneratingInfo']
                }
            }
            new _KA( options ).send();
        }
    }
}();



/* ############################################################### */
/* ------------- Onload Events  ---------------------------------- */
/* ############################################################### */
(function init(){
    
    window.addEvent( 'domready', _KL.setup );

    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
        	KNOWWE.shared.init();
        	KNOWWE.shared.actions.init();
            KNOWWE.shared.typebrowser.init();
            KNOWWE.shared.template.init();
        });
    };
}());