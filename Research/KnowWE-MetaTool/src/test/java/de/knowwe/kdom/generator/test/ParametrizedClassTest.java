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

import de.knowwe.kdom.generator.ParametrizedClass;

/**
 *
 * @author sebastian
 * @created Feb 4, 2011
 */
public class ParametrizedClassTest {

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsClass() {
		new ParametrizedClass("de.knowwe", null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsPackage() {
		new ParametrizedClass(null, "TestClass", "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyClass() {
		new ParametrizedClass("de.knowwe", "", "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testEmptyPackage() {
		new ParametrizedClass("", "TestClass", "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullAsValue() {
		new ParametrizedClass("de.knowwe", "TestClass", null);
	}

	@Test
	public void testConstructor() {
		ParametrizedClass test = new ParametrizedClass("de.knowwe.test", "TestClass", "value");
		assertEquals("Wrong package.", "de.knowwe.test", test.getPackageName());
		assertEquals("Wrong class.", "TestClass", test.getClassName());
		assertEquals("Wrong value.", "value", test.getValue());
		assertEquals("Wrong qualified class name",
				"de.knowwe.test.TestClass", test.getQualifiedClassName());
	}

}
