
DiaFluxDialog = {};

DiaFluxDialog.FLOWCHARTID = 'DiaFluxDialogFlowchart';
DiaFluxDialog.FLOWCHARTPATHID = 'DiaFluxDialogPath';
DiaFluxDialog.flowcharts = [];

/**
 * the entry function to the dialog "fires" startNodes and adds events to the
 * next nodes
 */
DiaFluxDialog.addEvents = function() {
	
	if (!DiaFluxDialog.flowcharts.contains(theFlowchart)) {
		DiaFluxDialog.flowcharts.push(theFlowchart);
	}
	DiaFluxDialog.fireStartNodes();
}

/**
 * adds the onclick event to all follow up nodes (and their rules) of a node
 */ 
DiaFluxDialog.addEventsToFollowUp = function(flowNode) {
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(flowNode);
	
	for (var i = 0; i < outgoingRules.length; i++) {
		var currentNode = outgoingRules[i].targetAnchor.node;
		DiaFluxDialog.addClickHandlerToNode(currentNode);		
		DiaFluxDialog.addClickHandlerToRules(currentNode);
	}
}


/**
 * adds the onclick handler to all outgoing rules of a node
 */
DiaFluxDialog.addClickHandlerToRules = function(flowNode) {
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(flowNode);
	for (var j = 0; j < outgoingRules.length; j++) {
		outgoingRules[j].dom.removeEvents('click');
		outgoingRules[j].dom.addEvent('click', function(event) {
			DiaFluxDialog.clickRule(event);
		});
	}
}

/**
 * adds the onclick handler to a node (to create the context menu)
 */
DiaFluxDialog.addClickHandlerToNode = function(flowNode) {
	flowNode.dom.removeEvents('click');
	flowNode.dom.addEvent('click', function(event){
		DiaFluxDialog.createContextMenu(event);
	});
}


/**
 * fires all start nodes and returns them in an array
 */
DiaFluxDialog.fireStartNodes = function() {
	var nodes = theFlowchart.nodes;

	for (var i = 0; i < nodes.length; i++) {
		if (i === 0) {
			DiaFluxDialog.sendAddActiveFlowchartRequest(nodes[0].flowchart.name);
		}
		
		if (nodes[i].nodeModel.start && nodes[i].nodeModel.start === 'Start') {
			DiaFluxDialog.colorNode(nodes[i]);

			
			// color outgoing rules
			var rules = DiaFluxDialog.Utils.findOutgoingRules(nodes[i]);
			for (var j = 0; j < rules.length; j++) {
				DiaFluxDialog.activateRule(rules[j], false);
			}
		}
	}
}


/**
 * the function which is called when a rule is clicked
 */
DiaFluxDialog.clickRule = function(event) {
	var target = event.target;
	var htmlRule = DiaFluxDialog.Utils.findParentWithClass(target, 'Rule');
	var flowRule = DiaFluxDialog.Utils.findFittingRule(htmlRule);
	DiaFluxDialog.activateRule(flowRule, true);

}

/**
 * activates a rule, e.g. colors the rule and adds the event to its target node
 * and the outgoing rules
 */
DiaFluxDialog.activateRule = function(flowRule, sendRequest) {
	if (!flowRule.isActive) {
		flowRule.isActive = true;
	} else {
		DiaFluxDialog.deactivateRule(flowRule);
		return;
	}
	var sourceNode = flowRule.sourceNode;
	var targetNode = flowRule.targetNode;
	
	DiaFluxDialog.deactivateAllRulesExcept(flowRule);
	DiaFluxDialog.colorRule(flowRule);
	DiaFluxDialog.colorNode(sourceNode);
	
	DiaFluxDialog.increaseSupportForNode(targetNode);
	
	
	// -> exit node
	if (!DiaFluxDialog.Utils.hasOutgoingRules(targetNode)){
		DiaFluxDialog.colorNode(targetNode);
		DiaFluxDialog.sendGetNextActiveFlowchartRequest();
		return;
	}
	
	var action = targetNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	
	// -> solution or subflowchart
	if (!infoObject.type) {
		
		// subflowchart
		if (infoObject.startNames && infoObject.exitNames) {
			DiaFluxDialog.sendGetFlowchartRequest(infoObject.name);
			return;
		} 
		
		// solution
		DiaFluxDialog.colorNode(targetNode);
		var outGoingRules = DiaFluxDialog.Utils.findOutgoingRules(targetNode);
		for (var i = 0; i < outGoingRules.length; i++) {
			DiaFluxDialog.activateRule(outGoingRules[i], false);
		}
		return;
	}


	DiaFluxDialog.addClickHandlerToNode(targetNode);	
	DiaFluxDialog.addClickHandlerToRules(targetNode);
	
	var start = DiaFluxDialog.Utils.isStartNode(sourceNode);
	if (start || !sendRequest) {
		return;
	}
	
	var sourceAction = sourceNode.actionPane.action;
	var sourceInfoObject = KBInfo.lookupInfoObject(sourceAction.getInfoObjectName());
	var type = sourceInfoObject.type;
	var question = sourceInfoObject.name;
	if (type !== KBInfo.Question.TYPE_NUM) {	
		var selected = flowRule.guard.displayHTML;
		DiaFluxDialog.sendSetSingleFindingRequest(question, {ValueID : selected});
	
	// TODO num werte generieren
	} else {
		DiaFluxDialog.sendSetSingleFindingRequest();
	}
}

DiaFluxDialog.deactivateRule = function(flowRule) {
	flowRule.isActive = false;
	DiaFluxDialog.resetRuleColor(flowRule);
	DiaFluxDialog.decreaseSupportForNode(flowRule.targetNode);
}

DiaFluxDialog.activateNode = function(flowNode) {
	DiaFluxDialog.colorNode(flowNode);
	DiaFluxDialog.addEventsToFollowUp(flowNode);
}

DiaFluxDialog.deactivateNode = function(flowNode) {
	// deactivate outgoing rules first
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(flowNode);
	for (var i = 0; i < outgoingRules.length; i++) {
		DiaFluxDialog.deactivateRule(outgoingRules[i]);
	}
	DiaFluxDialog.resetNodeColor(flowNode);
}

/**
 * fires a node, i.e. sets the css class
 */
DiaFluxDialog.fireNode = function(event) {
	var target = event.target;	
	var hiddenNode = $('hiddenNodeId')	
	var flowNode = DiaFluxDialog.Utils.findFlowNodeById(hiddenNode.firstChild.textContent);

	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(flowNode);
	for (var i = 0; i < outgoingRules.length; i++) {
		DiaFluxDialog.resetRuleColor(outgoingRules[i]);
	}	

	
	var question = $('hiddenQuestion').innerHTML;
	var contextMenu = theFlowchart.dom.getElement('div.dialogBox');
		
	var trs = $(contextMenu).getElements('tr');
	var selected = '';
	var answer;
	
	// mc or oc question
	if (trs.length !== 0) {
		for (var i = 0; i < trs.length; i++) {
			var checkbox = trs[i].getElement('input')
			if (checkbox.checked) {
				// putting together the string with selected values
				if (selected != '') {
					selected += '#####';
				}
				selected += checkbox.value;
					
				// coloring the rules
				for (var j = 0; j < outgoingRules.length; j++) {
					if (outgoingRules[j].guard.displayHTML === checkbox.value) {
						DiaFluxDialog.activateRule(outgoingRules[j], false);
					}
				}
			}	
		}
		answer = {ValueID : selected};
	// numquestion
	} else {
		selected = $('numQuestion').value;
		answer = {ValueNum : selected};
	}
	var incomingRules = DiaFluxDialog.Utils.findIncomingRules(flowNode);
	for (var i = 0; i < incomingRules.length; i++) {
		if (!incomingRules[i].isActive) {
			DiaFluxDialog.activateRule(incomingRules[i], false);
		}
	}
	
	DiaFluxDialog.activateNode(flowNode);
	DiaFluxDialog.sendSetSingleFindingRequest(question, answer);
	contextMenu.parentNode.removeChild(contextMenu);
}

DiaFluxDialog.sendSetSingleFindingRequest = function(question, selected) {
	var master = $('hiddenMaster');
	if (master) {
		master = master.value;
	} else {
		return;
	}
	params = {
			action : 'SetSingleFindingAction',
	        KWikiWeb : 'default_web',
	        namespace : master,
	        ObjectID : question,
	        TermName : 'undefined'
		};
	
	params = KNOWWE.helper.enrich( selected, params );
		
		// options for AJAX request
	    options = {
	        url : KNOWWE.core.util.getURL( params ),
	        response : {
	    		action : 'none',
	        	fn : function(){
	    			KNOWWE.helper.observer.notify('update');
	    		},
	    		onError : function () {
		        	KNOWWE.core.util.updateProcessingState(-1);                    	
                }
	        }
	    };
	    
	    // send AJAX request
	    KNOWWE.core.util.updateProcessingState(1);
	    new _KA( options ).send();
}

DiaFluxDialog.sendGetFlowchartRequest = function(name) {
	var master = $('hiddenMaster');
	if (master) {
		master = master.value;
	} else {
		return;
	}
	params = {
			action : 'DiaFluxDialogAction',
	        KWikiWeb : 'default_web',
	        master : master,
	        name : name,
	        type : 'getFlowchart'
		};
	

		// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				DiaFluxDialog.replaceFlowchart(this, name);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}


/**
 * replaces the current flowchart with the new
 * one from the request
 */
DiaFluxDialog.replaceFlowchart = function(request, name) {
	// hide all other flowcharts
	DiaFluxDialog.hideFlowcharts();
	
	// paste the new flowchart
	$(DiaFluxDialog.FLOWCHARTID).innerHTML += request.responseText;
	
	// change path
	$(DiaFluxDialog.FLOWCHARTPATHID).innerHTML += ' - ' + name;
	
	
	name += 'Source';
	
	// activate the new flowchart
	KBInfo._updateCache($('referredKBInfo'));
	Flowchart.createFromXML(DiaFluxDialog.FLOWCHARTID, $(name)).setVisible(true);
	DiaFluxDialog.addEvents();

}


/**
 * hides all flowcharts, e.g. makes display:none
 */
DiaFluxDialog.hideFlowcharts = function() {
	var parent = $(DiaFluxDialog.FLOWCHARTID);
	var flowcharts = parent.getElements('.Flowchart');
	for (var i = 0; i < flowcharts.length; i++) {
		flowcharts[i].parentNode.setStyle('display', 'none');
	}
}


/**
 * if the flowchart is not yet active, it will be added
 * to the active flowcharts else it will be removed because
 * it is finished
 */
DiaFluxDialog.sendAddActiveFlowchartRequest = function(name) {
	params = {
			action : 'DiaFluxDialogManageAction',
	        KWikiWeb : 'default_web',
	        name : name,
	        type : 'addActiveFlowchart'
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
 * removes the current active flowchart and returns the next
 */
DiaFluxDialog.sendGetNextActiveFlowchartRequest = function() {
	params = {
			action : 'DiaFluxDialogManageAction',
	        KWikiWeb : 'default_web',
	        type : 'getNextActiveFlowchart'
		};
	

	// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				DiaFluxDialog.activatePreviousFlowchart(this);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}


/**
 * activates the parent flowchart, after dead end
 * is reached
 */
DiaFluxDialog.activatePreviousFlowchart = function(request) {
	var text = request.responseText.split('#####');
	var nextFlowchart = text[0];
	
	DiaFluxDialog.Utils.createPath(text);
	
	DiaFluxDialog.hideFlowcharts();
	
	for (var i = 0; i < DiaFluxDialog.flowcharts.length; i++) {
		if (DiaFluxDialog.flowcharts[i].name === nextFlowchart) {
			theFlowchart = DiaFluxDialog.flowcharts[i];
			break;
		}
	}
	
	var next = $(DiaFluxDialog.FLOWCHARTID).getElement('#' + theFlowchart.dom.id);
	next.setStyle('display', 'block');

//	var test3 = $(theFlowchart.dom.id);

	
	var a = 1;
}


/**
 * creates the context menu, when nodes are clicked
 */
DiaFluxDialog.createContextMenu = function(event) {
	var contextMenu = theFlowchart.dom.getElement('div.dialogBox');
	if (contextMenu) {
		contextMenu.parentNode.removeChild(contextMenu);
	}
	
	var target = event.target;
	var htmlNode = DiaFluxDialog.Utils.findParentWithClass(target, 'Node');
	var flowNode = DiaFluxDialog.Utils.findFittingNode(htmlNode);
	
	var action = flowNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	var nodeId = flowNode.nodeModel.fcid;
	
	var question = action.infoObjectName;
	var answers = DiaFluxDialog.createAnswerPossibilites(infoObject);
	
	var height = htmlNode.offsetHeight;
	var width = htmlNode.offsetWidth > 110 ? htmlNode.offsetWidth : 110;
	var left = flowNode.nodeModel.position.left;
	var top = flowNode.nodeModel.position.top;
	
	
	// puts together the context menu with answer alternatives and
	// the node id hidden
	var hiddenId = '<div id="hiddenNodeId">' + nodeId + '</div>';
	var hiddenQuestion = '<div id="hiddenQuestion">' + question + '</div>';
	var dom = Builder.node('div', {
		className: 'dialogBox',
		style: 'position: absolute; left: ' + left + 'px; top: ' + top + 'px; min-height: ' + height + 'px; min-width: ' + width + 'px; background-color: #ffb;'
	});
	
	theFlowchart.dom.firstChild.appendChild(dom);
	dom.innerHTML = answers + hiddenId + hiddenQuestion;
	
	
	var closeLeft = $(dom).offsetWidth - 20;
	var closeButton = Builder.node('div', {
		id: 'closeButton', 
		onclick: 'DiaFluxDialog.removeContextMenu();',
		style: 'left: ' + closeLeft + 'px; top: 4px;'
	});
	dom.appendChild(closeButton);
	
	
	// increase size of the box and move the close button
	// for num questions
	if (infoObject.type === KBInfo.Question.TYPE_NUM) {
		var width = $(dom).getStyle('width').toInt() + 10;
		var left = $(closeButton).getStyle('left').toInt() + 10;
		$(dom).setStyle('width', width);
		$(closeButton).setStyle('left', left);
	}
	
	$('sendAnswer').addEvent('click', function(event) {
		DiaFluxDialog.fireNode(event);
	});
}

/**
 * creates the possible answers for the context menu
 */
DiaFluxDialog.createAnswerPossibilites = function(infoObject) {
	var html = '';
	var type = '';
	
	if (infoObject.getClassInstance() == KBInfo.Question) {
		switch (infoObject.getType()) {
		
			case KBInfo.Question.TYPE_BOOL:
			case KBInfo.Question.TYPE_OC:
				type = 'radio';
			case KBInfo.Question.TYPE_MC:
				if (type === '') type = 'checkbox';
				
				html += '<div>Select your answer</div>';
				html += '<table>';

				var options = infoObject.getOptions();
				for (var i=0; i<options.length; i++) {
					html += '<tr><td class="noPadding">';
					html += '<input name="answer" type="' + type + '" value="' + options[i] + '"></td><td class="noPadding">"' + infoObject.getName()+'" = "'+options[i]+'"</td></tr>';
				}
				html += '</table>';
				break;

			case KBInfo.Question.TYPE_NUM:
				var unit = infoObject.unit ? (' in ' + infoObject.unit) : '';
				html += '<div>Enter value';
				html += unit;
				html += ': <input id="numQuestion" type="text"></div>'
				break;
		}
	}
	
	html += '<button id="sendAnswer">Select</button>'
	
	return html;	
}

DiaFluxDialog.colorNode = function(flowNode) {
	flowNode.dom.addClass('nodeVisited');
}

DiaFluxDialog.resetNodeColor = function(flowNode) {
	flowNode.dom.removeClass('nodeVisited');
}

DiaFluxDialog.colorRule = function(flowRule) {
	flowRule.dom.addClass('ruleVisited');
}

DiaFluxDialog.resetRuleColor = function(flowRule) {
	flowRule.dom.removeClass('ruleVisited');
}

DiaFluxDialog.removeContextMenu = function() {
	var contextMenu = theFlowchart.dom.getElement('div.dialogBox');
	if (contextMenu) {
		contextMenu.parentNode.removeChild(contextMenu);
	}
}

/**
 * deactivates all rules sharing the same source node
 */
DiaFluxDialog.deactivateAllRulesExcept = function(flowRule) {
	var sourceNode = flowRule.sourceNode;
	
	// dont deactivate if the source node is the start node
	if (sourceNode.nodeModel.start && sourceNode.nodeModel.start === 'Start') {
		return;
	}
	
	// dont deactivate if the source node is mc
	var action = sourceNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	if (infoObject.type === 'mc') {
		return;
	}
	
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(sourceNode);
	for (var i = 0; i < outgoingRules.length; i++) {
		if (outgoingRules[i] !== flowRule) {
			DiaFluxDialog.deactivateRule(outgoingRules[i]);
		}
	}
}

DiaFluxDialog.increaseSupportForNode = function(flowNode) {
	if (flowNode.support) {
		flowNode.support++;
	} else {
		flowNode.support = 1;
	}
}


/**
 * decreases the support of a node, only if it already has support nodes with
 * support = 0 are either not yet started or just lost their support
 */
DiaFluxDialog.decreaseSupportForNode = function(flowNode) {
	if (flowNode.support && flowNode.support > 0) {
		flowNode.support--;
		if (flowNode.support === 0) {
			DiaFluxDialog.deactivateNode(flowNode);
		}
	}
}

DiaFluxDialog.Utils = {};

DiaFluxDialog.Utils.findFlowNodeById = function(fcid) {
	for (var i = 0; i < theFlowchart.nodes.length; i++) {
		if (theFlowchart.nodes[i].nodeModel.fcid === fcid) {
			return theFlowchart.nodes[i];
		}
	}
}

DiaFluxDialog.Utils.findOutgoingRules = function(flowNode) {
	var rules = theFlowchart.rules;
	var outgoingRules = [];
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].sourceNode === flowNode) {
			outgoingRules.push(rules[i]);
		}
	}
	return outgoingRules;
}

DiaFluxDialog.Utils.findIncomingRules = function(flowNode) {
	var rules = theFlowchart.rules;
	var outgoingRules = [];
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].targetNode === flowNode) {
			outgoingRules.push(rules[i]);
		}
	}
	return outgoingRules;
}

DiaFluxDialog.Utils.findFittingNode = function(htmlNode) {
	var nodes = theFlowchart.nodes;
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].dom === htmlNode) {
			return nodes[i];
		}
	}
	return null;
}

DiaFluxDialog.Utils.findFittingRule = function(htmlRule) {
	var rules = theFlowchart.rules;
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].dom === htmlRule) {
			return rules[i];
		}
	}
	return null;
}

DiaFluxDialog.Utils.findParentWithClass = function(node, clazz) {
	while (!node.hasClass(clazz)) {
		if (node === document.body) {
			return null;
		}
		node = node.parentNode;
	}
	return node;
}

DiaFluxDialog.Utils.isStartNode = function(flowNode) {
	if (flowNode.nodeModel && flowNode.nodeModel.start && flowNode.nodeModel.start === 'Start') {
		return true;
	}
	return false;
}

DiaFluxDialog.Utils.hasOutgoingRules = function(flowNode) {
	return (DiaFluxDialog.Utils.findOutgoingRules(flowNode).length !== 0);
}

DiaFluxDialog.Utils.createPath =  function(array) {
	var path = 'Path: ';
	if (array.length > 1) {
		for (var i = 1; i < array.length; i++) {
			if (array[i] !== '') {
				path += array[i] + ' - ';
			}
		}
		path = path.substring(0, path.length  - 3);
		
		$(DiaFluxDialog.FLOWCHARTPATHID).innerHTML = path;
	}
}


