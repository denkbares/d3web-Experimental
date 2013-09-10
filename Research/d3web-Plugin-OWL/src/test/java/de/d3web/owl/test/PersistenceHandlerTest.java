/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.owl.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.d3web.core.io.PersistenceManager;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.owl.assignment.AssignmentSet;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Tests the Reader of the @link{AssignmentPersistenceHandler}.
 *
 * @author Sebastian Furth
 * @created Apr 5, 2011
 */
public class PersistenceHandlerTest {

	@Test
	public void testAssignmentSetSize() throws IOException {
		InitPluginManager.init();
		File kbFile = new File("src/test/resources/knowledgebases/car.d3web");
		KnowledgeBase kb = PersistenceManager.getInstance().load(kbFile);
		AssignmentSet set = kb.getKnowledgeStore().getKnowledge(AssignmentSet.KNOWLEDGE_KIND);
		assertEquals("Wrong amount of assignments", 3, set.getAssignments().size());
	}

}
