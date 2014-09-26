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
import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.d3web.testing.MessageObject;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.wisskont.ConceptMarkup;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 22.05.2013
 */
public class OrphanTest extends AbstractTest<Rdf2GoCore> {

	@Override
	public Message execute(Rdf2GoCore testObject, String[] args, String[]... ignores) throws InterruptedException {
		String wissassConceptString = ConceptMarkup.WISSASS_CONCEPT.toString();

		String conceptQuery = "SELECT ?x WHERE { ?x rdf:type <" + wissassConceptString + ">.}";
		QueryResultTable resultTable = testObject.sparqlSelect(conceptQuery);
		ClosableIterator<QueryRow> resultIterator = resultTable.iterator();

		SortedMap<String, List<Identifier>> errors = new TreeMap<String, List<Identifier>>();

		while (resultIterator.hasNext()) {
			QueryRow parentConceptResult = resultIterator.next();
			Node value = parentConceptResult.getValue("x");
			URI concept = value.asURI();
			List<Identifier> parents = MarkupUtils.getParents(testObject, concept);
			if (parents.size() == 0) {
				errors.put(MarkupUtils.getConceptName(concept).toExternalForm(), parents);
			}
		}

		if (errors.size() == 0) {
			return Message.SUCCESS;
		}
		else {
			return generateErrorMessage(errors);
		}

	}

	/**
	 * 
	 * @created 22.05.2013
	 * @param errors
	 * @return
	 */
	private Message generateErrorMessage(Map<String, List<Identifier>> errors) {
		String messageText = "The following concepts have no parents:";
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
			messageText += "\n* " + label;
			messageObjects.add(new MessageObject(label, Article.class));
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
		return "Tests whether there is for each concept at least one parent concept, i.e. orphan detection.";
	}

}
