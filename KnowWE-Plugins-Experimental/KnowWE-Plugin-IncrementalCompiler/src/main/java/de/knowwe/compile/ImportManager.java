package de.knowwe.compile;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.denkbares.strings.Identifier;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * 
 * @author Jochen, Stefan Mark
 * @created 14.12.2011
 */
public class ImportManager {

	private static final Map<Section<? extends AbstractType>, Set<Section<?>>> imports = new HashMap<>();

	private static final Map<Section<? extends AbstractType>, Set<Section<?>>> justImported = new HashMap<>();
	private static final Map<Section<? extends AbstractType>, Set<Section<?>>> justRemoved = new HashMap<>();

	public static void addImport(Section<? extends AbstractType> key, Set<Section<?>> terms) {
		imports.put(key, terms);
		justImported.put(key, terms);
	}

	public static void addImport(Section<? extends AbstractType> key, Section<?> term) {
		if (!imports.containsKey(key)) {
			imports.put(key, new HashSet<>());
		}
		imports.get(key).add(term);

		if (!justImported.containsKey(key)) {
			justImported.put(key, new HashSet<>());
		}
		justImported.get(key).add(term);
	}

	public static void removeImport(Section<? extends AbstractType> key) {
		justRemoved.put(key, imports.get(key));
		imports.remove(key);
	}

	public static Map<Section<? extends AbstractType>, Set<Section<?>>> fetchNewImports() {
		Map<Section<? extends AbstractType>, Set<Section<?>>> result =
				new HashMap<>();
		result.putAll(justImported);
		justImported.clear();
		return result;
	}

	public static Map<Section<? extends AbstractType>, Set<Section<?>>> fetchRemovedImports() {
		Map<Section<? extends AbstractType>, Set<Section<?>>> result =
				new HashMap<>();
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

	public static Set<Section<?>> getImports(Section<? extends AbstractType> key) {
		return Collections.unmodifiableSet(imports.get(key));
	}

	public static Section<? extends AbstractType> resolveImportSection(Identifier termIdentifier) {
		for (Section<? extends AbstractType> importSec : imports.keySet()) {
			for (Section<?> section : imports.get(importSec)) {
				if (KnowWEUtils.getTermIdentifier(section).toString().equals(
						termIdentifier.toString())) {
					return importSec;
				}
			}
		}
		return null;
	}

}
