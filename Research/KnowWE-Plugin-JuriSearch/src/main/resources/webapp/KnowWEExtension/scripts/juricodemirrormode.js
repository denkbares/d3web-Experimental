CodeMirror.defineMode("juricodemirrormode", function() {

	var keywordsTree = ("start (oder) (und) FRAGE  Erläuterung: [jn]").split(" ");
	var keywordsTerminology = ("FRAGE  Erläuterung: [jn]").split(" ");
	var information = ("@ %").split(" ");
	
	var isInListing = false;

	function arrayContains(arr, item) {
		if (!Array.prototype.indexOf) {
			i = arr.length;
			while (i--) {
				if (arr[i] === item) {
					return true;
				}
			}
			return false;
		}
		return arr.indexOf(item) != -1;
	}
	
	function tokenize(stream) {
		var token = "";
		
		// if stream starts with whitespaces
		while (stream.peek() == " ")
			token = stream.next();
		if (token.length != 0 && token.charAt(0) == ' ')
			return "whitespaces";
		
		// listing/dash
		if (!stream.eol()) {
			var test = stream.next();
			
			// another line in listing; no comment line
			if (isInListing) {
				if (test != "/")
					return "listing";
				else {
					token += test;
					test = stream.next();
					if (test != "/")
					    return "listing"
					else
						return "//";
				}
			}
			
			// dash
			if (test == "-")
				return "dash";
			
			//listing
			while (!isNaN(test)) {
				token += test;
				test = stream.next();
			}
			 
			if (test == ".") {
				isInListing = true;
				return "listing";
			}
			else
				token += test; 
		}
		
		
		// brackets / equals / package
//		if (!stream.eol()) {
//			token = stream.next();
//			if (token == "=")
//				return "bracket";
//		}
		
		// compute the token
		while(!stream.eol() && stream.peek() != " ")
			token += stream.next();
		
		return token;
	}

  return {
		
    token: function(stream) {
      var token = tokenize(stream);
      
      // whitespaces
      if (token == "whitespaces")
    	  return "whitespacesJuri";
      
	  // dash
	  if (token == "dash")
		return "dash";
	  
	  if (token == "listing") {
	    stream.skipToEnd();
	  	return "listingJuri";
	  }

	  // line is a comment
	  if (token.indexOf("//") == 0) {
		stream.skipToEnd();
		return "comment";
	  }
	  
	  // keyword
      if (arrayContains(keywordsTree, token) || arrayContains(keywordsTerminology, token))
		return "keywordJuri";
	
	  // package
	  if (arrayContains(information, token.charAt(0))) {
	  	stream.skipToEnd();
		return "information";
	  }
		
	  return "termJuri";
    }	
  };
});

CodeMirror.defineMIME("text/x-juricodemirrormode", "juricodemirrormode");