package de.knowwe.util;

import java.util.List;
import java.util.Set;

import de.knowwe.core.correction.CorrectionProvider.Suggestion;
import de.knowwe.core.correction.CorrectionToolProvider;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;
import de.knowwe.tools.ToolUtils;

public class KeywordCorrectionToolProvider implements ToolProvider {

	@Override
	public boolean hasTools(Section<?> section, UserContext userContext) {
		if (!hasErrors(section)) return false;
		Set<Suggestion> suggestions = CorrectionToolProvider.getSuggestions(section, 1);
		return !suggestions.isEmpty();
	}

	private boolean hasErrors(Section<?> section) {
		if (section.hasErrorInSubtree()) return true;

		return false;
	}

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {
		if (!hasErrors(section)) {
			return ToolUtils.emptyToolArray();
		}

		List<Suggestion> suggestions = CorrectionToolProvider.getSuggestions(section);
		if (suggestions.isEmpty()) {
			return ToolUtils.emptyToolArray();
		}

		Tool[] tools = new Tool[suggestions.size() + 1];

		tools[0] = new DefaultTool(
				Icon.LIGHTBULB,
				Messages.getMessageBundle().getString("KnowWE.Correction.do"),
				"",
				null,
				"correct"
		);

		for (int i = 0; i < suggestions.size(); i++) {
			tools[i + 1] = new DefaultTool(
					Icon.SHARE,
					suggestions.get(i).getSuggestion(),
					"",
					"KNOWWE.plugin.onte.actions.doCorrection('" + section.getID() + "', '"
							+ suggestions.get(i).getSuggestion() + "');",
					"correct/item"
			);
		}
		return tools;
	}

}
