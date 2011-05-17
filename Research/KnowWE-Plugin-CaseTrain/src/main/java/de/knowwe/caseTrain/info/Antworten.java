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
package de.knowwe.caseTrain.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.subtreehandler.GeneralSubtreeHandler;
import de.knowwe.caseTrain.message.MissingComponentWarning;
import de.knowwe.caseTrain.renderer.DivStyleClassRenderer;
import de.knowwe.caseTrain.type.general.SubblockMarkup;


/**
 * Part of Info: Contains Antwort-Lines.
 * 
 * @author Jochen Reutelshoefer
 * @created 28.04.2011
 */
public class Antworten extends SubblockMarkup {

	private final String ANTWORT = "Antwort";

	public Antworten() {
		super("Antworten");
		PlainText plain = new PlainText();
		plain.setSectionFinder(new RegexSectionFinder("\\r?\\n"));
		this.addContentType(plain);
		this.addContentType(new Antwort());
		this.addContentType(new Praefix());
		this.addContentType(new Postfix());
		this.addContentType(new Ueberschrift());

		this.addSubtreeHandler(new GeneralSubtreeHandler<Frage>() {

			@Override
			public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<Frage> s) {

				List<KDOMReportMessage> messages = new ArrayList<KDOMReportMessage>(0);
				List<Section<Antwort>> found = new ArrayList<Section<Antwort>>();
				Sections.findSuccessorsOfType(s, Antwort.class, found);

				if (found.isEmpty()) {
					messages.add(new MissingComponentWarning(ANTWORT));
				}

				return messages;
			}
		});
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 09.05.2011
	 */
	public class Praefix extends AbstractType {
		public Praefix() {
			this.setSectionFinder(
					new RegexSectionFinder(AntwortenKorrektheitChecker.PRAEFIX + ":.*"));
			this.setCustomRenderer(new DivStyleClassRenderer("praefix"));
		}
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 09.05.2011
	 */
	public class Postfix extends AbstractType {
		public Postfix() {
			this.setSectionFinder(
					new RegexSectionFinder(AntwortenKorrektheitChecker.POSTFIX + ":.*"));
			this.setCustomRenderer(new DivStyleClassRenderer("postfix"));
		}
	}

	/**
	 * 
	 * @author Johannes Dienst
	 * @created 09.05.2011
	 */
	public class Ueberschrift extends AbstractType {
		public Ueberschrift() {
			this.setSectionFinder(
					new RegexSectionFinder(AntwortenKorrektheitChecker.UEBERSCHRIFT + ":.*"));
			this.setCustomRenderer(new DivStyleClassRenderer("ueberschrift"));
		}
	}
}
