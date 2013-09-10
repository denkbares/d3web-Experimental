/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.ci;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.MessageObject;
import de.d3web.testing.TestParameter.Mode;
import de.d3web.testing.TestParameter.Type;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 02.07.2013
 */
public class MaxChildrenNumberTest extends AbstractTest<Rdf2GoCore> {

	/**
	 * 
	 */
	public MaxChildrenNumberTest() {
		this.addParameter("Maximale Anzahl Unterkonzepte", Type.Number, Mode.Mandatory,
				"Gibt an die Schwelle an ab wievielen Unterbegriffe der Test anschlägt.");
	}

	@Override
	public Message execute(Rdf2GoCore testObject, String[] args, String[]... ignores) throws InterruptedException {

		int maxThreshold = Integer.parseInt(args[0]);

		String wissassConceptString = ConceptMarkup.WISSASS_CONCEPT.toString();

		String conceptQuery = "SELECT ?x WHERE { ?x rdf:type <" + wissassConceptString + ">.}";
		QueryResultTable resultTable = testObject.sparqlSelect(conceptQuery);
		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();

		SortedMap<String, List<String>> errors = new TreeMap<String, List<String>>();

		while (resultIterator.hasNext()) {
			QueryRow parentConceptResult = resultIterator.next();
			Node value = parentConceptResult.getValue("x");
			URI concept = value.asURI();
			List<String> children = MarkupUtils.getChildren(testObject, concept);
			if (children.size() > maxThreshold) {
				errors.put(MarkupUtils.getConceptName(concept), children);
			}
		}

		if (errors.size() == 0) {
			return Message.SUCCESS;
		}
		else {
			return generateErrorMessage(errors, maxThreshold);
		}

	}

	private Message generateErrorMessage(Map<String, List<String>> errors, int max) {
		String messageText = "Die folgenden Begriffe haben mehr als " + max + " Unterbegriffe:";
		Set<String> keySet = errors.keySet();
		List<MessageObject> messageObjects = new ArrayList<MessageObject>();
		for (String string : keySet) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
					new Identifier(string));
			String label = string;
			if (termDefinitions.size() > 0) {
				Section<? extends SimpleDefinition> def = termDefinitions.iterator().next();
				label = def.getTitle();

			}
			messageText += "\n* " + label + " (";
			List<String> parents = errors.get(string);
			messageText += Strings.concat(", ", parents);
			messageText += ")";
			messageObjects.add(new MessageObject(label, Article.class));
			for (String parent : parents) {
				Collection<Section<? extends SimpleDefinition>> parentDefs = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
						new Identifier(parent));
				String parentLabel = string;
				if (parentDefs.size() > 0) {
					Section<? extends SimpleDefinition> pDef = parentDefs.iterator().next();
					parentLabel = pDef.getTitle();
				}
				messageObjects.add(new MessageObject(parentLabel, Article.class));
			}
		}
		Message message = new Message(Message.Type.FAILURE, messageText);
		message.setObjects(messageObjects);
		return message;
	}

	@Override
	public Class<Rdf2GoCore> getTestObjectClass() {
		return Rdf2GoCore.class;
	}

	@Override
	public String getDescription() {
		return "Tests whether there each concept has at most x children, i.e. not more than x children are allowed.";
	}

}
