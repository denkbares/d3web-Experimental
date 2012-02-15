/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.casetrain.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.util.Utils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.KnowWEDomRenderer;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.subtreehandler.GeneralSubtreeHandler;

/**
 * 
 * One part of the case-markup-structure of the casetrain-wiki-format
 * 
 * MetaDaten are rendered as a table with tow columns - attribute - value
 * Allowed attributes are specified in {@link MetaAttributes}
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class MetaData extends BlockMarkupType {

	private final String META = "MetaDaten";

	public MetaData() {
		super("Metadaten");

		AnonymousType at = new AnonymousType("LineBreak");
		at.setSectionFinder(new RegexSectionFinder("\\r?\\n"));
		this.addContentType(at);

		this.addContentType(new MetaLine());

		this.setRenderer(new KnowWEDomRenderer<MetaData>() {

			@SuppressWarnings("unchecked")
			@Override
			public void render(KnowWEArticle article, Section<MetaData> sec, UserContext user, StringBuilder string) {
				Utils.renderKDOMReportMessageBlock(
						Messages.getErrors(Messages.getMessagesFromSubtree(
								article, sec)), string);

				Utils.renderKDOMReportMessageBlock(
						Messages.getWarnings(Messages.getMessagesFromSubtree(
								article, sec)), string);

				Utils.renderKDOMReportMessageBlock(
						Messages.getNotices(Messages.getMessagesFromSubtree(
								article, sec)), string);

				string.append(KnowWEUtils.maskHTML("%%collapsebox-closed \r\n"));
				string.append(
						KnowWEUtils.maskHTML("! "
								+ ResourceBundle.getBundle("casetrain_messages")
										.getString(META)
								+ "\r\n"));

				// Quickedit button
				string.append(KnowWEUtils.maskHTML("<pre id=\""
						+ sec.getID()
						+ "\" class=\"casetrain-instantedit-pre\">"));
				string.append(KnowWEUtils.maskHTML("<div class=\"casetrain-instantedit\">"
						// + getFrameName(sec)
						// + getEditorIcon(sec)
						+ Utils.renderTools(article, sec, user)
						// + getLink(sec)
						+ "</div>"));

				string.append(KnowWEUtils.maskHTML("<table class='wikitable'>"));
				string.append(KnowWEUtils.maskHTML("<th>Metadaten:</th><th></th>"));

				Section<BlockMarkupContent> con =
						Sections.findSuccessor(sec, BlockMarkupContent.class);
				List<Section<MetaLine>> lines = new ArrayList<Section<MetaLine>>();
				Sections.findSuccessorsOfType(con, MetaLine.class, lines);

				for (Section<MetaLine> l : lines) {
					l.get().getRenderer().render(article, l, user, string);
				}

				string.append(KnowWEUtils.maskHTML("</table>"));
				string.append(KnowWEUtils.maskHTML("/%\r\n"));

				string.append(KnowWEUtils.maskHTML("</pre>"));
			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<MetaData>() {

			@Override
			public Collection<Message> create(KnowWEArticle article, Section<MetaData> s) {

				List<Message> messages = new ArrayList<Message>(0);
				Section<Info> infoSection = Sections.findSuccessor(s.getArticle().getSection(),
						Info.class);
				if (infoSection == null) {
					messages.add(
							Utils.missingComponentError(
									Info.class.getSimpleName()));
				}

				List<Section<AttributeName>> atts = new ArrayList<Section<AttributeName>>();
				Sections.findSuccessorsOfType(s, AttributeName.class, atts);
				messages.addAll(MetaAttributes.getInstance().compareAttributeList(atts));
				return messages;
			}
		});
	}

	@Override
	public String getCSSClass() {
		return "Metadaten";
	}

}
