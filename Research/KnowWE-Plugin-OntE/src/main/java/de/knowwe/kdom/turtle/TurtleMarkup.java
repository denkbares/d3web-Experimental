package de.knowwe.kdom.turtle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.SyntaxError;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.sectionFinder.AllBeforeTypeSectionFinder;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.core.renderer.FontColorRenderer;
import de.knowwe.termObject.OWLClassDefinition;
import de.knowwe.termObject.OWLObjectPropertyDefinition;

public class TurtleMarkup extends DefaultAbstractKnowWEObjectType{

	public TurtleMarkup() {
		
		this.setSectionFinder(new RegexSectionFinder("<.*?::.*>"));


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
		this.addSubtreeHandler(Priority.HIGHER, new ObjectPropertyDefintionChecker());
		this.addSubtreeHandler(new ClassDefintionChecker());

	}
	
	class TurtlePredicate extends DefaultAbstractKnowWEObjectType implements KnowWETerm<String> {

		public TurtlePredicate() {
			this.setSectionFinder(new RegexSectionFinder("\\b([^\\s]*)::", 0));
			this.addSubtreeHandler(Priority.LOWER, new TermChecker(new String[] {
					"type", "subClassOf", "domain", "range" }));
		}

		@Override
		public String getTermName(Section<? extends KnowWETerm<String>> s) {
			String text = s.getOriginalText();
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


		}
	}

	class TurtleObject extends DefaultAbstractKnowWEObjectType {
		public TurtleObject() {
			this.setCustomRenderer(FontColorRenderer.getRenderer(FontColorRenderer.COLOR6));
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(new SingleChildConstraint());
			this.setSectionFinder(c);
			this.addSubtreeHandler(Priority.LOWER, new TermChecker(new String[] {
				"Class", "ObjectProperty" }));
		}
	}

	private class TermChecker extends GeneralSubtreeHandler<KnowWEObjectType> {

			String knownObjectTerms[] = null;

			public TermChecker(String[] values) {
				this.knownObjectTerms = values;
			}

			@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<KnowWEObjectType> s) {
				String termName = s.getOriginalText();
				if(s.get() instanceof KnowWETerm) {
					termName = ((KnowWETerm)s.get()).getTermName(s);
				}
				
				boolean found = false;
				for (String string : knownObjectTerms) {
				if (termName.equalsIgnoreCase(string)) {
						found = true;
					}

				}
				TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
				boolean defined = terminologyHandler.isDefinedTerm(article,
						termName, KnowWETerm.GLOBAL);
				if (!found && !defined) {
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
	
	class ClassDefintionChecker extends GeneralSubtreeHandler<TurtleMarkup> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TurtleMarkup> s) {
			Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
			Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
			Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
			if (o != null && p != null) {
				if (o.getOriginalText().equalsIgnoreCase("Class")) {
					if (p.getOriginalText().startsWith("type")) {
						subject.setType(new OWLClassDefinition());
					}

				}
			}
			return null;
		}
	}

	class ObjectPropertyDefintionChecker extends GeneralSubtreeHandler<TurtleMarkup> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<TurtleMarkup> s) {
			Section<TurtleSubject> subject = s.findSuccessor(TurtleSubject.class);
			Section<TurtleObject> o = s.findSuccessor(TurtleObject.class);
			Section<TurtlePredicate> p = s.findSuccessor(TurtlePredicate.class);
			if (o != null && p != null) {
				if (o.getOriginalText().equalsIgnoreCase("ObjectProperty")) {
					if (p.getOriginalText().startsWith("type")) {
						subject.setType(new OWLObjectPropertyDefinition());
					}

				}
			}
			return null;
		}
	}

}
