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
package de.knowwe.wisskont;

import java.util.ArrayList;
import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 27.11.2012
 */
public class RelationMarkupRenderer extends DefaultMarkupRenderer {

	@Override
	public void renderMessages(Section<?> section, RenderResult string) {
		List<Section<ConceptMarkup>> conecptDefinitions = MarkupUtils.getConecptDefinitions(section);
		if (conecptDefinitions.size() == 0) {
			Messages.storeMessage(
					null,
					section,
					this.getClass(),
					new Message(
							Message.Type.WARNING,
							"Es ist kein Begriff auf dieser Seite definiert ('Begriff: <Begriffname>'). Damit dieses Markup funktioniert, muss genau ein Begriff auf dieser Seite definiert sein."));
		}
		if (conecptDefinitions.size() > 1) {
			Messages.storeMessage(
					null,
					section,
					this.getClass(),
					new Message(
							Message.Type.WARNING,
							"Es sind mehrere Konzepte ("
									+ verbalizeDefs(conecptDefinitions)
									+ ") auf dieser Seite definiert. Damit dieses Markup funktioniert, darf nur genau ein Begriff auf dieser Seite definiert sein."));
		}
		renderMessageBlock(section, string, Message.Type.ERROR, Message.Type.WARNING);
	}

	/**
	 * 
	 * @created 28.11.2012
	 * @param conecptDefinitions
	 * @return
	 */
	private String verbalizeDefs(List<Section<ConceptMarkup>> conecptDefinitions) {
		List<String> list = new ArrayList<String>();
		for (Section<ConceptMarkup> section : conecptDefinitions) {
			@SuppressWarnings("rawtypes")
			Section<IncrementalTermDefinition> def = Sections.findSuccessor(section,
					IncrementalTermDefinition.class);
			list.add(def.get().getTermName(def));
		}
		return Strings.concat(", ", list);
	}
}
