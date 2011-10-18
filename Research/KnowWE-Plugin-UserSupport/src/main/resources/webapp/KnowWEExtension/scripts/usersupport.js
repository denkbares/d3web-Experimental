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

/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

var toSelect;
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
 * The KNOWWE.plugin.quicki global namespace object. If KNOWWE.plugin.quicki is already defined, the
 * existing KNOWWE.plugin.quicki object will not be overwritten so that defined namespaces
 * are preserved.
 */
KNOWWE.plugin.usersupport = function(){
    return {
    }
}();

/**
 * Namespace: KNOWWE.usersupport.plugin The KNOWWE usersupport namespace.
 */
KNOWWE.plugin.usersupport = function() {
	return {
		init : function(){       
            //init all import and export button
			exportbuttons = _KS('.table-export');
			importbuttons = _KS('.table-import');
			
			// add action to all buttons
			for (var i = 0; i < exportbuttons.length; i++) {
				 _KE.add('click', exportbuttons[i], KNOWWE.plugin.usersupport.exportAction);
			}
			for (var i = 0; i < importbuttons.length; i++) {
				 _KE.add('click', importbuttons, KNOWWE.plugin.usersupport.importAction);
			} 
        },
		
		/**
		 * Function: exportAction
		 * adds the ImportTableAction for Tables to the Button
		 */
		exportAction : function(event) {
			var rel = eval("(" + _KE.target( event ).getAttribute('rel') + ")");
			var params = {
				action : 'TableExportAction',
				tableId : rel.objectId,
				// objectname : objectName.innerHTML
			}

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					// Empty for now
				}
			}
			new _KA(options).send();

		},
		
		/**
		 * Function: importAction
		 * adds the ImportTableAction for Tables to the Button
		 */
		importAction : function(event) {
			var rel = eval("(" + _KE.target( event ).getAttribute('rel') + ")");
			var params = {
				action : 'TableImportAction',
				tableId : rel.objectId,
				// objectname : objectName.innerHTML
			}

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					// Empty for now
				}
			}
			new _KA(options).send();

		},
	}
}();

/* ############################################################### */
/* ------------- Onload Events  ---------------------------------- */
/* ############################################################### */
(function init(){ 
    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
        	KNOWWE.plugin.usersupport.init();
        });
    }
}());