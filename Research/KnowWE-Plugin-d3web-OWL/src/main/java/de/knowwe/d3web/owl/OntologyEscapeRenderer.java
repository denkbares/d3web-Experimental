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
package de.knowwe.d3web.owl;

import java.util.List;

import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.ContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;

/**
 *
 * @author Sebastian Furth
 * @created Apr 8, 2011
 */
public class OntologyEscapeRenderer extends DefaultMarkupRenderer<OntologyProviderType> {

	public OntologyEscapeRenderer() {
		super("KnowWEExtension/images/owl24.png");
	}

	@Override
	protected void renderContents(Section<OntologyProviderType> section, UserContext user, StringBuilder string) {
		List<Section<?>> subsecs = section.getChildren();
		Section<?> first = subsecs.get(0);
		Section<?> last = subsecs.get(subsecs.size() - 1);
		for (Section<?> subsec : subsecs) {
			if (subsec == first && subsec.get() instanceof PlainText) {
				continue;
			}
			if (subsec == last && subsec.get() instanceof PlainText) {
				continue;
			}
			// Necessary to avoid clashes with JSPWiki syntax
			if (subsec.get() instanceof ContentType) {
				string.append("%%prettify\n{{{");
				string.append(subsec.getText());
				string.append("}}}\n/%");
				continue;
			}
			subsec.get().getRenderer().render(subsec, user, string);
		}
	}

}
