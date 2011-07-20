DiaFluxDialog = {};

DiaFluxDialog.FLOWCHARTID = 'DiaFluxDialogFlowchart';
DiaFluxDialog.FLOWCHARTPATHID = 'DiaFluxDialogPath';
DiaFluxDialog.ENDOFDIALOG = 'ENDOFDIALOG';
DiaFluxDialog.flowcharts = [];
DiaFluxDialog.activeFlowchart = '';

/**
 * the entry function to the dialog "fires" startNodes and adds events to the
 * next nodes
 */
DiaFluxDialog.start = function() {
	var divs = $(DiaFluxDialog.FLOWCHARTID).getElements('.FlowchartGroup');
	DiaFluxDialog.activeFlowchart = divs[divs.length - 1].__flowchart;
	if (!DiaFluxDialog.flowcharts.contains(DiaFluxDialog.activeFlowchart)) {
		DiaFluxDialog.flowcharts.push(DiaFluxDialog.activeFlowchart);
	}
	DiaFluxDialog.fireStartNodes();
	
	var path = $('hiddenPath');
	if (!path) {
		return;
	}
	
	var value = path.value;
	var pairs = value.split('#####');
	
	for (var i = 0; i < pairs.length; i++) {
		if (pairs[i] !== "") {
			var temp = pairs[i].split('+++++');
			var question = temp[0];
			var answer = temp[1].split('+-+-+');
			DiaFluxDialog.activateRuleWithName(question, answer, false);		
		}
	}
}

DiaFluxDialog.activateRuleWithName = function(question, answer, sendRequest) {
	var node = DiaFluxDialog.Utils.findNodeWithName(question);
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(node);
	
	if (!node) {
		return;
	}
	
	var nodeType = DiaFluxDialog.Utils.getNodeType(node);
	if (nodeType === 'question') {
		var action = node.actionPane.action;
		var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName()); 
		var abs = infoObject.is_abstract;
		if (abs){
			DiaFluxDialog.getStatusOfQuestion(node, false);
			return;
		}			
	}

	for (var j = 0; j < outgoingRules.length; j++) {
		if (DiaFluxDialog.Utils.checkRuleCondition(outgoingRules[j], answer)) {
			DiaFluxDialog.activateRule(outgoingRules[j], sendRequest, true);
		}
	}
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
	var nodes = DiaFluxDialog.activeFlowchart.nodes;

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
DiaFluxDialog.activateRule = function(flowRule, sendRequest, activate) {
	if (!activate) {
		if (!flowRule.isActive) {
			flowRule.isActive = true;
		} else {
			DiaFluxDialog.deactivateRule(flowRule);
			return;
		}
	}
	var sourceNode = flowRule.sourceNode;
	var targetNode = flowRule.targetNode;
	
	DiaFluxDialog.deactivateAllRulesExcept(flowRule);
	DiaFluxDialog.colorRule(flowRule);
	DiaFluxDialog.colorNode(sourceNode);
	
	DiaFluxDialog.increaseSupportForNode(targetNode);
	
	
	// if start || !sendRequest -> no setsinglefinding request
	// is needed
	var start = DiaFluxDialog.Utils.isStartNode(sourceNode);
	if (!start && sendRequest) {
		DiaFluxDialog.prepareSetSingleFindingRequest(flowRule, sourceNode);
	}
	
	var targetNodeType = DiaFluxDialog.Utils.getNodeType(targetNode);
	// -> exit node or last node in a flowchart
	if (!DiaFluxDialog.Utils.hasOutgoingRules(targetNode)){
		if (targetNodeType !== 'subflow') {
			var outcome = '';
			if (targetNodeType === 'exit') {
				outcome = targetNode.nodeModel.exit;
			} else {
				outcome = targetNode.actionPane.action.infoObjectName;
			}
			DiaFluxDialog.colorNode(targetNode);
			DiaFluxDialog.sendGetNextActiveFlowchartRequest(outcome);
			return;
		}
	}
	
	if (targetNodeType === 'comment') {
		DiaFluxDialog.colorNode(targetNode);
		var outGoingRules = DiaFluxDialog.Utils.findOutgoingRules(targetNode);
		for (var i = 0; i < outGoingRules.length; i++) {
			DiaFluxDialog.activateRule(outGoingRules[i], false);
		}
		return;
	}
	
	var action = targetNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	
	// -> solution or subflowchart
	if (!infoObject.type) {
		DiaFluxDialog.colorNode(targetNode);
		
		// subflowchart
		if (infoObject.startNames && infoObject.exitNames) {
			DiaFluxDialog.sendGetFlowchartRequest(infoObject.name);
		} else {
			// solution
			var outGoingRules = DiaFluxDialog.Utils.findOutgoingRules(targetNode);
			for (var i = 0; i < outGoingRules.length; i++) {
				DiaFluxDialog.activateRule(outGoingRules[i], false);
			}
			return;
		}
	}

	// add the click handler
	DiaFluxDialog.addClickHandlerToNode(targetNode);	
	DiaFluxDialog.addClickHandlerToRules(targetNode);
}

DiaFluxDialog.prepareSetSingleFindingRequest = function(flowRule, flowNode) {
	var action = flowNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	var type = infoObject.type;
	var question = infoObject.name;
	if (type !== KBInfo.Question.TYPE_NUM) {	
		var selected = flowRule.guard.displayHTML;
		DiaFluxDialog.sendSetSingleFindingRequest(question, {ValueID : selected}, true);
	
	// TODO num werte generieren
	} else {
		var guard = flowRule.guard.displayHTML;
		var maxRange = infoObject.range;
		
		var min = 0;
		var max = 100;
		if (maxRange) {
			if (maxRange[0]) {
				min = parseInt(maxRange[0]);
			}
			if (maxRange[1]) {
				max = parseInt(maxRange[1]);
			}
		}
		var operator = DiaFluxDialog.Utils.extractOperator(flowRule);
		var number = DiaFluxDialog.Utils.extractNumber(flowRule);
		
		number[0] = parseInt(number[0]);
		number[1] = parseInt(number[1]);
		
		var r;
		if (operator === '>') {
			min = number + 0.1;
		} else if (operator === '<') {
			max = number - 0.1;
		} else if (operator === '>=') {
			min = number;
		} else if (operator === '<=') {
			max = number;
		} else if (operator === '!=') {
			r = min + Math.floor(Math.random() * ( max-min) * 10) / 10;
			while (r === number) {
				r = min + Math.floor(Math.random() * ( max-min) * 10) / 10;
			}
		} else if (operator === '=') {
			r = number;
		} else if (operator === 'inin') {
			min = number[0];
			max = number[1];
		} else if (operator === 'inex') {
			min = number[0];
			max = number[1] - 0.1;
		} else if (operator === 'exin') {
			min = number[0] + 0.1;
			max = number[1];
		} else if (operator === 'exex') {
			min = number[0] + 0.1;
			max = number[1] - 0.1;
		}
		
		if (!r) {
			r = min + Math.floor(Math.random() * ( max-min) * 10) / 10;
		}
		
		DiaFluxDialog.sendSetSingleFindingRequest(question, {ValueNum: r}, true);
	
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
	var contextMenu = DiaFluxDialog.activeFlowchart.dom.getElement('div.dialogBox');
		
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
	DiaFluxDialog.sendSetSingleFindingRequest(question, answer, true);
	contextMenu.parentNode.removeChild(contextMenu);
}

DiaFluxDialog.sendSetSingleFindingRequest = function(question, selected, savePath) {
	var master = $('hiddenMaster');
	if (master) {
		master = master.value;
	} else {
		return;
	}

	params = {
			action : 'DiaFluxDialogSetFindingAction',
	        KWikiWeb : 'default_web',
	        namespace : master,
	        ObjectID : question,
	        TermName : 'undefined',
	        save : savePath
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
	if (request.responseText === "") {
		return;
	}
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
	DiaFluxDialog.start();

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
DiaFluxDialog.sendGetNextActiveFlowchartRequest = function(outcome) {
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
 				DiaFluxDialog.activatePreviousFlowchart(this, outcome);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}


/**
 * activates the parent flowchart, after dead end
 * is reached, with the outcome of the dead end
 */
DiaFluxDialog.activatePreviousFlowchart = function(request, outcome) {
	if (request.responseText === DiaFluxDialog.ENDOFDIALOG) {
		return;
	}
	var text = request.responseText.split('#####');
	var nextFlowchart = text[0];
	var oldFlowchart = text[1];
	
	DiaFluxDialog.Utils.createPath(text);
	
	DiaFluxDialog.hideFlowcharts();
	
	for (var i = 0; i < DiaFluxDialog.flowcharts.length; i++) {
		if (DiaFluxDialog.flowcharts[i].name === nextFlowchart) {
			DiaFluxDialog.activeFlowchart = DiaFluxDialog.flowcharts[i];
			break;
		}
	}
	
	// haxx because of strange bug
	var next = $(DiaFluxDialog.FLOWCHARTID);
	var fc = $(DiaFluxDialog.activeFlowchart.dom.id);
	
	// if the flowchart is already on the page
	if (fc.parentNode !== next) {
		fc = next.getElement('#' + DiaFluxDialog.activeFlowchart.dom.id);
	}
	fc.parentNode.replaceChild(DiaFluxDialog.activeFlowchart.dom, fc);
	DiaFluxDialog.activeFlowchart.dom.setStyle('display', 'block');


	DiaFluxDialog.readdEvents();
	DiaFluxDialog.manageOutcomeOfSubFlowchart(oldFlowchart, outcome)
	var a = 1;
}

DiaFluxDialog.readdEvents = function(){
	var nodes = DiaFluxDialog.activeFlowchart.nodes;
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].support > 0) {
			DiaFluxDialog.activateNode(nodes[i]);
		}
	}
	
	var rules = DiaFluxDialog.activeFlowchart.rules;
	for (var i = 0; i < rules.length; i++) {
		if (rules[i].isActive > 0) {
			DiaFluxDialog.colorRule(rules[i]);
		}
	}
}

/**
 * searches the node of the subflowchart and either removes 
 * the events if there is no fitting outgoing edge or 
 * else changes nothing
 */
DiaFluxDialog.manageOutcomeOfSubFlowchart = function(nodeName, outcome) {
	var nodes = DiaFluxDialog.activeFlowchart.nodes;
	var subflowNode = '';
	for (var i = 0; i < nodes.length; i++) {
		if (DiaFluxDialog.Utils.getNodeType(nodes[i]) === 'subflow') {
			var action = nodes[i].actionPane.action;
			var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
			if (infoObject.startNames && infoObject.exitNames) {
				if (infoObject.name === nodeName) {
					subflowNode = nodes[i];
					break;
				}
			}
		}
	}
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(subflowNode);
	
	var correctRule = null;
	for (var i = 0; i < outgoingRules.length; i++) {
		if (outgoingRules[i].guard.displayHTML === outcome) {
			correctRule = outgoingRules[i];
		}
	}
	DiaFluxDialog.deactivateAllRulesExcept(correctRule);
	
	// if an outgoing rules was the outcome if the subflowchart
	// activate it
	if (correctRule) {
		DiaFluxDialog.activateRule(correctRule, false);
	}
	var a = 1;
}


/**
 * request required forward knowledge for abstract questions
 */
DiaFluxDialog.requestRequiredQuestions = function(question, flowNode) {
	var topic = KNOWWE.helper.gup('page')
	params = {
			action : 'DiaFluxDialogGetRequiredQuestions',
			question : question,
	        KWikiWeb : 'default_web',
	        type : 'getQuestions'
	};
	

	// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				DiaFluxDialog.askNeededQuestions(this, flowNode);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}


/**
 * asks the required forward knowledge question from
 * the response of the request
 */
DiaFluxDialog.askNeededQuestions = function(request, flowNode) {
	var text = request.responseText.split('#####');
	var boxes = [];
	
	for (var i = 0; i < text.length; i++) {
		if (text[i] != null && text[i] != "") {
			var parts = text[i].split('+-+-+');
			var name = parts[0];
			var type = parts[1];
			var answers = '';
			
			if (parts.length > 2) {
				answers = parts[2].split('+#+#+');
			}
			var htmlAnswers = DiaFluxDialog.createAnswerPossibilites(name, answers, type);
			boxes.push(DiaFluxDialog.createContextMenuDiv(flowNode, name, htmlAnswers));
		}
	}
	this.requiredInfo = boxes;
	DiaFluxDialog.askNextNeededQuestion(flowNode);
}


/**
 * asks the next required forward knowledge question
 */
DiaFluxDialog.askNextNeededQuestion = function(flowNode) {
	if (this.requiredInfo.length > 0) {
		var current = this.requiredInfo.pop();
		DiaFluxDialog.activeFlowchart.dom.firstChild.appendChild(current);
		DiaFluxDialog.appendContextMenuCloseButton(current);
		
		$('sendAnswer').addEvent('click', function(event) {
			DiaFluxDialog.setForwardKnowledge(flowNode);
		});
	} else {
		var question = flowNode.actionPane.action.expression;
		DiaFluxDialog.getStatusOfQuestion(flowNode, true);
	}
}

/**
 * returns the current value of a question
 */
DiaFluxDialog.getStatusOfQuestion = function(flowNode, sendRequest) {
	var question = flowNode.actionPane.action.expression;
	var topic = KNOWWE.helper.gup('page')
	params = {
			action : 'DiaFluxDialogGetRequiredQuestions',
			question : question,
	        KWikiWeb : 'default_web',
	        type : 'getStatus'
	};
	

	// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				DiaFluxDialog.activateAbstractQuestionEdge(flowNode, this, sendRequest);
    		}
        }
    };
    
    // send AJAX request
    new _KA( options ).send();
}

/**
 * checks the outgoing rules of an abstract node, and
 * activates those whose condition is satisfied
 */
DiaFluxDialog.activateAbstractQuestionEdge = function(flowNode, request, sendRequest) {
	var answer = request.responseText;
	var outgoingRules = DiaFluxDialog.Utils.findOutgoingRules(flowNode);
	
	for (var i = 0; i < outgoingRules.length; i++) {
		var checked = DiaFluxDialog.Utils.checkRuleCondition(outgoingRules[i]);
		DiaFluxDialog.activateRule(outgoingRules[i], sendRequest);
		var b = 1;
	}
	
	var a = 1;
}

/**
 * calls the SetSingleFindingAction and sets the required forward knowledge
 * from the input of the context menu
 */
DiaFluxDialog.setForwardKnowledge = function(flowNode) {
	var contextMenu = DiaFluxDialog.activeFlowchart.dom.getElement('div.dialogBox');
	var question = $('hiddenQuestion').textContent;
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
			}	
		}
		answer = {ValueID : selected};
	// numquestion
	} else {
		selected = $('numQuestion').value;
		answer = {ValueNum : selected};
	}
	DiaFluxDialog.sendSetSingleFindingRequest(question, answer, false);
	contextMenu.parentNode.removeChild(contextMenu);
	DiaFluxDialog.askNextNeededQuestion(flowNode);
}


/**
 * creates the context menu, when nodes are clicked
 */
DiaFluxDialog.createContextMenu = function(event) {
	var contextMenu = DiaFluxDialog.activeFlowchart.dom.getElement('div.dialogBox');
	if (contextMenu) {
		contextMenu.parentNode.removeChild(contextMenu);
	}
	
	var target = event.target;
	var htmlNode = DiaFluxDialog.Utils.findParentWithClass(target, 'Node');
	var flowNode = DiaFluxDialog.Utils.findFittingNode(htmlNode);
	
	var action = flowNode.actionPane.action;
	var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
	
	var question = action.infoObjectName;
	
	// -> abstract question -> no context menu
	if (infoObject.is_abstract) {
		DiaFluxDialog.requestRequiredQuestions(question, flowNode);
		return;
	}
	
	var answers = DiaFluxDialog.createAnswerPossibilites(infoObject.getName(), infoObject.getOptions(), infoObject.getType(), infoObject.unit);
	
	var contextMenuDom = DiaFluxDialog.createContextMenuDiv(flowNode, question, answers);
	DiaFluxDialog.activeFlowchart.dom.firstChild.appendChild(contextMenuDom);
	DiaFluxDialog.appendContextMenuCloseButton(contextMenuDom);
		
	$('sendAnswer').addEvent('click', function(event) {
		DiaFluxDialog.fireNode(event);
	});
}

/**
 * creates the context menu
 */
DiaFluxDialog.createContextMenuDiv = function(flowNode, question, answers) {
	var htmlNode = flowNode.dom;
	var nodeId = flowNode.nodeModel.fcid;
	
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
	

	dom.innerHTML = answers + hiddenId + hiddenQuestion;
	
	return dom;
}


/**
 * appends the close button
 */
DiaFluxDialog.appendContextMenuCloseButton = function(contextmenu) {
	var closeLeft = $(contextmenu).offsetWidth - 20;
	var closeButton = Builder.node('div', {
		id: 'closeButton', 
		onclick: 'DiaFluxDialog.removeContextMenu();',
		style: 'left: ' + closeLeft + 'px; top: 4px;'
	});
	contextmenu.appendChild(closeButton);
	return closeButton;
}



/**
 * creates the possible answers for the context menu
 */
DiaFluxDialog.createAnswerPossibilites = function(question, answers, questionType, unitType) {
	var html = '';
	var type = '';
	
	switch (questionType) {
	
		case KBInfo.Question.TYPE_BOOL:
		case KBInfo.Question.TYPE_OC:
			type = 'radio';
		case KBInfo.Question.TYPE_MC:
			if (type === '') type = 'checkbox';
			
			html += '<div>Select your answer</div>';
			html += '<table>';

			for (var i=0; i<answers.length; i++) {
				html += '<tr><td class="noPadding">';
				html += '<input name="answer" type="' + type + '" value="' + answers[i] + '"></td><td class="noPadding">"' + question +'" = "'+ answers[i] + '"</td></tr>';
			}
			html += '</table>';
			break;

		case KBInfo.Question.TYPE_NUM:
			var unit = unitType ? ('<br /> in ' + unitType) : '';
			html += '<div>Enter value for <br />' + question;
			html += unit;
			html += ': <input id="numQuestion" type="text"></div>'
			break;
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
	var contextMenu = DiaFluxDialog.activeFlowchart.dom.getElement('div.dialogBox');
	if (contextMenu) {
		contextMenu.parentNode.removeChild(contextMenu);
	}
}

/**
 * deactivates all rules sharing the same source node
 */
DiaFluxDialog.deactivateAllRulesExcept = function(flowRule) {
	if (!flowRule) {
		return;
	}
	var sourceNode = flowRule.sourceNode;
	
	// dont deactivate if the source node is the start node
	if (sourceNode.nodeModel.start && sourceNode.nodeModel.start === 'Start') {
		return;
	}
	
	var sourceNodeType = DiaFluxDialog.Utils.getNodeType(sourceNode);
	
	if (sourceNodeType === 'question') {
		// dont deactivate if the source node is mc
		var action = sourceNode.actionPane.action;
		var infoObject = KBInfo.lookupInfoObject(action.getInfoObjectName());
		if (infoObject.type === 'mc') {
			return;
		}
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

DiaFluxDialog.reset = function(sectionID) {
	var topic = KNOWWE.helper.gup('page')
	params = {
			action : 'DiaFluxDialogResetAction',
	        KWikiWeb : 'default_web'
	};
	

	// options for AJAX request
 	options = {
        url : KNOWWE.core.util.getURL( params ),
        response : {
    		action : 'none',
        	fn : function(){
 				if (sectionID) {
					KNOWWE.core.rerendercontent.updateNode(sectionID, topic, null);
					KNOWWE.core.util.updateProcessingState(-1); 
 				}
    		}
        }
    };
    
    // send AJAX request
 	if (sectionID) {
 		KNOWWE.core.util.updateProcessingState(1);
 	}
    new _KA( options ).send();
}





