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

package de.d3web.we.kdom.kopic.renderer;

import java.util.ArrayList;
import java.util.List;

import de.d3web.report.Message;
import de.d3web.we.kdom.renderer.SettingsModeRenderer;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.kdom.table.TableCellContent;

public class KopicTableSectionRenderer extends KopicSectionRenderer {

	@SuppressWarnings("unchecked")
	@Override
	protected void insertErrorRenderer(List<Section<? extends Type>> lines, Message m, String user) {
		int line = m.getLineNo();
		int col = m.getColumnNo();
		if (line - 1 >= 0 && line - 1 < lines.size()) {
			Section lineSec = lines.get(line - 1);
			List<Section<TableCellContent>> cells = new ArrayList<Section<TableCellContent>>();
			Sections.findSuccessorsOfType(lineSec, TableCellContent.class, cells);
			Section cell = cells.get(col - 1);
			if (!cell.hasQuickEditModeSet(user)) {
				((AbstractType) cell.get()).setCustomRenderer(ErrorRendererTable.getInstance());
			}
			else {
				((AbstractType) cell.get()).setCustomRenderer(new SettingsModeRenderer(
						DelegateRenderer.getInstance(), new EditCoveringTableCellRenderer()));
			}
		}
	}

}
