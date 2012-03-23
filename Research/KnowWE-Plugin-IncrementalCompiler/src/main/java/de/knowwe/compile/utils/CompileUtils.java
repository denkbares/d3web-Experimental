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
import java.util.Map;
import java.util.Set;

import de.knowwe.compile.ImportManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

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
	public static Collection<Section<? extends Type>> findSectionsNotReused(Article parsedArticle) {
		Collection<Section<? extends Type>> result = new HashSet<Section<? extends Type>>();
		addNonReusedSection(parsedArticle.getRootSection(), result);

		Map<Section<? extends AbstractType>, Set<Section<?>>> newImports = ImportManager.fetchNewImports();
		for (Set<Section<?>> anImport : newImports.values()) {
			result.addAll(anImport);
		}
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
	public static Collection<Section<? extends Type>> findOldNonReusedSections(Article lastVersionOfArticle) {
		Collection<Section<? extends Type>> result = new HashSet<Section<? extends Type>>();
		if (lastVersionOfArticle == null) return result;
		addOldNonReusedSection(lastVersionOfArticle.getRootSection(), result,
				lastVersionOfArticle);

		Map<Section<? extends AbstractType>, Set<Section<?>>> removedImports = ImportManager.fetchRemovedImports();
		for (Set<Section<?>> removed : removedImports.values()) {
			result.addAll(removed);
		}

		return result;
	}

	private static void addOldNonReusedSection(Section<? extends Type> section, Collection<Section<? extends Type>> result, Article lastVersionOfArticle) {
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
	public static Collection<Section<? extends KnowledgeUnit>> filterKnowledgeUnits(Collection<Section<? extends Type>> oldSectionsNotReused) {
		Collection<Section<? extends KnowledgeUnit>> result = new HashSet<Section<? extends KnowledgeUnit>>();
		for (Section<? extends Type> section : oldSectionsNotReused) {
			if (section.get() instanceof KnowledgeUnit<?>) {
				result.add((Section<? extends KnowledgeUnit>) section);
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
	@SuppressWarnings("unchecked")
	public static Collection<Section<SimpleDefinition>> filterDefinitions(Collection<Section<? extends Type>> oldSectionsNotReused) {
		Collection<Section<SimpleDefinition>> result = new HashSet<Section<SimpleDefinition>>();
		for (Section<? extends Type> section : oldSectionsNotReused) {
			if (section.get() instanceof SimpleDefinition) {
				result.add((Section<SimpleDefinition>) section);
			}
		}
		return result;
	}

	public static Collection<Section<SimpleReference>> getAllReferencesOfCompilationUnit(Section<? extends KnowledgeUnit> section) {
		List<Section<SimpleReference>> result = new ArrayList<Section<SimpleReference>>();
		Sections.findSuccessorsOfType(section, SimpleReference.class, result);
		return result;
	}

	public static <T extends Type> Collection<Section<SimpleReference>> getAllReferencesOfComplexDefinition(Section<? extends ComplexDefinition> section) {
		List<Section<SimpleReference>> result = new ArrayList<Section<SimpleReference>>();
		Sections.findSuccessorsOfType(section, SimpleReference.class, result);
		return result;
	}

	public static String createLinkToDefinition(String termname) {
		Collection<Section<? extends SimpleReference>> termReferences = IncrementalCompiler.getInstance().getTerminology().getTermReferences(
				termname);
		if (termReferences != null && termReferences.size() > 0) {
			return createLinkToDefinition(termReferences.iterator().next(), termname);
		}

		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				termname);
		if (termDefinitions != null && termDefinitions.size() > 0) {
			return createLinkToDefinition(termDefinitions.iterator().next());
		}

		return null;
	}

	public static String createLinkToDefinition(Section<?> section) {
		return createLinkToDef(section, KnowWEUtils.getTermIdentifier(section));

	}

	public static String createLinkToDefinition(Section<?> section, String linktext) {

		Collection<Section<? extends SimpleDefinition>> definitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				KnowWEUtils.getTermIdentifier(section));
		if (definitions != null && definitions.size() > 0) {

			return createLinkToDef(definitions.iterator().next(), linktext);

		}

		return linktext;

	}

	protected static String createLinkToDef(Section<?> definition, String linktext) {
		Article defArticle = definition.getArticle();

		if (defArticle == null) {
			return linktext; // predefined/imported/undefined Term !?
		}
		String defArticleName = defArticle.getTitle();
		StringBuffer link = new StringBuffer();
		link.append(KnowWEUtils.maskHTML("<a href='Wiki.jsp?page="));
		link.append(defArticleName);
		link.append("#");
		link.append(definition.getID());

		link.append(KnowWEUtils.maskHTML("'>"));
		link.append(linktext);
		link.append(KnowWEUtils.maskHTML("</a>"));
		return link.toString();

	}
}
