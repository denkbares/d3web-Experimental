/*
 * Copyright (C) 2010 University Wuerzburg, Computer Science VI
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
package de.knowwe.compile.correction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.wcohen.ss.Levenstein;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;

public class IncrementalTermReferenceCorrectionProvider implements CorrectionProvider {

	@Override
	public List<CorrectionProvider.Suggestion> getSuggestions(Article article, Section<?> section, int threshold) {
		if (!(section.get() instanceof SimpleReference)) {
			return null;
		}

		ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
		Collection<Section<? extends SimpleDefinition>> defs = terminology.getAllTermDefinitions();

		String originalText = section.getText();
		List<CorrectionProvider.Suggestion> suggestions = new LinkedList<CorrectionProvider.Suggestion>();
		Levenstein l = new Levenstein();

		for (Section<? extends SimpleDefinition> def : defs) {
			String termName = KnowWEUtils.getTermIdentifier(def);
			double score = l.score(originalText, termName);
			if (score >= -threshold) {
				suggestions.add(new CorrectionProvider.Suggestion(termName, (int) score));
			}
		}

		return suggestions;
	}
}
