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
import java.util.List;
import java.util.Map;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.dependency.Dependency;
import de.d3web.dependency.DependencyFinder;


/**
 * Test for unused questions, ie questions not used to derive another objects
 * value. If the kb derives output values that are stored in questions, those
 * questions must be ignored.
 * 
 * @author Reinhard Hatko
 * @created 23.01.2013
 */
public class UnusedQuestionTest extends KBObjectsTest {

	public UnusedQuestionTest() {
		super("Knowledge base contains {0} unused questions: ");
	}


	@Override
	protected List<TerminologyObject> doTest(KnowledgeBase kb, List<TerminologyObject> objects, String[] args) {

		checkUsage(kb, objects);

		objects.remove(kb.getRootQASet());

		return objects;
	}

	@Override
	protected List<TerminologyObject> getBaseObjects(KnowledgeBase kb, String[] args) {
		return new ArrayList<TerminologyObject>(kb.getManager().getQuestions());
	}

	/**
	 * 
	 * @created 25.03.2013
	 * @param kb
	 * @param result
	 */
	protected static void checkUsage(KnowledgeBase kb, List<TerminologyObject> result) {

		Map<TerminologyObject, Collection<Dependency>> dependencies = DependencyFinder.getBackwardDependencies(kb);

		// could be optimized to iterate only over objects of same type, eg
		// questions
		for (TerminologyObject o : kb.getManager().getAllTerminologyObjects()) {
			if (dependencies.containsKey(o)) {
				result.remove(o);
			}

		}
	}


	@Override
	public String getDescription() {
		return "This test checks for questions, that are not used to derive another objects value.";
	}

}
