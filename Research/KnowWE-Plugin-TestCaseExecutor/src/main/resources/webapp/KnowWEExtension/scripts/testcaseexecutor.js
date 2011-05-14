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

TestCaseExecutor.SEPARATOR = '#####';

TestCaseExecutor.runTestcase = function() {
	var cases = $('selectCases');
	var selectedCases = '';
	
	for (var i = 0; i < cases.childNodes.length; i++) {
		if (cases.childNodes[i].firstChild.checked) {
			if (selectedCases !== '') {
				selectedCases += TestCaseExecutor.SEPARATOR;
			} 
			selectedCases += cases.childNodes[i].lastChild.nodeValue;
		}
	}

	var filename = $('filename').innerHTML;
	var master = $('master').innerHTML;
	params = {
			action : 'TestCaseExecutorRunTestcaseAction',
	        KWikiWeb : 'default_web',
	        testcases : selectedCases,
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

TestCaseExecutor.backToCaseSelection = function() {
	var filename = $('filename').innerHTML;
	var master = $('master').innerHTML;
	
	TestCaseExecutor.getTestcases(filename, master);
}

TestCaseExecutor.selectAllCases = function() {
	var cases = $('selectCases');
	
	for (var i = 0; i < cases.childNodes.length; i++) {
		var input = cases.childNodes[i].getElement('input');
		input.checked = "checked";
	}
}

TestCaseExecutor.deSelectAllCases = function() {
	var cases = $('selectCases');
	
	for (var i = 0; i < cases.childNodes.length; i++) {
		var input = cases.childNodes[i].getElement('input');
		input.checked = false;
	}
}