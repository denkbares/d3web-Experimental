package de.d3web.we.kdom.questionTree.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.core.inference.Rule;
import de.d3web.core.inference.condition.CondNot;
import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.manage.RuleFactory;
import de.d3web.we.kdom.condition.Finding;
import de.d3web.we.kdom.rules.RuleContentType;
import de.d3web.we.object.QASetDefinition;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.NothingRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.sectionFinder.UnquotedExpressionFinder;

/**
 * This type allows to define within the definition of a question, when this
 * question will be asked This is an alternative method to dashTree-indents
 * 
 * @author Jochen, Albrecht
 * @created 23.12.2010
 */
public class InlineIndicationCondition extends AbstractType {

	private static final String START_KEY = "&\\s*?(Nur falls|Only if):?";
	private static final String END_KEY = "&";

	public InlineIndicationCondition() {
		this.addSubtreeHandler(new CreateIndicationRulesHandler());
		this.setSectionFinder(new InlineIndiFinder());

		// TODO find better way to crop open and closing signs
		AnonymousType open = new AnonymousType(START_KEY);
		open.setSectionFinder(new RegexSectionFinder(
				START_KEY, Pattern.CASE_INSENSITIVE));
		open.setRenderer(new Renderer() {

			@Override
			public void render(Section<?> sec, UserContext user, StringBuilder string) {
				string.append(KnowWEUtils.maskHTML("<b>"));
				string.append(sec.getText().substring(1).trim());
				string.append(KnowWEUtils.maskHTML("</b>"));
			}
		});
		this.addChildType(open);

		AnonymousType close = new AnonymousType(END_KEY);
		close.setSectionFinder(new UnquotedExpressionFinder(
				END_KEY));
		close.setRenderer(NothingRenderer.getInstance());
		this.addChildType(close);

		this.addChildType(new Finding());

	}

	private class InlineIndiFinder implements SectionFinder {

		@Override
		public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {

			Pattern pattern = Pattern.compile(START_KEY, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				int start = matcher.start();
				int end = SplitUtility.lastIndexOfUnquoted(text, END_KEY);
				return SectionFinderResult.createSingleItemList(new SectionFinderResult(start,
						end + 1));
			}
			return null;
		}

	}

	static class CreateIndicationRulesHandler extends D3webSubtreeHandler<InlineIndicationCondition> {

		@Override
		public Collection<Message> create(Article article, Section<InlineIndicationCondition> s) {

			if (s.hasErrorInSubtree(article)) {
				return Messages.asList(Messages.creationFailedWarning("Rule"));
			}

			Section<Finding> finding = Sections.findSuccessor(s, Finding.class);

			@SuppressWarnings("rawtypes")
			Section<QASetDefinition> qDef = Sections.findSuccessor(s.getFather(),
					QASetDefinition.class);
			Collection<Message> msgs = new HashSet<Message>();
			if (finding != null && qDef != null) {

				@SuppressWarnings("unchecked")
				QASet qaset = (QASet) qDef.get().getTermObject(article, qDef);

				Condition condition = finding.get().getCondition(article, finding);
				if (condition == null) {
					msgs.add(Messages.objectCreationError(Condition.class.getSimpleName()));
					return msgs;
				}

				// create an contraIndication-rule
				List<QASet> qasets = new ArrayList<QASet>();
				qasets.add(qaset);

				Rule r = RuleFactory.createContraIndicationRule(qasets, new CondNot(condition));
				Rule r2 = null;
				if (qaset instanceof QContainer) {
					r2 = RuleFactory.createIndicationRule(qasets, condition);
				}
				if (r == null || (qaset instanceof QContainer && r2 == null)) {
					msgs.add(Messages.objectCreationError(Rule.class.getSimpleName()));
				}
				if (r != null && !(qaset instanceof QContainer && r2 == null)) {
					KnowWEUtils.storeObject(article, s, RuleContentType.ruleStoreKey, r);
					return Messages.asList(Messages.objectCreatedNotice("Rule"));
				}

			}

			return msgs;
		}

	}
}
