package de.knowwe.ophtovisD3;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public class InfoboxMarkup extends DefaultMarkupType {

	public InfoboxMarkup(DefaultMarkup markup) {
		super(markup);
		this.setRenderer(new InfoboxRenderer());
	}

	private static DefaultMarkup m = null;

	static {
		m = new DefaultMarkup("infobox");

	}

	public InfoboxMarkup() {
		super(m);
		this.setRenderer(new InfoboxRenderer());
	}

	private class InfoboxRenderer implements Renderer {

		@Override
		public void render(Section<?> section, UserContext user, RenderResult string) {
		
			String topic = user.getTitle();
			if(DataBaseHelper.conceptIsInHierachy(topic)){
				string.appendHtml("<div class=infobox>");
				string.appendHtml("<div class=\"colapser\" onclick=\"collapseInfobox()\" >Infobox  ▲</div>");
				string.appendHtml("<div id=\"chart\"></div>");
				string.appendHtml("</Div>");
				string.appendHtml("<script> createSidebarTree(\"" + topic + "\")</script>");
			
			}
		
				
		}

	}

}