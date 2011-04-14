/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.hermes.kdom.conceptMining;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.exception.ModelRuntimeException;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.util.RDFTool;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.contexts.Context;
import de.d3web.we.kdom.contexts.ContextManager;
import de.d3web.we.kdom.contexts.DefaultSubjectContext;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.hermes.kdom.TimeEventType;
import de.knowwe.rdf2go.DefaultURIContext;
import de.knowwe.rdf2go.Rdf2GoCore;

public class ConceptOccurrenceRenderer extends KnowWEDomRenderer {

	private static String TITLE_QUERY = "SELECT  ?title WHERE {  <URI> lns:hasTitle ?title }";

	@Override
	public void render(KnowWEArticle article, Section arg0, UserContext arg1, StringBuilder arg2) {

		Section<PersonOccurrence> personSection = arg0;

		// TableUtils.getRow(arg0);

		String conceptName = arg0.getOriginalText();

		Context context = ContextManager.getInstance().getContext(arg0, DefaultSubjectContext.CID);

		String subjectString = "error: subject not found!";
		URI subjectURI = null;

		if (context != null) {
			if (context instanceof DefaultURIContext) {
				subjectURI = ((DefaultURIContext) context).getSolutionURI();
			}
			if (context instanceof DefaultSubjectContext) {
				subjectURI = Rdf2GoCore.getInstance().createlocalURI(
						((DefaultSubjectContext) context).getSubject());
			}
			subjectString = RDFTool.getLabel(subjectURI);
			String q = TITLE_QUERY.replaceAll("URI", subjectURI.toString());
			ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(q);
			if (result != null) {
				try {
					if (result.hasNext()) {
						QueryRow row = result.next();
						String title = row.getValue("title").toString();
						try {
							title = URLDecoder.decode(title, "UTF-8");
							subjectString = title;
						}
						catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				catch (ModelRuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// <img src='KnowWEExtension/images/question.gif' width='12' />
		// id='" + arg0.getId() + "'

		String htmlContent1 = "<strong>"
				+ arg0.getOriginalText()
				+ "</strong>"
				+ "<img rel=\"{type: '"
				+ conceptName
				+ "', id: '"
				+ arg0.getID()
				+ "', termName: '"
				+ conceptName
				+ "', user:'"
				+ arg1.getUserName()
				+ "'}\" class=\"conceptLink pointer\" id='"
				+ arg0.getID()
				+ "' src='KnowWEExtension/images/question.gif' width='12' /> "
				+ "<span id='"
				+ arg0.getID()
				+ "_popupcontent' style='visibility:hidden;display:none;position:fixed' >";

		String popupContent = generatePopupContent(arg0, subjectURI,
				subjectString);

		if (popupContent == null) {
			arg2.append("__" + conceptName + "__");
			return;
		}

		String htmlContentTail = "</span>";
		arg2.append(KnowWEUtils.maskHTML(htmlContent1));
		arg2.append(KnowWEUtils.maskHTML(popupContent));
		arg2.append(KnowWEUtils.maskHTML(htmlContentTail));

	}

	private static final String PROP_SPARQL = "SELECT ?x WHERE {  "
			+ "?x rdf:type owl:ObjectProperty .   "
			+ "?x rdfs:domain <SUBJECT> .		   " + "?x rdfs:range <OBJECT>."
			+ "} ";

	protected String[] getPossibleProperties(URI subject, String object) {
		List<String> propList = new ArrayList<String>();

		String classSparql = "SELECT ?x WHERE { <URI> rdf:type ?x.} ";

		// all classes the subject belongs to
		ClosableIterator<QueryRow> subjectClasses = Rdf2GoCore.getInstance().sparqlSelectIt(
				classSparql.replaceAll("URI", subject.toString()));
		// TupleQueryResult subjectClasses = SPARQLUtil
		// .findClassesOfEntity(subject);

		// all classes the object belongs to
		ClosableIterator<QueryRow> objectClasses = Rdf2GoCore.getInstance().sparqlSelectIt(
				classSparql.replaceAll("URI",
						Rdf2GoCore.getInstance().createlocalURI(object).toString()));
		// TupleQueryResult objectClasses = SPARQLUtil
		// .findClassesOfEntity(UpperOntology.getInstance().getHelper()
		// .createlocalURI(object));

		try {
			while (subjectClasses.hasNext()) {
				QueryRow subjectClass = subjectClasses.next();
				String subjectClazzString = subjectClass.getValue("x").toString();

				while (objectClasses.hasNext()) {
					QueryRow objectClass = objectClasses.next();
					Node objectNode = objectClass.getValue("x");
					String objectClassString = objectNode.toString();

					// new..
					// String name = bindingX.getName();
					// TupleQueryResult objectSuperClasses =
					// SPARQLUtil.findSuperClasses(UpperOntology.getInstance().getHelper().createlocalURI(
					// name));
					//
					// while (objectSuperClasses.hasNext()) {
					// BindingSet objectSuperClass = objectClasses.next();
					// Binding bindingSuperX = objectClass.getBinding("x");
					// String objectSuperClassString =
					// bindingX.getValue().toString();
					// }

					String q = PROP_SPARQL.replaceAll("SUBJECT", subjectClazzString);
					q = q.replaceAll("OBJECT", objectClassString);
					ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(q);

					if (result != null) {
						while (result.hasNext()) {
							QueryRow row = result.next();
							Node propB = row.getValue("x");
							String propName = propB.toString();

							try {
								propName = URLDecoder.decode(propName,
											"UTF-8");
							}
							catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							propList.add(propName.substring(propName.lastIndexOf('#') + 1));
						}

					}
				}

			}
			return propList.toArray(new String[propList.size()]);
		}
		catch (ModelRuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return new String[] {};
	}

	private String generatePopupContent(Section arg0, URI subject, String subjectTitle) {
		StringBuffer buffy = new StringBuffer();

		buffy.append("<div style='padding:10px' class=\"confirmPanel\" >");

		buffy.append("<strong>" + subjectTitle + "</strong>");

		buffy.append("<ul style='padding:10px' class=\"options\" >");

		String originalText = arg0.getOriginalText();
		String[] opts = getPossibleProperties(subject, originalText);

		String[] newOpts = filterOpts(subject, originalText, opts);

		if (newOpts.length == 0) return null;

		String[] defaultOpts = {
				"concept missmatch", "dont ask again" };

		Section<? extends TimeEventType> eventSection = Sections
				.findAncestorOfType(arg0, TimeEventType.class);
		if (eventSection == null) return null;

		for (String relationName : newOpts) {

			String options = "kdomid='" + arg0.getID() + "' subject='"
					+ subject + "' rel='" + relationName + "' object='"
					+ originalText + "' name='" + relationName + "' "
					+ "ancestor='" + eventSection.getID() + "'";

			buffy.append("<li><p class=\"confirmOption pointer\" " + options
					+ ">");
			buffy.append("" + relationName + "  " + "");
			buffy.append("<span style='font-style:italic' class='confirmobject' "
					+ options + ">" + originalText + " </span>");
			buffy.append("<em> ? </em>");
			buffy.append("</p></li>");
		}

		for (String string : defaultOpts) {
			buffy.append("<li><p class=\"confirmOption\" name='" + string
					+ "'>");
			buffy.append("" + string + "  " + "");
			buffy.append("</p></li>");
		}

		buffy.append("</ul>");
		buffy.append("</div>\n"); // add some \n from time to time to satisfy
		// jspwiki's paragraph length
		// restriction.... :p
		return buffy.toString();
	}

	private static final String RELATION_QUERY = "ASK { SUBJECT lns:RELATION OBJECT .}";

	private String[] filterOpts(URI subject, String originalText, String[] opts) {

		List<String> goodOpts = new ArrayList<String>();

		for (String relation : opts) {

			String q = RELATION_QUERY.replaceAll("SUBJECT", "<" + subject.toString() + ">");
			q = q.replaceAll("RELATION", relation);
			q = q.replaceAll("OBJECT", "<"
					+ Rdf2GoCore.getInstance().createlocalURI(originalText).toString() + ">");
			Boolean result = Rdf2GoCore.getInstance().sparqlAsk(q);
			if (result != null && !result.booleanValue()) {
				goodOpts.add(relation);
			}
		}
		return goodOpts.toArray(new String[goodOpts.size()]);
	}

}
