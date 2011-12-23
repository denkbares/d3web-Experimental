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
package de.d3web.we.util;

import java.util.ArrayList;
import java.util.Collection;

import de.knowwe.core.compile.TerminologyHandler;
import de.knowwe.core.compile.packaging.PackageRenderUtils;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * @author Johannes Dienst
 * @created 15.12.2011
 */
public class UserSupportUtil {

	/**
	 * 
	 * This method collects all TermDefinitions in the whole system.
	 * 
	 * @created 15.12.2011
	 * @param article
	 * @return
	 */
	public static Collection<Section<? extends TermDefinition>> getTermReferences(KnowWEArticle article)
	{
		TerminologyHandler tH =
				KnowWEUtils.getTerminologyHandler(article.getWeb());

		Collection<String> globalTerms = tH.getAllGlobalTerms();

		Collection<Section<? extends TermDefinition>> toReturn = new ArrayList<Section<? extends TermDefinition>>();
		for (String gT : globalTerms)
		{
			Collection<Section<? extends TermDefinition>> allLocalTermDefs = tH.getAllLocalTermDefs(gT);
			toReturn.addAll(allLocalTermDefs);
		}

		return toReturn;
	}

	/**
	 * 
	 * Collect only the TermDefinitions used by a given Section
	 * 
	 * @created 23.12.2011
	 * @param article
	 * @param markup
	 * @return
	 */
	public static Collection<Section<? extends TermDefinition>> getTermReferencesCompilingArticle(KnowWEArticle article, Section<?> markup)
	{

		StringBuilder content = new StringBuilder();
		KnowWEArticle compilingArticle = PackageRenderUtils.checkArticlesCompiling(article, markup, content);

		TerminologyHandler tH =
				KnowWEUtils.getTerminologyHandler(compilingArticle.getWeb());
		Collection<Section<? extends TermDefinition>> globalTerms = tH.getAllLocalTermDefs(compilingArticle.getTitle());

		return globalTerms;
	}

}
