package de.knowwe.onte.toolbar;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarButtonProvider;
import de.knowwe.tools.DefaultTool;
import de.knowwe.util.Icon;

public class ImportToolbarButton implements ToolbarButtonProvider {

	@Override
	public ToolbarButton[] getButtons(Article article, UserContext userContext) {
		return new ToolbarButton[] { getToolbarButton() };
	}

	/**
	 * Returns the {@link DefaultTool} for the creation of new
	 * {@link ClassFrame} out of unknown terms.
	 *
	 * @param section
	 * @return
	 * @created 06.10.2011
	 */
	private ToolbarButton getToolbarButton() {
		String name = "Import";
		String description = "Shows an overview of imported ontologies and lets you import additional ones";
		String action = "KNOWWE.plugin.onte.actions.showImportTab()";

		return new DefaultToolbarButton(Icon.IMPORT, name, description, action);
	}
}
