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
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.report.Message;
import de.d3web.report.Report;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.rendering.DefaultTextRenderer;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.xml.AbstractXMLObjectType;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class Rule extends DefaultAbstractKnowWEObjectType {

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
				KnowWEUserContext user, StringBuilder string) {

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
			Section xml = s.findAncestorOfType(AbstractXMLObjectType.class);
			Map<String, String> attributes = null;
			if (xml != null) {
				attributes = AbstractXMLObjectType
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

			KnowledgeBaseManagement kbm = getKBM(article);

			if (kbm != null) {

				D3ruleBuilder builder = new D3ruleBuilder(s.getID(), lazy,
						new SingleKBMIDObjectManager(kbm));

				if (s != null) {
					String text = s.getOriginalText();
					Reader r = new StringReader(text);

					List<Message> bm = builder.addKnowledge(r,
							new SingleKBMIDObjectManager(kbm), null);

					if (builder.getRuleIDs().size() == 1) {
						KnowWEUtils.storeObject(article.getWeb(), article
								.getTitle(), s.getID(), KBID_KEY, builder
								.getRuleIDs().get(0));
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
		KnowWEUtils.storeObject(s.getWeb(), article
				.getTitle(), s.getID(), RULE_ERROR_MESSAGE_STORE_KEY, message);
	}

	/**
	 * Stores a message under to rule-error-store-key
	 * 
	 * @param s
	 * @param message
	 */
	public static List<Message> getErrorMessages(KnowWEArticle article, Section s) {
		return (List<Message>) KnowWEUtils.getStoredObject(
				KnowWEEnvironment.DEFAULT_WEB, article.getTitle(), s.getID(),
				RULE_ERROR_MESSAGE_STORE_KEY);
	}

}
