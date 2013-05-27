package de.knowwe.ophtovisD3;

import de.knowwe.core.append.PageAppendHandler;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;


public class HierarchyViewAppendHandler implements PageAppendHandler {

	@Override
	public void append(String web, String topic, UserContext user, RenderResult result) {
		if(DataBaseHelper.conceptIsInHierachy(topic)){
		result.appendHtml("<div class=infobox>");
		result.appendHtml("<div class=\"Colapser\" onclick=\"collapseInfobox()\" >Infobox  ▲</div>");
		result.appendHtml("<div id=\"chart\"></div>");
		result.appendHtml("<script> createSidebarTree(\"" + topic + "\")</script>");
		}
		result.appendHtml("<script> var url =KNOWWE.core.util.getURL({action : 'ConnectionsAction', concept : \"" + topic + "\"});");
		result.appendHtml("renderConnections(url); </script>");
		result.appendHtml("</Div>");
	}

	@Override
	public boolean isPre() {
		// TODO Auto-generated method stub
		return true;
	}

}
