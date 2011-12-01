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

package de.d3web.we.covering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.we.basic.D3webModule;
import de.d3web.we.finding.ApproximateFinding;
import de.d3web.we.finding.ApproximateNumericalFinding;
import de.d3web.we.finding.ApproximateNumericalIntervallFinding;
import de.d3web.we.kdom.condition.CompositeCondition;
import de.d3web.we.kdom.condition.KDOMConditionFactory;
import de.d3web.we.object.SolutionDefinition;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.D3webUtils;
import de.d3web.we.utils.XCLRelationWeight;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelation;
import de.d3web.xcl.XCLRelationType;
import de.knowwe.core.compile.ConstraintModule;
import de.knowwe.core.compile.IncrementalMarker;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.SuccessorNotReusedConstraint;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.CommentLineType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.renderer.ReRenderSectionMarkerRenderer;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.AllTextFinderTrimSpaces;
import de.knowwe.kdom.sectionFinder.ConditionalSectionFinder;
import de.knowwe.kdom.sectionFinder.EmbracedContentFinder;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;
import de.knowwe.kdom.sectionFinder.UnquotedExpressionFinder;

/**
 * @author Jochen
 * 
 *         A covering-list markup parser
 * 
 *         In the first line the solution is defined @see ListSolutionType The
 *         rest of the content is split by ',' (komas) and the content inbetween
 *         is taken as CoveringRelations
 * 
 */
public class ApproximateCoveringList extends AbstractType {

	public ApproximateCoveringList() {
		this.sectionFinder = new AllTextSectionFinder();
		this.addChildType(new ApproximateListSolutionType());

		// cut the optional closing }
		AnonymousType closing = new AnonymousType("closing-bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("}"));
		this.addChildType(closing);

		// allow for comment lines
		this.addChildType(new CommentLineType());

		// split by search for komas
		AnonymousType koma = new AnonymousType("koma");
		koma.setSectionFinder(new UnquotedExpressionFinder(","));
		this.addChildType(koma);

		// the rest is CoveringRelations
		this.addChildType(new ApproximateCoveringRelation());

		this.setCustomRenderer(new ReRenderSectionMarkerRenderer<Type>(
				new CoveringListRenderer()));

		// anything left is comment
		AnonymousType residue = new AnonymousType("derRest");
		residue.setSectionFinder(new AllTextFinderTrimmed());
		residue.setCustomRenderer(StyleRenderer.COMMENT);
		this.addChildType(residue);
	}

	/**
	 * 
	 * @author volker_belli
	 * @created 08.12.2010
	 */
	private static final class CoveringListRenderer extends KnowWEDomRenderer<Type> {

		@Override
		public void render(KnowWEArticle article, Section<Type> sec, UserContext user, StringBuilder string) {
			string.append(KnowWEUtils.maskHTML("<span id='" + sec.getID() + "'>"));
			DelegateRenderer.getInstance().render(article, sec, user, string);
			string.append(KnowWEUtils.maskHTML("</span>"));
		}
	}

	class ApproximateCoveringRelation extends AbstractType implements IncrementalMarker {

		public ApproximateCoveringRelation() {

			this.setSectionFinder(new ConditionalSectionFinder(new AllTextFinderTrimSpaces()) {

				// hack to allow for comment after last relation
				// TODO: find better way
				@Override
				protected boolean condition(String text, Section<?> father) {
					// if starts as a comment and there is no next line, there
					// is CoveringRelation in it
					if (text.trim().startsWith("//") && !(text.contains("\n"))) {
						return false;
					}
					return true;
				}
			});

			this.addSubtreeHandler(Priority.LOW, new CreateXCLRelationHandler());
			this.setCustomRenderer(new CoveringRelationRenderer());

			// here also a comment might occur:
			AnonymousType relationComment = new AnonymousType("comment");
			relationComment.setSectionFinder(new RegexSectionFinder("[\\t ]*"
					+ "//[^\r\n]*+" + "\\r?\\n"));
			relationComment.setCustomRenderer(StyleRenderer.COMMENT);
			this.addChildType(relationComment);

			// take weights
			this.addChildType(new XCLWeight());

			// add condition
			CompositeCondition cond = new CompositeCondition();

			// these are the allowed/recognized terminal-conditions
			List<Type> termConds = new ArrayList<Type>();
			termConds.add(new ApproximateFinding());
			termConds.add(new ApproximateNumericalFinding());
			termConds.add(new ApproximateNumericalIntervallFinding());
			cond.setAllowedTerminalConditions(termConds);

			this.addChildType(cond);
		}

		/**
		 * @author Jochen
		 * 
		 *         this handler translates the parsed covering-relation-KDOM to
		 *         the d3web knowledge base
		 * 
		 */
		class CreateXCLRelationHandler extends D3webSubtreeHandler<ApproximateCoveringRelation> {

			private final String relationStoreKey = "XCLRELATION_STORE_KEY";

			public CreateXCLRelationHandler() {
				this.registerConstraintModule(new SuccessorNotReusedConstraint<ApproximateCoveringRelation>());
				this.registerConstraintModule(new CreateXCLRelationConstraint());
			}

			private Section<SolutionDefinition> getCorrespondingSolutionDef(KnowWEArticle article, Section<ApproximateCoveringRelation> s) {
				return Sections.findSuccessor(s.getFather().getFather(), SolutionDefinition.class);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * de.d3web.we.kdom.subtreeHandler.SubtreeHandler#create(de.d3web
			 * .we.kdom.KnowWEArticle, de.d3web.we.kdom.Section)
			 */
			@Override
			public Collection<Message> create(KnowWEArticle article, Section<ApproximateCoveringRelation> s) {

				List<Message> result = new ArrayList<Message>();

				Section<CompositeCondition> cond = Sections.findSuccessor(s,
						CompositeCondition.class);
				if (cond == null) {
					// no valid relation, do not revise
					return result;
				}

				if (s.hasErrorInSubtree(article)) {
					return Messages.asList(Messages.creationFailedWarning(
							D3webModule.getKwikiBundle_d3web()
									.getString("KnowWE.xcllist.relationfail")));
				}

				Section<SolutionDefinition> solutionDef = getCorrespondingSolutionDef(article, s);
				if (solutionDef != null) {
					Solution solution = solutionDef.get().getTermObject(
							article, solutionDef);

					if (solution != null) {
						XCLModel xclModel = solution.getKnowledgeStore().getKnowledge(
								XCLModel.KNOWLEDGE_KIND);

						if (xclModel != null) {

							if (cond != null) {

								Condition condition = KDOMConditionFactory.createCondition(article,
										cond);

								if (condition == null) {
									return Messages.asList(Messages.creationFailedWarning(
											D3webModule.getKwikiBundle_d3web()
													.getString("KnowWE.xcllist.conditionerror")));
								}

								// check the weight/relation type in square
								// brackets
								Section<XCLWeight> weight = Sections.findSuccessor(
										s, XCLWeight.class);
								XCLRelationType type = XCLRelationType.explains;
								Double w = 1.0;
								if (weight != null) {
									String weightString = weight.getOriginalText();
									type = getXCLRelationTypeForString(weightString);
									if (type == XCLRelationType.explains) {
										weightString = weightString.replaceAll("\\[", "");
										weightString = weightString.replaceAll("\\]", "");
										try {
											w = Double.valueOf(weightString.trim());
											if (w <= 0) {
												result.add(Messages.invalidNumberWarning(
														weightString));
											}
										}
										catch (NumberFormatException e) {
											// not a valid weight
											result.add(Messages.invalidNumberWarning(weightString));
										}
									}
								}

								// Insert the Relation into the currentModel
								XCLRelation relation = XCLModel.insertAndReturnXCLRelation(
										getKB(article),
										condition,
										solution, type, w, null);

								// set KDOMID here used in {@link KBRenderer}
								relation.setKdmomID(s.getID());

								KnowWEUtils.storeObject(article, s, relationStoreKey, relation);

								String wString = "";
								if (w > 0 && w != 1) {
									wString = Double.toString(w);
								}
								result.add(Messages.relationCreatedNotice("XCL: "
										+ type.toString() + " " + wString));
								return result;

							}
						}
					}
				}
				return Messages.asList(Messages.creationFailedWarning(
						D3webModule.getKwikiBundle_d3web()
								.getString("KnowWE.xcllist.relationfail")));
			}

			@Override
			public void destroy(KnowWEArticle article, Section<ApproximateCoveringRelation> s) {
				Section<SolutionDefinition> soltuionDef = getCorrespondingSolutionDef(article, s);

				if (soltuionDef == null) return;
				Solution solution = soltuionDef.get().getTermObject(article,
						soltuionDef);

				if (solution == null) return;
				XCLModel xclModel = solution.getKnowledgeStore().getKnowledge(
						XCLModel.KNOWLEDGE_KIND);

				if (xclModel == null) return;
				XCLRelation rel = (XCLRelation) s.getSectionStore().getObject(article,
						relationStoreKey);

				if (rel == null) return;
				xclModel.removeRelation(rel);

			}

			private class CreateXCLRelationConstraint extends ConstraintModule<ApproximateCoveringRelation> {

				public CreateXCLRelationConstraint() {
					super(Operator.COMPILE_IF_VIOLATED, Purpose.CREATE);
				}

				@Override
				public boolean violatedConstraints(KnowWEArticle article, Section<ApproximateCoveringRelation> s) {
					Section<SolutionDefinition> solutionDef = getCorrespondingSolutionDef(article,
							s);
					if (solutionDef == null) {
						return false;
					}
					return !solutionDef.isReusedBy(article.getTitle());
				}

			}

		}

	}

	class XCLWeight extends AbstractType {

		public static final char BOUNDS_OPEN = '[';
		public static final char BOUNDS_CLOSE = ']';

		public XCLWeight() {
			this.setSectionFinder(new EmbracedContentFinder(BOUNDS_OPEN, BOUNDS_CLOSE, 1));

		}
	}

	/**
	 * @author Johannes Dienst
	 * 
	 *         Highlights XCLRelations. Answer Right: Green Answer wrong: Red
	 *         Answer unknown: No Highlighting
	 * 
	 */
	class CoveringRelationRenderer extends KnowWEDomRenderer<ApproximateCoveringRelation> {

		public static final String KBID_KEY = "XCLRELATION_STORE_KEY";

		@Override
		public void render(KnowWEArticle article, Section<ApproximateCoveringRelation> sec, UserContext user, StringBuilder string) {

			// wrapper for highlighting
			string.append(KnowWEUtils.maskHTML("<span id='" + sec.getID()
					+ "' class = 'XCLRelationInList'>"));

			XCLRelation relation = (XCLRelation) KnowWEUtils.getStoredObject(article, sec,
					KBID_KEY);

			if (relation == null) {
				DelegateRenderer.getInstance().render(article, sec, user, string);
				return;
			}

			Session session = D3webUtils.getSession(article.getTitle(), user, article.getWeb());

			if (session != null) {
				// eval the Relation to find the right Rendering
				try {
					boolean fulfilled = relation.eval(session);
					// Highlight Relation
					this.renderRelation(article, sec, user, fulfilled, string, true);
					// close the wrapper
					string.append(KnowWEUtils.maskHTML("</span>"));
					return;
				}
				catch (Exception e) {
					// Call the XCLRelationMarkerHighlightingRenderer
					// without any additional info
					// string.append(this.renderRelationChildren(sec, user,
					// false, false);
				}
			}
			// Something went wrong: Delegate to children
			this.renderRelation(article, sec, user, false, string, false);
			// close the wrapper
			string.append(KnowWEUtils.maskHTML("</span>"));
		}

		/***
		 * Replaces the SpecialDelegateRenderer functionality to enable
		 * highlighting of Relations without their RelationWeights.
		 * 
		 * @param sec
		 * @param user
		 * @param web
		 * @param topic
		 * @param fulfilled
		 * @param string
		 * @return
		 */
		private void renderRelation(KnowWEArticle article, Section<ApproximateCoveringRelation> sec,
				UserContext user, boolean fulfilled, StringBuilder string, boolean highlight) {

			StringBuilder buffi = new StringBuilder();

			// need a span below XCLRelationInList
			if (!highlight) {
				List<Section<?>> children = sec.getChildren();
				for (Section<?> s : children) {
					buffi.append(this.renderRelationChild(article, s,
							fulfilled, user, ""));
				}
				string.append(KnowWEUtils.maskHTML(buffi.toString()));
				return;
			}

			// b true: Color green
			if (fulfilled) {
				// Iterate over children of the relation.
				List<Section<?>> children = sec.getChildren();
				for (Section<?> s : children) {
					buffi.append(this.renderRelationChild(article, s,
							fulfilled, user, StyleRenderer.CONDITION_FULLFILLED));
				}

			}
			else {
				// b false: Color red
				List<Section<?>> children = sec.getChildren();
				for (Section<?> s : children) {
					buffi.append(this.renderRelationChild(article, s,
							fulfilled, user, StyleRenderer.CONDITION_FALSE));
				}

			}
			string.append(KnowWEUtils.maskHTML(buffi.toString()));
		}

		/**
		 * Renders the children of a CoveringRelation.
		 * 
		 * @param article
		 * @param sec
		 * @param fulfilled
		 * @param user
		 * @param color
		 * @return
		 */
		@SuppressWarnings("unchecked")
		private String renderRelationChild(KnowWEArticle article,
				Section<?> sec, boolean fulfilled, UserContext user,
				String color) {
			StringBuilder buffi = new StringBuilder();
			Type type = sec.get();

			if (type instanceof XCLRelationWeight) { // renders contradiction in
				// red if fulfilled

				if (fulfilled && sec.getOriginalText().trim().equals("[--]")) {
					StyleRenderer.OPERATOR.render(article,
							sec, user, buffi);
				}
				else {
					type.getRenderer().render(article, sec, user, buffi);
				}

			}
			else if (type instanceof CompositeCondition) {
				StyleRenderer.getRenderer(null, color).render(
						article, sec, user, buffi);
			}
			else {
				type.getRenderer().render(article, sec, user, buffi);
			}

			return buffi.toString();
		}
	}

	public static XCLRelationType getXCLRelationTypeForString(String weightString) {
		if (weightString.contains("--")) {
			return XCLRelationType.contradicted;
		}
		else if (weightString.contains("!")) {
			return XCLRelationType.requires;
		}
		else if (weightString.contains("++")) {
			return XCLRelationType.sufficiently;
		}
		else {
			return XCLRelationType.explains;
		}
	}
}
