package de.knowwe.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.core.Environment;
import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.ScopeUtils;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

public class KeywordCorrectionToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		List<CorrectionProvider.Suggestion> suggestions = new LinkedList<CorrectionProvider.Suggestion>();
		ResourceBundle wikiConfig = ResourceBundle.getBundle("KnowWE_config");

		int threshold = Integer.valueOf(wikiConfig.getString("knowweplugin.correction.threshold"));
		Article article = KnowWEUtils.getCompilingArticles(section).iterator().next();
		if (!section.hasErrorInSubtree(article)) {
			return new Tool[0];
		}

		for (CorrectionProvider c : getProviders(section)) {
			List<CorrectionProvider.Suggestion> s = c.getSuggestions(article, section, threshold);

			if (s != null) {
				suggestions.addAll(s);
			}
		}

		// Ensure there are no duplicates
		suggestions = new LinkedList<CorrectionProvider.Suggestion>(
				new HashSet<CorrectionProvider.Suggestion>(suggestions));

		// Sort by ascending distance
		Collections.sort(suggestions);

		if (suggestions.size() == 0) {
			return new Tool[0];
		}

		Tool[] tools = new Tool[suggestions.size() + 1];

		tools[0] = new DefaultTool(
				"KnowWEExtension/images/quickfix.gif",
				Environment.getInstance().getMessageBundle().getString("KnowWE.Correction.do"),
				Environment.getInstance().getMessageBundle().getString("KnowWE.Correction.do"),
				null,
				"correct"
				);

		for (int i = 0; i < suggestions.size(); i++) {
			tools[i + 1] = new DefaultTool(
					"KnowWEExtension/images/correction_change.gif",
					suggestions.get(i).getSuggestion(),
					"",
					"KNOWWE.plugin.onte.actions.doCorrection('" + section.getID() + "', '"
							+ suggestions.get(i).getSuggestion() + "');",
					"correct/item"
					);
		}
		return tools;
	}

	public static CorrectionProvider[] getProviders(Section<?> section) {
		Extension[] extensions = PluginManager.getInstance().getExtensions("KnowWEExtensionPoints",
				"CorrectionProvider");
		extensions = ScopeUtils.getMatchingExtensions(extensions, section);
		CorrectionProvider[] providers = new CorrectionProvider[extensions.length];

		for (int i = 0; i < extensions.length; i++) {
			Extension extension = extensions[i];
			providers[i] = (CorrectionProvider) extension.getSingleton();
		}

		return providers;
	}
}
