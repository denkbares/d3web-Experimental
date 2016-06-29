/*
 * Copyright (C) 2012 Chair of Artificial Intelligence and Applied Informatics
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

package de.knowwe.lexiconinterface.relations;

/**
 * Enumeration of all conceptual relations.
 *
 */
@SuppressWarnings("UnnecessaryEnumModifier")
public enum ConceptualRelation {
    has_hypernym(true),
    has_hyponym(true),
    has_component_meronym(true),
    has_component_holonym(true),
    has_member_meronym(true),
    has_member_holonym(true),
    has_substance_meronym(true),
    has_substance_holonym(true),
    has_portion_meronym(true),
    has_portion_holonym(true),
    entails(false),
    is_entailed_by(false),
    is_related_to(false),
    causes(false);
    private boolean transitive;

    ConceptualRelation(boolean transitive) {
        this.transitive = transitive;
    }

    /**
     * Returns true if the <code>String</code> <code>relationName</code> represents a
     * valid <code>relations.ConceptualRelation</code>.
     * @param relationName the name of the relation to verify
     * @return true if the <code>String relationName</code> represents a valid
     * <code>relations.ConceptualRelation</code>
     */
    public static boolean isConceptualRelation(String relationName) {
        ConceptualRelation[] vals = values();

        for (int i = 0; i < vals.length; i++) {
            if (vals[i].toString().equals(relationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if this is a transitive relationship.
     * @return true if this is a transitive relationship.
     */
    public boolean isTransitive() {
        return transitive;
    }
}
