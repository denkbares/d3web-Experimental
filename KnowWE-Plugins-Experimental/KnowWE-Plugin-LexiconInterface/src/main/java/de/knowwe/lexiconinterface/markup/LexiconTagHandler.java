package de.knowwe.lexiconinterface.markup;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.lexiconinterface.datatypes.LexiconType;
import de.knowwe.lexiconinterface.provider.OpenThesaurusProvider;
import de.knowwe.lexiconinterface.provider.WordNetProvider;
import de.knowwe.lexiconinterface.relations.Relation;

import java.util.Map;

/**
 * Created by ru4262 on 04.02.2015.
 */
public class LexiconTagHandler extends AbstractTagHandler {
    public LexiconTagHandler() {
        super("lexicon");
    }

    /**
     * Renders the tag handler into a wiki markup string. The resulting string
     * is rendered into the wiki page as wiki markup.
     *
     * @param section     the section where the tag handler is used.
     * @param userContext
     * @param parameters  the parameters of the tag handler invocation
     * @param result      the result where the rendered contents are appended
     */
    @Override
    public void render(Section<?> section, UserContext userContext, Map<String, String> parameters, RenderResult result) {
        String word = parameters.get("word");
        String lexicon = parameters.get("lexicon");
        String relation = parameters.get("relation");

        String resultString;

        if(lexicon.equals(LexiconType.wordnet.name())){
            WordNetProvider wnp = new WordNetProvider();

            if(relation.equals(Relation.synonym.name())) {
                resultString = wnp.getSynonyms(word).toString();
            }else if(relation.equals(Relation.antonym.name())){
                resultString = wnp.getAntonyms(word).toString();
            }else{
                resultString = " Bitte geben Sie eine gültige Relation an! ";
            }



        }else if(lexicon.equals(LexiconType.openthesaurus.name())){

            OpenThesaurusProvider otp = new OpenThesaurusProvider();

            if(relation.equals(Relation.synonym.name())) {
                resultString = otp.getSynonyms(word).toString();
            }else if(relation.equals(Relation.antonym.name())){
                resultString = otp.getAntonyms(word).toString();
            }else{
                resultString = " Bitte geben Sie eine gültige Relation an! ";
            }

        }else{
            resultString = " Bitte geben Sie ein gültiges Lexikon an! ";
        }

        result.appendHtml(resultString.substring(1, resultString.length()-1));

    }
}
