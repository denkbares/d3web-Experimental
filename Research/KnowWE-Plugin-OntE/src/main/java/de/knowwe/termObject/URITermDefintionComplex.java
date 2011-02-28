package de.knowwe.termObject;

import java.util.ArrayList;
import java.util.List;

import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;

public abstract class URITermDefintionComplex extends URITermDefinition{

	
	@Override
	protected boolean checkDependencies(Section<URITermDefinition> s) {
		Section<?> father = s.getFather();
		List<Section<TermReference>> refs = new ArrayList<Section<TermReference>>();
		father.findSuccessorsOfType(TermReference.class, refs);
		TerminologyHandler tHandler = KnowWEUtils.getTerminologyHandler(s.getArticle().getWeb());
		for (Section<TermReference> section : refs) {
			boolean valid = tHandler.isDefinedTerm(s.getArticle(), section.get().getTermName(section),KnowWETerm.GLOBAL);
			if(!valid) return false;
		}
		
		return true;
	}
}
