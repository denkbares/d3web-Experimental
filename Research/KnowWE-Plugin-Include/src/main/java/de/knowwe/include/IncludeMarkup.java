package de.knowwe.include;

/*
 * Copyright (C) 2012 denkbares GmbH
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.Strings;
import de.knowwe.core.wikiConnector.WikiConnector;
import de.knowwe.jspwiki.types.HeaderType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;

/**
 * 
 * @author Benedikt Kaemmerer
 * @created 06.07.2012
 */

public class IncludeMarkup extends DefaultMarkupType {

	private static DefaultMarkup m = null;

	public static final String MARKUP_NAME = "include";

	public static String target;

	public static String targetKey;

	public static String frame;

	public static String zoom;

	public static String subSectionKey;

	static {
		m = new DefaultMarkup("include");
		m.addAnnotation("target", true);
		m.addAnnotation("frame", false, "true", "false");
		m.addAnnotation("zoom", false);
	}

	public IncludeMarkup() {
		super(m);
		this.setRenderer(new IncludeRenderer());
		setIgnorePackageCompile(true);
	}

	static class IncludeRenderer implements Renderer {

		@SuppressWarnings("unchecked")
		@Override
		public void render(Section<?> section, UserContext user,
				StringBuilder string) {

			target = DefaultMarkupType.getAnnotation(section,
					"target");
			String[] targetArray = target.split("#");
			targetKey = targetArray[0];
			if (targetArray.length > 1) {
				subSectionKey = targetArray[1];
			}

			frame = DefaultMarkupType.getAnnotation(section,
					"frame");

			zoom = DefaultMarkupType.getAnnotation(section,
					"zoom");

			Article article = Environment.getInstance()
					.getArticleManager(Environment.DEFAULT_WEB)
					.getArticle(targetKey);
			// warning if article not found
			if (article == null) {
				renderWarning(user, section, string, "Article '" + targetKey + "' not found!");
			}
			else {
				// render article
				Section<RootType> renderarticle = article.getRootSection();

				if (targetArray.length > 1) {
					List<?> secList = Sections.findSuccessorsOfType(
							article.getRootSection(), HeaderType.class);
					Iterator<?> listIterator = secList.iterator();
					while (listIterator.hasNext()) {
						Section<?> listElement = (Section<?>) listIterator.next();
						String text = listElement.getText();
						int start = 0;
						while (text.startsWith("!")) {
							start++;
							text = text.substring(start);
						}
						text = text.trim();
						if (text.startsWith(subSectionKey)) {
							// renderarticle for single section
							renderarticle = (Section<RootType>) listElement;
						}
					}
					// warning if section not found
					if (renderarticle.equals(article.getRootSection())) {
						renderWarning(user, section, string, "Section '" + subSectionKey
								+ "' not found!");
					}
					else {
						if (frame != null && frame.equals("false")) {
							// render section, no frame
							renderNoFrame(user, renderarticle, string);
						}
						else {
							// render section, with frame
							renderFrame(article, user, section, renderarticle, string, true);
						}
					}
				}
				else {
					if (frame != null && frame.equals("false")) {
						// render whole article, no frame
						renderNoFrame(user, renderarticle, string);
					}
					else {
						// render whole article, with frame
						renderFrame(article, user, section, renderarticle, string, false);
					}
				}
			}
		}

		public void renderFrame(Article article, UserContext user, Section<?> section, Section<?> renderarticle, StringBuilder string, Boolean rendersec) {

			WikiConnector wikiConnector = Environment.getInstance().getWikiConnector();
			String context = wikiConnector.getServletContext().getContextPath();
			String link;
			if (rendersec) {
				// link for section
				link = "<a href=\""
						+ context
						+ "/"
						+ KnowWEUtils.getURLLink(article.getRootSection().getArticle())
						+ "#section-"
						+
						article.getRootSection().getArticle().getTitle().replaceAll(
								"\\s",
								"+")
						+ "-"
						+ subSectionKey.replaceAll("\\s", "") + "\">"
						+ targetKey + "</a>";
			}
			else {
				// link for article
				link = "<a href=\"" + context + "/"
						+ KnowWEUtils.getURLLink(article.getRootSection().getArticle())
						+ "\">"
						+ targetKey + "</a>";
			}

			Tool[] tools = new Tool[1];
			tools[0] = new DefaultTool(
					"",
					link,
					"Go to Page", "", "");

			StringBuilder builder = new StringBuilder();
			if (zoom != null) {
				builder.append(Strings.maskHTML("<div style=\"zoom: " + zoom + "%\">"));
			}
			builder.append("\n");
			DelegateRenderer.getInstance().render(renderarticle,
					user, builder);
			if (zoom != null) {
				builder.append(Strings.maskHTML("</div>"));
			}
			new DefaultMarkupRenderer().renderDefaultMarkupStyled("include",
					builder.toString(),
					section.getID(), "", tools, user,
					string);

		}

		public void renderNoFrame(UserContext user, Section<?> renderarticle, StringBuilder string) {
			if (zoom != null) {
				string.append(Strings.maskHTML("<div style=\"zoom: " + zoom + "%\">"));
			}
			string.append("\n");
			DelegateRenderer.getInstance().render(renderarticle,
					user, string);
			if (zoom != null) {
				string.append(Strings.maskHTML("</div>"));
			}
		}

		public void renderWarning(UserContext user, Section<?> section, StringBuilder string, String warning) {
			StringBuilder builder = new StringBuilder();
			Message noSuchSection = new Message(Message.Type.WARNING,
					warning);
			Collection<Message> messages = new HashSet<Message>();
			messages.add(noSuchSection);
			DefaultMarkupRenderer.renderMessagesOfType(Message.Type.WARNING, messages,
					builder);
			new DefaultMarkupRenderer().renderDefaultMarkupStyled("include",
					builder.toString(),
					section.getID(), "", null, user,
					string);
		}
	}
}