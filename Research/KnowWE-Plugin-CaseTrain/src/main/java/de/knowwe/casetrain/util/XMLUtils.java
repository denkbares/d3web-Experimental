/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
import java.util.ResourceBundle;

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
import de.knowwe.casetrain.info.AnswerLine;
import de.knowwe.casetrain.info.AnswerLine.AnswerExplanation;
import de.knowwe.casetrain.info.AnswerLine.AnswerText;
import de.knowwe.casetrain.info.AnswerLine.AnswerTextArgument;
import de.knowwe.casetrain.info.AnswersBlock;
import de.knowwe.casetrain.info.AnswersBlock.AnswersBlockWeightMark;
import de.knowwe.casetrain.info.AnswersBlockValidator;
import de.knowwe.casetrain.info.Explanation;
import de.knowwe.casetrain.info.Hint;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.info.Question;
import de.knowwe.casetrain.info.Question.QuestionText;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.info.Question.QuestionWeight;
import de.knowwe.casetrain.type.AttributeContent;
import de.knowwe.casetrain.type.AttributeName;
import de.knowwe.casetrain.type.Closure;
import de.knowwe.casetrain.type.Introduction;
import de.knowwe.casetrain.type.MetaAttributes;
import de.knowwe.casetrain.type.MetaData;
import de.knowwe.casetrain.type.MetaLine;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.BlockMarkupType;
import de.knowwe.casetrain.type.general.SubblockMarkup;
import de.knowwe.casetrain.type.general.SubblockMarkupContent;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Audio;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.Link;
import de.knowwe.casetrain.type.multimedia.MultimediaItem;
import de.knowwe.casetrain.type.multimedia.Video;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.basicType.PlainText;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.AnonymousType;

/**
 * 
 * XMLOutputParser for the casetrain-Markup.
 * 
 * @author Johannes Dienst
 * @created 11.05.2011
 */
public class XMLUtils {

	public static final String TMP_CASE_XML = "/tmp/case.xml";

	public static void createXML(Article article, String user) {
		Section<RootType> articleSec = article.getRootSection();

		ObjectFactory fac = new ObjectFactory();
		Case c = fac.createCase();

		// TODO how to get the webapp path?
		try {
			String path = KnowWEUtils.getApplicationRootPath();
			LoadSave.initialise(path);
		}
		catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		// MetaData
		XMLUtils.addMetaData(c, articleSec, fac);

		// Einleitung
		XMLUtils.addTitledMM(c, articleSec, fac, "Intro");

		// Info-Abschnitte
		XMLUtils.addSections(c, articleSec, fac);

		// Abschluss
		XMLUtils.addTitledMM(c, articleSec, fac, "Extro");

		// TODO Evaluation
		XMLUtils.addEvaluation(c, articleSec, fac);

		String webapp = KnowWEUtils.getKnowWEExtensionPath();
		try {
			File f = new File(webapp + TMP_CASE_XML);
			f.createNewFile();
			FileOutputStream stream = new FileOutputStream(f);
			stream.flush();
			LoadSave.saveCase(c, stream);
			stream.close();

			// Store File in Attachments
			Environment.getInstance().getWikiConnector().
					storeAttachment(article.getTitle(), user, f);

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
	private static void addEvaluation(Case c, Section<RootType> articleSec, ObjectFactory fac) {
		Evaluation evo = fac.createCaseEvaluation();
		EvaluationSections evoSecs = fac.createCaseEvaluationEvaluationSections();

		List<Section<de.knowwe.casetrain.evaluation.Evaluation>> found =
				new ArrayList<Section<de.knowwe.casetrain.evaluation.Evaluation>>();
		Sections.findSuccessorsOfType(
				articleSec, de.knowwe.casetrain.evaluation.Evaluation.class, found);

		for (Section<de.knowwe.casetrain.evaluation.Evaluation> infoSec : found) {
			List<Section<?>> childs =
					Sections.findSuccessor(infoSec, BlockMarkupContent.class).getChildren();
			SimpleSection simpleSec = fac.createSimpleSection();
			simpleSec.setQuestions(fac.createBasicSectionQuestions());

			List<Section<?>> frageChilds = new ArrayList<Section<?>>();

			// TODO This is a fix, cause there is no title at all
			// So we add a title manually here.
			simpleSec.setTitle(ResourceBundle.getBundle("casetrain_messages").
					getString("Evaluation"));

			for (Section<?> child : childs) {

				// First PlainText+Some Multimedia
				if (Sections.hasType(child, PlainText.class)) {
					XMLUtils.clearPlainText(
							child, simpleSec.getContentOrMultimediaItemOrFormula());
					continue;
				}

				if (Sections.hasType(child, Link.class)) {
					String original = child.getText();
					simpleSec.getContentOrMultimediaItemOrFormula().add(original);
					continue;
				}

				if (Sections.hasType(child, MultimediaItem.class)) {
					Mmitem it = fac.createMmitem();
					XMLUtils.configureMmitem(it, child);
					simpleSec.getContentOrMultimediaItemOrFormula().add(it);
					continue;
				}

				if (Sections.hasType(child, Question.class)) {
					if (!frageChilds.isEmpty()) {
						XMLUtils.addQuestions(simpleSec, frageChilds, fac);
					}
					frageChilds.clear();
					frageChilds.add(child);
					continue;
				}
				if ((Sections.hasType(child, Hint.class))
						|| (Sections.hasType(child, AnswersBlock.class))
						|| (Sections.hasType(child, Explanation.class))) {
					frageChilds.add(child);
				}

				// TODO Evaluation has title?
				// if(child.get().isType(Title.class)) {
				// simpleSec.setTitle(XMLUtils.clearPlainText(child));
				// continue;
				// }

			}

			if (!frageChilds.isEmpty()) XMLUtils.addQuestions(simpleSec, frageChilds, fac);

			// Add the EvaluationEnd
			EvaluationEnd end = fac.createCaseEvaluationEvaluationEnd();

			Section<de.knowwe.casetrain.evaluation.EvaluationEnd> evoEnd =
					Sections.findSuccessor(infoSec,
							de.knowwe.casetrain.evaluation.EvaluationEnd.class);

			if (evoEnd == null) {
				end.setTitle("Evaluationende");
				end.getContentOrMultimediaItemOrFormula().
						add("Wir danken Ihnen für Ihre Mitarbeit!");
			}

			if (evoEnd != null) {
				Section<Title> tit = Sections.findSuccessor(evoEnd, Title.class);
				Section<PlainText> con = Sections.findSuccessor(evoEnd, PlainText.class);
				end.setTitle(XMLUtils.clearPlainText(tit));
				// Does this work?
				// end.getContentOrMultimediaItemOrFormula().add(XMLUtils.clearPlainText(con));
				XMLUtils.clearPlainText(con, end.getContentOrMultimediaItemOrFormula());
			}

			evo.setEvaluationEnd(end);

			evoSecs.getSimpleSection().add(simpleSec);
		}

		if (evoSecs.getSimpleSection().isEmpty()) return;

		evo.setEvaluationSections(evoSecs);

		c.setEvaluation(evo);
	}

	private static void addTitledMM(Case c, Section<RootType> sec,
			ObjectFactory fac, String elementName) {

		Section<?> introSec = null;

		if (elementName.equals("Intro")) introSec = Sections.findSuccessor(sec, Introduction.class);
		if (elementName.equals("Extro")) introSec = Sections.findSuccessor(sec, Closure.class);

		if (introSec == null) return;
		XMLUtils.addTitledmmmixedcontent(c, introSec, fac);
	}

	/**
	 * 
	 * @created 16.05.2011
	 * @param c
	 * @param introSec
	 */
	private static void addTitledmmmixedcontent(Case c,
			Section<?> introSec, ObjectFactory fac) {

		List<Section<?>> contentChildren = null;

		if (Sections.hasType(introSec, BlockMarkupType.class)) {
			Section<BlockMarkupContent> s =
					Sections.findSuccessor(introSec, BlockMarkupContent.class);
			contentChildren = s.getChildren();
		}

		if (Sections.hasType(introSec, SubblockMarkup.class)) {
			Section<SubblockMarkupContent> s =
					Sections.findSuccessor(introSec, SubblockMarkupContent.class);
			contentChildren = s.getChildren();
		}

		Titledmmcontent titledmmContent = fac.createTitledmmcontent();

		for (Section<?> sec : contentChildren) {
			if (Sections.hasType(sec, Title.class)) {
				titledmmContent.setTitle(XMLUtils.clearPlainText(sec));
				continue;
			}

			if (Sections.hasType(sec, PlainText.class)) {
				XMLUtils.clearPlainText(
						sec, titledmmContent.getContentOrMultimediaItemOrFormula());
				continue;
			}

			if (Sections.hasType(sec, Link.class)) {
				String original = sec.getText();
				titledmmContent.getContentOrMultimediaItemOrFormula().add(original);
				continue;
			}

			if (Sections.hasType(sec, MultimediaItem.class)) {
				Mmitem it = fac.createMmitem();
				XMLUtils.configureMmitem(it, sec);
				titledmmContent.getContentOrMultimediaItemOrFormula().add(it);
				continue;
			}
		}

		if (Sections.hasType(introSec, Introduction.class)) c.setIntro(titledmmContent);

		if (Sections.hasType(introSec, Closure.class)) {
			Extro ex = fac.createCaseExtro();
			ex.setTitle(titledmmContent.getTitle());
			ex.getContentOrMultimediaItemOrFormula().
					addAll(titledmmContent.getContentOrMultimediaItemOrFormula());
			c.setExtro(ex);
		}

	}

	private static void configureMmitem(Mmitem it, Section<?> sec) {
		String type = "";
		if (Sections.hasType(sec, Image.class)) type = "image";
		if (Sections.hasType(sec, Video.class)) type = "video";
		if (Sections.hasType(sec, Audio.class)) type = "audio";
		it.setType(type);
		it.setURL(sec.getChildren().get(1).getText().trim());
	}

	/**
	 * 
	 * 
	 * @created 16.05.2011
	 * @param c
	 * @param articleSec
	 */
	private static void addMetaData(
			Case c, Section<RootType> articleSec, ObjectFactory fac) {
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
		for (Section<MetaLine> line : lines) {
			Section<AttributeName> attributeName = Sections.findSuccessor(line,
					AttributeName.class);
			if (attributeName == null) continue;

			attName = attributeName.getText().trim();
			attContent = Sections.findSuccessor(line, AttributeContent.class).getText().trim();

			if (attName.equals(MetaAttributes.CASE_TODO)) {
				todos.add(attContent);
				continue;
			}

			// Score element
			if (attName.equals(MetaAttributes.CASE_POINTS)
					|| attName.equals(MetaAttributes.CASE_PASS)
					|| attName.equals(MetaAttributes.TIME_LIMIT100)
					|| attName.equals(MetaAttributes.TIME_LIMIT0)
					|| attName.equals(MetaAttributes.TIME_WEIGHT)) {
				scoreAtts.put(attName, attContent);
				continue;
			}

			// Misc
			if (attName.equals(MetaAttributes.FEEDBACK)
					|| attName.equals(MetaAttributes.LANGUAGE)
					|| attName.equals(MetaAttributes.SHOW_TIME)) {
				miscAtts.put(attName, attContent);
				continue;
			}

			if (attName.equals(MetaAttributes.CASE_ID_KEY)) metaObj.setID(attContent);
			if (attName.equals(MetaAttributes.CASE_AUTHOR)) metaObj.setAuthor(attContent);
			if (attName.equals(MetaAttributes.CASE_DATE)) metaObj.setDate(attContent);
			if (attName.equals(MetaAttributes.CASE_TITLE)) metaObj.setTitle(attContent);
			if (attName.equals(MetaAttributes.CASE_VERSION)) metaObj.setVersion(attContent);
			if (attName.equals(MetaAttributes.DURATION_MIN)) metaObj.setDurationMinutes(new Long(
					attContent));
			if (attName.equals(MetaAttributes.DIFFICULTY)) metaObj.setDifficulty(attContent);
			if (attName.equals(MetaAttributes.REQUIREMENTS)) metaObj.setPrereqs(attContent);
			if (attName.equals(MetaAttributes.HINT)) metaObj.setNotice(attContent);
			if (attName.equals(MetaAttributes.COMMENT)) metaObj.setComment(attContent);
			if (attName.equals(MetaAttributes.KEYWORDS)) metaObj.setKeywords(attContent);
			if (attName.equals(MetaAttributes.CASE_HISTORY)) metaObj.setHistory(attContent);

			// TODO Background and setURL?
			// if (attName.equals(MetaAttributes.BACKGROUND))
			// metaObj.setURL(arg0)(attContent);
			// if (attName.equals(MetaAttributes.HW_LINKTEXT))
			// metaObj.setHistory(attContent);
		}

		// add Score-element with Attributes
		if (!scoreAtts.isEmpty()) {
			Score score = fac.createCaseMetadataScore();
			for (Map.Entry<String, String> e : scoreAtts.entrySet()) {

				if (e.getKey().equals(MetaAttributes.TIME_LIMIT0)) score.setMaxTimeSeconds(new Long(
						e.getValue()));

				if (e.getKey().equals(MetaAttributes.CASE_PASS)) score.setMinScoreForSuccess(new BigDecimal(
						e.getValue()));

				if (e.getKey().equals(MetaAttributes.TIME_LIMIT100)) score.setOkTimeSeconds(new Long(
						e.getValue()));

				if (e.getKey().equals(MetaAttributes.CASE_POINTS)) score.setWeight(new BigDecimal(
						e.getValue()));

				if (e.getKey().equals(MetaAttributes.TIME_WEIGHT)) score.setWeightTime(new BigDecimal(
						e.getValue()));
			}
			metaObj.setScore(score);
		}

		// add Misc-element with Attributes
		// TODO colorSchema missing / Set show clock is ugly
		if (!miscAtts.isEmpty()) {
			Misc misc = fac.createCaseMetadataMisc();
			for (Map.Entry<String, String> e : miscAtts.entrySet()) {

				if (e.getKey().equals(MetaAttributes.FEEDBACK)) misc.setFeedback(e.getValue());

				if (e.getKey().equals(MetaAttributes.LANGUAGE)) misc.setLanguage(e.getValue());

				if (e.getKey().equals(MetaAttributes.SHOW_TIME)) misc.setShowClock(new Boolean(
						e.getValue()));
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
	private static void addSections(Case c, Section<RootType> articleSec,
			ObjectFactory fac) {

		List<Section<Info>> infoSecs = new ArrayList<Section<Info>>();
		Sections.findSuccessorsOfType(articleSec, Info.class, infoSecs);

		for (Section<Info> infoSec : infoSecs) {
			List<Section<?>> childs = Sections.findSuccessor(infoSec,
					BlockMarkupContent.class).getChildren();
			SimpleSection simpleSec = fac.createSimpleSection();
			simpleSec.setQuestions(fac.createBasicSectionQuestions());

			List<Section<?>> frageChilds = new ArrayList<Section<?>>();

			for (Section<?> child : childs) {

				// First PlainText+Some Multimedia
				if (Sections.hasType(child, PlainText.class)) {
					XMLUtils.clearPlainText(child,
							simpleSec.getContentOrMultimediaItemOrFormula());
					continue;
				}

				if (Sections.hasType(child, Link.class)) {
					String original = child.getText();
					simpleSec.getContentOrMultimediaItemOrFormula().add(original);
					continue;
				}

				if (Sections.hasType(child, MultimediaItem.class)) {
					Mmitem it = fac.createMmitem();
					XMLUtils.configureMmitem(it, child);
					simpleSec.getContentOrMultimediaItemOrFormula().add(it);
					continue;
				}

				if (Sections.hasType(child, Question.class)) {
					if (!frageChilds.isEmpty()) {
						XMLUtils.addQuestions(simpleSec, frageChilds, fac);
					}
					frageChilds.clear();
					frageChilds.add(child);
					continue;
				}
				if ((Sections.hasType(child, Hint.class))
						|| (Sections.hasType(child, AnswersBlock.class))
						|| (Sections.hasType(child, Explanation.class))) {
					frageChilds.add(child);
				}

				if (Sections.hasType(child, Title.class)) {
					simpleSec.setTitle(XMLUtils.clearPlainText(child));
					continue;
				}

			}

			if (!frageChilds.isEmpty()) XMLUtils.addQuestions(simpleSec, frageChilds, fac);
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
	private static void addQuestions(
			SimpleSection simpleSec, List<Section<?>> frageChilds, ObjectFactory fac) {
		Questions questions = simpleSec.getQuestions();
		List<JAXBElement<? extends BasicQuestion>> questionsList = questions.getOCQuestionOrMCQuestionOrHLMMCQuestion();

		Section<?> frage = frageChilds.get(0);
		Section<?> fragetyp = Sections.findSuccessor(frage, QuestionType.class);
		String typ = fragetyp.getText().trim();

		if (typ.equals(AnswersBlockValidator.OC)) {
			ChoiceQuestion q = fac.createChoiceQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.
					add(fac.createBasicSectionQuestionsOCQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.MC)) {
			ChoiceQuestion q = fac.createChoiceQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.
					add(fac.createBasicSectionQuestionsMCQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.W)) {
			WordQuestion q = fac.createBasicSectionQuestionsWordQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.add(fac.createBasicSectionQuestionsWordQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.UMW)) {
			MultiWordQuestion q = fac.createMultiWordQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.
					add(fac.createBasicSectionQuestionsUMWordQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.OMW)) {
			MultiWordQuestion q = fac.createMultiWordQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.
					add(fac.createBasicSectionQuestionsOMWordQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.N)) {
			NumQuestion q = fac.createBasicSectionQuestionsNumQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.
					add(fac.createBasicSectionQuestionsNumQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.MN)) {
			MNumQuestion q = fac.createBasicSectionQuestionsMNumQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
			questionsList.
					add(fac.createBasicSectionQuestionsMNumQuestion(q));
		}

		if (typ.equals(AnswersBlockValidator.T)) {
			TextQuestion q = fac.createBasicSectionQuestionsTextQuestion();
			XMLUtils.createQuestion(frageChilds, q, fac);
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
	@SuppressWarnings("unchecked")
	private static void createQuestion(
			List<Section<?>> frageChilds, BasicQuestion question, ObjectFactory fac) {

		Section<?> frage = frageChilds.remove(0);
		// Section<?> fragetyp = Sections.findSuccessor(frage, FrageTyp.class);
		Section<?> frageGewicht = Sections.findSuccessor(frage, QuestionWeight.class);
		Section<?> frageText = Sections.findSuccessor(frage, QuestionText.class);

		String weight = frageGewicht.getText().trim();
		question.setWeight(new BigDecimal(weight));
		question.setText(frageText.getText());

		for (Section<?> sec : frageChilds) {

			if (Sections.hasType(sec, Hint.class)) {
				Mmmixedcontent it = fac.createMmmixedcontent();
				XMLUtils.renderHinweisOrErklaerung(it, sec, fac);
				question.setInfo(it);
				continue;
			}

			if (Sections.hasType(sec, AnswersBlock.class)) {
				XMLUtils.addAntwortenWithBinding(
						question, (Section<AnswersBlock>) sec, fac);
				continue;
			}

			// Feedback is Erklaerung
			if (Sections.hasType(sec, Explanation.class)) {
				Mmcontent it = fac.createMmcontent();
				XMLUtils.renderHinweisOrErklaerung(it, sec, fac);
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
	private static void addAntwortenWithBinding(BasicQuestion question, Section<AnswersBlock> antworten, ObjectFactory fac) {

		List<AnswerAttributeStore> ants = new ArrayList<AnswerAttributeStore>();
		String postfix = null;
		String praefix = null;
		String heading = null;
		for (Section<?> s : Sections.findSuccessor(
				antworten, SubblockMarkupContent.class).getChildren()) {

			if (Sections.hasType(s, PlainText.class)
					|| Sections.hasType(s, AnswersBlockWeightMark.class)
					|| Sections.hasExactType(s, AnonymousType.class)) continue;

			// PosFactor and NegFactor
			String posFactor = AnswerLine.getPosFactor((Section<AnswerLine>) s);
			String negFactor = AnswerLine.getNegFactor((Section<AnswerLine>) s);

			// AntwortText
			Section<AnswerText> text = Sections.findSuccessor(s, AnswerText.class);
			String antwortText = "";
			if (text != null) antwortText = text.getText();

			// AntwortTextArgument
			String textArgString = null;
			Section<AnswerTextArgument> textArg = Sections.findSuccessor(s,
					AnswerTextArgument.class);
			if (textArg != null) textArgString = textArg.getText().trim();

			// SimpleFeedback
			Section<AnswerExplanation> erklaerung = Sections.findSuccessor(s,
					AnswerExplanation.class);
			String erkl = null;
			if (erklaerung != null) {
				erkl = erklaerung.getText().
						substring(1, erklaerung.getText().length() - 1);
			}

			// Postfix Praefix Ueberschrift
			Section<AnswersBlock.Postfix> post =
					Sections.findSuccessor(s, AnswersBlock.Postfix.class);
			if (post != null) postfix = post.getText().trim();

			Section<AnswersBlock.Praefix> prae =
					Sections.findSuccessor(s, AnswersBlock.Praefix.class);
			if (prae != null) praefix = prae.getText().trim();

			Section<AnswersBlock.Heading> head =
					Sections.findSuccessor(s, AnswersBlock.Heading.class);
			if (head != null) heading = head.getText().trim();

			ants.add(new AnswerAttributeStore(posFactor, negFactor,
					antwortText, erkl, textArgString));
		}

		if (question instanceof ChoiceQuestion) {
			ChoiceQuestion qu = (ChoiceQuestion) question;
			Answers ans = fac.createChoiceQuestionAnswers();
			for (AnswerAttributeStore store : ants) {
				ChoiceAnswer a = fac.createChoiceAnswer();
				if (store.getNegFactor() != null) a.setNegFactor(new BigDecimal(
						store.getNegFactor()));
				a.setPosFactor(new BigDecimal(store.getPosFactor()));
				if (store.getSimplefeedback() != null) a.setSimpleFeedback(store.getSimplefeedback());
				a.setText(store.getText());
				ans.getAnswer().add(a);
			}
			qu.setAnswers(ans);
		}

		if (question instanceof WordQuestion) {
			WordQuestion qu = (WordQuestion) question;
			WordAnswers ans = fac.createWordAnswers();

			if (postfix != null) ans.setAnswerPostfix(postfix);
			if (praefix != null) ans.setAnswerPrefix(praefix);
			if (heading != null) ans.setAnswerCaption(heading);

			for (AnswerAttributeStore store : ants) {
				WordAnswer a = fac.createWordAnswersWordAnswer();
				a.setEditDistance(new Long(
						AnswersBlock.getEditDistance(store.getTextArgument())));
				a.setIsRegularExpression(AnswersBlock.getIsRegularExpression(store.getTextArgument()));
				if (store.getNegFactor() != null) a.setNegFactor(new BigDecimal(
						store.getNegFactor()));
				a.setPosFactor(new BigDecimal(store.getPosFactor()));
				if (store.getSimplefeedback() != null) a.setSimpleFeedback(store.getSimplefeedback());
				a.setText(store.getText());
				ans.getWordAnswer().add(a);
			}
			qu.setWordAnswers(ans);
		}

		if (question instanceof MultiWordQuestion) {
			MultiWordQuestion qu = (MultiWordQuestion) question;
			WordAnswers ans = fac.createWordAnswers();

			if (postfix != null) ans.setAnswerPostfix(postfix);
			if (praefix != null) ans.setAnswerPrefix(praefix);
			if (heading != null) ans.setAnswerCaption(heading);

			Section<AnswersBlockWeightMark> aBWMark =
					Sections.findSuccessor(antworten, AnswersBlockWeightMark.class);
			if (aBWMark != null) ans.setWeight(new BigDecimal(
					AnswersBlock.getWeight(aBWMark.getText())));

			for (AnswerAttributeStore store : ants) {
				WordAnswer a = fac.createWordAnswersWordAnswer();
				a.setEditDistance(new Long(
						AnswersBlock.getEditDistance(store.getTextArgument())));
				a.setIsRegularExpression(AnswersBlock.getIsRegularExpression(store.getTextArgument()));
				if (store.getNegFactor() != null) a.setNegFactor(new BigDecimal(
						store.getNegFactor()));
				a.setPosFactor(new BigDecimal(store.getPosFactor()));
				if (store.getSimplefeedback() != null) a.setSimpleFeedback(store.getSimplefeedback());
				a.setText(store.getText());
				ans.getWordAnswer().add(a);
			}

			qu.getWordAnswers().add(ans);
		}

		if (question instanceof NumQuestion) {
			NumQuestion q = (NumQuestion) question;
			NumAnswers ans = fac.createNumAnswers();

			if (postfix != null) ans.setAnswerPostfix(postfix);
			if (praefix != null) ans.setAnswerPrefix(praefix);
			if (heading != null) ans.setAnswerCaption(heading);

			for (AnswerAttributeStore store : ants) {
				XMLUtils.addNumAnswerInstance(ans, store, fac);
			}
			XMLUtils.addPraePostUeberschrift(ans, heading, postfix, praefix);
			q.setNumAnswers(ans);
		}

		if (question instanceof MNumQuestion) {
			MNumQuestion q = (MNumQuestion) question;
			NumAnswers ans = fac.createNumAnswers();

			if (postfix != null) ans.setAnswerPostfix(postfix);
			if (praefix != null) ans.setAnswerPrefix(praefix);
			if (heading != null) ans.setAnswerCaption(heading);

			Section<AnswersBlockWeightMark> aBWMark =
					Sections.findSuccessor(antworten, AnswersBlockWeightMark.class);
			if (aBWMark != null) ans.setWeight(new BigDecimal(
					AnswersBlock.getWeight(aBWMark.getText())));

			for (AnswerAttributeStore store : ants) {
				XMLUtils.addNumAnswerInstance(ans, store, fac);
			}
			XMLUtils.addPraePostUeberschrift(ans, heading, postfix, praefix);
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
	 * Adds Praefix/Postfix/Ueberschrift to NumAnswerObject. They are not added
	 * if they are null.
	 * 
	 * @created 18.05.2011
	 * @param ans
	 * @param heading
	 * @param postfix
	 * @param praefix
	 */
	private static void addPraePostUeberschrift(NumAnswers ans, String heading,
			String postfix, String praefix) {
		if (heading != null) ans.setAnswerCaption(heading);
		if (postfix != null) ans.setAnswerPostfix(postfix);
		if (praefix != null) ans.setAnswerPrefix(praefix);
	}

	/**
	 * 
	 * Adds NumAnswer or NumAnswerInterval to a NumAnswers-Object.
	 * 
	 * @created 18.05.2011
	 * @param ans
	 * @param store
	 * @param fac
	 */
	private static void addNumAnswerInstance(NumAnswers ans, AnswerAttributeStore store,
			ObjectFactory fac) {
		String[] i = AnswerLine.getInterval(store.getText());
		if (i != null) {
			NumAnswerInterval inter = fac.createNumAnswersNumAnswerInterval();
			inter.setLower(new BigDecimal(i[0]));
			inter.setUpper(new BigDecimal(i[1]));
			if (store.getNegFactor() != null) inter.setNegFactor(new BigDecimal(
					store.getNegFactor()));
			inter.setPosFactor(new BigDecimal(store.getPosFactor()));
			if (store.getSimplefeedback() != null) inter.setSimpleFeedback(store.getSimplefeedback());
			ans.getNumAnswerOrNumAnswerInterval().add(inter);
		}

		NumAnswer a = fac.createNumAnswersNumAnswer();
		if (store.getNegFactor() != null) a.setNegFactor(new BigDecimal(store.getNegFactor()));
		a.setPosFactor(new BigDecimal(store.getPosFactor()));
		if (store.getSimplefeedback() != null) a.setSimpleFeedback(store.getSimplefeedback());
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
	private static void renderHinweisOrErklaerung(Mmcontent it, Section<?> sec,
			ObjectFactory fac) {

		List<Object> itList = it.getContentOrMultimediaItemOrFormula();

		for (Section<?> s : Sections.findSuccessor(sec, SubblockMarkupContent.class).getChildren()) {

			if (Sections.hasType(s, PlainText.class)) {
				XMLUtils.clearPlainText(s, itList);
				continue;
			}

			if (Sections.hasType(s, Link.class)) {
				String original = s.getText();
				itList.add(original);
				continue;
			}

			if (Sections.hasType(s, MultimediaItem.class)) {
				Mmitem item = fac.createMmitem();
				XMLUtils.configureMmitem(item, s);
				itList.add(item);
				continue;
			}
		}
	}

	private static void renderHinweisOrErklaerung(Mmmixedcontent it,
			Section<?> sec, ObjectFactory fac) {

		List<Object> itList = it.getContent();

		for (Section<?> s : Sections.findSuccessor(sec, SubblockMarkupContent.class).getChildren()) {

			if (Sections.hasType(s, PlainText.class)) {
				XMLUtils.clearPlainText(s, itList);
				continue;
			}

			if (Sections.hasType(s, Link.class)) {
				String original = s.getText();
				itList.add(original);
				continue;
			}

			if (Sections.hasType(s, MultimediaItem.class)) {
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
	 * @param child
	 * @param itemList
	 */
	private static void clearPlainText(Section<?> child, List<Object> itemList) {
		String te = child.getText().replaceAll("[\\r\\n]", "");
		if (!te.equals("")) {
			te = Utils.wikiSyntaxToHtml(te);
			itemList.add(te);
		}
	}

	/**
	 * 
	 * 
	 * @created 30.05.2011
	 * @param child
	 */
	private static String clearPlainText(Section<?> child) {
		String te = child.getText().replaceAll("[\\r\\n]", "");
		te = Utils.wikiSyntaxToHtml(te);
		return te;
	}

}
