package de.knowwe.rdfs.inspect;

import java.util.Collection;
import java.util.Map;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.node.URI;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.taghandler.AbstractTagHandler;
import de.knowwe.core.taghandler.TagHandlerTypeContent;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.rdfs.util.SparqlResultSetRenderer;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolUtils;

public class ClassMemberTagHandler extends AbstractTagHandler {

	// Parameter used in the request
	public static final String OBJECTNAME = "objectname";

	private static DefaultMarkupRenderer<DefaultMarkupType> defaultMarkupRenderer =
			new DefaultMarkupRenderer<DefaultMarkupType>();

	public ClassMemberTagHandler() {
		super("listmembers");
	}

	@Override
	public final String render(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {

		String content = renderContent(article, section, userContext, parameters);
		Section<TagHandlerTypeContent> tagNameSection = Sections.findSuccessor(section,
				TagHandlerTypeContent.class);
		String sectionID = section.getID();
		Tool[] tools = ToolUtils.getTools(article, tagNameSection, userContext);

		StringBuilder buffer = new StringBuilder();
		String cssClassName = "type_" + section.get().getName();
		defaultMarkupRenderer.renderDefaultMarkupStyled(
				getTagName(), content, sectionID, cssClassName, tools, userContext,
				buffer);
		KnowWEUtils.maskJSPWikiMarkup(buffer);
		return buffer.toString();
	}

	private String renderContent(KnowWEArticle article, Section<?> section, UserContext userContext, Map<String, String> parameters) {

		StringBuffer buffy = new StringBuffer();
		Map<String, String> urlParameters = userContext.getParameters();

		// First try the URL-Parameter, if null try the TagHandler-Parameter.
		String objectName = null;
		if (urlParameters.get(OBJECTNAME) != null) {
			objectName = KnowWEUtils.urldecode(urlParameters.get(OBJECTNAME));
		}
		else if (parameters.get(OBJECTNAME) != null) {
			objectName = KnowWEUtils.urldecode(parameters.get(OBJECTNAME));
		}

		Collection<Section<? extends TermReference>> termReferences = IncrementalCompiler.getInstance().getTerminology().getTermReferences(
				objectName);

		if (termReferences != null && termReferences.size() > 0) {

			buffy.append(KnowWEUtils.maskHTML("Members of class <b>" + objectName
					+ "</b>:<br>"));

			URI classURI = RDFSUtil.getURI(termReferences.iterator().next());

			String query = "SELECT ?x WHERE { ?x rdf:type +<" + classURI.toString()
					+ ">.}";
			QueryResultTable classMembersTable = Rdf2GoCore.getInstance().sparqlSelect(
					query);
			buffy.append(SparqlResultSetRenderer.renderQueryResult(classMembersTable,
					true));

		}
		return buffy.toString();
	}
}
