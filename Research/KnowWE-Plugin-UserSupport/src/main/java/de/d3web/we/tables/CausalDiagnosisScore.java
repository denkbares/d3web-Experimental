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

import de.d3web.we.kdom.xcl.list.ListSolutionType;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;

/**
 *
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class CausalDiagnosisScore extends AbstractType {

	public CausalDiagnosisScore() {
		this.sectionFinder = new AllTextSectionFinder();

		this.addChildType(new ListSolutionType());

		// cut the optional closing }
		AnonymousTypeInvisible closing = new AnonymousTypeInvisible("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		this.addChildType(new InnerTable());
	}

}
