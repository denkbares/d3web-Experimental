package de.knowwe.d3web.summary;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * KnowledgeSummaryMarkup shows KnowledgeSummary
 * Created by Veronika Sehne on 23.04.2014.
 */

public class KnowledgeSummaryMarkup extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;
	     static {
	             MARKUP = new DefaultMarkup("KnowledgeSummary");
	     }

	     public KnowledgeSummaryMarkup() {
			 super(MARKUP);
			 this.setRenderer(new KnowledgeSummaryRenderer());
	    }
}
