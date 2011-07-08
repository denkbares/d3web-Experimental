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
import java.util.regex.Pattern;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.ResourceType;

import de.d3web.we.drools.kdom.rendering.DroolsRuleNoticeRenderer;
import de.d3web.we.drools.kdom.rendering.DroolsRuleRenderer;
import de.d3web.we.drools.kdom.rendering.DroolsRuleWarningRenderer;
import de.d3web.we.drools.rules.Rule;
import de.d3web.we.drools.terminology.DroolsKnowledgeHandler;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.MessageRenderer;
import de.d3web.we.kdom.report.message.NewObjectCreated;
import de.d3web.we.kdom.report.message.NoSuchObjectError;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.logging.Logging;
import de.d3web.we.utils.KnowWEUtils;

/**
 * DroolsRule contains a complete Drools rule Rules start with `rule "name"' and
 * end with `end'.
 * 
 * @author Alex Legler
 */
public class DroolsRule extends AbstractType {

	private static final String rule_store_key = "rule_store_key";

	public DroolsRule() {
		addSubtreeHandler(new DroolsRuleSubtreeHandler());
		setSectionFinder(new RegexSectionFinder("rule.*?^end$", Pattern.DOTALL | Pattern.MULTILINE));
		setCustomRenderer(new DroolsRuleRenderer<DroolsRule>());
		childrenTypes.add(new DroolsRuleTitleLine());
		childrenTypes.add(new DroolsRuleBody());
	}

	@Override
	public MessageRenderer getNoticeRenderer() {
		return DroolsRuleNoticeRenderer.getInstance();
	}

	@Override
	public MessageRenderer getWarningRenderer() {
		return new DroolsRuleWarningRenderer();
	};

	static class DroolsRuleSubtreeHandler extends SubtreeHandler<DroolsRule> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<DroolsRule> s) {
			Collection<KDOMReportMessage> messages = new LinkedList<KDOMReportMessage>();
			Section<DroolsRule> section = s;

			Section<? extends DroolsRulesRootType> rtSection =
					Sections.findSuccessor(section.getArticle().getSection(),
							DroolsRulesRootType.class);

			if (rtSection == null) {
				Logging.getInstance().warning("DroolsRulesRootType Section not found!");
				messages.add(new NoSuchObjectError("DroolsRulesRootType Section not found!"));
				return messages;
			}

			KnowledgeBuilder b = DroolsKnowledgeHandler.getInstance().getKnowledgeBuilder(
					article.getTitle());

			Rule r = new Rule(section.getOriginalText());

			// // Test the rule
			// KnowledgeBuilder testBuilder =
			// KnowledgeBuilderFactory.newKnowledgeBuilder();
			// testBuilder.add(r.toResource(), ResourceType.DRL);
			//
			// if (testBuilder.hasErrors()) {
			// messages.add(new DroolsRuleWarning(testBuilder.getErrors()));
			// return messages;
			// }
			//
			// testBuilder = null;

			b.add(r.toResource(), ResourceType.DRL);

			KnowWEUtils.storeObject(article, section, rule_store_key, r);

			messages.add(new NewObjectCreated(b.toString()));
			return messages;
		}

		@Override
		public void destroy(KnowWEArticle article, Section<DroolsRule> s) {
			Rule rule = (Rule) s.getSectionStore().getObject(article, rule_store_key);
			if (rule != null) {
				if (rule.getName() == null) {
					article.setFullParse(getClass());
					return;
				}
				try {
					DroolsKnowledgeHandler.getInstance().getKnowledgeBase(
							article.getTitle()).removeRule(Rule.packageName, rule.getName());
				}
				catch (Exception e) {

				}
			}
		}
	}
}
