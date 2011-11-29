(function () {
  
  CodeMirror.javascriptHint = function(editor) {
    // Find the token at the cursor
    var cur = editor.getCursor(), token = editor.getTokenAt(cur), tprop = token;
    // If it's not a 'word-style' token, ignore the token.
    if (!/^[\w$_]*$/.test(token.string)) {
      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                       className: token.string == "." ? "property" : null};
    }
    // If it is a property, find out what it is a property of.
    while (tprop.className == "property") {
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (tprop.string != ".") return;
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (!context) var context = [];
      context.push(tprop);
    }
    return {list: KNOWWE.plugin.usersupport.getCompletions(token, context),
            from: {line: cur.line, ch: token.start},
            to: {line: cur.line, ch: token.end}};
  }
})();

//  /**
//   * The real work starts here: Gathering of completions!
//   */
//  KNOWWE.plugin.javascriptHint = function() {
//  	
//	// the global array for the suggestions
//    var found = [];
//    var start = token.string;
//  	
//  	var keywords = ("IF WENN THEN DANN AND UND & OR ODER | NOT NICHT ! KNOWN UNKNOWN AUSSER EXCEPT").split(" ");
//  	var brackets = ("{ } ( ) | [ ] ,").split(" ");
//  	var d3webScorings = ("P7 P6 P5x P5 P4 P3 P2 P1 N1 N2 N3 N4 N5 N5x N6 N7 ESTABLISHED ETABLIERT SUGGESTED VERDAECHTIGT ++ + x YES JA NO NEIN -").split(" ");
//  	var information = ("@ %").split(" ");
//	
//	return {
//		
//		// do function: f for each element in array: arr
//  		forEach: function(arr, f) {
//    		for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
//  		},
//  
//  		// checks if array: arr contains an item: item
//  		arrayContains: function(arr, item) {
//    		if (!Array.prototype.indexOf) {
//      			var i = arr.length;
//      			while (i--) {
//        			if (arr[i] === item) {
//          				return true;
//        			}
//      			}
//      		return false;
//    		}
//    		return arr.indexOf(item) != -1;
//  		},
//		
//		// tests if String str matches the start and if it is not
//    	// in found already adds it
//    	maybeAdd: function (str) {
//      		if (str.indexOf(start) == 0 && !arrayContains(found, str))
//      		found.push(str);
//    	},
//    	
//    	// gathers the completions from the given static arrays
//    	// keywords, brackets, d3webScorings, information...
//       	gatherStaticCompletions: function() {
//      		KNOWWE.plugin.javascriptHint.forEach(keywords, maybeAdd);
//      		KNOWWE.plugin.javascriptHint.forEach(brackets, maybeAdd);
//     		KNOWWE.plugin.javascriptHint.forEach(d3webScorings, maybeAdd);
//     		KNOWWE.plugin.javascriptHint.forEach(information, maybeAdd);
//    	},
//    	
//    	// Calls the DialogComponent over KnowWE-Ajax-Invocation
//    	// TODO show loading-gif
//   		gatherDialogComponentCompletions: function (matchMe) {
//			var params = {
//				action : 'GetSuggestionsAction',
//				toMatch: matchMe
//			}
//
//			var options = {
//				url : KNOWWE.core.util.getURL(params),
//				response : {
//					action : 'none',
//                    fn : function() {
//			        	var suggestions = JSON.parse(this.responseText);
//			        	KNOWWE.plugin.javascriptHint.foreach(suggestions, maybeAdd);
//                    },
//				}
//			}
//			new _KA(options).send();
//		},
//		
//		getCompletions: function (token, context) {   
//
//    		if (context) {
//      			// If this is a property, see if it belongs to some object we can
//      			// find in the current environment.
//      			var obj = context.pop(), base;
//      			if (obj.className == "variable")
//       				base = window[obj.string];
//      			else if (obj.className == "string")
//        			base = "";
//      			else if (obj.className == "atom")
//        			base = 1;
//      			while (base != null && context.length)
//        			base = base[context.pop().string];
//      			if (base != null) gatherCompletions(base);
//    		}
//    		else {
//      			KNOWWE.plugin.javascriptHint.gatherStaticCompletions();
////      			KNOWWE.plugin.javascriptHint.gatherDialogComponentCompletions();
//    		}
//    		return found;
// 		}
//	}

//  }();
