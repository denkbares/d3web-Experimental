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
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Message;

/**
 * OWLAPIAbstractKnowledgeUnitCompileScript class which handles basic knowledge
 * creation. Implements dummy deleteFromOntology method. If you need to
 * implement your own use the {@link OWLAPIRDFKnowledgeUnitCompileScript}.
 *
 * Simply adapted the {@link OWLAPISubtreeHandler} to the new
 * {@link IncrementalCompiler}.
 *
 * @author Stefan Mark
 * @created 22.11.2011
 * @param <T>
 */
public abstract class OWLAPIAbstractKnowledgeUnitCompileScript<T extends Type> extends OWLAPIRDFKnowledgeUnitCompileScript<T> {

	public OWLAPIAbstractKnowledgeUnitCompileScript(boolean sync) {
		super(sync);
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
	@Override
	public abstract Set<OWLAxiom> createOWLAxioms(Section<T> section, Collection<Message> messages);

	@Override
	public void deleteFromOntology(Section<T> section) {

	}

	@Override
	public void insertIntoOntology(Section<T> section) {

	}
}
