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
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.GlobalTermDefinition;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.ObjectAlreadyDefinedError;
import de.d3web.we.kdom.subtreeHandler.SubtreeHandler;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.onte.owl.terminology.PredefinedTermError;
import de.knowwe.onte.owl.terminology.URIUtil;
import de.knowwe.termObject.URIObject.URIObjectType;

public abstract class URITermDefinition extends GlobalTermDefinition<URIObject> implements RDFResourceType {

	public URITermDefinition() {
		super(URIObject.class);
		this.addSubtreeHandler(Priority.HIGHER, new URIDefinitionRegistrationHandler());
	}
	
	@Override
	public URI getNode(Section<? extends RDFResourceType> s) {
		//KnowWEArticle main = KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");
		
		if (s.get() instanceof TermDefinition) {
			Object termObject = ((TermDefinition)s.get()).getTermObject(null,
					s);
			if (termObject instanceof URIObject) {
				return ((URIObject) termObject).getURI();
			}
		}
		return null;
	}
	
	protected boolean checkDependencies(Section<URITermDefinition> s) {
		return true;
	}
	
	protected abstract URIObjectType getURIObjectType();


	static class URIDefinitionRegistrationHandler extends SubtreeHandler<URITermDefinition> {

		@Override
		public Collection<KDOMReportMessage> create(KnowWEArticle article, Section<URITermDefinition> s) {

			
			TerminologyHandler tHandler = KnowWEUtils.getTerminologyHandler(article.getWeb());

			String termName = s.get().getTermName(s);

			if (URIUtil.checkForKnownTerms(termName, URIUtil.OBJECT_VOCABULARY)
					|| URIUtil.checkForKnownTerms(termName, URIUtil.PREDICATE_VOCABULARY)) {
				return Arrays.asList((KDOMReportMessage) new PredefinedTermError(
						s.get().getName() + ": " + s.get().getTermName(s)));
			}

			//if(s.get().checkDependencies(s)) {
				tHandler.registerTermDefinition(article, s);
			//}else {
			//	return Arrays.asList((KDOMReportMessage) new NoSuchObjectError(""));
			//}


			Section<? extends TermDefinition<URIObject>> defSec = tHandler.getTermDefiningSection(
					article, s);

			if (defSec != s) {
				return Arrays.asList((KDOMReportMessage) new ObjectAlreadyDefinedError(
						s.get().getName()
								+ ": " + s.get().getTermName(s), defSec));
			}
			
			URIObject uri = createTermObject(s);

			//KnowWEArticle main = KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");
			
			s.get().storeTermObject(null, s, uri);
			

			return new ArrayList<KDOMReportMessage>(0);
		}



		protected URIObject createTermObject(Section<URITermDefinition> s) {
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
			return new URIObject(uri,s.get().getURIObjectType());
		}
		
		



		@Override
		public void destroy(KnowWEArticle article, Section<URITermDefinition> s) {
			KnowWEUtils.getTerminologyHandler(article.getWeb()).unregisterTermDefinition(
					article, s);
		}

	}


}
