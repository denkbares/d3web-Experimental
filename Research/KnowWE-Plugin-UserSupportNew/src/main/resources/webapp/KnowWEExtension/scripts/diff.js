CodeMirror.defineMode("diff", function() {
	
  return {
  	
    token: function(stream) {
      var token = tokenise(stream);
      if (token == "comment") return "plus";
      if (token == "-") return "minus";
      if (token == "@") return "rangeinfo";
    },
    
    tokenise: function(stream) {
    	var token;
    	
    	// end of line
    	if (stream.eol()) return "";
    	
    	var token = stream.next();
    	
    	// comment: advance to end of line
    	if (token == "/" && stream.next() == "/") {
    		stream.skipToEnd();
    		return "comment";
    	}

    	
    	return token;
    }
  };
});

CodeMirror.defineMIME("text/x-diff", "diff");
