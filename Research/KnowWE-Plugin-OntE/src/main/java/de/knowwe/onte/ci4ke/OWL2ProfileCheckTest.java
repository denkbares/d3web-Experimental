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
package de.knowwe.onte.ci4ke;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;

import cc.denkbares.testing.ArgsCheckResult;
import cc.denkbares.testing.Message;
import cc.denkbares.testing.Message.Type;
import cc.denkbares.testing.Test;
import de.knowwe.owlapi.OWLAPIConnector;

/**
 * A simple test for the continuous integration plugin for KnowWE. This test
 * checks if the local ontology is within a certain OWL 2 Profile (DL, EL, QL,
 * RL, OWL2 Full).
 * 
 * @author Stefan Mark
 * @created 17.10.2011
 */
public class OWL2ProfileCheckTest implements Test<OWLAPIConnector> {

	public static final String OWL2_PROFILE_DL = "DL";
	public static final String OWL2_PROFILE_EL = "EL";
	public static final String OWL2_PROFILE_QL = "QL";
	public static final String OWL2_PROFILE_RL = "RL";
	public static final String OWL2_PROFILE_FULL = "FULL";

	@Override
	public Message execute(OWLAPIConnector connector, String[] args) {

		// OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
		OWLOntology o = connector.getOntology();

		OWLProfile profile = null;
		String givenProfile = args[0].toUpperCase();

		if (givenProfile.equals(OWL2_PROFILE_DL)) {
			profile = new OWL2DLProfile();
		}
		else if (givenProfile.equals(OWL2_PROFILE_EL)) {
			profile = new OWL2ELProfile();
		}
		else if (givenProfile.equals(OWL2_PROFILE_RL)) {
			profile = new OWL2RLProfile();
		}
		else if (givenProfile.equals(OWL2_PROFILE_QL)) {
			profile = new OWL2QLProfile();
		}
		else if (givenProfile.equals(OWL2_PROFILE_FULL)) {
			profile = new OWL2Profile();
		}

		OWLProfileReport report = profile.checkOntology(o);

		StringBuilder configuration = new StringBuilder();
		configuration.append("Checking OWL2 Profile of local ontology with <strong>'");
		configuration.append(profile.getName());
		configuration.append("'</strong>");

		StringBuilder message = new StringBuilder();

		if (report.isInProfile()) {

			message.append("Local ontology is in OWL2 Profile: true");
			return new Message(Type.SUCCESS, message.toString());
		}
		else {
			message.append("Local ontology is in OWL2 Profile: false");
			return new Message(Type.FAILURE, message.toString());
		}
	}

	@Override
	public ArgsCheckResult checkArgs(String[] args) {

		// check if the parameters match the necessary amount
		if (!(args.length == 1)) {

			StringBuilder errorMessage = new StringBuilder();
			errorMessage.append("The number of arguments for the test '");
			errorMessage.append(this.getClass().getSimpleName());
			errorMessage.append("' are not sufficient. Please specify 1 argument: ");
			errorMessage.append("arg0: Type of the OWL2 profile");

			return new ArgsCheckResult(ArgsCheckResult.Type.ERROR, errorMessage.toString());
		}
		return new ArgsCheckResult(ArgsCheckResult.Type.FINE);
	}

	@Override
	public Class<OWLAPIConnector> getTestObjectClass() {
		return OWLAPIConnector.class;
	}
}
