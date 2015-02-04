package de.knowwe.lexiconinterface.markup;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.user.UserContext;

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
        String count = parameters.get("count");

        int number = 1;

        if (count != null) {
            try {
                number = Integer.parseInt(count);
            }
            catch (NumberFormatException e) {
                // not a valid number
            }

            if (number < 0) {
                number = 1;
            }
        }
        result.appendHtml(" <b>Hello World!</b>");
    }
}
