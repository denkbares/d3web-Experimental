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
package de.d3web.we.kdom.xcl;

import java.util.ArrayList;
import java.util.Collection;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.report.message.NoSuchObjectError;

/**
 * A section for storing DCProperties in a MMInfo. The storing could be generic,
 * but then where to get the TerminologyObject from, to store the info in?!?!
 * 
 * ATM this class is creating the diagnosis, due to the execution order of
 * subtreehandlers. So take care the right SolutionContext is set in the
 * subtreehandler of XCLHead.
 * 
 * @author Reinhard Hatko Created on: 03.12.2009
 */
public class DCPropertySubtreeHandler extends D3webSubtreeHandler<DCPropertyType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<DCPropertyType> s) {

		KnowledgeBase kb = getKB(article);

		if (kb == null) return null;

		TerminologyObject obj = getNamedObject(s, kb);

		if (obj == null) {
			ArrayList<KDOMReportMessage> list = new ArrayList<KDOMReportMessage>();
			list.add(new NoSuchObjectError("Could not find name of solution in XCList."));

			return list;
		}
		storeMMInfo(s, obj);

		return null;

	}

	/**
	 * Stores the content of the section into the NamedObjects MMInfoStore
	 * 
	 */
	private void storeMMInfo(Section<DCPropertyType> s, TerminologyObject obj) {

		Property<Object> untypedProperty = Property.getUntypedProperty(Sections.findChildOfType(
				s, DCPropertyNameType.class).getOriginalText().toLowerCase());

		obj.getInfoStore().addValue(untypedProperty,
				Sections.findChildOfType(s, DCPropertyContentType.class).getOriginalText());

	}

	/**
	 * Looks for the TerminologyObject. ATM this is tailored to work in XCLs.
	 * this is the part which would have to be adapted to other scenarios
	 * 
	 */
	private TerminologyObject getNamedObject(Section<?> s, KnowledgeBase kb) {
		Section xclhead = Sections.findAncestorOfType(s, XCList.class);

		String diagnosis = (String) KnowWEUtils.getStoredObject(xclhead, XCLHead.KEY_SOLUTION_NAME);

		if (diagnosis == null) {
			return null;
		}

		TerminologyObject d = kb.getManager().searchSolution(diagnosis);

		if (d == null) { // should not happen
			// solution should already be created by STH of XCLHEAD
			// as this one has lower priority
			d = new Solution(kb.getRootSolution(), diagnosis);
		}
		return d;
	}

}
