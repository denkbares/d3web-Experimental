package de.knowwe.kdom.n3.render;

import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.n3.TurtleComplete;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolUtils;

public class TurtleN3Renderer extends KnowWEDomRenderer<TurtleComplete> {

	@Override
	public void render(KnowWEArticle article, Section<TurtleComplete> sec, UserContext user, StringBuilder string) {
		// Quickedit button
		string.append(KnowWEUtils.maskHTML("<pre id=\""
				+ sec.getID()
				+ "\" class=\"turtle-instantedit-pre\">"));
		string.append(KnowWEUtils.maskHTML("<div class=\"casetrain-instantedit\">"
				+ renderTools(article, sec, user)
				+ "</div>"));

		// string.append(KnowWEUtils.maskHTML("<pre>"));
		DelegateRenderer.getInstance().render(article, sec, user, string);
		string.append(KnowWEUtils.maskHTML("</pre>"));

	}

	public String renderTools(KnowWEArticle article, Section<?> sec, UserContext user) {

		StringBuilder string = new StringBuilder();

		Tool[] tools = ToolUtils.getTools(article, sec, user);

		for (Tool t : tools) {
			String icon = t.getIconPath();
			String jsAction = t.getJSAction();
			boolean hasIcon = icon != null && !icon.trim().isEmpty();

			string.append("<span class=\"" + t.getClass().getSimpleName() + "\" >"
					+ "<"
					+ (jsAction == null ? "span" : "a")
					+ " class=\"markupMenuItem\""
					+ (jsAction != null
							? " href=\"javascript:" + t.getJSAction() + ";undefined;\""
							: "") +
					" title=\"" + t.getDescription() + "\">" +
					(hasIcon ? ("<img src=\"" + icon + "\"></img>") : "") +
					"</" + (jsAction == null ? "span" : "a") + ">" +
					"</span>");
		}
		return string.toString();
	}

}
