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
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

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
	private final Map<String, Section<? extends TermDefinition>> validPredefinedObjects = new HashMap<String, Section<? extends TermDefinition>>();

	private Map<String, TermDefinitionInformation> validObjectsOld = new HashMap<String, TermDefinitionInformation>();

	private final Map<String, Set<Section<? extends TermReference>>> allReferences = new HashMap<String, Set<Section<? extends TermReference>>>();
	private final Map<String, Set<Section<? extends TermDefinition>>> allDefinitions = new HashMap<String, Set<Section<? extends TermDefinition>>>();

	public void newCompilationStep() {
		validObjectsOld = new HashMap<String, TermDefinitionInformation>();
		validObjectsOld.putAll(validObjects);
	}

	public void addToValidObjects(Section<? extends TermDefinition> s) {
		// store (generic) type-compiler-information along with the definition
		TermDefinitionInformation termDefinitionInformation = new TermDefinitionInformation(
				s);
		if (s.get() instanceof TypedTermDefinition) {
			Object typedTermInformation = ((TypedTermDefinition) s.get()).getTypedTermInformation(s);
			termDefinitionInformation.setTypeInformation(typedTermInformation);
		}
		validObjects.put(s.get().getTermIdentifier(s), termDefinitionInformation);
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
	public void addPredefinedObject(Section<? extends TermDefinition> s) {
		validPredefinedObjects.put(s.get().getTermIdentifier(s), s);
	}

	public boolean isPredefinedObject(String termIdentifer) {
		return validPredefinedObjects.containsKey(termIdentifer);
	}

	public void removeFromValidObjects(Section<? extends TermDefinition> s) {
		validObjects.remove(s.get().getTermIdentifier(s));
	}

	public boolean isValid(String termIdentifier) {
		return validObjects.containsKey(termIdentifier)
				|| validPredefinedObjects.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(String termIdentifier) {
		return validObjectsOld.containsKey(termIdentifier);
	}

	public boolean wasValidInOldVersion(Section<? extends TermDefinition> s) {
		return wasValidInOldVersion(s.get().getTermIdentifier(s));
	}

	public Collection<Section<? extends KnowledgeUnit>> getReferencingSlices(Section<? extends TermDefinition> section) {
		Collection<Section<? extends KnowledgeUnit>> result = new HashSet<Section<? extends KnowledgeUnit>>();
		String termIdentifier = section.get().getTermIdentifier(section);
		Set<Section<? extends TermReference>> refSet = allReferences.get(termIdentifier);
		if (refSet == null) return result;
		for (Section<? extends TermReference> ref : refSet) {
			Section<KnowledgeUnit> compilationUnit = Sections.findAncestorOfType(ref,
					KnowledgeUnit.class);
			if (compilationUnit != null) {
				result.add(compilationUnit);
			}
		}
		return result;

	}

	public void registerTermReference(Section<? extends TermReference> section) {
		String identifier = section.get().getTermIdentifier(section);
		if (allReferences.containsKey(identifier)) {
			allReferences.get(identifier).add(section);
		}
		else {
			HashSet<Section<? extends TermReference>> set = new HashSet<Section<? extends TermReference>>();
			set.add(section);
			allReferences.put(identifier, set);
		}
	}

	public void deregisterTermReference(Section<? extends TermReference> section) {
		String identifier = section.get().getTermIdentifier(section);
		if (allReferences.containsKey(identifier)) {
			allReferences.get(identifier).remove(section);
		}
	}

	public void registerTermDefinition(Section<? extends TermDefinition> section) {
		String identifier = section.get().getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).add(section);
		}
		else {
			HashSet<Section<? extends TermDefinition>> set = new HashSet<Section<? extends TermDefinition>>();
			set.add(section);
			allDefinitions.put(identifier, set);
		}
	}

	public void deregisterTermDefinition(Section<? extends TermDefinition> section) {
		String identifier = section.get().getTermIdentifier(section);
		if (allDefinitions.containsKey(identifier)) {
			allDefinitions.get(identifier).remove(section);
		}
	}

	public Collection<Section<? extends TermDefinition>> getTermDefinitions(Section<? extends TermDefinition> section) {
		String identifier = section.get().getTermIdentifier(section);
		return getTermDefinitions(identifier);
	}

	public Collection<Section<? extends TermDefinition>> getTermDefinitions(String identifier) {
		if (allDefinitions.containsKey(identifier)) {
			return allDefinitions.get(identifier);
		}
		return new ArrayList<Section<? extends TermDefinition>>();
	}

	public Collection<Section<? extends TermReference>> getTermReferences(String identifier) {
		if (allReferences.containsKey(identifier)) {
			return allReferences.get(identifier);
		}
		return new ArrayList<Section<? extends TermReference>>();
	}

	public Collection<Section<? extends ComplexDefinition>> getReferencingDefinitions(Section<? extends TermDefinition> section) {
		Collection<Section<? extends ComplexDefinition>> result = new HashSet<Section<? extends ComplexDefinition>>();
		String termIdentifier = section.get().getTermIdentifier(section);
		Set<Section<? extends TermReference>> refSet = allReferences.get(termIdentifier);
		if (refSet == null) return result;
		for (Section<? extends TermReference> ref : refSet) {
			Section<ComplexDefinition> compilationUnit = Sections.findAncestorOfType(ref,
					ComplexDefinition.class);
			if (compilationUnit != null) {
				result.add(compilationUnit);
			}
		}
		return result;
	}

	public Collection<Section<? extends TermDefinition>> getAllTermDefinitions() {
		Set<Section<? extends TermDefinition>> result = new HashSet<Section<? extends TermDefinition>>();
		for (String termName : validObjects.keySet()) {
			Collection<Section<? extends TermDefinition>> termDefinitions = this.getTermDefinitions(termName);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		for (String termName : validPredefinedObjects.keySet()) {
			Collection<Section<? extends TermDefinition>> termDefinitions = this.getTermDefinitions(termName);
			if (termDefinitions.size() > 0) {
				result.add(termDefinitions.iterator().next());
			}
		}
		return result;
	}

}

class TermDefinitionInformation {

	private final Section<? extends TermDefinition> def;

	private Object typeInformation;

	public TermDefinitionInformation(Section<? extends TermDefinition> d) {
		this.def = d;
	}

	public TermDefinitionInformation(Section<? extends TermDefinition> d, Object o) {
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
