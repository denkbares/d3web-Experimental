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
package de.knowwe.metatool.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.Test;

import de.knowwe.metatool.ObjectType;
import de.knowwe.metatool.ParameterizedClass;
import de.knowwe.metatool.QualifiedClass;
import de.knowwe.metatool.io.JavaWriter;

/**
 *
 * @author Sebastian Furth
 * @created Jan 25, 2011
 */
public class JavaWriterTest {

	private static final String EXPECTEDSIMPLEFILE = "src/test/resources/TestType.java";
	private static final String EXPECTEDCONSTRAINTFILE = "src/test/resources/TestTypeWithConstraint.java";

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsObjectType() throws IOException {
		JavaWriter.getInstance().write(null, new StringWriter());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsWriter() throws IOException {
		JavaWriter.getInstance().write(getSimpleObjectType(), null);
	}

	@Test
	public void testGeneratedSimpleFile() throws IOException {
		String expected = loadExpectedFile(EXPECTEDSIMPLEFILE);
		StringWriter w = new StringWriter();
		ObjectType type = getSimpleObjectType();
		JavaWriter.getInstance().write(type, w);
		assertEquals("Generated Java File differs.", expected, w.toString());
	}

	@Test
	public void testGeneratedConstraintFile() throws IOException {
		String expected = loadExpectedFile(EXPECTEDCONSTRAINTFILE);
		StringWriter w = new StringWriter();
		ObjectType type = getObjectTypeWithConstraint();
		JavaWriter.getInstance().write(type, w);
		assertEquals("Generated Java File differs.", expected, w.toString());
	}

	private String loadExpectedFile(String file) throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), "UTF-8"));
		int currentChar = bufferedReader.read();
		while (currentChar != -1) {
			content.append((char) currentChar);
			currentChar = bufferedReader.read();
		}
		bufferedReader.close();
		return content.toString();
	}

	private ObjectType getSimpleObjectType() {

		QualifiedClass childClass1 = new QualifiedClass("de.knowwe.kdom", "TestChildren1");
		ObjectType child1 = new ObjectType.Builder("02", childClass1, true).build();

		QualifiedClass childClass2 = new QualifiedClass("de.knowwe.kdom", "TestChildren2");
		ObjectType child2 = new ObjectType.Builder("03", childClass2, true).build();

		QualifiedClass childClass3 = new QualifiedClass("de.knowwe.kdom", "TestChildren3");
		ObjectType child3 = new ObjectType.Builder("04", childClass3, true).build();

		QualifiedClass superType = new QualifiedClass("de.knowwe.core.kdom.objects", "TermDefinition");

		ParameterizedClass sectionFinder = new ParameterizedClass("de.knowwe.core.kdom.sectionFinder",
				"RegexSectionFinder", "\".*\"");

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false)
												.setSuperType(superType)
												.setSectionFinder(sectionFinder)
												.setColor("red")
												.build();

		objectType.addChild(0, child1);
		objectType.addChild(1, child2);
		objectType.addChild(2, child3);

		return objectType;
	}

	private ObjectType getObjectTypeWithConstraint() {

		QualifiedClass childClass1 = new QualifiedClass("de.knowwe.kdom", "TestChildren1");
		ObjectType child1 = new ObjectType.Builder("02", childClass1, true).build();

		QualifiedClass childClass2 = new QualifiedClass("de.knowwe.kdom", "TestChildren2");
		ObjectType child2 = new ObjectType.Builder("03", childClass2, true).build();

		QualifiedClass childClass3 = new QualifiedClass("de.knowwe.kdom", "TestChildren3");
		ObjectType child3 = new ObjectType.Builder("04", childClass3, true).build();

		QualifiedClass superType = new QualifiedClass("de.knowwe.core.kdom.objects", "TermDefinition");

		ParameterizedClass sectionFinder = new ParameterizedClass("de.knowwe.core.kdom.sectionFinder",
				"RegexSectionFinder", "\".*\"");

		QualifiedClass constraint = new QualifiedClass("de.knowwe.kdom.constraint",
				"AtMostOneFindingConstraint");

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom",
				"TestTypeWithConstraint");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false)
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
