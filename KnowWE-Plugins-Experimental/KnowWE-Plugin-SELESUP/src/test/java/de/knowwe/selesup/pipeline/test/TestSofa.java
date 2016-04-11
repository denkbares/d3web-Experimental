/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.selesup.pipeline.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import de.knowwe.selesup.sofa.Sofa;

/**
 * Dummy implementation of the {@link Sofa} interface for test purposes.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 12.11.14
 */
public class TestSofa implements Sofa {

	private final String text;

	public TestSofa(String text) {
		this.text = text;
	}

	@Override
	public String getName() {
		return Long.toString(System.currentTimeMillis());
	}

	@Override
	public String getType() {
		return getClass().getSimpleName();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
	}
}
