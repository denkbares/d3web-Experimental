CodeMirror.defineMode("diff", function() {

	var keywords = ("IF WENN THEN DANN AND UND & OR ODER | NOT NICHT ! KNOWN UNKNOWN AUSSER EXCEPT").split(" ");
	var brackets = ("{ } ( ) | [ ] ,").split(" ");
	var d3webScorings = ("P7 P6 P5x P5 P4 P3 P2 P1 N1 N2 N3 N4 N5 N5x N6 N7 ESTABLISHED ETABLIERT SUGGESTED VERDAECHTIGT ++ + x YES JA NO NEIN -").split(" ");
	var information = ("@ %").split(" ");

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
		
		// brackets / equals / package
		if (!stream.eol()) {
			token = stream.next();
			if (arrayContains(brackets, token) || token == "=")
				return "bracket";
		}
		
		// compute the token
		while(!stream.eol() && stream.peek() != " " && !arrayContains(brackets, stream.peek()))
			token += stream.next();
		
		return token;
	}

  return {
		
    token: function(stream) {
      var token = tokenize(stream);
      
      // whitespaces
      if (token == "whitespaces")
    	  return "whitespaces";
      
	  // equals
	  if (token == "=")
		return "equals";
	  
	  // brackets or delimiter
	  if (token == "bracket")
		return token;
	  
	  // line is a comment
	  if (token.indexOf("//") == 0) {
		stream.skipToEnd();
		return "comment";
	  }
	  
	  // keyword
      if (arrayContains(keywords, token))
		return "keyword";
		
	  // scoring
	  if (arrayContains(d3webScorings, token))
	  	return "scoring";
	
	  // package
	  if (arrayContains(information, token.charAt(0))) {
	  	stream.skipToEnd();
		return "information";
	  }
		
	  return "term";
    }	
  };
});

CodeMirror.defineMIME("text/x-diff", "diff");
