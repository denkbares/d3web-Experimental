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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.knowwe.caseTrain.info.Antwort;
import de.knowwe.caseTrain.info.Antwort.AntwortErklaerung;
import de.knowwe.caseTrain.info.Antwort.AntwortText;
import de.knowwe.caseTrain.info.Antworten;
import de.knowwe.caseTrain.info.Erklaerung;
import de.knowwe.caseTrain.info.Frage;
import de.knowwe.caseTrain.info.Frage.FrageGewicht;
import de.knowwe.caseTrain.info.Frage.FrageText;
import de.knowwe.caseTrain.info.Frage.FrageTyp;
import de.knowwe.caseTrain.info.Hinweis;
import de.knowwe.caseTrain.info.Info;
import de.knowwe.caseTrain.type.MetaLine.AttributeContent;
import de.knowwe.caseTrain.type.MetaLine.AttributeName;
import de.knowwe.caseTrain.type.general.Bild;
import de.knowwe.caseTrain.type.general.BlockMarkupContent;
import de.knowwe.caseTrain.type.general.BlockMarkupType;
import de.knowwe.caseTrain.type.general.MultimediaItem;
import de.knowwe.caseTrain.type.general.SubblockMarkup;
import de.knowwe.caseTrain.type.general.SubblockMarkupContent;
import de.knowwe.caseTrain.type.general.Title;
import de.knowwe.caseTrain.type.general.Video;


/**
 * 
 * XMLOutputParser for the CaseTrain-Markup.
 * 
 * @author Johannes Dienst
 * @created 11.05.2011
 */
public class XMLUtils {

	public static void createXMLFromCase(KnowWEArticle article) {
		Section<KnowWEArticle> articleSec = article.getSection();

		// TODO XML-Schema
		Element root = new Element("Case");
		Document doc = new Document(root);

		// MetaData
		XMLUtils.addMetaDataElement(root, articleSec);

		// Einleitung
		XMLUtils.addMixedMultimediaElement(root, articleSec, "Intro");

		// Info Abschnitte
		XMLUtils.addSectionsElement(root, articleSec);

		// Abschluss
		XMLUtils.addMixedMultimediaElement(root, articleSec, "Extro");

		XMLOutputter fmt = new XMLOutputter();
		fmt.setFormat( Format.getPrettyFormat() );
		try {
			fmt.output( doc, System.out );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Adds the Introduction/Outroduction Element to root.
	 * 
	 * @created 11.05.2011
	 * @param root
	 * @param sec
	 * @param elementName
	 */
	private static void addMixedMultimediaElement(Element root, Section<?> sec, String elementName) {
		Element intro = new Element(elementName);

		Section<?> introSec = null;

		if (elementName.equals("Intro"))
			introSec = Sections.findSuccessor(sec, Einleitung.class);
		if (elementName.equals("Extro"))
			introSec = Sections.findSuccessor(sec, Abschluss.class);

		if (introSec == null) return;
		XMLUtils.addmmmixedcontent(intro, introSec);

		root.addContent(intro);
	}

	/**
	 * 
	 * 
	 * @created 12.05.2011
	 * @param intro
	 * @param introSec
	 */
	@SuppressWarnings("unchecked")
	private static void addmmmixedcontent(Element intro, Section<?> introSec) {
		List<Section<?>> contentChildren = null;

		if (introSec.get().isAssignableFromType(BlockMarkupType.class)) {
			Section<BlockMarkupContent> s =
				Sections.findSuccessor(introSec, BlockMarkupContent.class);
			contentChildren = s.getChildren();
		}

		if (introSec.get().isAssignableFromType(SubblockMarkup.class)) {
			Section<SubblockMarkupContent> s =
				Sections.findSuccessor(introSec, SubblockMarkupContent.class);
			contentChildren = s.getChildren();
		}

		for (Section<?> sec : contentChildren) {
			if (sec.get().isType(Title.class)) {
				Element neu = new Element("Title");
				neu.addContent(sec.getOriginalText());
				intro.addContent(neu);
				continue;
			}
			if (sec.get().isType(PlainText.class)) {
				XMLUtils.addContentElement(intro, sec);
				continue;
			}

			if(sec.get().isAssignableFromType(MultimediaItem.class)) {
				intro.addContent(XMLUtils.createMultimediaElement(sec));
				continue;
			}
		}
	}

	/**
	 * 
	 * Adds all Info-Abschnitte to root.
	 * 
	 * TODO only supports SimpleSections
	 * TODO InfoWahl-Sections
	 * 
	 * @created 11.05.2011
	 * @param root
	 * @param articleSec
	 */
	private static void addSectionsElement(Element root, Section<KnowWEArticle> articleSec) {
		Element sections = new Element("Sections");

		List<Section<Info>> infoSecs = new ArrayList<Section<Info>>();
		Sections.findSuccessorsOfType(articleSec, Info.class, infoSecs);

		for(Section<Info> infoSec : infoSecs) {
			List<Section<?>> childs = infoSec.getChildren().get(0).getChildren();
			Element simple = new Element("SimpleSection");

			List<Section<?>> frageChilds = new ArrayList<Section<?>>();
			Element questions = new Element("Questions");

			for (Section<?> child : childs) {

				// First PlainText+Some Multimedia
				if (child.get().isType(PlainText.class)) {
					XMLUtils.addContentElement(simple, child);
					continue;
				}

				if(child.get().isAssignableFromType(MultimediaItem.class)) {
					simple.addContent(XMLUtils.createMultimediaElement(child));
					continue;
				}

				if (child.get().isType(Frage.class)) {
					if (!frageChilds.isEmpty()) {
						XMLUtils.addQuestionsElement(questions, frageChilds);
					}
					frageChilds.clear();
					frageChilds.add(child);
					continue;
				}
				if ( (child.get().isType(Hinweis.class))
						|| (child.get().isType(Antworten.class))
						|| (child.get().isType(Erklaerung.class)) ) {
					frageChilds.add(child);
				}

				if(child.get().isType(Title.class)) {
					Element neu = new Element("Title");
					neu.addContent(child.getOriginalText());
					simple.addContent(neu);
					continue;
				}

			}

			if (!frageChilds.isEmpty())
				XMLUtils.addQuestionsElement(questions, frageChilds);
			simple.addContent(questions);
			sections.addContent(simple);
		}

		root.addContent(sections);
	}

	/**
	 * Creates the Question-Element from the list children
	 * Possible children for a question are:
	 * FrageText/Info/Feedback/Antworten
	 * 
	 * @created 12.05.2011
	 * @param questionss
	 * @param frageChilds.
	 */
	private static void addQuestionsElement(Element questions, List<Section<?>> frageChilds) {

		Section<?> frage = frageChilds.remove(0);
		Section<?> fragetyp = Sections.findSuccessor(frage, FrageTyp.class);
		Section<?> frageGewicht = Sections.findSuccessor(frage, FrageGewicht.class);
		Section<?> frageText = Sections.findSuccessor(frage, FrageText.class);
		Element question = new Element(fragetyp.getOriginalText() + "Question");

		String weight = frageGewicht.getOriginalText().trim();
		question.setAttribute("weight", weight);
		Element text = new Element("Text");
		text.addContent(frageText.getOriginalText());
		question.addContent(text);

		for (Section<?> sec : frageChilds) {
			// Hints koennen immer kommen und element Info
			// beinhalten Text und Multimedia
			if (sec.get().isType(Hinweis.class)) {
				XMLUtils.renderHinweis(question, sec);
				continue;
			}

			if(sec.get().isType(Antworten.class)) {
				XMLUtils.addAntwortenElement(question, sec);
				continue;
			}

			// Feedback is Erklaerung
			if (sec.get().isType(Erklaerung.class)) {
				Element i = new Element("Feedback");
				XMLUtils.addmmmixedcontent(i, sec);
				question.addContent(i);
				continue;
			}
		}

		questions.addContent(question);
	}

	/**
	 * 
	 * @created 12.05.2011
	 * @param question
	 * @param sec
	 */
	@SuppressWarnings("unchecked")
	private static void addAntwortenElement(Element question, Section<?> sec) {
		Element ants = new Element("Answers");

		for (Section<?> s : sec.getChildren().get(0).getChildren()) {
			if (s.get().isType(PlainText.class)) continue;
			Element answer = new Element("Answer");

			// PosFactor and NegFactor
			String posFactor = Antwort.getPosFactor((Section<Antwort>)s);
			answer.setAttribute("posFactor", posFactor);
			String negFactor = Antwort.getNegFactor((Section<Antwort>)s);
			if (negFactor != null)
				answer.setAttribute("negFactor", negFactor);

			Section<AntwortText> text = Sections.findSuccessor(s, AntwortText.class);
			Element t = new Element("Text");
			t.addContent(text.getOriginalText());
			answer.addContent(t);

			//SimpleFeedback
			Section<AntwortErklaerung> erklaerung = Sections.findSuccessor(s, AntwortErklaerung.class);
			if (erklaerung != null) {
				Element t1 = new Element("SimpleFeedback");
				t1.addContent(text.getOriginalText());
				answer.addContent(t1);
			}

			ants.addContent(answer);
		}

		question.addContent(ants);
	}

	/**
	 * Appends a Hinweis/Info
	 * 
	 * @created 12.05.2011
	 * @param frage
	 * @param infoSec
	 */
	private static void renderHinweis(Element frage, Section<?> infoSec) {
		Element info = new Element("Info");

		for (Section<?> sec : infoSec.getChildren().get(0).getChildren()) {
			if (sec.get().isType(PlainText.class)) {
				Element neu = new Element("Content");
				neu.addContent(sec.getOriginalText());
				info.addContent(neu);
				continue;
			}
			if(sec.get().isAssignableFromType(MultimediaItem.class)) {
				info.addContent(XMLUtils.createMultimediaElement(sec));
				continue;
			}
		}

		frage.addContent(info);
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
		if (!miscAtts.isEmpty()) {
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

	/**
	 * 
	 * Creates a Element of the given MultimediaSection.
	 * 
	 * @created 15.05.2011
	 * @param child
	 * @return
	 */
	private static Element createMultimediaElement(Section<?> child) {
		String type = "";
		if(child.get().isType(Bild.class)) type = "image";
		if(child.get().isType(Video.class)) type = "video";

		Element neu = new Element("MultimediaItem");
		neu.setAttribute("type", type);
		Element url = new Element("URL");
		url.addContent(child.getChildren().get(1).getOriginalText().trim());
		neu.addContent(url);
		return neu;
	}

	/**
	 * 
	 * @created 15.05.2011
	 * @param simple
	 * @param child
	 */
	private static void addContentElement(Element simple, Section<?> child) {
		Element neu = new Element("Content");
		String te = child.getOriginalText().replaceAll("[\\r\\n]", "");
		if (te.equals("")) return;
		neu.addContent(te);
		simple.addContent(neu);
	}
}
