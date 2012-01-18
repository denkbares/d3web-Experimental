/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

//var _KE = KNOWWE.helper.event;    /* Alias KNOWWE event. */
//var _KA = KNOWWE.helper.ajax;     /* Alias KNOWWE ajax. */
//var _KS = KNOWWE.helper.selector; /* Alias KNOWWE ElementSelector */
//var _KL = KNOWWE.helper.logger;   /* Alias KNOWWE logger */
//var _KN = KNOWWE.helper.element   /* Alias KNOWWE.helper.element */
//var _KH = KNOWWE.helper.hash      /* Alias KNOWWE.helper.hash */


/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.plugin global namespace object. If KNOWWE.plugin is already defined, the
 * existing KNOWWE.plugin object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
	    KNOWWE.plugin = function(){
	         return {  }
	    }
}

/**
 * The KNOWWE.plugin.debug global namespace object. If KNOWWE.plugin.quicki is already defined, the
 * existing KNOWWE.plugin.debug object will not be overwritten so that defined namespaces
 * are preserved.
 */
KNOWWE.plugin.debug = function(){
    return {
    }
}();
/**
 * Namespace: KNOWWE.plugin.debug
 * The debugger namespace.
 */
KNOWWE.plugin.debug = function(){
	
	return {
		/**
		 * 
		 */
		rerender : function (){
            var params = {
                    action : 'DebugAction'
            }           
            
            var id;
           	$$('div').each(function(element){
        		if (element.id.match(/debugger_/)) {
        			id = element.id;
        		}
            });
            var options = {
                url : KNOWWE.core.util.getURL( params ),
                response : {
                    action : 'insert',
                    ids : [ id ],
                    fn : function(){
			        	try {
                    		KNOWWE.core.rerendercontent.update(); //Clear new SolutionPanel
			        	}
			        	catch (e) { /*ignore*/ }
			        	KNOWWE.core.util.updateProcessingState(-1);
                    },
                    onError : function () {
			        	KNOWWE.core.util.updateProcessingState(-1);                    	
                    }
                }
            }
            KNOWWE.core.util.updateProcessingState(1);
            new _KA( options ).send(); 
		},
		/**
		 * 
		 */
		indicate : function (element) {
			var div = element.parentNode.childNodes[2];
			if (div.style.display == 'none') {
				div.style.display = 'block';
			} else if (div.style.display == 'block') {
				div.style.display = 'none';
			}
		}
    }
}();
/**
 * Initializes the required JS functionality when DOM is readily loaded
 */
(function init(){ 
    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
        	KNOWWE.helper.observer.subscribe( 'update', KNOWWE.plugin.debug.rerender);
        });
    }
}());