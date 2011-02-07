//author Florian Ziegler

function Drools() { 
}

// calls Drools.createFact() if enter is pressed
function submitenter(myfield, e) {
	var keycode = getKeyCode(e);

	if (keycode == 38 || keycode == 40) {
		Drools.getLastCommands(keycode);
	}

	if (keycode == 13) {
		if (e.shiftKey || $('droolsField')) {
			Drools.createFact();
			return false;
		}
	}
	
//	return true;
}

function getKeyCode(event) {
	event = event || window.event;
	return event.keyCode;
}



checkEmpty = function(text) {
	text = text.replace(/\s/g,"");
	
	return (text.length > 0);
}

// the function, which is called from the command line
Drools.createFact = function() {
	var text = document.DroolsCommandLine.fact.value;

	if (text === "")
		return;
	
	if ($('droolsField')) {
		Drools.sendRequest(text);
		
	} else if ($('droolsTextArea')) {
		var textToArray = text.split('\n');
		for (var i = 0; i < textToArray.length; i++) {
			if (checkEmpty(textToArray[i])) {
				Drools.sendRequest(textToArray[i]);
			}
		}
	}
}

Drools.sendRequest = function(command) {
	if (command === "clear") {
		$('drools-console').update('');
		Drools.correctConsoleSize();
		document.DroolsCommandLine.fact.value = '';
		return;
	}
	
	if (command === "help") {
		Drools.consoleOutput("<tt>clear</tt>: Clear console");
		Drools.consoleOutput("<tt>reset</tt>: Reset session");
		Drools.consoleOutput("<tt>store &lt;Session name&gt;</tt>: Store current session");
		Drools.consoleOutput("<tt>load &lt;Session name&gt;</tt>: Load stored session");
		Drools.consoleOutput("<tt>fire</tt>: Fire the rules");
		Drools.consoleOutput("<tt>get &lt;Fact&gt;</tt>: Display the fact value");
		Drools.consoleOutput("<tt>set &lt;Fact&gt; = &lt;Value&gt;</tt>: Set the fact to the value");
		Drools.consoleOutput("Available commands:");
		document.DroolsCommandLine.fact.value = '';
		return;
	}
	
	if (command && command.startsWith('store') && command.indexOf(' ') > 0) {
		var completeHistory = Drools.getCommandsFromHistory();
		command += '&sessionContent=' + completeHistory; 
	}
		
	var title = $$("meta[name=wikiPageName]")[0].content;

	var url = 'KnowWE.jsp?action=DroolsAction&command=' + command + '&title=' + title;

	new Ajax.Request(url, {
		method: 'get',
		onSuccess: function(transport) {
			// values from the response 
			var respText = transport.responseText.toString();
			var messages = respText.split("\n");
			var timeStamp = Drools.createTimestamp();
			
			// command line response
			for (var x = 0; x < messages.length - 1; x++) {
				var msg = messages[x].evalJSON(true);
				document.DroolsCommandLine.fact.value = '';
				
				// change console output
				var console = document.getElementById('drools-console');
				if (console.childNodes.length == 0) {
					console.style.display = '';
				}
				
				Drools.consoleOutput(msg.message, msg.status);
				Drools.consoleOutput(timeStamp + Drools.htmlescape(msg.command), 0);
			}
			
		},
		onFailure: function() {
			document.DroolsCommandLine.fact.value =
				'AJAX connection error. Please try again later.'; 
		},
		onException: function(transport, exception) {
			document.DroolsCommandLine.fact.value =
				'Internal error: ' +  exception.toString();
		}
	}); 
}

// adds the latest command to the console in first position
Drools.consoleOutput = function (text, type) {
	var console = document.getElementById('drools-console');
	console.style.visibility = "visible";
	var newSpan = document.createElement('span');
	var newLine = document.createElement('br');
	newSpan.innerHTML = Drools.htmlescape(text);
	
	if (type == 1)
		newSpan.className = "droolsConsoleError";
	
	var firstChild = console.childNodes[0];
	if (firstChild) {
		console.insertBefore(newLine, console.firstChild);
		console.insertBefore(newSpan, console.firstChild);
	} else {
		console.appendChild(newSpan);
		console.appendChild(newLine);
	}
	
	Drools.correctConsoleSize();
}

// adds a scrollbar if 10 or more entries are on the console
Drools.correctConsoleSize = function() {
	var console = document.getElementById('drools-console');
	if (console.childNodes.length > 20) {
		console.style.height = '160px';
	}
}

// expands/collapes the console
Drools.showConsole = function() {
	var console = document.getElementById('drools-console');
	var button = document.getElementById('droolsExpand');
	if ((console.style.display) === 'none') {
		console.style.display = '';
		button.value = '(-)';
	} else {
		console.style.display= 'none';
		button.value = '(+)';
	}
}

Drools.switchToTextarea = function() {
	var current = $('DroolsCommandLine').childNodes[0];
	var button = $('droolsTextAreaButton');
	
	if (current.className == 'droolsField') {
		var textArea = document.createElement('textarea');
		textArea.id = 'droolsTextArea';
		textArea.className = 'droolsTextArea';
		textArea.name = 'fact';
		$('DroolsCommandLine').replaceChild(textArea, $('DroolsCommandLine').childNodes[0]);
		button.value = ('Single Command');
		button.setAttribute('class', 'droolsButtonExtended');
	
	} else if (current.className == 'droolsTextArea') {
		var field = document.createElement('input');
		field.type = 'text';
		field.id = 'droolsField';
		field.className = 'droolsField';
		field.name = 'fact';
		$('DroolsCommandLine').replaceChild(field, $('DroolsCommandLine').childNodes[0]);
		button.value = ('Batch');
		button.setAttribute('class', 'droolsButton');
	}
}

Drools.createTimestamp = function() {
	var currentTime = new Date() ;
   	var month = currentTime.getMonth() + 1; 
   	var day = currentTime.getDate() ;
   	var year = currentTime.getFullYear(); 
   	var hours = currentTime.getHours() + "";
   	
   	if (hours.length == 1) {
   		hours = "0" + hours;	
   	}
   	var minutes = currentTime.getMinutes() + "";
   	if (minutes.length == 1) {
   		minutes = "0" + minutes;
   	}
   	var seconds = currentTime.getSeconds() + "";
   	if (seconds.length == 1) {
   		seconds = "0" + seconds;
   	}
   	// Drools.getLastCommands();
   	return '[' + hours + ':' + minutes + ':' + seconds + '] ';
}


Drools.getLastCommands = function(keycode) {
	var completeHistory = $('drools-console').childNodes;
	var current = $('droolsField').value;
	var noSuggestions = $('Autocomplete_droolsField').style.display;
	

	// only if there is atleast something as history
	if (completeHistory && completeHistory.length > 0 && noSuggestions) {
		var pos = 0;
		var newValue;
		// only if there is already a value
		if (current) {
			for ( var i = 0; i < completeHistory.length; i++) {
				var node = completeHistory[i].firstChild;
				if (node && node.nodeValue.indexOf('[') == 0
						&& node.nodeValue.indexOf(']') == 9) {
					var textValue =  completeHistory[i].firstChild.nodeValue.substring(11);
					if (textValue === current) {
						if (keycode == 38) {
							pos = i + 1;
							newValue = Drools.getNextCommandInHistory(pos);	
						} else {
							pos = i - 1;
							newValue = Drools.getPreviousCommandInHistory(pos);	
						}
						break;
					} 
				}
			}
		}
		if (pos == 0) {
			newValue = Drools.getNextCommandInHistory(pos);	
		};
		$('droolsField').value = newValue;
	}
}

// returns the value of the next (or first) command 
Drools.getNextCommandInHistory = function (pos) {
	var completeHistory = $('drools-console').childNodes;
	var nextCommand;
	
	for (var i = pos; i < completeHistory.length; i++) {
		var node = completeHistory[i].firstChild;
		if (node && node.nodeValue.indexOf('[') == 0
				&& node.nodeValue.indexOf(']') == 9) {
			nextCommand = completeHistory[i].firstChild.nodeValue.substring(11);
			break;
		}
	}
	if (nextCommand) {
		return nextCommand;
	} else {
		return completeHistory[0].firstChild.nodeValue.substring(11);
	}
}

Drools.getPreviousCommandInHistory = function(pos) {
	var completeHistory = $('drools-console').childNodes;
	var prevCommand;
	
	for (var i = pos; i >= 0; i--) {
		var node = completeHistory[i].firstChild;
		if (node && node.nodeValue.indexOf('[') == 0
				&& node.nodeValue.indexOf(']') == 9) {
			prevCommand = completeHistory[i].firstChild.nodeValue.substring(11);
			break;
		}
	}
	if (prevCommand) {
		return prevCommand;
	} else {
		return completeHistory[completeHistory.length -4].firstChild.nodeValue.substring(11);
	}
}

	
//HTML escaping
Drools.htmlescape = function(str) {
	return str.replace(/</g, "&lt;").replace(/>/g, "&gt;");
}

Drools.getCommandsFromHistory = function() {
	var completeHistory = $('drools-console').childNodes;
	var commands = '';
	
	for (var i = 0; i < completeHistory.length; i++) {
		var node = completeHistory[i].firstChild;
		if (node && node.nodeValue.indexOf('[') == 0
				&& node.nodeValue.indexOf(']') == 9) {
			commands += node.nodeValue.substring(11) + 'DROOLSLINEBREAK';
		}
	}
	return commands;
}

	