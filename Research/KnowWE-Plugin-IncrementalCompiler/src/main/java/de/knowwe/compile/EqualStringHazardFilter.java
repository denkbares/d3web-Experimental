package de.knowwe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.LocationDependantKnowledgeUnit;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

public class EqualStringHazardFilter {

	public void filter(Collection<Section<? extends KnowledgeUnit>> insert, Collection<Section<? extends KnowledgeUnit>> remove) {
		// NOTE: This weird collection juggling using CompileSections is
		// necessary because the equals method of the class Section itself does
		// not have the characteristics required here.
		// All this code only aims to find sections that appear in both
		// collection with the same text (String-equal)
		// and remove it from both (once respectively!)

		// transform Section-collections to CompileSection-collections
		Collection<CompileSection<? extends KnowledgeUnit>> removeSet = new ArrayList<CompileSection<? extends KnowledgeUnit>>();
		for (Section<? extends KnowledgeUnit> section : remove) {
			removeSet.add(CompileSection.create(section));
		}

		Collection<CompileSection<? extends KnowledgeUnit>> insertSet = new ArrayList<CompileSection<? extends KnowledgeUnit>>();
		for (Section<? extends KnowledgeUnit> section : insert) {
			insertSet.add(CompileSection.create(section));
		}

		boolean changes = false; // for efficiency only

		// find corresponding sections
		Iterator<CompileSection<? extends KnowledgeUnit>> removeIter = removeSet.iterator();
		while (removeIter.hasNext()) {
			CompileSection<?> next = removeIter.next();
			if (insertSet.contains(next)) {
				Section<? extends Type> other = retrieveSection3(next.getSection(), insertSet);
				// check whether the sections are not only textual identical,
				// but also the resolved references (e.g., using the 'this'
				// keyword) are identical
				boolean equalTermNamesSignature = hasEqualResolvedTermNamesSignature(
						next.getSection(), other);
				if (equalTermNamesSignature) {

					// there are knowledge units which compile at different
					// locations to different knowledge when containing the same
					// section text
					if (!(other.get() instanceof LocationDependantKnowledgeUnit)) {
						// item found in both sets, removing from both
						removeIter.remove();
						insertSet.remove(next);

						changes = true;
					}
				}
			}
		}

		// refill original sets with remaining sections
		if (changes) {
			insert.clear();
			for (CompileSection<? extends KnowledgeUnit> compileSection : insertSet) {
				insert.add(compileSection.getSection());
			}
			remove.clear();
			for (CompileSection<? extends KnowledgeUnit> compileSection : removeSet) {
				remove.add(compileSection.getSection());
			}
		}
	}

	private boolean hasEqualResolvedTermNamesSignature(Section<?> oldSection, Section<?> other) {
		Section<KnowledgeUnit> castedOldSection = Sections.cast(oldSection,
				KnowledgeUnit.class);
		Section<KnowledgeUnit> castedOtherSection = Sections.cast(other,
				KnowledgeUnit.class);

		// old external refs are stored as they cannot be retrieved any more
		Collection<Section<? extends Term>> allOldReferences = new HashSet<Section<? extends Term>>();
		@SuppressWarnings("unchecked")
		Collection<Section<? extends Term>> oldExternalReferences = (Collection<Section<? extends Term>>) (KnowWEUtils.getStoredObject(
				oldSection, IncrementalCompiler.EXTERNAL_REFERENCES_OF_KNOWLEDGEUNIT));
		if (oldExternalReferences != null) {
			allOldReferences.addAll(oldExternalReferences);
		}

		// fetching only the local references as external ones are incorrect
		// (retrieval is based on new article version)
		Collection<Section<Term>> allLocalReferencesOfCompilationUnit = CompileUtils.getAllLocalReferencesOfCompilationUnit(castedOldSection);
		// merge both sets
		allOldReferences.addAll(allLocalReferencesOfCompilationUnit);

		// for the new unit the normal way of fetching all refs can be used.

		Collection<Section<? extends Term>> referencesOfOtherSection = new HashSet<Section<? extends Term>>();
		KnowledgeUnitCompileScript<?> compileScript = castedOtherSection.get().getCompileScript();
		if (compileScript != null) {
			referencesOfOtherSection = compileScript.getAllReferencesOfKnowledgeUnit(
					castedOtherSection);
		}
		else {
			Logger.getLogger(this.getClass().getName()).warning(
					"KnowledgeUnit without CompileScript: " + castedOtherSection.toString());
		}

		Collection<String> termNamesOther = resolveTermNames(referencesOfOtherSection);
		Collection<String> termNamesOld = resolveTermNames(allOldReferences);

		return termNamesOld.equals(termNamesOther);
	}

	/**
	 * 
	 * @created 10.07.2012
	 * @param referencesOfSection
	 * @return
	 */
	private Collection<String> resolveTermNames(Collection<Section<? extends Term>> referencesOfSection2) {
		Collection<String> termNames = new HashSet<String>();

		for (Section<? extends Term> section : referencesOfSection2) {
			termNames.add(section.get().getTermName(section));
		}
		return termNames;
	}

	@SuppressWarnings("rawtypes")
	public void filterDefs(Collection<Section<IncrementalTermDefinition>> insert, Collection<Section<IncrementalTermDefinition>> remove) {
		// NOTE: This weird collection juggling using CompileSections is
		// necessary because the equals of the Sections class itself does not
		// have the characteristics required here.
		// All this code only aims to find sections that appear in both
		// collections with the same text (String-equal)
		// and remove it from both (once respectively!)

		// transform Section-collections to CompileSection-collections
		Collection<CompileSection<IncrementalTermDefinition>> removeSet = new ArrayList<CompileSection<IncrementalTermDefinition>>();
		for (Section<IncrementalTermDefinition> section : remove) {
			removeSet.add(new CompileSection<IncrementalTermDefinition>(section));
		}

		Collection<CompileSection<IncrementalTermDefinition>> insertSet = new ArrayList<CompileSection<IncrementalTermDefinition>>();
		for (Section<IncrementalTermDefinition> section : insert) {
			insertSet.add(new CompileSection<IncrementalTermDefinition>(section));
		}

		boolean changes = false; // for efficiency only

		// find corresponding sections
		Iterator<CompileSection<IncrementalTermDefinition>> removeIter = removeSet.iterator();
		while (removeIter.hasNext()) {
			CompileSection<IncrementalTermDefinition> next = removeIter.next();
			if (insertSet.contains(next)) {
				// item found in both sets, removing from both

				// special case: dealing with typed termDefs
				// this is a problem if a term with same name is newly as a
				// different type
				// therefore compare SectionTypes of definitions
				Section<IncrementalTermDefinition> section1 = next.getSection();
				// retrieve reference on other object
				// (equals-but-not-really-equal-problem)
				Section<?> section2 = retrieveSection2(section1, insertSet);
				if (section1.get() instanceof TypedTermDefinition) {
					if (section2.get() instanceof TypedTermDefinition) {
						if (section1.get().getClass().equals(section2.get().getClass())) {
							removeIter.remove();
							insertSet.remove(next);
							changes = true;
						}
						// else do noting
					}
					// else do noting
				}
				else if (section2.get() instanceof TypedTermDefinition) {
					// if section2 is but section1 was not --> do not remove
					// because they are obviously different
				}
				else {
					// common case
					removeIter.remove();
					insertSet.remove(next);

					changes = true;
				}

			}
		}

		// refill original sets with remaining sections
		if (changes) {
			insert.clear();
			for (CompileSection<IncrementalTermDefinition> compileSection : insertSet) {
				insert.add(compileSection.getSection());
			}
			remove.clear();
			for (CompileSection<IncrementalTermDefinition> compileSection : removeSet) {
				remove.add(compileSection.getSection());
			}
		}
	}

	private static <T extends Type> Section<? extends Type> retrieveSection2(Section<?> section1, Collection<CompileSection<T>> insertSet) {
		CompileSection<?> toFind = CompileSection.create(section1);
		for (CompileSection<?> compileSection : insertSet) {
			if (compileSection.equals(toFind)) {
				return compileSection.getSection();
			}
		}
		return null;
	}

	private static <T> Section<? extends Type> retrieveSection3(Section<?> section1, Collection<CompileSection<? extends T>> insertSet) {
		CompileSection<?> toFind = CompileSection.create(section1);
		for (CompileSection<?> compileSection : insertSet) {
			if (compileSection.equals(toFind)) {
				return compileSection.getSection();
			}
		}
		return null;
	}
}
