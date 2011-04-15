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
                            result = JSON.parse(this.responseText);
                            if(result.url) {
                            	window.location.assign( result.url );
                            }
                        }
                    }
            }
            new _KA( options ).send(); 
        },
        /**
         * Adds the actions to the reply button.
         */
        addReply : function() {
        	var replyButtons = _KS('.forum-reply');
        	if( replyButtons.length > 0 ) {
        		for(var i = 0; i < replyButtons.length; i++ ) {
        			_KE.add('click', replyButtons[i], KNOWWE.plugin.comment.appendReply);
        		}
        	}
        },
        /**
         * Adds the previous comment to the HTML textare.
         */
        appendReply : function( event ) {
        	
        	var source = _KE.target( event );
        	var id = JSON.parse(source.rel).id;
        	
        	var comString = _KS('#forum-comment-'+id);
        	//var value = comString.innerText || comString.textContent || '';
        	var value = comString.innerHTML;
        	value = value.replace(/<br>/g, "\n> ");
        	
        	if(_KS('#knowwe-plugin-comment')) {
        		_KS('#knowwe-plugin-comment').value = "> " + value + "\n\n";
        	}
        }
    }
}();


/* ############################################################### */
/* ------------- Onload Events  ---------------------------------- */
/* ############################################################### */
(function init(){
    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
            KNOWWE.plugin.comment.addReply();
        });
    };
}());