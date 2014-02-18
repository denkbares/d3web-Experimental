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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.d3web.strings.Identifier;
import de.knowwe.compile.ImportManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
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
		Collection<Section<? extends Type>> result = new LinkedList<Section<? extends Type>>();
		addNonReusedSection(parsedArticle.getRootSection(), result);

		Map<Section<? extends AbstractType>, Set<Section<?>>> newImports = ImportManager.fetchNewImports();
		for (Set<Section<?>> anImport : newImports.values()) {
			result.addAll(anImport);
		}
		return result;
	}

	private static void addNonReusedSection(Section<? extends Type> section, Collection<Section<? extends Type>> result) {
		List<Section<?>> children = section.getChildren();
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
		List<Section<?>> children = section.getChildren();
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
	public static Collection<Section<? extends KnowledgeUnit>> filterKnowledgeUnits(Collection<Section<?>> oldSectionsNotReused) {
		Collection<Section<? extends KnowledgeUnit>> result = new HashSet<Section<? extends KnowledgeUnit>>();
		for (Section<? extends Type> section : oldSectionsNotReused) {
			if (section.get() instanceof KnowledgeUnit) {
				Section<KnowledgeUnit> castedSection = Sections.cast(section, KnowledgeUnit.class);
				result.add(castedSection);
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
	@SuppressWarnings("rawtypes")
	public static Collection<Section<IncrementalTermDefinition>> filterDefinitions(Collection<Section<? extends Type>> oldSectionsNotReused) {
		Collection<Section<IncrementalTermDefinition>> result = new HashSet<Section<IncrementalTermDefinition>>();
		for (Section<? extends Type> section : oldSectionsNotReused) {
			if (section.get() instanceof IncrementalTermDefinition<?>) {
				result.add(Sections.cast(section, IncrementalTermDefinition.class));
			}
		}
		return result;
	}

	public static Collection<Section<Term>> getAllLocalReferencesOfCompilationUnit(Section<? extends KnowledgeUnit> section) {
		List<Section<Term>> result = new ArrayList<Section<Term>>();
		Sections.findSuccessorsOfType(section, Term.class, result);
		return result;
	}

	public static Collection<Section<SimpleReference>> getAllReferencesOfComplexDefinition(Section<? extends ComplexDefinition> section) {
		List<Section<SimpleReference>> result = new ArrayList<Section<SimpleReference>>();
		Sections.findSuccessorsOfType(section, SimpleReference.class, result);
		return result;
	}

	public static String createLinkToDefinition(Identifier termIdentifier, UserContext user) {
		Collection<Section<? extends SimpleReference>> termReferences = IncrementalCompiler.getInstance().getTerminology().getTermReferences(
				termIdentifier);
		if (termReferences != null && termReferences.size() > 0) {
			return createLinkToDefinition(termReferences.iterator().next(),
					termIdentifier.getLastPathElement(), user);
		}

		Collection<Section<? extends SimpleDefinition>> termDefinitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				termIdentifier);
		if (termDefinitions != null && termDefinitions.size() > 0) {
			return createLinkToDefinition(termDefinitions.iterator().next(), user);
		}

		return null;
	}

	public static String createLinkToDefinition(Section<?> section, UserContext user) {
		return createLinkToDef(section, KnowWEUtils.getTermIdentifier(section).toString(), user);

	}

	public static String createLinkToDefinition(Section<?> section, String linktext, UserContext user) {

		Collection<Section<? extends SimpleDefinition>> definitions = IncrementalCompiler.getInstance().getTerminology().getTermDefinitions(
				KnowWEUtils.getTermIdentifier(section));
		if (definitions != null && definitions.size() > 0) {

			return createLinkToDef(definitions.iterator().next(), linktext, user);

		}

		return linktext;

	}

	protected static String createLinkToDef(Section<?> definition, String linktext, UserContext user) {
		Article defArticle = definition.getArticle();

		if (defArticle == null) {
			return linktext; // predefined/imported/undefined Term !?
		}
		String defArticleName = defArticle.getTitle();
		RenderResult link = new RenderResult(user);
		link.appendHtml("<a href='Wiki.jsp?page=");
		link.append(defArticleName);
		link.append("#");
		link.append(definition.getID());

		link.appendHtml("'>");
		link.append(linktext);
		link.appendHtml("</a>");
		return link.toStringRaw();

	}
}
