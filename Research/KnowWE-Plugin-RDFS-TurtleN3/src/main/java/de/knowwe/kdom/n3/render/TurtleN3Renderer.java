package de.knowwe.kdom.n3.render;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolUtils;

public class TurtleN3Renderer implements Renderer {

	@Override
	public void render(Section<?> sec, UserContext user, StringBuilder string) {
		string.append(Strings.maskHTML("<pre id=\""
				+ sec.getID()
				+ "\" class=\"turtle-instantedit-pre\">"));
		string.append(Strings.maskHTML("<div class=\"casetrain-instantedit\">"));

		StringBuilder buffy = new StringBuilder();
		DelegateRenderer.getInstance().render(sec, user, buffy);

		String result = buffy.toString();
		result = result.replaceAll("\\[", "~[");
		result = result.replaceAll("\\]", "~]");
		string.append(result);

		string.append(Strings.maskHTML("</div>"));
		string.append(Strings.maskHTML("</pre>"));

	}

	public String renderTools(Section<?> sec, UserContext user) {

		StringBuilder string = new StringBuilder();

		Tool[] tools = ToolUtils.getTools(sec, user);

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
