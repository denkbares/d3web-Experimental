package de.knowwe.onte.toolbar;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarButtonProvider;
import de.knowwe.tools.DefaultTool;

public class UndefinedTermsToolbarButton implements ToolbarButtonProvider {

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
		String image = "KnowWEExtension/images/onte/tools-report-bug.png";
		String name = "Undefined Terms";
		String description = "Shows a list of currently undefined terms";
		String action = "KNOWWE.plugin.onte.actions.showUndefinedTermsTab()";

		return new DefaultToolbarButton(image, name, description, action);
	}
}
