/*
 * Copyright (C) 2013 denkbares GmbH
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.strings.Strings;
import de.d3web.utils.Log;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.annotation.type.list.ListObjectIdentifier;
import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.CompositeRenderer;
import de.knowwe.tools.ToolMenuDecoratingRenderer;
import de.knowwe.wisskont.dss.KnowledgeBaseInstantiation;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 02.07.2013
 */
public class ValuesMarkup extends RelationMarkup implements KnowledgeUnit {

	private static final String key = "Werte";
	public static final String VALUE_PROPERTY = "wertebereich";

	/**
	 * 
	 */
	public ValuesMarkup() {
		super(key);
		Type contentType = de.knowwe.core.kdom.Types.successor(this,
				RelationMarkupContentType.class);
		boolean replaced = de.knowwe.core.kdom.Types.replaceType(contentType,
				ListObjectIdentifier.class,
				new ValueDefinitionListElement(new OIDeleteItemRenderer()));

		this.addChildType(0, new NumericalValueMarkerType());

		if (!replaced) {
			Log.severe("Type NOT REPLACED ! ");
		}
	}

	public static class NumericalValueMarkerType extends AbstractType {

		/**
		 * 
		 */
		public NumericalValueMarkerType() {
			this.setSectionFinder(new RegexSectionFinder("num\\.\\s(.*?)", Pattern.DOTALL
					| Pattern.MULTILINE));
		}
	}

	@Override
	public String getName() {
		return "Werte-Bereich des Begriffs";
	}

	@Override
	public boolean isInverseDir() {
		return false;
	}

	@Override
	public URI getRelationURI() {
		return createURI(VALUE_PROPERTY);
	}

	class ValueDefinitionListElement extends IncrementalTermDefinition<String> {

		/**
		 * @param termObjectClass
		 */
		public ValueDefinitionListElement(Renderer r) {
			super(String.class);
			this.setSectionFinder(new AllTextFinderTrimmed());
			CompositeRenderer renderer = new CompositeRenderer(r,
					new ReferenceSurroundingRenderer());
			this.setRenderer(new ToolMenuDecoratingRenderer(renderer));
		}

		@Override
		public String getTermName(Section<? extends Term> s) {
			@SuppressWarnings("rawtypes")
			Section<IncrementalTermDefinition> conceptDefinition = MarkupUtils.getConceptDefinition(s);
			if (conceptDefinition == null) {
				return null; // do nothing
			}
			String valueText = s.getText().trim();
			if (valueText.length() == 0) {
				return null;
			}
			return Strings.unquote(getLongAnswerName(
					conceptDefinition.get().getTermName(conceptDefinition), s.getText().trim()));
		}

	}

	@Override
	public KnowledgeUnitCompileScript<ValuesMarkup> getCompileScript() {
		return new ValuesD3webObjectsCompileScript();
	}

	class ValuesD3webObjectsCompileScript extends AbstractKnowledgeUnitCompileScript<ValuesMarkup> {

		@Override
		public Collection<Section<? extends Term>> getExternalReferencesOfKnowledgeUnit(Section<? extends KnowledgeUnit> section) {
			return Collections.emptyList();
		}

		@Override
		public void insertIntoRepository(Section<ValuesMarkup> section) {
			KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB,
					KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE);
			if (knowledgeBase == null) return;

			TerminologyManager manager = knowledgeBase.getManager();

			List<Section<ConceptMarkup>> conecptDefinitions = MarkupUtils.getConecptDefinitionForLocalPage(section);
			if (conecptDefinitions.size() != 1) return;
			Section<ConceptMarkup> conceptMarkup = conecptDefinitions.get(0);
			Section<TermDefinition> mainTerm = Sections.successor(conceptMarkup,
					TermDefinition.class);
			String termName = mainTerm.get().getTermName(mainTerm);
			TerminologyObject foundObject = manager.search(termName);
			if (foundObject == null) {
				Section<NumericalValueMarkerType> numMarker = Sections.successor(
						section, NumericalValueMarkerType.class);
				if (numMarker != null) {
					createQuestionNum(section, manager, termName);
				}
				else {
					createQuestionOCWithValues(section, manager, termName);
				}
			}

		}

		@Override
		public void deleteFromRepository(Section<ValuesMarkup> section) {
			KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB,
					KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE);
			if (knowledgeBase == null) return;

			TerminologyManager manager = knowledgeBase.getManager();
			Object storedObject = KnowWEUtils.getStoredObject(section, VALUE_STORE_KEY);
			if (storedObject instanceof QuestionOC) {
				QuestionOC qoc = ((QuestionOC) storedObject);
				List<Choice> allAlternatives = qoc.getAllAlternatives();
				for (Choice choice : allAlternatives) {
					qoc.removeAlternative(choice);
				}
				manager.remove((Question) storedObject);
			}
		}
	}

	public static final String VALUE_STORE_KEY = "VALUE_STORE_KEY";

	public static String getShortAnswerName(String concept, String fullAnswer) {
		return fullAnswer.substring(concept.length()).trim();
	}

	public static String getLongAnswerName(String parentConceptName, String shortAnswer) {
		return parentConceptName + " "
				+ shortAnswer;
	}

	public static Question createQuestionNum(Section<ValuesMarkup> section, TerminologyManager manager, String termName) {

		TerminologyObject questionnaire = manager.search(KnowledgeBaseInstantiation.PATIENTENDATEN);
		if (questionnaire != null && questionnaire instanceof QASet) {
			QuestionNum question = new QuestionNum((QASet) questionnaire, termName);
			manager.putTerminologyObject(question);
			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
					KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE);
			KnowWEUtils.storeObject(
					D3webUtils.getCompiler(article),
					section, VALUE_STORE_KEY, question);
			return question;
		}
		return null;
	}

	/**
	 * 
	 * @created 25.07.2013
	 * @param section
	 * @param manager
	 * @param termName
	 */
	public static Question createQuestionOCWithValues(Section<ValuesMarkup> section, TerminologyManager manager, String termName) {
		List<Section<ValueDefinitionListElement>> values = Sections.successors(
				section, ValueDefinitionListElement.class);

		TerminologyObject questionnaire = manager.search(KnowledgeBaseInstantiation.PATIENTENDATEN);
		if (questionnaire != null && questionnaire instanceof QASet) {
			QuestionOC question = new QuestionOC((QASet) questionnaire, termName);

			for (Section<ValueDefinitionListElement> listElement : values) {
				Section<Term> answerTerm = Sections.successor(listElement, Term.class);
				String answerName = answerTerm.get().getTermName(answerTerm);
				question.addAlternative(new Choice(getShortAnswerName(termName, answerName)));
			}
			manager.putTerminologyObject(question);

			Article article = Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
					KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE);
			KnowWEUtils.storeObject(
					D3webUtils.getCompiler(article),
					section, VALUE_STORE_KEY, question);
			return question;
		}
		return null;
	}

	@Override
	public String getDerivationMessagePrefix() {
		// not required
		return null;
	}
}
