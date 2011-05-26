package tests;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import utils.MyTestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.knowwe.casetrain.evaluation.Evaluation;
import de.knowwe.casetrain.info.Info;
import de.knowwe.casetrain.type.Abschluss;
import de.knowwe.casetrain.type.Einleitung;
import de.knowwe.casetrain.type.MetaDaten;


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

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		MyTestArticleManager.getArticle(TESTSUITEARTICLE);
	}

	@Test
	public void testTestCases() {
		KnowWEArticle article =
			MyTestArticleManager.getArticle(TESTSUITEARTICLE);

		Section<KnowWEArticle> articleSec = article.getSection();

		// MetaDaten
		Section<MetaDaten> meta = Sections.findSuccessor(articleSec, MetaDaten.class);
		assertNotNull(missingComponent+MetaDaten.class.getName(), meta);

		// Introduction
		Section<Einleitung> intro = Sections.findSuccessor(articleSec, Einleitung.class);
		assertNotNull(missingComponent+Einleitung.class.getName(), meta);

		// Info
		// OMW and MN are not marked with {1} in Antworten-Block
		Section<Info> info = Sections.findSuccessor(articleSec, Info.class);
		assertNotNull(missingComponent+Info.class.getName(), meta);

		// Evaluation
		Section<Evaluation> eval = Sections.findSuccessor(articleSec, Evaluation.class);
		assertNotNull(missingComponent+Evaluation.class.getName(), meta);

		// Conclusion
		Section<Abschluss> concl = Sections.findSuccessor(articleSec, Abschluss.class);
		assertNotNull(missingComponent+Abschluss.class.getName(), meta);

	}

}
