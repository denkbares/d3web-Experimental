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
         * Checks the URL for a given tab combination. If one is found,
         * the active tab is set to the found tab. Used to select other tabs
         * than the first one after a page load in the Wiki.
         */
        checkURLForTab : function() {
            var prefix = 'menu-tab-';
            var prefix_content = 'tab-';
            
            var tab = KNOWWE.helper.gup('tab');
            var first = KNOWWE.helper.gup('first');
            if( tab ) {
                tab = tab.replace(/\s+/, '');
                first = first.replace(/\s+/, '');
                
                var dom_element, dom_content_element;
                dom_element = $(prefix + first);
                dom_content_element = $(prefix_content + first);
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
                    action : 'none',
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
            KNOWWE.helper.observer.subscribe( 'onload', KNOWWE.plugin.defi.enableLogTabClicks);
        });
    }
}());