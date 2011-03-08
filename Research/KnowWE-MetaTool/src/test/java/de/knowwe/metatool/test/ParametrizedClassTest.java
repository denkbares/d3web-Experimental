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

import org.junit.Test;

import de.knowwe.metatool.ParameterizedClass;

/**
 *
 * @author sebastian
 * @created Feb 4, 2011
 */
public class ParametrizedClassTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsClass() {
		new ParameterizedClass("de.knowwe", null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsPackage() {
		new ParameterizedClass(null, "TestClass", "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyClass() {
		new ParameterizedClass("de.knowwe", "", "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyPackage() {
		new ParameterizedClass("", "TestClass", "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsValue() {
		new ParameterizedClass("de.knowwe", "TestClass", null);
	}

	@Test
	public void testInstantiationString() {
		ParameterizedClass test = new ParameterizedClass("de.knowwe.test", "TestClass", "\"value\"");
		assertEquals("Wrong instantiation.", "new TestClass(\"value\")",
				test.getInstantiationString());
	}

	@Test
	public void testQualifiedClassString() {
		ParameterizedClass test = new ParameterizedClass("de.knowwe.test", "TestClass", "\"value\"");
		assertEquals("Wrong qualified class name",
				"de.knowwe.test.TestClass", test.getQualifiedClassName());
	}

	@Test
	public void testConstructor() {
		ParameterizedClass test = new ParameterizedClass("de.knowwe.test", "TestClass", "\"value\"");
		assertEquals("Wrong package.", "de.knowwe.test", test.getPackageName());
		assertEquals("Wrong class.", "TestClass", test.getClassName());
		assertEquals("Wrong value.", "\"value\"", test.getValue());
	}

}
