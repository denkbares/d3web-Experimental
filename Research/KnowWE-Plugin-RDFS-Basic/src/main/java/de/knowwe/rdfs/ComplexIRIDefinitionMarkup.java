/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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

package de.knowwe.rdfs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.EndLineComment;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.RegexSectionFinderSingle;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.rendering.PreEnvRenderer;
import de.knowwe.rdfs.util.RDFSUtil;

public class ComplexIRIDefinitionMarkup extends AbstractType implements ComplexDefinition, KnowledgeUnit {

	private static final String REGEX = "(.+)\\s(\\w+)::\\s(.+)$";

	private static final String REGEX_DEF = "^def\\s+";

	public ComplexIRIDefinitionMarkup() {
		this.sectionFinder = new RegexSectionFinder(REGEX_DEF + REGEX, Pattern.MULTILINE);

		this.addChildType(new EndLineComment());

		this.addChildType(new DefType());
		this.addChildType(new DefinitionTerm());
		this.addChildType(new Predicate());
		this.addChildType(new Object());

		this.setRenderer(new PreEnvRenderer());
	}

	// @Override
	// public Collection<Section<TermReference>>
	// getAllReferencesOfComplexDefinition(
	// Section<? extends ComplexDefinition<ComplexIRIDefinitionMarkup>> section)
	// {
	// return CompileUtils.getAllReferencesOfComplexDefinition(section);
	// }

	class DefType extends AbstractType {

		public DefType() {
			this.setSectionFinder(new RegexSectionFinderSingle(REGEX_DEF));
			this.setRenderer(new StyleRenderer("font-style:italic;"));
		}
	}

	class DefinitionTerm extends AbstractIRITermDefinition {

		public DefinitionTerm() {
			this.setSectionFinder(new RegexSectionFinderSingle(Pattern.compile(REGEX), 1));
		}

		@Override
		public String getTermName(Section<? extends SimpleTerm> section) {
			return Strings.unquote(section.getText().trim());
		}

	}

	class Predicate extends IRITermRef {

		public Predicate() {
			this.sectionFinder = new RegexSectionFinderSingle("\\b([^\\s]*)::",
					Pattern.DOTALL, 1);
		}

		@Override
		public String getTermName(Section<? extends SimpleTerm> s) {
			return s.getText().trim().replaceAll("::", "").trim();
		}
	}

	class Object extends IRITermRef {

		public Object() {
			this.sectionFinder = new RegexSectionFinderSingle("::\\s(.*)",
					Pattern.DOTALL, 1);
		}

		@Override
		public String getTermName(Section<? extends SimpleTerm> s) {
			return s.getText().trim();
		}
	}

	class ComplexIRIDefinitionCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<ComplexIRIDefinitionMarkup> {

		@Override
		public void insertIntoRepository(Section<ComplexIRIDefinitionMarkup> section) {
			List<Section<SimpleReference>> found = new ArrayList<Section<SimpleReference>>();
			Node subURI = null;
			Node predURI = null;
			Node objURI = null;

			Sections.findSuccessorsOfType(section, SimpleReference.class, found);
			Section<SimpleDefinition> subject = Sections.findSuccessor(section,
					SimpleDefinition.class);

			if (found.size() == 2) {

				Section<SimpleReference> predicate = found.get(0);
				Section<SimpleReference> object = found.get(1);

				subURI = RDFSUtil.getURI(subject);
				predURI = RDFSUtil.getURI(predicate);
				objURI = RDFSUtil.getURI(object);
			}
			else {
				// return Arrays.asList((KDOMReportMessage) new SyntaxError(
				// "invalid term combination:" + found.size()));
			}
			if (subURI == null) {
				// return Arrays.asList((KDOMReportMessage) new SyntaxError(
				// "subject URI not found"));
			}
			if (predURI == null) {
				// return Arrays.asList((KDOMReportMessage) new SyntaxError(
				// "predicate URI not found"));
			}
			if (objURI == null) {
				// return Arrays.asList((KDOMReportMessage) new SyntaxError(
				// "object URI not found"));
			}

			Rdf2GoCore.getInstance().addStatement(subURI.asResource(),
					predURI.asURI(), objURI, section);

			// return new ArrayList<KDOMReportMessage>(0);

		}

	}

	@Override
	public KnowledgeUnitCompileScript<ComplexIRIDefinitionMarkup> getCompileScript() {
		return new ComplexIRIDefinitionCompileScript();
	}

}
