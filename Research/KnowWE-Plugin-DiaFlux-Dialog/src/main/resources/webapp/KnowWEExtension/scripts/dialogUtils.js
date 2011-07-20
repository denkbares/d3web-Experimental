if (!DiaFluxDialog) {
	DiaFluxDialog = {};
}

DiaFluxDialog.Utils = {};

DiaFluxDialog.Utils.findFlowNodeById = function(fcid) {
	for (var i = 0; i < DiaFluxDialog.activeFlowchart.nodes.length; i++) {
		if (DiaFluxDialog.activeFlowchart.nodes[i].nodeModel.fcid === fcid) {
			return DiaFluxDialog.activeFlowchart.nodes[i];
		}
	}
}

DiaFluxDialog.Utils.findOutgoingRules = function(flowNode) {
	var rules = DiaFluxDialog.activeFlowchart.rules;
	var outgoingRules = [];
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].sourceNode === flowNode) {
			outgoingRules.push(rules[i]);
		}
	}
	return outgoingRules;
}

DiaFluxDialog.Utils.findIncomingRules = function(flowNode) {
	var rules = DiaFluxDialog.activeFlowchart.rules;
	var outgoingRules = [];
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].targetNode === flowNode) {
			outgoingRules.push(rules[i]);
		}
	}
	return outgoingRules;
}

DiaFluxDialog.Utils.findFittingNode = function(htmlNode) {
	var nodes = DiaFluxDialog.activeFlowchart.nodes;
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].dom === htmlNode) {
			return nodes[i];
		}
	}
	return null;
}

DiaFluxDialog.Utils.findFittingRule = function(htmlRule) {
	var rules = DiaFluxDialog.activeFlowchart.rules;
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

DiaFluxDialog.Utils.createPath = function(array) {
	var path = 'Path: ';
	if (array.length > 2) {
		for (var i = 2; i < array.length; i++) {
			if (array[i] !== '') {
				path += array[i] + ' - ';
			}
		}
		path = path.substring(0, path.length  - 3);
		
		$(DiaFluxDialog.FLOWCHARTPATHID).innerHTML = path;
	}
}

DiaFluxDialog.Utils.getNodeType = function(flowNode) {
	var nodeModel = flowNode.nodeModel;
	if (nodeModel.start) {
		return 'start';
	} else if (nodeModel.exit) {
		return 'exit';
	} else if (nodeModel.snapshot) {
		return 'snapshot';
	} else if (nodeModel.comment) {
		return 'comment';
	}
	
	var action = flowNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName()); 
	
	if (infoObject.startNames && infoObject.exitNames) {
		return 'subflow';
	} else if (infoObject.type) {
		return 'question';
	} 
	return 'solution';
}

DiaFluxDialog.Utils.findNodeWithName = function(name) {
	var nodes = DiaFluxDialog.activeFlowchart.nodes;
	for (var i = 0; i < nodes.length; i++) {
		var nodeType = DiaFluxDialog.Utils.getNodeType(nodes[i]);
		
		if (nodeType === 'start') {
			if (nodes[i].nodeModel.start === name) {
				return nodes[i];
			}
			continue;
		} else if (nodeType === 'exit') {
			if (nodes[i].nodeModel.exit === name) {
				return nodes[i];
			}
			continue;
		} else if (nodeType === 'snapshot') {
			continue;
		} 
		
		var action = nodes[i].actionPane.action;
		var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName()); 
		if (infoObject.name === name) {
			return nodes[i];
		}
//		if (nodeType === 'subflow') {
//			
//		} else if (nodeType === 'question') {
//			
//		} else if (nodeType === 'solution') {
//			
//		}
	}
}

/**
 * extracts the operator from an outgoing edge of a num question
 */
DiaFluxDialog.Utils.extractOperator = function(flowRule) {
	var guard = flowRule.guard.displayHTML;
	
	if (guard.startsWith('[') && guard.endsWith(']')) {
		return 'inin';
	} else if (guard.startsWith('[') && guard.endsWith('{')) {
		return 'inex';
	} else if (guard.startsWith(']') && guard.endsWith('[')) {
		return 'exex';
	} else if (guard.startsWith(']') && guard.endsWith(']')) {
		return 'exin';
	} else {
		var parts = guard.split(' ');

		if (parts[0] === '&gt;') {
			return '>';
		} else if (parts[0] === '&lt;') {
			return '<';
		} else if (parts[0] === '&ge;') {
			return '>=';
		} else if (parts[0] === '&le;') {
			return '<=';
		} else if (parts[0] === '=') {
			return '=';
		} else if (parts[0] === '&ne;') {
			return '!=';
		}
	}
}


/**
 * extracts the number from an outgoing edge of a num question
 */
DiaFluxDialog.Utils.extractNumber = function(flowRule) {
	var guard = flowRule.guard.displayHTML;
	if (guard.startsWith('[') || guard.startsWith(']')) {
		guard = guard.replace('[', '');
		guard = guard.replace(']', '');
		
		var parts = guard.split('..');
		return new Array(parts[0].trim(), parts[1].trim());
	} else {
		var parts = guard.split(' ');
		return parts[1];
	}
}

DiaFluxDialog.Utils.checkRuleCondition = function(flowRule, answer) {
	var node = flowRule.sourceNode;
	var nodeType = DiaFluxDialog.Utils.getNodeType(node);
	var questionType = '';
	if (nodeType === 'question') {
		var action = node.actionPane.action;
		var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName()); 
		questionType = infoObject.type;
	
	}
	// check if num edges must be activated
//	if (nodeType === 'question' && questionType === 'num') {
	
	// geht anders nicht, nur mit breakpoints
	if (questionType === 'num') {
		var operator = DiaFluxDialog.Utils.extractOperator(flowRule);
		var number = DiaFluxDialog.Utils.extractNumber(flowRule);
	
		if (operator === '>') {
			if (answer > number) {
				return true;
			}
		} else if (operator === '<') {
			if (answer < number) {
				return true;
			}
		} else if (operator === '>=') {
			if (answer >= number) {
				return true;
			}
		} else if (operator === '<=') {
			if (answer <= number) {
				return true;
			}
		} else if (operator === '=') {
			if (answer == number) {
				return true;
			}
		} else if (operator === '!=') {
			if (answer != number) {
				return true;
			}
		} else if (operator === 'inin') {
			if (answer >= number[0] && answer <= number[1]) {
				return true;
			}
		} else if (operator === 'inex') {
			if (answer >= number[0] && answer < number[1]) {
				return true;
			}
		} else if (operator === 'exin') {
			if (answer > number[0] && answer <= number[1]) {
				return true;
			}
		} else if (operator === 'exex') {
			if (answer > number[0] && answer < number[1]) {
				return true;
			}
		}
		
	// not num questions
	} else if (answer.length > 0 && flowRule.guard.displayHTML === answer[0]) {
		return true;
	}
	return false;
}
