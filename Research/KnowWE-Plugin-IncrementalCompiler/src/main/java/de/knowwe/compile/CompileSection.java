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

import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;

/**
 * Wrapper class for sections allowing equals to compare only the text-content.
 * The compilation algorithm presumes, that identical text-statements also
 * result in the same entities in the target representation when being
 * translated
 * 
 * @author Jochen
 * @created 04.03.2012
 */
public class CompileSection<T extends Type> {

	T type;

	public T get() {
		return type;
	}

	public Section<? extends Type> getSection() {
		return section;
	}

	private Section<? extends Type> section = null;

	public CompileSection(Section<T> s) {
		this.section = s;
		type = s.get();
	}

	@Override
	public int hashCode() {
		return section.getText().hashCode();
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof CompileSection) {
			CompileSection other = (CompileSection) arg0;
			if (!(other.section.getText().equals(this.section.getText()))) {
				return false;
			}
			else {
				return true;
				// if (type instanceof KnowledgeUnit && other.get() instanceof
				// KnowledgeUnit) {
				// // find out whether external references are equals also
				// Section<KnowledgeUnit> thisUnit = Sections.cast(section,
				// KnowledgeUnit.class);
				// Collection<Section<? extends SimpleTerm>> externalReferences
				// = (Collection<Section<? extends SimpleTerm>>)
				// (KnowWEUtils.getStoredObject(
				// thisUnit,
				// IncrementalCompiler.EXTERNAL_REFERENCES_OF_KNOWLEDGEUNIT));
				// Section<KnowledgeUnit> otherUnit = Sections.cast(section,
				// KnowledgeUnit.class);
				// Collection<Section<? extends SimpleTerm>>
				// otherExternalReferences = (Collection<Section<? extends
				// SimpleTerm>>) (KnowWEUtils.getStoredObject(
				// otherUnit,
				// IncrementalCompiler.EXTERNAL_REFERENCES_OF_KNOWLEDGEUNIT));

				// return otherExternalReferences.equals(externalReferences);

				// Collection<Section<? extends SimpleTerm>>
				// allReferencesOfKnowledgeUnit =
				// thisUnit.get().getCompileScript().getAllReferencesOfKnowledgeUnit(
				// thisUnit);
				// Collection<CompileSection<? extends SimpleTerm>> refs =
				// new
				// HashSet<CompileSection<? extends SimpleTerm>>();
				// for (Section<? extends SimpleTerm> section :
				// allReferencesOfKnowledgeUnit) {
				// refs.add(new CompileSection(section));
				// }
				//
				// Collection<Section<? extends SimpleTerm>>
				// allReferencesOfOtherKnowledgeUnit =
				// otherUnit.get().getCompileScript().getAllReferencesOfKnowledgeUnit(
				// otherUnit);
				// Collection<CompileSection<? extends SimpleTerm>>
				// otherRefs =
				// new HashSet<CompileSection<? extends SimpleTerm>>();
				// for (Section<? extends SimpleTerm> section :
				// allReferencesOfOtherKnowledgeUnit) {
				// otherRefs.add(new CompileSection(section));
				// }

				// return refs.equals(otherRefs);
				// }
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return section.toString();
	}

}
