package de.knowwe.onte.toolbar;

import de.knowwe.core.kdom.Article;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.frame.ClassFrame;
import de.knowwe.toolbar.DefaultToolbarButton;
import de.knowwe.toolbar.ToolbarButton;
import de.knowwe.toolbar.ToolbarButtonProvider;
import de.knowwe.tools.DefaultTool;

public class QueryToolbarButton implements ToolbarButtonProvider {

	@Override
	public ToolbarButton[] getButtons(Article article, UserContext userContext) {
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
		String image = "KnowWEExtension/images/onte/system-search.png";
		String name = "Query";
		String description = "Query the ontology with manchester class expressions";
		String action = "KNOWWE.plugin.onte.actions.showQueryTab()";

		return new DefaultToolbarButton(image, name, description, action);
	}
}
