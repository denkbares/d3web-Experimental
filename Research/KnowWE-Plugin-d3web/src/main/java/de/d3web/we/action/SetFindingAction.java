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

package de.d3web.we.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEParameterMap;
import de.d3web.we.event.EventManager;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.d3web.event.FindingSetEvent;

public class SetFindingAction extends DeprecatedAbstractKnowWEAction {

	@SuppressWarnings({ "deprecation" })
	@Override
	public String perform(KnowWEParameterMap parameterMap) {
		String namespace = java.net.URLDecoder.decode(parameterMap.get(KnowWEAttributes.SEMANO_NAMESPACE));
		String objectid = parameterMap.get(KnowWEAttributes.SEMANO_OBJECT_ID);
		String valueid = parameterMap.get(KnowWEAttributes.SEMANO_VALUE_ID);
		String valuenum = parameterMap.get(KnowWEAttributes.SEMANO_VALUE_NUM);
		String valueids = parameterMap.get(KnowWEAttributes.SEMANO_VALUE_IDS);
		String topic = parameterMap.getTopic();
		String user = parameterMap.get(KnowWEAttributes.USER);
		String web = parameterMap.get(KnowWEAttributes.WEB);

		if (namespace == null || objectid == null) {
			return "null";
		}
		KnowledgeBaseUtils kbm = D3webModule.getKnowledgeRepresentationHandler(web).getKBM(
					topic);
		Session session = D3webUtils.getSession(topic, user, web);
		Blackboard blackboard = session.getBlackboard();

		// Necessary for FindingSetEvent
		Question question = kbm.getKnowledgeBase().getManager().searchQuestion(objectid);
		if (question != null) {

			List<Value> values = new ArrayList<Value>();
			if (valueids != null) {
				String[] ids = valueids.split("\\,");
				for (String string : ids) {
					values.add(kbm.findValue(question, string.trim()));
				}
			}
			Value singleValue = null;
			if (valueid != null) {
				singleValue = kbm.findValue(question, valueid);
			}
			else if (valuenum != null) {
				singleValue = new NumValue(Double.parseDouble(valuenum));
			}
			if (singleValue != null) values.add(singleValue);

			if (!values.isEmpty()) {
				for (Value value : values) {
					if (question instanceof QuestionMC && !value.equals(Unknown.getInstance())) {
						Fact mcFact = blackboard.getValueFact(question);
						if (mcFact != null && !mcFact.getValue().equals(Unknown.getInstance())) {
							MultipleChoiceValue oldMCValue = ((MultipleChoiceValue) mcFact.getValue());
							Collection<ChoiceID> thisMCValue = ((MultipleChoiceValue) value).getChoiceIDs();
							for (ChoiceID cv : oldMCValue.getChoiceIDs()) {
								if (!thisMCValue.contains(cv)) {
									thisMCValue.add(cv);
								}
							}
							value = new MultipleChoiceValue(thisMCValue);
						}
					}

					Fact fact = FactFactory.createUserEnteredFact(question, value);
					blackboard.addValueFact(fact);

					EventManager.getInstance().fireEvent(
								new FindingSetEvent(question, value, namespace, web, user));
				}
			}
		}
		return "value set";
	}

}
