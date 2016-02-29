/*
 * Copyright (C) 2013 denkbares GmbH
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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.collections.DefaultMultiMap;
import de.d3web.collections.MultiMap;
import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.d3web.utils.Log;
import de.knowwe.core.Environment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.Scope;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 04.06.2013
 */
public class DragDropEditActionManager extends AbstractAction {

	public static final String DRAG_DROP_INSERTER = "DragDropInserter";

	/**
	 * Captures the information which DragDropInserter to be called for a section of a given type.
	 */
	private MultiMap<DragDropEditInserter, Scope> inserters = new DefaultMultiMap<>();

	/**
	 * 
	 */
	public DragDropEditActionManager() {
		Extension[] extensions = PluginManager.getInstance().getExtensions(
				"KnowWE-Plugin-TermBrowser", DRAG_DROP_INSERTER);
		for (Extension extension : extensions) {
			Object inserterInstance = extension.getNewInstance();
			List<String> scopeValues = extension.getParameters("scope");
			if (inserterInstance instanceof DragDropEditInserter) {
				for (String string : scopeValues) {
					inserters.put((DragDropEditInserter) inserterInstance, Scope.getScope(string));
				}
			}
		}
	}

	@Override
	public void execute(UserActionContext context) throws IOException {
		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/plain; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	/**
	 * 
	 * @created 04.06.2013
	 * @param context
	 * @return
	 */
	private String perform(UserActionContext context) throws IOException {
		String title = context.getTitle();
		boolean mayEdit = Environment.getInstance().getWikiConnector().userCanEditArticle(title,
				context.getRequest());
		if (mayEdit) {
			String termname = context.getParameter("termname");
			String targetIDString = context.getParameter("targetID");
			Section<?> section = Sections.get(targetIDString);

			if (section == null) {
				String message = "Section not found: " + targetIDString;
				Log.warning(message);
				return message;
			}

			for (DragDropEditInserter inserter : this.inserters.keySet()) {
				Collection<Scope> scopes = this.inserters.getValues(inserter);
				for (Scope scope : scopes) {
					List<Section<?>> match = scope.getMatchingSuccessors(section);
					if (match.contains(section)) {
						return inserter.insert(section, termname, null, context);
					}
				}
			}
		}
		else {
			return "You are not allowed to edit this page.";
		}
		return null;
	}

}
