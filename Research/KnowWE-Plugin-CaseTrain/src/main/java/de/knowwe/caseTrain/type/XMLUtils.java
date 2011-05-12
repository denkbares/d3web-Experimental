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
package de.knowwe.caseTrain.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.knowwe.caseTrain.type.MetaLine.AttributeContent;
import de.knowwe.caseTrain.type.MetaLine.AttributeName;
import de.knowwe.caseTrain.type.general.Bild;
import de.knowwe.caseTrain.type.general.Title;


/**
 * 
 * @author Johannes Dienst
 * @created 11.05.2011
 */
public class XMLUtils {
	public static void createXMLFromCase(KnowWEArticle article) {
		Section<KnowWEArticle> articleSec = article.getSection();
		Element root = new Element("StartElement");
		Document doc = new Document(root);

		// MetaData
		XMLUtils.addMetaDataElement(root, articleSec);

		// Einleitung
		XMLUtils.addIntroOutroElement(root, articleSec, "Intro");

		// Info Abschnitte
		XMLUtils.addSectionsElement(root, articleSec);

		// Abschluss
		XMLUtils.addIntroOutroElement(root, articleSec, "Extro");

	}

	/**
	 * Adds the Introduction/Outroduction Element to root.
	 * 
	 * TODO Getting the BlockMarkupContent is ugly!
	 * TODO Some MultimediaTypes are missing
	 * 
	 * @created 11.05.2011
	 * @param root
	 * @param articleSec
	 * @param elementName
	 */
	private static void addIntroOutroElement(Element root, Section<KnowWEArticle> articleSec, String elementName) {
		Element intro = new Element(elementName);

		Section<Einleitung> introSec = Sections.findSuccessor(articleSec, Einleitung.class);
		List<Section<?>> contentChildren = introSec.getChildren().get(0).getChildren();

		for (Section<?> sec : contentChildren) {
			if (sec.get() instanceof Title) {
				Element neu = new Element("Title");
				neu.addContent(sec.getOriginalText());
				intro.addContent(neu);
				continue;
			}
			if (sec.get() instanceof PlainText) {
				Element neu = new Element("Content");
				neu.addContent(sec.getOriginalText());
				intro.addContent(neu);
				continue;
			}
			if(sec.get() instanceof Bild) {
				Element neu = new Element("MultimediaItem");
				neu.setAttribute("type", "image");
				Element url = new Element("URL");
				url.addContent(sec.getOriginalText());
				neu.addContent(url);
				intro.addContent(neu);
				continue;
			}
		}

		root.addContent(intro);
	}

	/**
	 * 
	 * Adds all Info-Abschnitte to root.
	 * 
	 * TODO not finished.
	 * 
	 * @created 11.05.2011
	 * @param root
	 * @param articleSec
	 */
	private static void addSectionsElement(Element root, Section<KnowWEArticle> articleSec) {
		Element sections = new Element("Sections");


	}

	/**
	 * Adds the MetaData from the Wikipage to the root.
	 * 
	 * @created 11.05.2011
	 * @param root
	 * @param articleSec
	 */
	private static void addMetaDataElement(Element root, Section<KnowWEArticle> articleSec) {
		Element metaData = new Element("MetaData");

		Section<MetaDaten> meta = Sections.findSuccessor(articleSec, MetaDaten.class);
		if (meta == null) return;

		List<Section<MetaLine>> lines = new ArrayList<Section<MetaLine>>();
		Sections.findSuccessorsOfType(meta, MetaLine.class, lines);

		List<String> todos = new ArrayList<String>();
		HashMap<String, String> scoreAtts = new HashMap<String, String>();
		HashMap<String, String> miscAtts = new HashMap<String, String>();

		HashMap<String, String> attMap = MetaAttributes.getInstance().getAttributesForXMLMap();
		String attName = "";
		String attContent = "";
		for(Section<MetaLine> line : lines) {
			attName = Sections.findSuccessor(line, AttributeName.class).getOriginalText().trim();
			attContent = Sections.findSuccessor(line, AttributeContent.class).getOriginalText().trim();

			if (attName.equals(MetaAttributes.CASE_TODO)) {
				todos.add(attContent);
				continue;
			}

			// Score element
			if (attName.equals(MetaAttributes.CASE_POINTS) || attName.equals(MetaAttributes.CASE_PASS)
					|| attName.equals(MetaAttributes.TIME_LIMIT100) || attName.equals(MetaAttributes.TIME_LIMIT0)
					|| attName.equals(MetaAttributes.TIME_WEIGHT)) {
				scoreAtts.put(attMap.get(attName), attContent);
				continue;
			}

			// Misc
			if (attName.equals(MetaAttributes.FEEDBACK) || attName.equals(MetaAttributes.LANGUAGE)
					|| attName.equals(MetaAttributes.SHOW_TIME)) {
				miscAtts.put(attMap.get(attName), attContent);
				continue;
			}

			attName = attMap.get(attName);
			Element neu = new Element(attName);
			neu.addContent(attContent);
			metaData.addContent(neu);
		}

		// add Score-element with Attributes
		if (!scoreAtts.isEmpty()) {
			Element score = new Element("Score");
			for (Map.Entry<String,String> e : scoreAtts.entrySet()) {
				score.setAttribute(e.getKey(), e.getValue());
			}
			metaData.addContent(score);
		}

		// add Misc-element with Attributes
		if (!scoreAtts.isEmpty()) {
			Element misc = new Element("Misc");
			for (Map.Entry<String,String> e : miscAtts.entrySet()) {
				misc.setAttribute(e.getKey(), e.getValue());
			}
			metaData.addContent(misc);
		}

		// add Todos
		if (!todos.isEmpty()) {
			Element todosEl = new Element("Todos");
			for (String s : todos) {
				Element todo = new Element("Todo");
				todo.addContent(s);
				todosEl.addContent(todo);
			}
			metaData.addContent(todosEl);
		}

		root.addContent(metaData);
	}
}
