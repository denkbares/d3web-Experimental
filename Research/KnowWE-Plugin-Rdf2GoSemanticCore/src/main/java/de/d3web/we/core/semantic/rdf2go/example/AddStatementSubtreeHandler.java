/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.d3web.we.core.semantic.rdf2go.example;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.semantic.rdf2go.RDF2GoSubtreeHandler;
import de.d3web.we.core.semantic.rdf2go.Rdf2GoCore;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;

/**
 * 
 * @author grotheer
 * @created 02.12.2010
 */
public class AddStatementSubtreeHandler extends RDF2GoSubtreeHandler<AddStatementType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AddStatementType> s) {

		Rdf2GoCore r2gc = new Rdf2GoCore();
		r2gc.init();

		ArrayList<Statement> l = new ArrayList<Statement>();

		URI sub = Rdf2GoCore.getInstance().createURI("http://localhost/owl#1");
		URI pred = Rdf2GoCore.getInstance().createURI("http://localhost/owl#2");
		URI obj = Rdf2GoCore.getInstance().createURI("http://localhost/owl#3");

		try {
			String section = s.toString();
			section = section.replaceAll(article.getTitle() + " - %add%", "");
			section = section.replaceAll("%/add%", "");
			section = section.replaceAll("\n", "");
			String[] spo = section.split(";");
			String subS = spo[0];
			String predS = spo[1];
			String objS = spo[2];
			sub = Rdf2GoCore.getInstance().createURI(subS);
			pred = Rdf2GoCore.getInstance().createURI(predS);
			obj = Rdf2GoCore.getInstance().createURI(objS);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		Statement st = Rdf2GoCore.getInstance().createStatement(sub, pred, obj);
		l.add(st);
		Rdf2GoCore.getInstance().addStatements(l, s);
		System.out.println("Statement added successfully");
		return null;
	}

}
