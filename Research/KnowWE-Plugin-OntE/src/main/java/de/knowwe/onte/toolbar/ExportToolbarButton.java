package de.knowwe.onte.toolbar;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarButtonProvider;
import de.knowwe.tools.DefaultTool;

public class ExportToolbarButton implements ToolbarButtonProvider {

	@Override
	public ToolbarButton[] getButtons(KnowWEArticle article, UserContext userContext) {
		return new ToolbarButton[] { getToolbarButton() };
	}

	/**
	 * Returns the {@link DefaultTool} for the creation of new
	 * {@link ClassFrame} out of unknown terms.
	 *
	 * @created 06.10.2011
	 * @param section
	 * @return
	 */
	private ToolbarButton getToolbarButton() {
		String image = "KnowWEExtension/images/onte/emblem-downloads.png";
		String name = "Export";
		String description = "Exports the local ontology";
		String action = "KNOWWE.plugin.onte.actions.showExportTab()";

		return new DefaultToolbarButton(image, name, description, action);
	}
}
