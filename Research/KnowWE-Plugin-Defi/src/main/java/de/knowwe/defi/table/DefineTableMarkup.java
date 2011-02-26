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

package de.knowwe.defi.table;

import de.d3web.we.kdom.defaultMarkup.DefaultMarkup;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

/**
 * @author Jochen
 * 
 *         A markup to create a block for text-rules
 * @see RuleContentType
 * 
 * 
 */
public class DefineTableMarkup extends DefaultMarkupType {

	public DefineTableMarkup(DefaultMarkup markup) {
		super(markup);
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("Befragungstabelle");
		m.addContentType(new DefineTableContentType());
		m.addAnnotation("id", true);
	}

	public DefineTableMarkup() {
		super(m);
	}
}