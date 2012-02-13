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
import java.util.Map;
import java.util.Set;

import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.TypedTermDefinition;
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

	private final Map<String, TermDefinitionInformation> validObjects = new HashMap<String, TermDefinitionInformation>();
	private final Map<String, Section<?>> validPredefinedObjects = new HashMap<String, Section<?>>();

	private final Map<String, Section<?>> validImportedObjects = new HashMap<String, Section<?>>();

	private Map<String, TermDefinitionInformation> validObjectsOld = new HashMap<String, TermDefinitionInformation>();

	private final Map<String, Set<Section<? extends SimpleReference>>> allReferences = new HashMap<String, Set<Section<? extends SimpleReference>>>();

	public Map<String, Set<Section<? extends SimpleReference>>> getAllReferences() {
		return allReferences;
	}

	private final Map<String, Set<Section<? extends SimpleDefinition>>> allDefinitions = new HashMap<String, Set<Section<? extends SimpleDefinition>>>();

	public void newCompilationStep() {
		validObjectsOld = new HashMap<String, TermDefinitionInformation>();
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

	public Object getDefinitionInformationForValidTerm(String termname) {
		if (validObjects.containsKey(termname)) {
			return validObjects.get(termname).getTypeInformation();
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
	public void addPredefinedObject(Section<?> s) {
		validPredefinedObjects.put(KnowWEUtils.getTermIdentifier(s), s);
	}

	public boolean isPredefinedObject(String termIdentifer) {
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
	public boolean isImportedObject(String termIdentifer) {
		return validImportedObjects.containsKey(termIdentifer);
	}

	public boolean isLocalObject(String termIdentifer) {
		return validObjects.containsKey(termIdentifer);
	}

	public void removeImportedObject(String termIdentifier) {
		if (validImportedObjects.containsKey(termIdentifier)) {
			validImportedObjects.remove(termIdentifier);
		}
	}

	public void removeFromValidObjects(Section<?> s) {
		validObjects.remove(KnowWEUtils.getTermIdentifier(s));
		validImportedObjects.remove(KnowWEUtils.getTermIdentifier(s));
	}

	public boolean isValid(String termIdentifier) {
		return validObjects.containsKey(termIdentifier)
				|| validPredefinedObjects.containsKey(termIdentifier)
				|| validImportedObjects.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(String termIdentifier) {
		return validObjectsOld.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(Section<?> s) {
		return wasValidInOldVersion(KnowWEUtils.getTermIdentifier(s));
	}

	public Collection<Section<? extends KnowledgeUnit>> getReferencingSlices(Section<? extends SimpleTerm> section) {
		Collection<Section<? extends KnowledgeUnit>> result = new HashSet<Section<? extends KnowledgeUnit>>();
		String termIdentifier = KnowWEUtils.getTermIdentifier(section);
		Set<Section<? extends SimpleReference>> refSet = allReferences.get(termIdentifier);
		if (refSet == null) return result;
		for (Section<?> ref : refSet) {
			Section<KnowledgeUnit> compilationUnit = Sections.findAncestorOfType(ref,
					KnowledgeUnit.class);
			if (compilationUnit != null) {
				result.add(compilationUnit);
			}
		}
		return result;

	}

	public void registerTermReference(Section<? extends SimpleReference> section) {
		String identifier = KnowWEUtils.getTermIdentifier(section);
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
		String identifier = KnowWEUtils.getTermIdentifier(section);
		if (allReferences.containsKey(identifier)) {
			allReferences.get(identifier).remove(section);
		}
	}

	public void registerTermDefinition(Section<? extends SimpleDefinition> section) {
		String identifier = KnowWEUtils.getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).add(section);
		}
		else {
			HashSet<Section<? extends SimpleDefinition>> set = new HashSet<Section<? extends SimpleDefinition>>();
			set.add(section);
			allDefinitions.put(identifier, set);
		}
	}

	public void deregisterTermDefinition(Section<?> section) {
		String identifier = KnowWEUtils.getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).remove(section);
		}
	}

	public Collection<Section<? extends SimpleDefinition>> getTermDefinitions(Section<?> section) {
		String identifier = KnowWEUtils.getTermIdentifier(section);
		return getTermDefinitions(identifier);
	}

	public Collection<Section<? extends SimpleDefinition>> getTermDefinitions(String identifier) {
		if (allDefinitions.containsKey(identifier)) {
			return allDefinitions.get(identifier);
		}
		return new ArrayList<Section<? extends SimpleDefinition>>();
	}

	public Collection<Section<? extends SimpleReference>> getTermReferences(String identifier) {
		if (allReferences.containsKey(identifier)) {
			return allReferences.get(identifier);
		}
		return new ArrayList<Section<? extends SimpleReference>>();
	}

	public Collection<Section<? extends ComplexDefinition>> getReferencingDefinitions(Section<?> section) {
		Collection<Section<? extends ComplexDefinition>> result = new HashSet<Section<? extends ComplexDefinition>>();
		String termIdentifier = KnowWEUtils.getTermIdentifier(section);
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
		for (String termName : validObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termName);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		for (String termName : validPredefinedObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termName);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}

		for (String termName : validImportedObjects.keySet()) {
			Collection<Section<? extends SimpleDefinition>> termDefinitions = this.getTermDefinitions(termName);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		return result;
	}

}

class TermDefinitionInformation {

	private final Section<?> def;

	private Object typeInformation;

	public TermDefinitionInformation(Section<?> d) {
		this.def = d;
	}

	public TermDefinitionInformation(Section<?> d, Object o) {
		this(d);
		this.typeInformation = o;
	}

	public Object getTypeInformation() {
		return typeInformation;
	}

	public void setTypeInformation(Object typeInformation) {
		this.typeInformation = typeInformation;
	}

}
