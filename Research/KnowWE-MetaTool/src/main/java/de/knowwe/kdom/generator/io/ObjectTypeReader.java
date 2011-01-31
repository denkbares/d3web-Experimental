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
package de.knowwe.kdom.generator.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.knowwe.kdom.generator.ObjectType;


/**
 * Interface for reading ObjectTypes
 *
 * @author Sebastian Furth
 * @created Jan 31, 2011
 */
public interface ObjectTypeReader {

	/**
	 * Reads ObjectTypes from a File. The returned ObjectType is the
	 * "Root"-Element. All other ObjectTypes contained in the file have to be
	 * successors of this ObjectType.
	 *
	 * @created Jan 31, 2011
	 * @param input File which contains the ObjectTypes
	 * @return "Root"-ObjectType
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	ObjectType read(File input) throws IOException;

}
