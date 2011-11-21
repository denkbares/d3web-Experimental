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
package de.d3web.we.correction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.d3web.we.dialog.algorithm.DialogComponent;
import de.d3web.we.dialog.algorithm.Suggestion;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.TerminologyHandler;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;


/**
 * 
 * There is only this
 * 
 * @author Johannes Dienst
 * @created 15.09.2011
 */
public class ApproximateCorrectionProviderImpl implements ApproximateCorrectionProvider {


	@Override
	public List<Suggestion> getSuggestions(
			KnowWEArticle article, Section<?> section, int threshold) {

		TerminologyHandler terminologyHandler =
			KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);

		// Get all Terms from Terminology used
		// TODO get right KnowledgeBase
		Collection<Section<? extends TermDefinition>> localTermMatches =
			terminologyHandler.getAllLocalTermDefs("Demo - Master");

		String originalText = section.getOriginalText();

		List<Suggestion> suggestions = new LinkedList<Suggestion>();

		DialogComponent.getInstance();


		return suggestions;
	}

}
