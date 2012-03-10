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
package de.d3web.we.drools.kdom;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.we.drools.terminology.AbstractFact;
import de.d3web.we.drools.terminology.DroolsKnowledgeHandler;
import de.d3web.we.drools.terminology.MCInput;
import de.d3web.we.drools.terminology.NumInput;
import de.d3web.we.drools.terminology.OCInput;
import de.d3web.we.drools.terminology.SolutionInput;
import de.d3web.we.drools.terminology.TextValue;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;

/**
 * DroolsFact contains the type, name and possible values of an input.
 * 
 * @author Alex Legler, Sebastian Furth
 */
public class DroolsFact extends AbstractType {

	public DroolsFact() {
		setSectionFinder(new RegexSectionFinder("Input.*;"));
		childrenTypes.add(new DroolsFactInput());
		childrenTypes.add(new DroolsFactOptions());
		addSubtreeHandler(Priority.HIGH, new DroolsFactSubtreeHandler());
	}

	static class DroolsFactSubtreeHandler extends SubtreeHandler<DroolsFact> {

		@Override
		public Collection<Message> create(Article article, Section<DroolsFact> s) {

			Collection<Message> messages = new LinkedList<Message>();

			Section<? extends DroolsFactsType> factsSection =
					Sections.findSuccessor(article.getSection(), DroolsFactsType.class);

			if (factsSection != null) {
				Section<DroolsFact> section = s;
				Section<? extends DroolsFactInput> inputSection;
				Section<? extends DroolsFactInputType> inputTypeSection;
				Section<? extends DroolsFactOptions> optionsSection;
				Section<? extends DroolsFactName> nameSection;
				Section<? extends DroolsFactValues> valuesSection;

				String valueName;
				TextValue value = null;
				AbstractFact fact = null;

				Map<String, Object> factsStore = DroolsKnowledgeHandler.getInstance().getFactsStore(
						article.getTitle());

				// Check for the "Input<XX>" part; if there's a DroolsFactInput,
				// there'll also be
				// a DroolsFactInputType.
				if ((inputSection = Sections.findChildOfType(section, DroolsFactInput.class)) == null) {
					messages.add(Messages.syntaxError("Input type declaration missing."));
					return messages;
				}

				inputTypeSection = Sections.findChildOfType(inputSection, DroolsFactInputType.class);

				// now the options: ("name", {"values"})
				if ((optionsSection = Sections.findChildOfType(section, DroolsFactOptions.class)) == null) {
					messages.add(Messages.syntaxError("Input options missing."));
					return messages;
				}

				// The name
				if ((nameSection = Sections.findChildOfType(optionsSection,
						DroolsFactName.class)) == null) {
					messages.add(Messages.syntaxError("Input name missing."));
					return messages;
				}

				String type = inputTypeSection.getText().toLowerCase();

				// The possible values
				if ((valuesSection = Sections.findChildOfType(optionsSection,
						DroolsFactValues.class)) == null) {
					if (type.equals("mc") || type.equals("oc")) {
						messages.add(Messages.syntaxError("This Input type requires more options."));
						return messages;
					}
				}
				else {
					if (type.equals("num") || type.equals("solution")) {
						messages.add(Messages.syntaxError("This Input type does not expect more options."));
						return messages;
					}
				}
				// The String still contains the literal markers
				String factName = nameSection.getText().substring(1,
						nameSection.getText().length() - 1);

				// Process the possible values
				List<TextValue> possibleValues = new LinkedList<TextValue>();

				if (valuesSection != null) {
					for (Section<DroolsFactValue> v : Sections.findChildrenOfType(valuesSection,
							DroolsFactValue.class)) {
						valueName = v.getText().substring(1,
								v.getText().length() - 1);
						if ((value = (TextValue) factsStore.get(valueName)) == null) {
							value = new TextValue(valueName);
							factsStore.put(valueName, value);
						}
						possibleValues.add(value);
					}
				}

				if ((type.equals("mc") || type.equals("oc")) && possibleValues.size() < 1) {
					messages.add(Messages.syntaxError("You need to specify at least one possible answer."));
					return messages;
				}

				if (type.equals("mc")) {
					fact = new MCInput(factName);
					((MCInput) fact).addPossibleValues(possibleValues);
				}
				else if (type.equals("oc")) {
					fact = new OCInput(factName);
					((OCInput) fact).addPossibleValues(possibleValues);
				}
				else if (type.equals("num")) {
					fact = new NumInput(factName);
				}
				else if (type.equals("solution")) {
					fact = new SolutionInput(factName);
				}
				else {
					messages.add(Messages.syntaxError("Unknown Input type: "
							+ inputTypeSection.getText()));
					return messages;
				}

				factsStore.put(factName, fact);
				messages.add(Messages.objectCreatedNotice(fact.getClass().getSimpleName() + " "
						+ fact.getName()));
				return messages;
			}

			messages.add(Messages.syntaxError("KnowledgeBuilder is not present, unable to create Fact!"));
			return messages;
		}

		@Override
		public void destroy(Article article, Section<DroolsFact> s) {
			article.setFullParse(getClass());
		}

	}
}