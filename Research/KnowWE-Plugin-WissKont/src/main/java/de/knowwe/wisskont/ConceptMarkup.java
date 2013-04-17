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

import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.ontoware.rdf2go.vocabulary.RDF;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.support.Editable;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.strings.Strings;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.AbstractKnowledgeUnitCompileScriptRDFS;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 22.11.2012
 */
public class ConceptMarkup extends AbstractKnowledgeUnitType<ConceptMarkup> implements Editable {

	private final String REGEX;

	public static URI WISSASS_CONCEPT = null;

	static {
		WISSASS_CONCEPT = new URIImpl(Rdf2GoCore.getInstance().getLocalNamespace()
				+ "WissassConcept");
	}

	public ConceptMarkup() {
		String keyRegex = "(Konzept:)";
		REGEX = "(?i)^" + keyRegex + "\\s(.+)$";
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE));

		this.addChildType(new DefinitionTerm());
		this.addChildType(new KeyType(keyRegex));

		this.setCompileScript(new ConceptCompileScript());
		this.setRenderer(new DefaultMarkupRenderer());
		this.setIgnorePackageCompile(true);
	}

	@Override
	public String getName() {
		return "Konzept-Definition";
	}

	class DefinitionTerm extends IncrementalTermDefinition<String> {

		public DefinitionTerm() {
			super(String.class);
			this.setSectionFinder(new RegexSectionFinder(REGEX, 0, 2));
			this.setRenderer(new StyleRenderer("color:#19196C"));
		}

		@Override
		public String getTermName(Section<? extends Term> s) {
			return Strings.unquote(s.getText().trim());
		}

	}

	class ConceptCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<ConceptMarkup> {

		@Override
		public void insertIntoRepository(Section<ConceptMarkup> section) {

			Section<IncrementalTermDefinition> termSec = MarkupUtils.getConceptDefinition(section);
			if (termSec != null) {
				URI subject = RDFSUtil.getURI(termSec);
				URI pred = RDF.type;

				Rdf2GoCore.getInstance().addStatement(section, subject, pred, WISSASS_CONCEPT);
			}
		}
	}
}
