/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.compile.utils;

import java.util.Collection;

import de.d3web.strings.Identifier;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.Environment;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author jochenreutelshofer
 * @created 24.04.2013
 */
public class IncrementalCompilerLinkToTermDefinitionProvider implements de.knowwe.core.utils.LinkToTermDefinitionProvider {

	@Override
	public String getLinkToTermDefinition(Identifier name, String masterArticle) {
		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				name);
		String targetArticle = name.toExternalForm();
		if (termDefinitions.size() > 0) {
			targetArticle = termDefinitions.iterator().next().getTitle();
		}
		else {
			return null;
		}

		return createBaseURL() + "?page=" + targetArticle;
	}

	/**
	 * 
	 * @created 29.11.2012
	 * @return
	 */
	public String createBaseURL() {
		return Environment.getInstance().getWikiConnector().getBaseUrl() + "Wiki.jsp";
	}

}
