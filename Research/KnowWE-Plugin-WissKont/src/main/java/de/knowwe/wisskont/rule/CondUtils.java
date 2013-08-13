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
package de.knowwe.wisskont.rule;

import de.d3web.core.inference.condition.CondNum;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.we.kdom.condition.NumericalFinding;
import de.d3web.we.kdom.condition.NumericalFinding.Comparator;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.basicType.Number;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.wisskont.ValuesMarkup;
import de.knowwe.wisskont.ValuesMarkup.NumericalValueMarkerType;
import de.knowwe.wisskont.dss.KnowledgeBaseInstantiation;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 08.08.2013
 */
public class CondUtils {

	/**
	 * 
	 * @created 08.08.2013
	 * @param condNumSection
	 * @return
	 */
	public static CondNum createCondNum(Section<NumericalFinding> condNumSection) {
		Section<Term> ref = Sections.findSuccessor(condNumSection, Term.class);

		boolean valid = IncrementalCompiler.getInstance().getTerminology().isValid(
				ref.get().getTermIdentifier(ref));

		if (!valid) return null;

		Section<? extends SimpleDefinition> conceptDefinition = MarkupUtils.getConceptDefinitionGlobal(ref);
		Section<NumericalValueMarkerType> numMarker = Sections.findSuccessor(
				conceptDefinition.getArticle().getRootSection(),
				NumericalValueMarkerType.class);

		Section<ValuesMarkup> markup = Sections.findAncestorOfType(numMarker, ValuesMarkup.class);

		if (markup == null) return null;

		Object o = KnowWEUtils.getStoredObject(
				Environment.getInstance().getArticle(Environment.DEFAULT_WEB,
						KnowledgeBaseInstantiation.WISSKONT_KNOWLEDGE),
				markup, ValuesMarkup.VALUE_STORE_KEY);

		QuestionNum qNum = null;
		if (o instanceof QuestionNum) {
			qNum = (QuestionNum) o;
		}
		else {
			return null;
		}

		String comparator = Sections.findSuccessor(condNumSection, Comparator.class).getText();

		Section<Number> numberSec = Sections.findSuccessor(condNumSection, Number.class);
		Double number = Number.getNumber(numberSec);

		CondNum condNum = null;
		if (number != null && qNum != null && comparator != null && comparator.length() > 0) {
			condNum = NumericalFinding.createCondNum(comparator, number, qNum);

		}
		return condNum;
	}
}
