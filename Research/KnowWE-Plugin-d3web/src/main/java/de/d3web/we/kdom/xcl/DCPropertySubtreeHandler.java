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

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;

/**
 * A section for storing DCProperties in a MMInfo. The storing could be generic,
 * but then where to get the NamedObject from, to store the info in?!?!
 * 
 * ATM this class is creating the diagnosis, due to the execution order of
 * subtreehandlers. So take care the right SolutionContext is set in the
 * subtreehandler of XCLHead.
 * 
 * @author Reinhard Hatko Created on: 03.12.2009
 */
public class DCPropertySubtreeHandler extends D3webSubtreeHandler<DCPropertyType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {

		KnowledgeBaseManagement kbm = getKBM(article);

		if (kbm == null) return null;

		TerminologyObject obj = getNamedObject(s, kbm);

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
	private void storeMMInfo(Section s, TerminologyObject obj) {

		Property<Object> untypedProperty = Property.getUntypedProperty(s.findChildOfType(
				DCPropertyNameType.class).getOriginalText().toLowerCase());

		obj.getInfoStore().addValue(untypedProperty,
				s.findChildOfType(DCPropertyContentType.class).getOriginalText());

	}

	/**
	 * Looks for the NamedObject. ATM this is tailored to work in XCLs. this is
	 * the part which would have to be adapted to other scenarios
	 * 
	 */
	private TerminologyObject getNamedObject(Section s, KnowledgeBaseManagement kbm) {
		Section xclhead = s.findAncestorOfType(XCList.class);

		String diagnosis = (String) KnowWEUtils.getStoredObject(xclhead, XCLHead.KEY_SOLUTION_NAME);

		if (diagnosis == null) {
			return null;
		}

		TerminologyObject d = kbm.findSolution(diagnosis);

		if (d == null) { // should not happen
			// solution should already be created by STH of XCLHEAD
			// as this one has lower priority
			d = kbm.createSolution(diagnosis);
		}
		return d;
	}

}
