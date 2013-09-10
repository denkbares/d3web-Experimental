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

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.taghandler.OWLApiTagHandlerUtil;

/**
 *
 *
 * @author Stefan Mark
 * @created 05.12.2011
 */
public class OWL2ProfileValidationTabAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		StringBuilder html = new StringBuilder();

		OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLOntology o = connector.getOntology();

		String toCheckProfile = context.getParameter("profile");
		toCheckProfile = toCheckProfile.toUpperCase();


		if (toCheckProfile.equals("DL")) {
			checkProfile(new OWL2DLProfile(), o, html);
		}
		else if (toCheckProfile.equals("EL")) {
			checkProfile(new OWL2ELProfile(), o, html);
		}
		else if (toCheckProfile.equals("QL")) {
			checkProfile(new OWL2QLProfile(), o, html);
		}
		else if (toCheckProfile.equals("RL")) {
			checkProfile(new OWL2RLProfile(), o, html);
		}
		else if (toCheckProfile.equals("FULL")) {
			checkProfile(new OWL2Profile(), o, html);
		}
		context.getWriter().write(html.toString());
	}

	private void checkProfile(OWLProfile profile, OWLOntology o, StringBuilder html) {

		OWLProfileReport report = profile.checkOntology(o);

		if (report.isInProfile()) {
			html.append("<span style=\"color:#00B233\">");
			html.append("The ontology and all of its imports are in the ")
					.append(report.getProfile().getName()).append(" profile");
			html.append("</span>");
		}
		else {
			html.append("<span style=\"color:#7F0000\">");
			html.append("The ontology and all of its imports are not in the ")
					.append(report.getProfile().getName()).append(" profile");
			html.append("</span>");

			html.append("<h3>Summary</h3>");
			html.append("<ul>");

			for (OWLProfileViolation violation : report.getViolations()) {
				String verbalized = OWLApiTagHandlerUtil.verbalizeToManchesterSyntax(violation.getAxiom());
				html.append("<li>").append(violation.getClass().getSimpleName()).append(
						" [" + verbalized + " ]").append(
						"</li>");
			}
			html.append("</ul>");

		}
	}
}
