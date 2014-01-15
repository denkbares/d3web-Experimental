package de.knowwe.wisskont.refactoring;

import java.util.Map;

import de.d3web.strings.Strings;
import de.knowwe.core.Attributes;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

public class AddListsHandler extends AbstractTagHandler {

	public AddListsHandler() {
		super("refactorLists");
	}

	@Override
	public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
		getButtonHTML(userContext, result);
	}

	public static void getButtonHTML(UserContext userContext, RenderResult html) {

		String description = "Überschriften refaktorisieren";
		String jsAction = "window.location='action/AddListsScript" +
				"?" + Attributes.TOPIC + "=" + userContext.getTitle() +
				"&amp;" + Attributes.USER + "=" + userContext.getUserName() +
				"&amp;" + Attributes.WEB + "=" + Environment.DEFAULT_WEB + "'";

		html.appendHtml("<a href=\"javascript:");
		html.appendHtml(jsAction);
		html.appendHtml(";void(0);\" ");
		html.appendHtml("\" title=\"");
		html.appendHtml(Strings.encodeHtml(description));
		html.appendHtml("\" class=\"onte-button left small\">");
		html.appendHtml("<img src=\"KnowWEExtension/images/dt_icon_realisation2.gif\" style=\"");
		html.appendHtml("background: url('").appendHtml("KnowWEExtension/images/disk.png").appendHtml(
				"') no-repeat scroll center 6px transparent; height: 16px;width: 16px;");
		html.appendHtml("\" /></a>");

	}

}
