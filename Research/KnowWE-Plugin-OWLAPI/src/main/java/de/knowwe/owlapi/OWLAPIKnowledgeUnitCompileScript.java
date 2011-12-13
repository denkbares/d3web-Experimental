/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.owlapi;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.semanticweb.owlapi.model.OWLAxiom;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;

/**
 * OWLAPIKnowledgeUnitCompileScript class which handles basic knowledge
 * creation.
 * 
 * Simply adapted the {@link OWLAPISubtreeHandler} to the new
 * {@link IncrementalCompiler}.
 * 
 * @author Stefan Mark
 * @created 22.11.2011
 * @param <T>
 */
public abstract class OWLAPIKnowledgeUnitCompileScript<T extends Type> extends AbstractKnowledgeUnitCompileScript<T> {

	/**
	 * Cache for the OWLAxioms. Enables incremental compilation.
	 */
	private static final Map<Section<?>, Set<OWLAxiom>> axiomCache = new WeakHashMap<Section<?>, Set<OWLAxiom>>();

	/**
	 * The instance of the {@link OWLAPIConnector} handles the access to the
	 * ontology.
	 */
	private final OWLAPIConnector connector;

	/**
	 * Specifies whether the axiom inserted should be synced with a
	 * RDF2Go-Store.
	 */
	private final boolean sync;

	/**
	 * Creates an OWLAPISubtreeHandler instance using KnowWE's global ontology
	 * and applying all changes to this ontology to the RDF2Go-Store.
	 */
	public OWLAPIKnowledgeUnitCompileScript() {
		this(OWLAPIConnector.getGlobalInstance(), true);
	}

	/**
	 * Creates an OWLAPISubtreeHandler instance using KnowWE's global ontology
	 * but letting you decide whether the changes to the ontology shall be
	 * applied to the RDF2Go-Store.
	 * 
	 * @param sync specifies the sync with the RDF2Go-Store (true means sync is
	 *        active).
	 */
	public OWLAPIKnowledgeUnitCompileScript(boolean sync) {
		this(OWLAPIConnector.getGlobalInstance(), sync);
	}

	/**
	 * Creates an OWLAPISubtreeHandler using the specified connector and letting
	 * you decide whether the changes to the ontology shall be applied to the
	 * RDF2Go-Store.
	 * 
	 * @param connector specifies to OWLAPIConnector used in the subtreehandler
	 * @param sync specifies the sync with the RDF2Go-Store (true means sync is
	 *        active).
	 */
	public OWLAPIKnowledgeUnitCompileScript(OWLAPIConnector connector, boolean sync) {
		if (connector == null) {
			throw new NullPointerException("The connector can't be null!");
		}
		this.connector = connector;
		this.sync = sync;
	}

	@Override
	public void deleteFromRepository(Section<T> section) {
		deleteFromOntology(section);
		Set<OWLAxiom> axioms = axiomCache.remove(section);
		connector.removeAxioms(axioms);
		if (sync) {
			RDF2GoSync.synchronize(axioms, section, RDF2GoSync.Mode.REMOVE);
		}
	}

	@Override
	public void insertIntoRepository(Section<T> section) {
		Collection<Message> messages = new LinkedList<Message>();
		Set<OWLAxiom> axioms = createOWLAxioms(section, messages);
		connector.addAxioms(axioms);
		axiomCache.put(section, axioms);

		// store messages found while compiling the current section
		Messages.storeMessages(section.getArticle(), section, getClass(), messages);
		insertIntoOntology(section);

		if (sync) {
			RDF2GoSync.synchronize(axioms, section, RDF2GoSync.Mode.ADD);
		}
	}

	/**
	 * Creates the {@link OWLAxiom} that will be added to the ontology via the
	 * underlying @link{OWLAPIConnector} instance.
	 * 
	 * @created 22.11.2011
	 * @param section The processed section belonging to the specified article.
	 * @param messages messages returned after compiling this section.
	 * @return a @link{Set} of @link{OWLAxiom}s which will be added to the
	 *         ontology.
	 */
	public abstract Set<OWLAxiom> createOWLAxioms(Section<T> section, Collection<Message> messages);

	public abstract void deleteFromOntology(Section<T> section);

	public abstract void insertIntoOntology(Section<T> section);
}
