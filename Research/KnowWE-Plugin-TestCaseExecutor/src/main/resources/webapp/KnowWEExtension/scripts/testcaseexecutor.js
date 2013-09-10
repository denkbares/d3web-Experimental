var TestCaseExecutor = {};

TestCaseExecutor.getTestcases = function(filename, master) {
	if (filename === null) {
		var select = $('testcases').getElement('select');
		var filename = select.options[select.selectedIndex].value;
		if (!filename) return;
	}
	
	
	var params = {
		action : 'TestCaseExecutorAction',
        KWikiWeb : 'default_web',
        filename : filename, 
        master : master
	};
	
	// options for AJAX request
    var options = {
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
}

TestCaseExecutor.SEPARATOR = '#####';

TestCaseExecutor.runTestcase = function() {
	var cases = $('selectCases');
	var selectedCases = '';
	var trs = cases.getElements('tr');
	
	for (var i = 1; i < trs.length - 1; i++) {
		var checkbox = trs[i].getElement('input');
		if (checkbox.checked) {
			if (selectedCases !== '') {
				selectedCases += TestCaseExecutor.SEPARATOR;
			} 
			selectedCases += trs[i].lastChild.textContent;
		}
	}
	

	var filename = $('filename').innerHTML;
	var master = $('master').innerHTML;
	var params = {
		action : 'TestCaseExecutorRunTestcaseAction',
        KWikiWeb : 'default_web',
        testcases : selectedCases,
        filename : filename,
        master : master
	};
		
		// options for AJAX request
	 var options = {
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
	var select = false;
	
	var trs = cases.getElements('tr');
	if (trs[0].getElement('input').checked) {
		select = true;
	}
	
	for (var i = 1; i < trs.length - 1; i++) {
		var checkbox = trs[i].getElement('input');
		if (select) {
			checkbox.checked = 'checked';
		} else {
			checkbox.checked = false;
		}
	}
}

TestCaseExecutor.runTestcaseFromSection = function(kdomid){

	var params = {
		action : 'TestCaseExecutorRunTestcaseAction',
		kdomid : kdomid
	};

	// options for AJAX request
	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			action : 'none',
			fn : function() {
				var text = this.responseText;
				var parent = $('testcases');
				parent.innerHTML = text;
			}
		}
	};

	// send AJAX request
	new _KA(options).send();
	
	
}
