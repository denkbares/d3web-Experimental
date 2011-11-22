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
package de.d3web.we.tables;

import de.d3web.we.renderer.TableRenderer;
import de.knowwe.core.kdom.basicType.CommentLineType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.UnquotedExpressionFinder;


/**
 * 
 * @author Johannes Dienst
 * @created 19.10.2011
 */
public class InnerTable extends ITable {

	public InnerTable() {

		
		this.sectionFinder = new AllTextSectionFinder();

		// allow for comment lines
		this.addChildType(new CommentLineType());

		// split by search for komas
		AnonymousType koma = new AnonymousType("koma");
		koma.setSectionFinder(new UnquotedExpressionFinder(","));
		this.addChildType(koma);

		// Lines of the table
		this.addChildType(new TableLine());

		// Renderer
		this.setCustomRenderer(new TableRenderer());

		// anything left is comment
		AnonymousType residue = new AnonymousType("rest");
		residue.setSectionFinder(new AllTextFinderTrimmed());
		residue.setCustomRenderer(StyleRenderer.COMMENT);
		this.addChildType(residue);
	}
}