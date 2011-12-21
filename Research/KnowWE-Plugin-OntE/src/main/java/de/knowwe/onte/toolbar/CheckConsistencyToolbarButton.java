package de.knowwe.onte.toolbar;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarButtonProvider;
import de.knowwe.tools.DefaultTool;

public class CheckConsistencyToolbarButton implements ToolbarButtonProvider {

	@Override
	public ToolbarButton[] getButtons(KnowWEArticle article, UserContext userContext) {
		return new ToolbarButton[] { getConsistencyToolbarButton() };
	}

	/**
	 * Returns the {@link DefaultTool} for the creation of new
	 * {@link ClassFrame} out of unknown terms.
	 *
	 * @created 06.10.2011
	 * @param section
	 * @return
	 */
	private ToolbarButton getConsistencyToolbarButton() {
		String image = "KnowWEExtension/images/onte/utilities-system-monitor.png";
		String name = "Check Consistency";
		String description = "Checks the consistency of the local ontology";
		String action = "KNOWWE.plugin.onte.actions.checkConsistency()";

		return new DefaultToolbarButton(image, name, description, action);
	}
}
