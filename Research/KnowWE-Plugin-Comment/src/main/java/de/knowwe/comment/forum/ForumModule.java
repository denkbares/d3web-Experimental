/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.comment.forum;

import java.util.Locale;
import java.util.ResourceBundle;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.user.UserContext;

public class ForumModule {

	public static ResourceBundle getForumBundle() {

		return ResourceBundle.getBundle("Forum_messages");
	}

	public static ResourceBundle getForumBundle(UserContext user) {

		Locale.setDefault(KnowWEEnvironment.getInstance().getWikiConnector().getLocale(
				user.getRequest()));
		return getForumBundle();

	}
}
