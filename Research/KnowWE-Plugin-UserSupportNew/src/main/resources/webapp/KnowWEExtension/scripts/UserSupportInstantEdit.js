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
		    	+ KNOWWE.plugin.usersupportinstantedit.getSaveCancelDeleteButtons(id);
	    },
	    
	    postProcessHTML : function(id) {
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
            	mode: "javascript",
            	theme: "elegant",
            	extraKeys: {"Ctrl-Space": function(cm) {CodeMirror.simpleHint(cm, CodeMirror.javascriptHint);}}           		
            });
	        
	    },
	    
	    getSaveCancelDeleteButtons : function(id) {
    		return KNOWWE.plugin.instantEdit.getButtonsTable(new Array(
    				KNOWWE.plugin.usersupportinstantedit.getSaveButton(id), 
    				KNOWWE.plugin.instantEdit.getCancelButton(id), 
    				"       ",  
    				KNOWWE.plugin.instantEdit.getDeleteSectionButton(id)));
    	},
    	
    	getSaveButton : function(id) {
    		return "<a class=\"action save\" " 
    			+ "href=\"javascript:KNOWWE.plugin.usersupportinstantedit.save('"
    			+ id
    			+ "')\">Save</a>";
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