/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
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
package de.knowwe.ontology.kdom.clazz;

import java.util.Collection;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.ontology.kdom.resource.AbbreviatedResourceDefinition;
import de.knowwe.rdf2go.Rdf2GoCore;

public class AbbreviatedClassDefinition extends AbbreviatedResourceDefinition {

	public AbbreviatedClassDefinition() {
		this.setSectionFinder(new AllTextFinderTrimmed());
		this.addSubtreeHandler(new AbbreviatedClassHandler());
	}

	public URI getClassNameURI(Rdf2GoCore core, Section<AbbreviatedClassDefinition> section) {
		return super.getResourceURI(core, section);
	}

	private class AbbreviatedClassHandler extends SubtreeHandler<AbbreviatedClassDefinition> {

		@Override
		public Collection<Message> create(Article article, Section<AbbreviatedClassDefinition> section) {
			if (section.hasErrorInSubtree()) return Messages.noMessage();

			Rdf2GoCore core = Rdf2GoCore.getInstance(article);

			URI classNameURI = getClassNameURI(core, section);

			Statement classStatement = core.createStatement(classNameURI, RDF.type, RDFS.Class);
			core.addStatements(classStatement);

			return Messages.noMessage();
		}

	}

}
