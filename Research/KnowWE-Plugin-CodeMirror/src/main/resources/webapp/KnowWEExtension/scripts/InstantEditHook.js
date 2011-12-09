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
 * 
 * Used to hook into InstantEdit-Interface
 * 
 * You can provide your own js-function for the save-button.
 * 
 * You have to specify your own ToolProvider for the instantEditButton in
 * order to get your js-namespace set. 
 * Then the InstantEditTool calls the generateHTML/postProcessHTML-method in your js-file.
 * There you can delegate the work to this class.
 * 
 */
KNOWWE.plugin.instantEditHook = function() {
	
	/**
	 * The editor object.
	 */
	var editor;
	
    return {
    	
    	createTextAreaID : function(id, name) {
    		return name + id;
    	},
    	
    	getSaveButton : function(id, namespace) {
    		return "<a class=\"action save\" " 
    			+ "href=\"javascript:" + namespace + ".save('"
    			+ id
    			+ "')\">Save</a>";
    	},
    
	    getSaveCancelDeleteButtons : function(id, namespace) {
    		return KNOWWE.plugin.instantEdit.getButtonsTable(new Array(
    				KNOWWE.plugin.instantEditHook.getSaveButton(id, namespace),
    				KNOWWE.plugin.instantEdit.getCancelButton(id),
    				"       ",
    				KNOWWE.plugin.instantEdit.getDeleteSectionButton(id)));
    	}
    }
}();