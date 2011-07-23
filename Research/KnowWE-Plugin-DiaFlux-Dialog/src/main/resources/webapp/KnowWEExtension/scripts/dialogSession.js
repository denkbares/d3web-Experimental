if (!DiaFluxDialog) {
	DiaFluxDialog = {};
}

DiaFluxDialog.Session = {};


/**
 * shows the available sessions
 */
DiaFluxDialog.Session.showSessions = function() {
	DiaFluxDialog.Session.removeAllSelects();
	
	if ($('sessionNavigator').getStyle('display') === 'block') {
		sessionParent.setStyle('display', 'block');
		$('sessionNavigator').setStyle('display', 'none');
	}
	DiaFluxDialog.Session.loadRequest();
}

/**
 * saves a session
 */
DiaFluxDialog.Session.saveSession = function() {
	DiaFluxDialog.Session.saveRequest();
}


/**
 * sends the request to save a session
 */
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


/**
 * sends the request to load a session
 */
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


/**
 * extracts sessions from the response of a request
 */
DiaFluxDialog.Session.extractSessionsFromRequest = function(request) {
	var text = request.responseText;
	var sessions = text.split('[SESSION]');
	DiaFluxDialog.Session.createSelect(sessions);
}

/**
 * creates a select with options from the sessions
 */
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
	
	DiaFluxDialog.Session.appendOptionsToSelect(select, sessions);
	
	var button = Builder.node('button', {
		id : 'selectSessionButton',
		onClick : 'DiaFluxDialog.Session.loadSession()'
	});
	button.appendChild(Document.createTextNode('Select'));
	$('loadSessionParent').appendChild(Document.createElement('br'));
	$('loadSessionParent').appendChild(button);
}


/**
 * adds options, each one being a single session, to a select
 * from the sessions
 */
DiaFluxDialog.Session.appendOptionsToSelect = function(select, sessions) {
	for (var i = 0; i < sessions.length; i++) {
		if (sessions[i] !== ''){
			var option = Builder.node('option', {
				value: sessions[i]
			});
			option.appendChild(Document.createTextNode('Session ' + i));
			select.appendChild(option);
		}
	}
}


/**
 * loads the selected session 
 */
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


/**
 * extracts question/finding pairs divided in forward knowledge and
 * the path from a string
 */
DiaFluxDialog.Session.extractPairs = function(string) {
	var array = [];
	var forwardKnowledge = string.substring(18, string.indexOf('[PATH]'));
	var path = string.substring(string.indexOf('[PATH]') + 6);
	
	forwardKnowledge = forwardKnowledge.split('#####');
	
	array.push('[FORWARDKNOWLEDGE]');
	for (var i = 0; i < forwardKnowledge.length; i++) {
		if (forwardKnowledge[i] !== '') {
			var parts = forwardKnowledge[i].split('+++++');
			var pair = [];
			
			for (var j = 0; j < parts.length; j++) {
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


/**
 * executes the next step of a loaded session
 */
DiaFluxDialog.Session.nextStep = function() {
	var session = DiaFluxDialog.loadedSession;
	if (session.length > 0) {
		var step = session[0];
		if (step === '[FORWARDKNOWLEDGE]') {
			var forwardKnowledge = DiaFluxDialog.Session.extractForwardknowledge(session);
			DiaFluxDialog.Session.setForwardKnowledge(forwardKnowledge);
			DiaFluxDialog.Session.nextStep();
			
		} else if (step === '[PARTS]') {
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

/**
 * executes all steps from a session one after the other
 * TODO too many requests
 */
DiaFluxDialog.Session.playSession = function() {
	var session = DiaFluxDialog.loadedSession;
	
	while (session.length > 0) {
		DiaFluxDialog.Session.nextStep();
	}
}


/**
 * fires an array of forwards knowledge
 */
DiaFluxDialog.Session.setForwardKnowledge = function(forwardKnowledge) {
	for (var i = 0; i < forwardKnowledge.length; i++) {
		var pair = forwardKnowledge[i];
		var question = pair[0];
		var	answer = {ValueNum : pair[1]};
		DiaFluxDialog.sendSetSingleFindingRequest(question, answer, false);
	}
}

/**
 * compares two sessions
 */
DiaFluxDialog.Session.compareSessions = function() {
	DiaFluxDialog.Session.removeAllSelects();
	DiaFluxDialog.Session.compareSessionsRequest();
}

/**
 * sends the request to get the saved sessions which can be compared
 */
DiaFluxDialog.Session.compareSessionsRequest = function() {
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
 				DiaFluxDialog.Session.createCompareSessionsSelects(this);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}


/**
 * creates the selects from a request which contain the saved sessions
 */
DiaFluxDialog.Session.createCompareSessionsSelects = function(request) {
	var text = request.responseText;
	var sessions = text.split('[SESSION]');
	
	var select1	 = Builder.node('select', {
		id : 'compareSelect1'
	});
	var select2	 = Builder.node('select', {
		id : 'compareSelect2'
	});
	
	var compareSessionParent = $('compareSessionParent');
	compareSessionParent.appendChild(select1);
	compareSessionParent.appendChild(select2);
	
	DiaFluxDialog.Session.appendOptionsToSelect(select1, sessions);
	DiaFluxDialog.Session.appendOptionsToSelect(select2, sessions);
	
	var button = Builder.node('button', {
		id : 'compareSessionButton',
		onClick : 'DiaFluxDialog.Session.startCompare()'
	});
	button.appendChild(Document.createTextNode('Select'));
	compareSessionParent.appendChild(Document.createElement('br'));
	compareSessionParent.appendChild(button);
}


/**
 * compares two selected sessions
 */
DiaFluxDialog.Session.startCompare = function() {
	var select1 = $('compareSelect1');
	var session1 = DiaFluxDialog.Session.extractPairs(select1.childNodes[select1.selectedIndex].value);
	var select2 = $('compareSelect2');
	var session2 = DiaFluxDialog.Session.extractPairs(select2.childNodes[select2.selectedIndex].value);


	var forwardKnowledge1 = DiaFluxDialog.Session.extractForwardknowledge(session1);
	var forwardKnowledge2 = DiaFluxDialog.Session.extractForwardknowledge(session2);
	
	DiaFluxDialog.Session.setForwardKnowledge(forwardKnowledge1);
	
	while (session1.length > 0) {
		if (session1[0] === '[PARTS]') {
			session1.remove(session1[0]);
			if (session2[0] === '[PARTS]') {
				session2.remove(session2[0]);
			}
			continue;
		}
		var edge1 = DiaFluxDialog.Utils.getMatchingEdge(session1[0][0], session1[0][1]);
		var edge2 = DiaFluxDialog.Utils.getMatchingEdge(session2[0][0], session2[0][1]);
		
		DiaFluxDialog.activateRule(edge1, true, false);
		if (edge1 === edge2) {
			session1.remove(session1[0]);
			session2.remove(session2[0]);
		} else {
			DiaFluxDialog.Session.colorDifferenceNode(edge2.sourceNode);
			DiaFluxDialog.Session.colorDifferenceRule(edge2);
			session1.remove(session1[0]);
			session2.remove(session2[0]);
			
			var nextMeeting = DiaFluxDialog.Session.findNextMeeting(session1, session2);
			
			while (session1.length > 0 && session1[0][0] !== nextMeeting) {
				edge1 = DiaFluxDialog.Utils.getMatchingEdge(session1[0][0], session1[0][1]);
				DiaFluxDialog.activateRule(edge1, true, false);
				session1.remove(session1[0]);
			}
			
			while (session2.length > 0 && session2[0][0] !== nextMeeting) {
				edge2 = DiaFluxDialog.Utils.getMatchingEdge(session2[0][0], session2[0][1]);
				DiaFluxDialog.Session.colorDifferenceNode(edge2.sourceNode);
				DiaFluxDialog.Session.colorDifferenceRule(edge2);
				session2.remove(session2[0]);
			}
		}
	}
}


/**
 * extracts forward knowledge from an array
 */
DiaFluxDialog.Session.extractForwardknowledge = function(session) {
	var forwardKnowledge = [];
	if (session.length > 0) {
		var step = session[0];
		if (step === '[FORWARDKNOWLEDGE]') {
			session.remove(step);
			var current = session[0];
			while (current !== '[PARTS]') {
				forwardKnowledge.push(current);
				session.remove(current);
				var current = session[0];
			}
		}
	}
	return forwardKnowledge;
}


/**
 * finds the next node where both sessions meet
 */
DiaFluxDialog.Session.findNextMeeting = function(session1, session2) {
	for (var i = 0; i < session1.length; i++) {
		for (var j = 0; j < session2.length; j++) {
			if (session1[i][0] === session2[j][0]) {
				return session1[i][0];
			}
		}
	}
	return null;
}


/**
 * colors a rule, if it is not yet visited, blue
 */
DiaFluxDialog.Session.colorDifferenceRule = function(flowRule) {
	if (!flowRule.dom.hasClass('ruleVisited')) {
		flowRule.dom.addClass('sessionDifferenceRule')
	}
}


/**
 * colors a node, if it is not yet visited, blue
 */
DiaFluxDialog.Session.colorDifferenceNode = function(flowNode) {
	if (!flowNode.dom.hasClass('nodeVisited')) {
		flowNode.dom.addClass('sessionDifferenceNode');
	}
}

/**
 * removes all selects and so on from load/compare
 */
DiaFluxDialog.Session.removeAllSelects = function() {
	DiaFluxDialog.Utils.removeAllChildNodes($('loadSessionParent'));
	DiaFluxDialog.Utils.removeAllChildNodes($('compareSessionParent'));
}

