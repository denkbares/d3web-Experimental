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

package de.d3web.we.kdom.xcl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import de.d3web.core.inference.condition.Condition;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.Annotation.Finding;
import de.d3web.we.kdom.condition.antlr.ComplexFinding;
import de.d3web.we.kdom.condition.antlr.FindingToConditionBuilder;
import de.d3web.we.kdom.condition.antlr.NegatedFinding;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.xml.XMLContent;
import de.d3web.we.reviseHandler.D3webSubtreeHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.utils.XCLRelationWeight;
import de.d3web.xcl.XCLModel;
import de.d3web.xcl.XCLRelationType;
import de.knowwe.core.CommentLineType;

@SuppressWarnings("unchecked")
public class CoveringListContent extends XMLContent {

	public static final String KBID_KEY = "XCLRELATION_STORE_KEY";
	private final Pattern p = Pattern.compile("\"");

	@Override
	protected void init() {
		this.childrenTypes.add(new XCList());
		this.childrenTypes.add(new CommentLineType());
		this.addSubtreeHandler(new CoveringListContentSubTreeHandler());
	}

	public class CoveringListContentSubTreeHandler extends D3webSubtreeHandler {

		// KnowledgeBaseUtils kbm = null;
		// String currentWeb = "";
		// private Diagnosis currentdiag;

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section s) {

			// Set currentWeb
			String currentWeb = s.getWeb();

			KnowledgeBase kb = getKB(article);

			if (kb != null) {
				// Analyse s (Has XCList-Children)
				ArrayList<Section> elements = new ArrayList<Section>(s.getChildren());
				for (Section sec : elements) {
					this.analyseXCList(article, sec, kb, currentWeb);
				}
			}

			return null;
		}

		/**
		 * Analyses a given XCList and writes it to the KnowledgeBase.
		 * 
		 * @param kbm
		 * @param currentweb
		 * @param XCLList
		 */
		private void analyseXCList(KnowWEArticle article, Section xclList, KnowledgeBase kb, String currentweb) {

			// Check if xclList is XCList
			if ((xclList.get() instanceof XCList) && (kb != null)) {

				// Get all children of xclList containing
				// XCLHead/XCLBody/XCLTail and some other text
				ArrayList<Section> elements = new ArrayList<Section>(
						this.getXCLHeadBodyTail(new ArrayList<Section>(xclList.getChildren())));

				if (elements.size() <= 1) {
					// invalid XCL-KDOM-tree
					return;
				}

				// Insert Solution into KnowledgeBase when Solution doesnt exist
				Section head = elements.get(0);
				Solution currentdiag = kb.getManager().searchSolution(
						head.getOriginalText().replaceAll(
								p.toString(), "").trim());
				if (currentdiag == null) {
					currentdiag = new Solution(kb.getRootSolution(),
							head.getOriginalText().replaceAll(
									p.toString(), "").trim());
				}

				// Insert XCLRelations belonging to current Diagnosis
				ArrayList<Section> currentRels = new ArrayList<Section>(
						this.getXCLRelations(elements.get(1)));

				// insert every Relation into currentModel
				this.insertRelations(article, currentRels, kb, currentdiag, currentweb);

				// tail with thresholds
				if (elements.size() == 3) {
					Section tail = elements.get(2);
					if (tail.get() instanceof XCLTail) setThresholds(kb, currentdiag,
							tail);

				}

			}
		}

		private void setThresholds(KnowledgeBase kb,
				Solution currentdiag, Section tail) {

			Collection<XCLModel> knowledge = kb.getAllKnowledgeSlicesFor(
					XCLModel.KNOWLEDGE_KIND);

			if (knowledge == null) return;

			Iterator<XCLModel> iterator = knowledge.iterator();

			while (iterator.hasNext()) {
				XCLModel model = iterator.next();
				if (model.getSolution().equals(currentdiag)) {

					double suggestedThreshold = XCLTail.getSuggestedThreshold(tail);
					if (suggestedThreshold != -1) model.setSuggestedThreshold(suggestedThreshold);

					double establishedThreshold = XCLTail.getEstablisehdThreshold(tail);
					if (establishedThreshold != -1) model.setEstablishedThreshold(establishedThreshold);

					double minsupport = XCLTail.getMinSupport(tail);
					if (minsupport != -1) model.setMinSupport(minsupport);

				}

			}
		}

		/**
		 * Inserts Relation into currentModel
		 * 
		 * @param currentRels
		 * @param kbm
		 * @param currentdiag
		 * @param currentWeb
		 * @return
		 */
		private void insertRelations(KnowWEArticle article, List<Section> currentRels,
				KnowledgeBase kb, Solution currentdiag, String currentWeb) {

			for (Section rel : currentRels) {
				double weight = this.getWeight(rel);
				XCLRelationType relationType = getRelationType(rel);
				// Get the Conditions
				Condition cond = FindingToConditionBuilder.analyseAnyRelation(article, rel, kb);

				if (cond == null) continue;

				// Insert the Relation into the currentModel
				String kbRelId = XCLModel.insertXCLRelation(kb, cond,
						currentdiag, relationType, weight, rel.getID());
				KnowWEUtils.storeObject(currentWeb, article.getTitle(), rel.getID(), KBID_KEY,
						kbRelId);

			}

		}

		/**
		 * Gets the weight from a Relation. If it has none it returns 1.0.
		 * 
		 * @param rel
		 * @return
		 */
		private double getWeight(Section<?> rel) {
			try {
				if (Sections.findChildOfType(rel, XCLRelationWeight.class) != null) {
					Section<?> relWeight = Sections.findChildOfType(rel, XCLRelationWeight.class);
					String weight = relWeight.getOriginalText();
					weight = weight.replaceAll("\\[", "");
					weight = weight.replaceAll("\\]", "");
					return Double.valueOf(weight);
				}
			}
			catch (NumberFormatException e27) {
				// Do Nothing
			}
			return 1.0;
		}

		/**
		 * Gets all XCLRelations from a given XCLBody
		 * 
		 * @param section
		 * @return
		 */
		private List<Section<?>> getXCLRelations(Section<?> body) {
			ArrayList<Section<?>> rels = new ArrayList<Section<?>>(body.getChildren().size());

			// get the XCLRelation Sections
			// Sort out Relations only containing PlainText
			List<Section<?>> children = body.getChildren();
			for (Section<?> sec : children) {
				if (sec.get() instanceof XCLRelation) {
					if ((Sections.findChildOfType(sec, ComplexFinding.class) != null)
							|| (Sections.findChildOfType(sec, Finding.class) != null)
							|| (Sections.findChildOfType(sec, NegatedFinding.class) != null)) {
						rels.add(sec);
					}
				}
			}
			return rels;
		}

		/**
		 * Gets XCLHead, XCLBody and XCLTail from a given XCList type.
		 * 
		 * @param children
		 * @return
		 */
		private List<Section> getXCLHeadBodyTail(List<Section> children) {
			for (int i = 0; i < children.size(); i++) {
				Type name = children.get(i).get();
				if ((name instanceof XCLHead) || (name instanceof XCLBody)
						|| (name instanceof XCLTail)) {
					continue;
				}
				children.remove(i--);
			}
			return children;
		}

	}

	/**
	 * Gets the RelationType from Relation
	 * 
	 * @param rel
	 * @return
	 */
	public static XCLRelationType getRelationType(Section<XCLRelationWeight> rel) {

		if (Sections.findChildOfType(rel, XCLRelationWeight.class) != null) {
			Section<? extends XCLRelationWeight> relWeight = Sections.findChildOfType(rel,
					XCLRelationWeight.class);
			String weightString = relWeight.getOriginalText();
			return getXCLRealtionTypeForString(weightString);
		}

		return XCLRelationType.explains;
	}

	public static XCLRelationType getXCLRealtionTypeForString(String weightString) {
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
