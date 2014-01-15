/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.termbrowser;

import java.io.IOException;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 10.12.2012
 */
public class TermBrowserAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	/**
	 * 
	 * @created 10.12.2012
	 * @param context
	 * @return
	 */
	private String perform(UserActionContext context) {
		String command = context.getParameter("command");
		String term = context.getParameter("term");
		String master = TermBrowserMarkup.getCurrentTermbrowserMarkupMaster(context);

		/*
		 * treat case when the semantic-autocompletion slot sends full URIs
		 */
		if (term != null && term.startsWith("http:")) {
			Rdf2GoCore core = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
			term = Rdf2GoUtils.reduceNamespace(core, term);
			term = term.replace(":", "#");
		}

		if (term == null) {
			term = "";
		}
		else {
			if (command.equals("searched")) {
				// update ranking weights
				TermRecommender.getInstance().termSearched(context, createTermIdentifier(term));
			}
			else if (command.equals("remove")) {
				// removes this concept from the list
				TermRecommender.getInstance().clearTerm(context, createTermIdentifier(term));
			}
			else if (command.equals("expand")) {
				// adds all sub-concepts of a concept to the list
				TermRecommender.getInstance().expandTerm(context, createTermIdentifier(term));
			}
			else if (command.equals("addParent")) {
				// adds all sub-concepts of a concept to the list
				TermRecommender.getInstance().addParentTerm(context, createTermIdentifier(term));
			}
			else if (command.equals("collapse")) {
				// removes all sub-concepts of a concepts from the list
				TermRecommender.getInstance().collapseTerm(context, createTermIdentifier(term));
			}
			else if (command.equals("collapseGraph")) {
				// stores user's collapse state on server
				TermRecommender.getInstance().collapseGraph(context);
			}
			else if (command.equals("openGraph")) {
				// stores user's collapse state on server
				TermRecommender.getInstance().openGraph(context);
			}
			else if (command.equals("toggleGraph")) {
				// stores user's collapse state on server
				TermRecommender.getInstance().toggleGraph(context);
			}
			else if (command.equals("collapseList")) {
				// stores user's collapse state on server
				TermRecommender.getInstance().collapseList(context);
			}
			else if (command.equals("openList")) {
				// stores user's collapse state on server
				TermRecommender.getInstance().openList(context);
			}
			else if (command.equals("clear")) {
				// clears the entire concept list/tree
				TermRecommender.getInstance().clearList(context);
			}
			else if (command.equals("toggle")) {
				// toggles the the collapse state of list/tree
				TermRecommender.getInstance().toggleCollapse(context);
			}
			else if (command.equals("open")) {
				// opens the page for this concept
				// is handled by a link
			}
		}
		LinkToTermDefinitionProvider linkProvider = null;
		if (master == null) {
			linkProvider = new de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider();
		}
		else {
			linkProvider = new PackageCompileLinkToTermDefinitionProvider();
		}
		boolean abbreviationFlag = TermBrowserMarkup.getCurrentTermbrowserMarkupPrefixAbbreviationFlag(context);
		return RenderResult.unmask(new TermBrowserRenderer(context,
				linkProvider, master, abbreviationFlag).renderTermBrowser(), context);
	}


	/**
	 * 
	 * @created 10.10.2013
	 * @param term
	 * @return
	 */
	private Identifier createTermIdentifier(String term) {
		// TODO: caution: this will break if identifier names contain '#' !!
		String[] split = term.split("#");
		for (int i = 0; i < split.length; i++) {
			split[i] = Strings.unquote(split[i]);
		}
		return new Identifier(split);
	}
}
