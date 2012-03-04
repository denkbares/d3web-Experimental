package de.knowwe.compile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;

public class EqualStringHazardFilter {

	public static void filter(Collection<Section<? extends KnowledgeUnit>> insert, Collection<Section<? extends KnowledgeUnit>> remove) {
		// NOTE: This weird collection juggling using CompileSections is
		// necessary because the equals of the Sections class itself does not
		// have the characteristics required here.
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
			if (insertSet.contains(next)) {
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
				insert.add((Section<? extends KnowledgeUnit<?>>) compileSection.getSection());
			}
			remove.clear();
			for (CompileSection compileSection : removeSet) {
				remove.add((Section<? extends KnowledgeUnit<?>>) compileSection.getSection());

			}
		}
	}

	public static void filterDefs(Collection<Section<SimpleDefinition>> insert, Collection<Section<SimpleDefinition>> remove) {
		// NOTE: This weird collection juggling using CompileSections is
		// necessary because the equals of the Sections class itself does not
		// have the characteristics required here.
		// All this code only aims to find sections that appear in both
		// collection with the same text (String-equal)
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
				removeIter.remove();
				insertSet.remove(next);

				changes = true;
			}
		}

		// refill original sets with remaining sections
		if (changes) {
			insert.clear();
			for (CompileSection compileSection : insertSet) {
				insert.add((Section<SimpleDefinition>) compileSection.getSection());
			}
			remove.clear();
			for (CompileSection compileSection : removeSet) {
				remove.add((Section<SimpleDefinition>) compileSection.getSection());

			}
		}
	}
}
