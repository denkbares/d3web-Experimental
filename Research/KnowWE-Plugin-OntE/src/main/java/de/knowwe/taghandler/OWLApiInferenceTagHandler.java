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
package de.knowwe.taghandler;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;

import de.d3web.we.taghandler.AbstractHTMLTagHandler;
import de.d3web.we.taghandler.TagHandler;
import de.d3web.we.user.UserContext;
import de.knowwe.owlapi.OWLAPIConnector;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * <p>
 * The {@link OWLApiInferenceTagHandler} shows inferred information. Currently
 * the {@link TagHandler} can show to a given class the inferred hierarchy
 * relations, to a given class all the instances.
 * </p>
 *
 * necessary features???
 * <ul>
 * <li>get inverse of a property, i.e. which individuals are in relation with a
 * given individual</li>
 * <li>find to which classes the individual belongs</li>
 * <li>list all object property values for the individual</li>
 * <li>ask reasoner whether individual isProperty something</li>
 * <li>explain given OWLAxioms</li>
 * </ul>
 *
 * @author Stefan Mark
 * @created 20.09.2011
 */
public class OWLApiInferenceTagHandler extends AbstractHTMLTagHandler {

	public final static String PARAM_KEY_INFERENCE = "inference";
	public final static String PARAM_KEY_OBJECT = "object";
	public final static String PARAM_KEY_OBJECT2 = "object2";

	public final static String PARAM_INFERENCE_CLASS = "class";
	public final static String PARAM_INFERENCE_INDIVIDUAL = "individual";
	public final static String PARAM_INFERENCE_PROPERTY = "property";

	public OWLApiInferenceTagHandler() {
		super("owlapi.inference");
	}

	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		String paramInference = parameters.get(PARAM_KEY_INFERENCE);
		String paramObject = parameters.get(PARAM_KEY_OBJECT);

		StringBuilder html = new StringBuilder();
		html.append("<div style=\"background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");

		if (paramInference != null && !paramInference.isEmpty()
				&& paramObject != null && !paramObject.isEmpty()) {

			OWLAPIConnector connector = OWLAPIConnector.getGlobalInstance();
			OWLDataFactory factory = connector.getManager().getOWLDataFactory();
			OWLReasoner reasoner = connector.getReasoner();
			PrefixManager pm = new DefaultPrefixManager(Rdf2GoCore.basens);

			OWLClass clazz = factory.getOWLClass(":" + paramObject, pm);

			if (paramInference.equals(PARAM_INFERENCE_CLASS)) {
				OWLApiTagHandlerUtil.printInferredClassHierarchy(reasoner, clazz, 0, html);
			}
			else if (paramInference.equals(PARAM_INFERENCE_INDIVIDUAL)) {
				OWLApiTagHandlerUtil.printInferredIndividuals(reasoner, clazz, html);
			}
			else if (paramInference.equals(PARAM_INFERENCE_PROPERTY)) {
				// not yet :)
			}
		}
		else {
			html.append(helpMessage());
		}

		html.append("</div>");
		return html.toString();
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 *
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	private String helpMessage() {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>[{KnowWEPlugin owlapiinference}] - print inferred knowledge from an OWL ontology</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin owlapiinference}] - Prints this help message.</dd>");
		help.append("<dd>[{KnowWEPlugin owlapiinference , inference=class , object=Pizza}] - Prints class hierarchy starting with value from object. In this case \"Pizza\".</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiInferenceTagHandler prints out inferred knowledge from an OWL ontology. "
				+
				"The following explanation of the parameters 'inference' and 'object' show how to use the taghandler.<br />"
				+
				"[inference=class|individual|property] : class=A hierarchy starting with the specified class as root node is printed out;<br /> "
				+
				"individual=All individual of the specified class are retrieved; property=Retrieve all values for the specified property of a given individual; <br />"
				+
				"[object=name of the class|individual|property] : The 'object' parameter specifies the object the inference shoud start with. "
				+
				"E.g. in case of class 'inference' the heirarchy inferred from the reasoner starts with the value of the object parameter as root node."
				+
				"The type of the inferred knowledge can be specified through the 'inference' parameter of the taghandler."
				+
				"" +
				"</dd>");

		help.append("</dl>");

		return help.toString();
	}
}
