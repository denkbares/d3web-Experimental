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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.antlr.runtime.RecognitionException;

import de.d3web.KnOfficeParser.decisiontree.D3DTBuilder;
import de.d3web.KnOfficeParser.rule.D3ruleBuilder;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.report.Message;

/**
 * Testklasse um den DecisionTree2Parser zu testen
 * 
 * @author Markus Friedrich
 * 
 */
public class Tester {

	/**
	 * @param args
	 * @throws IOException
	 * @throws RecognitionException
	 */
	public static void main(String[] args) throws IOException,
			RecognitionException {
		Locale.setDefault(Locale.GERMAN);
		File file = new File("examples\\car.txt");
		KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
		D3DTBuilder builder = new D3DTBuilder(file.toString(), new SingleKBMIDObjectManager(kb));
		new Solution(kb.getRootSolution(), "Leerlaufsystem defekt");
		new Solution(kb.getRootSolution(), "Ansaugsystem undicht");
		new Solution(kb.getRootSolution(), "Luftfiltereinsatz verschmutzt");
		new Solution(kb.getRootSolution(), "Zündeinstellung falsch");
		new Solution(kb.getRootSolution(), "Batterie leer");
		Reader r = new FileReader(file);
		Collection<Message> col = builder.addKnowledge(r, new SingleKBMIDObjectManager(kb), null);
		List<Message> errors = (List<Message>) col;
		for (Message m : errors) {
			System.out.println(m);
		}
		file = new File("examples\\carrule.txt");
		D3ruleBuilder builder2 = new D3ruleBuilder(file.toString(), false,
				new SingleKBMIDObjectManager(kb));
		builder2.setLazy(true);
		// builder2.setBuildonlywith0Errors(true);
		r = new FileReader(file);
		col = builder2.addKnowledge(r, new SingleKBMIDObjectManager(kb), null);
		errors = (List<Message>) col;
		for (Message m : errors) {
			System.out.println(m);
		}
	}
}
