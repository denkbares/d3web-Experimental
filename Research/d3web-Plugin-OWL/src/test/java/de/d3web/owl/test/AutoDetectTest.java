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
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.owl.OWLAutoDetect;
import de.d3web.plugin.test.InitPluginManager;

/**
 * Test for the OWLAutoDetect class.
 *
 * @see OWLAutoDetect
 * @author Sebastian Furth
 * @created Mar 8, 2011
 */
public class AutoDetectTest {

	@Test
	public void testKBWithoutOntology() {
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		OWLAutoDetect autoDetect = new OWLAutoDetect();
		assertEquals("AutoDetect should return false.", false, autoDetect.check(kb));
	}

	@Test
	public void testKBWithOntology() throws IOException {
		InitPluginManager.init();
		File kbFile = new File("src/test/resources/knowledgebases/car.d3web");
		KnowledgeBase kb = PersistenceManager.getInstance().load(kbFile);
		OWLAutoDetect autoDetect = new OWLAutoDetect();
		assertEquals("AutoDetect should return true.", true, autoDetect.check(kb));
	}

}
