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

package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.d3web.plugin.test.InitPluginManager;
import de.d3web.we.action.ActionContext;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.SemanticCoreDelegator;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.RootType;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.tagging.TaggingMangler;
import dummies.KnowWETestWikiConnector;

public class GroovySparqlRendererTest {

	private KnowWEEnvironment ke;
	private KnowWEArticleManager am;
	private RootType type;
	private UserActionContext context;
	private TaggingMangler tm;

	@Before
	public void setUp() throws Exception {
		InitPluginManager.init();
		Locale.setDefault(Locale.US);
		/*
		 * Initialise KnowWEEnvironment
		 */
		KnowWEEnvironment.initKnowWE(new KnowWETestWikiConnector());
		ke = KnowWEEnvironment.getInstance();
		am = KnowWEEnvironment.getInstance().getArticleManager("default_web");
		type = ke.getRootType();

		/*
		 * Init first Article
		 */
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "TagTest", type,
				"default_web");

		am.registerArticle(article1);

		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put(KnowWEAttributes.WEB, KnowWEEnvironment.DEFAULT_WEB);
		parameter.put(KnowWEAttributes.USER, "testuser");
		context = new ActionContext("", "", parameter, null, null, null, null);
		tm = TaggingMangler.getInstance();
	}

	@Test
	public void testRenderer() {
		KnowWEArticle article1 = KnowWEArticle.createArticle("", "Tag1", type,
				"default_web");
		KnowWEArticle article2 = KnowWEArticle.createArticle("", "Tag2", type,
				"default_web");

		am.registerArticle(article1);
		am.registerArticle(article2);
		tm.addTag("Tag1", "tag", context);
		tm.addTag("Tag2", "tag", context);

		String querystring = "<sparql render=\"junit\">SELECT ?q \n"
				+ "WHERE {\n" + "?t rdf:object <"
				+ SemanticCoreDelegator.getInstance().getUpper().getLocaleNS()
				+ "tag> .\n" + "?t rdf:predicate ns:hasTag .\n"
				+ "?t rdfs:isDefinedBy ?o .\n" + "?o ns:hasTopic ?q .\n"
				+ "}</sparql>";
		String renderstring = "<groovysparqlrenderer name=\"junit\">return KnowWEUtils.maskHTML(\"hallo\");</groovysparqlrenderer>";
		KnowWEArticle setrenderer = KnowWEArticle.createArticle(renderstring,
				"SetRenderer", type, "default_web");
		am.registerArticle(setrenderer);
		StringBuilder articleString = new StringBuilder();
		setrenderer.getRenderer().render(setrenderer, setrenderer.getSection(),
				context, articleString);
		String setrenderer_result = KnowWEUtils.unmaskHTML(articleString
				.toString());
		String setrenderer_result_should_be = "renderer junit already present";
		assertEquals(setrenderer_result_should_be, setrenderer_result);
		articleString = new StringBuilder();
		KnowWEArticle junitquery = KnowWEArticle.createArticle(querystring, "JunitQuery",
				type, "default_web");
		am.registerArticle(junitquery);
		junitquery.getRenderer().render(junitquery, junitquery.getSection(),
				context, articleString);
		String result_is = KnowWEUtils.unmaskHTML(articleString.toString());
		String result_should_be = "hallo";
		assertEquals(result_should_be, result_is);
	}

	@After
	public void tearDown() {
		am.deleteArticle(am.getArticle("Tag1"));
		am.deleteArticle(am.getArticle("Tag2"));
		ArrayList<String> tags = tm.getAllTags();
		assertEquals(0, tags.size());
	}

}
