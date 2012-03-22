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
package de.knowwe.usersupport.correction;

import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.user.UserContext;


/**
 * This append handler is a placebo (read: it does nothing) for loading
 * JavaScript and CSS for the TermReference correction functionality
 * via plugin.xml.
 *
 * @author Alex Legler
 * @created 20.11.2010
 */
public class ApproximateCorrectionAppendHandler implements PageAppendHandler {

	@Override
	public String getDataToAppend(String topic, String web, UserContext user) {
		return "";
	}

	@Override
	public boolean isPre() {
		return false;
	}
}
