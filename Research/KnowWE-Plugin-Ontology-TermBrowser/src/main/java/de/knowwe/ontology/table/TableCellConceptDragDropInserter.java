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

import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.table.TableCellContent;
import de.knowwe.ontology.kdom.table.OntologyTableCellEntry;
import de.knowwe.termbrowser.DefaultMarkupDragDropInserter;

/**
 * @author Jochen Reutelshoefer (denkbares GmbH)
 * @created 07.02.16.
 */
public class TableCellConceptDragDropInserter extends DefaultMarkupDragDropInserter {

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {
		// nothing..
		return null;
	}

	@Override
	protected Section<?> findSectionToBeReplaced(Section<?> s) {
		return Sections.ancestor(s,
				TableCellContent.class);
	}

	@Override
	protected String createReplaceTextForSelectedSection(Section<?> section, String dropText) {
		String[] split = dropText.split("#");
		String shortURI = split[0] + ":" + Strings.encodeURL(split[1]);
		if(Strings.isBlank(section.getText().trim())) {
			return shortURI;
		} else {
			return section.getText().trim()+", "+shortURI;
		}
	}


}

