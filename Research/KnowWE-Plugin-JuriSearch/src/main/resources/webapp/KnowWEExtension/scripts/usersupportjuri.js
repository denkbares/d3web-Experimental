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
 * Namespace: KNOWWE.plugin.usersupportJuri The KNOWWE usersupport namespace.
 */
KNOWWE.plugin.usersupportjuri = function() {
	
  	/**
  	 * The static keywords used in JuriSearch
  	 * TODO: Don't know if this are all.
  	 */
	var keywordsTree = ("start (oder) (und) FRAGE  Erläuterung: [jn]").split(" ");
	var keywordsTerminology = ("FRAGE  Erläuterung: [jn]").split(" ");
	
	return {
		
		/**
  		 * 
  		 * Function:    maybeAdd
  		 * 
  		 * @param token : token object; no string!
		 * @param found : already found suggestions
    	 * Description: gathers the completions from the given static arrays
    	 * 				keywords, brackets, d3webScorings, information... 
    	 */
       	gatherStaticCompletions : function(token, found) {
      		found = KNOWWE.plugin.usersupport.forEach(keywordsTree, token, found, KNOWWE.plugin.usersupport.maybeAdd);
      		found = KNOWWE.plugin.usersupport.forEach(keywordsTerminology, token, found, KNOWWE.plugin.usersupport.maybeAdd);
     		return found;
    	},
    	
	    /**
    	 * 
    	 * Function: gatherDialogComponentCompletions
    	 * 
    	 * @param matchMe : token object; no string!
    	 * @param found : already found suggestions
    	 * @param id : section ID to show loader gif for
    	 * Description: Calls the DialogComponent over KnowWE-Ajax-Invocation
    	 */
   		gatherDialogComponentCompletions : function(editor, getHints, id) {
   			
   			// Get token/from/to as object; function called usersupportHint()
   			var tokenInfo = getHints(editor, id);
   			
			var params = {
				action : 'GetSuggestionsActionJuri',
				toMatch: tokenInfo.tok.string,
				sectionID : id
			}

			var options = {
				url : KNOWWE.core.util.getURL(params),
				response : {
					action : 'none',
					async : false,
                    fn : function() {
						var found = [];
						found = KNOWWE.plugin.usersupportjuri.gatherStaticCompletions(tokenInfo.tok, found);
						var suggestions = JSON.parse(this.responseText);
						found = found.concat(suggestions);
						var toSimpleHint = {list : found,
    										from: tokenInfo.from,
            								to: tokenInfo.to
            								};
                    	KNOWWE.plugin.usersupport.simpleHint(editor, toSimpleHint, id);
                    },
				}
			}
			new _KA(options).send();

		}
	}
}();