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

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.correction.CorrectionToolProvider;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;

public class IncrementalCorrectionToolProvider extends CorrectionToolProvider {

	@Override
	protected boolean hasError(Article article, Section<?> section) {
		boolean error = true;
		if (section.get() instanceof IncrementalTermReference) {
			ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
			error = !terminology.isValid(KnowWEUtils.getTermIdentifier(section));
		}

		return super.hasError(article, section) || error;
	}
}
