
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
		currentNode.dom.addEvent('click', function(event){
			DiaFluxDialog.createContextMenu(event);
		});
		
		var newOutgoingRules = DiaFluxDialog.Utils.findOutgoingRules(currentNode);
		for (var j = 0; j < newOutgoingRules.length; j++) {
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
	DiaFluxDialog.deactivateAllRules(flowRule);
	DiaFluxDialog.colorRule(flowRule);
	DiaFluxDialog.colorNode(flowRule.sourceNode);
	
	var targetNode = flowRule.targetNode;
	targetNode.dom.addEvent('click', function(event){
		DiaFluxDialog.createContextMenu(event);
	});
	
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(targetNode);
	for (var i = 0; i < outgoingRules.length; i++) {
		outgoingRules[i].dom.addEvent('click', function(event) {
			DiaFluxDialog.clickRule(event);
		});
	}
}

DiaFluxDialog.createContextMenu = function(event) {
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

//fires a node, i.e. sets the css class 
DiaFluxDialog.fireNode = function(event) {
	var target = event.target;	
	var hiddenNode = $('hiddenNodeId')	
	var flowNode = DiaFluxDialog.Utils.findFlowNodeById(hiddenNode.firstChild.textContent);

	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(flowNode);
	for (var i = 0; i < outgoingRules.length; i++) {
		DiaFluxDialog.resetRuleColor(outgoingRules[i]);
	}	

	DiaFluxDialog.colorNode(flowNode);
	
	var question = $('hiddenQuestion').innerHTML;
	var contextMenu = theFlowchart.dom.getElement('div.dialogBox');
	
	
	
	// only if the context menu was called
	if (contextMenu) {
		
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
						DiaFluxDialog.colorRule(outgoingRules[j]);
					}
				}
				
			}
			
			
		}
		contextMenu.parentNode.removeChild(contextMenu);
	}
	
	DiaFluxDialog.addEventsToFollowUp(flowNode);
}

DiaFluxDialog.colorNode = function(flowNode) {
	flowNode.dom.addClass('nodeVisited');
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
DiaFluxDialog.deactivateAllRules = function(flowRule) {
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
		DiaFluxDialog.resetRuleColor(outgoingRules[i]);
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
		if (rules[i].sourceAnchor.node === flowNode) {
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
