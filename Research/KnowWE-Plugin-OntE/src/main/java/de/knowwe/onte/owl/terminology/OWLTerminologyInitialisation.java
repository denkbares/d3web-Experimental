package de.knowwe.onte.owl.terminology;

import javax.servlet.ServletContext;

import de.d3web.we.action.KnowWEActionDispatcher;
import de.d3web.we.core.KnowWEArticleManager;
import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.KnowWEObjectType;
import de.d3web.we.kdom.Section;
import de.d3web.we.terminology.TerminologyHandler;
import de.d3web.we.utils.KnowWEUtils;
import de.knowwe.plugin.Instantiation;

public class OWLTerminologyInitialisation implements Instantiation{

	@Override
	public void init(ServletContext context) {
		KnowWEArticle art = KnowWEArticle.createArticle("some eternal truth...","Unmodifiable KnowWE article", null, KnowWEEnvironment.DEFAULT_WEB);
		Section<?> s = art.getSection();
		TerminologyHandler terminologyHandler = KnowWEUtils.getTerminologyHandler(KnowWEEnvironment.DEFAULT_WEB);
		//terminologyHandler.registerTermDefinition(art, s);
		// TODO: some more magic here..
	}



}
