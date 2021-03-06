/*
 * Copyright (C) 2015 denkbares GmbH, Germany
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

package de.knowwe.rdfs.d3web;

import de.d3web.core.knowledge.Resource;
import com.denkbares.strings.Identifier;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.CompilerMessage;
import de.knowwe.ontology.compile.OntologyCompileScript;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.ontology.kdom.resource.ResourceReference;

/**
 * Registers ResourceReferences with the Identifier used in D3webTermReferences. This way, D3webTermReferences and
 * D3webTermReferences are matched to the same identifiers and the tools like "Rename" and "Show Info" work properly.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 04.09.15
 */
public class Rdf2GoResourceReferenceHandler extends OntologyCompileScript<ResourceReference> {

	@Override
	public void compile(OntologyCompiler compiler, Section<ResourceReference> section) throws CompilerMessage {
		compiler.getTerminologyManager()
				.registerTermReference(compiler, section, Resource.class, new Identifier(section.get(ResourceReference::getTermName)));
	}

	@Override
	public void destroy(OntologyCompiler compiler, Section<ResourceReference> section) {
		compiler.getTerminologyManager()
				.unregisterTermReference(compiler, section, Resource.class, new Identifier(section
						.get(ResourceReference::getTermName)));
	}
}
