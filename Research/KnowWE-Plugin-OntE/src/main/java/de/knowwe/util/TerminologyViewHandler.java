package de.knowwe.util;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.node.URI;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.taghandler.AbstractTagHandler;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.d3web.we.wikiConnector.KnowWEUserContext;

public class TerminologyViewHandler extends AbstractTagHandler{

	public TerminologyViewHandler() {
		super("terminology");
	}

	@Override
	public String render(KnowWEArticle article, Section<?> section, KnowWEUserContext userContext, Map<String, String> parameters) {
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		Collection<String> globalTerms = terminologyHandler.getAllGlobalTerms();
		String result = "<ul>Registered Terms:<br>";
		for (String string : globalTerms) {
			Section<? extends TermDefinition> termDefiningSection = terminologyHandler.getTermDefiningSection(article, string, KnowWETerm.GLOBAL);
			KnowWEArticle main = KnowWEEnvironment.getInstance().getArticleManager(KnowWEEnvironment.DEFAULT_WEB).getArticle("Main");
			
			Object object = termDefiningSection.get().getTermObject(main, termDefiningSection);
			String uriString = "no URI";
			if(object instanceof URI) {
				uriString = ((URI)object).toString();
			}
			result += "<li>"+string+" ("+uriString+")"+"</li>";
			
		}
		result += "</ul>";
		return  KnowWEUtils.maskHTML(result);
	}

}
