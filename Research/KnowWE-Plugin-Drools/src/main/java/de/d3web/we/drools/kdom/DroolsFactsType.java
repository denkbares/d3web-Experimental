/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.drools.kdom;

import java.util.Collection;
import java.util.Map;

import de.d3web.we.drools.terminology.DroolsKnowledgeHandler;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.basicType.CommentLineType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.KDOMReportMessage;

/**
 * Content type for the DroolsFacts section.
 * Can contain Comments or DroolsFacts
 * 
 * @author Alex Legler, Sebastian Furth
 */
public class DroolsFactsType extends AbstractType {
		
	public DroolsFactsType() {
		setSectionFinder(new AllTextSectionFinder());
		childrenTypes.add(new CommentLineType());
		childrenTypes.add(new DroolsFact());
		addSubtreeHandler(Priority.DEFAULT, new DroolsFactsSubtreeHandler());;
	}
		
	static class DroolsFactsSubtreeHandler extends SubtreeHandler<DroolsFactsType> {
		
		/*
		 * For testing purposes only, can be removed!
		 * (non-Javadoc)
		 * @see de.d3web.we.kdom.ReviseSubTreeHandler#reviseSubtree(de.d3web.we.kdom.KnowWEArticle, de.d3web.we.kdom.Section)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {
									
			// try to load factsStore
			Map<String, Object> factsStore = DroolsKnowledgeHandler.getInstance().getFactsStore(
					article.getTitle());
						
			// Only for testing purposes
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
			System.out.println("FactsStore of Article \"" + article.getTitle() + "\" contains " + factsStore.size() + " facts.");
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
				
			return null;
		}


		
	}
}
