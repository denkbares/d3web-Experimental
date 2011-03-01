package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
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
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
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

public class TurtleMarkup extends DefaultAbstractKnowWEObjectType {

	public static final StyleRenderer PROPERTY_RENDERER = new StyleRenderer(
			"color:rgb(40, 40, 160)");
	public static final StyleRenderer INDIVIDUAL_RENDERER = new StyleRenderer(
			"color:rgb(0, 128, 0)");

	public TurtleMarkup() {

		this.setSectionFinder(new RegexSectionFinder("<.*?::.*>"));

		//TODO: Aufteilen in Def-Turtle und normales Turle
		
		AnonymousTypeInvisible start = new AnonymousTypeInvisible("turtlestart");
		start.setSectionFinder(new ISectionFinder() {
			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, KnowWEObjectType type) {
				return SectionFinderResult.createSingleItemResultList(0, 1);
			}
		});
		this.addChildType(start);

		AnonymousTypeInvisible stop = new AnonymousTypeInvisible("turtlestop");
		stop.setSectionFinder(new ISectionFinder() {

			@Override
			public List<SectionFinderResult> lookForSections(String text, Section<?> father, KnowWEObjectType type) {
				return SectionFinderResult.createSingleItemResultList(text.length() - 1,
						text.length());
			}
		});
		this.addChildType(stop);

		TurtlePredicate predicate = new TurtlePredicate();
		this.addChildType(predicate);

		TurtleSubject subject = new TurtleSubject();
		subject.setSectionFinder(AllBeforeTypeSectionFinder.createFinder(predicate));

		this.addChildType(subject);

		this.addChildType(new TurtleObject());

		this.addSubtreeHandler(Priority.HIGHER, new TripleChecker());
		// this.addSubtreeHandler(Priority.HIGHER, new
		// ObjectPropertyDefintionChecker());
		// this.addSubtreeHandler(Priority.HIGHER, new
		// DatatypePropertyDefintionChecker());
		// this.addSubtreeHandler(Priority.HIGHER, new ClassDefintionChecker());
		// this.addSubtreeHandler(Priority.HIGH, new
		// LocalPageInstanceDefintionChecker());
		// this.addSubtreeHandler(Priority.HIGH, new
		// InstanceDefintionChecker());
		// this.addSubtreeHandler(Priority.HIGH, new
		// IndirectInstanceDefintionChecker());
		// this.addSubtreeHandler(Priority.HIGH, new
		// IndirectClassDefintionChecker());

		this.addSubtreeHandler(Priority.LOWEST, new TurtleRDF2GoCompiler());

	}

	class TurtlePredicate extends DefaultAbstractKnowWEObjectType implements KnowWETerm<String> {

		public TurtlePredicate() {
			this.setSectionFinder(new RegexSectionFinder("\\b([^\\s]*)::", 0));
			this.addSubtreeHandler(Priority.LOW, new TermChecker(
					URIUtil.PREDICATE_VOCABULARY));
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

	class TurtleSubject extends DefaultAbstractKnowWEObjectType {
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
			this.addSubtreeHandler(Priority.LOWER, new URIObjectTypeChecker());
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
				Section<TurtleMarkup> turtle = s.findAncestorOfType(TurtleMarkup.class);
				List<Section<BasicVocabularyReference>> l = new ArrayList<Section<BasicVocabularyReference>>();
				turtle.findSuccessorsOfType(BasicVocabularyReference.class, l);

				if (l.size() == 2) {
					URI predURI = l.get(0).get().getURI(l.get(0));
					URI objURI = l.get(1).get().getURI(l.get(1));
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

	class TurtleObject extends DefaultAbstractKnowWEObjectType {
		public TurtleObject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
			this.addSubtreeHandler(Priority.LOW, new TermChecker(
					URIUtil.OBJECT_VOCABULARY));
		}
	}

	private class TermChecker extends GeneralSubtreeHandler<KnowWEObjectType> {

		String knownObjectTerms[] = null;

		public TermChecker(String[] values) {
			this.knownObjectTerms = values;
		}

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<KnowWEObjectType> s) {
			// if turtle object check for datatype prop!
			String termName = s.getOriginalText();
			if (s.get() instanceof KnowWETerm) {
				termName = ((KnowWETerm) s.get()).getTermName(s);
			}

			boolean thiss = false;
			boolean found = URIUtil.checkForKnownTerms(termName, knownObjectTerms);
			if (found) {
				s.setType(new BasicVocabularyReference());
			}else if(termName.equals(LocalConceptDefinition.LOCAL_KEY)) {
				thiss = true;
				s.setType(new LocalConceptReference());
			}
			else {
				s.setType(new OWLTermReference());
			}

			TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
			boolean defined = terminologyHandler.isDefinedTerm(article,
						termName, KnowWETerm.GLOBAL);

			if (!found && !defined && !thiss) {
				return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
							s.getOriginalText()));
			}

			return new ArrayList<KDOMReportMessage>();
		}



	}

	class TripleChecker extends GeneralSubtreeHandler<TurtleMarkup> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {
			if (s.findSuccessor(TurtlePredicate.class) == null) {
				return Arrays.asList((KDOMReportMessage) new SyntaxError(
						"TurtleMarkup: Predicate missing!"));
			}
			if (s.findSuccessor(TurtleObject.class) == null) {
				return Arrays.asList((KDOMReportMessage) new SyntaxError(
						"TurtleMarkup: Object missing!"));
			}

			return new ArrayList<KDOMReportMessage>();
		}

	}

	// class ClassDefintionChecker extends GeneralSubtreeHandler<TurtleMarkup> {
	//
	// ClassDefintionChecker(){
	// this.registerConstraintModule(new
	// SuccessorNotReusedConstraint<TurtleMarkup>());
	// }
	//		
	// @Override
	// public Collection<KDOMReportMessage> create(KnowWEArticle article,
	// Section<TurtleMarkup> s) {
	// Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
	// Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
	// Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
	// if (o != null && p != null) {
	// if (o.getOriginalText().equalsIgnoreCase("Class")) {
	// if (p.getOriginalText().startsWith("type")) {
	// subject.setType(new OWLClassDefinition());
	// }
	//
	// }
	// }
	// return new ArrayList<KDOMReportMessage>();
	// }
	// }

	// class LocalPageInstanceDefintionChecker extends
	// GeneralSubtreeHandler<TurtleMarkup> {
	//
	// LocalPageInstanceDefintionChecker() {
	// this.registerConstraintModule(new
	// SuccessorNotReusedConstraint<TurtleMarkup>());
	// }
	//
	// @Override
	// public Collection<KDOMReportMessage> create(KnowWEArticle article,
	// Section<TurtleMarkup> s) {
	// Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
	// Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
	// Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
	// if (subject == null && o != null && p != null) {
	// if (p.getOriginalText().startsWith("type")) {
	// if (o.getOriginalText().equalsIgnoreCase("Thing")) {
	// Section<AnonymousType> anoSec = s.findSuccessor(AnonymousType.class);
	// anoSec.setType(new LocalPageOWLInstanceDef());
	//
	// }
	// }
	// }
	// return new ArrayList<KDOMReportMessage>();
	// }
	// }
	// class InstanceDefintionChecker extends
	// GeneralSubtreeHandler<TurtleMarkup> {
	//
	// InstanceDefintionChecker(){
	// this.registerConstraintModule(new
	// SuccessorNotReusedConstraint<TurtleMarkup>());
	// }
	//		
	// @Override
	// public Collection<KDOMReportMessage> create(KnowWEArticle article,
	// Section<TurtleMarkup> s) {
	// Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
	// Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
	// Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
	// if (subject != null && o != null && p != null) {
	// if (p.getOriginalText().startsWith("type")) {
	// if (o.getOriginalText().equalsIgnoreCase("Thing")) {
	//
	// subject.setType(new OWLInstanceDefinition());
	//
	// }
	// }
	// }
	// return new ArrayList<KDOMReportMessage>();
	// }
	// }
	//	
	// class IndirectInstanceDefintionChecker extends
	// GeneralSubtreeHandler<TurtleMarkup> {
	//
	// IndirectInstanceDefintionChecker(){
	// this.registerConstraintModule(new
	// SuccessorNotReusedConstraint<TurtleMarkup>());
	// }
	//		
	// @Override
	// public Collection<KDOMReportMessage> create(KnowWEArticle article,
	// Section<TurtleMarkup> s) {
	// Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
	// Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
	// Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
	// if (subject != null && o != null && p != null) {
	// if (p.getOriginalText().startsWith("isA")) {
	// //if (o.getOriginalText().equalsIgnoreCase("Thing")) {
	//
	// o.setType(new OWLTermReference());
	// subject.setType(new OWLIndirectInstanceDefinition());
	// //}
	// }
	// }
	// return new ArrayList<KDOMReportMessage>();
	// }
	// }

	// class ObjectPropertyDefintionChecker extends
	// GeneralSubtreeHandler<TurtleMarkup> {
	//		
	// ObjectPropertyDefintionChecker(){
	// this.registerConstraintModule(new
	// SuccessorNotReusedConstraint<TurtleMarkup>());
	// }
	//
	// @Override
	// public Collection<KDOMReportMessage> create(KnowWEArticle article,
	// Section<TurtleMarkup> s) {
	// Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
	// Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
	// Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
	// if (o != null && p != null) {
	// if (o.getOriginalText().equalsIgnoreCase("ObjectProperty")) {
	// if (p.getOriginalText().startsWith("type")) {
	// subject.setType(new OWLObjectPropertyDefinition());
	// }
	//
	// }
	// }
	// return new ArrayList<KDOMReportMessage>();
	// }
	// }

	// class DatatypePropertyDefintionChecker extends
	// GeneralSubtreeHandler<TurtleMarkup> {
	//
	// DatatypePropertyDefintionChecker(){
	// this.registerConstraintModule(new
	// SuccessorNotReusedConstraint<TurtleMarkup>());
	// }
	//		
	// @Override
	// public Collection<KDOMReportMessage> create(KnowWEArticle article,
	// Section<TurtleMarkup> s) {
	// Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
	// Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
	// Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
	// if (o != null && p != null) {
	// if (o.getOriginalText().equalsIgnoreCase("DatatypeProperty")) {
	// if (p.getOriginalText().startsWith("type")) {
	// subject.setType(new OWLDatatypePropertyDefinition());
	// }
	//
	// }
	// }
	// return new ArrayList<KDOMReportMessage>();
	// }
	//		
	// }

}
