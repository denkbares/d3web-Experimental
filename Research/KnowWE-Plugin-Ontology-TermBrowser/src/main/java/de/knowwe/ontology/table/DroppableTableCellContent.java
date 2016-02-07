/*
 * Copyright (C) 2016 denkbares GmbH, Germany
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

package de.knowwe.ontology.table;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.renderer.CompositeRenderer;
import de.knowwe.kdom.renderer.SurroundingRenderer;
import de.knowwe.termbrowser.DroppableTargetSurroundingRenderer;

/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 07.02.16.
 */
public class DroppableTableCellContent extends AbstractType {

	public DroppableTableCellContent() {

		this.setSectionFinder(new ConstraintSectionFinder(new RegexSectionFinder("\\s+"), AtMostOneFindingConstraint.getInstance()));
		this.setRenderer(new CompositeRenderer(new SurroundingRenderer() {
			@Override
			public void renderPre(Section<?> section, UserContext user, RenderResult string) {
				if(section.getParent().getText().length() < 5) {
					string.appendHtml("<div style='display:inline;' dragdropid='" + section.getID()
							+ "' class='dropTargetMarkup'>");
				}
			}

			@Override
			public void renderPost(Section<?> section, UserContext user, RenderResult string) {
				if(section.getParent().getText().length() < 5) {
					string.appendHtml("<span style='color:lightgray;'>drag-on-me</span></div>");
				}
			}
		}));
	}

}
