package de.knowwe.termObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.wcohen.ss.Levenstein;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.core.correction.CorrectionProvider;

/**
 * A Correction Provider for OWLTermReferences
 * 
 * @author Alex Legler
 * @created 22.03.2011
 */
public class OWLTermCorrectionProvider implements CorrectionProvider {

	@Override
	public List<String> getSuggestions(KnowWEArticle article, Section<?> section, int threshold) {
		if (!(section.get() instanceof TermReference)) {
			return null;
		}
		
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		TermReference<?> termReference = ((TermReference<?>) section.get());		
		
		Collection<String> localTermMatches = terminologyHandler.getAllGlobalTermsOfType(
				termReference.getTermObjectClass()
		);
		
		String originalText = section.getOriginalText();
		List<String> suggestions = new LinkedList<String>();
		Levenstein l = new Levenstein();
		
		for (String match : localTermMatches) {
			if (l.score(originalText, match) >= -threshold) {		
				suggestions.add(match);
			}
		}
		
		return suggestions;
	}
}
