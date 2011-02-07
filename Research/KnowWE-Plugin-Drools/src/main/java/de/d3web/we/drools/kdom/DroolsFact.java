/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.drools.kdom;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.d3web.we.drools.kdom.report.DroolsFactParseError;
import de.d3web.we.drools.terminology.AbstractFact;
import de.d3web.we.drools.terminology.DroolsKnowledgeHandler;
import de.d3web.we.drools.terminology.MCInput;
import de.d3web.we.drools.terminology.NumInput;
import de.d3web.we.drools.terminology.OCInput;
import de.d3web.we.drools.terminology.SolutionInput;
import de.d3web.we.drools.terminology.TextValue;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.NewObjectCreated;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;

/**
 * DroolsFact contains the type, name and possible values of an input.
 * @author Alex Legler, Sebastian Furth
 */
public class DroolsFact extends DefaultAbstractKnowWEObjectType {
		
	public DroolsFact() {
		setSectionFinder(new RegexSectionFinder("Input.*;"));
		childrenTypes.add(new DroolsFactInput());
		childrenTypes.add(new DroolsFactOptions());
		addSubtreeHandler(Priority.HIGH, new DroolsFactSubtreeHandler());
	}
	
	static class DroolsFactSubtreeHandler extends SubtreeHandler<DroolsFact> {
		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<DroolsFact> s) {
			
			Collection<KDOMReportMessage> messages = new LinkedList<KDOMReportMessage>();
			
			Section<? extends DroolsFactsType> factsSection = 
				article.getSection().findSuccessor(DroolsFactsType.class);
						
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

				
				// Check for the "Input<XX>" part; if there's a DroolsFactInput, there'll also be
				// a DroolsFactInputType.
				if ((inputSection = section.findChildOfType(DroolsFactInput.class)) == null) {
					messages.add(new DroolsFactParseError("Input type declaration missing."));
					return messages;
				}
				
				inputTypeSection = inputSection.findChildOfType(DroolsFactInputType.class);
				
				// now the options: ("name", {"values"})
				if ((optionsSection = section.findChildOfType(DroolsFactOptions.class)) == null) {
					messages.add(new DroolsFactParseError("Input options missing."));
					return messages;
				}
				
				// The name
				if ((nameSection = optionsSection.findChildOfType(DroolsFactName.class)) == null) {
					messages.add(new DroolsFactParseError("Input name missing."));
					return messages;
				}

				String type = inputTypeSection.getOriginalText().toLowerCase();
				
				// The possible values
				if ((valuesSection = optionsSection.findChildOfType(DroolsFactValues.class)) == null) {
					if (type.equals("mc") || type.equals("oc")) {
						messages.add(new DroolsFactParseError("This Input type requires more options."));
						return messages;
					}
				} else {
					if (type.equals("num") || type.equals("solution")) {
						messages.add(new DroolsFactParseError("This Input type does not expect more options."));
						return messages;
					}
				}
				// The String still contains the literal markers
				String factName = nameSection.getOriginalText().substring(1, nameSection.getOriginalText().length() - 1);

				// Process the possible values
				List<TextValue> possibleValues = new LinkedList<TextValue>();
				
				if (valuesSection != null) {
					for (Section<DroolsFactValue> v : valuesSection.findChildrenOfType(DroolsFactValue.class)) {
						valueName = v.getOriginalText().substring(1, v.getOriginalText().length() - 1);
						if ((value = (TextValue) factsStore.get(valueName)) == null) {
							value = new TextValue(valueName);
							factsStore.put(valueName, value);
						}
						possibleValues.add(value);
					}
				}
				
				if ((type.equals("mc") || type.equals("oc")) && possibleValues.size() < 1) {
					messages.add(new DroolsFactParseError("You need to specify at least one possible answer."));
					return messages;
				}
					
				if (type.equals("mc")) {
					fact = new MCInput(factName);
					((MCInput) fact).addPossibleValues(possibleValues);
				} else if (type.equals("oc")) {
					fact = new OCInput(factName);
					((OCInput) fact).addPossibleValues(possibleValues);
				} else if (type.equals("num")) {
					fact = new NumInput(factName);
				} else if (type.equals("solution")) {
					fact = new SolutionInput(factName);
				} else {
					messages.add(new DroolsFactParseError("Unknown Input type: " + inputTypeSection.getOriginalText()));
					return messages;
				}
				
				factsStore.put(factName, fact);
				messages.add(new NewObjectCreated(fact.getClass().getSimpleName() + " " + fact.getName()));
				return messages;
			}

			messages.add(new DroolsFactParseError("KnowledgeBuilder is not present, unable to create Fact!"));
			return messages;
		}

		@Override
		public void destroy(KnowWEArticle article, Section<DroolsFact> s) {
			article.setFullParse(getClass());
		}


		
	}
}