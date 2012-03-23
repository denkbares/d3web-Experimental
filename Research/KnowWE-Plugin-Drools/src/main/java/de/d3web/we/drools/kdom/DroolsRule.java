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
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.subtreeHandler.SubtreeHandler;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.MessageRenderer;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.logging.Logging;

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
		setRenderer(new DroolsRuleRenderer());
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
		public Collection<Message> create(Article article, Section<DroolsRule> s) {
			Collection<Message> messages = new LinkedList<Message>();
			Section<DroolsRule> section = s;

			Section<? extends DroolsRulesRootType> rtSection =
					Sections.findSuccessor(section.getArticle().getRootSection(),
							DroolsRulesRootType.class);

			if (rtSection == null) {
				Logging.getInstance().warning("DroolsRulesRootType Section not found!");
				messages.add(Messages.noSuchObjectError("DroolsRulesRootType Section not found!"));
				return messages;
			}

			KnowledgeBuilder b = DroolsKnowledgeHandler.getInstance().getKnowledgeBuilder(
					article.getTitle());

			Rule r = new Rule(section.getText());

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

			messages.add(Messages.objectCreatedNotice(b.toString()));
			return messages;
		}

		@Override
		public void destroy(Article article, Section<DroolsRule> s) {
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
