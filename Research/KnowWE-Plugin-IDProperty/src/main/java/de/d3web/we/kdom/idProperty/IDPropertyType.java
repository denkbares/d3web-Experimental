package de.d3web.we.kdom.idProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import de.d3web.core.knowledge.InfoStore;
import de.d3web.core.knowledge.terminology.NamedObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.Property;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.StringDefinition;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.rendering.StyleRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.kdom.type.AnonymousType;
import de.d3web.we.object.D3webTermReference;
import de.d3web.we.object.QuestionReference;
import de.d3web.we.object.QuestionnaireReference;
import de.d3web.we.object.SolutionReference;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.utils.MessageUtils;

/**
 * 
 * Linebase markup for defining stable IDs to objects, exmaple:
 * 
 * weight: weigthIdentifier
 * 
 * @author Jochen
 * @created 14.12.2010
 */
public class IDPropertyType extends AbstractType {

	public IDPropertyType() {
		this.setSectionFinder(new AllTextFinderTrimmed());

		AnonymousType colon = new AnonymousType("colon");
		colon.setSectionFinder(new StringSectionFinderUnquoted(":"));
		this.addChildType(colon);

		this.addChildType(new IDObjectType());

		this.addChildType(new IDPropertyDefinition());

	}

	class IDObjectType extends AbstractType {

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

	/**
	 * A type for the unique stable ID-Property. The uniqueness is asserted by
	 * being a TermDefinition. Stability is not (strongly) asserted but an
	 * corresponding error is thrown if the section changes.
	 * 
	 * 
	 * @author Jochen
	 * @created 14.12.2010
	 */
	class IDPropertyDefinition extends StringDefinition {

		public IDPropertyDefinition() {
			SectionFinder sectionFinder = new ConstraintSectionFinder(
					new AllTextFinderTrimmed(), SingleChildConstraint.getInstance());
			this.setSectionFinder(sectionFinder);
			this.setCustomRenderer(StyleRenderer.SOLUTION);
			this.addSubtreeHandler(new CreateIDPropertyHandler());
			this.addSubtreeHandler(new ChangeWarningSubtreeHandler());
		}

		@Override
		public String getTermName(Section<? extends KnowWETerm<String>> s) {
			return KnowWEUtils.trimQuotes(s.getOriginalText());
		}

	}

	/**
	 * The only purpose of this handler is to throw an error/warning if a
	 * section of type IDPropertyDefinition is changed
	 * 
	 * 
	 * @author Jochen
	 * @created 14.12.2010
	 */
	class ChangeWarningSubtreeHandler extends SubtreeHandler<IDPropertyDefinition> {

		@Override
		public Collection create(KnowWEArticle article, Section<IDPropertyDefinition> s) {

			/*
			 * NOTICE: here 'null' needs to be returned!
			 */
			return null;
			/*
			 * because a message might have been stored via the destroy method
			 * 
			 * @see destroy()
			 */
		}

		@Override
		public void destroy(KnowWEArticle article, Section<IDPropertyDefinition> s) {

			Section<? extends Type> sectionToStoreFor = article.findSection(s.getID());
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

	/**
	 * 
	 * This handler actually creates the property entry for the object
	 * 
	 * @author Jochen
	 * @created 14.12.2010
	 */
	class CreateIDPropertyHandler extends D3webSubtreeHandler<IDPropertyDefinition> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<IDPropertyDefinition> s) {

			Section<D3webTermReference> idobjectSection = Sections.findSuccessor(
					s.getFather(),
					D3webTermReference.class);
			Section<IDPropertyDefinition> propertySection = Sections.findSuccessor(s,
					IDPropertyDefinition.class);
			if (idobjectSection == null) return null;
			Object object = idobjectSection.get().getTermObject(article,
					idobjectSection);
			if (object == null) return null; // errormessage generated by other
			// subtreehandler
			if (propertySection == null) return null;
			Property<?> property = Property.getProperty("IDProperty", String.class);
			if (property == null) return null;

			String content = propertySection.get().getTermName(
					propertySection);

			Object value;
			try {
				value = property.parseValue(content);
			}
			catch (NoSuchMethodException e) {
				return MessageUtils.syntaxErrorAsList("The property " + property
							+ " is not supported by the %%Propery markup.");
			}
			catch (IllegalArgumentException e) {
				return MessageUtils.syntaxErrorAsList("The property value \"" + content
							+ "\" is not compatible with the property " + property);
			}
			if (object instanceof NamedObject) {
				((NamedObject) object).getInfoStore().addValue(property,
						InfoStore.NO_LANGUAGE, value);
			}
			return new ArrayList<KDOMReportMessage>(0);
		}

		@Override
		public void destroy(KnowWEArticle article, Section<IDPropertyDefinition> s) {
			Section<D3webTermReference> idobjectSection = Sections.findSuccessor(
					s.getFather(), D3webTermReference.class);
			Section<IDPropertyDefinition> propertySection = Sections.findSuccessor(
					s.getFather(), IDPropertyDefinition.class);
			if (idobjectSection == null) return;
			Object object = idobjectSection.get().getTermObject(article,
					idobjectSection);
			if (object == null) return; // errormessage generated by other
			// subtreehandler
			if (propertySection == null) return;
			Property<?> property = Property.getProperty("IDProperty", String.class);
			if (property == null) return;
			if (object instanceof NamedObject) {
				((NamedObject) object).getInfoStore().remove(property);
			}
		}

	}

}
