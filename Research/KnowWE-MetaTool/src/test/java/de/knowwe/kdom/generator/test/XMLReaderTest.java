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
package de.knowwe.kdom.generator.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.knowwe.kdom.generator.ObjectType;
import de.knowwe.kdom.generator.QualifiedClass;
import de.knowwe.kdom.generator.io.XMLReader;

/**
 *
 * @author Sebastian Furth
 * @created Jan 31, 2011
 */
public class XMLReaderTest {

	private static final String TESTFILE = "src/test/resources/TestTypes.xml";

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsFile() throws IOException {
		XMLReader reader = new XMLReader();
		reader.read(null);
	}

	@Test
	public void testGeneratedObjectTypes() throws IOException {
		XMLReader reader = new XMLReader();
		ObjectType actual = reader.read(new File(TESTFILE));
		ObjectType expected = getObjectType();
		testObjectType(expected, actual);
	}

	private void testObjectType(ObjectType expected, ObjectType actual) {
		// Mandatory attributes
		assertEquals("Wrong ID.", expected.getID(), actual.getID());
		assertEquals("Wrong Class.", expected.getClassName(), actual.getClassName());
		assertEquals("Wrong Package.", expected.getPackageName(), actual.getPackageName());

		// Default Attributes
		assertEquals("SuperType has wrong Class.", expected.getSuperType().getClassName(),
				actual.getSuperType().getClassName());
		assertEquals("SuperType has wrong package.", expected.getSuperType().getPackageName(),
				actual.getSuperType().getPackageName());

		// Test Children
		assertEquals("Wrong number of children.", expected.getChildren().size(),
				actual.getChildren().size());
		for (int i = 0; i < actual.getChildren().size(); i++) {
			testObjectType(expected.getChildren().get(i), actual.getChildren().get(i));
		}
	}

	private ObjectType getObjectType() {

		QualifiedClass childClass1 = new QualifiedClass("de.knowwe.kdom", "TestChildren1");
		ObjectType child1 = new ObjectType.Builder("B0", childClass1, true).build();

		QualifiedClass childClass2 = new QualifiedClass("de.knowwe.kdom", "TestChildren2");
		ObjectType child2 = new ObjectType.Builder("B1", childClass2, true).build();

		QualifiedClass childClass3 = new QualifiedClass("de.knowwe.kdom", "TestChildren3");
		ObjectType child3 = new ObjectType.Builder("B2", childClass3, true).build();

		QualifiedClass superType = new QualifiedClass("de.d3web.we.kdom.objects", "TermDefinition");

		QualifiedClass actualClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType actual = new ObjectType.Builder("A0", actualClass, false)
												.setSuperType(superType)
												.build();

		actual.addChild(0, child1);
		actual.addChild(1, child2);
		actual.addChild(2, child3);

		return actual;
	}

}
