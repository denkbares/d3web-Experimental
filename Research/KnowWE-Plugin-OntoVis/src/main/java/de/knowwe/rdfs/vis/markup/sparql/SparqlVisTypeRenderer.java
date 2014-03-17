package de.knowwe.rdfs.vis.markup.sparql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Message.Type;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;
import de.knowwe.rdfs.vis.markup.OntoVisType;
import de.knowwe.rdfs.vis.util.Utils;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.visualization.GraphDataBuilder.NODE_TYPE;
import de.knowwe.visualization.GraphVisualizationRenderer;
import de.knowwe.visualization.SubGraphData;
import de.knowwe.visualization.d3.D3VisualizationRenderer;
import de.knowwe.visualization.dot.DOTVisualizationRenderer;

public class SparqlVisTypeRenderer implements Renderer {

	@Override
	public void render(Section<?> content, UserContext user, RenderResult string) {

		Section<SparqlVisType> section = Sections.findAncestorOfType(content,
				SparqlVisType.class);
		Section<DefaultMarkupType> defMarkupSection = Sections.cast(section,
				DefaultMarkupType.class);
		Rdf2GoCore core = Rdf2GoUtils.getRdf2GoCore(defMarkupSection);
		if (core == null) {
			string.appendHtmlElement("div", "");
			return;
		}

		List<Message> messages = new ArrayList<Message>();

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only

		String realPath = servletContext.getRealPath("");

		Map<String, String> parameterMap = new HashMap<String, String>();

		// set css layout to be used
		String layout = SparqlVisType.getAnnotation(section, SparqlVisType.ANNOTATION_DESIGN);
		if (layout != null) {

			String cssText = null;

			ArticleManager articleManager = Environment.getInstance().getArticleManager(
					Environment.DEFAULT_WEB);
			Collection<Article> articles = articleManager.getArticles();

			for (Article article : articles) {
				Section<RootType> rootSection = article.getRootSection();
				// search layouttypes
				List<Section<SparqlVisDesignType>> sparqlVisDesignSections = Sections.findSuccessorsOfType(
						rootSection, SparqlVisDesignType.class);

				for (Section<SparqlVisDesignType> currentSection : sparqlVisDesignSections) {

					String currentLayout = SparqlVisDesignType.getAnnotation(currentSection,
							SparqlVisDesignType.ANNOTATION_NAME);
					if (currentLayout.equals(layout)) {

						cssText = SparqlVisDesignType.getContentSection(currentSection).getText();

					}

				}
			}

			if (cssText != null) {

				parameterMap.put(OntoGraphDataBuilder.D3_FORCE_VISUALISATION_STYLE, cssText);
			}
			else {
				Message noSuchLayout = new Message(Message.Type.WARNING,
						"No such layout " + layout + " found!");
				Collection<Message> warnings = new HashSet<Message>();
				messages.add(noSuchLayout);
				DefaultMarkupRenderer.renderMessagesOfType(Message.Type.WARNING, warnings,
						string);

			}

		}

		parameterMap.put(OntoGraphDataBuilder.REAL_PATH, realPath);

		parameterMap.put(OntoGraphDataBuilder.SECTION_ID, section.getID());

		// set panel size
		parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_SIZE));

		// set format (png/svg)
		String format = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

		// additional dot source code
		String dotApp = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_DOT_APP);
		parameterMap.put(OntoGraphDataBuilder.DOT_APP, dotApp);

		// set renderer
		String rendererType = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_RENDERER);
		parameterMap.put(OntoGraphDataBuilder.RENDERER, rendererType);

		// set visualization
		String visualization = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_VISUALIZATION);
		parameterMap.put(OntoGraphDataBuilder.VISUALIZATION, visualization);

		// set master
		String master = getMaster(section);
		if (master != null) {
			parameterMap.put(OntoGraphDataBuilder.MASTER, master);
		}

		// set language
		String lang = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(OntoGraphDataBuilder.LANGUAGE, lang);
		}

		// set link mode
		String linkModeValue = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LINK_MODE);
		if (linkModeValue == null) {
			// default link mode is 'jump'
			linkModeValue = SparqlVisType.LinkMode.jump.name();
		}
		parameterMap.put(OntoGraphDataBuilder.LINK_MODE, linkModeValue);

		String addToDOT = "";
		List<Section<? extends AnnotationContentType>> annotationSections =
				DefaultMarkupType.getAnnotationContentSections(section,
						SparqlVisType.ANNOTATION_ADD_TO_DOT);
		for (Section<? extends AnnotationContentType> anno : annotationSections) {
			if (anno != null) addToDOT += anno.getText() + "\n";
		}
		parameterMap.put(OntoGraphDataBuilder.ADD_TO_DOT, addToDOT);

		LinkToTermDefinitionProvider uriProvider;
		String globalAnnotation = DefaultMarkupType.getAnnotation(section, Rdf2GoCore.GLOBAL);
		if (globalAnnotation != null && globalAnnotation.equals("true")) {
			// TODO: completely remove dependency to IncrementalCompiler
			try {
				uriProvider = (LinkToTermDefinitionProvider) Class.forName(
						"de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider")
						.newInstance();
			}
			catch (Exception e) {
				uriProvider = new LinkToTermDefinitionProvider() {
					@Override
					public String getLinkToTermDefinition(Identifier name, String masterArticle) {
						return null;
					}
				};
			}
		}
		else {
			uriProvider = new PackageCompileLinkToTermDefinitionProvider();
		}

		// evaluate sparql query and create graph data
		String sparqlString = Rdf2GoUtils.createSparqlString(core, content.getText());

		QueryResultTable resultSet = core.sparqlSelect(
				sparqlString);
		SubGraphData data = convertToGraph(resultSet, parameterMap, core, uriProvider,
				section, messages);

		// read passed concept parameter from browser url if existing
		String conceptName = user.getParameter("concept");

		// otherwise use annotation value
		if (conceptName == null) {
			conceptName = OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONCEPT);
		}
		// if no concept is specified, finally take first guess
		if (data != null && conceptName == null && data.getConceptDeclarations().size() > 0) {

			// if no center concept has explicitly been specified, take any
			conceptName = data.getConceptDeclarations().iterator().next().getName();
		}
		parameterMap.put(OntoGraphDataBuilder.CONCEPT, conceptName);

		String renderedContent = "";

		if (data != null) {

			// current default source renderer is DOT
			GraphVisualizationRenderer graphRenderer = new DOTVisualizationRenderer(data,
					parameterMap);
			String renderer = parameterMap.get(OntoGraphDataBuilder.RENDERER);
			if (renderer != null && renderer.equals(GraphDataBuilder.Renderer.d3.name())) {
				graphRenderer = new D3VisualizationRenderer(data, parameterMap);
			}

			graphRenderer.generateSource();
			renderedContent = graphRenderer.getHTMLIncludeSnipplet();

		}
		if (messages.size() > 0) {
			DefaultMarkupRenderer.renderMessagesOfType(Message.Type.WARNING, messages,
					string);
		}

		string.appendHtml(renderedContent);

	}

	private SubGraphData convertToGraph(QueryResultTable resultSet, Map<String, String> parameters, Rdf2GoCore rdfRepository, LinkToTermDefinitionProvider uriProvider, Section<?> section, List<Message> messages) {
		SubGraphData data = new SubGraphData();
		List<String> variables = resultSet.getVariables();
		if (variables.size() < 3) {
			Message m = new Message(Type.ERROR,
					"A sparqlvis query requires exactly three variables!");
			messages.add(m);
			return null;
		}
		for (QueryRow row : resultSet) {
			Node fromURI = row.getValue(variables.get(0));

			Node relationURI = row.getValue(variables.get(1));
			String relation = OntoGraphDataBuilder.getConceptName(relationURI, rdfRepository);

			Node toURI = row.getValue(variables.get(2));

			ConceptNode fromNode = createNode(parameters, rdfRepository, uriProvider,
					section, data, fromURI);

			ConceptNode toNode = createNode(parameters, rdfRepository, uriProvider, section,
					data, toURI);

			String relationLabel = createRelationLabel(parameters, rdfRepository, relationURI,
					relation);

			Edge newLineRelationsKey = new Edge(fromNode, relationLabel, toNode);

			data.addEdge(newLineRelationsKey);

		}
		if (data.getConceptDeclarations().size() == 0) {
			Message m = new Message(Type.ERROR,
					"The query produced an empty result set!");
			messages.add(m);
			return null;
		}
		return data;
	}

	private String createRelationLabel(Map<String, String> parameters, Rdf2GoCore rdfRepository, Node relationURI, String relation) {
		// is the node a literal ?
		Literal toLiteral = null;
		try {
			toLiteral = relationURI.asLiteral();
		}
		catch (ClassCastException e) {
			// do nothing
		}

		String relationName = relation;
		if (toLiteral != null) {
			relationName = toLiteral.toString();
			if (relationName.contains("@")) {
				relationName = relationName.substring(0, relationName.indexOf('@'));
			}
		}
		else {
			// if it is no literal look for label for the URI
			String relationLabel = Utils.getRDFSLabel(
					relationURI.asURI(), rdfRepository,
					parameters.get(OntoGraphDataBuilder.LANGUAGE));
			if (relationLabel != null) {
				relationName = relationLabel;
			}
		}
		return relationName;
	}

	private ConceptNode createNode(Map<String, String> parameters, Rdf2GoCore rdfRepository, LinkToTermDefinitionProvider uriProvider, Section<?> section, SubGraphData data, Node toURI) {
		String to = OntoGraphDataBuilder.getConceptName(toURI, rdfRepository);
		// is the node a literal ?
		Literal toLiteral = null;
		try {
			toLiteral = toURI.asLiteral();
			if (to == null) {
				to = toLiteral.toString();
			}
		}
		catch (ClassCastException e) {
			// do nothing
		}

		ConceptNode toNode = null;

		if (toLiteral != null) {
			toNode = data.getConcept(to);
		}
		if (toNode == null) {
			NODE_TYPE type = NODE_TYPE.UNDEFINED;
			String label = null;
			if (toLiteral == null) {
				label = Utils.getRDFSLabel(
						toURI.asURI(), rdfRepository,
						parameters.get(OntoGraphDataBuilder.LANGUAGE));
			}
			if (label == null && toLiteral == null) {
				label = to;
			}
			if (toLiteral != null) {
				type = NODE_TYPE.LITERAL;
				label = toLiteral.toString();
				if (label.contains("@")) {
					label = label.substring(0, label.indexOf('@'));
				}
			}
			toNode = new ConceptNode(to, type, createConceptURL(to, parameters,
					section,
					uriProvider), label);
			data.addConcept(toNode);
		}
		return toNode;
	}

	private String getMaster(Section<?> section) {
		return SparqlVisType.getAnnotation(section,
				PackageManager.MASTER_ATTRIBUTE_NAME);
	}

	private String createConceptURL(String to, Map<String, String> parameters, Section<?> s, LinkToTermDefinitionProvider uriProvider) {
		if (parameters.get(OntoGraphDataBuilder.LINK_MODE) != null) {
			if (parameters.get(OntoGraphDataBuilder.LINK_MODE).equals(
					OntoGraphDataBuilder.LINK_MODE_BROWSE)) {
				Identifier identifier = new Identifier(to);
				String[] identifierParts = to.split(":");
				if (identifierParts.length == 2) {
					identifier = new Identifier(
							identifierParts[0], Strings.decodeURL(identifierParts[1]));

				}
				return uriProvider.getLinkToTermDefinition(identifier,
						parameters.get(OntoGraphDataBuilder.MASTER));
			}
		}
		return OntoGraphDataBuilder.createBaseURL() + "?page="
				+ OntoGraphDataBuilder.getSectionTitle(s)
				+ "&concept=" + to;
	}

}
