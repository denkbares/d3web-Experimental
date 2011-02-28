package de.knowwe.termObject;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.sectionFinder.StringSectionFinder;

public class LocalConceptReference extends OWLTermReference {
	
	public LocalConceptReference(){
		this.setSectionFinder(new StringSectionFinder(LocalConceptDefinition.LOCAL_KEY));
	}
	
	@Override
	public String getTermName(Section<? extends KnowWETerm<URIObject>> s) {
		return s.getArticle().getTitle();

	}

}
