
DiaFluxDialog = {};

// the entry function to the dialog
// "fires" startNodes and adds events to the next nodes
DiaFluxDialog.addEvents = function() {
	DiaFluxDialog.fireStartNodes();
}

// adds the onclick event to all follow up nodes 
// (and their rules) of a node
DiaFluxDialog.addEventsToFollowUp = function(previousNode) {
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(previousNode);
	
	for (var i = 0; i < outgoingRules.length; i++) {
		var currentNode = outgoingRules[i].targetAnchor.node;
		currentNode.dom.removeEvents('click');
		currentNode.dom.addEvent('click', function(event){
			DiaFluxDialog.createContextMenu(event);
		});
		
		var newOutgoingRules = DiaFluxDialog.Utils.findOutgoingRules(currentNode);
		for (var j = 0; j < newOutgoingRules.length; j++) {
			newOutgoingRules[j].dom.removeEvents('click');
			newOutgoingRules[j].dom.addEvent('click', function(event) {
				DiaFluxDialog.clickRule(event);
			});
		}
	}
}


// fires all start nodes and returns them in an array
DiaFluxDialog.fireStartNodes = function() {
	var nodes = theFlowchart.nodes;
	
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].nodeModel.start && nodes[i].nodeModel.start === 'Start') {
			DiaFluxDialog.colorNode(nodes[i]);

			
			// color outgoing rules
			var rules = DiaFluxDialog.Utils.findOutgoingRules(nodes[i]);
			for (var j = 0; j < rules.length; j++) {
				DiaFluxDialog.activateRule(rules[j]);
			}
		}
	}
}

DiaFluxDialog.clickRule = function(event) {
	var target = event.target;
	var htmlRule = DiaFluxDialog.Utils.findParentWithClass(target, 'Rule');
	var flowRule = DiaFluxDialog.Utils.findFittingRule(htmlRule);
	DiaFluxDialog.activateRule(flowRule);

}

// activates a rule, e.g. colors the rule and adds the event to
// its target node and the outgoing rules
DiaFluxDialog.activateRule = function(flowRule) {
	if (!flowRule.isActive) {
		flowRule.isActive = true;
	} else {
		DiaFluxDialog.deactivateRule(flowRule);
		return;
	}
	DiaFluxDialog.deactivateAllRulesExcept(flowRule);
	DiaFluxDialog.colorRule(flowRule);
	DiaFluxDialog.colorNode(flowRule.sourceNode);
	
	var targetNode = flowRule.targetNode;
	DiaFluxDialog.increaseSupportForNode(targetNode);
	
	
	var action = targetNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	
	// -> solution -> color target node
	if (!infoObject.type) {
		DiaFluxDialog.colorNode(targetNode);
		return;
	}
	
	targetNode.dom.removeEvents('click');
	targetNode.dom.addEvent('click', function(event){
		DiaFluxDialog.createContextMenu(event);
	});
	
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(targetNode);
	for (var i = 0; i < outgoingRules.length; i++) {
		outgoingRules[i].dom.removeEvents('click');
		outgoingRules[i].dom.addEvent('click', function(event) {
			DiaFluxDialog.clickRule(event);
		});
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

//fires a node, i.e. sets the css class 
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
					DiaFluxDialog.activateRule(outgoingRules[j]);
				}
			}
				
		}
			
			
	}
	var incomingRules = DiaFluxDialog.Utils.findIncomingRules(flowNode);
	for (var i = 0; i < incomingRules.length; i++) {
		if (!incomingRules[i].isActive) {
			DiaFluxDialog.activateRule(incomingRules[i]);
		}
	}
	
	
	DiaFluxDialog.activateNode(flowNode);
	contextMenu.parentNode.removeChild(contextMenu);
}


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
	
	
	$('sendAnswer').addEvent('click', function(event) {
		DiaFluxDialog.fireNode(event);
	});
}


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

// deactivates all rules sharing the same source node
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


// decreases the support of a node, only if it already has support
// nodes with support = 0 are either not yet started or just lost
// their support
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
