/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.onte.editor;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.AbstractOWLTermDefinition;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.kdom.manchester.frame.DataPropertyFrame;
import de.knowwe.kdom.manchester.frame.ObjectPropertyFrame;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.tools.ToolUtils;

/**
 * The CreateEntityToolProvider offers help with quick creation of not yet
 * defined terms of the ontology. For this it uses the ToolProvider mechanismen
 * and offers in a drop down actions for the following entity creations:
 * <ul>
 * <li>classes</li>
 * <li>individuals</li>
 * <li>object properties</li>
 * <li>data properties</li>
 * </ul>
 * 
 * @author Stefan Mark
 * @created 06.10.2011
 */
public class CreateEntityToolProvider implements ToolProvider {

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		return section.hasErrorInSubtree();
	}

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {

		// section does not have an error in the subtree, that means there are
		// now unknown terms, so return (no creation feature needed)
		if (!section.hasErrorInSubtree()) {
			return ToolUtils.emptyToolArray();
		}

		@SuppressWarnings("unchecked")
		Section<? extends AbstractOWLTermDefinition> s = (Section<? extends AbstractOWLTermDefinition>) section;
		return new Tool[] {
				getCreateClassTool(s), getCreateObjectPropertyTool(s),
				getCreateDataPropertyTool(s) };

		/*
		 * in JavaScript: call per Ajax the creation Action and redirect to the
		 * newly created page, insert in the new page a default frame for the
		 * entity and call the edit.jsp page so the user can instantly start
		 * editing
		 */
	}

	/**
	 * Returns the {@link DefaultTool} for the creation of new
	 * {@link ClassFrame} out of unknown terms.
	 * 
	 * @created 06.10.2011
	 * @param section
	 * @return
	 */
	private Tool getCreateClassTool(Section<? extends AbstractOWLTermDefinition> section) {
		String image = "KnowWEExtension/images/dt_icon_realisation.png";
		String name = "Create Class";
		String description = "Creates a article containing a basic OWL Class Frame for adding the term to the Ontology!";
		String action = "KnowWE.plugin.onte.actions.createEntity(urlencode('"
				+ section.getText()
				+ "'), 'OWLClass')";

		return new DefaultTool(image, name, description, action);
	}

	/**
	 * Returns the {@link DefaultTool} for the creation of new
	 * {@link ObjectPropertyFrame} out of unknown terms.
	 * 
	 * @created 06.10.2011
	 * @param section
	 * @return
	 */
	private Tool getCreateObjectPropertyTool(Section<? extends AbstractOWLTermDefinition> section) {
		String image = "KnowWEExtension/images/dt_icon_realisation.png";
		String name = "Create ObjectProperty";
		String description = "Creates a article containing a basic OWL ObjectProperty Frame for adding the term to the Ontology!";
		String action = "KnowWE.plugin.onte.actions.createEntity(urlencode('"
				+ section.getText()
				+ "'), 'OWLObjectProperty')";

		return new DefaultTool(image, name, description, action);
	}

	/**
	 * Returns the {@link DefaultTool} for the creation of new
	 * {@link DataPropertyFrame} out of unknown terms.
	 * 
	 * @created 06.10.2011
	 * @param section
	 * @return
	 */
	private Tool getCreateDataPropertyTool(Section<? extends AbstractOWLTermDefinition> section) {
		String image = "KnowWEExtension/images/dt_icon_realisation.png";
		String name = "Create DataProperty";
		String description = "Creates a article containing a basic OWL DataProperty Frame for adding the term to the Ontology!";
		String action = "KnowWE.plugin.onte.actions.createEntity(urlencode('"
				+ section.getText()
				+ "'), 'OWLDataProperty')";

		return new DefaultTool(image, name, description, action);
	}
}
