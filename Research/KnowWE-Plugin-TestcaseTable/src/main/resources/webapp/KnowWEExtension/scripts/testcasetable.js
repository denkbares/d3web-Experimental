var Testcase = {};
/**
 * adds a new row (as last row) to the table
 */
Testcase.addRow = function(element) {
	// save the table, so changed fields
	// stay after the request
	Testcase.saveTable();
	
	// delay the request, otherwise save would not work
	(function() {Testcase.sendRequest('row', element);}).delay(500);
}


/**
 * adds a new column (as last column) to the table
 */
Testcase.addCol = function(element) {
	// save the table, so changed fields
	// stay after the request
	Testcase.saveTable();
	
	// delay the request, otherwise save would not work
	(function() {Testcase.sendRequest('col', element);}).delay(500);
}

/**
 * sends the ajax request. as type only 'row' or 'col'
 * are allowed.
 */
Testcase.sendRequest = function(type, element) {
	if (type != 'row' && type != 'col') {
		return;
	}

	var topic = KNOWWE.helper.gup('page')
	var table = element.parentNode.id;
		
	var params = {
        action : 'AppendTableNodesAction',
        type : type,
        KWiki_Topic : topic,
        table : table
    }

    var options = {
        url : KNOWWE.core.util.getURL ( params ),
        loader : true,
        response : {
            action : 'none',
            fn : function(){
				if (table)
					KNOWWE.core.rerendercontent.updateNode(table, topic, null);	
            }
        }
    }
    new _KA( options ).send();
	(function() {Testcase.addNewAnswers(table);}).delay(700);
}



/**
 * returns the tablelines of the currently edited table
 */
Testcase.getTableLines = function(table) {
	return table.getElement('tbody').childNodes;
}

/**
 * adds an event to each td in the headerLine which
 * fires if the select is changed
 */
Testcase.addNewAnswers = function(table) {

	var wikitable = $(table).getElement('table');
	
	if (!wikitable) {
		return;
	}
	
	var headerNodes = wikitable.getElement('tr').getElements('th');
	if (!headerNodes || headerNodes.length == 0) {
		headerNodes = wikitable.getElement('tr').getElements('td');
	}
	
	for (var j = 0; j < headerNodes.length; j++) {
		
			if (headerNodes[j].firstChild && headerNodes[j].firstChild.nodeName != '#text') {
				var select = headerNodes[j].firstChild;
				$(select).addEvent('change', function(event) {
					Testcase.changeEvent(event);
				});
		}
		
	}
}

/**
 * the even which is fired when the select is changed
 */
Testcase.changeEvent = function(event) {
	var headerElement = event.currentTarget;
	var table = Testcase.findParentWikiTable(headerElement);
	
	var params = {
	        action : 'GetNewQuickEditAnswersAction',
	        KWiki_Topic : KNOWWE.helper.gup('page'),
	        element : headerElement.value
	    }

	    var options = {
	        url : KNOWWE.core.util.getURL ( params ),
	        loader : true,
	        response : {
	            action : 'none',
	            fn : function(){
					Testcase.changeFieldsAccordingToHeader(this, headerElement, table);
				}
	        }
	    }
		new _KA( options ).send();
}

Testcase.findParentWikiTable = function(element) {
	while (true) {
		if (element.className == "wikitable knowwetable") {
			break;
		}
		element = element.parentNode;
		if (element.tagName == 'BODY') {
			return null;
		}
	}
	return element;
}

/**
 * changes the fields in the table according to the new header and
 * the answers from the request
 */
Testcase.changeFieldsAccordingToHeader = function(request, headerElement, table) {
	var text = request.responseText;
	var newAnswers = text.split('[:;:]');
	
	var cellID = '';
	// strange bug, sometimes the top line is called TableHeaderLine
	// sometimes just TableLine
	if (headerElement.id.indexOf('/TableHeaderLine') > 0) {
		cellID = headerElement.id.substring(headerElement.id.indexOf('/TestcaseTableHeaderLine/TestcaseTableCell') + 42);
	} else {
		cellID = headerElement.id.substring(headerElement.id.indexOf('/TestcaseTableLine/TestCaseTableCell') + 36);
	}
	
	cellID = cellID.substring(0, cellID.indexOf('/'));
	var lines = Testcase.getTableLines(table);
	var empty = false;
	
	if (text == '[:]EMPTY[:]') {
		empty = true;
	}
	
	// if no change is needed, return
	if  (!Testcase.checkForChange(newAnswers, lines[1].childNodes[cellID -1])){
		return;
	}
	
	var current = '';
	var newOption = '';
	
	for (var i = 1; i < lines.length; i++) {
		current = lines[i].childNodes[cellID -1].firstChild;
		
		
		// remove all old select options
		while (current.hasChildNodes()) {
			current.removeChild(current.lastChild);
		}
		
		// if the question type needs answers in a select element
		if (!empty) {
			// if the old question was not a mc/oc/yn question
			// there is no select element, so the input field 
			// needs to be replaced with a select field
			// and an event has to be added in order to correctly
			// save changes
			if (current.nodeName != "SELECT") {
				var newSelect = document.createElement("select");
				newSelect.id = current.id;
				current.parentNode.replaceChild(newSelect, current);
				current = newSelect;
				$(current).addEvent('change', function(event) {
					Testcase.saveInputAfterChange(event);
				})
			}
			
		
			// add the new select options
			for (var j = 0; j < newAnswers.length; j++) {
				newOption = document.createElement("option");
				newOption.value = newAnswers[j];
				newOption.text = newAnswers[j];
				newOption.className = 'table-edit-node';
				current.appendChild(newOption);
			}
			
			
			
			// save every element, so the change will be visible
			// after clicking the check mark
			KNOWWE.table.getMap().set(current.id, current.value);
			
		// if the question just needs an input field
		} else {
			newOption = document.createElement("input");
			newOption.id = current.id;
			newOption.type = 'text';
			newOption.value = '';
			newOption.className = 'table-edit-node';
			current.parentNode.replaceChild(newOption, current);
			
			
			$(newOption).addEvent('change', function(event) {
				Testcase.saveInputAfterChange(event);
			})
			
			// save every element, so the change will be visible
			// after clicking the check mark
			KNOWWE.table.getMap().set(newOption.id, newOption.value);
		}
		
		
	}
}

/**
 * checks if the tables needs to be changed (e.g. the question type 
 * is different or there is an new mc/oc question
 */
Testcase.checkForChange = function(newAnswers, sampleChild) {
	var currentOptions = sampleChild.firstChild.childNodes;
	
	if (currentOptions.length != newAnswers.length) {
		return true;
	}
	
	for (var i = 0; i < currentOptions.length; i++) {
		if (newAnswers.indexOf(currentOptions[i].text) < 0) {
			return true;
		}
	}
	return false;
}


/**
 * needed so after switching from input to select fields
 * and the other way around in order to save changes
 */
Testcase.saveInputAfterChange = function(event) {
    var el = _KE.target(event);
	KNOWWE.table.getMap().set(el.id, el.value);
}


Testcase.runTestcaseNew = function(element) {
	element.parentNode.className = 'testcaseExecuted';
	
	
	var topic = KNOWWE.helper.gup('page')
	
	var params = {
        action : 'RunTestcaseAction',
        KWiki_Topic : topic,
        execLine : element.id
    }

    var options = {
        url : KNOWWE.core.util.getURL ( params ),
        loader : true,
        response : {
            action : 'none',
            fn : function(){
				KNOWWE.helper.observer.notify('update');
			}

        }
    }
    new _KA( options ).send();
	
}
/**
 * runs a Testcase from the TestcaseTable.
 * Takes all values from the line and the header line with the keys
 * and sends them to RunTestcaseAction.
 */
Testcase.runTestcase = function(element) {
	element.parentNode.className = 'testcaseExecuted';
	var table = Testcase.findParentWikiTable(element);
	var lines = Testcase.getTableLines(table);
	var headerLine = lines[0].getElements('td');
	if (!headerLine || headerLine.length == 0) {
		headerLine = lines[0].getElements('th');
	}
	var currentLine = Testcase.findLineOfElement(element).getElements('td');
	
	var headerLineToLine = '';
	var currentLineToLine = '';
	for (var i = 0; i < headerLine.length; i++) {
		if (headerLine[i].childNodes.length > 0) {
			headerLineToLine += headerLine[i].lastChild.nodeValue + ',.,';
		}
		currentLineToLine += currentLine[i].lastChild.nodeValue + ',.,'; 
	}
	
	headerLineToLine = headerLineToLine.substring(0, headerLineToLine.length - 3);
	currentLineToLine = currentLineToLine.substring(0, currentLineToLine.length -3 );

		
	
	var topic = KNOWWE.helper.gup('page')
		
	var params = {
        action : 'RunTestcaseAction',
        KWiki_Topic : topic,
        headerLine : headerLineToLine,
        currentLine : currentLineToLine
    }

    var options = {
        url : KNOWWE.core.util.getURL ( params ),
        loader : true,
        response : {
            action : 'none',
            fn : function(){
				KNOWWE.helper.observer.notify('update');
			}

        }
    }
    new _KA( options ).send();
}

Testcase.findLineOfElement = function(element) {
	var e = $(element);
	while (e.tagName != 'BODY') {
		if (e.tagName == 'TR') {
			return e;
		} else {
			e = e.parentNode;
		}
	}
}


/**
 * saves the table before adding a new col/row
 */
Testcase.saveTable = function() {
    var n = '';
    KNOWWE.table.getMap().forEach(function(key, value){
        n += key + ";-;" + value + "::";
    });
    n = n.substring(0, n.lastIndexOf('::'));

    var params = {
        action : 'UpdateTableKDOMNodesAction',
        TargetNamespace : n,
        KWiki_Topic : KNOWWE.helper.gup('page')
    }

    var options = {
        url : KNOWWE.core.util.getURL ( params ),
        loader : true,
        response : {
            action : 'none',
            fn : function(){
    			
            }
        }
    }
    new _KA( options ).send();
}