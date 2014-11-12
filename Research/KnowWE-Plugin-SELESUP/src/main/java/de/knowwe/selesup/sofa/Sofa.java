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
package de.knowwe.selesup.sofa;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a resource that can be analyzed in order to extract relevant information.
 * A Sofa (<underline>S</underline>ubject <underline>of</underline> <underline>A</underline>nalysis)
 * could be a structured or unstructured file, provided as wiki attachment or article.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 29.08.14
 */
public interface Sofa {

	/**
	 * Returns the name of this Sofa.
	 * For a wiki article, this should be the article name, for a file the file name.
	 *
	 * @return the name of this Sofa
	 */
	String getName();

	/**
	 * Returns the type of this Sofa. The type can be an arbitrary string, but should be
	 * considered thoroughly, because it is presented in the user interface.
	 *
	 * @return the type of this sofa
	 */
	String getType();

	/**
	 * Returns an input stream, that can be used to read the content of this sofa.
	 *
	 * @return an input stream for this sofa
	 * @throws IOException if the InputStream can not be retrieved.
	 */
	InputStream getInputStream() throws IOException;

}
