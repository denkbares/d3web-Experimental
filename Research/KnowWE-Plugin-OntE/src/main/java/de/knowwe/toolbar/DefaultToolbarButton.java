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
 * This is a default implementation of a {@link ToolbarButton}. For more
 * information about a button see {@link ToolbarButton}.
 *
 * @author Stefan Mark.
 * @created 04.12.2011
 */
public class DefaultToolbarButton implements ToolbarButton {

	/**
	 * Stores the icon path of the button.
	 */
	private final String iconPath;

	/**
	 * Stores the title of the button.
	 */
	private final String title;

	/**
	 * Stores the description of the button.
	 */
	private final String description;

	/**
	 * Stores the javascript method to be executed.
	 */
	private final String jsAction;

	public DefaultToolbarButton(String iconPath, String title, String description, String jsAction) {
		this.iconPath = iconPath;
		this.title = title;
		this.description = description;
		this.jsAction = jsAction;
	}

	@Override
	public String getIconPath() {
		return iconPath;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getJSAction() {
		return jsAction;
	}
}
