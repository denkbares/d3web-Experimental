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

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;

/**
 * OWLAPISubtreeHandler class which provides access to an OWL ontology
 * represented using the OWLAPI.
 *
 * By default, everything is applied to KnowWE's global ontology and synced to
 * the @link{Rdf2GoCore}. For special purposes one can define an alternative
 * instance of the @link{OWLAPIConnector} class, giving access to another
 * ontology. Additionally the syncing with the @link{Rdf2GoCore} can be
 * disabled.
 *
 * The created axioms will be automatically removed from the underlying ontology
 * during incremental compilation. In case syncing is active they are also
 * removed from the @link{Rdf2GoCore}.
 *
 * Syncing with the @link{Rdf2GoCore} is done by the @link{RDF2GoSync} class.
 *
 * @author Sebastian Furth
 * @created May 24, 2011
 */
public abstract class OWLAPISubtreeHandler<T extends Type> extends SubtreeHandler<T> {

	/**
	 * Cache for the OWLAxioms. Enables incremental compilation.
	 */
	private static final Map<Section<?>, Set<OWLAxiom>> axiomCache = new WeakHashMap<Section<?>, Set<OWLAxiom>>();

	/**
	 * The @link{OWLAPIConnector} instance granting access to the ontology.
	 */
	private final OWLAPIConnector connector;

	/**
	 * Specifies whether to sync all changes to the ontology with the
	 * RDF2Go-Store
	 */
	private final boolean sync;

	/**
	 * Creates an OWLAPISubtreeHandler instance using KnowWE's global ontology
	 * and applying all changes to this ontology to the RDF2Go-Store.
	 */
	public OWLAPISubtreeHandler() {
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
	public OWLAPISubtreeHandler(boolean sync) {
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
	public OWLAPISubtreeHandler(OWLAPIConnector connector, boolean sync) {
		super(true);
		if (connector == null) {
			throw new NullPointerException("The connector can't be null!");
		}
		this.connector = connector;
		this.sync = sync;
	}

	/**
	 * Specifies the OWLAxioms which will be added to @link{OWLOntology}
	 * instance accessible via the underlying @link{OWLAPIConnector} instance.
	 *
	 * @created May 24, 2011
	 * @param article the processed article.
	 * @param s the processed section belonging to the specified article.
	 * @param messages messages returned after compiling this section.
	 * @return a @link{Set} of @link{OWLAxiom}s which will be added to the
	 *         ontology.
	 */
	public abstract Set<OWLAxiom> createOWLAxioms(KnowWEArticle article, Section<T> s, Collection<KDOMReportMessage> messages);

	@Override
	public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<T> s) {
		Collection<KDOMReportMessage> messages = new LinkedList<KDOMReportMessage>();
		Set<OWLAxiom> axioms = createOWLAxioms(article, s, messages);
		connector.addAxioms(axioms);
		axiomCache.put(s, axioms);
		if (sync) {
			RDF2GoSync.synchronize(axioms, s, RDF2GoSync.Mode.ADD);
		}
		return messages;
	}

	@Override
	public void destroy(KnowWEArticle article, Section<T> s) {
		Set<OWLAxiom> axioms = axiomCache.remove(s);
		connector.removeAxioms(axioms);
		if (sync) {
			RDF2GoSync.synchronize(axioms, s, RDF2GoSync.Mode.REMOVE);
		}
	}

}
