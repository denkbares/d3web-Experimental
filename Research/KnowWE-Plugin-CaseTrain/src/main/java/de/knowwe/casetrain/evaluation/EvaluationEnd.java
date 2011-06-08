/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.casetrain.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.casetrain.message.MissingContentWarning;
import de.knowwe.casetrain.type.Closure;
import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.SubblockMarkupContent;
import de.knowwe.casetrain.type.general.Title;


/**
 * 
 * @author Johannes Dienst
 * @created 20.05.2011
 */
public class EvaluationEnd extends SubblockMarkup {

	public EvaluationEnd() {
		super("EvaluationEnd");
		this.addContentType(new Title());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Closure>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Closure> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);

				//				Section<Title> title = Sections.findSuccessor(s, Title.class);
				//				if (title == null) {
				//					messages.add(new MissingTitleError(Title.TITLE));
				//				}

				Section<SubblockMarkupContent> plain =
					Sections.findSuccessor(s, SubblockMarkupContent.class);
				if (plain.getOriginalText() == null || plain.getOriginalText().trim().equals("")) {
					messages.add(new MissingContentWarning(EvaluationEnd.class.getSimpleName()));
				}

				return messages;
			}
		});
	}

	@Override
	public String getCSSClass() {
		return "Ie";
	}

}
