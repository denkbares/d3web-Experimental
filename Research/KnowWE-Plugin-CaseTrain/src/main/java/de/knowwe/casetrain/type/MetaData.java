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

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.message.MissingComponentError;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.util.Utils;

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
		this.addContentType(new MetaLine());

		this.setCustomRenderer(new KnowWEDomRenderer<MetaData>() {

			@Override
			public void render(KnowWEArticle article, Section<MetaData> sec, UserContext user, StringBuilder string) {
				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMError.class), string);

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMWarning.class), string);

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMNotice.class), string);

				string.append(KnowWEUtils.maskHTML("%%collapsebox-closed \r\n"));
				string.append(
						KnowWEUtils.maskHTML("! "
								+ META
								+ "\r\n"));
				string.append(KnowWEUtils.maskHTML("<table class='wikitable'>"));
				string.append(KnowWEUtils.maskHTML("<th>Metadaten:</th><th></th>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</table>"));
				string.append(KnowWEUtils.maskHTML("/%\r\n"));
			}
		});

		this.addSubtreeHandler(new GeneralSubtreeHandler<MetaData>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<MetaData> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);
				Section<Info> infoSection = Sections.findSuccessor(s.getArticle().getSection(),
						Info.class);
				if (infoSection == null) {
					messages.add(
							new MissingComponentError(
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
