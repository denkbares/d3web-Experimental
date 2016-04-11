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


import de.knowwe.core.Environment;
import de.knowwe.lexiconinterface.datatypes.RelationWordPair;
import de.knowwe.lexiconinterface.datatypes.WordCategory;
import de.knowwe.lexiconinterface.datatypes.WordClass;
import de.knowwe.lexiconinterface.relations.ConceptualRelation;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;
import edu.smu.tspell.wordnet.impl.file.FileDatabase;

import java.util.*;

/**
 * Created by Daniel Knöll on 01.10.2014.
 */
public class WordNetProvider implements LexiconProvider {

    WordNetDatabase database;

    public WordNetProvider() {

        //Set base dir for WordNet
        System.setProperty("wordnet.database.dir", Environment.getInstance().getWikiConnector().getKnowWEExtensionPath()+"/dict");
        WordNetDatabase wndb = new FileDatabase();
        database = WordNetDatabase.getFileInstance();
    }

    @Override
    public List<String> getSynonyms(String word) {
        Synset[] synsets = database.getSynsets(word);
        ArrayList<String> synsetList = new ArrayList<String>(synsets.length);
        for (int i = 0; i < synsets.length; i++) {
            for (int j = 0; j < synsets[i].getWordForms().length; j++)
                if(!synsets[i].getWordForms()[j].equals(word)) {
                    synsetList.add(synsets[i].getWordForms()[j]);
                }
        }
        return synsetList;
    }

    /**
     * Returns a list of antonyms of the word
     *
     * @param word
     * @return a string list of synonyms for the word
     */
    @Override
    public List<String> getAntonyms(String word) {
        Synset[] synsets = database.getSynsets(word.trim());
        List<String> antonyms = new ArrayList<>();

        for (int i = 0; i < synsets.length; i++) {
            WordSense[] wordSenses = synsets[i].getAntonyms(word);
            for (int j = 0; j < wordSenses.length; j++) {
                antonyms.add(wordSenses[j].getWordForm());
            }
        }
        return antonyms;
    }

    @Override
    public WordClass getWordClass(String word) {
        WordClass wordClass = null;

        return wordClass;
    }

    @Override
    public Set<WordCategory> getWordCategories(String word) {

        Set<WordCategory> categories = new HashSet<WordCategory>();
        ArrayList synset;

        //get all Synsets for the word
        Synset[] synsets = database.getSynsets(word);

        //search in every Synset for the exact word and add the WordCategory to the set
        for (Synset syn:synsets){
            synset = new ArrayList(Arrays.asList(syn.getWordForms()));
            if (synset.contains(word)){
                categories.add(wordNetTypeToCategory(syn.getType()));
            }
        }

        //No WordCategory found
        if (categories.isEmpty()){
            throw new RuntimeException("The word you entered was not found in WordNet");

            //found exactly one WordCategory
        }else {
            return categories;
        }
    }

    /**
     * Converts the SynsetType from WordNet to the WordCategory, which is used in our Interface
     *
     * @param type SynsetType
     *
     * @return wordCategory
     */
    private WordCategory wordNetTypeToCategory(SynsetType type){
        WordCategory wordCategory = null;
        switch (type.getCode()){
            case 1:
                wordCategory = WordCategory.noun;
                break;
            case 2:
                wordCategory = WordCategory.verb;
                break;
            case 3:
                wordCategory = WordCategory.adjective;
                break;
            case 4:
                wordCategory = WordCategory.adverb;
                break;
            case 5:
                wordCategory = WordCategory.adjectiveSatellite;
                break;
        }
        return wordCategory;

    }

    @Override
    public List<RelationWordPair> getAllRelatedWords(String word) {


        return null;
    }

    /**
     * Returns a List of this word's relations of
     * type "type".
     *
     * @param type type of relations to retrieve
     * @param word
     * @return a List of this word's relations of
     * type type
     * For example, hypernyms of this word can be retrieved with
     * the type relations.ConceptualRelation.has_hypernym
     */
    @Override
    public List<String> getRelatedWordsOfType(ConceptualRelation type, String word) {
        return null;
    }


    @Override
    public String getParaphrase(String word) {
        Synset[] synsets = database.getSynsets(word);
        Synset synset;
        String paraphrase = "";
        for (int i = 0; i < synsets.length; i++) {
            synset = synsets[i];
            if (synset.getUsageExamples().length > 0) {
                paraphrase = paraphrase + synset.getWordForms()[0] + ": " + synset.getDefinition() + " Example: " + synset.getUsageExamples()[0] + "\n";
            }else{
                paraphrase = paraphrase + synset.getWordForms()[0] + ": " + synset.getDefinition() + "\n";
            }

        }
        return paraphrase;
    }
}
