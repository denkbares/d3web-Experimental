package de.knowwe.termObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;

import de.d3web.we.core.KnowWEEnvironment;
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
			
			URI uri = null;
			String baseUrl = KnowWEEnvironment.getInstance().getWikiConnector().getBaseUrl();
			try {
				String name = URLEncoder.encode(s.get().getTermName(s), "UTF-8");
				uri = new URIImpl(baseUrl+name);
			}
			catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			s.get().storeTermObject(article, s, uri);

			return new ArrayList<KDOMReportMessage>(0);
		}

		@Override
		public void destroy(KnowWEArticle article, Section<URITermDefinition> s) {
			KnowWEUtils.getTerminologyHandler(article.getWeb()).unregisterTermDefinition(
					article, s);
		}

	}


}
