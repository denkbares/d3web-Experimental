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

package de.knowwe.lexiconinterface.datatypes;


import de.knowwe.lexiconinterface.relations.ConceptualRelation;

/**
 * Created by ru4262 on 07.10.2014.
 */
public class RelationWordPair {

    ConceptualRelation relation;
    String word;

    public RelationWordPair(ConceptualRelation relation, String word) {
        this.relation = relation;
        this.word = word;
    }

    public ConceptualRelation getRelation() {
        return relation;
    }

    public void setRelation(ConceptualRelation relation) {
        this.relation = relation;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
