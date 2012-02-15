/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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
 *
 */
package de.knowwe.semantic.semanticFactSheet;

import java.util.Collection;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlHelper;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.PropertyManager;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.core.semantic.UpperOntology;
import de.knowwe.core.contexts.ContextManager;
import de.knowwe.core.contexts.DefaultSubjectContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.xml.XMLContent;

/**
 * @author kazamatzuri
 * 
 */
public class InfoContent extends XMLContent {

	public InfoContent() {
		this.setRenderer(InfoRenderer.getInstance());
		this.addSubtreeHandler(new InfoContentOWLSubTreeHandler());
	}

	private class InfoContentOWLSubTreeHandler extends OwlSubtreeHandler {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section s) {
			IntermediateOwlObject io = new IntermediateOwlObject();
			String text = s.getText();
			PropertyManager pm = PropertyManager.getInstance();
			String subjectconcept = ((DefaultSubjectContext) ContextManager
					.getInstance().getContext(s, DefaultSubjectContext.CID))
					.getSubject();
			for (String cur : text.split("\r\n|\r|\n")) {
				if (cur.trim().length() > 0) {
					String[] spaces = cur.split(" ");
					if (spaces.length > 0) {
						String prop = cur.split(" ")[0].trim();
						boolean valid = pm.isValid(prop);
						if (valid) {
							String value = cur.substring(cur.indexOf(" "),
									cur.length()).trim();
							io.merge(UpperOntology.getInstance().getHelper()
									.createProperty(subjectconcept, prop,
											value, s));
						}
						else {
							io.setValidPropFlag(valid);
							io.setBadAttribute(prop.trim());
							// break at first bad property
							KnowWEUtils.storeObject(article, s, OwlHelper.IOO, io);
							return null;
						}
					}

				}
			}
			KnowWEUtils.storeObject(article, s, OwlHelper.IOO, io);
			SemanticCoreDelegator.getInstance().addStatements(io, s);
			return null;
		}

	}

}
