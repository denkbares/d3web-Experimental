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

package de.knowwe.lexiconinterface.provider;


import de.knowwe.lexiconinterface.datatypes.RelationWordPair;
import de.knowwe.lexiconinterface.datatypes.WordCategory;
import de.knowwe.lexiconinterface.datatypes.WordClass;
import de.knowwe.lexiconinterface.relations.ConceptualRelation;

import java.util.List;
import java.util.Set;

/**
 * Created by Daniel Knöll on 16.09.2014.
 */
public interface LexiconProvider {

    /**
     * Returns a list of synonyms of the word
     *
     * @param word
     * @return a string list of synonyms for the word
     */
    List<String> getSynonyms(String word);

    /**
     * Returns a list of antonyms of the word
     *
     * @param word
     * @return a string list of synonyms for the word
     */
    List<String> getAntonyms(String word);

    /**
     * Returns the WordClass (Pflanze, Tier,...) that this word belongs to.
     *
     * @param word
     * @return the WordClass that this word belongs to
     */
    WordClass getWordClass(String word);

    /**
     * Returns alls WordCategories (adj, nomen, verben) that this word can belong to.
     *
     * @return the WordCategories that this word belongs to
     */
    Set<WordCategory> getWordCategories(String word);

    /**
     * Returns a List of all of all the words that this
     * word has any relation to.
     *
     * @return a List of all of the words that this
     * word has any relation to
     */
    List<RelationWordPair> getAllRelatedWords(String word);

    /**
     * Returns a List of this word's relations of
     * type "type".
     *
     * @param type type of relations to retrieve
     * @return a List of this word's relations of
     * type type
     * For example, hypernyms of this word can be retrieved with
     * the type relations.ConceptualRelation.has_hypernym
     */
    List<String> getRelatedWordsOfType(ConceptualRelation type, String word);

    /**
     * Returns this word's paraphrases (can be empty). This list
     * contains all paraphrases of the word
     *
     * @return this word's paraphrases
     */
    String getParaphrase(String word);









}
