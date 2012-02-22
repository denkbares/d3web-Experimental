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

/**
 * Namespace: KNOWWE.plugin.usersupportinstantedit The KNOWWE usersupport namespace.
 */
KNOWWE.plugin.usersupportinstantedit = function() {
	
	var editor;
	
	function createTextAreaID(id) {
		return "defaultEdit" + id;
	}
	
    return {
    	
	    generateHTML : function(id) {
	    	return "<textarea id = " + createTextAreaID(id)
	    		+ " class='defaultEditTool' style='height: " + $(id).clientHeight + "px;'>"
		    	+ KNOWWE.plugin.instantEdit.getWikiText(id)
		    	+ "</textarea>"
		    	+ KNOWWE.plugin.instantEditHook.getSaveCancelDeleteButtons(id, "KNOWWE.plugin.usersupportinstantedit");
	    },
	    
	    postProcessHTML : function(id, coloringmode) {
	    	var textarea = $(createTextAreaID(id));
	    	if (typeof AutoComplete != "undefined") AutoComplete.initialize(textarea);
	        TextArea.initialize(textarea);
	        
	        textarea.focus();
	        while (textarea.clientHeight == textarea.scrollHeight) {
	        	var tempHeight = textarea.style.height; 
	        	textarea.style.height = textarea.clientHeight - 5 + "px";
	        	// abort if we are below minHeight and the height does not change anymore
	        	if (textarea.style.height == tempHeight) break;
	        }
	        textarea.style.height = textarea.scrollHeight + 15 + "px";
	        
	        var editorID = "defaultEdit"+id;
            editor = CodeMirror.fromTextArea(document.getElementById(editorID), {
            	lineNumbers: true,
//            	mode: "usersupportmode",
            	mode: coloringmode,
            	theme: "elegant",
 		        extraKeys: {"Ctrl-Space": function(cm)
 		        	{
 		        		KNOWWE.plugin.usersupport.gatherDialogComponentCompletions(cm, CodeMirror.usersupportHint, id)
 		        	}
 		        }
            });
	        
	    },
    	
    	save : function(id) {
    		editor.toTextArea();
			KNOWWE.plugin.instantEdit.save(id);
    	},
	    
	    unloadCondition : function(id) {
	    	var textArea = $(createTextAreaID(id));
			return textArea.defaultValue == textArea.value;
	    },
	    
	    generateWikiText : function(id) {
	    	return $(createTextAreaID(id)).value;
	    }
    }
}();