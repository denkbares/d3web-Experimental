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
package de.knowwe.annotation.type.list;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdfs.util.RDFSUtil;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 22.06.2013
 */
public class OILinkRenderer implements Renderer {

	@Override
	public void render(Section<?> section, UserContext user, RenderResult string) {
		Section<Term> ref = Sections.cast(section,
				Term.class);

		boolean hasError = !IncrementalCompiler.getInstance().getTerminology().isValid(
				ref.get().getTermIdentifier(ref));

		string.appendHtml("<span class='' style=''>");
		if (!hasError) {
			string.appendHtml("<a  style='position:relative;' href='"
					+ RDFSUtil.getURI(ref)
					+ "'>");
		}
		string.appendHtml(section.getText());
		if (!hasError) {
			string.appendHtml("</a>");
		}
		string.appendHtml("</span>");
	}
}
