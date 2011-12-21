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

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.user.UserContext;

/**
 *
 * @author Stefan Mark
 * @created 30.11.2011
 */
public interface ToolbarButtonProvider {

	/**
	 * Returns the button depending on the current {@link UserContext} and
	 * {@link KnowWEArticle}.
	 *
	 * @param KnowWEArticle article The article the button is requested for
	 * @param UserContext userContext The user's context the button is requested
	 *        for
	 * @return ToolbarButton The button that can be provided by this provider
	 */
	ToolbarButton[] getButtons(KnowWEArticle article, UserContext userContext);
}
