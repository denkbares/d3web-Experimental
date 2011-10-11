package de.knowwe.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.wcohen.ss.Levenstein;

import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.compile.TerminologyHandler;
import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * A Correction Provider for OWLTermReferences
 * 
 * @author Alex Legler
 * @created 22.03.2011
 */
public class IRITermCorrectionProvider implements CorrectionProvider {

	@Override
	public List<CorrectionProvider.Suggestion> getSuggestions(KnowWEArticle article, Section<?> section, int threshold) {
		if (!(section.get() instanceof TermReference)) {
			return null;
		}
		
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		TermReference<?> termReference = ((TermReference<?>) section.get());		
		
		Collection<String> localTermMatches = terminologyHandler.getAllGlobalTermsOfType(
				termReference.getTermObjectClass()
		);
		
		String originalText = section.getOriginalText();
		List<CorrectionProvider.Suggestion> suggestions = new LinkedList<CorrectionProvider.Suggestion>();
		Levenstein l = new Levenstein();
		
		for (String match : localTermMatches) {
			double score = l.score(originalText, match);
			if (score >= -threshold) {		
				suggestions.add(new CorrectionProvider.Suggestion(match, (int)score));
			}
		}
		
		return suggestions;
	}
}
