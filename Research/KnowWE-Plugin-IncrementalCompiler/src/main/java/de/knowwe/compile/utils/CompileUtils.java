/*
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.compile.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;

/**
 * Some util methods needed for the compilation algorithm
 * 
 * @author Jochen
 * @created 09.06.2011
 */
public class CompileUtils {

	/**
	 * 
	 * This method could be improved to higher efficiency by a more elegant data
	 * management for the resource delta
	 * 
	 * @created 09.06.2011
	 * @param parsedArticle
	 * @return
	 */
	public static Collection<Section<? extends Type>> findSectionsNotReused(KnowWEArticle parsedArticle) {
		Collection<Section<? extends Type>> result = new HashSet<Section<? extends Type>>();
		addNonReusedSection(parsedArticle.getSection(), result);

		return result;
	}

	private static void addNonReusedSection(Section<? extends Type> section, Collection<Section<? extends Type>> result) {
		List<Section<? extends Type>> children = section.getChildren();
		for (Section<? extends Type> child : children) {
			if (child.isReusedBy(child.getArticle().getTitle())) {
				// old section
			}
			else {
				result.add(child);
				addNonReusedSection(child, result);
			}
		}

	}

	/**
	 * 
	 * This method could be improved to higher efficiency by a more elegant data
	 * management for the resource delta
	 * 
	 * @created 09.06.2011
	 * @param lastVersionOfArticle
	 * @return
	 */
	public static Collection<Section<? extends Type>> findOldNonReusedSections(KnowWEArticle lastVersionOfArticle) {
		Collection<Section<? extends Type>> result = new HashSet<Section<? extends Type>>();
		if (lastVersionOfArticle == null) return result;
		addOldNonReusedSection(lastVersionOfArticle.getSection(), result,
				lastVersionOfArticle);

		return result;
	}

	private static void addOldNonReusedSection(Section<? extends Type> section, Collection<Section<? extends Type>> result, KnowWEArticle lastVersionOfArticle) {
		List<Section<? extends Type>> children = section.getChildren();
		for (Section<? extends Type> child : children) {
			if (child.getArticle() == lastVersionOfArticle) {
				// old section
				result.add(child);
				addNonReusedSection(child, result);
			}
		}

	}

	/**
	 * 
	 * filters a set of sections and returns only those which are of type
	 * KnowledgeUnit
	 * 
	 * @created 08.06.2011
	 * @param oldSectionsNotReused
	 * @return
	 */
	public static Collection<Section<? extends KnowledgeUnit<?>>> filterKnowledgeUnits(Collection<Section<? extends Type>> oldSectionsNotReused) {
		Collection<Section<? extends KnowledgeUnit<?>>> result = new HashSet<Section<? extends KnowledgeUnit<?>>>();
		for (Section<? extends Type> section : oldSectionsNotReused) {
			if (section.get() instanceof KnowledgeUnit<?>) {
				result.add((Section<? extends KnowledgeUnit<?>>) section);
			}
		}
		return result;
	}

	/**
	 * 
	 * filters a set of sections and returns only those which are of type
	 * TermDefinition
	 * 
	 * @created 08.06.2011
	 * @param oldSectionsNotReused
	 * @return
	 */
	public static Collection<Section<? extends TermDefinition<?>>> filterDefinitions(Collection<Section<? extends Type>> oldSectionsNotReused) {
		Collection<Section<? extends TermDefinition<?>>> result = new HashSet<Section<? extends TermDefinition<?>>>();
		for (Section<? extends Type> section : oldSectionsNotReused) {
			if (section.get() instanceof TermDefinition<?>) {
				result.add((Section<? extends TermDefinition<?>>) section);
			}
		}
		return result;
	}

	public static Collection<Section<TermReference>> getAllReferencesOfCompilationUnit(Section<? extends KnowledgeUnit> section) {
		List<Section<TermReference>> result = new ArrayList<Section<TermReference>>();
		Sections.findSuccessorsOfType(section, TermReference.class, result);
		return result;
	}

	public static <T extends Type> Collection<Section<TermReference>> getAllReferencesOfComplexDefinition(Section<? extends ComplexDefinition> section) {
		List<Section<TermReference>> result = new ArrayList<Section<TermReference>>();
		Sections.findSuccessorsOfType(section, TermReference.class, result);
		return result;
	}
}
