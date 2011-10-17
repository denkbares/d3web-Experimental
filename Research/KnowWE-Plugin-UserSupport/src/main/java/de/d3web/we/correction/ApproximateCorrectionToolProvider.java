/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.correction;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.d3web.we.algorithm.Suggestion;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.ScopeUtils;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;


/**
 * 
 * @author Johannes Dienst
 * @created 15.09.2011
 */
public class ApproximateCorrectionToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(KnowWEArticle article, Section<?> section, UserContext userContext) {
		List<Suggestion> suggestions = new LinkedList<Suggestion>();
		ResourceBundle wikiConfig = ResourceBundle.getBundle("KnowWE_config");

		//		int threshold = Integer.valueOf(wikiConfig.getString("knowweplugin.correction.threshold"));
		// Set this rather high. Just for Testing
		int threshold = 5;


		for (ApproximateCorrectionProvider c : getProviders(section)) {
			List<Suggestion> s = c.getSuggestions(article, section, threshold);

			if (s != null) {
				suggestions.addAll(s);
			}
		}

		// Ensure there are no duplicates
		suggestions = new LinkedList<Suggestion>(
				new HashSet<Suggestion>(suggestions));

		// Sort by ascending distance
		Collections.sort(suggestions);

		if (suggestions.size() == 0) {
			return new Tool[0];
		}

		Tool[] tools = new Tool[suggestions.size() + 1];

		tools[0] = new DefaultTool(
				"KnowWEExtension/images/quickfix.gif",
				KnowWEEnvironment.getInstance().getKwikiBundle().getString("KnowWE.Correction.do"),
				KnowWEEnvironment.getInstance().getKwikiBundle().getString("KnowWE.Correction.do"),
				null,
				"correct"
				);

		for (int i = 0; i < suggestions.size(); i++) {
			tools[i + 1] = new DefaultTool(
					"KnowWEExtension/images/correction_change.gif",
					suggestions.get(i).getSuggestion(),
					"",
					"KNOWWE.plugin.correction.doCorrection('" + section.getID() + "', '"
							+ suggestions.get(i).getSuggestion() + "');",
							"correct/item"
					);
		}

		return tools;
	}

	private static ApproximateCorrectionProvider[] getProviders(Section<?> section) {
		Extension[] extensions = PluginManager.getInstance().getExtensions("KnowWEExtensionPoints",
				"ApproximateCorrectionProvider");
		extensions = ScopeUtils.getMatchingExtensions(extensions, section);
		ApproximateCorrectionProvider[] providers = new ApproximateCorrectionProvider[extensions.length];

		for (int i = 0; i < extensions.length; i++) {
			Extension extension = extensions[i];
			providers[i] = (ApproximateCorrectionProvider) extension.getSingleton();
		}

		return providers;
	}

}
