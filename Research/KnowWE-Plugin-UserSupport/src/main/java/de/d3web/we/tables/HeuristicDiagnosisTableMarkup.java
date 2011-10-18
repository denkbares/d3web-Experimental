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

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


/**
 * One table for each diagnosis. Each column is one rule with
 * the following meaning
 * 
 * Header consists of two rows:
 * 		1. The condition when the when the rule fires
 * 		2. The weighting of this rule
 * 
 * Left column: Questions
 * Rest: consist of Yes/No(+/-) rating for the Questions.
 * 
 * @author Johannes Dienst
 * @created 14.10.2011
 */
public class HeuristicDiagnosisTableMarkup extends DefaultMarkupType {

	//		public static final String ESTABLISHED_THRESHOLD = "establishedThreshold";

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("HeuristicDiagnosisTable");
		m.addContentType(new HeuristicDiagnosisTable());
		//m.addAnnotation(ESTABLISHED_THRESHOLD, false);
	}

	public HeuristicDiagnosisTableMarkup () {
		super(m);
	}
}
