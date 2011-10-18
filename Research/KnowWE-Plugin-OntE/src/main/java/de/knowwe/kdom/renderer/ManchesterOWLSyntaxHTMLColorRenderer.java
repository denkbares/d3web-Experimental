/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.renderer;

import java.util.StringTokenizer;

import org.semanticweb.owlapi.model.OWLAxiom;

import de.knowwe.core.compile.TerminologyHandler;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.onte.editor.OWLApiAxiomCache;
import de.knowwe.util.ManchesterSyntaxKeywords;

/**
 *
 *
 * @author Stefan Mark
 * @created 17.10.2011
 */
public class ManchesterOWLSyntaxHTMLColorRenderer {

	private final TerminologyHandler terminology;

	public ManchesterOWLSyntaxHTMLColorRenderer(TerminologyHandler terminology) {
		this.terminology = terminology;
	}

	/**
	 *
	 *
	 * @created 17.10.2011
	 * @param axiom
	 */
	public void colorize(String token, StringBuilder doc, OWLAxiom axiom) {

		StringTokenizer st = new StringTokenizer(token, " ([{}])", true);

		while (st.hasMoreTokens()) {
			String curToken = st.nextToken();

			if (curToken.equals("(")) { // break lines
				doc.append(curToken);
			}
			else if (isRestrictionKeyWord(curToken)) {
				doc.append(" <span style=\"" + StyleRenderer.OPERATOR.getCssStyle() + "\">");
				doc.append(curToken);
				doc.append("</span> ");
			}
			else if (isCharacteristic(curToken)) {
				doc.append(" <span style=\"" + StyleRenderer.OPERATOR.getCssStyle() + "\">");
				doc.append(curToken);
				doc.append("</span> ");
			}
			else if (terminology.getAllGlobalTerms().contains(curToken)) {
				doc.append(" <span style=\"color:rgb(25, 180, 120);\">");
				doc.append(curToken);
				doc.append("</span> ");
			}
			else {
				doc.append(curToken);
				doc.append(" ");
			}
		}

		Section<? extends Type> section = OWLApiAxiomCache.getInstance().lookUpSection(axiom,
				OWLApiAxiomCache.STORE_CACHE);
		if (section != null) {
			renderHyperlink(section, doc);
		}
	}

	/**
	 * Checks weather a given token is a restriction keyword.
	 *
	 * @created 17.10.2011
	 * @param keyword
	 * @return
	 */
	private boolean isRestrictionKeyWord(String keyword) {
		keyword = keyword.toUpperCase().trim();
		if (keyword.equals(ManchesterSyntaxKeywords.SOME.getKeyword())
				|| keyword.equals(ManchesterSyntaxKeywords.ONLY.getKeyword())
				|| keyword.equals(ManchesterSyntaxKeywords.EXACTLY.getKeyword())
				|| keyword.equals(ManchesterSyntaxKeywords.MIN.getKeyword())
				|| keyword.equals(ManchesterSyntaxKeywords.MAX.getKeyword())
				|| keyword.equals(ManchesterSyntaxKeywords.VALUE.getKeyword())
				|| keyword.equals(ManchesterSyntaxKeywords.SELF.getKeyword())) {
			return true;
		}
		return false;
	}

	/**
	 * Checks weather a given token is a characteristics keyword.
	 *
	 * @created 17.10.2011
	 * @param keyword
	 * @return
	 */
	private boolean isCharacteristic(String keyword) {
		keyword = keyword.trim();
		if (keyword.equals(ManchesterSyntaxKeywords.ASYMMETRIC.getKeyword()) ||
				keyword.equals(ManchesterSyntaxKeywords.SYMMETRIC.getKeyword()) ||
				keyword.equals(ManchesterSyntaxKeywords.FUNCTIONAL.getKeyword()) ||
				keyword.equals(ManchesterSyntaxKeywords.INVERSE_FUNCTIONAL.getKeyword()) ||
				keyword.equals(ManchesterSyntaxKeywords.TRANSITIVE.getKeyword()) ||
				keyword.equals(ManchesterSyntaxKeywords.REFLEXIVE.getKeyword()) ||
				keyword.equals(ManchesterSyntaxKeywords.IRREFLEXIVE.getKeyword())) {
			return true;
		}
		return false;
	}



	public static void renderHyperlink(Section<? extends Type> section, StringBuilder doc) {
		doc.append("<br /><span style=\"font-size:9px;padding-left:30px;\">(Asserted in local article: ");
		doc.append("<a href=\"Wiki.jsp?page=" + section.getArticle().getTitle()
				+ "\" title=\"Goto definition article\">");
		doc.append(section.getArticle().getTitle());
		doc.append("</a>");
		doc.append(")</span>");
	}
}
