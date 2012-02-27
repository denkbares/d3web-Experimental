/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

//var _KE = KNOWWE.helper.event;    /* Alias KNOWWE event. */
//var _KA = KNOWWE.helper.ajax;     /* Alias KNOWWE ajax. */
//var _KS = KNOWWE.helper.selector; /* Alias KNOWWE ElementSelector */
//var _KL = KNOWWE.helper.logger;   /* Alias KNOWWE logger */
//var _KN = KNOWWE.helper.element   /* Alias KNOWWE.helper.element */
//var _KH = KNOWWE.helper.hash      /* Alias KNOWWE.helper.hash */


/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.plugin global namespace object. If KNOWWE.plugin is already defined, the
 * existing KNOWWE.plugin object will not be overwritten so that defined namespaces
 * are preserved.
 */
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
	    KNOWWE.plugin = function(){
	         return {  }
	    }
}

/**
 * The KNOWWE.plugin.debuggr global namespace object. If KNOWWE.plugin.debuggr is already defined, the
 * existing KNOWWE.plugin.debuggr object will not be overwritten so that defined namespaces
 * are preserved.
 */
KNOWWE.plugin.debuggr = function(){
    return {
    }
}();
/**
 * Namespace: KNOWWE.plugin.debuggr
 */
KNOWWE.plugin.debuggr = function(){
	
	return {
		/**
         * Function: initializeMenu
         * 		add the click and mouse events for debuggermenu
         */
		initializeMenu : function (){
			var i = 0;
			$$('[class^=debuggerMenu]').each(function(element) {
				element.id = 'debuggerMenu_' + i;
				i++;
				_KE.add('click', element, function() { 
					KNOWWE.plugin.debuggr.renderTrace(element);
					KNOWWE.plugin.debuggr.renderMenu(element);
					KNOWWE.plugin.debuggr.renderMain(element);
					$('debuggerRule').empty();
				});
			});
		},
		traceClicked : function(element) {
			KNOWWE.plugin.debuggr.removeTrace(element);
			KNOWWE.plugin.debuggr.renderMenu(element);
			KNOWWE.plugin.debuggr.renderMain(element);
			$('debuggerRule').empty();
		},
		renderMenu : function(element) {
       		var params = {
       				action : 'DebuggerMenuAction',
       				kbid : element.getAttribute('kbid'),
       				qid : element.innerHTML
       		}  
           	var options = {
	            url : KNOWWE.core.util.getURL( params ),
	            response : {
	                action : 'insert',
	                ids : [ 'debuggerMenu' ],
	                fn : function() {
	                	KNOWWE.plugin.debuggr.initializeMenu();
					}
	            }
            }
           	new _KA( options ).send(); 
		},
		renderTrace : function(element) {
			// get trace-div
			trace = $('debuggerTrace');
			// get trace-entries
			traceEntries = trace.childNodes;
			newEntry = new Element('span', {
				lvl: traceEntries.length, 
				kbid: element.getAttribute("kbid"),
			    events: {
			        click: function(){
			        	KNOWWE.plugin.debuggr.traceClicked(this);
			        }
			    }
			});
			newEntry.innerHTML = element.innerHTML;
			if (newEntry.getAttribute("lvl") == 1)
				newEntry.style.color = 'rgb(150, 110, 120)';
				
			trace.adopt(newEntry);
		},
		removeTrace : function(element) {
			trace = $('debuggerTrace');
			level = element.getAttribute("lvl");
			$ES('span', 'debuggerTrace').each(function(element) {
				if(element.getAttribute("lvl") > level) {
					trace.removeChild(element);
				}
			});
		},
		/**
         * Function: initializeMain
         * 		add the click and mouse events for main-div
         */
        initializeMain : function (){
        	$$('[class^=debuggerMainEntry]').each(function(element) {
        		_KE.add('click', element, function() { 
        			KNOWWE.plugin.debuggr.renderRule(element);
        			$$('[class^=debuggerMainEntryActive]').each(function(mainEntry) {
        				mainEntry.className = 'debuggerMainEntry';
        			});
        			element.className = element.className + 'Active';
        		});
        	});
        },
		renderMain : function(element) {
			if (element.innerHTML == 'Solutions') {
				$('debuggerMain').empty();
				return;
			}
			
   			traceLength = $('debuggerTrace').childNodes.length;
   			sid = "";
   			if (traceLength > 1)
   				sid = $('debuggerTrace').childNodes[1].innerHTML;
				
       		var params = {
       				action : 'DebuggerMainAction',
       				kbid : element.getAttribute('kbid'),
       				qid : element.innerHTML,
       				sid : sid
       		}  
           	var options = {
	            url : KNOWWE.core.util.getURL( params ),
	            response : {
	                action : 'insert',
	                ids : [ 'debuggerMain' ],
	                fn : function() {
	                	KNOWWE.plugin.debuggr.initializeMain();
	                }
	            }
            }
           	new _KA( options ).send(); 
		},
		renderRule : function(element) {
			if (element.getAttribute('ruleid') == null)
				return;
			
       		var params = {
       				action : 'DebuggerRuleAction',
       				kbid : element.getAttribute('kbid'),
       				ruleid : element.getAttribute('ruleid')
       		}  
           	var options = {
	            url : KNOWWE.core.util.getURL( params ),
	            response : {
	                action : 'insert',
	                ids : [ 'debuggerRule' ],
	                fn : function() {
	                	KNOWWE.plugin.debuggr.initializeQuestions();
					}
	            }
            }
           	new _KA( options ).send(); 
		},
		/**
		 * Function: mainSelected
		 * 		Shows the list of influential rules for element
		 */
		mainSelected : function(option) {
			$ES('ul', 'debuggerMain').each(function(element) {
				element.style.display = "none";
			});
			$$('[class^=debuggerMainEntryActive]').each(function(mainEntry) {
				mainEntry.className = 'debuggerMainEntry';
			});
			id = option + "_rules";
			document.getElementById(id).style.display = "block";
			$('debuggerRule').empty();
		},
		/**
         * Function: initializeQuestions
         * 		add the click and mouse events for questions
         */
        initializeQuestions : function (){
        	// walk through questions
        	var i = 0;
        	$$('[class^=debuggerQuestion]').each(function(element) {
        		// build unique id for each question
        		element.id = 'debuggerQuestion_' + i;
        		i++;
                _KE.add('click', element, function() { 
                	KNOWWE.plugin.debuggr.closeDropdowns();
                	KNOWWE.plugin.debuggr.openDropdown(element);
                });
        	});
        	// initialize each dropdown with a unique id
        	i = 0;
        	$$('[class^=debuggerDropdown]').each(function(element) {
        		element.id = 'debuggerDropdown_' + i;
        		i++;
        		_KE.add('mouseout', element, function(e) { 
        			event = e || window.event;
        			src = event.target || event.srcElement;
        			to = event.relatedTarget || event.toElement;
        			
        			if (to.className.match(/dchoice/) || to.className == 'dquestionLink' || to.className == 'dQtext' || 
        					to.className == 'dQnum' || to.parentNode.className == 'dQtext' || to.parentNode.className == 'dQnum')
        				return;
        			
        			element.style.display = 'none';
        		});
        	});
        },
		/**
         * Function: questionOCClicked
         * 		handles an incoming QuestionOC-Click
         */
        questionOCclicked : function(element) {
        	rel = eval("(" + element.getAttribute('rel') + ")");
        	if (element.className == 'dchoiceActive')
        		KNOWWE.plugin.debuggr.sendChoice(element, {action: 'RetractSingleFindingAction', ValueID: element.innerHTML, KBid: rel.kbid});
        	else
        		KNOWWE.plugin.debuggr.sendChoice(element, {action: 'SetSingleFindingAction', ValueID: element.innerHTML, KBid: rel.kbid});
        },
		/**
         * Function: questionMCClicked
         * 		handles an incoming QuestionMC-Click
         */
        questionMCclicked : function(element) {
        	rel = eval("(" + element.getAttribute('rel') + ")");
        	choices = '';
        	dropdown = element.parentNode;
        	// Workaround (see KNOWWE.plugin.quicki.answerMCCollect)
        	// Collect all active choices from the active dropdown-box
	       	 for (i = 0; i < dropdown.childNodes.length; i++) {
	    		 if (dropdown.childNodes[i] != element && dropdown.childNodes[i].className == 'dchoiceActive') {
	    			 choices += dropdown.childNodes[i].innerHTML;
	    			 choices += "#####";
	    		 }
	    	 }
	       	 
	       	if (element.className == 'dchoiceActive') {
        		// If last active choice is going to be retracted, we need to call 'RetractSingleFindingAction'
        		if (choices.length == 0)
        			KNOWWE.plugin.debuggr.sendChoice(element, {action: 'RetractSingleFindingAction', ValueID: element.innerHTML, KBid: rel.kbid});
        		// Delete the last "#####"
        		else choices = choices.substring(0, choices.length-5);
        	}
        	// Add the clicked non-active choice
        	else
        		choices += element.innerHTML;
        	
        	KNOWWE.plugin.debuggr.sendChoice(element, {action: 'SetSingleFindingAction', ValueID: choices, KBid: rel.kbid});
        },
		/**
         * Function: questionNumClicked
         * 		handles an incoming QuestionNum-Click
         */
        questionNumClicked : function(element) {
        	rel = eval("(" + element.getAttribute('rel') + ")");
        	inputNode = element.parentNode.childNodes[0];
        	errorNode = element.parentNode.childNodes[2];
        	input = inputNode.value;
        	
        	// enabling float value input also with "," instead of "."
            if(input.indexOf(",")!=-1){
            	input = input.replace(",", ".");
            }
	 		if (!input.match(/^[-+]?[0-9]*\.?[0-9]+([eE][-+]?[0-9]+)?$/)) {
	 			if (input == "") {
	 				KNOWWE.plugin.debuggr.sendChoice(element, {action: 'SetSingleFindingAction', ValueID: "MaU", KBid: rel.kbid});
	 				return;
	 			}
	 			errorNode.innerHTML = 'Input needs to be a number.';
	 			inputNode.value = "";
        		return;
	 		}
	 		
	 		  // if range is given, validate range
            if(rel.rangeMin!='NaN' && rel.rangeMax!='NaN'){
            	min = parseFloat(rel.rangeMin);
            	max = parseFloat(rel.rangeMax);
        	 	// compare with range
            	if(parseFloat(input) < min || parseFloat(input) > max){
            		errorNode.innerHTML = 'Input needs to be between '+min+' and '+max+'.';
            		inputNode.value = "";
            		return;
            	}
            }
        	
        	KNOWWE.plugin.debuggr.sendChoice(element, {action: 'SetSingleFindingAction', ValueID: input, KBid: rel.kbid});
			errorNode.innerHTML = "";
        },
		/**
         * Function: questionTextClicked
         * 		handles an incoming QuestionText-Click
         */
        questionTextClicked : function(element) {
        	rel = eval("(" + element.getAttribute('rel') + ")");
        	input = element.parentNode.childNodes[0].value;
        	KNOWWE.plugin.debuggr.sendChoice(element, {action: 'SetSingleFindingAction', ValueID: input, KBid: rel.kbid});
        },
		/**
         * Function: sendChoice
         * 		sends the chosen answer
         */
        sendChoice : function(element, params) {
        	rel = eval("(" + element.getAttribute('rel') + ")");
        	
        	pDefault = {
		            KWikiWeb : rel.web,
		            namespace : rel.ns,
		            ObjectID : rel.qid,
		            TermName : rel.qid
			}
        	
        	pDefault = KNOWWE.helper.enrich( params, pDefault );
        	
        	options = {
                    url : KNOWWE.core.util.getURL( pDefault ),
                    response : {
                    	action : 'none',
                    	fn : function(){
    			        	try {
    	                		KNOWWE.helper.observer.notify('update');
    			        	}
    			        	catch (e) { /*ignore*/ }
    			        	KNOWWE.core.util.updateProcessingState(-1);
                    	},
                        onError : function () {
    			        	KNOWWE.core.util.updateProcessingState(-1);                    	
                        }
                    }
                }
            	KNOWWE.core.util.updateProcessingState(1);
                new _KA( options ).send();
        },
		/**
         * Function: closeDropdowns
         * 		close all dropdowns
         */
        closeDropdowns : function (){
        	$$('[class^=debuggerDropdown]').each(function(element) {
        		element.style.display = 'none';
        	});
        },
		/**
         * Function: openDropdown
         * 		open a dropdown
         */
        openDropdown : function (element){
        	id = 'debuggerDropdown_' + element.id.split("_")[1];
        	dropdown = document.getElementById(id);
        	dropdown.style.left = element.offsetLeft + 'px';
        	dropdown.style.display = 'block';
        },
		/**
         * Function: rerender
         * 		rerender the dropdown
         */
        rerender : function () {
        	menu = $('debuggerMenu').innerHTML;
        	trace = $('debuggerTrace').innerHTML;
        	kbid = $('debuggerTrace').childNodes[0].getAttribute('kbid');
   			ruleid = $('debuggerRule').childNodes[0].getAttribute('ruleid');
   			traceLength = $('debuggerTrace').childNodes.length;
   			qid = $('debuggerTrace').childNodes[traceLength - 1].innerHTML;
   			selectInd = $ES('select', 'debuggerMain')[0].selectedIndex;
   			sid = "";
   			if (traceLength > 1)
   				sid = $('debuggerTrace').childNodes[1].innerHTML;
   			id = 'debugger';
   			
       		params = {
       				action : 'DebuggerRerenderAction',
       				menu : menu,
       				trace : trace,
       				kbid : kbid,
       				qid : qid,
       				sid : sid,
       				selectInd : selectInd,
       				ruleid : ruleid
       		} 
       		
           	options = {
                    url : KNOWWE.core.util.getURL( params ),
                    response : {
                        action : 'insert',
                        ids : [ id ],
                        fn : function(){
    			        	try {
    			            	KNOWWE.plugin.debuggr.initializeMenu();
    			            	KNOWWE.plugin.debuggr.initializeMain();
    			            	
    			            	$('debuggerTrace').getElements('span').each(function(element) {
    			            		element.onclick = function() {
    			            			KNOWWE.plugin.debuggr.traceClicked(this);
    			            		}
    			            	});
                        		KNOWWE.core.rerendercontent.update(); //Clear new SolutionPanel
    			        	}
    			        	catch (e) { /*ignore*/ }
    			        	KNOWWE.core.util.updateProcessingState(-1);
                        },
                        onError : function () {
    			        	KNOWWE.core.util.updateProcessingState(-1);                    	
                        }
                    }
                }
       		KNOWWE.core.util.updateProcessingState(1);
            new _KA( options ).send(); 
        }
	}
}();
/**
 * Initializes the required JS functionality when DOM is readily loaded
 */
(function init(){ 
    if( KNOWWE.helper.loadCheck( ['Wiki.jsp'] )){
        window.addEvent( 'domready', function(){
        	if ($('debugger') != null) {
        		KNOWWE.plugin.debuggr.initializeMenu();
        		KNOWWE.plugin.debuggr.initializeMain();
        		KNOWWE.plugin.debuggr.initializeQuestions();
        	}
        	KNOWWE.helper.observer.subscribe( 'update', KNOWWE.plugin.debuggr.rerender);
        	KNOWWE.plugin.debuggr.initializeQuestions();
        	KNOWWE.helper.observer.subscribe( 'update', function() {
        		setTimeout(KNOWWE.plugin.debuggr.initializeQuestions, 1000);
        	});
        });
    }
}());