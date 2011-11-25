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

import java.util.Collection;

import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.we.object.SolutionDefinition;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.xcl.XCLModel;
import de.knowwe.core.compile.ConstraintModule;
import de.knowwe.core.compile.IncrementalMarker;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.report.KDOMReportMessage;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.sectionFinder.NonEmptyLineSectionFinder;
import de.knowwe.kdom.sectionFinder.StringSectionFinderUnquoted;

/**
 * @author Jochen
 * 
 *         A type for the head of a covering-list defining the solution that is
 *         described by that list. The solution is created from the term found.
 *         Further, a covering-model is created.
 * 
 * 
 */
public class ApproximateListSolutionType extends AbstractType implements IncrementalMarker {

	public ApproximateListSolutionType() {
		ConstraintSectionFinder solutionFinder = new ConstraintSectionFinder(
				new NonEmptyLineSectionFinder());
		solutionFinder.addConstraint(ExactlyOneFindingConstraint.getInstance());
		this.setSectionFinder(solutionFinder);

		this.addSubtreeHandler(Priority.HIGH, new XCLModelCreator());

		// cut the optional '{'
		AnonymousType closing = new AnonymousType("bracket");
		closing.setSectionFinder(new StringSectionFinderUnquoted("{"));
		this.addChildType(closing);

		ApproximateXCListSolutionDefinition solDef = new ApproximateXCListSolutionDefinition();
		ConstraintSectionFinder allFinder = new ConstraintSectionFinder(new AllTextFinderTrimmed());
		allFinder.addConstraint(ExactlyOneFindingConstraint.getInstance());
		solDef.setSectionFinder(allFinder);
		this.addChildType(solDef);
	}

	/**
	 * @author Jochen
	 * 
	 *         This handler creates the solution in the KB and also a
	 *         covering-model
	 * 
	 */
	class XCLModelCreator extends D3webSubtreeHandler<ApproximateListSolutionType> {

		public XCLModelCreator() {
			this.registerConstraintModule(new XCLModelCreatorConstraint());
		}

		private class XCLModelCreatorConstraint extends ConstraintModule<ApproximateListSolutionType> {

			@Override
			public boolean violatedConstraints(KnowWEArticle article, Section<ApproximateListSolutionType> s) {
				return !Sections.findSuccessor(s, SolutionDefinition.class).isReusedBy(
						article.getTitle());
			}

		}

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<ApproximateListSolutionType> s) {

			Section<SolutionDefinition> solutionDef = Sections.findSuccessor(s,
					SolutionDefinition.class);

			Solution solution = solutionDef.get().getTermObject(article, solutionDef);

			Section<DefaultMarkupType> defaultMarkupType = Sections.findAncestorOfType(s,
					DefaultMarkupType.class);

			if (solution != null) {
				XCLModel xclModel = solution.getKnowledgeStore().getKnowledge(XCLModel.KNOWLEDGE_KIND);
				if (xclModel == null) {
					XCLModel m = new XCLModel(solution);

					setThresholdsAndMinSupport(defaultMarkupType, m);

					solution.getKnowledgeStore().addKnowledge(XCLModel.KNOWLEDGE_KIND,
							m);

					String description = DefaultMarkupType.getAnnotation(defaultMarkupType,
							ApproximateCoveringListMarkup.DESCRIPTION);
					if (description != null) {
						m.getSolution().getInfoStore().addValue(MMInfo.DESCRIPTION, description);
					}
				}
			}
			return null;
		}

		@Override
		public void destroy(KnowWEArticle article, Section<ApproximateListSolutionType> s) {
			// nothing to do, the solution, along with its attached model, will
			// be destroyed in the SolutionDef
			return;
		}

		/**
		 * reads out the respective annotations for suggestedThreshold,
		 * establishedThreshold and minSupport and sets them in the XCLModel if
		 * existing
		 * 
		 * @param defaultMarkupType
		 * @param m
		 */
		private void setThresholdsAndMinSupport(Section<DefaultMarkupType> defaultMarkupType, XCLModel m) {

			// handle ESTABLISHED_THRESHOLD
			Section<? extends AnnotationContentType> estaAnnoSection = DefaultMarkupType.getAnnotationContentSection(
					defaultMarkupType,
					ApproximateCoveringListMarkup.ESTABLISHED_THRESHOLD);

			if (estaAnnoSection != null) {
				String estaText = estaAnnoSection.getOriginalText();
				// set estaThreashold if defined
				if (estaText != null) {
					try {
						Double estaThreshold = Double.parseDouble(estaText);
						m.setEstablishedThreshold(estaThreshold);
					}
					catch (NumberFormatException e) {

					}
				}

			}

			// handle SUGGESTED_THRESHOLD
			Section<? extends AnnotationContentType> suggAnnoSection = DefaultMarkupType.getAnnotationContentSection(
					defaultMarkupType,
					ApproximateCoveringListMarkup.SUGGESTED_THRESHOLD);

			if (suggAnnoSection != null) {
				String suggText = suggAnnoSection.getOriginalText();
				// set suggThreashold if defined
				if (suggText != null) {
					try {
						Double suggThreashold = Double.parseDouble(suggText);
						m.setSuggestedThreshold(suggThreashold);
					}
					catch (NumberFormatException e) {

					}
				}
			}

			// handle MIN_SUPPORT
			Section<? extends AnnotationContentType> minAnnoSection = DefaultMarkupType.getAnnotationContentSection(
					defaultMarkupType,
					ApproximateCoveringListMarkup.MIN_SUPPORT);
			if (minAnnoSection != null) {
				String minText = minAnnoSection.getOriginalText();
				// set minSupport if defined
				if (minText != null) {
					try {
						Double minSupport = Double.parseDouble(minText);
						m.setMinSupport(minSupport);
					}
					catch (NumberFormatException e) {

					}
				}

			}

		}

	}

}
