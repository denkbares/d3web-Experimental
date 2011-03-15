/**
 * Title: KnowWE-Comment Plugin
 * Contains javascript functions the KnowWE comment plugin needs to functions properly.
 * The functions are based upon some KnowWE helper functions and need the
 * KNOWWE-helper.js in order to work correct.
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
 * Namespace: KNOWWE.plugin.comment
 * The KNOWWE comment namespace.
 */
KNOWWE.plugin.comment = function(){
    return {
        /**
         * Function: init
         * Core init functions.
         */
        saveComment : function(){
            var text = document.getElementById('knowwe-plugin-comment').value;
            var topic = KNOWWE.helper.gup('page');
            
            var params = {
                    action : 'ForumBoxAction',
                    ForumBoxText : text,
                    ForumArticleTopic : topic
                }
            
            var options = {
                    url : KNOWWE.core.util.getURL( params ),
                    response : {
                        action : 'none',
                        ids : [],
                        fn : function() {
                            document.getElementById('knowwe-plugin-comment').value = "";                
                            window.location = location.pathname + '?page=' + topic;
                            setTimeout(window.location.reload, 20);
                        }
                    }
            }
            new _KA( options ).send(); 
        }
    }
}();