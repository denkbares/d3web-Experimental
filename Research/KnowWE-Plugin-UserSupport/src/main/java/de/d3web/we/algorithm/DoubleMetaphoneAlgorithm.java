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
package de.d3web.we.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;

import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author Johannes Dienst
 * @created 04.10.2011
 */
public class DoubleMetaphoneAlgorithm implements MatchingAlgorithm {

	@Override
	public List<Suggestion> getMatches(int maxCount, String toMatch
			,Collection<Section<? extends TermDefinition>> localTermMatches) {

		DoubleMetaphone dM = new DoubleMetaphone();
		List<Suggestion> suggestions = new ArrayList<Suggestion>();

		for (Section<? extends TermDefinition> match : localTermMatches) {
			if (dM.isDoubleMetaphoneEqual(toMatch, match.getOriginalText())) {
				suggestions.add(new Suggestion(match.getText(), 0));
				if (suggestions.size() >= maxCount) break;
			}
		}

		return suggestions;
	}

}