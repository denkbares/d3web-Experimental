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
import de.knowwe.casetrain.info.Antworten;
import de.knowwe.casetrain.info.Erklaerung;
import de.knowwe.casetrain.info.Frage;
import de.knowwe.casetrain.info.Hinweis;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.type.Abschluss;
import de.knowwe.casetrain.type.Einleitung;
import de.knowwe.casetrain.type.MetaDaten;
import de.knowwe.casetrain.type.MetaLine;
import de.knowwe.casetrain.type.general.BlockMarkupContent;
import de.knowwe.casetrain.type.general.Title;
import de.knowwe.casetrain.type.multimedia.Bild;
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
 * @author Johannes Dienst
 * @created 20.05.2011
 */
public class CaseTrainArticleTest extends TestCase {

	private final String TESTSUITEARTICLE = "src/test/resources/CaseTrainWikipage.txt";

	private final String missingComponent = "Missing component: ";
	private Section<KnowWEArticle> articleSec;
	private Section<MetaDaten> meta;
	private Section<Einleitung> intro;
	private Section<Info> info;
	private Section<Evaluation> eval;
	private Section<Abschluss> closure;

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

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEArticle article = MyTestArticleManager.getArticle(TESTSUITEARTICLE);
		articleSec = article.getSection();
		meta = Sections.findSuccessor(articleSec, MetaDaten.class);
		intro = Sections.findSuccessor(articleSec, Einleitung.class);
		info = Sections.findSuccessor(articleSec, Info.class);
		eval = Sections.findSuccessor(articleSec, Evaluation.class);
		closure = Sections.findSuccessor(articleSec, Abschluss.class);
	}

	@Test
	public void testTestCases() {

		// MetaDaten
		assertNotNull(missingComponent+MetaDaten.class.getName(), meta);

		// Introduction
		assertNotNull(missingComponent+Einleitung.class.getName(), intro);

		// Info
		assertNotNull(missingComponent+Info.class.getName(), info);

		// Evaluation
		assertNotNull(missingComponent+Evaluation.class.getName(), eval);

		// Conclusion
		assertNotNull(missingComponent+Abschluss.class.getName(), closure);

	}

	@Test
	public void testMetaDaten() {
		Section<BlockMarkupContent> content =
			Sections.findSuccessor(meta, BlockMarkupContent.class);
		List<Section<MetaLine>> children = Sections.findChildrenOfType(content, MetaLine.class);
		assertEquals("Wrong Meta-Attribute-Count", 25, children.size());
	}

	// TODO MultimediaTypes missing.
	@Test
	public void testIntroduction() {
		Section<BlockMarkupContent> content =
			Sections.findSuccessor(intro, BlockMarkupContent.class);
		Section<Title> title = Sections.findSuccessor(content, Title.class);
		Section<PlainText> text = Sections.findSuccessor(content, PlainText.class);
		Section<Bild> bild = Sections.findSuccessor(content, Bild.class);
		Section<Video> video = Sections.findSuccessor(content, Video.class);
		//		Section<Audio> audio = Sections.findSuccessor(content, Audio.class);
		//		Section<Link> link = Sections.findSuccessor(content, Link.class);

		// Not null tests
		assertNotNull(TITLE_MISSING + Einleitung.class.getName(), title);
		assertNotNull(TEXT_MISSING + Einleitung.class.getName(), text);
		assertNotNull(PICTURE_MISSING + Einleitung.class.getName(), bild);
		assertNotNull(VIDEO_MISSING + Einleitung.class.getName(), video);
		//		assertNotNull(AUDIO_MISSING + Einleitung.class.getName(), audio);
		//		assertNotNull(LINK_MISSING + Einleitung.class.getName(), link);

		// Content tests
		assertEquals(WRONG_STRING  + Einleitung.class.getName(),
				"Titel der Einleitung", title.getOriginalText().trim());
		assertEquals(WRONG_STRING  + Einleitung.class.getName(),
				"Text der Einleitung\r\n", text.getOriginalText());

		// MultimediaItems test
		Section<MultimediaItemContent> mContent =
			Sections.findSuccessor(bild, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Einleitung.class.getName(),
				"ls6logo.gif", mContent.getOriginalText().trim());

		mContent =
			Sections.findSuccessor(video, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Einleitung.class.getName(),
				"video42.avi", mContent.getOriginalText().trim());
	}

	@Test
	public void testInfo() {

		Section<BlockMarkupContent> content =
			Sections.findSuccessor(info, BlockMarkupContent.class);

		//		List<Section<Bild>> pics = new ArrayList<Section<Bild>>();
		Section<Bild> bild = Sections.findChildOfType(content, Bild.class);
		assertNotNull("No picture in Info", bild);
		//		assertEquals(WRONG_PIC_COUNT + Info.class.getName(), 1, pics.size());

		//		List<Section<Video>> vids = new ArrayList<Section<Video>>();
		Section<Video> video = Sections.findChildOfType(content, Video.class);
		//		Sections.findSuccessorsOfType(info, Video.class, vids);
		assertNotNull("No video in Info", video);
		//		assertEquals(WRONG_VIDEO_COUNT + Info.class.getName(), 1, vids.size());

		List<Section<Hinweis>> hints = new ArrayList<Section<Hinweis>>();
		Sections.findSuccessorsOfType(info, Hinweis.class, hints);
		assertEquals(WRONG_HINT_COUNT + Info.class.getName(), 11, hints.size());

		// Test the first Hint
		Section<Hinweis> hint = hints.get(0);
		List<Section<PlainText>> hintPlains = new ArrayList<Section<PlainText>>();
		Sections.findSuccessorsOfType(hint, PlainText.class, 2, hintPlains);
		//		Sections.findSuccessorsOfType(hint, PlainText.class, hintPlains);
		assertEquals("Wrong number of PlainText in " + Hinweis.class.getName(), 3, hintPlains.size());
		String text = hintPlains.get(0).getOriginalText().trim();
		assertEquals(WRONG_STRING + Hinweis.class.getName(),"Geben Sie mindestens 2 Probleme an.", text);
		text = hintPlains.get(2).getOriginalText().trim();
		assertEquals(WRONG_STRING + Hinweis.class.getName(),"Testtext.", text);

		// Multimedia-items
		Section<Bild> hintPic = Sections.findSuccessor(hint, Bild.class);
		assertNotNull(PICTURE_MISSING + Hinweis.class.getName(), hintPic);
		Section<Video> hintVideo = Sections.findSuccessor(hint, Video.class);
		assertNotNull(VIDEO_MISSING + Hinweis.class.getName(), hintVideo);

		List<Section<Frage>> questions = new ArrayList<Section<Frage>>();
		Sections.findSuccessorsOfType(info, Frage.class, questions);
		assertEquals(WRONG_QUESTION_COUNT + Info.class.getName(), 9, questions.size());

		List<Section<Antworten>> antworten = new ArrayList<Section<Antworten>>();
		Sections.findSuccessorsOfType(info, Antworten.class, antworten);
		assertEquals(WRONG_ANSWERSBLOCKS_COUNT + Info.class.getName(), 13, antworten.size());

		List<Section<Erklaerung>> explications = new ArrayList<Section<Erklaerung>>();
		Sections.findSuccessorsOfType(info, Erklaerung.class, explications);
		assertEquals(WRONG_EXPLICATION_COUNT + Info.class.getName(), 11, explications.size());

	}

	//
	//	@Test
	//	public void testEvaluation() {
	//
	//	}
	//

	// TODO MultimediaTypes missing.
	@Test
	public void testClosure() {
		Section<BlockMarkupContent> content =
			Sections.findSuccessor(closure, BlockMarkupContent.class);

		Section<Title> title = Sections.findSuccessor(content, Title.class);
		Section<PlainText> text = Sections.findSuccessor(content, PlainText.class);
		Section<Bild> bild = Sections.findSuccessor(content, Bild.class);
		Section<Video> video = Sections.findSuccessor(content, Video.class);
		//		Section<Audio> audio = Sections.findSuccessor(content, Audio.class);
		//		Section<Link> link = Sections.findSuccessor(content, Link.class);

		// Not null tests
		assertNotNull(TITLE_MISSING + Abschluss.class.getName(), title);
		assertNotNull(TEXT_MISSING + Abschluss.class.getName(), text);
		assertNotNull(PICTURE_MISSING + Abschluss.class.getName(), bild);
		assertNotNull(VIDEO_MISSING + Abschluss.class.getName(), video);
		//		assertNotNull(AUDIO_MISSING + Abschluss.class.getName(), audio);
		//		assertNotNull(LINK_MISSING + Abschluss.class.getName(), link);

		// Content tests
		assertEquals(WRONG_STRING  + Abschluss.class.getName(),
				"Abschlusskommentar", title.getOriginalText().trim());
		assertEquals(WRONG_STRING  + Abschluss.class.getName(),
				"Ende des Falles!\r\n", text.getOriginalText());

		// MultimediaItems test
		Section<MultimediaItemContent> mContent =
			Sections.findSuccessor(bild, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Abschluss.class.getName(),
				"ls6logo.gif", mContent.getOriginalText().trim());

		mContent =
			Sections.findSuccessor(video, MultimediaItemContent.class);
		assertEquals(WRONG_STRING  + Abschluss.class.getName(),
				"video42.avi", mContent.getOriginalText().trim());
	}

}
