package de.knowwe.onte.toolbar;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarButtonProvider;
import de.knowwe.tools.DefaultTool;

public class CheckOWL2ProfileToolbarButton implements ToolbarButtonProvider {

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
		// http://www.webont.org/owled/2011/presentations/OWLAPI_owled2011_tutorial.pdf

		String image = "KnowWEExtension/images/onte/system-run-4.png";
		String name = "Check Profile";
		String description = "Checks the OWL2 Profile of the local ontology";
		String action = "KNOWWE.plugin.onte.actions.showValidationTab()";

		return new DefaultToolbarButton(image, name, description, action);
	}
}
