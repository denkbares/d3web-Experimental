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
package de.knowwe.caseTrain.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.rendering.DelegateRenderer;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.LineSectionFinder;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.caseTrain.message.MissingAttributeError;
import de.knowwe.caseTrain.type.MetaLine.AttributeName;
import de.knowwe.caseTrain.type.general.BlockMarkupType;
import de.knowwe.caseTrain.util.Utils;

/**
 * 
 * One part of the case-markup-structure of the caseTrain-wiki-format
 * 
 * MetaDaten are rendered as a table with tow columns - attribute - value
 * 
 * @author Jochen
 * @created 06.04.2011
 */
public class MetaDaten extends BlockMarkupType {

	public static final String CASE_ID_KEY = "FALL_ID";

	public MetaDaten() {
		super("Metadaten");
		this.addContentType(new MetaLine());
		
		this.setCustomRenderer(new KnowWEDomRenderer<MetaLine>() {

			@Override
			public void render(KnowWEArticle article, Section<MetaLine> sec, UserContext user, StringBuilder string) {
				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMError.class), string);

				Utils.renderKDOMReportMessageBlock(KnowWEUtils.getMessagesFromSubtree(
						article,
						sec,
						KDOMWarning.class), string);
				string.append(KnowWEUtils.maskHTML("<table class='wikitable'>"));
				string.append(KnowWEUtils.maskHTML("<th>Metadaten:</th><th></th>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</table>"));
				
			}
		});
		this.addSubtreeHandler(new GeneralSubtreeHandler<MetaLine>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<MetaLine> s) {
				List<Section<AttributeName>> atts = new ArrayList<Section<AttributeName>>();
				Sections.findSuccessorsOfType(s, AttributeName.class, atts);
				boolean found = false;
				for (Section<AttributeName> section : atts) {
					if (section.getOriginalText().trim().startsWith(CASE_ID_KEY)) {
						found = true;
					}
				}

				if (found == false) {
					return Arrays.asList((KDOMReportMessage) new MissingAttributeError(
							CASE_ID_KEY));
				}
				return new ArrayList<KDOMReportMessage>(0);
			}
		});
	}

	@Override
	public String getCSSClass() {
		return "Metadaten";
	}

}

class MetaLine extends AbstractType {



	public MetaLine() {
		this.setSectionFinder(new LineSectionFinder());
		this.addChildType(new AttributeName());
		this.addChildType(new AttributeContent());

		this.setCustomRenderer(new KnowWEDomRenderer<MetaLine>() {

			@Override
			public void render(KnowWEArticle article, Section<MetaLine> sec, UserContext user, StringBuilder string) {

				string.append(KnowWEUtils.maskHTML("<tr>"));
				DelegateRenderer.getInstance().render(article, sec, user, string);
				string.append(KnowWEUtils.maskHTML("</tr>"));

			}
		});
		

	}

	class AttributeName extends AbstractType {

		public AttributeName() {
			this.setSectionFinder(new RegexSectionFinder(".*?:"));
			this.setCustomRenderer(new KnowWEDomRenderer<MetaLine>() {

				@Override
				public void render(KnowWEArticle article, Section<MetaLine> sec, UserContext user, StringBuilder string) {

					string.append(KnowWEUtils.maskHTML("<td>"));
					DelegateRenderer.getInstance().render(article, sec, user, string);
					string.append(KnowWEUtils.maskHTML("</td>"));

				}
			});
		}
	}

	class AttributeContent extends AbstractType {
		public AttributeContent() {
			this.setSectionFinder(new AllTextFinderTrimmed());
			this.setCustomRenderer(new KnowWEDomRenderer<MetaLine>() {

				@Override
				public void render(KnowWEArticle article, Section<MetaLine> sec, UserContext user, StringBuilder string) {

					string.append(KnowWEUtils.maskHTML("<td>"));
					DelegateRenderer.getInstance().render(article, sec, user, string);
					string.append(KnowWEUtils.maskHTML("</td>"));

				}
			});
		}
	}
}