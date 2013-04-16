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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import de.d3web.strings.Identifier;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * 
 * Auxiliary data structure for the incremental compilation algorithm. Here the
 * dependency graph of knowledge slices and terms is stored. It is updated using
 * the register and deregister methods before each compilation step.
 * 
 * @author Jochen
 * @created 09.06.2011
 */
public class ReferenceManager {

	private final Map<Identifier, TermDefinitionInformation> validObjects = new HashMap<Identifier, TermDefinitionInformation>();
	private final Map<Identifier, Section<?>> validPredefinedObjects = new HashMap<Identifier, Section<?>>();

	private final Map<Identifier, Section<?>> validImportedObjects = new HashMap<Identifier, Section<?>>();

	private Map<Identifier, TermDefinitionInformation> validObjectsOld = new HashMap<Identifier, TermDefinitionInformation>();

	private final Map<Identifier, Set<Section<? extends SimpleReference>>> allReferences = new HashMap<Identifier, Set<Section<? extends SimpleReference>>>();

	public Map<Identifier, Set<Section<? extends SimpleReference>>> getAllReferences() {
		return allReferences;
	}

	private final Map<Identifier, Set<Section<? extends SimpleDefinition>>> allDefinitions = new HashMap<Identifier, Set<Section<? extends SimpleDefinition>>>();

	public void newCompilationStep() {
		validObjectsOld = new HashMap<Identifier, TermDefinitionInformation>();
		validObjectsOld.putAll(validObjects);
	}

	public void addToValidObjects(Section<? extends SimpleDefinition> s) {
		// store (generic) type-compiler-information along with the definition
		TermDefinitionInformation termDefinitionInformation = new TermDefinitionInformation(
				s);
		if (s.get() instanceof TypedTermDefinition) {
			Object typedTermInformation = ((TypedTermDefinition) s.get()).getTypedTermInformation(s);
			termDefinitionInformation.setTypeInformation(typedTermInformation);
		}
		validObjects.put(KnowWEUtils.getTermIdentifier(s), termDefinitionInformation);
	}

	public Object getDefinitionInformationForValidTerm(Identifier termIdentifier) {
		if (validObjects.containsKey(termIdentifier)) {
			return validObjects.get(termIdentifier).getTypeInformation();
		}
		return null;
	}

	/**
	 * This is for predefined terms only! They cannot be removed and will last
	 * in the system forever. Use this in initialization only!
	 * 
	 * @created 10.06.2011
	 * @param s
	 */
	public void addPredefinedObject(Section<? extends SimpleDefinition> s) {

		Identifier termIdentifier = KnowWEUtils.getTermIdentifier(s);
		if (validPredefinedObjects.containsKey(termIdentifier)) {
			throw new IllegalArgumentException(
					"Term is already registered as predefined term. Check plugin configuration: "
							+ termIdentifier.toString());
		}
		else {
			validPredefinedObjects.put(termIdentifier, s);
			registerTermDefinition(s);
		}
	}

	public boolean isPredefinedObject(Identifier termIdentifer) {
		return validPredefinedObjects.containsKey(termIdentifer);
	}

	/**
	 * Adds an identifier to the known imported terms. Use this when importing
	 * identifiers from a non local location.
	 * 
	 * @created 01.12.2011
	 * @param Section<?> s
	 */
	public void addImportedObject(Section<?> s) {
		validImportedObjects.put(KnowWEUtils.getTermIdentifier(s), s);
	}

	/**
	 * Checks weather the identifier is an imported on. Use this when importing
	 * identifiers from a non local location.
	 * 
	 * @created 01.12.2011
	 * @param String termIdentifer
	 * @return boolean TRUE is imported term, FALSE otherwise
	 */
	public boolean isImportedObject(Identifier termIdentifer) {
		return validImportedObjects.containsKey(termIdentifer);
	}

	public boolean isLocalObject(Identifier termIdentifer) {
		return validObjects.containsKey(termIdentifer);
	}

	public void removeImportedObject(Identifier termIdentifier) {
		if (validImportedObjects.containsKey(termIdentifier)) {
			validImportedObjects.remove(termIdentifier);
		}
	}

	public void removeFromValidObjects(Section<?> s) {
		validObjects.remove(KnowWEUtils.getTermIdentifier(s));
		validImportedObjects.remove(KnowWEUtils.getTermIdentifier(s));
	}

	public boolean isValid(Identifier termIdentifier) {
		return validObjects.containsKey(termIdentifier)
				|| validPredefinedObjects.containsKey(termIdentifier)
				|| validImportedObjects.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(Identifier termIdentifier) {
		return validObjectsOld.containsKey(termIdentifier)
				|| validPredefinedObjects.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(Section<?> s) {
		return wasValidInOldVersion(KnowWEUtils.getTermIdentifier(s));
	}

	public Collection<Section<? extends KnowledgeUnit>> getReferencingSlices(Section<? extends Term> section) {
		Collection<Section<? extends KnowledgeUnit>> result = new HashSet<Section<? extends KnowledgeUnit>>();
		Identifier termIdentifier = KnowWEUtils.getTermIdentifier(section);
		Set<Section<? extends Term>> termSet = new HashSet<Section<? extends Term>>();
		Set<Section<? extends SimpleReference>> refs = allReferences.get(termIdentifier);
		if (refs != null) {
			termSet.addAll(refs);
		}
		termSet.add(section);
		for (Section<?> ref : termSet) {
			Section<KnowledgeUnit> compilationUnit = Sections.findAncestorOfType(ref,
					KnowledgeUnit.class);
			if (compilationUnit != null) {
				result.add(compilationUnit);
			}

			// we need to additionally find all knowledge units that refer
			// externally to this reference
			// TODO: find better/faster way to do this - this brute force style
			// is
			// awkward
			// maybe it can be stored and cached somehow ?
			Section<RootType> rootSection = ref.getArticle().getRootSection();
			List<Section<KnowledgeUnit>> allKnowledgeUnitsOfArticle = Sections.findSuccessorsOfType(
					rootSection, KnowledgeUnit.class);
			for (Section<KnowledgeUnit> knowledge : allKnowledgeUnitsOfArticle) {
				KnowledgeUnitCompileScript<Type> compileScript = knowledge.get().getCompileScript();
				if (compileScript == null) {
					Logger.getLogger(this.getClass().getName()).warning(
							"KnowledgeUnit without compile script: " + knowledge.toString());
					continue;
				}
				Collection<Section<? extends Term>> allReferencesOfKnowledgeUnit = compileScript.getAllReferencesOfKnowledgeUnit(
						knowledge);
				for (Section<? extends Term> sliceRef : allReferencesOfKnowledgeUnit) {
					Identifier sliceRefTermIdentifier = KnowWEUtils.getTermIdentifier(sliceRef);
					if (sliceRefTermIdentifier.equals(termIdentifier)) {
						result.add(knowledge);
						break;
					}
				}
			}
		}
		return result;

	}

	public void registerTermReference(Section<? extends SimpleReference> section) {
		Identifier identifier = KnowWEUtils.getTermIdentifier(section);
		if (allReferences.containsKey(identifier)) {
			allReferences.get(identifier).add(section);
		}
		else {
			HashSet<Section<? extends SimpleReference>> set = new HashSet<Section<? extends SimpleReference>>();
			set.add(section);
			allReferences.put(identifier, set);
		}
	}

	public void deregisterTermReference(Section<?> section) {
		Identifier identifier = KnowWEUtils.getTermIdentifier(section);
		if (allReferences.containsKey(identifier)) {
			allReferences.get(identifier).remove(section);
		}
	}

	public void registerTermDefinition(Section<? extends SimpleDefinition> section) {
		Identifier identifier = KnowWEUtils.getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).add(section);
		}
		else {
			Set<Section<? extends SimpleDefinition>> set = new HashSet<Section<? extends SimpleDefinition>>();
			set.add(section);
			allDefinitions.put(identifier, set);
		}
	}

	public void deregisterTermDefinition(Section<?> section) {
		Identifier identifier = KnowWEUtils.getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).remove(section);
		}
	}

	public Collection<Section<? extends SimpleDefinition>> getTermDefinitions(Section<?> section) {
		Identifier identifier = KnowWEUtils.getTermIdentifier(section);
		return getTermDefinitions(identifier);
	}

	public Collection<Section<? extends SimpleDefinition>> getTermDefinitions(Identifier identifier) {
		if (allDefinitions.containsKey(identifier)) {
			return allDefinitions.get(identifier);
		}
		return new ArrayList<Section<? extends SimpleDefinition>>();
	}

	public Collection<Section<? extends SimpleReference>> getTermReferences(Identifier identifier) {
		if (allReferences.containsKey(identifier)) {
			return allReferences.get(identifier);
		}
		return new ArrayList<Section<? extends SimpleReference>>();
	}

	public Collection<Section<? extends ComplexDefinition>> getReferencingDefinitions(Section<?> section) {
		Collection<Section<? extends ComplexDefinition>> result = new HashSet<Section<? extends ComplexDefinition>>();
		Identifier termIdentifier = KnowWEUtils.getTermIdentifier(section);
		Set<Section<? extends SimpleReference>> refSet = allReferences.get(termIdentifier);
		if (refSet == null) return result;
		for (Section<?> ref : refSet) {
			Section<ComplexDefinition> compilationUnit = Sections.findAncestorOfType(ref,
					ComplexDefinition.class);
			if (compilationUnit != null) {
				result.add(compilationUnit);
			}
		}
		return result;
	}

	public Collection<Section<? extends SimpleDefinition>> getAllTermDefinitions() {
		Set<Section<? extends SimpleDefinition>> result = new HashSet<Section<? extends SimpleDefinition>>();
		for (Identifier termIdentifier : validObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termIdentifier);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		for (Identifier termIdentifier : validPredefinedObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termIdentifier);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}

		for (Identifier termIdentifier : validImportedObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termIdentifier);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		return result;
	}

}

class TermDefinitionInformation {

	private Object typeInformation;

	public TermDefinitionInformation(Object o) {
		this.typeInformation = o;
	}

	public Object getTypeInformation() {
		return typeInformation;
	}

	public void setTypeInformation(Object typeInformation) {
		this.typeInformation = typeInformation;
	}

}
