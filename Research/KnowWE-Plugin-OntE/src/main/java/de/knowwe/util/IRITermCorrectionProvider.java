package de.knowwe.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.wcohen.ss.Levenstein;

import de.d3web.strings.Identifier;
import de.knowwe.core.compile.terminology.TerminologyManager;
import de.knowwe.core.correction.CorrectionProvider;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;

/**
 * A Correction Provider for OWLTermReferences
 * 
 * @author Alex Legler
 * @created 22.03.2011
 */
public class IRITermCorrectionProvider implements CorrectionProvider {

	@Override
	public List<CorrectionProvider.Suggestion> getSuggestions(Article article, Section<?> section, int threshold) {
		if (!(section.get() instanceof Term)) {
			return null;
		}

		TerminologyManager terminologyHandler = KnowWEUtils.getGlobalTerminologyManager(article.getWeb());
		Term termReference = ((Term) section.get());

		Collection<Identifier> localTermMatches = terminologyHandler.getAllDefinedTermsOfType(
				termReference.getTermObjectClass(Sections.cast(section, Term.class))
				);

		String originalText = section.getText();
		List<CorrectionProvider.Suggestion> suggestions = new LinkedList<CorrectionProvider.Suggestion>();
		Levenstein l = new Levenstein();

		for (Identifier match : localTermMatches) {
			double score = l.score(originalText, match.getLastPathElement());
			if (score >= -threshold) {
				suggestions.add(new CorrectionProvider.Suggestion(match.getLastPathElement(),
						(int) score));
			}
		}

		return suggestions;
	}
}
