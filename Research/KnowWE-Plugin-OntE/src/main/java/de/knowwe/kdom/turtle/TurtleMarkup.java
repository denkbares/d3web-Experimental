package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.report.message.UnexpectedSequence;
import de.d3web.we.kdom.sectionFinder.AllBeforeTypeSectionFinder;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.d3web.we.kdom.sectionFinder.ConditionalSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.onte.owl.terminology.URIUtil;
import de.knowwe.termObject.BasicVocabularyReference;
import de.knowwe.termObject.LocalConceptDefinition;
import de.knowwe.termObject.LocalConceptReference;
import de.knowwe.termObject.OWLTermReference;
import de.knowwe.termObject.URIObject;
import de.knowwe.termObject.URITermDefinition;
import de.knowwe.termObject.URIObject.URIObjectType;

public class TurtleMarkup extends AbstractType {

	public static final StyleRenderer PROPERTY_RENDERER = new StyleRenderer(
			"color:rgb(40, 40, 160)");
	public static final StyleRenderer INDIVIDUAL_RENDERER = new StyleRenderer(
			"color:rgb(0, 128, 0)");

	public TurtleMarkup() {

		this.setSectionFinder(new RegexSectionFinder("<.*?::.*>"));

		// TODO: Aufteilen in Def-Turtle und normales Turle

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

	}

	class TurtlePredicate extends AbstractType implements KnowWETerm<String> {

		public TurtlePredicate() {
			this.setSectionFinder(new RegexSectionFinder("\\b([^\\s]*)::", 0));
			this.addSubtreeHandler(Priority.DEFAULT, new BasicVocTermChecker(
					URIUtil.PREDICATE_VOCABULARY));
			this.addSubtreeHandler(Priority.LOWER, new TermReferenceChecker());
		}

		@Override
		public String getTermName(Section<? extends KnowWETerm<String>> s) {
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
		public int getTermScope() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void setTermScope(int termScope) {
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

	class SubjectReference extends OWLTermReference {
		public SubjectReference() {
			this.setSectionFinder(new AllTextFinderTrimmed());
		}
	}

	class SubjectDefinition extends URITermDefinition {
		final StyleRenderer CLASS_RENDERER = new StyleRenderer(
				"color:rgb(152, 180, 12)");

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
		public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
			String text = s.getOriginalText();
			if (text.trim().equals(DEF_PREFIX)) return s.getArticle().getTitle();
			return text.substring(3).trim();
		}

		@Override
		protected URIObjectType getURIObjectType() {
			return URIObjectType.unspecified;
		}

		class URIObjectTypeChecker extends GeneralSubtreeHandler<SubjectDefinition> {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<SubjectDefinition> s) {
				Section<TurtleMarkup> turtle = Sections.findAncestorOfType(s,
						TurtleMarkup.class);
				List<Section<BasicVocabularyReference>> l = new ArrayList<Section<BasicVocabularyReference>>();
				Sections.findSuccessorsOfType(turtle, BasicVocabularyReference.class, l);

				if (l.size() == 2) {
					URI predURI = l.get(0).get().getNode(l.get(0));
					URI objURI = l.get(1).get().getNode(l.get(1));
					URIObject termObject = s.get().getTermObject(article, s);
					if (termObject == null) return new ArrayList<KDOMReportMessage>(0);
					URIObjectType uriType = termObject.getURIType();

					if (predURI.equals(RDF.type)) {
						if (objURI.equals(OWL.Class)) {
							if (uriType == URIObjectType.unspecified) {
								termObject.setURIType(URIObjectType.Class);
							}
							else {
								return Arrays.asList((KDOMReportMessage) new UnexpectedSequence(
										s.getOriginalText()));
							}
						}
						else if (objURI.equals(OWL.ObjectProperty)) {
							if (uriType == URIObjectType.unspecified) {
								termObject.setURIType(URIObjectType.objectProperty);
							}
							else {
								return Arrays.asList((KDOMReportMessage) new UnexpectedSequence(
											s.getOriginalText()));
							}

						}
						else if (objURI.equals(OWL.DatatypeProperty)) {
							if (uriType == URIObjectType.unspecified) {
								termObject.setURIType(URIObjectType.datatypeProperty);
							}
							else {
								return Arrays.asList((KDOMReportMessage) new UnexpectedSequence(
											s.getOriginalText()));
							}

						}
						else if (objURI.equals(OWL.Thing)) {
							if (uriType == URIObjectType.unspecified) {
								termObject.setURIType(URIObjectType.instance);
							}
							else {
								return Arrays.asList((KDOMReportMessage) new UnexpectedSequence(
											s.getOriginalText()));
							}

						}
					}

				}

				return new ArrayList<KDOMReportMessage>(0);
			}

		}
	}

	class TurtleObject extends AbstractType {
		public TurtleObject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
			this.addSubtreeHandler(Priority.DEFAULT, new BasicVocTermChecker(
					URIUtil.OBJECT_VOCABULARY));
			this.addSubtreeHandler(Priority.LOWER, new TermReferenceChecker());
		}
	}

	private class TermReferenceChecker extends GeneralSubtreeHandler<Type> {

		// String knownObjectTerms[] = null;
		//
		// public TermChecker(String[] values) {
		// this.knownObjectTerms = values;
		// }

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Type> s) {

			String termName = s.getOriginalText();
			if (s.get() instanceof KnowWETerm) {
				termName = ((KnowWETerm) s.get()).getTermName(s);
			}

			boolean thiss = false;
			boolean datavalue = false;
			// boolean found = URIUtil.checkForKnownTerms(termName,
			// knownObjectTerms);
			// if (found) {
			// s.setType(new BasicVocabularyReference());
			// }
			// else {
			if (termName.equals(LocalConceptDefinition.LOCAL_KEY)) {
				thiss = true;
				s.setType(new LocalConceptReference());
			}
			else {
				if (s.get() instanceof TurtleObject) {
					List<Section<OWLTermReference>> refs = new ArrayList<Section<OWLTermReference>>();
					Sections.findSuccessorsOfType(
							s.getFather(), OWLTermReference.class, refs);
					if (refs.size() > 0) {
						Section<OWLTermReference> prop = refs.get(refs.size() - 1);
						if (prop.get().getTermObject(article, prop).getURIType() == URIObjectType.datatypeProperty) {
							s.setType(new DataTypeValue());
							datavalue = true;
						}
					}
				}
				if (!datavalue) {
					s.setType(new OWLTermReference());
				}
			}

			TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
			boolean defined = terminologyHandler.isDefinedTerm(article,
						termName, KnowWETerm.GLOBAL);

			if (!defined && !thiss && !datavalue) {
				return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
							s.getOriginalText()));
			}

			return new ArrayList<KDOMReportMessage>(0);
		}
	}

	private class BasicVocTermChecker extends GeneralSubtreeHandler<Type> {

		String knownObjectTerms[] = null;

		public BasicVocTermChecker(String[] values) {
			this.knownObjectTerms = values;
		}

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Type> s) {
			// if turtle object check for datatype prop!
			String termName = s.getOriginalText();
			if (s.get() instanceof KnowWETerm) {
				termName = ((KnowWETerm) s.get()).getTermName(s);
			}

			// boolean thiss = false;
			boolean found = URIUtil.checkForKnownTerms(termName, knownObjectTerms);
			if (found) {
				s.setType(new BasicVocabularyReference());
			}
			else if (termName.equals(LocalConceptDefinition.LOCAL_KEY)) {
				// thiss = true;
				s.setType(new LocalConceptReference());
			}
			else {
				// do nothing
			}

			// TerminologyHandler terminologyHandler =
			// KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
			// boolean defined = terminologyHandler.isDefinedTerm(article,
			// termName, KnowWETerm.GLOBAL);
			//
			// if (!found && !defined && !thiss) {
			// return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
			// s.getOriginalText()));
			// }

			return new ArrayList<KDOMReportMessage>(0);
		}
	}

	class TripleChecker extends GeneralSubtreeHandler<TurtleMarkup> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {
			if (Sections.findSuccessor(s, TurtlePredicate.class) == null) {
				return Arrays.asList((KDOMReportMessage) new SyntaxError(
						"TurtleMarkup: Predicate missing!"));
			}
			if (Sections.findSuccessor(s, TurtleObject.class) == null) {
				return Arrays.asList((KDOMReportMessage) new SyntaxError(
						"TurtleMarkup: Object missing!"));
			}

			return new ArrayList<KDOMReportMessage>();
		}

	}

}
