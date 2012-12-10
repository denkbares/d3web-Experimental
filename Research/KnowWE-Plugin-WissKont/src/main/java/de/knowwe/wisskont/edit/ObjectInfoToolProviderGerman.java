/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.wisskont.edit;

import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.ObjectInfoTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * @author jochenreutelshofer
 * @created 28.11.2012
 */
public class ObjectInfoToolProviderGerman implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		Section<SimpleDefinition> definition = Sections.findSuccessor(section,
				SimpleDefinition.class);
		if (definition != null) {
			return new Tool[] { getObjectInfoPageTool(definition, userContext) };
		}
		return new Tool[] {};
	}

	protected Tool getObjectInfoPageTool(Section<? extends SimpleTerm> section, UserContext userContext) {
		TermIdentifier termIdentifier = section.get().getTermIdentifier(section);
		String lastPathElementExternalForm = new TermIdentifier(termIdentifier.getLastPathElement()).toExternalForm();
		String externalTermIdentifierForm = termIdentifier.toExternalForm();
		String jsAction = "window.location.href = "
				+ "'Wiki.jsp?page=ObjectInfoPage&" + ObjectInfoTagHandler.TERM_IDENTIFIER
				+ "=' + encodeURIComponent('"
				+ maskTermForHTML(externalTermIdentifierForm)
				+ "') + '&" + ObjectInfoTagHandler.OBJECT_NAME + "=' + encodeURIComponent('"
				+ maskTermForHTML(lastPathElementExternalForm) + "')";
		return new DefaultTool(
				"KnowWEExtension/d3web/icon/infoPage16.png",
				"Umbenennen",
				"Öffnet eine Seite zum Umbenennen des Konzeptes in der ganzen Wissensbasis.",
				jsAction);
	}

	private String maskTermForHTML(String string) {
		string = string.replace("\\", "\\\\");
		string = ObjectInfoTagHandler.maskTermForHTML(string);
		return string;
	}
}
