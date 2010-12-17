package de.knowwe.termObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.GlobalTermDefinition;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.ObjectAlreadyDefinedError;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;

public abstract class URITermDefinition extends GlobalTermDefinition<URI> {

	public URITermDefinition() {
		super(URI.class);
		this.addSubtreeHandler(new URIDefinitionRegistrationHandler());
	}

	/**
	 * 
	 * This handler registers this Term..
	 * 
	 * @author Jochen, Albrecht
	 * @created 08.10.2010
	 */
	static class URIDefinitionRegistrationHandler extends SubtreeHandler<URITermDefinition> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<URITermDefinition> s) {

			TerminologyHandler tHandler = KnowWEUtils.getTerminologyHandler(article.getWeb());

			tHandler.registerTermDefinition(article, s);


			Section<? extends TermDefinition<URI>> defSec = tHandler.getTermDefiningSection(
					article, s);

			if (defSec != s) {
				return Arrays.asList((KDOMReportMessage) new ObjectAlreadyDefinedError(
						s.get().getName()
								+ ": " + s.get().getTermName(s), s));
			}

			s.get().storeTermObject(article, s, s.get().getTermObject(article, s));

			return new ArrayList<KDOMReportMessage>(0);
		}

		@Override
		public void destroy(KnowWEArticle article, Section<URITermDefinition> s) {
			KnowWEUtils.getTerminologyHandler(article.getWeb()).unregisterTermDefinition(
					article, s);
		}

	}


}
