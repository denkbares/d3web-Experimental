/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.knowwe.compile.support;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.denkbares.strings.Identifier;
import com.denkbares.strings.Strings;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * Action which renames all Definitions and References of a given Term. The
 * following parameters are mandatory!
 * <ul>
 * <li>termname</li>
 * <li>termreplacement</li>
 * <li>web</li>
 * </ul>
 * 
 * @author Sebastian Furth
 * @created Dec 15, 2010
 */
public class TermRenamingActionIncr extends AbstractAction {

	public static final String TERMNAME = "termname";
	public static final String REPLACEMENT = "termreplacement";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String web = context.getParameter(Attributes.WEB);
		String term = Strings.unquote(context.getParameter(TERMNAME));
		String replacement = context.getParameter(REPLACEMENT);
		Identifier termIdentifier = new Identifier(term);

		ReferenceManager referenceManager = IncrementalCompiler.getInstance().getTerminology();

		Map<String, Set<Section<?>>> allTerms =
				new HashMap<>();

		// Check all TermDefinitions
		Collection<Section<? extends SimpleDefinition>> termDefinitions = referenceManager.getTermDefinitions(termIdentifier);
		for (Section<? extends SimpleDefinition> section : termDefinitions) {
			Set<Section<?>> articleSecs;
			if (allTerms.containsKey(section.getTitle())) {
				articleSecs = allTerms.get(section.getTitle());
			}
			else {
				articleSecs = new HashSet<>();
				allTerms.put(section.getTitle(), articleSecs);
			}
			articleSecs.add(section);
		}

		// Check all TermReferences
		Collection<Section<? extends SimpleReference>> termReferences = referenceManager.getTermReferences(termIdentifier);
		for (Section<? extends SimpleReference> section : termReferences) {
			Set<Section<?>> articleSecs;
			if (allTerms.containsKey(section.getTitle())) {
				articleSecs = allTerms.get(section.getTitle());
			}
			else {
				articleSecs = new HashSet<>();
				allTerms.put(section.getTitle(), articleSecs);
			}
			articleSecs.add(section);
		}

		ArticleManager mgr = Environment.getInstance().getArticleManager(web);
		Set<String> failures = new HashSet<>();
		Set<String> success = new HashSet<>();
		renameTerms(allTerms, replacement, mgr, context, failures,
				success);
		generateMessage(failures, success, context, termIdentifier, replacement);
	}

	private void generateMessage(Set<String> failures, Set<String> success, UserActionContext context, Identifier termIdentifier, String replacement) throws IOException {
		JSONObject response = new JSONObject();
		try {
			// the new external form of the TermIdentifier
			String[] pathElements = termIdentifier.getPathElements();
			String newLastPathElement = Identifier.fromExternalForm(replacement).getLastPathElement();
			pathElements[pathElements.length - 1] = newLastPathElement;
			response.append("newTermIdentifier", new Identifier(pathElements).toExternalForm());

			// the new object name
			response.append("newObjectName",
					new Identifier(newLastPathElement).toExternalForm());
			StringBuilder builder = new StringBuilder();

			// successes
			for (String article : success) {
				builder.append("##");
				builder.append(article);
			}
			builder.append("###");
			// failures
			for (String article : failures) {
				builder.append("##");
				builder.append(article);
			}
			response.accumulate("renamedArticles", builder);

			response.write(context.getWriter());
		}
		catch (JSONException e) {
			throw new IOException(e.getMessage());
		}
	}

	private void renameTerms(Map<String, Set<Section<?>>> allTerms,
			String replacement,
			ArticleManager mgr,
			UserActionContext context,
			Set<String> failures,
			Set<String> success) throws IOException {
		mgr.open();
		try {
		for (String articlename : allTerms.keySet()) {
			if (Environment.getInstance().getWikiConnector().userCanEditArticle(
					articlename, context.getRequest())) {

				Map<String, String> nodesMap = new HashMap<>();
				for (Section<?> section : allTerms.get(articlename)) {

					nodesMap.put(section.getID(), replacement);

				}
				Sections.replace(context,
						nodesMap).sendErrors(context);
				success.add(articlename);
			}
			else {
				failures.add(articlename);
			}
		}
		}
		finally {
			mgr.commit();
		}

	}
}
