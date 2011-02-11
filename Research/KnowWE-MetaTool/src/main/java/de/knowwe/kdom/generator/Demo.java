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
package de.knowwe.kdom.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import de.knowwe.kdom.generator.io.JavaWriter;
import de.knowwe.kdom.generator.io.XMLReader;

/**
 *
 * @author Sebastian Furth
 * @created Jan 25, 2011
 */
public class Demo {

	private static final String outputDir = "/Users/sebastian/Desktop/";
	private static final String inputFile = "src/main/resources/examples/TurtleMarkupSimple.xml";

	public static void main(String[] args) throws IOException {

		ObjectType temp = read();
		write(temp);
		write(createObjectType());
	}

	private static ObjectType read() throws IOException {
		XMLReader reader = new XMLReader();
		return reader.read(new File(inputFile));
	}

	private static void write(ObjectType objectType) throws IOException {

		// Write the object type
		if (!objectType.alreadyExists()) {
			Writer w = new FileWriter(outputDir + objectType.getClassName() + ".java");
			JavaWriter.getInstance().write(objectType, w);
		}

		// Write all children
		for (ObjectType child : objectType.getChildren()) {
			write(child);
		}
	}

	private static ObjectType createObjectType() {

		// Child
		QualifiedClass childClass1 = new QualifiedClass("de.knowwe.kdom", "TestChildren1");
		ObjectType child1 = new ObjectType.Builder("02", childClass1, false).build();

		// Child
		QualifiedClass childClass2 = new QualifiedClass("de.knowwe.kdom", "TestChildren2");
		ObjectType child2 = new ObjectType.Builder("03", childClass2, true).build();

		// Child
		QualifiedClass childClass3 = new QualifiedClass("de.knowwe.kdom", "TestChildren3");
		ObjectType child3 = new ObjectType.Builder("04", childClass3, false).build();

		// Parent
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");

		// Parent's custom super class
		QualifiedClass superType = new QualifiedClass("de.d3web.we.kdom.objects", "TermDefinition");

		// Parent's section finder
		ParameterizedClass sectionFinder = new ParameterizedClass("de.d3web.we.kdom.sectionFinder",
				"RegexSectionFinder", "\".*\"");

		// Parent's contraint
		QualifiedClass constraint = new QualifiedClass("de.d3web.we.kdom.constraint",
				"AtMostOneFindingConstraint");

		// Creation of ObjectType
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false)
												.setSuperType(superType)
												.setSectionFinder(sectionFinder)
												.addConstraint(constraint)
												.build();

		objectType.addChild(0, child1);
		objectType.addChild(1, child2);
		objectType.addChild(2, child3);

		return objectType;
	}



}
