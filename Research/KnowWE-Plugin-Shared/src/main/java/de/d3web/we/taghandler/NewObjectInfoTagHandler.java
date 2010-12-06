/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.taghandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

/**
 * ObjectInfo TagHandler
 *
 * This TagHandler gathers information about a specified Object. The TagHanlder
 * shows the article in which the object is defined and all articles with
 * references to this object.
 *
 * Additionally there is a possibility to rename this object in all articles and
 * to create a wiki page for this object.
 *
 * @author Sebastian Furth
 * @created 01.12.2010
 */
public class NewObjectInfoTagHandler extends AbstractDefaultStyledTagHandler {

	public static final String OBJECTNAME = "objectname";

	public NewObjectInfoTagHandler() {
		super("NewObjectInfo", true);
	}

	@Override
	public String renderContent(KnowWEArticle article, Section<?> section, KnowWEUserContext user, Map<String, String> parameters) {

		Map<String, String> urlParameters = user.getUrlParameterMap();

		// First try the URL-Parameter, if null try the TagHandler-Parameter.
		String objectName = urlParameters.get(OBJECTNAME) != null
				? KnowWEUtils.html_escape(urlParameters.get(OBJECTNAME))
				: KnowWEUtils.html_escape(parameters.get(OBJECTNAME));

		// If name is not defined -> render search form!
		if (objectName == null) {
			return KnowWEUtils.maskHTML(renderForm(article));
		}

		return KnowWEUtils.maskHTML(renderObjectInfo(objectName, article.getWeb()));
	}

	private String renderForm(KnowWEArticle article) {
		StringBuilder html = new StringBuilder();
		html.append("<strong>");
		// TODO: Strings -> Resource Bundle
		html.append("Look up object information");
		html.append("</strong>");

		html.append("<form action=\"\" method=\"get\">");
		html.append("<input type=\"hidden\" name=\"page\" value=\""
				+ KnowWEUtils.urlencode(article.getTitle())
				+ "\" />");
		html.append("<input type=\"text\" name=\"" + OBJECTNAME + "\" /> ");
		html.append("<input type=\"submit\" value=\"&rarr;\" />");
		html.append("</form>");

		return html.toString();
	}

	private String renderObjectInfo(String objectName, String web) {
		StringBuilder html = new StringBuilder();
		html.append("<strong>");
		html.append(objectName);
		html.append("</strong>");

		TerminologyHandler th = KnowWEUtils.getTerminologyHandler(web);
		Section<? extends TermDefinition> definition;
		Set<Section<? extends TermDefinition>> definitions = new HashSet<Section<? extends TermDefinition>>();
		Set<Section<? extends TermReference>> references = new HashSet<Section<? extends TermReference>>();
		Set<Section<? extends TermReference>> temp = new HashSet<Section<? extends TermReference>>();

		Iterator<KnowWEArticle> iter = KnowWEEnvironment.getInstance().getArticleManager(web).getArticleIterator();
		KnowWEArticle currentArticle;

		while (iter.hasNext()) {
			currentArticle = iter.next();

			// Check if there is a TermDefinition
			definition = th.getTermDefiningSection(currentArticle, objectName, KnowWETerm.LOCAL);
			if (definition != null) {
				definitions.add(definition);
			}

			// Check if there are References
			temp = th.getTermReferenceSections(currentArticle, objectName, KnowWETerm.LOCAL);
			if (temp != null && temp.size() > 0) {
				references.addAll(temp);
			}
		}

		html.append(renderTermDefinitions(definitions));
		html.append("<hr />");
		html.append(renderTermReferences(references));

		return html.toString();
	}

	private String renderTermDefinitions(Set<Section<? extends TermDefinition>> definitions) {
		StringBuilder html = new StringBuilder();

		if (definitions.size() > 0) {
			html.append("<p>");
			for (Section<? extends TermDefinition> definition : definitions) {
				html.append("<strong>");
				html.append(definition.getObjectType().getName());
				html.append("</strong>: ");
				html.append("<a href=\"Wiki.jsp?page=");
				html.append(KnowWEUtils.urlencode(definition.getTitle()));
				html.append("\" >");
				html.append(definition.getTitle());
				html.append("</a>");
				html.append("<br />");
			}
			html.append("</p>");
		}

		return html.toString();
	}

	private String renderTermReferences(Set<Section<? extends TermReference>> references) {

		StringBuilder html = new StringBuilder();

		if (references.size() > 0) {

			// Group References by article
			Map<KnowWEArticle, List<Section<? extends TermReference>>> groupedReferences = groupByArticle(references);

			// For each article
			for (KnowWEArticle article : groupedReferences.keySet()) {
				html.append("<p>");
				html.append("<a href=\"Wiki.jsp?page=");
				html.append(KnowWEUtils.urlencode(article.getTitle()));
				html.append("\" >");
				html.append(article.getTitle());
				html.append("</a>");
				html.append("<ul>");
				// render references for current article
				for (Section<? extends TermReference> reference : groupedReferences.get(article)) {
					html.append("<li>");
					html.append("<strong>");
					html.append(reference.getObjectType().getName());
					html.append("</strong>: ");
					html.append(reference.getFather().getObjectType().getName());
					html.append("</li>");
				}
				html.append("</ul>");
				html.append("</p>");
			}

		}

		return html.toString();
	}

	private Map<KnowWEArticle, List<Section<? extends TermReference>>> groupByArticle(Set<Section<? extends TermReference>> references) {

		Map<KnowWEArticle, List<Section<? extends TermReference>>> result = new HashMap<KnowWEArticle, List<Section<? extends TermReference>>>();
		KnowWEArticle article;

		for (Section<? extends TermReference> reference : references) {
			article = reference.getArticle();
			List<Section<? extends TermReference>> existingReferences = result.get(article);
			if (existingReferences == null) {
				existingReferences = new LinkedList<Section<? extends TermReference>>();
			}
			existingReferences.add(reference);
			result.put(article, existingReferences);
		}

		return result;
	}

}
