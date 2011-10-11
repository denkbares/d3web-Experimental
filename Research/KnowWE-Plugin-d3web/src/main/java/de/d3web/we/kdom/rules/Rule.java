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

package de.d3web.we.kdom.rules;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.d3web.KnOfficeParser.SingleKBMIDObjectManager;
import de.d3web.KnOfficeParser.rule.D3ruleBuilder;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.report.Message;
import de.d3web.report.Report;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DefaultTextRenderer;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.xml.AbstractXMLType;

public class Rule extends AbstractType {

	public static final String KBID_KEY = "RULE_STORE_KEY";
	public static final String KDOMID_KEY = "kdomid";

	@Override
	protected void init() {
		this.addSubtreeHandler(new RuleSubTreeHandler());
		sectionFinder = new RegexSectionFinder(
				"(IF|WENN).*?(?=(\\s*?(?m)^\\s*?$\\s*|\\s*IF|\\s*WENN"
						+ "|\\s*\\z))",
				Pattern.DOTALL);
		setCustomRenderer(new RuleRenderer());
		this.childrenTypes.add(new RuleActionLine());
		this.childrenTypes.add(new RuleCondLine());

	}

	private class RuleRenderer extends KnowWEDomRenderer<Rule> {

		@Override
		public void render(KnowWEArticle article, Section<Rule> sec,
				UserContext user, StringBuilder string) {

			List<Message> errors = getErrorMessages(article, sec);

			string.append(KnowWEUtils.maskHTML("<span id='" + sec.getID()
					+ "' class = 'XCLRelationInList'>"));

			boolean empty = true;
			if (errors != null) {
				for (Message error : errors) {
					if (error != null) {
						// hack showing only errors, this rendering needs a
						// complete redesign
						empty = false;
						string.append(KnowWEUtils
								.maskHTML("<span class='error' style='color:red'>"));
						string.append(KnowWEUtils
								.maskHTML(error.getMessageType() + ": "
										+ error.getMessageText()
										+ (error.getMessageType().equals(
												Message.NOTE) ? " "
												+ error.getCount() : " Line: "
												+ error.getLineNo())));

						string.append(KnowWEUtils.maskHTML("</span>"));

					}
				}
			}
			if (!empty) {
				string.append(KnowWEUtils.maskHTML("\n"));
			}

			StringBuilder b = new StringBuilder();
			if (!empty) {
				b.append(KnowWEUtils.maskHTML("<span class='error_highlight'>"));
				DefaultTextRenderer.getInstance().render(article, sec, user, b);
				b.append(KnowWEUtils.maskHTML("</span>"));
			}
			else {
				DelegateRenderer.getInstance().render(article, sec, user, b);
			}
			string.append(b.toString()
					+ KnowWEUtils.maskHTML("</span>"));
		}

	}

	class RuleSubTreeHandler extends D3webSubtreeHandler<Rule> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Rule> s) {

			boolean lazy = false;
			Section xml = Sections.findAncestorOfType(s, AbstractXMLType.class);
			Map<String, String> attributes = null;
			if (xml != null) {
				attributes = AbstractXMLType
						.getAttributeMapFor(xml);
			}
			if (attributes != null && attributes.containsKey("lazy")) {
				String l = attributes.get("lazy");
				if (l != null) {
					if (l.equals("1") | l.equals("on") | l.equals("true")
							| l.equals("an")) {
						lazy = true;
					}
				}
			}

			KnowledgeBase kb = getKB(article);

			if (kb != null) {

				D3ruleBuilder builder = new D3ruleBuilder(s.getID(), lazy,
						new SingleKBMIDObjectManager(kb));

				if (s != null) {
					String text = s.getOriginalText();
					Reader r = new StringReader(text);

					List<Message> bm = builder.addKnowledge(r,
							new SingleKBMIDObjectManager(kb), null);

					if (builder.getRules().size() == 1) {
						KnowWEUtils.storeObject(article, s, KBID_KEY, builder.getRules().get(0));
					}

					KnowWEUtils.storeMessages(article, s, this.getClass(), Message.class,
							bm);
					List<Message> errors = new ArrayList<Message>();
					for (Message message : bm) {
						if (message.getMessageType().equals(Message.ERROR)
								|| message.getMessageType().equals(Message.WARNING)) {
							errors.add(message);
						}
					}
					if (errors.isEmpty()) {
						storeErrorMessages(article, s, null);
					}
					else {
						storeErrorMessages(article, s, errors);
					}

					Report ruleRep = new Report();
					for (Message messageKnOffice : bm) {
						ruleRep.add(messageKnOffice);
					}
				}
			}
			else {
				// store empty message to prevent surviving of old errors due to
				// update-inconstistencies
				storeErrorMessages(article, s, null);
			}
			return null;
		}
	}

	public static final String RULE_ERROR_MESSAGE_STORE_KEY = "Rule-error-message";

	/**
	 * Stores a message under to rule-error-store-key
	 * 
	 * @param s
	 * @param message
	 */
	public static void storeErrorMessages(KnowWEArticle article, Section s, List<Message> message) {
		KnowWEUtils.storeObject(article, s, RULE_ERROR_MESSAGE_STORE_KEY, message);
	}

	/**
	 * Stores a message under to rule-error-store-key
	 * 
	 * @param s
	 * @param message
	 */
	public static List<Message> getErrorMessages(KnowWEArticle article, Section s) {
		return (List<Message>) KnowWEUtils.getStoredObject(article, s,
				RULE_ERROR_MESSAGE_STORE_KEY);
	}

}
