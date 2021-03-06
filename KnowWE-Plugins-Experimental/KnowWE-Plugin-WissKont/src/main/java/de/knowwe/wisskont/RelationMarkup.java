/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.knowwe.wisskont;

import java.util.Collection;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.renderer.CompositeRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.termbrowser.DroppableTargetSurroundingRenderer;
import de.knowwe.wisskont.relationMarkup.RelationMarkupUtils;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 27.11.2012
 */
public abstract class RelationMarkup extends AbstractType {

	public RelationMarkup(String key) {
		String REGEX = RelationMarkupUtils.getLineRegex(key);
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE | Pattern.DOTALL));

		this.addChildType(new RelationMarkupContentType(REGEX));
		this.addChildType(new KeyType(RelationMarkupUtils.getKeyRegex(key)));
		CompositeRenderer renderer = new CompositeRenderer(new RelationMarkupRenderer(),
				new DroppableTargetSurroundingRenderer());
		this.setRenderer(renderer);
	}

	public abstract URI getRelationURI();

	public abstract String getDerivationMessagePrefix();

	public boolean isInverseDir() {
		return false;
	}

	protected URI createURI(String s) {
		ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
		Collection<Section<? extends SimpleDefinition>> termDefinitions = terminology.getTermDefinitions(new Identifier(
				s));
		if (termDefinitions.size() > 0) {
			return RDFSUtil.getURI(termDefinitions.iterator().next());
		}
		else {
			return new URIImpl(Rdf2GoCore.getInstance().getLocalNamespace().toString() + s);
		}
	}

}
