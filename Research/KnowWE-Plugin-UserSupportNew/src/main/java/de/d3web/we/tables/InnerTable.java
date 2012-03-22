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

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.CommentLineType;
import de.knowwe.core.kdom.basicType.LineBreak;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.UnquotedExpressionFinder;
import de.knowwe.usersupport.renderer.TableRenderer;


/**
 * 
 * {@link CausalDiagnosisScore}, {@link DecisionTable} and {@link HeuristicDiagnosisTable}
 * use this class for recognition of the their knowledge table.
 * 
 * @author Johannes Dienst
 * @created 19.10.2011
 */
public class InnerTable extends ITable
{

	public InnerTable()
	{
		this.sectionFinder = new AllTextSectionFinder();

		// allow for comment lines
		this.addChildType(new CommentLineType());

		// split by search for komas
		AnonymousType koma = new AnonymousType("koma");
		koma.setSectionFinder(new UnquotedExpressionFinder(","));
		this.addChildType(koma);

		this.addChildType(new LineBreak());

		// Lines of the table
		this.addChildType(new TableHeaderLine());
		this.addChildType(new TableLine());

		// Renderer
		this.setRenderer(new TableRenderer());

		// anything left is comment
		AnonymousType residue = new AnonymousType("rest");
		residue.setSectionFinder(new AllTextFinderTrimmed());
		residue.setRenderer(StyleRenderer.COMMENT);
		this.addChildType(residue);
	}

	/**
	 * 
	 * @created 22.02.2012
	 * @param header
	 * @param i
	 */
	public void addChildTypeAtPosition(Type type, int i)
	{
		if (this.childrenTypes.size() > i)
			this.childrenTypes.add(i, type);
	}

}
