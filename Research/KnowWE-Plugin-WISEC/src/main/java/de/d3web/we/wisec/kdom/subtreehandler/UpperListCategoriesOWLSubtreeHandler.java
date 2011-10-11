/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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
package de.d3web.we.wisec.kdom.subtreehandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.RepositoryException;

import de.d3web.we.core.semantic.IntermediateOwlObject;
import de.d3web.we.core.semantic.OwlSubtreeHandler;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.wisec.kdom.UpperListCategoriesRootType;
import de.d3web.we.wisec.kdom.UpperListCategoriesType;
import de.d3web.we.wisec.kdom.WISECTable;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.table.TableCellContent;
import de.knowwe.kdom.table.TableLine;
import de.knowwe.logging.Logging;

public class UpperListCategoriesOWLSubtreeHandler extends OwlSubtreeHandler<UpperListCategoriesType> {

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<UpperListCategoriesType> s) {

		// Get the necessary Annotations
		Section<UpperListCategoriesRootType> root = Sections.findAncestorOfType(s,
				UpperListCategoriesRootType.class);
		String listID = DefaultMarkupType.getAnnotation(root, "ListID");

		// Get the WISEC Namespace and create OwlObject
		String ns = SemanticCoreDelegator.getInstance().expandNamespace("w");
		IntermediateOwlObject ioo = new IntermediateOwlObject();

		// create ListTypeStatement
		createListTypeStatement(ioo, ns, listID);

		// Check if we want to use the KDOM
		boolean useKDom = s.get().getAllowedChildrenTypes().size() > 0 ? true : false;

		// Process the Table Content
		if (useKDom) createOWLUsingKDom(s, ioo, ns, listID);
		else {
			createOWL(s.getOriginalText().trim(), ioo, ns, listID);
		}

		// Add the created statements to KnowWE's SemanticCore
		SemanticCoreDelegator.getInstance().addStatements(ioo, s);
		return null;
	}

	/**
	 * Creates OWL Statements by traversing the KDOM and extracting the
	 * necessary information from it.
	 * 
	 * @param section the current section
	 * @param ioo the already created IntermediateOwlObject which stores the
	 *        statements
	 * @param ns the WISEC Namespace
	 * @param listID the ID of the list
	 */
	private void createOWLUsingKDom(Section<UpperListCategoriesType> section,
			IntermediateOwlObject ioo, String ns, String listID) {

		// Check if the table was recognized
		if (Sections.findSuccessor(section, WISECTable.class) != null) {

			// Get all table lines
			List<Section<TableLine>> tableLines = new ArrayList<Section<TableLine>>();
			 Sections.findSuccessorsOfType(section, TableLine.class, tableLines);

			for (Section<TableLine> line : tableLines) {

				// Get the content of all cells
				ArrayList<Section<TableCellContent>> contents = new ArrayList<Section<TableCellContent>>();
				Sections.findSuccessorsOfType(line, TableCellContent.class, contents);

				// Create OWL statement from cell content
				if (contents.size() == 2 && !contents.get(1).getOriginalText().matches("\\s*")) createCharacteristicStatement(
						ioo,
													ns,
													listID,
													contents.get(0).getOriginalText().trim(),
													contents.get(1).getOriginalText().trim());
			}
		}
		else {
			Logging.getInstance().warning("Processing via KDOM failed, trying it without KDOM");
			createOWL(section.getOriginalText().trim(), ioo, ns, listID);
		}
	}

	/**
	 * Creates OWL Statements <b>without</b> traversing the KDOM
	 * 
	 * @param tableContent the Content of the Table
	 * @param ioo the already created IntermediateOwlObject which stores the
	 *        statements
	 * @param ns the WISEC Namespace
	 * @param listID the ID of the list
	 */
	private void createOWL(String tableContent, IntermediateOwlObject ioo,
			String ns, String listID) {

		// Remove the trailing dashes
		StringBuilder bob = new StringBuilder(tableContent);
		while (bob.charAt(bob.length() - 1) == '-')
			bob.delete(bob.length() - 1, bob.length());
		tableContent = bob.toString();

		Pattern cellPattern = Pattern.compile("\\s*\\|+\\s*");
		String[] cells = cellPattern.split(tableContent);
		for (int i = 1; i < cells.length - 1; i += 2) {
			if (!cells[i + 1].equals("")) createCharacteristicStatement(ioo, ns, listID,
					cells[i].trim(), cells[i + 1].trim());
		}
	}

	private void createCharacteristicStatement(IntermediateOwlObject ioo,
			String ns, String listID, String characteristic, String value) {
		URI source = SemanticCoreDelegator.getInstance().getUpper().getHelper().createURI(listID);
		URI prop = SemanticCoreDelegator.getInstance().getUpper().getHelper().createURI(ns,
				characteristic);
		Literal object = SemanticCoreDelegator.getInstance().getUpper().getHelper().createLiteral(
				value);
		try {
			Statement stmt = SemanticCoreDelegator.getInstance().getUpper().getHelper().createStatement(
					source, prop, object);
			ioo.addStatement(stmt);
		}
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	private void createListTypeStatement(IntermediateOwlObject ioo,
			String ns, String listID) {
		URI source = SemanticCoreDelegator.getInstance().getUpper().getHelper().createURI(listID);
		URI object = SemanticCoreDelegator.getInstance().getUpper().getHelper().createURI(ns,
				"UpperList");
		try {
			Statement stmt = SemanticCoreDelegator.getInstance().getUpper().getHelper().createStatement(
					source, RDF.TYPE, object);
			ioo.addStatement(stmt);
		}
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

}
