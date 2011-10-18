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
package de.knowwe.onte.action;

import java.io.IOException;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.renderer.ManchesterOWLSyntaxHTMLColorRenderer;
import de.knowwe.onte.editor.OWLApiAxiomCache;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 12.10.2011
 */
public class OWLApiCheckConsistency extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		StringBuilder html = new StringBuilder();

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLReasoner reasoner = connector.getReasoner();
		reasoner.precomputeInferences();

		Node<OWLClass> unsatNodes = reasoner.getUnsatisfiableClasses();

		html.append("<dl>");
		html.append("<dt>Checked concept consistency ...</dt>");

		Set<OWLClass> unsatisfiable = unsatNodes.getEntitiesMinusBottom();
		if (!unsatisfiable.isEmpty()) {

			ManchesterOWLSyntaxHTMLColorRenderer renderer = new ManchesterOWLSyntaxHTMLColorRenderer(
					KnowWEUtils.getTerminologyHandler(context.getWeb()));

			html.append("<dd><small>(Check any axiom in order to remove it and to restore consistency! Note: Changes cannot be undone!)</small></dd>");

			for (OWLClass clazz : unsatisfiable) {
				html.append("<dd><dl><dt> Concept '" + OWLApiTagHandlerUtil.labelClass(clazz)
						+ "' is inconsistent ... <br />... printing explanation:</dt>");
				Set<Set<OWLAxiom>> explanations = OWLApiTagHandlerUtil.getExplanations(clazz);
				for (Set<OWLAxiom> explanation : explanations) {
					for (OWLAxiom causingAxiom : explanation) {

						String verbalized = OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(causingAxiom);
						Section<? extends Type> section = OWLApiAxiomCache.getInstance().lookUpSection(
								causingAxiom, OWLApiAxiomCache.STORE_CACHE);
						OWLApiAxiomCache.getInstance().addToExplanation(section, causingAxiom);

						html.append("<dd>");
						html.append("<input type=\"checkbox\" value=\"yes\" name=\""
								+ section.getID()
								+ "\" style=\"vertical-align: top;\">");
						renderer.colorize(verbalized, html, causingAxiom);
						html.append("</dd>");
					}
				}
				html.append("<dl></dd>");
			}
		}
		else {
			html.append("<dd>No concept inconsistency found!</dd>");
		}
		html.append("</dl>");
		html.append(" <div class='onte-buttons onte-buttonbar'>"
				+ "    <a href='javascript:KNOWWE.plugin.onte.actions.repairOntology();void(0);' title='Repair checked axioms' class='left onte-button-txt'>Repair ontology</a>"
				+ " </div>");

		context.getWriter().write(html.toString());
	}
}
