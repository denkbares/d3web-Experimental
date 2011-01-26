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

import de.knowwe.kdom.generator.QualifiedClass;


/**
 *
 * @author Sebastian Furth
 * @created Jan 25, 2011
 */
public class QualifiedClassTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsClass() {
		new QualifiedClass("de.knowwe", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsPackage() {
		new QualifiedClass(null, "TestClass");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyClass() {
		new QualifiedClass("de.knowwe", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyPackage() {
		new QualifiedClass("", "TestClass");
	}

	@Test
	public void testConstructor() {
		QualifiedClass test = new QualifiedClass("de.knowwe.test", "TestClass");
		assertEquals("Wrong package.", test.getPackageName(), "de.knowwe.test");
		assertEquals("Wrong class.", test.getClassName(), "TestClass");
		assertEquals("Wrong qualified class name", test.getQualifiedClassName(),
				"de.knowwe.test.TestClass");
	}

}
