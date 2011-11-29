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
				 _KE.add('click', importbuttons[i], KNOWWE.plugin.usersupport.importAction);
			}

        },
		
		/**
		 * Function: exportAction
		 * adds the ExportTableAction for Tables to the Button.
		 * Renders a download button as response. The xls is
		 * stored on the server.
		 */
		exportAction : function(event) {
			var rel = eval("(" + _KE.target( event ).getAttribute('rel') + ")");
			var params = {
				action : 'TableExportAction',
				tableId : rel.objectId,
				// objectname : objectName.innerHTML
			}

			var id = "export-download"+rel.objectId;

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					action : 'insert',
                    ids : [ id ],
//                    fn : function(){
//			        	try {
//			        	}
//                    },
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
			}
			
			var options = {
				url : KNOWWE.core.util.getURL(params),
				tableId : rel.objectId,
//				response : {
//					action : 'insert',
//                    ids : [ id ],
//				}
			}
			new _KA(options).send();

		},
		
		/**
		 * Performs correction for a given Section ID and correction
		 */
		doCorrection : function(sectionID, correction) {
			var params = {
					action : 'KDOMReplaceTermUserSupportAction',
					TargetNamespace :  sectionID,
					KWiki_Topic : KNOWWE.helper.gup('page'),
					KWikitext : encodeURIComponent(correction.replace(/\s*$/im,""))
			};

			var options = {
					url : KNOWWE.core.util.getURL(params),
					loader : true,
					response : {
						action : 'none',
						fn : function() { 
							window.location.reload();
						},
						onError : function(http) {
							KNOWWE.helper.message.showMessage(http.responseText, "AJAX call failed");
						}
					}
			};
			new _KA(options).send();  
		},
		
		// the global array for the suggestions 
//	    var start = token.string;
//	  	
//	  	var keywords = ("IF WENN THEN DANN AND UND & OR ODER | NOT NICHT ! KNOWN UNKNOWN AUSSER EXCEPT").split(" ");
//	  	var brackets = ("{ } ( ) | [ ] ,").split(" ");
//	  	var d3webScorings = ("P7 P6 P5x P5 P4 P3 P2 P1 N1 N2 N3 N4 N5 N5x N6 N7 ESTABLISHED ETABLIERT SUGGESTED VERDAECHTIGT ++ + x YES JA NO NEIN -").split(" ");
//	  	var information = ("@ %").split(" ");
		
		// do function: f for each element in array: arr
  		forEach : function(arr, f) {
    		for (var i = 0, e = arr.length; i < e; ++i)
    			f(arr[i]);
  		},
  
  		// checks if array: arr contains an item: item
  		arrayContains: function(arr, item) {
    		if (!Array.prototype.indexOf) {
      			var i = arr.length;
      			while (i--) {
        			if (arr[i] === item) {
          				return true;
        			}
      			}
      		return false;
    		}
    		return arr.indexOf(item) != -1;
  		},
		
		// tests if String str matches the start and if it is not
    	// in found already adds it
    	maybeAdd: function (str) {
      		if (str.indexOf(start) == 0 && !arrayContains(found, str))
      		found.push(str);
    	},
    	
    	// gathers the completions from the given static arrays
    	// keywords, brackets, d3webScorings, information...
       	gatherStaticCompletions: function() {
      		KNOWWE.plugin.javascriptHint.forEach(keywords, maybeAdd);
      		KNOWWE.plugin.javascriptHint.forEach(brackets, maybeAdd);
     		KNOWWE.plugin.javascriptHint.forEach(d3webScorings, maybeAdd);
     		KNOWWE.plugin.javascriptHint.forEach(information, maybeAdd);
    	},
    	
    	// Calls the DialogComponent over KnowWE-Ajax-Invocation
    	// TODO show loading-gif
   		gatherDialogComponentCompletions: function (matchMe) {
			var params = {
				action : 'GetSuggestionsAction',
				toMatch: matchMe
			}

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					action : 'none',
                    fn : function() {
			        	var suggestions = JSON.parse(this.responseText);
			        	KNOWWE.plugin.javascriptHint.foreach(suggestions, maybeAdd);
                    },
				}
			}
			new _KA(options).send();
		},
		
		getCompletions: function(token, context) {

			var found = [];
    		if (context) {
      			// If this is a property, see if it belongs to some object we can
      			// find in the current environment.
      			var obj = context.pop(), base;
      			if (obj.className == "variable")
       				base = window[obj.string];
      			else if (obj.className == "string")
        			base = "";
      			else if (obj.className == "atom")
        			base = 1;
      			while (base != null && context.length)
        			base = base[context.pop().string];
      			if (base != null) gatherCompletions(base);
    		}
    		else {
      			KNOWWE.plugin.javascriptHint.gatherStaticCompletions();
//      			KNOWWE.plugin.javascriptHint.gatherDialogComponentCompletions();
    		}
    		return found;
 		}
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