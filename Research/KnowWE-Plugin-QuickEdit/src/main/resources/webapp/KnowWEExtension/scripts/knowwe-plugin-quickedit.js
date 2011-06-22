/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces are
 * preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.core global namespace object. If KNOWWE.core is already defined,
 * the existing KNOWWE.core object will not be overwritten so that defined
 * namespaces are preserved.
 */
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
    KNOWWE.plugin = {};
}


/**
 * Namespace: KNOWWE.core.plugin.quickedit
 * The KNOWWE quick edit namespace.
 */
KNOWWE.plugin.quickedit = function() {
    
    /**
     * Refeshes a certain DOM element.
     * @param String id The id of the DOM element
     * @param String title The name of the current page
     */
    function refreshDOM(id, title) {
        
        var params = {
            action : 'RenderAsQuickEditAction',
            KdomNodeId : id,
            KWiki_Topic : title
        }           
                            
        var options = {
            url : KNOWWE.core.util.getURL(params),
            response : {
                action : 'replace',
                ids : [id]
            }
        }
        new _KA(options).send();
    }
    
    return {
        enable : function(id, title) {
            var params = {
                action : 'QuickEditEnableAction',
                KdomNodeId : id,
                KWiki_Topic : title
            }           
            
            var options = {
                url : KNOWWE.core.util.getURL(params),
                response : {
                    action : 'none',
                    fn : function() {
                        var enabled = JSON.parse(this.responseText);
                        enabled = enabled.success;
                        if( enabled ) {
                            refreshDOM(id, title);
                        }
                    }
                }
            }
            new _KA(options).send();
        },
        disable : function(id, title) {
            var params = {
                action : 'QuickEditDisableAction',
                KdomNodeId : id,
                KWiki_Topic : title
            }           
            
            var options = {
                url : KNOWWE.core.util.getURL(params),
                response : {
                    action : 'none',
                    fn : function() {
                        var enabled = JSON.parse(this.responseText);
                        enabled = enabled.success;
                        
                        if( enabled ) {
                            //refreshDOM(id, title);
                            window.location.reload();
                        }
                    }
                }
            }
            new _KA(options).send();            
        },
        /**
         * Save the changes to the article. 
         * @param String id The id of the DOM element
         * @param String value The old text of the section
         */
        save : function(id, value){
            var title = KNOWWE.helper.gup('page');
            
            var textarea = document.getElementById(id).getElementsByTagName('textarea')[0];
                
            var params = {
                action : 'QuickEditHandlingAction',
                KdomNodeId : id,
                KWiki_Topic : title,
                changes : textarea.value
            }           

            var options = {
                url : KNOWWE.core.util.getURL(params),
                response : {
                    action : 'none',
                    fn : function() {
                        var enabled = JSON.parse(this.responseText);
                        enabled = enabled.success;

                        if( enabled ) {
                            KNOWWE.plugin.quickedit.disable(id, title);
                        }
                    }
                }
            }
            new _KA(options).send();  
        },
        /**
         * Cancel the quick edit action. Restore the original text.
         * @param String id The id of the DOM element
         */        
        cancel : function(id) {
            var title = KNOWWE.helper.gup('page');
            KNOWWE.plugin.quickedit.disable(id, title);
        }
    }
}();