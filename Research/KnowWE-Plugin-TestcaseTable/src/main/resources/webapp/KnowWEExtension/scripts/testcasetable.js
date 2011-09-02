var Testcase = {};

Testcase.testcaseSkipped = "testcaseSkipped";
Testcase.testcaseExecuted = "testcaseExecuted";

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
	(function() {KNOWWE.table.init();}).delay(700);
}



/**
 * returns the tablelines of the currently edited table
 */
Testcase.getTableLines = function(table) {
	var lines = table.getElement('tbody').childNodes;
	var tableLines = [];
	for (var i = 0 ; i < lines.length; i++) {
		if (lines[i].nodeName === 'TR') {
			tableLines.push(lines[i]);
		}
	}
	return tableLines;
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
	
	var lines = Testcase.getTableLines(table);
	var headerLineElements = lines[0].childNodes;
	var empty = false;
	var cellID = '';

	
	for (var i = 0; i < headerLineElements.length; i++) {
		var select = headerLineElements[i].getElement('select');
		if (select == headerElement) {
			cellID = i;
			break;
		}
	}

	if (text == '[:]EMPTY[:]') {
		empty = true;
	}
	
	// if no change is needed, return
	if  (!Testcase.checkForChange(newAnswers, lines[1].childNodes[cellID])){
		return;
	}
	
	var current = '';
	var newOption = '';
	
	for (var i = 1; i < lines.length; i++) {
		current = lines[i].childNodes[cellID].firstChild;
		
		
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

/**
 * runs a Testcase via RunTestcaseAction
 */
Testcase.runTestcase = function(el, including) {
	var topic = KNOWWE.helper.gup('page')
	
	var row = Testcase.findLineOfElement(el);
	
	var params = {
        action : 'RunTestcaseAction',
        KWiki_Topic : topic,
        execLine : row.id,
        multiLines : including
    }

    var options = {
        url : KNOWWE.core.util.getURL ( params ),
        loader : true,
        response : {
            action : 'none',
            fn : function(){
            	KNOWWE.core.util.updateProcessingState(-1);
				KNOWWE.helper.observer.notify('update');
				var table = Testcase.findParentWikiTable(el);
				KNOWWE.core.rerendercontent.updateNode(table.parentNode.id, topic, null);
			},
            onError : function () {
	        	KNOWWE.core.util.updateProcessingState(-1);                    	
            }

        }
    }
	KNOWWE.core.util.updateProcessingState(1);
    new _KA( options ).send();
	//Testcase.colorExecutedLines(row, including);	
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


/**
 * colors lines after their execution
 */
Testcase.colorExecutedLines = function(row, including) {
	var trs = row.parentNode.childNodes;

	if (including) {
		for (var i = 1; i < trs.length; i++) {
			if (trs[i] == row) { 
                break; 
           } else if (trs[i].className == Testcase.testcaseExecuted || trs[i].className == Testcase.testcaseSkipped) { 
                continue; 
           } else { 
                trs[i].className = Testcase.testcaseExecuted; 
           }
		}
	} else {
		for (var i = 1; i < trs.length; i++) {
			if (trs[i] == row) {
				break;
			} else if (trs[i].className == Testcase.testcaseExecuted) {
				continue;
			} else {
				trs[i].className = Testcase.testcaseSkipped;
			}
		}
	}
	row.className = Testcase.testcaseExecuted;
}

/**
 * resets all run lines
 */
Testcase.resetTestcase = function(sectionID, rerun) {
	var topic = KNOWWE.helper.gup('page')
		
	var params = {
        action : 'TestcaseTableResetAction',
        KWiki_Topic : topic,
        rerun : rerun,
        table : sectionID
    }

    var options = {
        url : KNOWWE.core.util.getURL ( params ),
        loader : true,
        response : {
            action : 'none',
            fn : function(){
				KNOWWE.core.rerendercontent.update(); //TODO 
				KNOWWE.core.rerendercontent.updateNode(sectionID, topic, null);
				KNOWWE.core.util.updateProcessingState(-1); 
            },
            onError : function () {
	        	KNOWWE.core.util.updateProcessingState(-1);                    	
            }
        }
    }
	KNOWWE.core.util.updateProcessingState(1);
    new _KA( options ).send();
    
    //Testcase.resetTableCSS(sectionID);
}


/**
 * resets the css back to standard
 */
Testcase.resetTableCSS = function(sectionID) {
	var sec = $(sectionID);
	var tables = $$('table.wikitable');
	var tds;
	
	for (var i = 0; i < tables.length; i++) {
		trs = tables[i].getElements('tr');
		for (var j = 1; j < trs.length; j++) {
			trs[j].removeClass(Testcase.testcaseExecuted);
			trs[j].removeClass(Testcase.testcaseSkipped);
		}
	}
}


Testcase.importTestcase = function(sectionID) {
	
	var form = new Element('form', {
		id: 'testcaseImport',
		method:'post',
		enctype: 'multipart/form-data',
		action: 'KnowWE.jsp?action=TestcaseImportAction'
	});
	
	var chooser = new Element('input', {
		type: 'file',
		name: 'testcasefile',
		size: 50
	});
	
	var section = new Element('input', {
		type: 'hidden',
		name: 'kdomid',
		value: sectionID
	});
	
	chooser.inject(form);
	section.inject(form);
	
	var action = function() {
		$('testcaseImport').submit();
	}
	
	KNOWWE.helper.message.showOKCancelDialog(form,'Import testcase' ,action);
	
}

