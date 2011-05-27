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
package de.knowwe.casetrain.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import de.casetrain.binding.LoadSave;
import de.casetrain.binding.traincase.jaxb.BasicQuestion;
import de.casetrain.binding.traincase.jaxb.BasicSection.Questions;
import de.casetrain.binding.traincase.jaxb.BasicSection.Questions.MNumQuestion;
import de.casetrain.binding.traincase.jaxb.BasicSection.Questions.NumQuestion;
import de.casetrain.binding.traincase.jaxb.BasicSection.Questions.TextQuestion;
import de.casetrain.binding.traincase.jaxb.BasicSection.Questions.WordQuestion;
import de.casetrain.binding.traincase.jaxb.Case;
import de.casetrain.binding.traincase.jaxb.Case.Evaluation;
import de.casetrain.binding.traincase.jaxb.Case.Evaluation.EvaluationEnd;
import de.casetrain.binding.traincase.jaxb.Case.Evaluation.EvaluationSections;
import de.casetrain.binding.traincase.jaxb.Case.Extro;
import de.casetrain.binding.traincase.jaxb.Case.Metadata;
import de.casetrain.binding.traincase.jaxb.Case.Metadata.Misc;
import de.casetrain.binding.traincase.jaxb.Case.Metadata.Score;
import de.casetrain.binding.traincase.jaxb.Case.Metadata.Todos;
import de.casetrain.binding.traincase.jaxb.ChoiceAnswer;
import de.casetrain.binding.traincase.jaxb.ChoiceQuestion;
import de.casetrain.binding.traincase.jaxb.ChoiceQuestion.Answers;
import de.casetrain.binding.traincase.jaxb.Mmcontent;
import de.casetrain.binding.traincase.jaxb.Mmitem;
import de.casetrain.binding.traincase.jaxb.Mmmixedcontent;
import de.casetrain.binding.traincase.jaxb.MultiWordQuestion;
import de.casetrain.binding.traincase.jaxb.NumAnswers;
import de.casetrain.binding.traincase.jaxb.NumAnswers.NumAnswer;
import de.casetrain.binding.traincase.jaxb.NumAnswers.NumAnswerInterval;
import de.casetrain.binding.traincase.jaxb.ObjectFactory;
import de.casetrain.binding.traincase.jaxb.SimpleSection;
import de.casetrain.binding.traincase.jaxb.Titledmmcontent;
import de.casetrain.binding.traincase.jaxb.WordAnswers;
import de.casetrain.binding.traincase.jaxb.WordAnswers.WordAnswer;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.knowwe.casetrain.info.AnswerLine;
import de.knowwe.casetrain.info.AnswerLine.AnswerExplanation;
import de.knowwe.casetrain.info.AnswerLine.AnswerText;
import de.knowwe.casetrain.info.AnswerLine.AnswerTextArgument;
import de.knowwe.casetrain.info.AnswersBlock;
import de.knowwe.casetrain.info.AnswerValidator;
import de.knowwe.casetrain.info.Explanation;
import de.knowwe.casetrain.info.Question;
import de.knowwe.casetrain.info.Question.QuestionWeight;
import de.knowwe.casetrain.info.Question.QuestionText;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.info.Hint;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.type.Closure;
import de.knowwe.casetrain.type.AttributeContent;
import de.knowwe.casetrain.type.AttributeName;
import de.knowwe.casetrain.type.Introduction;
import de.knowwe.casetrain.type.MetaAttributes;
import de.knowwe.casetrain.type.MetaData;
import de.knowwe.casetrain.type.MetaLine;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.SubblockMarkupContent;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.MultimediaItem;
import de.knowwe.casetrain.type.multimedia.Video;


/**
 * 
 * XMLOutputParser for the casetrain-Markup.
 * 
 * @author Johannes Dienst
 * @created 11.05.2011
 */
public class XMLUtils {

	public static void createXMLWithBindings(KnowWEArticle article) {
		Section<KnowWEArticle> articleSec = article.getSection();

		ObjectFactory fac = new ObjectFactory();
		Case c = fac.createCase();

		// TODO how to get the webapp path?
		try {
			String path = KnowWEEnvironment.getInstance().
			getKnowWEExtensionPath().replaceAll("/KnowWEExtension", "");
			LoadSave.initialise(path);
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
		}


		// MetaData
		XMLUtils.addMetaDataWithBinding(c, articleSec, fac);

		// Einleitung
		XMLUtils.addTitledMMWithBinding(c, articleSec, fac, "Intro");

		// Info-Abschnitte
		XMLUtils.addSectionsWithBinding(c, articleSec, fac);

		// Abschluss
		XMLUtils.addTitledMMWithBinding(c, articleSec, fac, "Extro");

		// TODO Evaluation
		XMLUtils.addEvaluation(c, articleSec, fac);

		String webapp = KnowWEEnvironment.getInstance().getKnowWEExtensionPath();
		try {
			File f = new File(webapp+"/tmp/case.xml");
			f.createNewFile();
			FileOutputStream stream = new FileOutputStream(f);
			stream.flush();
			LoadSave.saveCase(c, stream);
			stream.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @created 19.05.2011
	 * @param c
	 * @param articleSec
	 * @param fac
	 */
	private static void addEvaluation(Case c, Section<KnowWEArticle> articleSec, ObjectFactory fac) {
		Evaluation evo = fac.createCaseEvaluation();
		EvaluationSections evoSecs = fac.createCaseEvaluationEvaluationSections();

		List<Section<de.knowwe.casetrain.evaluation.Evaluation>> found =
			new ArrayList<Section<de.knowwe.casetrain.evaluation.Evaluation>>();
		Sections.findSuccessorsOfType(articleSec, de.knowwe.casetrain.evaluation.Evaluation.class, found);

		for(Section<de.knowwe.casetrain.evaluation.Evaluation> infoSec : found) {
			List<Section<?>> childs = infoSec.getChildren().get(0).getChildren();
			SimpleSection simpleSec = fac.createSimpleSection();
			simpleSec.setQuestions(fac.createBasicSectionQuestions());

			List<Section<?>> frageChilds = new ArrayList<Section<?>>();

			for (Section<?> child : childs) {

				// First PlainText+Some Multimedia
				if (child.get().isType(PlainText.class)) {
					String te = XMLUtils.clearPlainText(child);
					if (!te.equals("")) {
						simpleSec.getContentOrMultimediaItemOrFormula().add(te);
					}
					continue;
				}
				if(child.get().isAssignableFromType(MultimediaItem.class)) {
					Mmitem it = fac.createMmitem();
					XMLUtils.configureMmitem(it, child);
					simpleSec.getContentOrMultimediaItemOrFormula().add(it);
					continue;
				}

				if (child.get().isType(Question.class)) {
					if (!frageChilds.isEmpty()) {
						XMLUtils.addQuestionsWithBinding(simpleSec, frageChilds, fac);
					}
					frageChilds.clear();
					frageChilds.add(child);
					continue;
				}
				if ( (child.get().isType(Hint.class))
						|| (child.get().isType(AnswersBlock.class))
						|| (child.get().isType(Explanation.class)) ) {
					frageChilds.add(child);
				}

				if(child.get().isType(Title.class)) {
					simpleSec.setTitle(XMLUtils.clearPlainText(child).trim());
					continue;
				}

			}

			if (!frageChilds.isEmpty())
				XMLUtils.addQuestionsWithBinding(simpleSec, frageChilds, fac);

			// Add the EvaluationEnd
			EvaluationEnd end = fac.createCaseEvaluationEvaluationEnd();

			Section<de.knowwe.casetrain.evaluation.EvaluationEnd> evoEnd =
				Sections.findSuccessor(infoSec, de.knowwe.casetrain.evaluation.EvaluationEnd.class);

			if (evoEnd == null) {
				end.setTitle("Evaluationende");
				end.getContentOrMultimediaItemOrFormula().
				add("Wir danken Ihnen für Ihre Mitarbeit!");
			}

			if (evoEnd != null) {
				Section<Title> tit = Sections.findSuccessor(evoEnd, Title.class);
				Section<PlainText> con = Sections.findSuccessor(evoEnd, PlainText.class);
				end.setTitle(XMLUtils.clearPlainText(tit));
				end.getContentOrMultimediaItemOrFormula().add(XMLUtils.clearPlainText(con));
			}

			evo.setEvaluationEnd(end);

			evoSecs.getSimpleSection().add(simpleSec);
		}

		if (evoSecs.getSimpleSection().isEmpty()) return;

		evo.setEvaluationSections(evoSecs);

		c.setEvaluation(evo);
	}

	private static void addTitledMMWithBinding(Case c, Section<KnowWEArticle> sec,
			ObjectFactory fac ,String elementName) {

		Section<?> introSec = null;

		if (elementName.equals("Intro"))
			introSec = Sections.findSuccessor(sec, Introduction.class);
		if (elementName.equals("Extro"))
			introSec = Sections.findSuccessor(sec, Closure.class);

		if (introSec == null) return;
		XMLUtils.addTitledmmmixedcontentWithBinding(c, introSec, fac);
	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param c
	 * @param introSec
	 */
	private static void addTitledmmmixedcontentWithBinding(Case c, Section<?> introSec, ObjectFactory fac) {

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

		Titledmmcontent titledmmContent = fac.createTitledmmcontent();

		for (Section<?> sec : contentChildren) {
			if (sec.get().isType(Title.class)) {
				titledmmContent.setTitle(XMLUtils.clearPlainText(sec).trim());
				continue;
			}

			if (sec.get().isType(PlainText.class)) {
				String te = XMLUtils.clearPlainText(sec);
				if (!te.equals(""))
					titledmmContent.getContentOrMultimediaItemOrFormula().add(te);
				continue;
			}

			if(sec.get().isAssignableFromType(MultimediaItem.class)) {
				Mmitem it = fac.createMmitem();
				XMLUtils.configureMmitem(it, sec);
				titledmmContent.getContentOrMultimediaItemOrFormula().add(it);
				continue;
			}
		}

		if (introSec.get().isType(Introduction.class))
			c.setIntro(titledmmContent);

		if (introSec.get().isType(Closure.class)) {
			Extro ex = fac.createCaseExtro();
			ex.setTitle(titledmmContent.getTitle());
			ex.getContentOrMultimediaItemOrFormula().
			addAll(titledmmContent.getContentOrMultimediaItemOrFormula());
			c.setExtro(ex);
		}

	}

	private static void configureMmitem(Mmitem it, Section<?> sec) {
		String type = "";
		if(sec.get().isType(Image.class)) type = "image";
		if(sec.get().isType(Video.class)) type = "video";
		//		if(child.get().isType(Link.class)) type = "link";
		//		if(child.get().isType(Audio.class)) type = "audio";

		it.setType(type);
		it.setURL(sec.getChildren().get(1).getOriginalText().trim());
	}

	/**
	 * 
	 * 
	 * @created 16.05.2011
	 * @param c
	 * @param articleSec
	 */
	private static void addMetaDataWithBinding(
			Case c, Section<KnowWEArticle> articleSec, ObjectFactory fac) {
		Metadata metaObj = fac.createCaseMetadata();

		Section<MetaData> meta = Sections.findSuccessor(articleSec, MetaData.class);
		if (meta == null) return;

		List<Section<MetaLine>> lines = new ArrayList<Section<MetaLine>>();
		Sections.findSuccessorsOfType(meta, MetaLine.class, lines);

		List<String> todos = new ArrayList<String>();
		HashMap<String, String> scoreAtts = new HashMap<String, String>();
		HashMap<String, String> miscAtts = new HashMap<String, String>();

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
				scoreAtts.put(attName, attContent);
				continue;
			}

			// Misc
			if (attName.equals(MetaAttributes.FEEDBACK) || attName.equals(MetaAttributes.LANGUAGE)
					|| attName.equals(MetaAttributes.SHOW_TIME)) {
				miscAtts.put(attName, attContent);
				continue;
			}

			if (attName.equals(MetaAttributes.CASE_ID_KEY))
				metaObj.setID(attContent);
			if (attName.equals(MetaAttributes.CASE_AUTHOR))
				metaObj.setAuthor(attContent);
			if (attName.equals(MetaAttributes.CASE_DATE))
				metaObj.setDate(attContent);
			if (attName.equals(MetaAttributes.CASE_TITLE))
				metaObj.setTitle(attContent);
			if (attName.equals(MetaAttributes.CASE_VERSION))
				metaObj.setVersion(attContent);
			if (attName.equals(MetaAttributes.DURATION_MIN))
				metaObj.setDurationMinutes(new Long(attContent));
			if (attName.equals(MetaAttributes.DIFFICULTY))
				metaObj.setDifficulty(attContent);
			if (attName.equals(MetaAttributes.REQUIREMENTS))
				metaObj.setPrereqs(attContent);
			if (attName.equals(MetaAttributes.HINT))
				metaObj.setNotice(attContent);
			if (attName.equals(MetaAttributes.COMMENT))
				metaObj.setComment(attContent);
			if (attName.equals(MetaAttributes.KEYWORDS))
				metaObj.setKeywords(attContent);
			if (attName.equals(MetaAttributes.CASE_HISTORY))
				metaObj.setHistory(attContent);

			// TODO Background and setURL?
			//			if (attName.equals(MetaAttributes.BACKGROUND))
			//				metaObj.setURL(arg0)(attContent);
			//			if (attName.equals(MetaAttributes.HW_LINKTEXT))
			//				metaObj.setHistory(attContent);
		}

		// add Score-element with Attributes
		if (!scoreAtts.isEmpty()) {
			Score score = fac.createCaseMetadataScore();
			for (Map.Entry<String,String> e : scoreAtts.entrySet()) {

				if (e.getKey().equals(MetaAttributes.TIME_LIMIT0))
					score.setMaxTimeSeconds(new Long(e.getValue()));

				if (e.getKey().equals(MetaAttributes.CASE_PASS))
					score.setMinScoreForSuccess(new BigDecimal(e.getValue()));

				if (e.getKey().equals(MetaAttributes.TIME_LIMIT100))
					score.setOkTimeSeconds(new Long(e.getValue()));

				if (e.getKey().equals(MetaAttributes.CASE_POINTS))
					score.setWeight(new BigDecimal(e.getValue()));

				if (e.getKey().equals(MetaAttributes.TIME_WEIGHT))
					score.setWeightTime(new BigDecimal(e.getValue()));
			}
			metaObj.setScore(score);
		}

		// add Misc-element with Attributes
		// TODO colorSchema missing / Set show clock is ugly
		if (!miscAtts.isEmpty()) {
			Misc misc = fac.createCaseMetadataMisc();
			for (Map.Entry<String,String> e : miscAtts.entrySet()) {

				if (e.getKey().equals(MetaAttributes.FEEDBACK))
					misc.setFeedback(e.getValue());

				if (e.getKey().equals(MetaAttributes.LANGUAGE))
					misc.setLanguage(e.getValue());

				if (e.getKey().equals(MetaAttributes.SHOW_TIME))
					misc.setShowClock(new Boolean(e.getValue()));
			}
			metaObj.setMisc(misc);
		}

		// add Todos
		if (!todos.isEmpty()) {
			Todos ts = fac.createCaseMetadataTodos();
			for (String s : todos) {
				ts.getTodo().add(s);
			}
			metaObj.setTodos(ts);
		}

		c.setMetadata(metaObj);
	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param c
	 * @param articleSec
	 * @param fac
	 */
	private static void addSectionsWithBinding(Case c, Section<KnowWEArticle> articleSec,
			ObjectFactory fac) {

		List<Section<Info>> infoSecs = new ArrayList<Section<Info>>();
		Sections.findSuccessorsOfType(articleSec, Info.class, infoSecs);

		for(Section<Info> infoSec : infoSecs) {
			List<Section<?>> childs = infoSec.getChildren().get(0).getChildren();
			SimpleSection simpleSec = fac.createSimpleSection();
			simpleSec.setQuestions(fac.createBasicSectionQuestions());

			List<Section<?>> frageChilds = new ArrayList<Section<?>>();

			for (Section<?> child : childs) {

				// First PlainText+Some Multimedia
				if (child.get().isType(PlainText.class)) {
					String te = XMLUtils.clearPlainText(child);
					if (!te.equals("")) {
						simpleSec.getContentOrMultimediaItemOrFormula().add(te);
					}
					continue;
				}
				if(child.get().isAssignableFromType(MultimediaItem.class)) {
					Mmitem it = fac.createMmitem();
					XMLUtils.configureMmitem(it, child);
					simpleSec.getContentOrMultimediaItemOrFormula().add(it);
					continue;
				}

				if (child.get().isType(Question.class)) {
					if (!frageChilds.isEmpty()) {
						XMLUtils.addQuestionsWithBinding(simpleSec, frageChilds, fac);
					}
					frageChilds.clear();
					frageChilds.add(child);
					continue;
				}
				if ( (child.get().isType(Hint.class))
						|| (child.get().isType(AnswersBlock.class))
						|| (child.get().isType(Explanation.class)) ) {
					frageChilds.add(child);
				}

				if(child.get().isType(Title.class)) {
					simpleSec.setTitle(XMLUtils.clearPlainText(child).trim());
					continue;
				}

			}

			if (!frageChilds.isEmpty())
				XMLUtils.addQuestionsWithBinding(simpleSec, frageChilds, fac);
			c.setSections(fac.createCaseSections());
			c.getSections().getSimpleSectionOrInfoSection().add(simpleSec);
		}

	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param simpleSec
	 * @param frageChilds
	 */
	private static void addQuestionsWithBinding(
			SimpleSection simpleSec, List<Section<?>> frageChilds, ObjectFactory fac) {
		Questions questions = simpleSec.getQuestions();
		List<JAXBElement<? extends BasicQuestion>> questionsList = questions.getOCQuestionOrMCQuestionOrHLMMCQuestion();

		Section<?> frage = frageChilds.get(0);
		Section<?> fragetyp = Sections.findSuccessor(frage, QuestionType.class);
		String typ = fragetyp.getOriginalText().trim();

		if (typ.equals(AnswerValidator.OC)) {
			ChoiceQuestion q = fac.createChoiceQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsOCQuestion(q));
		}

		if (typ.equals(AnswerValidator.MC)) {
			ChoiceQuestion q = fac.createChoiceQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsMCQuestion(q));
		}

		if (typ.equals(AnswerValidator.W)) {
			WordQuestion q = fac.createBasicSectionQuestionsWordQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.add(fac.createBasicSectionQuestionsWordQuestion(q));
		}

		if (typ.equals(AnswerValidator.UMW)) {
			MultiWordQuestion q = fac.createMultiWordQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsUMWordQuestion(q));
		}

		if (typ.equals(AnswerValidator.OMW)) {
			MultiWordQuestion q = fac.createMultiWordQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsOMWordQuestion(q));
		}

		if (typ.equals(AnswerValidator.N)) {
			NumQuestion q = fac.createBasicSectionQuestionsNumQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsNumQuestion(q));
		}

		if (typ.equals(AnswerValidator.MN)) {
			MNumQuestion q = fac.createBasicSectionQuestionsMNumQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsMNumQuestion(q));
		}

		if (typ.equals(AnswerValidator.T)) {
			TextQuestion q = fac.createBasicSectionQuestionsTextQuestion();
			XMLUtils.createQuestionWithBinding(frageChilds, q, fac);
			questionsList.
			add(fac.createBasicSectionQuestionsTextQuestion(q));
		}

	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param frage
	 * @param fac
	 * @return
	 */
	private static void createQuestionWithBinding(
			List<Section<?>> frageChilds, BasicQuestion question, ObjectFactory fac) {

		Section<?> frage = frageChilds.remove(0);
		//		Section<?> fragetyp = Sections.findSuccessor(frage, FrageTyp.class);
		Section<?> frageGewicht = Sections.findSuccessor(frage, QuestionWeight.class);
		Section<?> frageText = Sections.findSuccessor(frage, QuestionText.class);

		String weight = frageGewicht.getOriginalText().trim();
		question.setWeight(new BigDecimal(weight));
		question.setText(frageText.getOriginalText());

		for (Section<?> sec : frageChilds) {
			// Hints koennen immer kommen und element Info
			// beinhalten Text und Multimedia
			if (sec.get().isType(Hint.class)) {
				Mmmixedcontent it = fac.createMmmixedcontent();
				XMLUtils.renderHinweisOrErklaerungWithBinding(it, sec, fac);
				question.setInfo(it);
				continue;
			}

			if(sec.get().isType(AnswersBlock.class)) {
				XMLUtils.addAntwortenWithBinding(question, sec, fac);
				continue;
			}

			// Feedback is Erklaerung
			if (sec.get().isType(Explanation.class)) {
				Mmcontent it = fac.createMmcontent();
				XMLUtils.renderHinweisOrErklaerungWithBinding(it, sec, fac);
				question.setFeedback(it);
				continue;
			}
		}
	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param question
	 * @param sec
	 * @param clazz
	 * @param fac
	 */
	@SuppressWarnings("unchecked")
	private static void addAntwortenWithBinding(BasicQuestion question, Section<?> antworten, ObjectFactory fac) {

		List<AnswerAttributeStore> ants = new ArrayList<AnswerAttributeStore>();
		String postfix = null;
		String praefix = null;
		String ueberschrift = null;
		for (Section<?> s : antworten.getChildren().get(0).getChildren()) {
			if (s.get().isType(PlainText.class)) continue;

			// PosFactor and NegFactor
			String posFactor = AnswerLine.getPosFactor((Section<AnswerLine>)s);
			String negFactor = AnswerLine.getNegFactor((Section<AnswerLine>)s);

			// AntwortTex
			Section<AnswerText> text = Sections.findSuccessor(s, AnswerText.class);
			String antwortText = "";
			if (text != null)
				antwortText = text.getOriginalText();

			// AntwortTextArgument
			String textArgString = null;
			Section<AnswerTextArgument> textArg = Sections.findSuccessor(s, AnswerTextArgument.class);
			if (textArg != null)
				textArgString = textArg.getOriginalText().trim();

			//SimpleFeedback
			Section<AnswerExplanation> erklaerung = Sections.findSuccessor(s, AnswerExplanation.class);
			String erkl = null;
			if (erklaerung != null) {
				erkl = erklaerung.getOriginalText().
				substring(1, erklaerung.getOriginalText().length()-1);
			}

			// Postfix Praefix Ueberschrift
			Section<AnswersBlock.Postfix> post = Sections.findSuccessor(s, AnswersBlock.Postfix.class);
			if (post != null)
				postfix = post.getOriginalText().trim();

			Section<AnswersBlock.Praefix> prae = Sections.findSuccessor(s, AnswersBlock.Praefix.class);
			if (prae != null)
				praefix =prae.getOriginalText().trim();

			Section<AnswersBlock.Heading> ueber = Sections.findSuccessor(s, AnswersBlock.Heading.class);
			if (ueber != null)
				ueberschrift = ueber.getOriginalText().trim();

			ants.add(new AnswerAttributeStore(posFactor, negFactor,
					antwortText, erkl, textArgString));
		}


		if (question instanceof ChoiceQuestion) {
			ChoiceQuestion qu = (ChoiceQuestion) question;
			Answers ans = fac.createChoiceQuestionAnswers();
			for (AnswerAttributeStore store : ants) {
				ChoiceAnswer a = fac.createChoiceAnswer();
				if (store.getNegFactor() != null)
					a.setNegFactor(new BigDecimal(store.getNegFactor()));
				a.setPosFactor(new BigDecimal(store.getPosFactor()));
				if (store.getSimplefeedback() != null)
					a.setSimpleFeedback(store.getSimplefeedback());
				a.setText(store.getText());
				ans.getAnswer().add(a);
			}
			qu.setAnswers(ans);
		}

		if (question instanceof WordQuestion) {
			WordQuestion qu = (WordQuestion) question;
			WordAnswers ans = fac.createWordAnswers();
			for (AnswerAttributeStore store : ants) {
				WordAnswer a = fac.createWordAnswersWordAnswer();
				a.setEditDistance(new Long(AnswersBlock.getEditDistance(store.getTextArgument())));
				a.setIsRegularExpression(AnswersBlock.getIsRegularExpression(store.getTextArgument()));
				if (store.getNegFactor() != null)
					a.setNegFactor(new BigDecimal(store.getNegFactor()));
				a.setPosFactor(new BigDecimal(store.getPosFactor()));
				if (store.getSimplefeedback() != null)
					a.setSimpleFeedback(store.getSimplefeedback());
				a.setText(store.getText());
				ans.getWordAnswer().add(a);
			}
			qu.setWordAnswers(ans);
		}

		if (question instanceof MultiWordQuestion) {
			MultiWordQuestion qu = (MultiWordQuestion) question;
			WordAnswers ans = fac.createWordAnswers();
			for (AnswerAttributeStore store : ants) {
				WordAnswer a = fac.createWordAnswersWordAnswer();
				a.setEditDistance(new Long(AnswersBlock.getEditDistance(store.getTextArgument())));
				a.setIsRegularExpression(AnswersBlock.getIsRegularExpression(store.getTextArgument()));
				if (store.getNegFactor() != null)
					a.setNegFactor(new BigDecimal(store.getNegFactor()));
				a.setPosFactor(new BigDecimal(store.getPosFactor()));
				if (store.getSimplefeedback() != null)
					a.setSimpleFeedback(store.getSimplefeedback());
				a.setText(store.getText());
				ans.getWordAnswer().add(a);
			}
			qu.getWordAnswers().add(ans);
		}

		if (question instanceof NumQuestion) {
			NumQuestion q = (NumQuestion) question;
			NumAnswers ans = fac.createNumAnswers();
			for (AnswerAttributeStore store : ants) {
				XMLUtils.addNumAnswerInstance(ans, store, fac);
			}
			XMLUtils.addPraePostUeberschrift(ans, ueberschrift, postfix, praefix);
			q.setNumAnswers(ans);
		}

		if (question instanceof MNumQuestion) {
			MNumQuestion q = (MNumQuestion) question;
			NumAnswers ans = fac.createNumAnswers();
			for (AnswerAttributeStore store : ants) {
				XMLUtils.addNumAnswerInstance(ans, store, fac);
			}
			XMLUtils.addPraePostUeberschrift(ans, ueberschrift, postfix, praefix);
			q.getNumAnswers().add(ans);
		}

		if (question instanceof TextQuestion) {
			TextQuestion q = (TextQuestion) question;
			for (AnswerAttributeStore store : ants) {
				q.setSolution(store.getText());
			}
		}

	}

	/**
	 * 
	 * Adds Praefix/Postfix/Ueberschrift to NumAnswerObject.
	 * They are not added if they are null.
	 * 
	 * @created 18.05.2011
	 * @param ans
	 * @param ueberschrift
	 * @param postfix
	 * @param praefix
	 */
	private static void addPraePostUeberschrift(NumAnswers ans, String ueberschrift, String postfix, String praefix) {
		if (ueberschrift != null)
			ans.setAnswerCaption(ueberschrift);
		if (postfix != null)
			ans.setAnswerPostfix(postfix);
		if (praefix != null)
			ans.setAnswerPrefix(praefix);
	}

	/**
	 * 
	 * Adds NumAnswer or NumAnswerInterval to a
	 * NumAnswers-Object.
	 * 
	 * @created 18.05.2011
	 * @param ans
	 * @param store
	 * @param fac
	 */
	private static void addNumAnswerInstance(NumAnswers ans, AnswerAttributeStore store, ObjectFactory fac) {
		String[] i = AnswerLine.getInterval(store.getText());
		if ( i != null ) {
			NumAnswerInterval inter = fac.createNumAnswersNumAnswerInterval();
			inter.setLower(new BigDecimal(i[0]));
			inter.setUpper(new BigDecimal(i[1]));
			if (store.getNegFactor() != null)
				inter.setNegFactor(new BigDecimal(store.getNegFactor()));
			inter.setPosFactor(new BigDecimal(store.getPosFactor()));
			if (store.getSimplefeedback() != null)
				inter.setSimpleFeedback(store.getSimplefeedback());
			ans.getNumAnswerOrNumAnswerInterval().add(inter);
		}

		NumAnswer a = fac.createNumAnswersNumAnswer();
		if (store.getNegFactor() != null)
			a.setNegFactor(new BigDecimal(store.getNegFactor()));
		a.setPosFactor(new BigDecimal(store.getPosFactor()));
		if (store.getSimplefeedback() != null)
			a.setSimpleFeedback(store.getSimplefeedback());
		a.setValue(new BigDecimal(store.getText()));
		ans.getNumAnswerOrNumAnswerInterval().add(a);
	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param it
	 * @param sec
	 * @param fac
	 */
	private static void renderHinweisOrErklaerungWithBinding(Mmcontent it, Section<?> sec, ObjectFactory fac) {

		List<Object> itList = it.getContentOrMultimediaItemOrFormula();

		for (Section<?> s : sec.getChildren().get(0).getChildren()) {

			if (s.get().isType(PlainText.class)) {
				String te = XMLUtils.clearPlainText(s);
				if (!te.equals(""))
					itList.add(te);
				continue;
			}
			if(s.get().isAssignableFromType(MultimediaItem.class)) {
				Mmitem item = fac.createMmitem();
				XMLUtils.configureMmitem(item, s);
				itList.add(item);
				continue;
			}
		}
	}

	private static void renderHinweisOrErklaerungWithBinding(Mmmixedcontent it, Section<?> sec, ObjectFactory fac) {

		List<Object> itList = it.getContent();

		for (Section<?> s : sec.getChildren().get(0).getChildren()) {

			if (s.get().isType(PlainText.class)) {
				String te = XMLUtils.clearPlainText(s);
				if (!te.equals(""))
					itList.add(fac.createContent(te));
				continue;
			}

			if(s.get().isAssignableFromType(MultimediaItem.class)) {
				Mmitem item = fac.createMmitem();
				XMLUtils.configureMmitem(item, s);
				itList.add(fac.createMultimediaItem(item));
				continue;
			}
		}
	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param simpleSec
	 * @param child
	 */
	private static String clearPlainText(Section<?> child) {
		String te = child.getOriginalText().replaceAll("[\\r\\n]", "");
		return te;
	}

	//	public static void createXMLFromCase(KnowWEArticle article) {
	//		Section<KnowWEArticle> articleSec = article.getSection();
	//
	//
	//
	//		Element root = new Element("Case");
	//		Document doc = new Document(root);
	//
	//		// MetaData
	//		XMLUtils.addMetaDataElement(root, articleSec);
	//
	//		// Einleitung
	//		XMLUtils.addMixedMultimediaElement(root, articleSec, "Intro");
	//
	//		// Info Abschnitte
	//		XMLUtils.addSectionsElement(root, articleSec);
	//
	//		// Abschluss
	//		XMLUtils.addMixedMultimediaElement(root, articleSec, "Extro");
	//
	//		XMLOutputter fmt = new XMLOutputter();
	//		fmt.setFormat( Format.getPrettyFormat() );
	//		try {
	//			fmt.output( doc, System.out );
	//		}
	//		catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//	}

	/**
	 * Creates the Question-Element from the list children
	 * Possible children for a question are:
	 * FrageText/Info/Feedback/Antworten
	 * 
	 * @created 12.05.2011
	 * @param questionss
	 * @param frageChilds.
	 */
	//	private static void addQuestionsElement(Element questions, List<Section<?>> frageChilds) {
	//
	//		Section<?> frage = frageChilds.remove(0);
	//		Section<?> fragetyp = Sections.findSuccessor(frage, FrageTyp.class);
	//		Section<?> frageGewicht = Sections.findSuccessor(frage, FrageGewicht.class);
	//		Section<?> frageText = Sections.findSuccessor(frage, FrageText.class);
	//		Element question = new Element(fragetyp.getOriginalText() + "Question");
	//
	//		String weight = frageGewicht.getOriginalText().trim();
	//		question.setAttribute("weight", weight);
	//		Element text = new Element("Text");
	//		text.addContent(frageText.getOriginalText());
	//		question.addContent(text);
	//
	//		for (Section<?> sec : frageChilds) {
	//			// Hints koennen immer kommen und element Info
	//			// beinhalten Text und Multimedia
	//			if (sec.get().isType(Hinweis.class)) {
	//				XMLUtils.renderHinweis(question, sec);
	//				continue;
	//			}
	//
	//			if(sec.get().isType(Antworten.class)) {
	//				XMLUtils.addAntwortenElement(question, sec);
	//				continue;
	//			}
	//
	//			// Feedback is Erklaerung
	//			if (sec.get().isType(Erklaerung.class)) {
	//				Element i = new Element("Feedback");
	//				XMLUtils.addmmmixedcontent(i, sec);
	//				question.addContent(i);
	//				continue;
	//			}
	//		}
	//
	//		questions.addContent(question);
	//	}
	//
	//	/**
	//	 *
	//	 * @created 12.05.2011
	//	 * @param question
	//	 * @param sec
	//	 */
	//	@SuppressWarnings("unchecked")
	//	private static void addAntwortenElement(Element question, Section<?> sec) {
	//		Element ants = new Element("Answers");
	//
	//		for (Section<?> s : sec.getChildren().get(0).getChildren()) {
	//			if (s.get().isType(PlainText.class)) continue;
	//			Element answer = new Element("Answer");
	//
	//			// PosFactor and NegFactor
	//			String posFactor = Antwort.getPosFactor((Section<Antwort>)s);
	//			answer.setAttribute("posFactor", posFactor);
	//			String negFactor = Antwort.getNegFactor((Section<Antwort>)s);
	//			if (negFactor != null)
	//				answer.setAttribute("negFactor", negFactor);
	//
	//			Section<AntwortText> text = Sections.findSuccessor(s, AntwortText.class);
	//			Element t = new Element("Text");
	//			t.addContent(text.getOriginalText());
	//			answer.addContent(t);
	//
	//			//SimpleFeedback
	//			Section<AntwortErklaerung> erklaerung = Sections.findSuccessor(s, AntwortErklaerung.class);
	//			if (erklaerung != null) {
	//				Element t1 = new Element("SimpleFeedback");
	//				t1.addContent(erklaerung.getOriginalText().
	//						substring(1, erklaerung.getOriginalText().length()-1));
	//				answer.addContent(t1);
	//			}
	//
	//			ants.addContent(answer);
	//		}
	//
	//		question.addContent(ants);
	//	}
	//
	//	/**
	//	 * Appends a Hinweis/Info
	//	 *
	//	 * @created 12.05.2011
	//	 * @param frage
	//	 * @param infoSec
	//	 */
	//	private static void renderHinweis(Element frage, Section<?> infoSec) {
	//		Element info = new Element("Info");
	//
	//		for (Section<?> sec : infoSec.getChildren().get(0).getChildren()) {
	//			if (sec.get().isType(PlainText.class)) {
	//				Element neu = new Element("Content");
	//				neu.addContent(sec.getOriginalText());
	//				info.addContent(neu);
	//				continue;
	//			}
	//			if(sec.get().isAssignableFromType(MultimediaItem.class)) {
	//				info.addContent(XMLUtils.createMultimediaElement(sec));
	//				continue;
	//			}
	//		}
	//
	//		frage.addContent(info);
	//	}
	//
	//	/**
	//	 *
	//	 * @created 15.05.2011
	//	 * @param simple
	//	 * @param child
	//	 */
	//	private static void addContentElement(Element simple, Section<?> child) {
	//		Element neu = new Element("Content");
	//		String te = child.getOriginalText().replaceAll("[\\r\\n]", "");
	//		if (te.equals("")) return;
	//		neu.addContent(te);
	//		simple.addContent(neu);
	//	}
	//
	//	/**
	//	 * Adds the MetaData from the Wikipage to the root.
	//	 *
	//	 * @created 11.05.2011
	//	 * @param root
	//	 * @param articleSec
	//	 */
	//	private static void addMetaDataElement(Element root, Section<KnowWEArticle> articleSec) {
	//		Element metaData = new Element("MetaData");
	//
	//		Section<MetaDaten> meta = Sections.findSuccessor(articleSec, MetaDaten.class);
	//		if (meta == null) return;
	//
	//		List<Section<MetaLine>> lines = new ArrayList<Section<MetaLine>>();
	//		Sections.findSuccessorsOfType(meta, MetaLine.class, lines);
	//
	//		List<String> todos = new ArrayList<String>();
	//		HashMap<String, String> scoreAtts = new HashMap<String, String>();
	//		HashMap<String, String> miscAtts = new HashMap<String, String>();
	//
	//		HashMap<String, String> attMap = MetaAttributes.getInstance().getAttributesForXMLMap();
	//		String attName = "";
	//		String attContent = "";
	//		for(Section<MetaLine> line : lines) {
	//			attName = Sections.findSuccessor(line, AttributeName.class).getOriginalText().trim();
	//			attContent = Sections.findSuccessor(line, AttributeContent.class).getOriginalText().trim();
	//
	//			if (attName.equals(MetaAttributes.CASE_TODO)) {
	//				todos.add(attContent);
	//				continue;
	//			}
	//
	//			// Score element
	//			if (attName.equals(MetaAttributes.CASE_POINTS) || attName.equals(MetaAttributes.CASE_PASS)
	//					|| attName.equals(MetaAttributes.TIME_LIMIT100) || attName.equals(MetaAttributes.TIME_LIMIT0)
	//					|| attName.equals(MetaAttributes.TIME_WEIGHT)) {
	//				scoreAtts.put(attMap.get(attName), attContent);
	//				continue;
	//			}
	//
	//			// Misc
	//			if (attName.equals(MetaAttributes.FEEDBACK) || attName.equals(MetaAttributes.LANGUAGE)
	//					|| attName.equals(MetaAttributes.SHOW_TIME)) {
	//				miscAtts.put(attMap.get(attName), attContent);
	//				continue;
	//			}
	//
	//			attName = attMap.get(attName);
	//			Element neu = new Element(attName);
	//			neu.addContent(attContent);
	//			metaData.addContent(neu);
	//		}
	//
	//		// add Score-element with Attributes
	//		if (!scoreAtts.isEmpty()) {
	//			Element score = new Element("Score");
	//			for (Map.Entry<String,String> e : scoreAtts.entrySet()) {
	//				score.setAttribute(e.getKey(), e.getValue());
	//			}
	//			metaData.addContent(score);
	//		}
	//
	//		// add Misc-element with Attributes
	//		if (!miscAtts.isEmpty()) {
	//			Element misc = new Element("Misc");
	//			for (Map.Entry<String,String> e : miscAtts.entrySet()) {
	//				misc.setAttribute(e.getKey(), e.getValue());
	//			}
	//			metaData.addContent(misc);
	//		}
	//
	//		// add Todos
	//		if (!todos.isEmpty()) {
	//			Element todosEl = new Element("Todos");
	//			for (String s : todos) {
	//				Element todo = new Element("Todo");
	//				todo.addContent(s);
	//				todosEl.addContent(todo);
	//			}
	//			metaData.addContent(todosEl);
	//		}
	//
	//		root.addContent(metaData);
	//	}
	//
	//	/**
	//	 *
	//	 * Adds the Introduction/Outroduction Element to root.
	//	 *
	//	 * @created 11.05.2011
	//	 * @param root
	//	 * @param sec
	//	 * @param elementName
	//	 */
	//	private static void addMixedMultimediaElement(Element root, Section<?> sec, String elementName) {
	//		Element intro = new Element(elementName);
	//
	//		Section<?> introSec = null;
	//
	//		if (elementName.equals("Intro"))
	//			introSec = Sections.findSuccessor(sec, Einleitung.class);
	//		if (elementName.equals("Extro"))
	//			introSec = Sections.findSuccessor(sec, Abschluss.class);
	//
	//		if (introSec == null) return;
	//		XMLUtils.addmmmixedcontent(intro, introSec);
	//
	//		root.addContent(intro);
	//	}
	//
	//	/**
	//	 *
	//	 *
	//	 * @created 12.05.2011
	//	 * @param intro
	//	 * @param introSec
	//	 */
	//	@SuppressWarnings("unchecked")
	//	private static void addmmmixedcontent(Element intro, Section<?> introSec) {
	//		List<Section<?>> contentChildren = null;
	//
	//		if (introSec.get().isAssignableFromType(BlockMarkupType.class)) {
	//			Section<BlockMarkupContent> s =
	//				Sections.findSuccessor(introSec, BlockMarkupContent.class);
	//			contentChildren = s.getChildren();
	//		}
	//
	//		if (introSec.get().isAssignableFromType(SubblockMarkup.class)) {
	//			Section<SubblockMarkupContent> s =
	//				Sections.findSuccessor(introSec, SubblockMarkupContent.class);
	//			contentChildren = s.getChildren();
	//		}
	//
	//		for (Section<?> sec : contentChildren) {
	//			if (sec.get().isType(Title.class)) {
	//				Element neu = new Element("Title");
	//				neu.addContent(sec.getOriginalText());
	//				intro.addContent(neu);
	//				continue;
	//			}
	//			if (sec.get().isType(PlainText.class)) {
	//				XMLUtils.addContentElement(intro, sec);
	//				continue;
	//			}
	//
	//			if(sec.get().isAssignableFromType(MultimediaItem.class)) {
	//				intro.addContent(XMLUtils.createMultimediaElement(sec));
	//				continue;
	//			}
	//		}
	//	}
	//
	//	/**
	//	 *
	//	 * Creates a Element of the given MultimediaSection.
	//	 *
	//	 * @created 15.05.2011
	//	 * @param child
	//	 * @return
	//	 */
	//	private static Element createMultimediaElement(Section<?> child) {
	//		String type = "";
	//		if(child.get().isType(Bild.class)) type = "image";
	//		if(child.get().isType(Video.class)) type = "video";
	//		//		if(child.get().isType(Link.class)) type = "link";
	//		//		if(child.get().isType(Audio.class)) type = "audio";
	//
	//		Element neu = new Element("MultimediaItem");
	//		neu.setAttribute("type", type);
	//		Element url = new Element("URL");
	//		url.addContent(child.getChildren().get(1).getOriginalText().trim());
	//		neu.addContent(url);
	//		return neu;
	//	}
	//
	//	/**
	//	 *
	//	 * Adds all Info-Abschnitte to root.
	//	 *
	//	 * TO DO only supports SimpleSections
	//	 * TO DO InfoWahl-Sections
	//	 *
	//	 * @created 11.05.2011
	//	 * @param root
	//	 * @param articleSec
	//	 */
	//	private static void addSectionsElement(Element root, Section<KnowWEArticle> articleSec) {
	//		Element sections = new Element("Sections");
	//
	//		List<Section<Info>> infoSecs = new ArrayList<Section<Info>>();
	//		Sections.findSuccessorsOfType(articleSec, Info.class, infoSecs);
	//
	//		for(Section<Info> infoSec : infoSecs) {
	//			List<Section<?>> childs = infoSec.getChildren().get(0).getChildren();
	//			Element simple = new Element("SimpleSection");
	//
	//			List<Section<?>> frageChilds = new ArrayList<Section<?>>();
	//			Element questions = new Element("Questions");
	//
	//			for (Section<?> child : childs) {
	//
	//				// First PlainText+Some Multimedia
	//				if (child.get().isType(PlainText.class)) {
	//					XMLUtils.addContentElement(simple, child);
	//					continue;
	//				}
	//
	//				if(child.get().isAssignableFromType(MultimediaItem.class)) {
	//					simple.addContent(XMLUtils.createMultimediaElement(child));
	//					continue;
	//				}
	//
	//				if (child.get().isType(Frage.class)) {
	//					if (!frageChilds.isEmpty()) {
	//						XMLUtils.addQuestionsElement(questions, frageChilds);
	//					}
	//					frageChilds.clear();
	//					frageChilds.add(child);
	//					continue;
	//				}
	//				if ( (child.get().isType(Hinweis.class))
	//						|| (child.get().isType(Antworten.class))
	//						|| (child.get().isType(Erklaerung.class)) ) {
	//					frageChilds.add(child);
	//				}
	//
	//				if(child.get().isType(Title.class)) {
	//					Element neu = new Element("Title");
	//					neu.addContent(child.getOriginalText());
	//					simple.addContent(neu);
	//					continue;
	//				}
	//
	//			}
	//
	//			if (!frageChilds.isEmpty())
	//				XMLUtils.addQuestionsElement(questions, frageChilds);
	//			simple.addContent(questions);
	//			sections.addContent(simple);
	//		}
	//
	//		root.addContent(sections);
	//	}
}