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

package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.core.compile.IncrementalConstraint;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.AllBeforeTypeSectionFinder;
import de.knowwe.kdom.sectionFinder.ConditionalSectionFinder;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.onte.owl.terminology.URIUtil;
import de.knowwe.termObject.AbstractIRITermDefinition;
import de.knowwe.termObject.BasicVocabularyReference;
import de.knowwe.termObject.IRIEntityType;
import de.knowwe.termObject.IRIEntityType.IRIDeclarationType;
import de.knowwe.termObject.IRITermReference;
import de.knowwe.termObject.LocalConceptDefinition;
import de.knowwe.termObject.LocalConceptReference;
import de.knowwe.termObject.RDFNodeType;
import de.knowwe.tools.ToolMenuDecoratingRenderer;
import de.knowwe.util.DelegateDestroyHandler;

public class TurtleMarkup extends AbstractType {

	public static final StyleRenderer PROPERTY_RENDERER = new StyleRenderer(
			"color:rgb(40, 40, 160)");
	public static final StyleRenderer INDIVIDUAL_RENDERER = new StyleRenderer(
			"color:rgb(0, 128, 0)");

	public TurtleMarkup() {

		this.setSectionFinder(new RegexSectionFinder("\\[.*?::.*?\\]", Pattern.MULTILINE));

		AnonymousTypeInvisible start = new AnonymousTypeInvisible("turtlestart");
		start.setSectionFinder(new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				return SectionFinderResult.createSingleItemResultList(0, 1);
			}
		});
		this.addChildType(start);

		AnonymousTypeInvisible stop = new AnonymousTypeInvisible("turtlestop");
		stop.setSectionFinder(new SectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
				return SectionFinderResult.createSingleItemResultList(text.length() - 1,
						text.length());
			}
		});
		this.addChildType(stop);

		TurtlePredicate predicate = new TurtlePredicate();
		this.addChildType(predicate);

		TurtleSubject subject = new TurtleSubject();
		subject.setSectionFinder(new AllBeforeTypeSectionFinder(predicate));

		this.addChildType(subject);

		this.addChildType(new TurtleObject());

		this.addSubtreeHandler(Priority.HIGHER, new TripleChecker());

		this.addSubtreeHandler(Priority.LOWEST, new TurtleRDF2GoCompiler());

		this.setCustomRenderer(new TurtleMarkupDivWrapper());

	}

	public static Section<? extends Type> getSubjectSection(Section<TurtleMarkup> s) {
		List<Section<? extends Type>> components = getTurtleComponents(s);
		if (components.size() == 3) {
			return components.get(0);
		}

		return null;
	}

	public static Section<? extends Type> getPredicateSection(Section<TurtleMarkup> s) {

		List<Section<? extends Type>> components = getTurtleComponents(s);
		if (components.size() == 3) {
			return components.get(1);
		}

		return null;
	}

	private static List<Section<? extends Type>> getTurtleComponents(Section<TurtleMarkup> s) {
		List<Section<? extends Type>> components = new ArrayList<Section<? extends Type>>();
		List<Section<? extends Type>> children = s.getChildren();
		for (Section<? extends Type> section : children) {
			if (section.get() instanceof AnonymousType
					|| section.get() instanceof PlainText) {
			}
			else {
				components.add(section);
			}
		}
		return components;
	}

	public static Section<? extends Type> getObjectSection(Section<TurtleMarkup> s) {
		List<Section<? extends Type>> components = getTurtleComponents(s);
		if (components.size() == 3) {
			return components.get(2);
		}

		return null;
	}

	class TurtlePredicate extends AbstractType implements KnowWETerm<String> {

		public TurtlePredicate() {
			this.setSectionFinder(new RegexSectionFinder("\\b([^\\s]*)::", 0));
			this.addSubtreeHandler(Priority.DEFAULT, new BasicVocTermChecker(
					URIUtil.PREDICATE_VOCABULARY));
			this.addSubtreeHandler(Priority.LOWER, new TermReferenceCheckerPredicate());
		}

		@Override
		public String getTermName(Section<? extends KnowWETerm<String>> s) {
			return getTermIdentifier(s);
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			String text = s.getOriginalText();
			// hack TODO remove
			if (text.endsWith("::")) {
				text = text.substring(0, text.length() - 2);
			}
			return text;
		}

		@Override
		public Class<String> getTermObjectClass() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Scope getTermScope() {
			// TODO Auto-generated method stub
			return Scope.LOCAL;
		}

		@Override
		public void setTermScope(Scope scope) {
			// TODO Auto-generated method stub

		}

	}

	class TurtleSubject extends AbstractType {

		public TurtleSubject() {
			this.addChildType(new LocalConceptDefinition());
			this.addChildType(new LocalConceptReference());
			this.addChildType(new SubjectDefinition());
			this.addChildType(new SubjectReference());

		}
	}

	class SubjectReference extends IRITermReference {

		public SubjectReference() {
			this.setSectionFinder(new AllTextFinderTrimmed());
		}
	}

	class SubjectDefinition extends AbstractIRITermDefinition implements
			IncrementalConstraint<TurtleMarkup> {

		final KnowWEDomRenderer<SubjectDefinition> CLASS_RENDERER =
				new ToolMenuDecoratingRenderer<SubjectDefinition>(
						new StyleRenderer("color:rgb(152, 180, 12)"));

		public final String DEF_PREFIX = "def";

		SubjectDefinition() {
			this.setCustomRenderer(CLASS_RENDERER);
			ConstraintSectionFinder finder = new ConstraintSectionFinder(
					new ConditionalSectionFinder(new AllTextSectionFinder()) {

						@Override
						protected boolean condition(String text, Section<?> father) {
							return text.startsWith(DEF_PREFIX);
						}
					});
			finder.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(finder);
			this.addSubtreeHandler(Priority.LOW, new URIObjectTypeChecker());
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<IRIEntityType>> s) {
			String text = s.getOriginalText();
			if (text.trim().equals(DEF_PREFIX)) return s.getArticle().getTitle();
			return text.substring(3).trim();
		}

		@Override
		protected IRIDeclarationType getIRIDeclarationType() {
			return IRIDeclarationType.UNSPECIFIED;
		}

		class URIObjectTypeChecker extends GeneralSubtreeHandler<SubjectDefinition> {

			@Override
			public void destroy(KnowWEArticle article, Section<SubjectDefinition> s) {

				IRIEntityType termObject = s.get().getTermObject(article, s);
				if (termObject == null) return;
				termObject.setIRIDeclarationType(IRIDeclarationType.UNSPECIFIED);
			}

			@Override
			public Collection<Message> create(KnowWEArticle article, Section<SubjectDefinition> s) {
				Section<TurtleMarkup> turtle = Sections.findAncestorOfType(s,
						TurtleMarkup.class);
				List<Section<BasicVocabularyReference>> l = new ArrayList<Section<BasicVocabularyReference>>();
				Sections.findSuccessorsOfType(turtle, BasicVocabularyReference.class, l);

				if (l.size() == 2) {
					URI predURI = l.get(0).get().getNode(l.get(0));
					URI objURI = l.get(1).get().getNode(l.get(1));
					IRIEntityType termObject = s.get().getTermObject(article, s);
					if (termObject == null) return new ArrayList<Message>(0);
					IRIDeclarationType uriType = termObject.getIRIDeclarationType();

					if (predURI.equals(RDF.type)) {
						if (objURI.equals(OWL.Class)) {
							if (uriType == IRIDeclarationType.UNSPECIFIED) {
								termObject.setIRIDeclarationType(IRIDeclarationType.CLASS);
							}
							else {
								return Messages.asList(Messages.syntaxError(
										s.getOriginalText()));
							}
						}
						else if (objURI.equals(OWL.ObjectProperty)) {
							if (uriType == IRIDeclarationType.UNSPECIFIED) {
								termObject.setIRIDeclarationType(IRIDeclarationType.OBJECT_PROPERTY);
							}
							else {
								return Messages.asList(Messages.syntaxError(
											s.getOriginalText()));
							}

						}
						else if (objURI.equals(OWL.DatatypeProperty)) {
							if (uriType == IRIDeclarationType.UNSPECIFIED) {
								termObject.setIRIDeclarationType(IRIDeclarationType.DATATYPE_PROPERTY);
							}
							else {
								return Messages.asList(Messages.syntaxError(
											s.getOriginalText()));
							}

						}
						else if (objURI.equals(OWL.Thing)) {
							if (uriType == IRIDeclarationType.UNSPECIFIED) {
								termObject.setIRIDeclarationType(IRIDeclarationType.NAMED_INDIVIDUAL);
							}
							else {
								return Messages.asList(Messages.syntaxError(s.getOriginalText()));
							}

						}
					}

				}

				return new ArrayList<Message>(0);
			}

		}

		@Override
		public boolean violatedConstraints(KnowWEArticle article, Section<TurtleMarkup> s) {
			Section<? extends Type> grandfather = s.getFather().getFather();
			boolean reusedBy = grandfather.isReusedBy(article.getTitle());
			return !reusedBy;
		}
	}

	class TurtleObject extends AbstractType implements IncrementalConstraint<TurtleObject> {

		public TurtleObject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
			this.addSubtreeHandler(Priority.DEFAULT, new BasicVocTermChecker(
					URIUtil.OBJECT_VOCABULARY));
			this.addSubtreeHandler(Priority.LOWER, new TermReferenceCheckerObject());
		}

		@Override
		public boolean violatedConstraints(KnowWEArticle article, Section<TurtleObject> s) {
			List<Section<RDFNodeType>> list = new ArrayList<Section<RDFNodeType>>();
			Sections.findSuccessorsOfType(s.getFather(), RDFNodeType.class, list);
			boolean orHasSuccessorNotReusedBy = s.getFather().isOrHasSuccessorNotReusedBy(
					article.getTitle());
			return orHasSuccessorNotReusedBy;
		}
	}

	private class TermReferenceCheckerPredicate extends GeneralSubtreeHandler<Type> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<Type> s) {

			String termName = s.getOriginalText();
			if (s.get() instanceof KnowWETerm) {
				termName = ((KnowWETerm) s.get()).getTermIdentifier(s);
			}

			if (termName.equals(LocalConceptDefinition.LOCAL_KEY)) {
				s.setType(new LocalConceptReference());
			}
			else {

				IRITermReference termReference = new IRITermReference();
				s.setType(termReference);
			}

			return new ArrayList<Message>(0);
		}
	}

	private class TermReferenceCheckerObject extends GeneralSubtreeHandler<Type> {

		@Override
		public void destroy(KnowWEArticle article, Section<Type> s) {
			List<Section<RDFNodeType>> list = new ArrayList<Section<RDFNodeType>>();
			Sections.findSuccessorsOfType(s.getFather(), RDFNodeType.class, list);
			if (list.size() < 3) return;
			if (list.get(2).getID().equals(s.getID())) {
				s.setType(new TurtleObject(), false);
				s.setReusedBy(article.getTitle(), false);
			}
		}

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<Type> s) {

			String termName = s.getOriginalText();
			if (s.get() instanceof KnowWETerm) {
				termName = ((KnowWETerm) s.get()).getTermIdentifier(s);
			}

			boolean datavalue = false;

			if (termName.equals(LocalConceptDefinition.LOCAL_KEY)) {
				s.setType(new LocalConceptReference());
			}
			else {
				if (s.get() instanceof TurtleObject) {
					List<Section<IRITermReference>> refs = new ArrayList<Section<IRITermReference>>();
					Sections.findSuccessorsOfType(
							s.getFather(), IRITermReference.class, refs);
					if (refs.size() > 0) {
						Section predSec = getPredicateSection(Sections.findAncestorOfType(
								s, TurtleMarkup.class));
						if (predSec != null && predSec.get() instanceof IRITermReference) {
							Section<IRITermReference> prop = predSec;
							IRIEntityType termObject = prop.get().getTermObject(article, prop);
							if (termObject == null) return new ArrayList<Message>(0);

							if (termObject.getIRIDeclarationType() == IRIDeclarationType.DATATYPE_PROPERTY) {
								DataTypeValueTurtle dataTypeValue = new DataTypeValueTurtle();
								dataTypeValue.addSubtreeHandler(new DelegateDestroyHandler(
										this));
								s.setType(dataTypeValue);
								datavalue = true;
							}
						}
					}
				}
				if (!datavalue && s.get() instanceof TurtleObject) {
					IRITermReference termReference = new OWLTermReferenceTurtle();
					termReference.addSubtreeHandler(new DelegateDestroyHandler(
							this));
					s.setType(termReference);
				}
			}
			return new ArrayList<Message>(0);
		}
	}

	private class BasicVocTermChecker extends GeneralSubtreeHandler<Type> {

		String knownObjectTerms[] = null;

		public BasicVocTermChecker(String[] values) {
			this.knownObjectTerms = values;
		}

		@Override
		public Collection<Message> create(KnowWEArticle article, Section<Type> s) {
			// if turtle object check for datatype prop!
			String termName = s.getOriginalText();
			if (s.get() instanceof KnowWETerm) {
				termName = ((KnowWETerm) s.get()).getTermIdentifier(s);
			}

			boolean found = URIUtil.checkForKnownTerms(termName, knownObjectTerms);
			if (found) {
				s.setType(new BasicVocabularyReference());
			}
			else if (termName.equals(LocalConceptDefinition.LOCAL_KEY)) {
				s.setType(new LocalConceptReference());
			}

			return new ArrayList<Message>(0);
		}
	}

	class TripleChecker extends GeneralSubtreeHandler<TurtleMarkup> {

		@Override
		public Collection<Message> create(KnowWEArticle article, Section s) {
			if (Sections.findSuccessor(s, TurtlePredicate.class) == null) {
				return Messages.asList(Messages.syntaxError(
						"TurtleMarkup: Predicate missing!"));
			}
			if (Sections.findSuccessor(s, TurtleObject.class) == null) {
				return Messages.asList(Messages.syntaxError(
						"TurtleMarkup: Object missing!"));
			}

			return new ArrayList<Message>();
		}

	}

	/**
	 * We need this div wrapper, because HTML-Strict which is used by JSPWiki
	 * allows inline elements (e.g. span) only in block elements (e.g. div). If
	 * we don't wrap the inline elements certain browsers (e.g. webkit-based
	 * browsers like Chrome or Safari) will destroy the markup.
	 * 
	 * @author Sebastian Furth
	 * @created Mar 21, 2011
	 */
	class TurtleMarkupDivWrapper extends KnowWEDomRenderer<TurtleMarkup> {

		@Override
		public void render(KnowWEArticle article, Section<TurtleMarkup> sec, UserContext user, StringBuilder string) {
			StringBuilder inner = new StringBuilder();
			DelegateRenderer.getInstance().render(article, sec, user, inner);

			StringBuilder wrapper = new StringBuilder();
			wrapper.append("<div style='display: inline-block;'>");
			wrapper.append(inner.toString());
			wrapper.append("</div>\n");
			string.append(KnowWEUtils.maskHTML(wrapper.toString()));
		}

	}

}
