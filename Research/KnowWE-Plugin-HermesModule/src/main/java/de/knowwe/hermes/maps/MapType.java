/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ontoware.rdf2go.model.Statement;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.rendering.KnowWEDomRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.xml.AbstractXMLType;
import de.d3web.we.user.UserContext;
import de.knowwe.rdf2go.RDF2GoSubtreeHandler;
import de.knowwe.rdf2go.Rdf2GoCore;

public class MapType extends AbstractXMLType {

	private static final DecimalFormat format = new DecimalFormat("#.###");

	public MapType() {
		super("Map");
		this.addSubtreeHandler(new MapTypeOWLSubTreeHandler());
	}

	@Override
	public List<Type> getAllowedChildrenTypes() {
		childrenTypes.add(new AbstractXMLType("iframe"));
		this.setCustomRenderer(new MapRenderer());
		return childrenTypes;
	}

	private class MapTypeOWLSubTreeHandler extends RDF2GoSubtreeHandler<MapType> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<MapType> s) {
			List<Statement> ioo = new ArrayList<Statement>();
			String url = getIFrameSrcURL(s);
			KMLLoader kmlLoader = new KMLLoader(url);
			List<Placemark> placemarks = kmlLoader.getPlacemarks();
			for (Placemark placem : placemarks) {
				addPlacemarkToOwlObject(placem, ioo);
			}
			Rdf2GoCore.getInstance().addStatements(ioo, s);
			return null;
		}

	}

	private String getIFrameSrcURL(Section<?> sec) {
		Section<AbstractXMLType> iframeSection = Sections.findChildOfType(sec,
				AbstractXMLType.class);
		AbstractXMLType objectType = iframeSection.get();
		if (objectType.getXMLTagName() != "iframe") {
			// System.out.println("warning");
			return null;
		}
		Map<String, String> attributeMap = AbstractXMLType.getAttributeMapFor(iframeSection);
		String url = attributeMap.get("src");
		return url;
	}

	public static void addPlacemarkToOwlObject(Placemark placem, List<Statement> ioo) {

		URI conceptURI = Rdf2GoCore.getInstance().createlocalURI(placem.getTitle());

		Literal latitude = Rdf2GoCore.getInstance().createLiteral(
				format.format(placem.getLatitude()));
		Literal longitude = Rdf2GoCore.getInstance().createLiteral(
				format.format(placem.getLongitude()));

		/* adding all OWL statements to ioo object */
		// try {
		ArrayList<Statement> slist = new ArrayList<Statement>();
		slist.add(Rdf2GoCore.getInstance().createStatement(conceptURI,
				Rdf2GoCore.getInstance().createlocalURI("hasLatitude"), latitude));
		slist.add(Rdf2GoCore.getInstance().createStatement(conceptURI,
				Rdf2GoCore.getInstance().createlocalURI("hasLongitude"), longitude));
		ioo.addAll(slist);
		// }
		// catch (Repository e) {
		// e.printStackTrace();
		// }

	}

	private class MapRenderer extends KnowWEDomRenderer<MapType> {

		@Override
		public void render(KnowWEArticle article, Section<MapType> sec, UserContext user, StringBuilder string) {
			string.append("<div id=\"map\" class=\"panel\">");
			string.append("<h3>Karte</h3>");
			String originalText = sec.getOriginalText();
			int start = originalText.indexOf("<Map>");
			int end = originalText.indexOf("</Map>");
			string.append(originalText.substring(start + 5, end));

			// dirty
			string.append("</a>");

			string.append("</div>");
		}
	}
}