/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.usersupport.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.usersupport.tables.InnerTable;

/**
 * This is a workaround class. Because the import/export-buttons will not be
 * functional inside the DefaultMarkup.
 * 
 * @author Johannes Dienst
 * @created 15.12.2011
 */
public class DefaultMarkupRendererUserSupport implements Renderer
{

	@Override
	public void render(Section<?> section, UserContext user, StringBuilder string)
	{
		new DefaultMarkupRenderer().render(section, user, string);
		Section<InnerTable> iT = Sections.findSuccessor(section, InnerTable.class);
		StringBuilder buildi = new StringBuilder();
		TableRenderer.renderExportImportButton(buildi, iT);
		string.append(Strings.maskHTML(buildi.toString()));
	}

}
