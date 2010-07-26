/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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

package de.d3web.we.kdom.questionTreeNew;

import de.d3web.we.kdom.dashTree.DashTreeElementContent;
import de.d3web.we.kdom.questionTreeNew.indication.IndicationLine;
import de.d3web.we.kdom.questionTreeNew.indication.QuestionRefLine;
import de.d3web.we.kdom.questionTreeNew.setValue.QuestionSetValueLine;
import de.d3web.we.kdom.questionTreeNew.setValue.QuestionSetValueNumLine;
import de.d3web.we.kdom.questionTreeNew.setValue.SolutionSetValueLine;

public class QuestionTreeElementContent extends DashTreeElementContent {

	public QuestionTreeElementContent() {
		this.childrenTypes.add(new QuestionRefLine());
		this.childrenTypes.add(new QuestionLine());
		this.childrenTypes.add(new NumericCondLine());
		this.childrenTypes.add(new SolutionSetValueLine());
		this.childrenTypes.add(new QuestionSetValueNumLine());
		this.childrenTypes.add(new QuestionSetValueLine());	 
		this.childrenTypes.add(new AnswerLine());
		this.childrenTypes.add(new QClassLine());
		this.childrenTypes.add(new IndicationLine());
	}

}
