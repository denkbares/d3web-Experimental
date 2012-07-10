package de.knowwe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

public class EqualStringHazardFilter {

	private Collection<String> locationDependantKeywords = new HashSet<String>();

	/**
	 * 
	 */
	public EqualStringHazardFilter(Collection<String> locationDependantKeywords) {
		this.locationDependantKeywords = locationDependantKeywords;
	}

	public void filter(Collection<Section<? extends KnowledgeUnit>> insert, Collection<Section<? extends KnowledgeUnit>> remove) {
		// NOTE: This weird collection juggling using CompileSections is
		// necessary because the equals method of the class Section itself does
		// not have the characteristics required here.
		// All this code only aims to find sections that appear in both
		// collection with the same text (String-equal)
		// and remove it from both (once respectively!)

		// transform Section-collections to CompileSection-collections
		Collection<CompileSection> removeSet = new ArrayList<CompileSection>();
		for (Section<? extends KnowledgeUnit> section : remove) {
			removeSet.add(new CompileSection(section));
		}

		Collection<CompileSection> insertSet = new ArrayList<CompileSection>();
		for (Section<? extends KnowledgeUnit> section : insert) {
			insertSet.add(new CompileSection(section));
		}

		boolean changes = false; // for efficiency only

		// find corresponding sections
		Iterator<CompileSection> removeIter = removeSet.iterator();
		while (removeIter.hasNext()) {
			CompileSection next = removeIter.next();
			boolean usesLocationDependantKeyword = containsLocationDependantKeywordReference(next);
			if (!usesLocationDependantKeyword && insertSet.contains(next)) {
				// item found in both sets, removing from both
				removeIter.remove();
				insertSet.remove(next);

				changes = true;
			}
		}

		// refill original sets with remaining sections
		if (changes) {
			insert.clear();
			for (CompileSection compileSection : insertSet) {
				insert.add(Sections.cast(compileSection.getSection(), KnowledgeUnit.class));
			}
			remove.clear();
			for (CompileSection compileSection : removeSet) {
				Section<KnowledgeUnit> castedSection = Sections.cast(compileSection.getSection(),
						KnowledgeUnit.class);
				remove.add(castedSection);

			}
		}
	}

	private boolean containsLocationDependantKeywordReference(CompileSection next) {
		boolean usesLocationDependantKeyword = false;
		for (String keyword : locationDependantKeywords) {
			if (next.getSection().getText().contains(keyword)) {
				Collection<Section<?>> containing = Sections.findSmallestSectionsContaining(
						next.getSection(), keyword);
				// should be one at most
				for (Section<?> section : containing) {
					if (section.get() instanceof IncrementalTermReference
							&& section.getText().trim().equals(keyword)) {
						usesLocationDependantKeyword = true;
					}
				}
			}
		}
		return usesLocationDependantKeyword;
	}

	public void filterDefs(Collection<Section<SimpleDefinition>> insert, Collection<Section<SimpleDefinition>> remove) {
		// NOTE: This weird collection juggling using CompileSections is
		// necessary because the equals of the Sections class itself does not
		// have the characteristics required here.
		// All this code only aims to find sections that appear in both
		// collections with the same text (String-equal)
		// and remove it from both (once respectively!)

		// transform Section-collections to CompileSection-collections
		Collection<CompileSection> removeSet = new ArrayList<CompileSection>();
		for (Section<? extends SimpleDefinition> section : remove) {
			removeSet.add(new CompileSection(section));
		}

		Collection<CompileSection> insertSet = new ArrayList<CompileSection>();
		for (Section<? extends SimpleDefinition> section : insert) {
			insertSet.add(new CompileSection(section));
		}

		boolean changes = false; // for efficiency only

		// find corresponding sections
		Iterator<CompileSection> removeIter = removeSet.iterator();
		while (removeIter.hasNext()) {
			CompileSection next = removeIter.next();
			if (insertSet.contains(next)) {
				// item found in both sets, removing from both

				// special case: dealing with typed termDefs
				// this is a problem if a term with same name is newly as a
				// different type
				// therefore compare SectionTypes of definitions
				Section<? extends Type> section1 = next.getSection();
				// retrieve reference on other object
				// (equals-but-not-really-equal-problem)
				Section<? extends Type> section2 = retrieveSection2(section1, insertSet);
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
			for (CompileSection compileSection : insertSet) {
				insert.add(Sections.cast(compileSection.getSection(), SimpleDefinition.class));
			}
			remove.clear();
			for (CompileSection compileSection : removeSet) {
				remove.add(Sections.cast(compileSection.getSection(), SimpleDefinition.class));

			}
		}
	}

	/**
	 * 
	 * @created 15.05.2012
	 * @param section1
	 * @param insertSet
	 */
	private static Section<? extends Type> retrieveSection2(Section<? extends Type> section1, Collection<CompileSection> insertSet) {
		for (CompileSection compileSection : insertSet) {
			if (compileSection.equals(new CompileSection(section1))) return compileSection.getSection();
		}
		return null;

	}
}
