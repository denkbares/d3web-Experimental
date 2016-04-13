/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.d3web.diaflux.test;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.diaFlux.flow.Flow;


/**
 * 
 * @author Reinhard Hatko
 * @created 12.06.2013
 */
public class DiaFluxDocumentationTest extends DiaFluxTest {

	public DiaFluxDocumentationTest() {
		super("The knowledge base contains {0} flowcharts without documentation:");
	}

	@Override
	public String getDescription() {
		return "Checks, if each flow in a kb has the property 'Description' set.";
	}

	@Override
	protected Collection<Flow> doTest(KnowledgeBase testObject, List<Flow> flows) {

		Collection<Flow> result = new LinkedList<Flow>();

		for (Flow flow : flows) {
			if (!flow.getInfoStore().contains(MMInfo.DESCRIPTION)) {
				result.add(flow);
			}
		}

		return result;
	}

}
