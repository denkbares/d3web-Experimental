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
package de.knowwe.hermes.maps;

import java.util.ArrayList;
import java.util.Collection;

import org.ontoware.rdf2go.model.Statement;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.report.Message;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.RDF2GoSubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;

public class LocationDefinitionType extends AbstractType {

	private static final String START_TAG = "<<ORT:";
	private static final String END_TAG = ">>";

	public LocationDefinitionType() {
		sectionFinder = new RegexSectionFinder(START_TAG + "[\\w|\\W]*?" + END_TAG);
		this.setRenderer(LocationRenderer.getInstance());
		this.addSubtreeHandler(new LocationDefinitionTypeOWLSubTreeHandler());
	}

	private class LocationDefinitionTypeOWLSubTreeHandler extends RDF2GoSubtreeHandler<LocationDefinitionType> {

		@Override
		public Collection<Message> create(Article article, Section<LocationDefinitionType> s) {
			ArrayList<Statement> ioo = new ArrayList<Statement>();
			Placemark placem = extractPlacemark(s);
			MapType.addPlacemarkToOwlObject(placem, ioo);
			Rdf2GoCore.getInstance().addStatements(ioo, s);
			return null;
		}

	}

	private static Placemark extractPlacemark(Section<?> section) {
		String sectionText = section.getText();
		sectionText = sectionText.substring(START_TAG.length(),
				sectionText.length() - END_TAG.length());

		String locationName = null;
		Double latitude = null;
		Double longitude = null;
		String description = null;
		if (sectionText == null) {
			return null;
		}
		String[] splittedSecText = sectionText.split(";");

		// location name omitted
		if (splittedSecText.length == 2) {
			locationName = section.getTitle();
			try {
				latitude = Double.parseDouble(splittedSecText[0]);
				longitude = Double.parseDouble(splittedSecText[1]);
			}
			catch (NumberFormatException e) {
				return null;
			}
		}
		if (splittedSecText.length > 2 && splittedSecText.length < 5) {
			locationName = splittedSecText[0].trim();
			try {
				latitude = Double.parseDouble(splittedSecText[1]);
				longitude = Double.parseDouble(splittedSecText[2]);
			}
			catch (NumberFormatException e) {
				return null;
			}
			if (splittedSecText.length == 4) {
				description = splittedSecText[3];
			}
		}

		if (locationName == null || latitude == null || longitude == null) {
			return null;
		}

		return new Placemark(locationName, latitude, longitude, description);
	}

	public static class LocationRenderer implements Renderer {

		private static LocationRenderer instance;

		public static LocationRenderer getInstance() {
			if (instance == null) {
				instance = new LocationRenderer();
			}
			return instance;
		}

		@Override
		public void render(Section<?> sec, UserContext user, StringBuilder string) {
			String originalText = sec.getText();
			Placemark extractPlacemark = extractPlacemark(sec);
			if (extractPlacemark == null) {
				string.append(Strings.maskHTML("<span class='error' title='invalid syntax'>")
						+ originalText + Strings.maskHTML("</span>"));
			}
			else {
				String htmlString = extractPlacemark.toHTMLString();
				string.append(Strings.maskHTML(htmlString));
			}
		}
	}

}
