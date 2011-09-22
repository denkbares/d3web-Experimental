/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.kdom.manchester.types;

import de.d3web.we.kdom.AbstractType;

/**
 * This is the main class for all lists in context of the manchster syntax.
 * Because comma-separated lists occur in very many places in the syntax, to
 * save space the grammar has three meta-productions, one for non-empty lists,
 * one for lists of minimum length two, and one for non-empty lists with
 * annotations in them.
 *
 * @author Stefan Mark
 * @created 19.09.2011
 */
public class NonEmptyList extends AbstractType {


	public static final String PATTERN = "(" +
			"[^\"]" + // everything not in quotes
			"|" +
			"\"[^\"]*\"" + // if in quotes everything that is not a quote
			")*?" +
			"(,|\\z)"; // till comma or line end

	public NonEmptyList() {

	}
}
