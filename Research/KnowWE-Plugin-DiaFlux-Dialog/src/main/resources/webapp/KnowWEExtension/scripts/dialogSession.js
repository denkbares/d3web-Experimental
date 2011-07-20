if (!DiaFluxDialog) {
	DiaFluxDialog = {};
}

DiaFluxDialog.Session = {};

DiaFluxDialog.Session.showSessions = function() {
	if ($('sessionNavigator').getStyle('display') === 'block') {
		$('loadSessionParent').setStyle('display', 'block');
		$('sessionNavigator').setStyle('display', 'none');
	}
	DiaFluxDialog.Session.loadRequest();
}

DiaFluxDialog.Session.saveSession = function() {
	DiaFluxDialog.Session.saveRequest();
}

DiaFluxDialog.Session.saveRequest = function() {
	params = {
			action : 'DiaFluxDialogSessionAction',
	        KWikiWeb : 'default_web',
	        type : 'save'
		};
	

	// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}

DiaFluxDialog.Session.loadRequest = function() {
	params = {
			action : 'DiaFluxDialogSessionAction',
	        KWikiWeb : 'default_web',
	        type : 'load'
		};
	

	// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				DiaFluxDialog.Session.extractSessionsFromRequest(this);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}

DiaFluxDialog.Session.extractSessionsFromRequest = function(request) {
	var text = request.responseText;
	var sessions = text.split('[SESSION]');
//	var hiddenSessions = $('hiddenSessions');
//	
//	for (var i = 0; i < sessions.length; i++) {
//		if (sessions[i] != "") {
//			var input = Builder.node('input', {
//				className: 'session',
//				type: 'hidden',
//				value: sessions[i]
//			});
//			hiddenSessions.appendChild(input);
//		}
//	}
	DiaFluxDialog.Session.createSelect(sessions);
}


DiaFluxDialog.Session.createSelect = function(sessions) {
	if ($('selectSession') !== null) {
		$('selectSession').parentNode.removeChild($('selectSession'));
	}	
	if ($('selectSessionButton') !== null) {
		$('selectSessionButton').parentNode.removeChild($('selectSessionButton'));
	}
		
	var select = Builder.node('select', {
		id : 'selectSession'
	});
	
	
	$('loadSessionParent').appendChild(select);
//	var sessions = $$('.session');
	
	for (var i = 0; i < sessions.length; i++) {
		if (sessions[i] !== ''){
			var option = Builder.node('option', {
				value: sessions[i]
			});
			option.appendChild(Document.createTextNode('Session ' + i));
			select.appendChild(option);
		}
	}
	
	var button = Builder.node('div', {
		id : 'selectSessionButton',
		onClick : 'DiaFluxDialog.Session.loadSession()'
	});
	button.appendChild(Document.createTextNode('Select'));
	$('loadSessionParent').appendChild(button);
}

DiaFluxDialog.Session.loadSession = function() {
	var select = $('selectSession');
	var selected = select.childNodes[select.selectedIndex];
	var value = selected.value;
	DiaFluxDialog.loadedSession = DiaFluxDialog.Session.extractPairs(value);
	
	if ($('sessionNavigator').getStyle('display') === 'none') {
		$('sessionNavigator').setStyle('display','block');
		$('loadSessionParent').setStyle('display', 'none');
		DiaFluxDialog.reset();
	}	
}

DiaFluxDialog.Session.extractPairs = function(string) {
	var array = [];
	var forwardKnowledge = string.substring(18, string.indexOf('[PATH]'));
	var path = string.substring(string.indexOf('[PATH]') + 6);
	
	forwardKnowledge = forwardKnowledge.split('#####');
	for (var i = 0; i < forwardKnowledge.length; i++) {
		if (forwardKnowledge[i] !== '') {
			var parts = forwardKnowledge[i].split('+++++');
			var pair = [];
			
			for (var j = 0; j < forwardKnowledge.length; j++) {
				pair.push(parts[j]);
			}
			array.push(pair);
		}
	}
	
	array.push('[PARTS]');
	
	
	path = path.split('#####');
	for (var i = 0; i < path.length; i++) {
		if (path[i] !== '') {
			var parts = path[i].split('+++++');
			var pair = [];
			
			for (var j = 0; j < parts.length; j++) {
				pair.push(parts[j]);
			}
			array.push(pair);
		}
	}
	return array;
}


// forward knowledge?
DiaFluxDialog.Session.nextStep = function() {
	var session = DiaFluxDialog.loadedSession;
	if (session.length > 0) {
		var step = session[0];
		if (step === '[PARTS]') {
			session.remove(step);
			DiaFluxDialog.sendAddActiveFlowchartRequest(DiaFluxDialog.activeFlowchart.name);
			DiaFluxDialog.Session.nextStep();
		} else {
			var step = session[0];
			if (step.length >= 2) {
				var question = step[0];
				step.remove(question);
				var answer = step;

				DiaFluxDialog.activateRuleWithName(question, answer, true);
				session.remove(step);
			}
		}
	}
}

DiaFluxDialog.Session.playSession = function() {
	var session = DiaFluxDialog.loadedSession;
	
	while (session.length > 0) {
		DiaFluxDialog.Session.nextStep();
	}
}

