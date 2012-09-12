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

import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.compile.terminology.TermIdentifier;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
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

	private final Map<TermIdentifier, TermDefinitionInformation> validObjects = new HashMap<TermIdentifier, TermDefinitionInformation>();
	private final Map<TermIdentifier, Section<?>> validPredefinedObjects = new HashMap<TermIdentifier, Section<?>>();

	private final Map<TermIdentifier, Section<?>> validImportedObjects = new HashMap<TermIdentifier, Section<?>>();

	private Map<TermIdentifier, TermDefinitionInformation> validObjectsOld = new HashMap<TermIdentifier, TermDefinitionInformation>();

	private final Map<TermIdentifier, Set<Section<? extends SimpleReference>>> allReferences = new HashMap<TermIdentifier, Set<Section<? extends SimpleReference>>>();

	public Map<TermIdentifier, Set<Section<? extends SimpleReference>>> getAllReferences() {
		return allReferences;
	}

	private final Map<TermIdentifier, Set<Section<? extends SimpleDefinition>>> allDefinitions = new HashMap<TermIdentifier, Set<Section<? extends SimpleDefinition>>>();

	public void newCompilationStep() {
		validObjectsOld = new HashMap<TermIdentifier, TermDefinitionInformation>();
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

	public Object getDefinitionInformationForValidTerm(TermIdentifier termIdentifier) {
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
		
		TermIdentifier termIdentifier = KnowWEUtils.getTermIdentifier(s);
		if(validPredefinedObjects.containsKey(termIdentifier)) {
			throw new IllegalArgumentException("Term is already registered as predefined term. Check plugin configuration: "+termIdentifier.toString());
		} else {
			validPredefinedObjects.put(termIdentifier, s);
			registerTermDefinition(s);
		}
	}

	public boolean isPredefinedObject(TermIdentifier termIdentifer) {
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
	public boolean isImportedObject(TermIdentifier termIdentifer) {
		return validImportedObjects.containsKey(termIdentifer);
	}

	public boolean isLocalObject(TermIdentifier termIdentifer) {
		return validObjects.containsKey(termIdentifer);
	}

	public void removeImportedObject(TermIdentifier termIdentifier) {
		if (validImportedObjects.containsKey(termIdentifier)) {
			validImportedObjects.remove(termIdentifier);
		}
	}

	public void removeFromValidObjects(Section<?> s) {
		validObjects.remove(KnowWEUtils.getTermIdentifier(s));
		validImportedObjects.remove(KnowWEUtils.getTermIdentifier(s));
	}

	public boolean isValid(TermIdentifier termIdentifier) {
		return validObjects.containsKey(termIdentifier)
				|| validPredefinedObjects.containsKey(termIdentifier)
				|| validImportedObjects.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(TermIdentifier termIdentifier) {
		return validObjectsOld.containsKey(termIdentifier)
				|| validPredefinedObjects.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(Section<?> s) {
		return wasValidInOldVersion(KnowWEUtils.getTermIdentifier(s));
	}

	public Collection<Section<? extends KnowledgeUnit>> getReferencingSlices(Section<? extends SimpleTerm> section) {
		Collection<Section<? extends KnowledgeUnit>> result = new HashSet<Section<? extends KnowledgeUnit>>();
		TermIdentifier termIdentifier = KnowWEUtils.getTermIdentifier(section);
		Set<Section<? extends SimpleReference>> refSet = allReferences.get(termIdentifier);
		if (refSet == null) return result;
		for (Section<?> ref : refSet) {
			Section<KnowledgeUnit> compilationUnit = Sections.findAncestorOfType(ref,
					KnowledgeUnit.class);
			if (compilationUnit != null) {
				result.add(compilationUnit);
			}

			// we need to additionally find all knowledge units that refer
			// externally to this reference
			// TODO: find better/faster way to do this - this brute force style is
			// awkward
			// maybe it can be stored and cached somehow ?
			Section<Article> rootSection = ref.getArticle().getRootSection();
			List<Section<KnowledgeUnit>> allKnowledgeUnitsOfArticle = Sections.findSuccessorsOfType(
					rootSection, KnowledgeUnit.class);
			for (Section<KnowledgeUnit> knowledge : allKnowledgeUnitsOfArticle) {
				Collection<Section<? extends SimpleReference>> allReferencesOfKnowledgeUnit = knowledge.get().getCompileScript().getAllReferencesOfKnowledgeUnit(
						knowledge);
				for (Section<? extends SimpleReference> sliceRef : allReferencesOfKnowledgeUnit) {
					TermIdentifier sliceRefTermIdentifier = KnowWEUtils.getTermIdentifier(sliceRef);
					if (sliceRefTermIdentifier.equals(termIdentifier)) {
						result.add(knowledge);
					}
				}
			}
		}
		return result;

	}

	public void registerTermReference(Section<? extends SimpleReference> section) {
		TermIdentifier identifier = KnowWEUtils.getTermIdentifier(section);
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
		TermIdentifier identifier = KnowWEUtils.getTermIdentifier(section);
		if (allReferences.containsKey(identifier)) {
			allReferences.get(identifier).remove(section);
		}
	}

	public void registerTermDefinition(Section<? extends SimpleDefinition> section) {
		TermIdentifier identifier = KnowWEUtils.getTermIdentifier(section);
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
		TermIdentifier identifier = KnowWEUtils.getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).remove(section);
		}
	}

	public Collection<Section<? extends SimpleDefinition>> getTermDefinitions(Section<?> section) {
		TermIdentifier identifier = KnowWEUtils.getTermIdentifier(section);
		return getTermDefinitions(identifier);
	}

	public Collection<Section<? extends SimpleDefinition>> getTermDefinitions(TermIdentifier identifier) {
		if (allDefinitions.containsKey(identifier)) {
			return allDefinitions.get(identifier);
		}
		return new ArrayList<Section<? extends SimpleDefinition>>();
	}

	public Collection<Section<? extends SimpleReference>> getTermReferences(TermIdentifier identifier) {
		if (allReferences.containsKey(identifier)) {
			return allReferences.get(identifier);
		}
		return new ArrayList<Section<? extends SimpleReference>>();
	}

	public Collection<Section<? extends ComplexDefinition>> getReferencingDefinitions(Section<?> section) {
		Collection<Section<? extends ComplexDefinition>> result = new HashSet<Section<? extends ComplexDefinition>>();
		TermIdentifier termIdentifier = KnowWEUtils.getTermIdentifier(section);
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
		for (TermIdentifier termIdentifier : validObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termIdentifier);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		for (TermIdentifier termIdentifier : validPredefinedObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termIdentifier);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}

		for (TermIdentifier termIdentifier : validImportedObjects.keySet()) {
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
