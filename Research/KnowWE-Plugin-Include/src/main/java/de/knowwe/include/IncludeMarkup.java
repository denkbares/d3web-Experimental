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

import com.denkbares.jspwiki.types.SectionType;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.wikiConnector.WikiConnector;
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

	public static String getSec;

	static {
		m = new DefaultMarkup("include");
		m.addAnnotation("target", true);
		m.addAnnotation("frame", false, "true", "false");
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
				getSec = targetArray[1];
			}

			frame = DefaultMarkupType.getAnnotation(section,
					"frame");

			Article article = Environment.getInstance()
					.getArticleManager(Environment.DEFAULT_WEB)
					.getArticle(targetKey);

			if (article == null) {
				StringBuilder builder = new StringBuilder();
				Message noSuchSection = new Message(Message.Type.WARNING, "Article not found!");
				Collection<Message> messages = new HashSet<Message>();
				messages.add(noSuchSection);
				DefaultMarkupRenderer.renderMessagesOfType(Message.Type.WARNING, messages,
						builder);
				new DefaultMarkupRenderer().renderDefaultMarkupStyled("include",
						builder.toString(),
						section.getID(), "", null, user,
						string);
			}
			else {

				Section<Article> renderarticle = article.getRootSection();

				if (targetArray.length > 1) {
					List<?> secList = Sections.findSuccessorsOfType(
							article.getRootSection(), SectionType.class);
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
						if (text.startsWith(getSec)) {
							renderarticle = (Section<Article>) listElement;
						}
					}
					if (renderarticle.equals(article.getRootSection())) {
						StringBuilder builder = new StringBuilder();
						Message noSuchSection = new Message(Message.Type.WARNING,
								"Section not found!");
						Collection<Message> messages = new HashSet<Message>();
						messages.add(noSuchSection);
						DefaultMarkupRenderer.renderMessagesOfType(Message.Type.WARNING, messages,
								builder);
						new DefaultMarkupRenderer().renderDefaultMarkupStyled("include",
								builder.toString(),
								section.getID(), "", null, user,
								string);
					}
					else {
						if (frame != null && frame.equals("true")) {
							WikiConnector wikiConnector = Environment.getInstance().getWikiConnector();
							String context = wikiConnector.getServletContext().getContextPath();
							String link = "<a href=\""
									+ context
									+ "/"
									+ KnowWEUtils.getURLLink(article.getRootSection().getArticle())
									+ "#section-"
									+
									article.getRootSection().getArticle().getTitle().replaceAll(
											"\\s",
											"+")
									+ "-"
									+ getSec.replaceAll("\\s", "") + "\">"
									+ targetKey + "</a>";

							Tool[] tools = new Tool[1];
							tools[0] = new DefaultTool(
									"",
									link,
									"Go to Page", "", "");

							StringBuilder builder = new StringBuilder();
							builder.append("\n");
							DelegateRenderer.getInstance().render(renderarticle,
									user, builder);
							new DefaultMarkupRenderer().renderDefaultMarkupStyled("include",
									builder.toString(),
									section.getID(), "", tools, user,
									string);
						}
						else {
							DelegateRenderer.getInstance().render(renderarticle,
									user, string);
						}
					}
				}
				else {
					if (frame != null && frame.equals("true")) {
						WikiConnector wikiConnector = Environment.getInstance().getWikiConnector();
						String context = wikiConnector.getServletContext().getContextPath();
						String link = "<a href=\"" + context + "/"
								+ KnowWEUtils.getURLLink(article.getRootSection().getArticle())
								+ "\">"
								+ targetKey + "</a>";

						Tool[] tools = new Tool[1];
						tools[0] = new DefaultTool(
								"",
								link,
								"Go to Page", "", "");

						StringBuilder builder = new StringBuilder();
						builder.append("\n");
						DelegateRenderer.getInstance().render(renderarticle,
								user, builder);
						new DefaultMarkupRenderer().renderDefaultMarkupStyled("include",
								builder.toString(),
								section.getID(), "", tools, user,
								string);
					}
					else {
						DelegateRenderer.getInstance().render(renderarticle,
								user, string);
					}
				}
			}
		}
	}
}