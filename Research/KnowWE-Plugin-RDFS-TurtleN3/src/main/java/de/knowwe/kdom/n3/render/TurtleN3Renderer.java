package de.knowwe.kdom.n3.render;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolSet;
import de.knowwe.tools.ToolUtils;

public class TurtleN3Renderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, RenderResult string) {
		string.appendHtml("<pre id=\""
				+ sec.getID()
				+ "\" class=\"turtle-instantedit-pre\">");
		string.appendHtml("<div class=\"casetrain-instantedit\">");

		RenderResult buffy = new RenderResult(user);
		DelegateRenderer.getInstance().render(sec, user, buffy);

		String result = buffy.toStringRaw();
		result = result.replaceAll("\\[", "~[");
		result = result.replaceAll("\\]", "~]");
		string.append(result);

		string.appendHtml("</div>");
		string.appendHtml("</pre>");

	}

	public String renderTools(Section<?> sec, UserContext user) {

		StringBuilder string = new StringBuilder();

		ToolSet tools = ToolUtils.getTools(sec, user);

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
					(hasIcon ? ("<img src=\"" + icon + "\" />") : "") +
					"</" + (jsAction == null ? "span" : "a") + ">" +
					"</span>");
		}
		return string.toString();
	}

}
