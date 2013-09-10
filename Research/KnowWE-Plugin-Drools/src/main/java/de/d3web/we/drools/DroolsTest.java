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
package de.d3web.we.drools;

import java.io.File;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.runtime.StatefulKnowledgeSession;

import de.d3web.we.drools.rules.Rule;
import de.d3web.we.drools.terminology.ChoiceInput;
import de.d3web.we.drools.terminology.MCInput;
import de.d3web.we.drools.terminology.NumInput;
import de.d3web.we.drools.terminology.NumValue;
import de.d3web.we.drools.terminology.SolutionInput;
import de.d3web.we.drools.terminology.SolutionScore;
import de.d3web.we.drools.terminology.TextValue;

public class DroolsTest {

	private static KnowledgeBase createKnowledgeBase() {
		KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		builder.add(Rule.fromFile(new File(System.getProperty("user.dir") + "/src/main/resources/misc/", "test.drl")).toResource(), ResourceType.DRL);
		
		if (builder.hasErrors()) {
			throw new RuntimeException(builder.getErrors().toString());
		}
		
		KnowledgeBase kb = KnowledgeBaseFactory.newKnowledgeBase();
		kb.addKnowledgePackages(builder.getKnowledgePackages());
		return kb;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		KnowledgeBase kb = createKnowledgeBase();
		
		StatefulKnowledgeSession session = kb.newStatefulKnowledgeSession();
		
		session.setGlobal("P1", SolutionScore.P1);
		session.setGlobal("P2", SolutionScore.P2);
		session.setGlobal("P3", SolutionScore.P3);
		session.setGlobal("P4", SolutionScore.P4);
		session.setGlobal("P5", SolutionScore.P5);
		session.setGlobal("P6", SolutionScore.P6);
		session.setGlobal("P7", SolutionScore.P7);
		
		session.setGlobal("N1", SolutionScore.N1);
		session.setGlobal("N2", SolutionScore.N2);
		session.setGlobal("N3", SolutionScore.N3);
		session.setGlobal("N4", SolutionScore.N4);
		session.setGlobal("N5", SolutionScore.N5);
		session.setGlobal("N6", SolutionScore.N6);
		session.setGlobal("N7", SolutionScore.N7);
		
		try {
			ChoiceInput q1 = new MCInput("Anlasser");
			TextValue q1a1 = new TextValue("haengt");
			q1.addPossibleValue(q1a1);
			q1.setValue(q1a1);
			TextValue q1a2 = new TextValue("hangt");
			q1.addPossibleValue(q1a2);
			
			SolutionInput d1 = new SolutionInput("Anlasser");
			
			NumInput q2 = new NumInput("Drehzahl");
			NumValue q2a1 = new NumValue(15000);
			q2.setValue(q2a1);
			
			SolutionInput d2 = new SolutionInput("Chris fährt");
			
			session.insert(q1);
			session.insert(q1a1);
			session.insert(q1a2);
			session.insert(d1);
			session.insert(q2);
			session.insert(q2a1);
			session.insert(d2);
			
			session.fireAllRules();
			
		} catch (Exception e) {
			System.err.println("FAIL: " + e.getMessage());
			e.printStackTrace();
		} finally {
			session.dispose();
		}
	}
}
