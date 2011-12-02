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
 * The KNOWWE.plugin.usersupport global namespace object. If KNOWWE.plugin.quicki is already defined, the
 * existing KNOWWE.plugin.usersupport object will not be overwritten so that defined namespaces
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
  	
  	/**
  	 * The static keywords used in KnowWE
  	 * TODO: Don't know if this are all.
  	 */
	var keywords = ("IF WENN THEN DANN AND UND & OR ODER | NOT NICHT ! KNOWN UNKNOWN AUSSER EXCEPT").split(" ");
	var brackets = ("{ } ( ) | [ ] ,").split(" ");
	var d3webScorings = ("P7 P6 P5x P5 P4 P3 P2 P1 N1 N2 N3 N4 N5 N5x N6 N7 ESTABLISHED ETABLIERT SUGGESTED VERDAECHTIGT ++ + x YES JA NO NEIN -").split(" ");
	var information = ("@ %").split(" ");
	
	var foundArray = [];
	
	return {
		
		/**
		 * Function:    init
		 * Description: inits import and export buttons
		 */
		init : function(){       

			exportbuttons = _KS('.table-export');
			for (var i = 0; i < exportbuttons.length; i++) {
				 _KE.add('click', exportbuttons[i], KNOWWE.plugin.usersupport.exportAction);
			}

        },
		
		/**
		 * Function:    exportAction
		 * @param event
		 * Description: adds the ExportTableAction for Tables to the Button.
		 * 				Renders a download button as response. The xls is
		 * 				stored on the server.
		 */
		exportAction : function(event) {
			var rel = eval("(" + _KE.target( event ).getAttribute('rel') + ")");
			var params = {
				action : 'TableExportAction',
				tableId : rel.objectId,
			}

			var id = "export-download"+rel.objectId;

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					action : 'insert',
                    ids : [ id ],
				}
			}
			new _KA(options).send();

		},
		
		/**
		 * Function:    importAction
		 * @param event
		 * Description: adds the ImportTableAction for Tables to the Button
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
			}
			new _KA(options).send();

		},
		
		/**
		 * Function:    docorrection
		 * @param sectionID
		 * @param correction
		 * Description: Performs correction for a given Section ID and correction
		 *              This is a copy from correction.js.
		 *              TODO could be generalized with it!
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
		
		/**
		 * Function:    forEach
		 * @param arr
		 * @param token : token object; no string!
		 * @param found : already found suggestions
		 * @param f     : function
		 * Description: do function: f for each element in array: arr
		 */
  		forEach : function(arr, token, found, f) {
    		for (var i = 0, e = arr.length; i < e; ++i)
    			found = f(token, found, arr[i]);
    			return found;
  		},
  
  		/**
  		 * 
  		 * Function:    arrayContains
  		 * @param arr: array
  		 * @param item: string
  		 * Description: checks if arr contains an item
  		 */ 
  		arrayContains : function(arr, item) {
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
		
		/**
  		 * 
  		 * Function:    maybeAdd
  		 * @param token : token object; no string!
		 * @param found : already found suggestions
  		 * @param item: string
  		 * Description: adds item to found if it starts with token
  		 */ 
    	maybeAdd : function (token, found, item) {
    		var tokenstring = token.string;
    		var index = item.indexOf(tokenstring);
    		var contains = KNOWWE.plugin.usersupport.arrayContains(found, item);
      		if (item.indexOf(tokenstring) == 0 && !contains)
      		found.push(item);
      		return found;
    	},
    	
    	
		/**
  		 * 
  		 * Function:    maybeAdd
  		 * @param token : token object; no string!
		 * @param found : already found suggestions
    	 * Description: gathers the completions from the given static arrays
    	 * 				keywords, brackets, d3webScorings, information... 
    	 */
       	gatherStaticCompletions : function(token, found) {
      		found = KNOWWE.plugin.usersupport.forEach(keywords, token, found, KNOWWE.plugin.usersupport.maybeAdd);
      		found = KNOWWE.plugin.usersupport.forEach(brackets, token, found, KNOWWE.plugin.usersupport.maybeAdd);
     		found = KNOWWE.plugin.usersupport.forEach(d3webScorings, token, found, KNOWWE.plugin.usersupport.maybeAdd);
     		found = KNOWWE.plugin.usersupport.forEach(information, token, found, KNOWWE.plugin.usersupport.maybeAdd);
     		return found;
    	},
    	
    	/**
    	 * 
    	 * Function: gatherDialogComponentCompletions
    	 * @param matchMe : token object; no string!
    	 * @param found : already found suggestions
    	 * @param id : section ID to show loader gif for
    	 * Description: Calls the DialogComponent over KnowWE-Ajax-Invocation
    	 */
   		gatherDialogComponentCompletions : function(matchMe, found, id) {
   			
   			KNOWWE.plugin.usersupport.showAjaxLoader(id);
   			
			var params = {
				action : 'GetSuggestionsAction',
				toMatch: matchMe.string
			}

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					action : 'none',
					async : false,
                    fn : function() {
                    	var found = [];
			        	var suggestions = JSON.parse(this.responseText);
			        	var found = KNOWWE.plugin.usersupport.forEach(suggestions, matchMe, found, KNOWWE.plugin.usersupport.maybeAdd);
			        	foundArray = found; 
                    },
				}
			}
			new _KA(options).send();
			
			KNOWWE.plugin.usersupport.hideAjaxLoader(id);
		},
		
		/**
		 * Function:    showAjaxLoader
		 * @param id : section ID
		 * Description:
		 */
		showAjaxLoader : function(id) {
			var ajaxLoaderGif = new Element("img", {
				'id' : 'loader_' + id,
				'src' : 'KnowWEExtension/images/ajax-100.gif',
				'class' : 'ajaxloader',
			});
			$(id).appendChild(ajaxLoaderGif);
		},
	
		/**
		 * Function:   hideAjaxLoader
		 * @param id : section ID
		 * Description:
		 */
		hideAjaxLoader : function (id) {
			var ajaxLoaderGif = $('loader_' + id);
			ajaxLoaderGif.parentNode.removeChild(ajaxLoaderGif);
		},
		
		/**
		 * 
		 * Function:    getCompletions
		 * @param token : token object; no string!
		 * @param context : TODO needed?
		 * @param id : section ID
		 * Description:  called from javascript-hint
		 */
		getCompletions : function(token, context, id) {
			var found = [];
			KNOWWE.plugin.usersupport.gatherDialogComponentCompletions(token, found, id);
      		found = KNOWWE.plugin.usersupport.gatherStaticCompletions(token, found);
			found = found.concat(foundArray);
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