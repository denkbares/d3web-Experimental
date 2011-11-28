CodeMirror.defineMode("diff", function() {

	var keywords = ("IF AND OR NOT THEN KNOWN UNKNOWN").split(" ");
	var brackets = ("{ } ( ) | [ ]").split(" ");

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
			stream.next();
			
		// brackets and equals
		if (!stream.eol()) {
			token = stream.next();
			if (arrayContains(brackets, token) || token == "=")
				return token;
		}
		
		// compute the token
		while(!stream.eol() && stream.peek() != " " && !arrayContains(brackets, stream.peek()))
			token += stream.next();
		
		return token;
	}

  return {
		
    token: function(stream) {
      var token = tokenize(stream);
      
	  // equals
	  if (token == "=")
		return "equals";
	  
	  // brackets or delimiter
	  if (arrayContains(brackets, token))
		return "bracket";
	  
	  // line is a comment
	  if (token.indexOf("//") == 0) {
		stream.skipToEnd();
		return "comment";
	  }
	  
      if (arrayContains(keywords, token))
		return "keyword";
		
	  return "term";
    }	
  };
});

CodeMirror.defineMIME("text/x-diff", "diff");
