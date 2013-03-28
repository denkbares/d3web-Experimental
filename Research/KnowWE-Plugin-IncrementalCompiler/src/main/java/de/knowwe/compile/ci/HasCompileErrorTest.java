/*
 * Copyright (C) 2012 denkbares GmbH
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
package de.knowwe.compile.ci;

import java.util.ArrayList;
import java.util.List;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.Message;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author jochenreutelshofer
 * @created 19.12.2012
 */
public class HasCompileErrorTest extends AbstractTest<Article> {

	@Override
	public Message execute(Article testObject, String[] args, String[]... ignores) throws InterruptedException {
		ReferenceManager referenceManager = IncrementalCompiler.getInstance().getTerminology();
		Section<RootType> rootSection = testObject.getRootSection();
		List<String> erroneousTerms = extractedErroneousTermNames(referenceManager,
				Sections.findSuccessorsOfType(rootSection,
						IncrementalTermDefinition.class));

		erroneousTerms.addAll(extractedErroneousTermNames(referenceManager,
				Sections.findSuccessorsOfType(rootSection,
						IncrementalTermReference.class)));

		if (erroneousTerms.size() == 0) {
			return Message.SUCCESS;
		}
		else {
			return new Message(Message.Type.FAILURE, "The following term have compile errors: "
					+ Strings.concat(", ", erroneousTerms));
		}

	}

	private <T extends Term> List<String> extractedErroneousTermNames(ReferenceManager referenceManager, List<Section<T>> terms) {
		List<String> erroneousTerms = new ArrayList<String>();
		for (Section<T> def : terms) {
			if (!referenceManager.isValid(def.get().getTermIdentifier(def))) {
				erroneousTerms.add(def.get().getTermName(def));
			}
		}
		return erroneousTerms;
	}

	@Override
	public Class<Article> getTestObjectClass() {
		return Article.class;
	}

	@Override
	public String getDescription() {
		return "Checks for Compile errors of the incremental compiler.";
	}

}
