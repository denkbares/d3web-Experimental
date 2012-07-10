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
import java.util.Set;

import de.d3web.plugin.Extension;
import de.d3web.plugin.PluginManager;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.ComplexDefinitionWithTypeConstraints;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.TypeRestrictedReference;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.compile.terminology.TerminologyExtension;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.AssertSingleTermDefinitionHandler;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.event.KDOMCreatedEvent;
import de.knowwe.plugin.Plugins;

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

	private EqualStringHazardFilter hazardFilter = null;

	private Collection<Section<? extends KnowledgeUnit>> potentiallyNewKnowledgeSlices = new HashSet<Section<? extends KnowledgeUnit>>();
	private Collection<Section<? extends KnowledgeUnit>> knowledgeSlicesToRemove = new HashSet<Section<? extends KnowledgeUnit>>();

	// TODO: how to implement singleton plugin?
	private static IncrementalCompiler instance;

	public static IncrementalCompiler getInstance() {
		if (instance == null) instance = new IncrementalCompiler();
		return instance;
	}

	public IncrementalCompiler() {
		// TODO: implement singleton properly
		instance = this;

		Set<String> keywords = new HashSet<String>();
		keywords.add("this");
		this.hazardFilter = new EqualStringHazardFilter(keywords);

		/*
		 * This term is for the tests only TODO: remove when tests are adapted
		 */
		Section<PreDefinedTerm> subclassDef =
				Section.createSection("subclassof",
						new PreDefinedTerm(), null);
		terminology.addPredefinedObject(subclassDef);
		terminology.registerTermDefinition(subclassDef);
		/*
		 * END term for testing
		 */

		// extension point for plugins defining predefined terminology

		// extension point for plugins defining predefined terminology
		Extension[] exts = PluginManager.getInstance().getExtensions(
				Plugins.EXTENDED_PLUGIN_ID,
				Plugins.EXTENDED_POINT_TERMINOLOGY);
		for (Extension extension : exts) {
			Object o = extension.getSingleton();
			if (o instanceof TerminologyExtension) {
				registerTerminology(((TerminologyExtension) o));
			}
		}

	}

	/**
	 * 
	 * 
	 * @created 14.12.2011
	 * @param key
	 * @param termIdentifier
	 */
	public void registerImportedTerminology(Section<? extends AbstractType> key, TermIdentifier termIdentifier) {
		Section<ImportedTerm> importedSection = Section.createSection(
				termIdentifier.getLastPathElement(), new ImportedTerm(), null);
		terminology.addImportedObject(importedSection);
		terminology.registerTermDefinition(importedSection);
		ImportManager.addImport(key, importedSection);
	}

	/**
	 * 
	 * 
	 * @created 14.12.2011
	 * @param key
	 */
	public void deregisterImportedTerminology(Section<? extends AbstractType> key) {

		Set<Section<?>> imports = ImportManager.getImports(key);
		Iterator<Section<?>> it = imports.iterator();

		while (it.hasNext()) {
			Section<?> termIdentifier = it.next();
			terminology.deregisterTermDefinition(termIdentifier);
			terminology.removeImportedObject(KnowWEUtils.getTermIdentifier(termIdentifier));
		}
		ImportManager.removeImport(key);
	}

	private void registerTerminology(TerminologyExtension terminologyExtension) {
		String[] termNames = terminologyExtension.getTermNames();
		for (String string : termNames) {
			Section<PreDefinedTerm> predefinedTermname =
					Section.createSection(string,
							new PreDefinedTerm(), null);
			terminology.addPredefinedObject(predefinedTermname);
			terminology.registerTermDefinition(predefinedTermname);
		}

		// Section<PreDefinedTerm> subclassDef =
		// Section.createSection("subclassof",
		// new PreDefinedTerm(), null);
		// terminology.addPredefinedObject(subclassDef);
		// terminology.registerTermDefinition(subclassDef);

	}

	class PreDefinedTerm extends SimpleDefinition {

		public PreDefinedTerm() {
			super(TermRegistrationScope.GLOBAL, String.class);
			this.addSubtreeHandler(Priority.HIGH, new AssertSingleTermDefinitionHandler(
					TermRegistrationScope.GLOBAL));
		}

	}

	class ImportedTerm extends SimpleDefinition {

		public ImportedTerm() {
			super(TermRegistrationScope.GLOBAL, String.class);
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

	@SuppressWarnings("rawtypes")
	private void compileChanges(KDOMCreatedEvent event) {
		Article modifiedArticle = event.getArticle();
		Collection<Section<? extends Type>> newSectionsNotReused = CompileUtils.findSectionsNotReused(modifiedArticle);
		Article lastVersionOfArticle = modifiedArticle.getLastVersionOfArticle();
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

		// check new knowledge units
		Collection<Section<? extends KnowledgeUnit>> createdknowledge = CompileUtils.filterKnowledgeUnits(newSectionsNotReused);
		Collection<Section<? extends KnowledgeUnit>> deletedknowledge = CompileUtils.filterKnowledgeUnits(oldSectionsNotReused);

		// filter resource-delta for equal sections
		hazardFilter.filter(createdknowledge, deletedknowledge);

		for (Section<? extends KnowledgeUnit> section : createdknowledge) {

			this.potentiallyNewKnowledgeSlices.add(section);
		}

		// check deleted knowledge units

		// for each check whether it had been valid in the last version,
		// otherwise it does not need to be in the toRemove list,
		// indeed it must not if hazard filter activated!
		for (Section<? extends KnowledgeUnit> section : deletedknowledge) {
			Collection<Section<SimpleReference>> referencesOfCompilationUnit = CompileUtils.getAllReferencesOfCompilationUnit(section);
			boolean valid = true;
			// check for all references if had been valid in old version
			for (Section<SimpleReference> ref : referencesOfCompilationUnit) {
				if (!terminology.wasValidInOldVersion(ref)) {
					valid = false;
				}
			}
			if (valid) {
				this.knowledgeSlicesToRemove.add(section);
			}
		}

		// check deleted objects
		Collection<Section<SimpleDefinition>> deletedObjectDefintions = CompileUtils.filterDefinitions(oldSectionsNotReused);
		Collection<Section<SimpleDefinition>> createdObjectDefintions = CompileUtils.filterDefinitions(newSectionsNotReused);
		hazardFilter.filterDefs(createdObjectDefintions,
				deletedObjectDefintions);
		for (Section<? extends SimpleDefinition> section : deletedObjectDefintions) {
			checkObject(section);
		}

		// check new objects
		for (Section<? extends SimpleDefinition> section : createdObjectDefintions) {
			checkObject(section);
		}

		/* now knowledge slice sets are filled */

		// now check all potentially new knowledge slices for validity
		Iterator<Section<? extends KnowledgeUnit>> compilationUnitIterator = potentiallyNewKnowledgeSlices.iterator();
		while (compilationUnitIterator.hasNext()) {
			Section<? extends KnowledgeUnit> section = compilationUnitIterator.next();
			KnowledgeUnitCompileScript compileScript = section.get().getCompileScript();
			if (compileScript != null) {
				Collection<Section<?>> refs = compileScript.getAllReferencesOfKnowledgeUnit(
						section);
				if (refs != null) {
					for (Section<?> ref : refs) {
						if (!terminology.isValid(KnowWEUtils.getTermIdentifier(ref))) {
							compilationUnitIterator.remove();
							break;
						}
						// check Types of referenced objects here
						if (ref.get() instanceof TypeRestrictedReference) {
							Section<TypeRestrictedReference> trRef = Sections.cast(ref,
									TypeRestrictedReference.class);
							if (trRef.get().checkTypeConstraints(trRef) == false) {
								compilationUnitIterator.remove();
								break;

							}
						}
					}
				}
			}
		}

		// run hazard-filter filtering knowledge being inserted and removed
		// right afterwards
		hazardFilter(potentiallyNewKnowledgeSlices, knowledgeSlicesToRemove);

		// finally create knowledge
		for (Section<? extends KnowledgeUnit> section : potentiallyNewKnowledgeSlices) {
			// System.out.println("Inserting Knowledge Slice: " +
			// section.toString());
			KnowledgeUnitCompileScript script = section.get().getCompileScript();
			if (script != null) {
				script.insertIntoRepository(section);
			}
		}

		// and remove knowledge
		for (Section<? extends KnowledgeUnit> section : knowledgeSlicesToRemove) {
			// System.out.println("Deleting Knowledge Slice: " +
			// section.toString());
			KnowledgeUnitCompileScript script = section.get().getCompileScript();
			if (script != null) {
				script.deleteFromRepository(section);
			}
		}

		if (!ImportManager.fetchNewImports().isEmpty()
				|| !ImportManager.fetchRemovedImports().isEmpty()) {
			notify(event);
		}

	}

	/**
	 * This filter, aiming to prevent insertion and subsequent removement of an
	 * identical knowledge entity from the repository, is not necessary for
	 * correctness - iff the employed knowledge repository is neutral to
	 * subsequent insert- and remove-operations of identical entities.
	 * 
	 * In principle it is only for efficiency improvement and might be left
	 * empty. However, implementation might cause problems if equality of
	 * sections is not dealt with carefully.
	 * 
	 * TODO: ExtensionPoint HazardFilter ?
	 * 
	 * @created 04.03.2012
	 * @param potentiallyNewKnowledgeSlices2
	 * @param knowledgeSlicesToRemove2
	 */
	private void hazardFilter(Collection<Section<? extends KnowledgeUnit>> potentiallyNewKnowledgeSlices2, Collection<Section<? extends KnowledgeUnit>> knowledgeSlicesToRemove2) {

		hazardFilter.filter(potentiallyNewKnowledgeSlices2,
				knowledgeSlicesToRemove2);
	}

	private void checkObject(Section<? extends SimpleDefinition> section) {
		if (hasValidDefinition(section)) {
			resolveRecursively(section);
		}
		else {
			removeRecursively(section);
		}

	}

	private void removeRecursively(Section<? extends SimpleTerm> section) {
		if (terminology.wasValidInOldVersion(section)) {
			terminology.removeFromValidObjects(section);
			Collection<Section<? extends KnowledgeUnit>> referencingSlices = terminology.getReferencingSlices(section);
			knowledgeSlicesToRemove.addAll(referencingSlices);

			// recursion for complex definitions
			Collection<Section<? extends ComplexDefinition>> referencingDefs = terminology.getReferencingDefinitions(section);
			for (Section<? extends ComplexDefinition> ref : referencingDefs) {
				removeRecursively(Sections.findSuccessor(ref,
						SimpleDefinition.class));
			}
		}

	}

	private void resolveRecursively(Section<? extends SimpleDefinition> section) {
		if (hasValidDefinition(section)) {
			// we cannot check on "!terminology.wasValidInOldVersion(section)"
			// here,
			// as it might occur that we have to rehabilitate a definition which
			// was already valid in the last version, but was temporarily
			// invalidated by the previous iterations of THIS compilation run

			// if (!terminology.wasValidInOldVersion(section)) {

			terminology.addToValidObjects(section);
			Collection<Section<? extends KnowledgeUnit>> referencingSlices = terminology.getReferencingSlices(section);
			potentiallyNewKnowledgeSlices.addAll(referencingSlices);

			// recursion for complex definitions
			Collection<Section<? extends ComplexDefinition>> referencingDefs = terminology.getReferencingDefinitions(section);
			for (Section<? extends ComplexDefinition> ref : referencingDefs) {
				Section<SimpleDefinition> def = Sections.findChildOfType(ref,
						SimpleDefinition.class);

				// beware of infinite recursion due to self recursive
				// definitions
				if (KnowWEUtils.getTermIdentifier(def).equals(
						KnowWEUtils.getTermIdentifier(section))) {
					// chicken-egg problem cursing infinite recursion
					// TODO: error handling !?
					// System.out.println("chicken-egg!!!");
				}
				else {
					// System.out.println("resolving recursivly: "
					// + def.get().getTermIdentifier(def));
					resolveRecursively(def);
				}
			}

			// }
		}

	}

	private boolean hasValidDefinition(Section<?> section) {
		TermIdentifier termIdentifier = KnowWEUtils.getTermIdentifier(section);
		return hasValidDefinition(termIdentifier);

	}

	public boolean hasValidDefinition(TermIdentifier termIdentifier) {

		// System.out.println("CheckDefinition: " + termIdentifier);
		Collection<Message> messages = checkDefinition(termIdentifier);
		for (Message kdomReportMessage : messages) {
			if (kdomReportMessage.getType() == Message.Type.ERROR) {
				// System.out.println("FALSE");
				return false;
			}
		}
		return true;
	}

	public Collection<Message> checkDefinition(TermIdentifier termIdentifier) {
		Collection<Message> messages = new ArrayList<Message>();

		Collection<Section<? extends SimpleDefinition>> termDefiningSections = terminology.getTermDefinitions(termIdentifier);
		if (termDefiningSections.size() == 0) {
			messages.add(Messages.noSuchObjectError(termIdentifier.toString()));
			return messages;
		}

		if (termDefiningSections.size() > 1) {
			if (terminology.isPredefinedObject(termIdentifier)) {
				messages.add(Messages.warning("This is a predefined term. " +
						"Concurrent definitions are existing but will be ignored!"));
				return messages;
			}
			if (terminology.isImportedObject(termIdentifier)) {
				messages.add(Messages.warning("This is a imported term. Concurrent definitions are existing but will be ignored!"));
				return messages;
			}
			messages.add(Messages.error("Object has concurrent definitions: "
					+ termIdentifier));
			return messages;
		}

		// check complex definitions here

		// there is exactly one
		Section<? extends SimpleDefinition> def = termDefiningSections.iterator().next();

		Section<ComplexDefinition> complexDef = Sections.findAncestorOfType(def,
				ComplexDefinition.class);
		// all references of this complexDef (if existing) need to be in the set
		// of valid objects
		if (complexDef != null) {
			Collection<Section<SimpleReference>> allReferencesOfComplexDefinition = complexDef.get().getAllReferences(
					complexDef);
			for (Section<SimpleReference> ref : allReferencesOfComplexDefinition) {
				String errorMsg = "ComplexDefinition has dependency error: ";
				// if one reference is not defined
				TermIdentifier termIdentifier2 = KnowWEUtils.getTermIdentifier(ref);
				if ((!terminology.isValid(termIdentifier2))) {
					// System.out.println("dependency error");
					messages.add(Messages.error(errorMsg +
							termIdentifier2.toString()));
					return messages;
				}

				// ADD-ON for type constraints
				if (complexDef.get() instanceof ComplexDefinitionWithTypeConstraints
						&& !((ComplexDefinitionWithTypeConstraints) complexDef.get()).checkTypeConstraints(
								complexDef, ref)) { // or has a wrong type
					messages.add(Messages.error(
							errorMsg + ((ComplexDefinitionWithTypeConstraints) complexDef.get())
											.getProblemMessageForConstraintViolation(complexDef,
													ref) + " :" + termIdentifier2.toString()));
					return messages;
				}
			}
		}

		return messages;
	}

	public ReferenceManager getTerminology() {
		return terminology;
	}

	private void registerNewSectionsInReferenceManager(Collection<Section<? extends Type>> sectionsNotReused) {
		for (Section<? extends Type> section : sectionsNotReused) {
			if (section.get() instanceof SimpleReference) {
				terminology.registerTermReference((Section<? extends SimpleReference>) section);
			}
			if (section.get() instanceof SimpleDefinition) {
				terminology.registerTermDefinition((Section<? extends SimpleDefinition>) section);
			}
		}

	}

	private void deregisterOldsectionsInReferenceManager(Collection<Section<? extends Type>> sectionsNotReused) {
		for (Section<? extends Type> section : sectionsNotReused) {
			if (section.get() instanceof SimpleReference) {
				terminology.deregisterTermReference(section);
			}
			if (section.get() instanceof SimpleDefinition) {
				terminology.deregisterTermDefinition(section);
			}
		}

	}

}
