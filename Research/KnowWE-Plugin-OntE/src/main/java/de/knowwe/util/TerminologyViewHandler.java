/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.util;

import java.util.Collection;
import java.util.Map;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm.Scope;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.user.UserContext;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.termObject.IRIEntityType;

public class TerminologyViewHandler extends AbstractTagHandler{

	public TerminologyViewHandler() {
		super("terminology");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		Collection<String> globalTerms = terminologyHandler.getAllGlobalTerms();
		String result = "<ul>Registered Terms:<br>";
		for (String string : globalTerms) {
			Section<? extends TermDefinition> termDefiningSection = terminologyHandler.getTermDefiningSection(
					article, string, Scope.GLOBAL);
			KnowWEArticle main = KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");
			
			Object object = termDefiningSection.get().getTermObject(main, termDefiningSection);
			String uriString = "no URI";
			if(object instanceof IRIEntityType) {
				uriString = ((IRIEntityType)object).toString();
			}
			result += "<li>"+string+" ("+uriString+")"+"</li>";
			
		}
		result += "</ul>";
		return  KnowWEUtils.maskHTML(result);
	}

}
