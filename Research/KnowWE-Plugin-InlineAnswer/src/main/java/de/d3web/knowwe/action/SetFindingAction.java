/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.knowwe.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceID;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.basic.SessionBroker;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.event.EventManager;
import de.knowwe.d3web.action.SetSingleFindingAction;
import de.knowwe.d3web.event.FindingSetEvent;


/**
 * 
 * TODO: Merge with {@link SetSingleFindingAction} ?
 * 
 * @author Johannes Dienst
 * @created 09.06.2011
 */
public class SetFindingAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String objectid = context.getParameter(KnowWEAttributes.SEMANO_OBJECT_ID);
		String valuenum = context.getParameter(KnowWEAttributes.SEMANO_VALUE_NUM);
		String valuedate = context.getParameter(KnowWEAttributes.SEMANO_VALUE_DATE);
		String valueText = context.getParameter(KnowWEAttributes.SEMANO_VALUE_TEXT);
		String valueids = context.getParameter(KnowWEAttributes.SEMANO_VALUE_IDS);
		String topic = context.getTopic();
		String user = context.getUserName();
		String web = context.getWeb();
		String namespace = null;
		//		String term = null;
		String valueid = null;
		try {
			topic = java.net.URLDecoder.decode(topic, "UTF-8");
			namespace = java.net.URLDecoder.decode(
					context.getParameter(KnowWEAttributes.SEMANO_NAMESPACE), "UTF-8");
			String tempValueID = context.getParameter(KnowWEAttributes.SEMANO_VALUE_ID);
			if (tempValueID != null) valueid = URLDecoder.decode(tempValueID, "UTF-8");
			//			term = URLDecoder.decode(context.getParameter(KnowWEAttributes.SEMANO_TERM_NAME),
			//			"UTF-8");
			if (valueids != null) valueids = URLDecoder.decode(valueids, "UTF-8");
			if (objectid != null) objectid = URLDecoder.decode(objectid, "UTF-8");
		}
		catch (UnsupportedEncodingException e1) {
			// should not occur
		}
		//		if (term != null && !term.equalsIgnoreCase("undefined")) {
		//			objectid = term;
		//		}

		//		if (namespace == null || objectid == null) {
		//			return "null";
		//		}

		KnowledgeBase kb = D3webModule.getKnowledgeRepresentationHandler(web).getKB(
				topic);
		SessionBroker broker = D3webModule.getBroker(user, web);
		Session session = broker.getServiceSession(kb.getId());
		Blackboard blackboard = session.getBlackboard();

		// Necessary for FindingSetEvent
		Question question = kb.getManager().searchQuestion(objectid);
		if (question != null) {

			Value value = null;
			if (valueid != null) {
				value = KnowledgeBaseUtils.findValue(question, valueid);
			}
			else if (valuenum != null) {
				try {
					value = new NumValue(Double.parseDouble(valuenum));
				}
				catch (NumberFormatException e) {
					// nothing to do, value will be null, field will be empty
				}

				// TODO set valuedate in Attributes
			}
			else if (valuedate != null) {
				final DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				try {
					Date date = format.parse(valuedate);
					value = new DateValue(date);
				}
				catch (ParseException e) {
					e.printStackTrace();
				}
			}
			else if (valueText != null) {
				value = new TextValue(valueText);
			}

			// Handle MultipleChoice
			if (valueids != null) {
				String[] ids = valueids.split(",");
				if (valueids.equals("")) ids = new String[0];
				ArrayList<ChoiceID> choices = new ArrayList<ChoiceID>();
				for (String id : ids)
					choices.add(new ChoiceID(id));

				List<String> strings = new ArrayList<String>();
				for (String id : ids)
					strings.add(id);
				MultipleChoiceValue newMCVal =
					KnowledgeBaseUtils.findMultipleChoiceValue((QuestionMC)question, strings);

				synchronized(session) {
					Fact fact = FactFactory.createUserEnteredFact(question, newMCVal);
					blackboard.addValueFact(fact);
				}
				EventManager.getInstance().fireEvent(
						new FindingSetEvent(question, newMCVal, namespace, web, user));
				return;
			}

			// Handle all other
			if (value != null) {
				// synchronize to session as suggested for multi-threaded
				// kernel access applications
				synchronized (session) {
					Fact fact = FactFactory.createUserEnteredFact(question, value);
					blackboard.addValueFact(fact);
				}
				EventManager.getInstance().fireEvent(
						new FindingSetEvent(question, value, namespace, web, user));
			}
		}
	}

}
