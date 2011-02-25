/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.data;

/**
 * The possible styles a dialog can implement
 * 
 * @author Martina Freiberg
 * @created 08.10.2010 TODO think about correct and sufficient types
 */
public enum DialogType {
	/*
	 * All questionnaires on one page, only corresponding highlighting e.g. the
	 * Rheuma dialog, but also the Kneipen dialog
	 */
	SINGLEFORM,

	/*
	 * Questionnaires distributed over several pages e.g., Standarddialog,
	 * SonoConsult...
	 */
	MULTIFORM,

	/* Hierarchical style dialogs, e.g. Labour Legislation */
	HIERARCHICAL,

	/* Pub consultation dialog or medical diagnosis clarification */
	CLARIFICATION,

	/* e.g. QuickI dialog */
	FREECHOICE,

	/* A default value */
	DEFAULT;
}
