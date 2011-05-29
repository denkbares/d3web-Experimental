package tests;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import utils.MyTestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.basic.PlainText;
import de.knowwe.casetrain.evaluation.Evaluation;
import de.knowwe.casetrain.evaluation.EvaluationEnd;
import de.knowwe.casetrain.info.AnswerLine;
import de.knowwe.casetrain.info.AnswerLine.AnswerExplanation;
import de.knowwe.casetrain.info.AnswerLine.AnswerMark;
import de.knowwe.casetrain.info.AnswerLine.AnswerText;
import de.knowwe.casetrain.info.AnswersBlock;
import de.knowwe.casetrain.info.AnswersBlock.AnswerOrderMark;
import de.knowwe.casetrain.info.AnswersBlock.Heading;
import de.knowwe.casetrain.info.AnswersBlock.Postfix;
import de.knowwe.casetrain.info.AnswersBlock.Praefix;
import de.knowwe.casetrain.info.Explanation;
import de.knowwe.casetrain.info.Hint;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.info.Question;
import de.knowwe.casetrain.info.Question.QuestionText;
import de.knowwe.casetrain.info.Question.QuestionType;
import de.knowwe.casetrain.info.Question.QuestionWeight;
import de.knowwe.casetrain.type.Closure;
import de.knowwe.casetrain.type.Introduction;
import de.knowwe.casetrain.type.MetaData;
import de.knowwe.casetrain.type.MetaLine;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.SubblockMarkupContent;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Image;
import de.knowwe.casetrain.type.multimedia.MultimediaItem.MultimediaItemContent;
import de.knowwe.casetrain.type.multimedia.Video;


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

/**
 * 
 * Note: Dont change the ordering in the CaseTrainWikipage.txt.
 * The tests rely on the right order of Questions/Hints/Answer/Blocks!
 * 
 * @author Johannes Dienst
 * @created 20.05.2011
 */
public class CaseTrainArticleTest extends TestCase {

	private final String TESTSUITEARTICLE = "src/test/resources/CaseTrainWikipage.txt";

	private final String missingComponent = "Missing component: ";
	private Section<KnowWEArticle> articleSec;
	private Section<MetaData> meta;
	private Section<Introduction> intro;
	private Section<Info> info;
	private Section<Evaluation> eval;
	private Section<Closure> closure;

	private List<Section<Question>> questions;
	List<Section<AnswersBlock>> antworten;

	private static final String TITLE_MISSING = "Title is Missing in ";
	private static final String TEXT_MISSING = "Text is Missing in ";
	private static final String PICTURE_MISSING = "Picture is Missing in ";
	private static final String VIDEO_MISSING = "Video is Missing in ";
	//	private static final String AUDIO_MISSING = "Audio is Missing in ";
	//	private static final String LINK_MISSING = "Link is Missing in ";

	private static final String WRONG_STRING = "Wrong String detected in ";
	private static final String WRONG_PIC_COUNT = "Wrong number of pictures in ";
	private static final String WRONG_VIDEO_COUNT = "Wrong number of videos in ";
	private static final String WRONG_HINT_COUNT = "Wrong number of hints in ";
	private static final String WRONG_QUESTION_COUNT = "Wrong number of questions in ";
	private static final String WRONG_ANSWERSBLOCKS_COUNT = "Wrong number of answer-blocks in ";
	private static final String WRONG_EXPLICATION_COUNT = "Wrong number of explications in ";

	private static final String MISSING = "Missing ";

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEArticle article = MyTestArticleManager.getArticle(TESTSUITEARTICLE);
		articleSec = article.getSection();
		meta = Sections.findSuccessor(articleSec, MetaData.class);
		intro = Sections.findSuccessor(articleSec, Introduction.class);
		info = Sections.findSuccessor(articleSec, Info.class);
		eval = Sections.findSuccessor(articleSec, Evaluation.class);
		closure = Sections.findSuccessor(articleSec, Closure.class);

		if (info != null) {
			questions = new ArrayList<Section<Question>>();
			Sections.findSuccessorsOfType(info, Question.class, questions);
			antworten = new ArrayList<Section<AnswersBlock>>();
			Sections.findSuccessorsOfType(info, AnswersBlock.class, antworten);
		}

	}

	@Test
	public void testTestCases() {

		// MetaDaten
		assertNotNull(missingComponent+MetaData.class.getName(), meta);

		// Introduction
		assertNotNull(missingComponent+Introduction.class.getName(), intro);

		// Info
		assertNotNull(missingComponent+Info.class.getName(), info);

		// Evaluation
		assertNotNull(missingComponent+Evaluation.class.getName(), eval);

		// Conclusion
		assertNotNull(missingComponent+Closure.class.getName(), closure);

	}

	@Test
	public void testMetaDaten() {
		Section<BlockMarkupContent> content =
			Sections.findSuccessor(meta, BlockMarkupContent.class);
		List<Section<MetaLine>> children = Sections.findChildrenOfType(content, MetaLine.class);
		assertEquals("Wrong Meta-Attribute-Count", 25, children.size());
	}

	// TODO MultimediaTypes: audio/link missing.
	@Test
	public void testIntroduction() {
		Section<BlockMarkupContent> content =
			Sections.findSuccessor(intro, BlockMarkupContent.class);
		Section<Title> title = Sections.findSuccessor(content, Title.class);
		Section<PlainText> text = Sections.findSuccessor(content, PlainText.class);
		Section<Image> bild = Sections.findSuccessor(content, Image.class);
		Section<Video> video = Sections.findSuccessor(content, Video.class);
		//		Section<Audio> audio = Sections.findSuccessor(content, Audio.class);
		//		Section<Link> link = Sections.findSuccessor(content, Link.class);

		// Not null tests
		assertNotNull(TITLE_MISSING + Introduction.class.getName(), title);
		assertNotNull(TEXT_MISSING + Introduction.class.getName(), text);
		assertNotNull(PICTURE_MISSING + Introduction.class.getName(), bild);
		assertNotNull(VIDEO_MISSING + Introduction.class.getName(), video);
		//		assertNotNull(AUDIO_MISSING + Einleitung.class.getName(), audio);
		//		assertNotNull(LINK_MISSING + Einleitung.class.getName(), link);

		// Content tests
		assertEquals(WRONG_STRING  + Introduction.class.getName(),
				"Titel der Einleitung", title.getOriginalText().trim());
		assertEquals(WRONG_STRING  + Introduction.class.getName(),
				"Text der Einleitung\r\n", text.getOriginalText());

		// MultimediaItems test
		Section<MultimediaItemContent> mContent =
			Sections.findSuccessor(bild, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Introduction.class.getName(),
				"ls6logo.gif", mContent.getOriginalText().trim());

		mContent =
			Sections.findSuccessor(video, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Introduction.class.getName(),
				"video42.avi", mContent.getOriginalText().trim());
	}

	@Test
	public void testInfo() {

		Section<BlockMarkupContent> content =
			Sections.findSuccessor(info, BlockMarkupContent.class);

		//		List<Section<Bild>> pics = new ArrayList<Section<Bild>>();
		Section<Image> bild = Sections.findChildOfType(content, Image.class);
		assertNotNull("No picture in Info", bild);
		//		assertEquals(WRONG_PIC_COUNT + Info.class.getName(), 1, pics.size());

		//		List<Section<Video>> vids = new ArrayList<Section<Video>>();
		Section<Video> video = Sections.findChildOfType(content, Video.class);
		//		Sections.findSuccessorsOfType(info, Video.class, vids);
		assertNotNull("No video in Info", video);
		//		assertEquals(WRONG_VIDEO_COUNT + Info.class.getName(), 1, vids.size());

		List<Section<Hint>> hints = new ArrayList<Section<Hint>>();
		Sections.findSuccessorsOfType(info, Hint.class, hints);
		assertEquals(WRONG_HINT_COUNT + Info.class.getName(), 11, hints.size());

		// Test the first Hint
		Section<Hint> hint = hints.get(0);
		List<Section<PlainText>> hintPlains = new ArrayList<Section<PlainText>>();
		Sections.findSuccessorsOfType(hint, PlainText.class, 2, hintPlains);
		assertEquals("Wrong number of PlainText in " + Hint.class.getName(), 3, hintPlains.size());
		String text = hintPlains.get(0).getOriginalText().trim();
		assertEquals(WRONG_STRING + Hint.class.getName(),"Geben Sie mindestens 2 Probleme an.", text);
		text = hintPlains.get(2).getOriginalText().trim();
		assertEquals(WRONG_STRING + Hint.class.getName(),"Testtext.", text);
		Section<Image> hintPic = Sections.findSuccessor(hint, Image.class);
		assertNotNull(PICTURE_MISSING + Hint.class.getName(), hintPic);
		Section<Video> hintVideo = Sections.findSuccessor(hint, Video.class);
		assertNotNull(VIDEO_MISSING + Hint.class.getName(), hintVideo);

		// Questions
		assertEquals(WRONG_QUESTION_COUNT + Info.class.getName(), 9, questions.size());

		// Answers-Block
		assertEquals(WRONG_ANSWERSBLOCKS_COUNT + Info.class.getName(), 13, antworten.size());

		// Explanation
		List<Section<Explanation>> explanations = new ArrayList<Section<Explanation>>();
		Sections.findSuccessorsOfType(info, Explanation.class, explanations);
		assertEquals(WRONG_EXPLICATION_COUNT + Info.class.getName(), 11, explanations.size());

		Section<SubblockMarkupContent> sContent =
			Sections.findSuccessor(explanations.get(0), SubblockMarkupContent.class);

		List<Section<PlainText>> expPlains = new ArrayList<Section<PlainText>>();
		Sections.findSuccessorsOfType(sContent, PlainText.class, 1, expPlains);
		//		Sections.findSuccessorsOfType(hint, PlainText.class, hintPlains);
		assertEquals("Wrong number of PlainText in " + Hint.class.getName(), 3, expPlains.size());
		text = hintPlains.get(0).getOriginalText().trim();
		assertEquals(WRONG_STRING + Hint.class.getName(),"Geben Sie mindestens 2 Probleme an.", text);
		text = hintPlains.get(2).getOriginalText().trim();
		assertEquals(WRONG_STRING + Hint.class.getName(),"Testtext.", text);

		bild = Sections.findChildOfType(sContent, Image.class);
		assertNotNull(PICTURE_MISSING + Explanation.class.getName(), bild);
		video = Sections.findChildOfType(sContent, Video.class);
		assertNotNull(VIDEO_MISSING + Explanation.class.getName(), video);

	}

	@Test
	public void testQuestions() {
		Section<SubblockMarkupContent> c = null;
		Section<QuestionWeight> weight = null;
		Section<QuestionType> type = null;
		Section<QuestionText> text = null;
		for (Section<Question> q : questions) {
			c = Sections.findSuccessor(q, SubblockMarkupContent.class);

			weight = Sections.findSuccessor(c, QuestionWeight.class);
			assertNotNull(QuestionWeight.class.getName() +" null", weight);
			assertEquals(QuestionWeight.class.getName() +" not equals 1", "1", weight.getOriginalText().trim());

			type = Sections.findSuccessor(c, QuestionType.class);
			assertNotNull(QuestionType.class.getName() +" null", type);

			text = Sections.findSuccessor(c, QuestionText.class);
			assertNotNull(QuestionText.class.getName() +" null", text);
			assertFalse(QuestionText.class.getName() +" is empty string", text.getOriginalText().trim().equals(""));
		}
	}

	@Test
	public void testAnswerBlocks() {
		Section<AnswerLine> line = null;
		List<Section<AnswerLine>> lines = new ArrayList<Section<AnswerLine>>();
		boolean actual;
		Section<AnswerMark> mark = null;
		Section<AnswerText> text = null;
		Section<AnswerExplanation> expl = null;

		for (Section<AnswersBlock> a : antworten) {
			lines.clear();
			Sections.findSuccessorsOfType(a, AnswerLine.class, lines);

			actual = (lines.size() <= 0);
			assertFalse("No "+ AnswerLine.class.getName() +" found", actual);

			for (Section<AnswerLine> l : lines) {
				mark = Sections.findSuccessor(l, AnswerMark.class);
				assertNotNull(MISSING + AnswerMark.class.getName(), mark);

				text = Sections.findSuccessor(l, AnswerText.class);
				assertNotNull(MISSING + AnswerText.class.getName(), text);
				assertFalse("Empty " + AnswerText.class.getName(), text.getOriginalText().trim().equals(""));

				expl = Sections.findSuccessor(l, AnswerExplanation.class);
				assertNotNull(MISSING + AnswerExplanation.class.getName(), expl);
				assertFalse("Empty " + AnswerExplanation.class.getName(),
						expl.getOriginalText().trim().equals(""));
			}

		}

		// Test one AnswersBlock which has
		// OrderMark/Praefix/Postfix/Heading
		Section<AnswersBlock> block = antworten.get(5);
		Section<AnswerOrderMark> mar = Sections.findSuccessor(block, AnswerOrderMark.class);
		Section<Praefix> praefix = Sections.findSuccessor(block, Praefix.class);
		Section<Postfix> postfix = Sections.findSuccessor(block, Postfix.class);
		Section<Heading> heading = Sections.findSuccessor(block, Heading.class);

		assertNotNull(MISSING + AnswerOrderMark.class.getName(), mar);
		assertNotNull(MISSING + Praefix.class.getName(), praefix);
		assertNotNull(MISSING + Postfix.class.getName(), postfix);
		assertNotNull(MISSING + Heading.class.getName(), heading);

		assertEquals(WRONG_STRING + AnswerOrderMark.class.getName(),
				"{2}", mar.getOriginalText().trim());
		assertEquals(WRONG_STRING + Praefix.class.getName(),
				"Präfix: weiblich", praefix.getOriginalText().trim());
		assertEquals(WRONG_STRING + Postfix.class.getName(),
				"Postfix: %", postfix.getOriginalText().trim());
		assertEquals(WRONG_STRING + Heading.class.getName(),
				"Überschrift: Überschrift", heading.getOriginalText().trim());

	}


	// We dont have to deeply test here.
	// All deep tests are done for Info.
	@Test
	public void testEvaluation() {
		Section<EvaluationEnd> evalEnd =
			Sections.findSuccessor(eval, EvaluationEnd.class);
		assertNotNull(MISSING + EvaluationEnd.class.getName(), evalEnd);

		List<Section<Question>> questions = new ArrayList<Section<Question>>();
		Sections.findSuccessorsOfType(eval, Question.class, questions);
		assertEquals(WRONG_QUESTION_COUNT + Evaluation.class.getName(), 2, questions.size());

		List<Section<AnswersBlock>> blocks = new ArrayList<Section<AnswersBlock>>();
		Sections.findSuccessorsOfType(eval, AnswersBlock.class, blocks);
		assertEquals(WRONG_ANSWERSBLOCKS_COUNT + Evaluation.class.getName(), 2, blocks.size());


	}


	// TODO MultimediaTypes missing.
	@Test
	public void testClosure() {
		Section<BlockMarkupContent> content =
			Sections.findSuccessor(closure, BlockMarkupContent.class);

		Section<Title> title = Sections.findSuccessor(content, Title.class);
		Section<PlainText> text = Sections.findSuccessor(content, PlainText.class);
		Section<Image> bild = Sections.findSuccessor(content, Image.class);
		Section<Video> video = Sections.findSuccessor(content, Video.class);
		//		Section<Audio> audio = Sections.findSuccessor(content, Audio.class);
		//		Section<Link> link = Sections.findSuccessor(content, Link.class);

		// Not null tests
		assertNotNull(TITLE_MISSING + Closure.class.getName(), title);
		assertNotNull(TEXT_MISSING + Closure.class.getName(), text);
		assertNotNull(PICTURE_MISSING + Closure.class.getName(), bild);
		assertNotNull(VIDEO_MISSING + Closure.class.getName(), video);
		//		assertNotNull(AUDIO_MISSING + Abschluss.class.getName(), audio);
		//		assertNotNull(LINK_MISSING + Abschluss.class.getName(), link);

		// Content tests
		assertEquals(WRONG_STRING  + Closure.class.getName(),
				"Abschlusskommentar", title.getOriginalText().trim());
		assertEquals(WRONG_STRING  + Closure.class.getName(),
				"Ende des Falles!\r\n", text.getOriginalText());

		// MultimediaItems test
		Section<MultimediaItemContent> mContent =
			Sections.findSuccessor(bild, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Closure.class.getName(),
				"ls6logo.gif", mContent.getOriginalText().trim());

		mContent =
			Sections.findSuccessor(video, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Closure.class.getName(),
				"video42.avi", mContent.getOriginalText().trim());
	}

}
