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
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.support.Editable;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.11.2012
 */
public class LabelMarkup extends AbstractType implements Editable {

	private final String REGEX;

	public LabelMarkup() {
		String keyRegex = "(Label:)";
		REGEX = "(?i)^" + keyRegex + "\\s(.+)$";
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE));

		this.addChildType(new LabelType(REGEX));
		this.addChildType(new KeyType(keyRegex));

		this.setRenderer(new RelationMarkupRenderer());
		this.setIgnorePackageCompile(true);

	}

	@Override
	public String getName() {
		return "Label";
	}

	static class LabelType extends AbstractKnowledgeUnitType<LabelType> {

		public LabelType(String regex) {
			this.setSectionFinder(new RegexSectionFinder(regex, 0, 2));
			this.setCompileScript(new LabelCompileScript());
		}

		class LabelCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<LabelType> {

			@Override
			public void insertIntoRepository(Section<LabelType> section) {

				Section<IncrementalTermDefinition> termSec = MarkupUtils.getConceptDefinition(section);
				if (termSec != null) {
					URI subject = RDFSUtil.getURI(termSec);
					URI pred = RDFS.label;
					Literal literal = Rdf2GoCore.getInstance().createLiteral(section.getText());
					Rdf2GoCore.getInstance().addStatement(section, subject, pred, literal);
				}
				else {
					// TODO: create warning: multiple concepts defined on this
					// page
				}
			}

			@Override
			public Collection<Section<? extends Term>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section) {
				Collection<Section<? extends Term>> result = new HashSet<Section<? extends Term>>();
				List<Section<ConceptMarkup>> conecptDefinitions = MarkupUtils.getConecptDefinitions(section);
				for (Section<ConceptMarkup> def : conecptDefinitions) {
					result.add(Sections.findSuccessor(def, IncrementalTermDefinition.class));
				}
				return result;
			}

		}
	}

}