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
package de.knowwe.esat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.we.object.SolutionDefinition;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * 
 * @author jochenreutelshofer
 * @created 18.11.2012
 */
public class CreateMonitorKnowledgeHandler extends D3webSubtreeHandler<MonitorMarkupContentType> {

	@Override
	public Collection<Message> create(Article article, Section<MonitorMarkupContentType> section) {

		Section<DefaultMarkupType> defaultMarkup = Sections.findAncestorOfType(section,
				DefaultMarkupType.class);

		// set solution description
		String solutionDescription = MonitorMarkup.getAnnotation(defaultMarkup, MonitorMarkup.NAME);

		Section<SolutionDefinition> solutionDef = Sections.findSuccessor(section,
				SolutionDefinition.class);
		Solution solution = solutionDef.get().getTermObject(article, solutionDef);
		solution.getInfoStore().addValue(MMInfo.DESCRIPTION, Locale.GERMAN,
				solutionDescription);

		// set link
		String link = MonitorMarkup.getAnnotation(defaultMarkup, MonitorMarkup.LINK);
		solution.getInfoStore().addValue(MMInfo.LINK, null,
				link);

		// add square size
		String widthString = MonitorMarkup.getAnnotation(defaultMarkup, MonitorMarkup.BREITE);
		String heightString = MonitorMarkup.getAnnotation(defaultMarkup, MonitorMarkup.HOEHE);
		try {
			double width = Double.parseDouble(widthString);
			double height = Double.parseDouble(heightString);

			// set width
			NamedObject qWidth = solution.getKnowledgeBase().getManager().search(
					solution.getName() + "_width");
			qWidth.getInfoStore().addValue(BasicProperties.INIT, "" + width);

			// set height
			NamedObject qHeight = solution.getKnowledgeBase().getManager().search(
					solution.getName() + "_height");
			qHeight.getInfoStore().addValue(BasicProperties.INIT, "" + height);

			// set area
			NamedObject qSquare = solution.getKnowledgeBase().getManager().search(
					solution.getName() + "_a");
			qSquare.getInfoStore().addValue(BasicProperties.INIT, "" + width * height);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return new ArrayList<Message>(0);
	}

}
