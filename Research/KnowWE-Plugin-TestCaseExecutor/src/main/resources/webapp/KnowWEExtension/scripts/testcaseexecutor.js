var TestCaseExecutor = {};

TestCaseExecutor.getTestcases = function(filename, master) {
		params = {
			action : 'TestCaseExecutorAction',
	        KWikiWeb : 'default_web',
	        filename : filename, 
	        master : master
		};
		
		// options for AJAX request
	    options = {
	        url : KNOWWE.core.util.getURL( params ),
	        response : {
	    		action : 'none',
            	fn : function(){
	    	TestCaseExecutor.addTestcases(this);
	    		}
	        }
	    };
	    
	    // send AJAX request
	    new _KA( options ).send();
}

TestCaseExecutor.addTestcases = function(request) {
	var text = request.responseText;
	
	var parent = $('testcases');
	
	while (parent.hasChildNodes) {
		if (parent.firstChild != null) {
			parent.removeChild(parent.firstChild);
		} else {
			break;
		}
	}
	
	parent.innerHTML = text;
	var a = 1;
}

TestCaseExecutor.runTestcase = function(element) {
	var testcase = element.innerHTML;
	var filename = $('filename').innerHTML;
	var master = $('master').innerHTML;
	params = {
			action : 'TestCaseExecutorRunTestcaseAction',
	        KWikiWeb : 'default_web',
	        testcase : testcase,
	        filename : filename,
	        master : master
		};
		
		// options for AJAX request
	    options = {
	        url : KNOWWE.core.util.getURL( params ),
	        response : {
	    		action : 'none',
            	fn : function(){
	    			var text = this.responseText;
	    			var parent = $('testcases');
	    			parent.innerHTML = text;
	    		}
	        }
	    };
	    
	    // send AJAX request
	    new _KA( options ).send();

}
