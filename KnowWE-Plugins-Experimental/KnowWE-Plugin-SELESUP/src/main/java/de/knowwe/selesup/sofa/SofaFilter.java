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

/**
 * The SofaFilter interface is used by the {@link de.knowwe.selesup.DataProvider}s to restrict the {@link Sofa}s
 * they can handle, e.g. by restricting the type of Sofas to a specific MIME type.
 *
 * @author Sebastian Furth (denkbares GmbH)
 * @created 01.09.14
 */
public interface SofaFilter {

	/**
	 * Returns true if the SofaFilter accepts the specified {@link Sofa}, false otherwise.
	 *
	 * @param sofa the {@link Sofa} object that is filtered
	 * @return true if accepted, false otherwise
	 */
	boolean accept(Sofa sofa);

}
