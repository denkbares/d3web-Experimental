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

import org.junit.Test;

import de.knowwe.kdom.generator.ObjectType;
import de.knowwe.kdom.generator.ParameterizedClass;
import de.knowwe.kdom.generator.QualifiedClass;

/**
 *
 * @author Sebastian Furth
 * @created Jan 21, 2011
 */
public class ObjectTypeCreationTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsID() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		new ObjectType.Builder(null, objectTypeClass, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyID() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		new ObjectType.Builder("", objectTypeClass, false);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyClass() {
		new ObjectType.Builder("01", null, false);
	}

	@Test
	public void testDefaultObject() {

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false).build();

		// Mandatory attributes
		assertEquals("Wrong ID.", "01", objectType.getID());
		assertEquals("Wrong Class.", "TestType", objectType.getClassName());
		assertEquals("Wrong Package.", "de.knowwe.kdom", objectType.getPackageName());

		// Default Attributes
		assertEquals("SuperType has wrong Class.", "DefaultAbstractKnowWEObjectType",
				objectType.getSuperType().getClassName());
		assertEquals("SuperType has wrong package", "de.d3web.we.kdom",
				objectType.getSuperType().getPackageName());
		assertEquals("Wrong number of children.", 0, objectType.getChildren().size());
	}

	@Test
	public void testDifferentSuperType() {

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		QualifiedClass superType = new QualifiedClass("de.d3web.we.kdom.objects", "TermDefinition");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false)
												.setSuperType(superType)
												.build();

		// Mandatory attributes
		assertEquals("Wrong ID.", "01", objectType.getID());
		assertEquals("Wrong Class.", "TestType", objectType.getClassName());
		assertEquals("Wrong Package.", "de.knowwe.kdom", objectType.getPackageName());

		// Custom SuperType
		assertEquals("SuperType has wrong Class.", "TermDefinition",
				objectType.getSuperType().getClassName());
		assertEquals("SuperType has wrong package", "de.d3web.we.kdom.objects",
				objectType.getSuperType().getPackageName());

		// Default Attribute
		assertEquals("Wrong number of children.", 0, objectType.getChildren().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsSuperType() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		new ObjectType.Builder("01", objectTypeClass, false).setSuperType(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testChangeSuperTypeOnExisting() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		QualifiedClass superType = new QualifiedClass("de.d3web.we.kdom.objects", "TermDefinition");
		new ObjectType.Builder("01", objectTypeClass, true).setSuperType(superType).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsSectionFinder() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		new ObjectType.Builder("01", objectTypeClass, false).setSectionFinder(null).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetSectionFinderOnExisting() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ParameterizedClass sectionFinder = new ParameterizedClass("de.d3web.we.kdom.sectionFinder",
				"RegExSectionFinder", ".*");
		new ObjectType.Builder("01", objectTypeClass, true).setSectionFinder(sectionFinder).build();
	}

	@Test
	public void testSectionFinder() {

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ParameterizedClass sectionFinder = new ParameterizedClass("de.d3web.we.kdom.sectionFinder",
				"RegExSectionFinder", ".*");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false)
												.setSectionFinder(sectionFinder)
												.build();

		// Mandatory attributes
		assertEquals("Wrong ID.", "01", objectType.getID());
		assertEquals("Wrong Class.", "TestType", objectType.getClassName());
		assertEquals("Wrong Package.", "de.knowwe.kdom", objectType.getPackageName());

		// Custom SectionFinder
		assertEquals("SectionFinder has wrong Class.", "RegExSectionFinder",
				objectType.getSectionFinder().getClassName());
		assertEquals("SectionFinder has wrong package", "de.d3web.we.kdom.sectionFinder",
				objectType.getSectionFinder().getPackageName());
		assertEquals("SectionFinder has wrong value", ".*",
				objectType.getSectionFinder().getValue());

		// Default Attribute
		assertEquals("Wrong number of children.", 0, objectType.getChildren().size());
	}

	@Test
	public void testDefaultObjectWithChildren() {

		QualifiedClass childClass1 = new QualifiedClass("de.knowwe.kdom", "TestChildren1");
		ObjectType child1 = new ObjectType.Builder("02", childClass1, false).build();

		QualifiedClass childClass2 = new QualifiedClass("de.knowwe.kdom", "TestChildren2");
		ObjectType child2 = new ObjectType.Builder("03", childClass2, false).build();

		QualifiedClass childClass3 = new QualifiedClass("de.knowwe.kdom", "TestChildren3");
		ObjectType child3 = new ObjectType.Builder("04", childClass3, false).build();

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false).build();

		objectType.addChild(0, child1);
		objectType.addChild(1, child2);
		objectType.addChild(2, child3);

		// Mandatory attributes
		assertEquals("Wrong ID.", "01", objectType.getID());
		assertEquals("Wrong Class.", "TestType", objectType.getClassName());
		assertEquals("Wrong Package.", "de.knowwe.kdom", objectType.getPackageName());

		// Default Attributes
		assertEquals("SuperType has wrong Class.", "DefaultAbstractKnowWEObjectType",
				objectType.getSuperType().getClassName());
		assertEquals("SuperType has wrong package", "de.d3web.we.kdom",
				objectType.getSuperType().getPackageName());

		// Test Children
		assertEquals("Wrong number of children.", 3, objectType.getChildren().size());
		assertEquals("Wrong child", child1, objectType.getChildren().get(0));
		assertEquals("Wrong child", child2, objectType.getChildren().get(1));
		assertEquals("Wrong child", child3, objectType.getChildren().get(2));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsChild() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false).build();
		objectType.addChild(0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidChildPosition1() {

		QualifiedClass childClass = new QualifiedClass("de.knowwe.kdom", "TestChildren");
		ObjectType child = new ObjectType.Builder("02", childClass, false).build();

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false).build();
		objectType.addChild(-1, child);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidChildPosition2() {
		QualifiedClass childClass = new QualifiedClass("de.knowwe.kdom", "TestChildren");
		ObjectType child = new ObjectType.Builder("02", childClass, false).build();

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false).build();
		objectType.addChild(1, child);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetConstraintOnExisting() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		QualifiedClass constraint = new QualifiedClass("de.d3web.we.kdom.constraint",
				"AtMostOneFindingConstraint");
		new ObjectType.Builder("01", objectTypeClass, true).addConstraint(constraint).build();
	}

	@Test(expected = IllegalStateException.class)
	public void testSetConstraintBeforeSectionFinder() {
		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		QualifiedClass constraint = new QualifiedClass("de.d3web.we.kdom.constraint",
				"AtMostOneFindingConstraint");
		new ObjectType.Builder("01", objectTypeClass, false).addConstraint(constraint).build();
	}

	@Test
	public void testConstraint() {

		QualifiedClass objectTypeClass = new QualifiedClass("de.knowwe.kdom", "TestType");
		ParameterizedClass sectionFinder = new ParameterizedClass("de.d3web.we.kdom.sectionFinder",
				"RegExSectionFinder", "\".*\"");
		QualifiedClass constraint = new QualifiedClass("de.d3web.we.kdom.constraint",
				"AtMostOneFindingConstraint");
		ObjectType objectType = new ObjectType.Builder("01", objectTypeClass, false)
												.setSectionFinder(sectionFinder)
												.addConstraint(constraint)
												.build();

		// Mandatory attributes
		assertEquals("Wrong ID.", "01", objectType.getID());
		assertEquals("Wrong Class.", "TestType", objectType.getClassName());
		assertEquals("Wrong Package.", "de.knowwe.kdom", objectType.getPackageName());

		// Custom SectionFinder
		assertEquals("SectionFinder has wrong Class.", "ConstraintSectionFinder",
				objectType.getSectionFinder().getClassName());
		assertEquals("SectionFinder has wrong package", "de.d3web.we.kdom.constraint",
				objectType.getSectionFinder().getPackageName());
		assertEquals("SectionFinder has wrong value", "new RegExSectionFinder(\".*\")",
				objectType.getSectionFinder().getValue());

		// Constraints
		assertEquals("Wrong number of constraints.", 1, objectType.getConstraints().size());
		assertEquals("Constraint has wrong Class.", "AtMostOneFindingConstraint",
				objectType.getConstraints().get(0).getClassName());
		assertEquals("Constraint has wrong package", "de.d3web.we.kdom.constraint",
				objectType.getConstraints().get(0).getPackageName());

		// Default Attribute
		assertEquals("Wrong number of children.", 0, objectType.getChildren().size());
	}


}
