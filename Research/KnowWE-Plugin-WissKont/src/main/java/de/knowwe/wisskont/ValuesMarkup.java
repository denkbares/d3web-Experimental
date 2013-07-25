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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyManager;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.strings.Strings;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.annotation.type.list.ListObjectIdentifier;
import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.renderer.CompositeRenderer;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Types;
import de.knowwe.tools.ToolMenuDecoratingRenderer;
import de.knowwe.wisskont.dss.KnowledgeBaseInstantiation;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 02.07.2013
 */
public class ValuesMarkup extends RelationMarkup implements KnowledgeUnit {

	private static final String key = "Werte";

	/**
	 * 
	 */
	public ValuesMarkup() {
		super(key);
		Type contentType = Types.findSuccessorType(this, RelationMarkupContentType.class);
		boolean replaced = Types.replaceType(contentType, ListObjectIdentifier.class,
				new ValueDefinitionListElement(new OIDeleteItemRenderer()));
		if (!replaced) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Type NOT REPLACED ! ");
		}
	}

	@Override
	public String getName() {
		return "Werte-Bereich des Patienten";
	}

	@Override
	public boolean isInverseDir() {
		return true;
	}

	@Override
	public URI getRelationURI() {
		return createURI(SubconceptMarkup.SUBCONCEPT_PROPERTY);
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
			Section<IncrementalTermDefinition> conceptDefinition = MarkupUtils.getConceptDefinition(s);
			if (conceptDefinition == null) {
				return null; // do nothing
			}
			String valueText = s.getText().trim();
			if (valueText.length() == 0) {
				return null;
			}
			return Strings.unquote(conceptDefinition.get().getTermName(conceptDefinition) + " "
					+ s.getText().trim());
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
			TerminologyManager manager = knowledgeBase.getManager();

			List<Section<ConceptMarkup>> conecptDefinitions = MarkupUtils.getConecptDefinitions(section);
			if (conecptDefinitions.size() != 1) return;
			Section<ConceptMarkup> conceptMarkup = conecptDefinitions.get(0);
			Section<TermDefinition> mainTerm = Sections.findSuccessor(conceptMarkup,
					TermDefinition.class);
			String termName = mainTerm.get().getTermName(mainTerm);
			TerminologyObject foundObject = manager.search(termName);
			if (foundObject == null) {

				createQuestionOCWithValues(section, manager, termName);
			}

		}

		@Override
		public void deleteFromRepository(Section<ValuesMarkup> section) {
			KnowledgeBase knowledgeBase = D3webUtils.getKnowledgeBase(Environment.DEFAULT_WEB,
					KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE);
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

	/**
	 * 
	 * @created 25.07.2013
	 * @param section
	 * @param manager
	 * @param termName
	 */
	public static Question createQuestionOCWithValues(Section<ValuesMarkup> section, TerminologyManager manager, String termName) {
		List<Section<ValueDefinitionListElement>> values = Sections.findSuccessorsOfType(
				section, ValueDefinitionListElement.class);

		TerminologyObject questionnaire = manager.search(KnowledgeBaseInstantiation.PATIENTENDATEN);
		if (questionnaire != null && questionnaire instanceof QASet) {
			QuestionOC question = new QuestionOC((QASet) questionnaire, termName);

			for (Section<ValueDefinitionListElement> listElement : values) {
				Section<Term> answerTerm = Sections.findSuccessor(listElement, Term.class);
				String answerName = answerTerm.get().getTermName(answerTerm);
				question.addAlternative(new Choice(answerName.substring(termName.length()).trim()));
			}
			manager.putTerminologyObject(question);

			KnowWEUtils.storeObject(
					Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
							KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE),
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
