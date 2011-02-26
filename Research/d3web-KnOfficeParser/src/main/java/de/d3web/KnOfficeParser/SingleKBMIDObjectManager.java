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

package de.d3web.KnOfficeParser;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.QuestionYN;
import de.d3web.core.knowledge.terminology.QuestionZC;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.IDObjectManagement;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Value;

/**
 * Default implementation for the IDObjectManagement, searches and creates
 * objects in a single kbm
 * 
 * @author Markus Friedrich (denkbares GmbH)
 */
public class SingleKBMIDObjectManager implements IDObjectManagement {

	protected KnowledgeBase kb;

	public SingleKBMIDObjectManager(KnowledgeBase kb) {
		super();
		this.kb = kb;
	}

	@Override
	public Solution createSolution(String name, Solution parent) {
		if (parent == null) {
			return new Solution(kb.getRootSolution(), name);
		}
		else {
			return new Solution(parent, name);
		}
	}

	@Override
	public QContainer createQContainer(String name, QASet parent) {
		if (parent == null) {
			return new QContainer(kb.getRootQASet(), name);
		}
		else {
			return new QContainer(parent, name);
		}
	}

	@Override
	public QuestionDate createQuestionDate(String name, QASet parent) {
		if (parent == null) {
			return new QuestionDate(kb.getRootQASet(), name);
		}
		else {
			return new QuestionDate(parent, name);
		}
	}

	@Override
	public Solution findSolution(String name) {
		return kb.getManager().searchSolution(name);
	}

	@Override
	public QContainer findQContainer(String name) {
		return kb.getManager().searchQContainer(name);
	}

	@Override
	public Question findQuestion(String name) {
		return kb.getManager().searchQuestion(name);
	}

	@Override
	public QuestionMC createQuestionMC(String name, QASet parent,
			Choice[] answers) {
		if (parent == null) {
			return new QuestionMC(kb.getRootQASet(), name, answers);
		}
		else {
			return new QuestionMC(parent, name, answers);
		}
	}

	@Override
	public QuestionMC createQuestionMC(String name, QASet parent,
			String[] answers) {
		if (parent == null) {
			return new QuestionMC(kb.getRootQASet(), name, answers);
		}
		else {
			return new QuestionMC(parent, name, answers);
		}
	}

	@Override
	public QuestionNum createQuestionNum(String name, QASet parent) {
		if (parent == null) {
			return new QuestionNum(kb.getRootQASet(), name);
		}
		else {
			return new QuestionNum(parent, name);
		}
	}

	@Override
	public QuestionOC createQuestionOC(String name, QASet parent,
			Choice[] answers) {
		if (parent == null) {
			return new QuestionOC(kb.getRootQASet(), name, answers);
		}
		else {
			return new QuestionOC(parent, name, answers);
		}
	}

	@Override
	public QuestionOC createQuestionOC(String name, QASet parent,
			String[] answers) {
		if (parent == null) {
			return new QuestionOC(kb.getRootQASet(), name, answers);
		}
		else {
			return new QuestionOC(parent, name, answers);
		}
	}

	@Override
	public QuestionText createQuestionText(String name, QASet parent) {
		if (parent == null) {
			return new QuestionText(kb.getRootQASet(), name);
		}
		else {
			return new QuestionText(parent, name);
		}
	}

	@Override
	public QuestionYN createQuestionYN(String name, QASet parent) {
		if (parent == null) {
			return new QuestionYN(kb.getRootQASet(), name);
		}
		else {
			return new QuestionYN(parent, name);
		}
	}

	@Override
	public QuestionZC createQuestionZC(String name, QASet parent) {
		if (parent == null) {
			return new QuestionZC(kb.getRootQASet(), name);
		}
		else {
			return new QuestionZC(parent, name);
		}
	}

	@Override
	public Choice addChoiceAnswer(QuestionChoice qc, String value) {
		Choice answer = new Choice(value);
		qc.addAlternative(answer);
		return answer;
	}

	@Override
	public KnowledgeBase getKnowledgeBase() {
		return kb;
	}

	@Override
	public Value findValue(Question q, String name) {
		return KnowledgeBaseUtils.findValue(q, name);
	}

	@Override
	public Choice findChoice(QuestionChoice qc, String name) {
		return KnowledgeBaseUtils.findChoice(qc, name);
	}
}
