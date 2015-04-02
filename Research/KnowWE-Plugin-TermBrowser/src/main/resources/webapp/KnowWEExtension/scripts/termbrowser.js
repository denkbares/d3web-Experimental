if (typeof KNOWWE == "undefined" || !KNOWWE) {
	/**
	 * The KNOWWE global namespace object. If KNOWWE is already defined, the
	 * existing KNOWWE object will not be overwritten so that defined namespaces
	 * are preserved.
	 */
	var KNOWWE = {};
}
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
	/**
	 * The KNOWWE.plugin global namespace object. If KNOWWE.plugin is already
	 * defined, the existing KNOWWE.plugin object will not be overwritten so
	 * that defined namespaces are preserved.
	 */
	KNOWWE.plugin = {};
}
/**
 * Namespace: KNOWWE.plugin.semanticautocompletion The KNOWWE plugin semantic
 * autocompletion namespace. Initialized empty to ensure existence.
 */
KNOWWE.plugin.termbrowser;

jq$(document).ready(function() {
	jq$(".termline").each(activateDraggables);
	jq$(".dragMarkupTerm").each(activateDraggables);


	jq$(".dropTargetMarkup").each(function() {
		initDropableMarkupSection(jq$(this));
	});

	jq$(".termlist").each(function() {
		initDropableTermList(jq$(this));
	});

	initAllIconHovers();

	initAllBrowserActionEvents();

	initCollapseTermBrowser();

	initSemanticAutocompletionSlot();


    // init draggable sparql result nodes after async rendering
    KNOWWE.helper.observer.subscribe("afterRerender", function () {
        jq$(".dragSparqlResultNode").each(activateDraggables);
    });

	KNOWWE.plugin.termbrowser = {};

});

/*
 * init semantic autocompletion slot
 */
function initSemanticAutocompletionSlot() {
	window.setTimeout(function() {
		jq$(".termbrowserframe").each(function() {
            KNOWWE.plugin.semanticautocompletion.actions.init();
			var termbrowserframeElement = jq$(this);
			var inputElement = termbrowserframeElement.find(".semanticautocompletion");
			var textboxListObject = KNOWWE.plugin.semanticautocompletion.actions.getTextboxListInstance(inputElement);
			if (textboxListObject) {
				textboxListObject.addEvent('bitAdd', completionItemSelected);
			}
		});
	});
}

function completionItemSelected(bitObject) {
	var value = bitObject.getValue();
	var firstList = value[0];
	if (firstList) {
		var jsonObject = jq$.parseJSON(firstList);
        var uri = jsonObject['concept'];
        var type = jsonObject['conceptClass'];
		var label = value[1];
		if(label.contains('(')) {
			label = label.substring(0, label.lastIndexOf('('));
		}
        sendTermBrowserAction(uri, 'searched', type, label);
	}
}

/*
 * here we store a set of functions that will be called when a drag-drop-edit
 * has been performed those can be registered, for example to update
 * visualization components
 */
var dropEditUpdateFunctions = [];

function addListenerFunctionForDropEditUpdate(func) {
	// push if not already there
	if (dropEditUpdateFunctions.indexOf(func) == -1) {
		dropEditUpdateFunctions.push(func);
	}
}

function initDropableMarkupSection(element) {
	element.droppable({
        accept: ".termline",
		drop : function(event, ui) {
			var termElement = ui.draggable;
			var termnameDiv = termElement.find("div.termID");
			var termname = termnameDiv.html();
			termname = termname.replace(/<wbr>/g, "");
			var markupElement = jq$(this).find("div.defaultMarkupFrame");

			var markupID = markupElement.attr('id');
			if (!markupID) {
				markupID = jq$(this).attr('dragdropid');
			}

			// alert('dropped: '+ termname + ' on id: '+markupID );
			sendAddedTerm(termname, markupID);
		},
		hoverClass : "drophover"
	});
}

function initDropableTermList(element) {
	element.droppable({
		drop : function(event, ui) {
			var termElement = ui.draggable;
			var termnameDiv = termElement.find("div.termID");
			var termname = termnameDiv.html();
			//alert(termname);

			sendTermBrowserAction(termname, 'searched');
		},
		hoverClass : "drophover"
	});
}

function initCollapseTermBrowser() {
	jq$(".showList").each(function() {
		jq$(this).bind('click', function() {
			jq$(".showList").hide();
			jq$(".hideList").show();
			jq$(".termlist").show('50');
			// stores user's collapse state on server
			handleTermActionEvent(jq$(this));
		});

	});
	jq$(".hideList").each(function() {
		jq$(this).bind('click', function() {
			jq$(".hideList").hide();
			jq$(".showList").show();
			jq$(".termlist").hide('50');
			// stores user's collapse state on server
			handleTermActionEvent(jq$(this));
		});

	});

}

function initAllBrowserActionEvents() {
	jq$(".removeConcept").each(function() {
		initClickEvents(jq$(this));
	});
	jq$(".openConcept").each(function() {
		initClickEvents(jq$(this));
	});
	jq$(".expandConcept").each(function() {
		initClickEvents(jq$(this));
	});
	jq$(".collapseConcept").each(function() {
		initClickEvents(jq$(this));
	});
	jq$(".clearList").each(function() {
		initClickEvents(jq$(this));
	});
	jq$(".toggleList").each(function() {
		initClickEvents(jq$(this));
	});
	jq$(".addParentConcept").each(function() {
		initClickEvents(jq$(this));
	});
	initCollapseTermBrowser();

}

function initClickEvents(element) {
	element.bind("click", function() {
		handleTermActionEvent(jq$(this));
	});
}

function handleTermActionEvent(element) {
	var command = 'noop';

	if (element.hasClass('removeConcept')) {
		command = 'remove';
	}
	if (element.hasClass('addParentConcept')) {
		command = 'addParent';
	}
	if (element.hasClass('hideList')) {
		command = 'collapseList';
	}
	if (element.hasClass('showList')) {
		command = 'openList';
	}
	if (element.hasClass('hideGraph')) {
		command = 'collapseGraph';
	}
	if (element.hasClass('showGraph')) {
		command = 'openGraph';
	}
	if (element.hasClass('toggleGraph')) {
		command = 'toggleGraph';
	}
	if (element.hasClass('expandConcept')) {
		command = 'expand';
	}
	if (element.hasClass('collapseConcept')) {
		command = 'collapse';
	}
	if (element.hasClass('clearList')) {
		command = 'clear';
	}
	if (element.hasClass('toggleList')) {
		command = 'toggle';
	}
	if (element.hasClass('openConcept')) {
		command = 'open';
	}

	var line = element.parent().parent().parent().parent().parent().parent();
	var termnameElement = line.find('div.termname').find('div.termID');
	var term = termnameElement.html();
	if (term) {
		term = term.replace(/<wbr>/g, "");
	}




	sendTermBrowserAction(term, command);

}

function sendTermBrowserAction(term, command, type, label) {
	var sectionID = jq$('.termbrowserframe').attr('sectionid');
	var params = {
		action : 'TermBrowserAction',
		term : term,
		command : command,
        type: type,
		sectionID : sectionID,
        label: label
	};
	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			fn : function() {
				// insert new browser data
				jq$('.termbrowserframe').replaceWith(this.response);

				// re-init js features
				jq$(".termline").each(activateDraggables);
				jq$(".termlist").each(function() {
					initDropableTermList(jq$(this));
				});

				initAllIconHovers();
				initAllBrowserActionEvents();
                initSemanticAutocompletionSlot();
			}
		}
	}

	new _KA(options).send();
}

function initAllIconHovers() {
	jq$(".termline").each(function() {
		initIconHover(jq$(this));
	});
}

function initIconHover(element) {

	element.find('.hoverAction').each(function() {
		var icon = jq$(this);
		element.hover(function() {
			icon.show();
		}, function() {
			icon.hide();
		});
	});
}

function activateDraggables() {
	jq$(this).draggable({
		distance : 20,
		opacity : 0.55,
		revert : true,
        helper: 'clone'
	});
}

function sendAddedTerm(term, oldTargetID) {

	var params = {
		action : 'DragDropEditActionManager',
		termname : term,
		targetID : oldTargetID
	};
	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			fn : function() {
				// the action can also specify an ancestor section to be
				// replaced,
				// and then sends back old and new ID of that section
				var response = cleanStringFromTrailingLinebreaks(this.responseText);
				var newTargetID = response;
				var delimiter = "#";
				if (response.contains(delimiter)) {
					var IDs = response.split(delimiter);
					if (IDs.length = 2) {
						oldTargetID = IDs[0];
						newTargetID = IDs[1];
					}
				}
				rerenderSection(oldTargetID, newTargetID);

				// execute registered listener functions
				// e.g., for update of KB visualization components
				for (var i = 0; i < dropEditUpdateFunctions.length; i++) {
					dropEditUpdateFunctions[i]();
				}
			}
		}
	}

	new _KA(options).send();
}

function rerenderSection(oldTargetID, newTargetID) {

	// clean ID that for some reason might have some linebreaks appended
	var replacer = new RegExp("\r\n", "g");
	newTargetID = newTargetID.replace(replacer, "");

	var params = {
		action : 'ReRenderContentPartAction',
		KdomNodeId : newTargetID
	};
	var options = {
		url : KNOWWE.core.util.getURL(params),
		response : {
			fn : function() {
				if (this.response.toLowerCase().startsWith("not found")) {
					// if section can not be rerendered correctly for instance
					// because multiple sections have changed/been created
					location.reload();
				} else {

					// insert re-rendered content block
					var directSelector = 'div[dragdropid="' + oldTargetID
						+ '"]';
					var markupBlockOld;
					if (jq$(directSelector).exists()) {
						markupBlockOld = jq$(directSelector);
					}
					else {
						markupBlockOld = jq$('#' + oldTargetID);
					}
					var replaceContent = this.response;
					replaceContent = cleanStringFromTrailingLinebreaks(replaceContent);
					markupBlockOld.replaceWith(replaceContent);

					// re-init edit-functionalities for inserted part
					var markupBlockNew = jq$('div[dragdropid="' + newTargetID
						+ '"]');
					if (markupBlockNew.length == 0) {
						markupBlockNew = jq$('#' + newTargetID);
					}
					markupBlockNew.find(".dropTargetMarkup").each(function() {
						initDropableMarkupSection(jq$(this));
					});
					initDropableMarkupSection(markupBlockNew);
					_TM.animateDefaultMarkupMenu(markupBlockNew);
					_TM.decorateToolMenus(markupBlockNew);
				}
			}
		}
	}

	new _KA(options).send();

}

function cleanStringFromTrailingLinebreaks(str) {
	while (str.lastIndexOf('\r\n') == str.length - 2) {
		str = str.substring(0, str.length - 2);
	}
	return str;
}

function updateTermBrowser(event, ui) {
	var term = ui.item.value;
	sendTermBrowserAction(term, 'searched');
}
