/*
 * Copyright (C) 2014 denkbares GmbH, Germany
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.openrdf.model.BNode;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.model.vocabulary.XMLSchema;

import de.d3web.strings.Identifier;
import de.knowwe.core.compile.CompileScript;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.DestroyScript;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.dashtree.DashTreeTermRelationScript;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * This {@link CompileScript} adds all relations defined by das trees to the triple store.
 *
 * @author Albrecht Striffler (denkbares GmbH)
 * @created 12.03.2014
 */
public abstract class Rdf2GoDashTreeTermRelationScript extends DashTreeTermRelationScript<OntologyCompiler> implements DestroyScript<OntologyCompiler, TermDefinition> {

	@Override
	protected void createObjectRelations(Section<TermDefinition> parentSection, OntologyCompiler compiler, Identifier parentIdentifier, List<Identifier> childrenIdentifier) {

		// since we also have to destroy all defining sections, we also have to compile all defining sections
		Collection<Section<?>> termDefiningSections = compiler.getTerminologyManager()
				.getTermDefiningSections(parentSection.get().getTermIdentifier(parentSection));
		Compilers.addSectionsToCompile(compiler, termDefiningSections, this.getClass());

		Rdf2GoCore core = compiler.getRdf2GoCore();
		URI parentURI = core.createlocalURI(Rdf2GoUtils.getCleanedExternalForm(parentIdentifier));
		URI hasChildURI = core.createlocalURI("hasChild");
		List<Statement> statements = new ArrayList<>();
		boolean hasParent = Rdf2GoD3webUtils.hasParentDashTreeElement(compiler, parentIdentifier);
		if (!hasParent) {
			URI rootURI = getRootURI(core);
			Rdf2GoUtils.addStatement(core, parentURI, RDFS.SUBCLASSOF, rootURI, statements);
			Rdf2GoUtils.addStatement(core, rootURI, hasChildURI, parentURI, statements);
		}
		int index = 0;
		for (Identifier childIdentifier : childrenIdentifier) {
			URI childURI = core.createlocalURI(Rdf2GoUtils.getCleanedExternalForm(childIdentifier));
			Rdf2GoUtils.addStatement(core, childURI, RDFS.SUBCLASSOF, parentURI, statements);
			Rdf2GoUtils.addStatement(core, parentURI, hasChildURI, childURI, statements);

			BNode indexNode = core.createBlankNode();
			URI hasIndexInfoURI = core.createlocalURI("hasIndexInfo");
			Rdf2GoUtils.addStatement(core, childURI, hasIndexInfoURI, indexNode, statements);
			URI hasIndexURI = core.createlocalURI("hasIndex");
			Literal indexLiteral = core.createDatatypeLiteral(Integer.toString(index++), XMLSchema.INTEGER);
			Rdf2GoUtils.addStatement(core, indexNode, hasIndexURI, indexLiteral, statements);
			URI indexOfURI = core.createlocalURI("isIndexOf");
			Rdf2GoUtils.addStatement(core, indexNode, indexOfURI, parentURI, statements);
		}
		core.addStatements(parentSection, Rdf2GoUtils.toArray(statements));

	}

	protected abstract URI getRootURI(Rdf2GoCore core);

	@Override
	public Class<OntologyCompiler> getCompilerClass() {
		return OntologyCompiler.class;
	}

	@Override
	public void destroy(OntologyCompiler compiler, Section<TermDefinition> section) {
		compiler.getRdf2GoCore().removeStatements(section);
		if (section.getObject(compiler, RELATIONS_ADDED) == null) return;
		Collection<Section<?>> termDefiningSections = compiler.getTerminologyManager()
				.getTermDefiningSections(section.get().getTermIdentifier(section));
		Compilers.addSectionsToDestroy(compiler, termDefiningSections, this.getClass());
		// we don't exactly know where the relations were added, so we destroy all defining sections
		for (Section<?> termDefiningSection : termDefiningSections) {
			termDefiningSection.storeObject(compiler, RELATIONS_ADDED, null);
		}
	}
}
