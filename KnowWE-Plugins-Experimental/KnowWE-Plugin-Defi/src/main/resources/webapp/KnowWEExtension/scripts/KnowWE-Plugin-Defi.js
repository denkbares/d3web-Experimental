/**
 * Title: KnowWE-Plugin-Defi contains all javascript functions concerning the
 * KnowWE Defi plugin.
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
 * Namespace: KNOWWE.plugin.defi The KNOWWE plugin defi namespace.
 */
KNOWWE.plugin.defi = function() {
    return {
    	/**
    	 * Adds blanks to the tab names. necessary due the fact that they are not
    	 * allowed in the JSPWiki tab markup.
    	 */
    	beatifyTabNames : function(){
    	    var tabs = $('pagecontent').getElements('.tabmenu');
    	    
            for(var i = 0; i < tabs.length; i++) {
            	var a = tabs[i].getElements('a');
            	for(var j = 0; j < a.length; j++) {
            		text = a[j].getText();
            		text = text.replace(/--/g, " ");
            	    a[j].setText(text);
            	}
            }
    	},    	
        /**
         * Checks the URL for a given tab combination. If one is found,
         * the active tab is set to the found tab. Used to select other tabs
         * than the first one after a page load in the Wiki.
         */
        checkURLForTab : function() {
            var prefix = 'menu-tab-';
            var prefix_content = 'tab-';
            
            var tab = KNOWWE.helper.gup('tab');
            if( tab ) {
                tab = tab.replace(/\s+/, '');
                
                var dom_element, dom_content_element;
                
                dom_element = $(prefix + tab).getParent().getFirst();
                dom_content_element = $(prefix_content + dom_element.id.replace(/menu-tab-/, ''));
                
                if( dom_element ) {
                   dom_element.removeClass('activetab');
                   dom_content_element.addClass('hidetab');
                }
                dom_element = $(prefix + tab);
                dom_content_element = $(prefix_content + tab);
                if( dom_element ) {
                    dom_element.addClass("activetab");
                    dom_content_element.removeClass('hidetab');
                }               
            }
        },
        /**
         * Checks the URL for a talk about subject and adds it to the 
         * comment area when on the talk about page.
         */
        checkURLForSubject : function() {
            var prefix = 'menu-tab-';
            var prefix_content = 'tab-';
            
            var subject = KNOWWE.helper.gup('talkabout');
            if( subject ) {
                var textarea = _KS('#knowwe-plugin-comment')
                if(textarea) {
                    textarea.value = '[Betreff: ' + subject + ' ]';
                }             
            }
        },        
        /**
         * Changes display of usernametag-parentDiv to "inline"
         */
        changeUsernameTagDisplay : function() {
    		for (var i = 0; i < document.getElementsByName("usernametag").length; i++) {
				document.getElementsByName("usernametag")[i].parentNode.style.display = "inline";
    		}
        },    
        /**
         * Enables the logging on the page tabs. Only the tabs within the
         * Wiki page are affected.
         */
        enableLogTabClicks : function() {
            var tabs = $('pagecontent').getElements('.tabmenu');
            for(var i = 0; i < tabs.length; i++) {
                _KE.add('click', tabs[i], KNOWWE.plugin.defi.logTabClicks);
            }
        },
        /**
         * An Ajax call that logs the users tab clicks.
         * @param event The event object
         */
        logTabClicks : function( event ) {
            var clicked_tab = _KE.target( event );
            _KE.cancel( event );

            var params = {
                action : 'LogTabClicksAction',
                tologtab : clicked_tab.text
            }           
            var url = KNOWWE.core.util.getURL( params );
            var options = {
                url : url,
                response : {
                    action : 'none'
                }
            }
            new _KA( options ).send();          
        }
    }
}();


(function init() {
    if (KNOWWE.helper.loadCheck( [ 'Wiki.jsp' ])) {
        window.addEvent('domready', function() {
            KNOWWE.helper.observer.subscribe( 'onload', KNOWWE.plugin.defi.checkURLForTab);
            KNOWWE.helper.observer.subscribe( 'onload', KNOWWE.plugin.defi.checkURLForSubject);
            KNOWWE.helper.observer.subscribe( 'onload', KNOWWE.plugin.defi.enableLogTabClicks);
            KNOWWE.helper.observer.subscribe( 'onload', KNOWWE.plugin.defi.changeUsernameTagDisplay);
        });
        window.addEvent('load', function(){
        	KNOWWE.plugin.defi.beatifyTabNames();
        });
    }
}());