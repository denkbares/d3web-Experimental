package de.d3web.we.kdom.idProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.ISectionFinder;
import de.d3web.we.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.kdom.type.AnonymousType;
import de.d3web.we.object.QuestionReference;
import de.d3web.we.object.QuestionnaireReference;
import de.d3web.we.object.SolutionReference;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.core.renderer.FontColorRenderer;

public class IDPropertyType extends DefaultAbstractKnowWEObjectType {

	public IDPropertyType() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		AnonymousType colon = new AnonymousType("colon");
		colon.setSectionFinder(new StringSectionFinderUnquoted(":"));
		this.addChildType(colon);

		this.addChildType(new IDObjectType());

		this.addChildType(new IDPropertyDefinition());

	}

	class IDObjectType extends DefaultAbstractKnowWEObjectType {

		public IDObjectType() {
			AllTextFinderTrimmed allTextFinderTrimmed = new AllTextFinderTrimmed();
			ConstraintSectionFinder cs = new ConstraintSectionFinder(
					allTextFinderTrimmed,
					SingleChildConstraint.getInstance());
			this.setSectionFinder(cs);
			this.addSubtreeHandler(new SubtreeHandler<AnonymousType>() {

				@Override
				public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<AnonymousType> s) {
					TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(article.getWeb());
					String termName = KnowWEUtils.trimQuotes(s.getOriginalText());
					if (terminologyHandler.isDefinedTerm(article, termName,
							KnowWETerm.LOCAL)) {
						Section<? extends TermDefinition> termDefinitionSection = terminologyHandler.getTermDefiningSection(
								article, termName, KnowWETerm.LOCAL);
						Class<?> objectClazz = termDefinitionSection.get().getTermObjectClass();
						if (Question.class.isAssignableFrom(objectClazz)) {
							s.setType(new QuestionReference());
							return new ArrayList<KDOMReportMessage>(0);
						}
						if (QContainer.class.isAssignableFrom(objectClazz)) {
							s.setType(new QuestionnaireReference());
							return new ArrayList<KDOMReportMessage>(0);
						}
						if (Solution.class.isAssignableFrom(objectClazz)) {
							s.setType(new SolutionReference());
							return new ArrayList<KDOMReportMessage>(0);
						}

						return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
								termName + "is defined as: "
										+ objectClazz.getName()
										+ " - expected was Question or Questionnaire"));
					}

					return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(
							"Could not find '" + termName
									+ "' - expected was Question or Questionnaire"));
				}
			});
		}
	}

	class IDPropertyDefinition extends TermDefinition<String> {

		public IDPropertyDefinition() {
			super(String.class, true);
			ISectionFinder sectionFinder = new ConstraintSectionFinder(
					new AllTextFinderTrimmed(), SingleChildConstraint.getInstance());
			this.setSectionFinder(sectionFinder);
			this.setCustomRenderer(FontColorRenderer.getRenderer(FontColorRenderer.COLOR4));
			this.addSubtreeHandler(new ChangeWarningSubtreeHandler());
			this.addSubtreeHandler(new CreateIDPropertyHandler());
		}

		@Override
		public String getTermName(Section<? extends KnowWETerm<String>> s) {
			return KnowWEUtils.trimQuotes(s.getOriginalText());
		}

	}

	class ChangeWarningSubtreeHandler extends SubtreeHandler<IDPropertyDefinition> {

		@Override
		public Collection create(KnowWEArticle article, Section<IDPropertyDefinition> s) {

			/*
			 * NOTICE: here 'null' needs to be returned!
			 */
			return null;
			/*
			 * because a message might have been stored via the destroy method
			 * @see destroy()
			 */
		}

		@Override
		public void destroy(KnowWEArticle article, Section<IDPropertyDefinition> s) {

			Section<? extends KnowWEObjectType> sectionToStoreFor = article.findSection(s.getID());
			String text = " removed: " + s.getOriginalText();
			if (sectionToStoreFor != null) {
				text += " - changed to: " + sectionToStoreFor.getOriginalText();
			}

			if (sectionToStoreFor == null) {
				// has completely been removed => store message elsewhere
				// TODO do
				sectionToStoreFor = article.getSection();
			}
			FinalObjectModificationError m = new FinalObjectModificationError(text);
			KnowWEUtils.storeSingleMessage(article, sectionToStoreFor, getClass(),
					KDOMError.class, m);
		}

	}

	class FinalObjectModificationError extends KDOMError {

		private String text = "(no detail)";

		public FinalObjectModificationError(String text) {
			this.text = text;
		}

		@Override
		public String getVerbalization() {
			return "Modified final object: " + text;
		}

	}

	class CreateIDPropertyHandler extends D3webSubtreeHandler<IDPropertyDefinition> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<IDPropertyDefinition> s) {
			// TODO: actually create the property
			return null;
		}

		@Override
		public void destroy(KnowWEArticle article, Section<IDPropertyDefinition> s) {
			// TODO: actually destroy the property
		}

	}

}
