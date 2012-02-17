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
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.TypeRestrictedReference;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.basicType.EndLineComment;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.DefaultErrorRenderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.constraint.AtMostOneFindingConstraint;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.literal.TurtleObjectLiteral;
import de.knowwe.rdfs.literal.TurtleObjectLiteralText;
import de.knowwe.rdfs.rendering.PreEnvRenderer;
import de.knowwe.rdfs.util.RDFSUtil;

public class TripleMarkup extends AbstractType implements
		KnowledgeUnit<TripleMarkup> {

	static final String TRIPLE_REGEX = "^>(.*?::.*?)$";

	public TripleMarkup() {

		this.setSectionFinder(new RegexSectionFinder(TRIPLE_REGEX,
				Pattern.DOTALL | Pattern.MULTILINE, 0));

		this.addChildType(new EndLineComment());
		this.addChildType(new TripleMarkupContent());
		this.setRenderer(new PreEnvRenderer());
	}

	class TripleMarkupContent extends AbstractType {

		public TripleMarkupContent() {
			this.setSectionFinder(new RegexSectionFinder(TRIPLE_REGEX,
					Pattern.DOTALL | Pattern.MULTILINE, 1));
			this.addChildType(new SimpleTurtlePredicate());
			this.addChildType(new SimpleTurtleSubject());
			this.addChildType(new SimpleTurtleObjectSection());

			this.setRenderer(new RangeCheckRenderer());
		}

		class RangeCheckRenderer implements Renderer {

			@Override
			public void render(Section<?> section, UserContext user, StringBuilder string) {
				Section<KnowledgeUnit> triple = Sections.findAncestorOfType(section,
						KnowledgeUnit.class);

				Section<SimpleTurtlePredicate> predicate = Sections.findSuccessor(triple,
						SimpleTurtlePredicate.class);

				Section<SimpleTurtleObjectRef> object = Sections.findSuccessor(triple,
						SimpleTurtleObjectRef.class);

				Section<SimpleTurtleSubject> subject = Sections.findSuccessor(triple,
						SimpleTurtleSubject.class);

				String predName = predicate.getText().trim();

				// IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(predName);

				Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(
						predName);
				String domainClassName = null;
				String rangeClassName = null;
				boolean warningRange = false;
				boolean warningDomain = false;
				if (info != null && object != null
						&& RDFSUtil.isTermCategory(predicate,
								RDFSTermCategory.ObjectProperty)) {

					if (info instanceof Map) {
						Set keyset = ((Map) info).keySet();
						for (Object key : keyset) {
							if (key.equals(
									ObjectPropertyDefinitionMarkup.RDFS_DOMAIN_KEY)) {
								domainClassName = (String) ((Map) info).get(key);
							}
							if (key.equals(
									ObjectPropertyDefinitionMarkup.RDFS_RANGE_KEY)) {
								rangeClassName = (String) ((Map) info).get(key);
							}
						}
					}
					URI rangeClassURI = RDFSUtil.getURI(IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
							rangeClassName).iterator().next());
					URI domainClassURI = RDFSUtil.getURI(IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
							domainClassName).iterator().next());

					URI objectURI = RDFSUtil.getURI(object);
					URI subjectURI = RDFSUtil.getURI(subject);

					String queryRange = "ASK { <" + objectURI + "> <" + RDF.type + "> <"
							+ rangeClassURI + "> .}";
					warningRange = !Rdf2GoCore.getInstance().sparqlAskExcludeStatementForSection(
							queryRange, triple);

					String queryDomain = "ASK { <" + subjectURI + "> <" + RDF.type
							+ "> <"
							+ domainClassURI + "> .}";
					warningDomain = !Rdf2GoCore.getInstance().sparqlAskExcludeStatementForSection(
							queryDomain, triple);
				}

				if (warningRange) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.preRenderMessage(
									new Message(Message.Type.WARNING,
											"Triple object does not match range definition"),
									user, null));
				}
				if (warningDomain) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.preRenderMessage(
									new Message(Message.Type.WARNING,
											"Triple subject does not match domain definition"),
									user, null));
				}

				DelegateRenderer.getInstance().render(section, user, string);

				if (warningRange) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.postRenderMessage(
									new Message(Message.Type.WARNING,
											""), user, null));
				}
				if (warningDomain) {
					string.append(
							DefaultErrorRenderer.INSTANCE_WARNING.postRenderMessage(
									new Message(Message.Type.WARNING,
											""), user, null));
				}

			}

		}
	}

	class SimpleTurtlePredicate extends IRITermRef implements TypeRestrictedReference {

		public SimpleTurtlePredicate() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("\\b([^\\s]*)::", Pattern.DOTALL, 1));
			c.addConstraint(SingleChildConstraint.getInstance());
			c.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(c);
		}

		@Override
		public boolean checkTypeConstraints(Section<? extends SimpleTerm> s) {
			Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(
					s.get().getTermIdentifier(s));
			if (info != null) {
				if (info instanceof Map) {
					Set keyset = ((Map) info).keySet();
					for (Object key : keyset) {
						if (((Map) info).get(key) instanceof RDFSTermCategory) {
							RDFSTermCategory rdfsTermCategory = (RDFSTermCategory) ((Map) info).get(key);
							if (rdfsTermCategory.equals(RDFSTermCategory.Class)
									|| rdfsTermCategory.equals(RDFSTermCategory.Individual)) {
								return false;
							}
						}
					}
				}

			}
			return true;
		}

		@Override
		public String getMessageForConstraintViolation(Section<? extends SimpleTerm> s) {
			return "only properties allowed here";
		}
	}

	class SimpleTurtleSubject extends IRITermRef {

		public SimpleTurtleSubject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
		}

	}

	class SimpleTurtleObjectSection extends AbstractType {

		public SimpleTurtleObjectSection() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("::\\s(.*)", Pattern.DOTALL, 1));
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);

			this.addChildType(new TurtleObjectLiteral());
			this.addChildType(new SimpleTurtleObjectRef());

		}
	}

	class SimpleTurtleObjectRef extends IRITermRef {

		public SimpleTurtleObjectRef() {
			this.setSectionFinder(new AllTextFinderTrimmed());
		}
	}

	class TripleCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<TripleMarkup> {

		@Override
		public void insertIntoRepository(Section<TripleMarkup> section) {

			List<Section<IRITermRef>> found = new ArrayList<Section<IRITermRef>>();
			Node subURI = null;
			Node predURI = null;
			Node objURI = null;

			Section<SimpleTurtleObjectSection> objectSec = Sections.findSuccessor(
					section, SimpleTurtleObjectSection.class);
			Section<TurtleObjectLiteralText> literalSec = Sections.findSuccessor(
					objectSec, TurtleObjectLiteralText.class);

			Sections.findSuccessorsOfType(section, IRITermRef.class, found);

			if (found.size() >= 2) {
				Section<IRITermRef> subject = found.get(0);
				Section<IRITermRef> predicate = found.get(1);
				subURI = RDFSUtil.getURI(subject);
				predURI = RDFSUtil.getURI(predicate);

				if (found.size() == 3) {

					Section<IRITermRef> object = found.get(2);
					objURI = RDFSUtil.getURI(object);
				}
				else if (literalSec != null) {
					objURI = Rdf2GoCore.getInstance().createLiteral(
							literalSec.getText());
				}

			}

			if (subURI != null && predURI != null && objURI != null) {

				Rdf2GoCore.getInstance().addStatement(subURI.asResource(),
						predURI.asURI(), objURI, section);
			}

		}

	}

	@Override
	public KnowledgeUnitCompileScript getCompileScript() {
		return new TripleCompileScript();
	}
}
