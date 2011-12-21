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
package de.knowwe.toolbar;

/**
 * This interface provides basic definition of button that can be added to a
 * toolbar and so integrated into the rendered wiki page. Each button has its
 * own action and can be set as a javascript function call. (Inspired by the
 * Tool mechanism of KnowWE, simply adapted for the usage as a toolbar in this
 * plugin).
 * 
 * @author Stefan Mark
 * @created 30.11.2011
 */
public interface ToolbarButton {

	/**
	 * Returns the icon for the button. The icon should have a height and width
	 * of 16px and should be based on a transparent background. There is no need
	 * to specify additional CSS classes for the default layout.
	 *
	 * @return String The path to the icon of the button
	 */
	String getIconPath();

	/**
	 * Returns a title of the button indicating the action of the button. The
	 * title of the button is used in the IMG tag as title and alt attribute.
	 *
	 * @return The toolbar buttons title
	 */
	String getTitle();

	/**
	 * Returns the description of the toolbar button. The description is used in
	 * the HTML A tag of the buttons layout as a simple tooltip.
	 *
	 * @return String The description of the button
	 */
	String getDescription();

	/**
	 * Returns the javascript function that should b executed if the button is
	 * pressed by a user. Note: Quote variables within the function call with
	 * simple opening and closing quotes. Do not use double quotes.
	 *
	 * E.g.: foo('value')
	 *
	 * @return String The javascript action to be executed
	 */
	String getJSAction();
}

