package de.knowwe.d3web.scoreTable.renderer;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWERenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;

public class TableCellRenderer implements KnowWERenderer {

	@Override
	public void render(Section section, UserContext user,
			StringBuilder string) {

		string.append(KnowWEUtils.maskHTML("<td style=\"white-space: normal\">"));
		DelegateRenderer.getInstance().render(section, user, string);
		string.append(KnowWEUtils.maskHTML("</td>"));

	}

}
