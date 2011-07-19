/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.metatool.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.knowwe.metatool.ObjectType;
import de.knowwe.metatool.ParameterizedClass;
import de.knowwe.metatool.QualifiedClass;
import de.knowwe.metatool.io.XMLReader;

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
		reader.read((File) null);
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

		// Section Finder
		if (expected.getSectionFinder() != null) {
			assertEquals("SectionFinder has wrong Class.", expected.getSectionFinder().getClass(),
					actual.getSectionFinder().getClass());
			assertEquals("SectionFinder has wrong package.",
					expected.getSectionFinder().getPackageName(),
					actual.getSectionFinder().getPackageName());
			assertEquals("SectionFinder has wrong value.", expected.getSectionFinder().getValue(),
					actual.getSectionFinder().getValue());
		}
		else {
			assertNull("SectionFinder should be null", actual.getSectionFinder());
		}

		// Constraints
		assertEquals("Wrong number of constraints", expected.getConstraints().size(),
				actual.getConstraints().size());
		for (int j = 0; j < actual.getConstraints().size(); j++) {
			assertEquals(expected.getConstraints().get(j), actual.getConstraints().get(j));
		}

		// Color
		if (expected.getColor() != null) {
			assertEquals("Wrong color", expected.getColor(), actual.getColor());
		}
		else {
			assertNull("Color should be null", actual.getColor());
		}

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
		ParameterizedClass sectionFinder = new ParameterizedClass("de.d3web.we.kdom.sectionFinder",
				"RegexSectionFinder", "\".*\"");

		QualifiedClass constraint = new QualifiedClass("de.d3web.we.kdom.constraint",
				"AtMostOneFindingConstraint");

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("A0", objectTypeClass, false)
												.setSuperType(superType)
												.setSectionFinder(sectionFinder)
												.addConstraint(constraint)
												.setColor("red")
												.build();

		objectType.addChild(0, child1);
		objectType.addChild(1, child2);
		objectType.addChild(2, child3);

		return objectType;
	}

}
