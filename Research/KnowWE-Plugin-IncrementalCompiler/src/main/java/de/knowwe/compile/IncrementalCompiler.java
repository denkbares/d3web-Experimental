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

package de.knowwe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.d3web.we.event.Event;
import de.d3web.we.event.EventListener;
import de.d3web.we.event.KDOMCreatedEvent;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.utils.CompileUtils;

/**
 * This class implements the incremental compilation algorithm as proposed by
 * the paper at KCAP2011 entitled 'Incremental Compilation of Knowledge
 * Documents for Markup-based Closed-World Authoring'
 * 
 * @author Jochen
 * @created 09.06.2011
 */
@SuppressWarnings("unchecked")
public class IncrementalCompiler implements EventListener {

	private final ReferenceManager terminology = new ReferenceManager();

	private Collection<Section<? extends KnowledgeUnit>> potentiallyNewKnowledgeSlices = new HashSet<Section<? extends KnowledgeUnit>>();
	private Collection<Section<? extends KnowledgeUnit>> knowledgeSlicesToRemove = new HashSet<Section<? extends KnowledgeUnit>>();

	// TODO: how to implement singleton plugin?
	private static IncrementalCompiler instance;

	public static IncrementalCompiler getInstance() {
		return instance;
	}

	public IncrementalCompiler() {
		// TODO: implement singleton properly
		instance = this;

		// TODO: add extension point for plugins defining predefined terminology
//		Section<PreDefinedTerm> subclassDef = Section.createSection("subclassof",
//				new PreDefinedTerm(), null);
//		terminology.addPredefinedObject(subclassDef);
//		terminology.registerTermDefinition(subclassDef);
	}

	class PreDefinedTerm extends TermDefinition<String> {

		public PreDefinedTerm() {
			super(String.class);
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText();
		}

	}

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		List<Class<? extends Event>> list = new ArrayList<Class<? extends Event>>();
		list.add(KDOMCreatedEvent.class);
		return list;
	}

	@Override
	public void notify(Event event) {
		if (event instanceof KDOMCreatedEvent) {
			compileChanges((KDOMCreatedEvent) event);
		}

	}

	private void compileChanges(KDOMCreatedEvent event) {
		KnowWEArticle modifiedArticle = event.getArticle();
		Collection<Section<? extends Type>> newSectionsNotReused = CompileUtils.findSectionsNotReused(modifiedArticle);
		KnowWEArticle lastVersionOfArticle = modifiedArticle.getLastVersionOfArticle();
		Collection<Section<? extends Type>> oldSectionsNotReused = CompileUtils.findOldNonReusedSections(lastVersionOfArticle);

		// reset knowledge slice sets
		potentiallyNewKnowledgeSlices = new HashSet<Section<? extends KnowledgeUnit>>();
		knowledgeSlicesToRemove = new HashSet<Section<? extends KnowledgeUnit>>();

		// update references --- necessary??
		registerNewSectionsInReferenceManager(newSectionsNotReused);
		deregisterOldsectionsInReferenceManager(oldSectionsNotReused);

		// set validObjects to oldValidObjects to initialize new compilation
		// iteration
		terminology.newCompilationStep();

		/* now dependency graph is updated */

		// check deleted knowledge units
		Collection<Section<? extends KnowledgeUnit<?>>> deletedknowledge = CompileUtils.filterKnowledgeUnits(oldSectionsNotReused);
		this.knowledgeSlicesToRemove.addAll(deletedknowledge);

		// check deleted knowledge units
		Collection<Section<? extends KnowledgeUnit<?>>> createdknowledge = CompileUtils.filterKnowledgeUnits(newSectionsNotReused);
		this.potentiallyNewKnowledgeSlices.addAll(createdknowledge);

		// check deleted objects
		Collection<Section<? extends TermDefinition<?>>> deletedObjectDefintions = CompileUtils.filterDefinitions(oldSectionsNotReused);
		for (Section<? extends TermDefinition<?>> section : deletedObjectDefintions) {
			checkObject(section);
		}

		// check new objects
		Collection<Section<? extends TermDefinition<?>>> createdObjectDefintions = CompileUtils.filterDefinitions(newSectionsNotReused);
		for (Section<? extends TermDefinition<?>> section : createdObjectDefintions) {
			checkObject(section);
		}

		/* now knowledge slice sets are filled */

		// now check all potentially new knowledge slices for validity
		Iterator<Section<? extends KnowledgeUnit>> compilationUnitIterator = potentiallyNewKnowledgeSlices.iterator();
		while (compilationUnitIterator.hasNext()) {
			Section<? extends KnowledgeUnit> section = compilationUnitIterator.next();
			Collection<Section<TermReference>> refs = section.get().getAllReferencesOfKnowledgeUnit(
					section);
			for (Section<TermReference> ref : refs) {
				if (!terminology.isValid(ref.get().getTermIdentifier(ref))) {
					// compilation unit not valid => remove
					compilationUnitIterator.remove();
					break;
				}
			}
		}

		/*
		 * BEGIN The following is not part of the original algorithm. However,
		 * it helps to get along with predefined terms, that are constantly
		 * valid in the system (even if user adds additional definitions for
		 * this term)
		 */
		// now check all (potentially) destroyed knowledge slice, whether they
		// might still be valid due to predefined terms
		Iterator<Section<? extends KnowledgeUnit>> removeKnowledgeUnitIterator = this.knowledgeSlicesToRemove.iterator();
		while (removeKnowledgeUnitIterator.hasNext()) {
			Section<? extends KnowledgeUnit> section = removeKnowledgeUnitIterator.next();
			Collection<Section<TermReference>> refs = section.get().getAllReferencesOfKnowledgeUnit(
					section);
			boolean valid = true;
			for (Section<TermReference> ref : refs) {
				if (!terminology.isValid(ref.get().getTermIdentifier(ref))) {
					// compilation unit definitely invalid (stays in list)
					valid = false;
				}

			}
			if (valid) {
				removeKnowledgeUnitIterator.remove();
			}
		}
		/*
		 * END
		 */

		// run hazard-filter filtering knowledge being inserted and removed
		// right afterwards
		hazardFilter(potentiallyNewKnowledgeSlices, knowledgeSlicesToRemove);

		// finally create knowledge
		for (Section<? extends KnowledgeUnit> section : potentiallyNewKnowledgeSlices) {
			section.get().insertIntoRepository(section);
		}

		// and remove knowledge
		for (Section<? extends KnowledgeUnit> section : knowledgeSlicesToRemove) {
			section.get().deleteFromRepository(section);
		}

	}

	private void hazardFilter(Collection<Section<? extends KnowledgeUnit>> potentiallyNewKnowledgeSlices2, Collection<Section<? extends KnowledgeUnit>> knowledgeSlicesToRemove2) {
		// TODO implement hazard-filter (optional!!)
		// this is efficiency improvement is only relevant in very special cases
	}

	private void checkObject(Section<? extends TermDefinition<?>> section) {
		if (hasValidDefinition(section)) {
			resolveRecursively(section);
		}
		else {
			removeRecursively(section);
		}

	}

	private void removeRecursively(Section<? extends TermDefinition> section) {
		if (terminology.wasValidInOldVersion(section)) {
			terminology.removeFromValidObjects(section);
			Collection<Section<? extends KnowledgeUnit>> referencingSlices = terminology.getReferencingSlices(section);
			knowledgeSlicesToRemove.addAll(referencingSlices);

			// recursion for complex definitions
			Collection<Section<? extends ComplexDefinition>> referencingDefs = terminology.getReferencingDefinitions(section);
			for (Section<? extends ComplexDefinition> ref : referencingDefs) {
				removeRecursively(Sections.findSuccessor(ref,
						TermDefinition.class));
			}
		}

	}

	private void resolveRecursively(Section<? extends TermDefinition> section) {
		if (hasValidDefinition(section)) {
			if (!terminology.wasValidInOldVersion(section)) {
				terminology.addToValidObjects(section);
				Collection<Section<? extends KnowledgeUnit>> referencingSlices = terminology.getReferencingSlices(section);
				potentiallyNewKnowledgeSlices.addAll(referencingSlices);

				// recursion for complex definitions
				Collection<Section<? extends ComplexDefinition>> referencingDefs = terminology.getReferencingDefinitions(section);
				for (Section<? extends ComplexDefinition> ref : referencingDefs) {
					resolveRecursively(Sections.findChildOfType(ref,
							TermDefinition.class));
				}

			}
		}

	}

	private boolean hasValidDefinition(Section<? extends TermDefinition> section) {
		String termIdentifier = section.get().getTermIdentifier(
				section);
		return hasValidDefinition(termIdentifier);

	}

	public boolean hasValidDefinition(String termIdentifier) {
		Collection<Section<? extends TermDefinition>> termDefiningSections = terminology.getTermDefinitions(termIdentifier);
		if (termDefiningSections.size() != 1) return false;

		// check complex definitions here

		// there is exactly one
		Section<? extends TermDefinition> def = termDefiningSections.iterator().next();

		Section<ComplexDefinition> complexDef = Sections.findAncestorOfType(def,
				ComplexDefinition.class);
		// all references of this complexDef (if existing) need to be in the set
		// of valid objects
		if (complexDef != null) {
			Collection<Section<TermReference>> allReferencesOfComplexDefinition = CompileUtils.getAllReferencesOfComplexDefinition(complexDef);
			for (Section<TermReference> ref : allReferencesOfComplexDefinition) {
				if (!terminology.isValid(ref.get().getTermIdentifier(ref))) {
					return false;
				}
			}
		}

		return true;
	}

	public ReferenceManager getTerminology() {
		return terminology;
	}

	private void registerNewSectionsInReferenceManager(Collection<Section<? extends Type>> sectionsNotReused) {
		for (Section<? extends Type> section : sectionsNotReused) {
			if (section.get() instanceof TermReference) {
				terminology.registerTermReference((Section<? extends TermReference>) section);
			}
			if (section.get() instanceof TermDefinition) {
				terminology.registerTermDefinition((Section<? extends TermDefinition>) section);
			}
		}

	}

	private void deregisterOldsectionsInReferenceManager(Collection<Section<? extends Type>> sectionsNotReused) {
		for (Section<? extends Type> section : sectionsNotReused) {
			if (section.get() instanceof TermReference) {
				terminology.deregisterTermReference((Section<? extends TermReference>) section);
			}
			if (section.get() instanceof TermDefinition) {
				terminology.deregisterTermDefinition((Section<? extends TermDefinition>) section);
			}
		}

	}

}
