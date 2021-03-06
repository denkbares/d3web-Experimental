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
package de.d3web.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.dependency.Dependency;
import de.d3web.dependency.DependencyFinder;


/**
 * This test checks for questions without derivation. Those questions must be
 * inputs into the kb.
 * 
 * @author Reinhard Hatko
 * @created 25.03.2013
 */
public class UnderivedQuestionTest extends KBObjectsTest {

	public UnderivedQuestionTest() {
		super("Knowledge base contains {0} questions without derivation: ");
	}

	@Override
	public String getDescription() {
		return "This test checks for questions that are not derived by the system. Inputs to the knowledge base should be excluded from this test.";
	}


	@Override
	protected List<TerminologyObject> doTest(KnowledgeBase kb, List<TerminologyObject> objects, String[] args) {

		Map<TerminologyObject, Collection<Dependency>> forward = DependencyFinder.getForwardDependencies(kb);

		List<TerminologyObject> result = new LinkedList<>();

		for (TerminologyObject object : objects) {
			if (!forward.containsKey(object)) {
				result.add(object);
			}

		}

		return result;
	}


	@Override
	protected List<TerminologyObject> getBaseObjects(KnowledgeBase kb, String[] args) {
		return new ArrayList<>(kb.getManager().getQuestions());
	}



}
