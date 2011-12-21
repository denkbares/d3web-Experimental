package de.knowwe.compile;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;

/**
 *
 *
 * @author Jochen, Stefan Mark
 * @created 14.12.2011
 */
public class ImportManager {

	private static Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> imports = new HashMap<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>>();

	private static Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> justImported = new HashMap<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>>();
	private static Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> justRemoved = new HashMap<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>>();

	public static void addImport(Section<? extends AbstractType> key, Set<Section<? extends TermDefinition<?>>> terms) {
		imports.put(key, terms);
		justImported.put(key, terms);
	}

	public static void addImport(Section<? extends AbstractType> key, Section<? extends TermDefinition<?>> term) {
		if(!imports.containsKey(key)) {
			imports.put(key, new HashSet<Section<? extends TermDefinition<?>>>());
		}
		imports.get(key).add(term);

		if (!justImported.containsKey(key)) {
			justImported.put(key, new HashSet<Section<? extends TermDefinition<?>>>());
		}
		justImported.get(key).add(term);
	}

	public static void removeImport(Section<? extends AbstractType> key) {
		justRemoved.put(key, imports.get(key));
		imports.remove(key);
	}

	public static Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> fetchNewImports() {
		Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> result = new HashMap<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>>();
		result.putAll(justImported);
		justImported.clear();
		return result;
	}

	public static Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> fetchRemovedImports() {
		Map<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>> result = new HashMap<Section<? extends AbstractType>, Set<Section<? extends TermDefinition<?>>>>();
		result.putAll(justRemoved);
		justRemoved.clear();
		return result;
	}

	public static void clearNewImports() {
		justImported.clear();
	}

	public static void clearRemovedImports() {
		justRemoved.clear();
	}

	public static Set<Section<? extends TermDefinition<?>>> getImports(Section<? extends AbstractType> key) {
		return Collections.unmodifiableSet(imports.get(key));
	}

	public static Section<? extends AbstractType> resolveImportSection(String termIdentifier) {
		for (Section<? extends AbstractType> importSec : imports.keySet()) {
			for (Section<? extends TermDefinition<?>> section : imports.get(importSec)) {
				if (section.getOriginalText().equals(termIdentifier)) {
					return importSec;
				}
			}
		}
		return null;
	}

}
