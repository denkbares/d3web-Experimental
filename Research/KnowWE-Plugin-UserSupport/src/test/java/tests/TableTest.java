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
package tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import utils.MyTestArticleManager;
import de.d3web.plugin.test.InitPluginManager;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;


/**
 * 
 * @author Johannes Dienst
 * @created 28.10.2011
 */
public class TableTest extends TestCase {

	private final String TESTSUITEARTICLE = "src/test/resources/TableMarkup.txt";
	Section<KnowWEArticle> articleSec = null;

	@Override
	protected void setUp() throws IOException {
		InitPluginManager.init();
		KnowWEArticle article = MyTestArticleManager.getArticle(TESTSUITEARTICLE);
		articleSec = article.getSection();
	}

	/**
	 * Only tests if there are the right count of TableCells etc.
	 * 
	 * @created 28.10.2011
	 */
	@Test
	public void testTableStructureSimple() {

	}
}
