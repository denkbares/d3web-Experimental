package de.knowwe.termObject;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.termObject.URIObject.URIObjectType;

public class LocalConceptDefinition extends URITermDefinition {
	
	public static final String LOCAL_KEY = "this";
	
	public LocalConceptDefinition() {
		this.setSectionFinder(new RegexSectionFinder("def\\s*?" + LOCAL_KEY + "\\s"));
	}

	@Override
	protected URIObjectType getURIObjectType() {
		return URIObjectType.unspecified;
	}

	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		return s.getArticle().getTitle();
	}
	
	

}
