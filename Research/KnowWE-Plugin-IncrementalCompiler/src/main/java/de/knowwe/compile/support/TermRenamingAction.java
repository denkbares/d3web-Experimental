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
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

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
import de.knowwe.core.report.Messages;

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
public class TermRenamingAction extends AbstractAction {

	public static final String TERMNAME = "termname";
	public static final String REPLACEMENT = "termreplacement";

	@Override
	public void execute(UserActionContext context) throws IOException {

		String web = context.getParameter(Attributes.WEB);
		String term = context.getParameter(TERMNAME);
		String replacement = context.getParameter(REPLACEMENT);

		ReferenceManager referenceManager = IncrementalCompiler.getInstance().getTerminology();

		Map<String, Set<Section<?>>> allTerms =
				new HashMap<String, Set<Section<?>>>();

		// Check all TermDefinitions
		Collection<Section<? extends SimpleDefinition>> termDefinitions = referenceManager.getTermDefinitions(term);
		for (Section<? extends SimpleDefinition> section : termDefinitions) {
			Set<Section<?>> articleSecs;
			if (allTerms.containsKey(section.getTitle())) {
				articleSecs = allTerms.get(section.getTitle());
			}
			else {
				articleSecs = new HashSet<Section<?>>();
				allTerms.put(section.getTitle(), articleSecs);
			}
			articleSecs.add(section);
		}

		// Check all TermReferences
		Collection<Section<? extends SimpleReference>> termReferences = referenceManager.getTermReferences(term);
		for (Section<? extends SimpleReference> section : termReferences) {
			Set<Section<?>> articleSecs;
			if (allTerms.containsKey(section.getTitle())) {
				articleSecs = allTerms.get(section.getTitle());
			}
			else {
				articleSecs = new HashSet<Section<?>>();
				allTerms.put(section.getTitle(), articleSecs);
			}
			articleSecs.add(section);
		}

		ArticleManager mgr = Environment.getInstance().getArticleManager(web);
		Set<String> failures = new HashSet<String>();
		Set<String> success = new HashSet<String>();
		renameTerms(allTerms, replacement, mgr, context, failures,
				success);
		generateMessage(failures, success, context);
	}

	private void generateMessage(Set<String> failures, Set<String> success, UserActionContext context) throws IOException {
		ResourceBundle rb = Messages.getMessageBundle();
		Writer w = context.getWriter();
		if (failures.size() == 0) {
			w.write("<p style=\"color:green;\">");
			w.write(rb.getString("KnowWE.ObjectInfoTagHandler.renamingSuccessful"));
			w.write("</p>");
			w.write("<ul>");
			for (String article : success) {
				w.write("<li>");
				w.write(article);
				w.write("</li>");
			}
			w.write("</ul>");
		}
		else {
			w.write("<p style=\"color:red;\">");
			w.write(rb.getString("KnowWE.ObjectInfoTagHandler.renamingFailed"));
			w.write("</p>");
			w.write("<ul>");
			for (String article : failures) {
				w.write("<li>");
				w.write(article);
				w.write("</li>");
			}
			w.write("</ul>");
		}
	}

	private void renameTerms(Map<String, Set<Section<?>>> allTerms,
			String replacement,
			ArticleManager mgr,
			UserActionContext context,
			Set<String> failures,
			Set<String> success) throws IOException {

		for (String articlename : allTerms.keySet()) {
			if (Environment.getInstance().getWikiConnector().userCanEditPage(
					articlename, context.getRequest())) {

				Map<String, String> nodesMap = new HashMap<String, String>();
				for (Section<?> section : allTerms.get(articlename)) {

					nodesMap.put(section.getID(), replacement);

				}
				Sections.replaceSections(context,
						nodesMap);
				success.add(articlename);
			}
			else {
				failures.add(articlename);
			}
		}

	}
}
