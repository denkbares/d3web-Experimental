package de.knowwe.ontology.summary;

import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * OntologySummaryMarkup shows a summary of an ontology
 * Created by Veronika Sehne on 30.04.2014.
 */
public class OntologySummaryMarkup extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("OntologySummary");
	}

	public OntologySummaryMarkup() {
		super(MARKUP);
		this.setRenderer(new OntologySummaryRenderer());
	}
}
