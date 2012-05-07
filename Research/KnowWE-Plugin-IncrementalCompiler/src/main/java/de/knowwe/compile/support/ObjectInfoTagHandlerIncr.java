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
package de.knowwe.compile.support;

import java.util.Collection;
import java.util.Set;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.ReferenceManager;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;

public class ObjectInfoTagHandlerIncr extends de.knowwe.core.taghandler.ObjectInfoTagHandler {

	public ObjectInfoTagHandlerIncr() {
		super("ObjectInfoIncr");
	}

	@Override
	protected void getTermDefinitions(Article currentArticle, TermIdentifier termIdentifier, Set<Section<?>> definitions) {

		ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
		Collection<Section<? extends SimpleDefinition>> termDefinitions = terminology.getTermDefinitions(termIdentifier);
		if (termDefinitions != null) {
			for (Section<?> section : termDefinitions) {
				definitions.add(section);
			}

		}

	}

	@Override
	protected void getTermReferences(Article currentArticle, TermIdentifier termIdentifier, Set<Section<?>> references) {
		ReferenceManager terminology = IncrementalCompiler.getInstance().getTerminology();
		Collection<Section<? extends SimpleReference>> termReferences = terminology.getTermReferences(termIdentifier);
		if (termReferences != null) {
			for (Section<?> section : termReferences) {
				references.add(section);
			}

		}

	}
}
