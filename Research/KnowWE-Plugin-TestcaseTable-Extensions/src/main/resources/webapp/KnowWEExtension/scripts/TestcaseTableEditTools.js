/**
 * The KNOWWE global namespace object. If KNOWWE is already defined, the
 * existing KNOWWE object will not be overwritten so that defined namespaces are
 * preserved.
 */
if (typeof KNOWWE == "undefined" || !KNOWWE) {
    var KNOWWE = {};
}

/**
 * The KNOWWE.core global namespace object. If KNOWWE.core is already defined,
 * the existing KNOWWE.core object will not be overwritten so that defined
 * namespaces are preserved.
 */
if (typeof KNOWWE.plugin == "undefined" || !KNOWWE.plugin) {
    KNOWWE.plugin = {};
}

if (typeof KNOWWE.plugin.testcasetable == "undefined" || !KNOWWE.plugin.testcasetable) {
    KNOWWE.plugin.testcasetable = {};
}


/**
 * This is the default edit provider.
 * It renders every table cell in a text field.
 * 
 */
KNOWWE.plugin.testcasetable.editProvider = (function(){


	var objects;
	var tableIndices = {};
	var editId;
		
	return {
		
		/**
		* fetches a Json Object which contains all questions and alternatives
		* [{'name': 'FrageNum','type': 'num'}, {'name': 'FrageOC','type': 'oc','alternatives': ['a1','a2','a3','a4','a5']}, {'name': 'FrageYN','type': 'oc','alternatives': ['Yes','No']}]
		*/
		prepare: function(id){
			editId = "defaultEditTable" + id;
			
			params = {
				action : 'TestcaseTableEditAction',
				KWikiWeb : 'default_web',
				kdomid: id
			};

			// options for AJAX request
			options = {
				async: false,
				url : KNOWWE.core.util.getURL( params ),
				response : {
					action : 'none',
					fn : function(){
						objects = Json.evaluate(this.responseText);
					}
				}
			};

			// send AJAX request
			new _KA( options ).send(); 
		},
		
		/**
		* checks if the parameter value is a valid number
		* Examples for valid numbers (without the ''):
		* '-1', '1', '1.2', '-1.2', '1,2', '-1,2'
		*/
		isValidNum: function(value) {
			return value.match(/[-+]?\b[0-9]+([.,][0-9]+)?$/);
		},
		
		/**
		* checks if the parameter value is a valid KnowWE/JSP-Date
		*/
		isValidDate: function(value) {
			//ToDo noch aktualisieren
			return value.match(/([0-3][0-9])[- /.]([0-1]?[0-9])[- /.](19|20)[0-9]{2}/);
		},
		
		/**
		* checks if the parameter value is valid text
		* everything is valid text --> returns always true
		*/
		isValidText: function(value) {
			//there are no restrictions for texts to be valid
			return true;
		},
		
		isValidDropdown: function(el) {
			var valid = !el.options[el.selectedIndex].disabled;
			//alert(el.selectedIndex);
			//alert(el.options[el.selectedIndex].selected);
			return valid;
		},
		
		/**
		* checks for the HTML-Object el if its value is a valid value
		* switches with the help of the name-tag which isValidxxx method is called
		*/
		isValid: function(el) {
			var valid = false;
			switch (el.name) {
				default:
				case "text":
					valid = KNOWWE.plugin.testcasetable.editProvider.isValidText(el.value);
					break;
				case "num":
					valid = KNOWWE.plugin.testcasetable.editProvider.isValidNum(el.value);
					break;
				case "date":
					valid = KNOWWE.plugin.testcasetable.editProvider.isValidDate(el.value);
					break;
				case "oc":
					valid = KNOWWE.plugin.testcasetable.editProvider.isValidDropdown(el);
					break;
			}
			//HTML 5 attribut "classList"
			if (valid) {
				if (el.classList.contains("errorField")) {
					el.classList.remove("errorField");
				}
			} else {
				if (!el.classList.contains("errorField")) {
					el.classList.add("errorField");
				}
			}
		},
		
		/**
		 * this method returns an HTML-String, that renders a DropDownBox (select)
		 * arr: an Array with the DropDownBox values
		 * value: the value which should be selected by default
		 * onChange: the onChange Event, if undefined, no onChange-Event is registered
		 */
		renderOC: function(arr, value, onChange) {
			var obj = {};
			var val = false;
			var inner = "";
		
			var box = '<select name="oc" size="1"';
			
			if (onChange != undefined || onChange == "") inner += ' onChange="' + onChange + '"';
			
			inner += '>';
			
			for (var i = 0; i < arr.length; i++) {
				inner += '<option';
					if (value != undefined && arr[i].toLowerCase() == value.toLowerCase()) {
						inner += ' selected="selected"';
						val = true;
					}
				inner += '>' + arr[i] + '</option>';
			}
			if (!val && value != undefined) {
				box += ' class="errorField"';
				inner += '<option disabled="disabled" selected="selected">' + value + '</option>';
			}
			inner += '</select>';
			
			obj.html = box + inner;
			obj.valid = val;
			
			return obj;
			
		},
		
		/**
		 * this method returns an HTML-String, that renders an Input-Textfield
		 * value: default value of the Input-Box, if value == undefined no value is set
		 */
		renderNum: function(value) {
			var obj = {};
			var end = "";
			var val = false;
		
			var box = '<input name="num" type="text" size="10"';
			if (value != undefined) {
				end += ' value="' + value + '"';
				val = KNOWWE.plugin.testcasetable.editProvider.isValidNum(value);
			}
			end += '>';
			
			if (!val) {
				box += ' class="errorField"';
			}
			
			obj.html = box + end;
			obj.valid = val;
			
			return obj;
		},
		
		/**
		 * this method returns an HTML-String, that renders an Input-Textfield
		 * value: default value of the Input-Box, if value == undefined no value is set
		 */
		renderText: function(value) {
			var obj = {};
			var end = "";
			var val = false;
		
			var box = '<input name="text" type="text" size="10"';
			if (value != undefined) {
				end += ' value="' + value + '"';
				val = KNOWWE.plugin.testcasetable.editProvider.isValidText(value);
			}
			end += '>';
			
			if (!val) {
				box += ' class="errorField"';
			}
			
			obj.html = box + end;
			obj.valid = val;
			
			return obj;
		},
		
		/**
		 * this method returns an HTML-String, that renders an Input-Textfield
		 * value: default value of the Input-Box, if value == undefined no value is set
		 */
		renderDate: function(value) {
			var obj = {};
			var end = "";
			var val = false;
		
			var box = '<input name="date" type="text" size="10"';
			if (value != undefined) {
				end += ' value="' + value + '"';
				val = KNOWWE.plugin.testcasetable.editProvider.isValidDate(value);
			}
			end += '>';
			
			if (!val) {
				box += ' class="errorField"';
			}
			
			obj.html = box + end;
			obj.valid = val;
			
			return obj;
		},
		
		/**
		* will be executed after the table is put in the DOM-structure
		*/
		postProcess: function(id) {
			var tmp = document.getElementById(editId).childNodes;
			while (true) {
				if (tmp[0].tagName == "TR") {
					break;
				}
				tmp = tmp[0].childNodes;
			}
			
			//in a first step just textboxes are rendered; textboxes will be replaced with specific textboxes/Dropdownboxes
			var firstTr = tmp[0].childNodes;
			for (var i = 2; i < firstTr.length; i++) {
				KNOWWE.plugin.testcasetable.editProvider.rerenderColumn(i);
			}
			
			//adding Changelistener to the first row and all other fields
			for (var j = 0; j < tmp[0].childNodes.length; j++) {
				KNOWWE.plugin.testcasetable.editProvider.addChangeColHandler(j);
				KNOWWE.plugin.testcasetable.editProvider.addValidHandler(j);
			}
		},
		
		/**
		* adding changeListener to the first row in the column specified with the int parameter col
		*/
		addChangeColHandler: function(col) {
			var tmp = document.getElementById(editId).childNodes;
			while (true) {
				if (tmp[0].tagName == "TR") {
					break;
				}
				tmp = tmp[0].childNodes;
			}
			tmp[0].childNodes[col].firstChild.onchange = function(){KNOWWE.plugin.testcasetable.editProvider.changeColumn(this)};
		},
		
		/**
		* adding changeListener to all rows in the column specified with the int parameter col
		*/
		addValidHandler: function(col) {
			var tmp = document.getElementById(editId).childNodes;
			while (true) {
				if (tmp[0].tagName == "TR") {
					break;
				}
				tmp = tmp[0].childNodes;
			}
			for (var i = 1; i < tmp.length; i++) {
				tmp[i].childNodes[col].firstChild.onchange = function(){KNOWWE.plugin.testcasetable.editProvider.isValid(this)};
			}
		},
		
		/**
		* is executed if a Dropdownbox in the first row is changed
		*/
		changeColumn: function(el) {
			if (el.classList.contains("errorField")) {
				el.classList.remove("errorField");
			}
			var col = KNOWWE.table.edit.getPositionInTable(el).column;
			KNOWWE.plugin.testcasetable.editProvider.rerenderColumn(col);
		},
		
		/**
		 * Called after inserting a new column into the table
		 * colNr : index of new column
		 */
		columnAdded : function(colNr){
			KNOWWE.plugin.testcasetable.editProvider.addChangeColHandler(colNr);
		},
		
		/**
		 * Called after inserting a new row into the table
		 * rowNr : index of new row
		 */
		rowAdded : function(rowNr){
			
		},
		
		/**
		* replaces all input fields of a column with new fields
		* copies the old value into the new input fields
		*/
		rerenderColumn: function(colNr) {
			var tmp = document.getElementById(editId).childNodes;
			while (true) {
				if (tmp[0].tagName == "TR") {
					break;
				}
				tmp = tmp[0].childNodes;
			}
			var oldInput;
			var value;
			var tmpInnerHtml, newHtml;
			for (var i = 1; i < tmp.length; i++) {
				//get old value, remove the input-box, save the old inner-HTML
				oldInput = tmp[i].childNodes[colNr].firstChild;
				value = KNOWWE.table.edit.getValue(editId, oldInput, i, colNr);
				tmp[i].childNodes[colNr].removeChild(oldInput);
				tmpInnerHtml = tmp[i].childNodes[colNr].innerHTML;
				
				//create new input-box, assign new inner-HTML string
				newHtml = KNOWWE.plugin.testcasetable.editProvider.getDisplayComponent(i, colNr, value);
				tmp[i].childNodes[colNr].innerHTML = newHtml + tmpInnerHtml;
			}
			KNOWWE.plugin.testcasetable.editProvider.addValidHandler(colNr);
		},
		
		/**
		* returns the questiontype of the column specified in the in parameter col
		* return the type specified in the JSON-Object
		*/
		getQuestionType : function(col) {
			if (document.getElementById(editId) == null) {
				return undefined;
			}
			var obj = {};
			var tmp = document.getElementById(editId).childNodes;
			while (true) {
				if (tmp[0].tagName == "TR") {
					break;
				}
				tmp = tmp[0].childNodes;
			}
			var rightNode = tmp[0].childNodes[col];
			var el = rightNode.firstChild;
			var index = el.selectedIndex;			
			var name = el.options[index].value.toLowerCase();
			
			var objIndex = -1;			
			if (index >= objects.length) {
				obj.type = "text";
				return obj;
			}
			
			if (objects[index].name.toLowerCase() == name) {
				objIndex = index;
			} else {
				for (var i = 0; i < objects.length; i++) {
					if (objects[i].name.toLowerCase() == name) {
						objIndex = i;
						break;
					}
				}
			}
			
			obj.type = objects[objIndex].type;
			
			if (obj.type == "oc") {
				obj.alternatives = objects[objIndex].alternatives;
			}
			
			return obj;
		},
	
		/**
		 * this method must return an HTML-String, that renders a component at the specified location
		 * (row, col) and displays the supplied value. 
		 * for non-editable values it can return a <span> or something similar.
		 * row / col: index in the table
		 * value: value to display, of undefined, if no value should be displayed
		 * 
		 */
		getDisplayComponent : function(row, col, value){
			var obj;
	
			//default: Input-Textfield
			var result = KNOWWE.plugin.testcasetable.editProvider.renderText(value).html;
			
			if (objects != undefined) {
				if (row == 0) {
					//first Row-->just Spans and Dropdownboxes
					if (col > 1) {
						var arr = new Array(objects.length);
						for (var i = 0; i < objects.length; i++) {
							arr[i] = objects[i].name;
						}
						obj = KNOWWE.plugin.testcasetable.editProvider.renderOC(arr, value);
						result = obj.html;
					} else if (col <= 1) {
						result = '<span>' + value + '</span>';
					}
				} else {
					//col <= 1 musss nicht ber�cksichtigt werden, da in Spalte 0&1 immer nur InputFelder stehen
					if (col > 1) {
						var qtype = KNOWWE.plugin.testcasetable.editProvider.getQuestionType(col);
						if (qtype == undefined) {
							return result;
						}
						switch (qtype.type) {
							default:
								break;
							case "date":
								result = KNOWWE.plugin.testcasetable.editProvider.renderDate(value).html;
								break;				
							case "text": 
								result = KNOWWE.plugin.testcasetable.editProvider.renderText(value).html;
								break;
							case "num":
								result = KNOWWE.plugin.testcasetable.editProvider.renderNum(value).html;
								break;					
							case "oc":
								var arr = qtype.alternatives.slice(0);
								arr.push("-");
								result = KNOWWE.plugin.testcasetable.editProvider.renderOC(arr, value).html;
								break;
						}
					}
				}
			}
			
			return result;
		},
		
		/**
		 * This method returns the default value for newly created cells, 
		 * after inserting a row or column. This method can also return 'undefined', 
		 * e.g. to leave a textual input empty 
		 */
		getDefaultValue : function(row, col) {
			if (row <= 1) {
				return "";
			} else {
				return KNOWWE.table.edit.getValue(editId, undefined, (row-1), col);
			}
		},
		
		
		/**
		 * Returns an array of actions, that are available for the cell at the specified coordinates.
		 * 
		 */
		getActions : function(el, row, col, editor) {
			var actions = [];
		
			if (row == 0 && col > 1){
				actions.push({name: 'Delete column', f: (function(){editor.delCol(el)}) });
				actions.push({name: 'Add column left', f: (function(){editor.addCol(el, 0)})});
				actions.push({name: 'Add column right', f: (function(){editor.addCol(el, 1)})});
			} else if (row == 0 && col == 1){
				actions.push({name: 'Add column right', f: (function(){editor.addCol(el, 1)})});
			}
			
			if (col == 0 && row > 0){
				actions.push({name: 'Delete row', f: (function(){editor.delRow(el)})});
				actions.push({name: 'Add row above', f: (function(){editor.addRow(el, 0)})});
				actions.push({name: 'Add row below', f: (function(){editor.addRow(el, 1)})});
			} else if (col == 0 && row == 0){
				actions.push({name: 'Add row below', f: (function(){editor.addRow(el, 1)})});
			}
			
			return actions;
		}
	}
})();
