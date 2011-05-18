
CCEvents.addClassListener('keydown', 'Rule', 
	function(event) {
		// [ctrl] + v
		if (event.keyCode == 86 && event.ctrlKey) {
			Refactoring.insertFromClipboard(this.__rule);
		}
	}
);



/**
* Gets inbound Rules for a node.
* @param node node
* @return inbound rules for node
*/
Flowchart.prototype.findInboundRulesForNode = function(node) {
	// TODO: shall be optimized by build an hashtable for each node!!!
	var result = [];
	for (var i=0; i<this.rules.length; i++) {
		var rule = this.rules[i];
		if (rule.targetNode == node) {
			result.push(rule);	
		} 
	}
	return result;
}

/**
* Gets outbound Rules for a node.
* @param node node
* @return outbound rules for node
*/
Flowchart.prototype.findOutboundRulesForNode = function(node) {
	// TODO: shall be optimized by build an hashtable for each node!!!
	var result = [];
	for (var i=0; i<this.rules.length; i++) {
		var rule = this.rules[i];
		if (rule.sourceNode == node) {
			result.push(rule);	
		} 
	}
	return result;
}

/**
* Gets all rules which are connected to the selected nodes.
* @param selection optional selection (otherwise the flowchart selection is used)
* @return rules of selected notes
*/
Flowchart.prototype.findRulesForSelection = function(selection) {
	
	var selectedRules = [];
	
	// get all rules of selected nodes
	var selectedNodes = selection || this.getSelection();
	for (var i = 0; i < selectedNodes.length; i++) {
		
		var curRules = this.findRulesForNode(selectedNodes[i]);
		for (var j = 0; j < curRules.length; j++) {
			selectedRules.push(curRules[j]); 
		}
	}
	
	return selectedRules;
}

/**
* Gets only the inbound rules of the selected nodes.
* @param selection optional selection (otherwise the flowchart selection is used)
* @return inbound rules of selected notes
*/
Flowchart.prototype.findInboundRulesForSelection = function(selection) {

	var result = [];
	var selectedNodes = this.getSelection();
	var selectedRules = this.findRulesForSelection(selection);
		
	// find all rules which don't have their source node in the
	// selection (these are the inbound ones)
	for (var i = 0; i < selectedRules.length; i++) {
		
		var foundSourceNode = false;
		for (var j = 0; j < selectedNodes.length; j++) {
			if (selectedRules[i].sourceNode == selectedNodes[j]) {
				foundSourceNode = true;
			}
		}
		
		if (!foundSourceNode) {
			result.push(selectedRules[i]);
		}
	}

	return result;
}

/**
* Gets only the outbound rules of the selected nodes.
* @param selection optional selection (otherwise the flowchart selection is used)
* @return outbound rules of selected notes
*/
Flowchart.prototype.findOutboundRulesForSelection = function(selection) {

	var result = [];
	var selectedNodes = this.getSelection();
	var selectedRules = this.findRulesForSelection(selection);
		
	// find all rules which don't have their target node in the
	// selection (these are the outbound ones)
	for (var i = 0; i < selectedRules.length; i++) {
		
		var foundSourceNode = false;
		for (var j = 0; j < selectedNodes.length; j++) {
			if (selectedRules[i].targetNode == selectedNodes[j]) {
				foundSourceNode = true;
			}
		}
		
		if (!foundSourceNode) {
			result.push(selectedRules[i]);
		}
	}

	return result;
}


/**
 * Pseudo class for refactoring functions, abused as name space.
 * @author Ralf Oechsner
 */
function Refactoring() {
	
}


/**
 * Selects all outbound rules of a selection.
 * @param selection selected nodes
 */
Refactoring.selectOutbound = function(selection) {
	
	var selRules = selection.flowchart.findOutboundRulesForSelection();
	for (var i = 0; i < selRules.length; i++) {
		selection.flowchart.setSelection(selRules[i], true, false);
	}
}

/**
 * Selects all inbound rules of a selection.
 * @param selection selected nodes
 */
Refactoring.selectInbound = function(selection) {

	var selRules = selection.flowchart.findInboundRulesForSelection();
	for (var i = 0; i < selRules.length; i++) {
		selection.flowchart.setSelection(selRules[i], true, false);
	}
}



/**
 * Insert a new node between two nodes which are connected with a given rule.
 * @param rule rule which connects nodes
 * @param type node type {decision, comment, snapshot}
 */
Refactoring.insertNode = function(rule, x, y, type, expression) {
	
	// this refactoring is only possible for one 
	if (rule.length > 1) {
		alert("More than one rule selected!");
		return;
	}
	
	var nodeModel;
	
	if (type == "decision")
		nodeModel = {action: { markup: 'KnOffice', expression: expression || ''}};
	else if (type == "comment")
		nodeModel = {comment: 'Comment'}
	else if (type == "snapshot")
		nodeModel = {snapshot: 'Snapshot'}
	else
		return;

	// create a new node and connect the source and target nodes
	// of the original rule to the new node and finally delete
	// the old rule
	
	nodeModel.position = {left: x - rule.flowchart.getLeft(), top: y - rule.flowchart.getTop()};
	var node = new Node(rule.flowchart, nodeModel);
	
	var inRule = new Rule(null, rule.getSourceNode(), rule.getGuard(), node);
	
	var outRule = new Rule(null, node, null, rule.getTargetNode());
	
	rule.destroy();
	
	// ask the user to edit the node
	node.select();
	node.edit();
}

/**
 * Insert a content from clipboard between two nodes which are connected with a given rule.
 * @param rule rule which connects nodes
 */
Refactoring.insertFromClipboard = function(rule, x, y) {
	
	if (CCClipboard.fromClipboard() == null)
		return;
	
	// first the clipboard is parsed into node and rule
	// objects. this code is copied from the addFromXML() method
	// of the flowchart object
	var pasteOptions = {
			flowchart: rule.flowchart,
			idMap: {},
			allIDs: [],
			translate: {left: 20, top: 20}
		};
		pasteOptions.createID = function(id) {
			if (rule.flowchart.findObject(id)) {
				var newID = this.flowchart.createID();
				this.idMap[id] = newID;
				this.allIDs.push(newID);
				return newID;
			}
			else {
				this.allIDs.push(id);
				return id;
			}
		}.bind(pasteOptions);
		pasteOptions.getID = function(id) {
			if(this.idMap[id]) id = this.idMap[id];
			return id;
		}.bind(pasteOptions);
	
	var xmlDom = CCClipboard.fromClipboard().parseXML();
	
	// nodes
	var nodes = [];
	var nodeDoms = xmlDom.getElementsByTagName('node');
	for (var i=0; i<nodeDoms.length; i++) {
		nodes.push(Node.createFromXML(rule.flowchart, nodeDoms[i], pasteOptions));
	}
	
	// rules
	var rules = [];
	var ruleDoms = xmlDom.getElementsByTagName('edge');
	for (var i=0; i<ruleDoms.length; i++) {
		rules.push(Rule.createFromXML(rule.flowchart, ruleDoms[i], pasteOptions));
	}
	
	var sel = [];
	for (var i=0; i<pasteOptions.allIDs.length; i++) {
		var item = rule.flowchart.findObject(pasteOptions.allIDs[i]);
		sel.push(item);
	}
	rule.flowchart.setSelection(sel);

	// now an entry node has to be determined
	var inbound = rule.flowchart.findInboundRulesForSelection(sel);
		
	if (inbound.length > 0) {
		var entry = inbound[0].getTargetNode();

		// reconnect inbound rules
		for (var i = 0; i < rules.length; i++) {
			if (rules[i].getTargetNode() == entry) {
				new Rule(pasteOptions.allIDs[i], rule.getSourceNode(), rule.getGuard(), entry);
				rules[i].destroy();
			}
		}
	}
	else {
		// when there are no inbound edges then connect all entry nodes
		// to the source node of the rule (keeping its guards)
		var entryNodes = [];
		for (var i = 0; i < nodes.length; i++) {
			if (rule.flowchart.findInboundRulesForNode(nodes[i]).length == 0) {
				entryNodes.push(nodes[i])
			}
		}
		
		for (var i = 0; i < entryNodes.length; i++) {
			new Rule(null, rule.getSourceNode(), rule.getGuard(), entryNodes[i]);
		}
	}
	
	// reconnect outbound rules
	var outbound = rule.flowchart.findOutboundRulesForSelection(sel);
	
	if (outbound.length > 0) {
		var exit = outbound[0].getSourceNode();

		for (var i = 0; i < rules.length; i++) {
			if (rules[i].getSourceNode() == exit) {
				new Rule(pasteOptions.allIDs[i], exit, rules[i].getGuard(), rule.getTargetNode());
				rules[i].destroy();
			}
		}
	}
	else {
		// when there are no outbound edges then connect all entry nodes
		// to the target node of the rule
		var exitNodes = [];
		for (var i = 0; i < nodes.length; i++) {
			if (rule.flowchart.findOutboundRulesForNode(nodes[i]).length == 0) {
				exitNodes.push(nodes[i])
			}
		}
		
		for (var i = 0; i < exitNodes.length; i++) {
			new Rule(null, exitNodes[i], null, rule.getTargetNode());
		}
	}
	
	// select inserted objects
	rule.flowchart.setSelection(sel);
	
	// finally delete the original rule
	rule.destroy();
}

/**
 * Remove a node between two nodes which are connected only via the rules 
 * of the given node.
 * @param node node whose rules connect two other nodes
 */
Refactoring.removeNode = function(node) {
	
	// nodes without inbound rules shalt be deleted :D
	var inbound = node.flowchart.findInboundRulesForSelection();
	if (inbound.length < 1) {
		// in this case theres no entry node to connect so just delete the selection
		node.flowchart.trashSelection();
		return;
	}
	
	// All outbound rules _must_ have their target in the same node
	// otherwise we have several entry nodes an this refactoring is not possible
	// and a warning is given to the user.
	// So if the condition is true we keep only one rule which is sufficient.
	var outbound = node.flowchart.findOutboundRulesForSelection();
	if (outbound.length > 0) {
		target = outbound[0].getTargetNode();
		for (var i = 0; i < outbound.length; i++) {
			if (outbound[i].getTargetNode() != target) {
				alert("Too many exit nodes! Restructuring can only be applied if the selection has only one outbound edge.");
				return;
			}
		}
	}
	else {
		// when there are no outbound rules then only delete the selection
		node.flowchart.trashSelection();
		return;
	}
	
	node.flowchart.trashSelection();
	
	// reconnect entry nodes and exit node
	for (var i = 0; i < inbound.length; i++) {
		var rule = new Rule(null, inbound[i].getSourceNode(), inbound[i].getGuard(),
				outbound[0].getTargetNode());
		rule.select(true);
	}
}

/**
 * Creates a new flowchart from the selection and inserts a decision node
 * which calls the flowchart at the selection position.
 * @param selection content of module
 */
Refactoring.extractModule = function(selection) {
	
	// first save the inbound and outbound rules
	var inbound = selection.flowchart.findInboundRulesForSelection();
	var outbound = selection.flowchart.findOutboundRulesForSelection();
	
	// ask user for module name
	var modID = Math.floor((2000-999)*Math.random()) + 1000; // generate number between 1000 and 2000
	var modName = prompt("Please enter module name:", "module " + modID);
	if (modName == null) {
		return;
	}
	
	// create new flowchart for module
	var modID = Math.floor((2000-999)*Math.random()) + 1000; // generate number between 1000 and 2000
	var module = new Flowchart('contents', "module" + modID, 650, 400, 1);
	module.name = modName;
	module.icon = "";
	module.autostart = false;
	
	var sel = selection.flowchart.selection.clone();

	// add all interconnecting rules to clipboard but not
	// inbound and outbound ones
	for (var i=0; i<selection.flowchart.rules.length; i++) {
		var rule = selection.flowchart.rules[i];
		if (sel.contains(rule)) continue;
		if (sel.contains(rule.getSourceNode()) && sel.contains(rule.getTargetNode())) {
			sel.push(rule);
		}
	}
	
	// convert to xml
	var result = '';
	for (var i=0; i<sel.length; i++) {
		result += sel[i].toXML();
	}
	
	var xmlDom = result.parseXML();
	
	module.addFromXML(xmlDom, 0, 0);
	
	// create start nodes for inbound rules and reconnect the rules
	var startNodes = [];
	var startCounter = 1;
	for (var i = 0; i < inbound.length; i++) {
		var x = inbound[i].getSourceNode().getLeft();
		var y = inbound[i].getSourceNode().getTop();
		var nodeModel = {start: 'Start' + startCounter++}
		nodeModel.position = {left: x, top: y};
		var newSource = new Node(module, nodeModel);
		startNodes.push(newSource);
		rule = new Rule(null, newSource, null, module.findNode(inbound[i].getTargetNode().nodeModel.fcid));
	}
	
	// create exit nodes for outbound rules and reconnect the rules
	var exitNodes = [];
	var exitCounter = 1;
	for (var i = 0; i < outbound.length; i++) {
		var x = outbound[i].getTargetNode().getLeft();
		var y = outbound[i].getTargetNode().getTop();
		var nodeModel = {exit: 'Exit' + exitCounter++}
		nodeModel.position = {left: x, top: y};
		var newSource = new Node(module, nodeModel);
		exitNodes.push(newSource);
		rule = new Rule(null, module.findNode(outbound[i].getSourceNode().nodeModel.fcid), outbound[i].getGuard(), newSource);
	}
	
	// preview doesn't work yet
	// will be fixed later when preview function is renewed
	module.setSelection(null, false, false);
	var modXML = module.toXML(true);
	
	// the module is saved in the AJAX request which gets the package name
	// because the request is done asynchronously but saving doesn't concern
	// the replacing of the module
	Refactoring.saveModule(modXML);
	
	// update kbinfo cache to prevent an error of not finding the module node
	var kbinfoXML = "<kbinfo>";
	kbinfoXML += "<flowchart id=\"" + topic + ".." + topic + "_KB/" + "module" + modID + "\" name=\"" + module.name + "\">\r\n";
	for (var i = 0; i < startNodes.length; i++) {
		kbinfoXML += '<start>' + startNodes[i].nodeModel.start + '</start>\r\n';
	}
	for (var i = 0; i < exitNodes.length; i++) {
		kbinfoXML += '<exit>' + exitNodes[i].nodeModel.exit + '</exit>\r\n';
	}
	kbinfoXML += "</flowchart>\r\n";
	kbinfoXML += "</kbinfo>";
	KBInfo._updateCache(kbinfoXML.parseXML());
	
	// create module node
	var model = {
			position: {left: inbound[0].getTargetNode().getLeft(), top: inbound[0].getTargetNode().getTop()},
			action: {markup: 'KnOffice', expression: 'CALL[' + module.name + '(' + startNodes[0].nodeModel.start + ')]' }
	};
	var moduleNode = new Node(selection.flowchart, model);
	moduleNode.select();
	
	// reconnect inbound rules to module node
	for (var i = 0; i < inbound.length; i++) {
		var inRule = new Rule(null, inbound[i].getSourceNode(), inbound[i].getGuard(), moduleNode);
		inRule.select(true);
		inbound[i].destroy();
	}
	
	// reconnect outbound rules to module node
	for (var i = 0; i < outbound.length; i++) {

		var newGuard = new Guard('KnOffice', 'IS_ACTIVE[' + module.name + '(' + exitNodes[i].nodeModel.exit + ')]', exitNodes[i].nodeModel.exit)
		
		var outRule = new Rule(null, moduleNode, newGuard, outbound[i].getTargetNode());
		outRule.select(true);
		outbound[i].destroy();
	}
	
	// finally trash original selection (can't use trashSelection()
	// because selection has changed)
	for (var i=0; i<sel.length; i++) {
		var item = sel[i];
		item.destroy();
	}
}

/**
 * Renames an element and automatically changes the terminology.
 * @param selection element to be renamed
 */
Refactoring.rename = function(node) {
	
	// get name of element
	var oldName = "";
	if (node.nodeModel.action) {
		var tmpAction = new Action(node.nodeModel.action.markup, node.nodeModel.action.expression);
		if (tmpAction.isFlowCall()) {
			alert("Renaming not possible for this node type.");
			return;
		}
		oldName = tmpAction.getInfoObjectName();
	}
	else  if (node.nodeModel.start) {
		oldName = node.nodeModel.start;
	}
	else if (node.nodeModel.exit) {
		oldName = node.nodeModel.exit;
	}
	else {
		alert("Renaming not possible for this node type.")
		return;
	}
	
	// get new name
	var newName = prompt("Please enter new name:", oldName);
	if (newName == null) {
		return;
	}
	if (node.nodeModel.action) {
		
		// get old InfoObject
		var oldAction = new Action(node.nodeModel.action.markup, node.nodeModel.action.expression);
		var oldInfoObject = KBInfo.lookupInfoObject(oldAction.getInfoObjectName());
				
		// update node
		node.nodeModel.action.expression = newName;

		// and InfoObject in the cache
		oldInfoObject.xmlDom.setAttribute('name', newName);
		oldInfoObject.name = newName;
		KBInfo._addToChache(oldInfoObject); // typo "Chache"
		
		// update guards
		var rules = node.flowchart.findRulesForNode(node);
		for (var i = 0; i < rules.length; i++) {
			var guard = rules[i].getGuard();
			if (guard != null) {
				guard.conditionString = guard.conditionString.replace(oldName, newName);
			}
		}

		// send renaming request to backend
		Refactoring.renameTerm(node, oldName, newName);
	}
	else  if (node.nodeModel.start) {
		oldName = node.nodeModel.start = newName;
		node.setVisible(false);
		node.setVisible(true);
	}
	else if (node.nodeModel.exit) {
		oldName = node.nodeModel.exit = newName;
		node.setVisible(false);
		node.setVisible(true);
	}
	else {
		alert("Renaming not possible for this type of node.")
	}
}

/**
 * Action for saving a module. Saves XML representation of a module in to
 * the Wiki article of its parent flowchart and puts it into the same package
 * as the parent flowchart.
 * @param xml XML representation of the module
 */
Refactoring.saveModule = function(xml) {
	
	var url = "KnowWE.jsp";
	new Ajax.Request(url, {
		method: 'get',
		parameters: {
			action: 'GetPackageName',
			KWiki_Topic: topic,			// article
			TargetNamespace: nodeID,	// KDOM nodeID
		},
		onSuccess: function(transport) {
			xml += '\r\n';
			var packageName = transport.responseText;
			xml += '@package: ' + packageName + '\r\n';
			Refactoring.newFlowchartText(xml);
		},
		onFailure: function() {
			CCMessage.warn(
				'AJAX Verbindungs-Fehler', 
				'Die Aenderungen konnten nicht gespeichert werden.');
		},
		onException: function(transport, exception) {
			CCMessage.warn(
				'AJAX interner Fehler, Aenderungen eventuell verloren',
				exception
				);
		}
	});
}

/**
 * Action for saving a module. Saves XML representation of a module in to
 * the Wiki article of its parent flowchart.
 * @param xml XML representation of the module
 */
Refactoring.newFlowchartText = function(xml) {
	
	var url = "KnowWE.jsp";
	new Ajax.Request(url, {
		method: 'post',
		parameters: {
			action: 'SaveFlowchartAction',
			KWiki_Topic: topic,			// article
			KWikitext: xml				// content
		},
		onSuccess: function(transport) {
			if (window.opener) window.opener.location.reload();
		},
		onFailure: function() {
			CCMessage.warn(
				'AJAX Verbindungs-Fehler', 
				'Die Aenderungen konnten nicht gespeichert werden.');
		},
		onException: function(transport, exception) {
			CCMessage.warn(
				'AJAX interner Fehler, Aenderungen eventuell verloren',
				exception
				);
		}
	});	
}

/**
 * Action for saving a module. Saves XML representation of a module in to
 * the Wiki article of its parent flowchart.
 * @param xml XML representation of the module
 */
Refactoring.renameTerm = function(node, term, replacement) {
	
	var url = "KnowWE.jsp";
	new Ajax.Request(url, {
		method: 'post',
		parameters: {
			action: 'TermRenamingAction',
			termname: term,
			termreplacement: replacement
		},
		onSuccess: function(transport) {
			node.setVisible(false);
			node.setVisible(true);
			if (window.opener) window.opener.location.reload();
		},
		onFailure: function() {
			CCMessage.warn(
				'AJAX Verbindungs-Fehler', 
				'Die Aenderungen konnten nicht durchgefuehrt werden.');
		},
		onException: function(transport, exception) {
			CCMessage.warn(
				'AJAX interner Fehler, Aenderungen eventuell verloren',
				exception
				);
		}
	});	
};



//createMenu when dom is ready
document.observe("dom:loaded", function() {

	contextMenuRule.addItem("Insert from clipboard", "FlowEditor.insertFromClipboard(contextMenuRule.getSelection(), contextMenuRule.getLeft(), contextMenuRule.getTop());", Flowchart.imagePath + "contextmenu/paste.png");
	
	
	contextMenuNode.addSeparator("Rename", "Refactoring.rename(contextMenuNode.getSelection());", Flowchart.imagePath + "contextmenu/rename.png");	
	contextMenuNode.addItem("Extract Module", "Refactoring.extractModule(contextMenuNode.getSelection());", Flowchart.imagePath + "contextmenu/extractmodule.png");	
	contextMenuNode.addItem("Remove Node", "Refactoring.removeNode(contextMenuNode.getSelection());", Flowchart.imagePath + "contextmenu/remove.png");

	
}
