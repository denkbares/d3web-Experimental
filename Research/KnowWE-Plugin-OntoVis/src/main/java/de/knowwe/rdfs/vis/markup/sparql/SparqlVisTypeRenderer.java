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
import de.d3web.utils.Log;
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
import de.knowwe.rdfs.vis.markup.VisConfigType;
import de.knowwe.rdfs.vis.util.Utils;
import de.knowwe.visualization.ConceptNode;
import de.knowwe.visualization.Edge;
import de.knowwe.visualization.GraphDataBuilder;
import de.knowwe.visualization.GraphVisualizationRenderer;
import de.knowwe.visualization.SubGraphData;
import de.knowwe.visualization.d3.D3VisualizationRenderer;
import de.knowwe.visualization.dot.DOTVisualizationRenderer;

public class SparqlVisTypeRenderer implements Renderer {

	private static final boolean SHOW_LABEL_FOR_URI = false;

	private Rdf2GoCore core;

	@Override
	public void render(Section<?> content, UserContext user, RenderResult string) {

		Section<SparqlVisType> section = Sections.findAncestorOfType(content,
				SparqlVisType.class);
		Section<DefaultMarkupType> defMarkupSection = Sections.cast(section,
				DefaultMarkupType.class);
		core = Rdf2GoUtils.getRdf2GoCore(defMarkupSection);
		if (core == null) {
			string.appendHtmlElement("div", "");
			return;
		}

		List<Message> messages = new ArrayList<Message>();

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only

		String realPath = servletContext.getRealPath("");

		Map<String, String> parameterMap = new HashMap<String, String>();

		// find and read config file if defined
		String configName = SparqlVisType.getAnnotation(section, SparqlVisType.ANNOTATION_CONFIG);

		if (configName != null) {
			findAndReadConfig(configName.trim(), section.getArticleManager(), parameterMap, messages, string);
		}

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
		String size =  SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_SIZE);
		if (size != null) {
			parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, size);
		}

		// set format (png/svg)
		String format = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
			parameterMap.put(OntoGraphDataBuilder.FORMAT, format);
		}

		// additional dot source code
		String dotApp = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_DOT_APP);
		if (dotApp != null) {
			parameterMap.put(OntoGraphDataBuilder.DOT_APP, dotApp);
		}

		// set rank direction of graph layout
		String rankDir = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_RANK_DIR);
		if (rankDir != null) {
			parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, rankDir);
		}

		// set color codings if existing
		String colorRelationName = SparqlVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_COLORS);
		if (!Strings.isBlank(colorRelationName)) {
			parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, Utils.createColorCodings(colorRelationName, core, "rdf:Property"));
			parameterMap.put(OntoGraphDataBuilder.CLASS_COLOR_CODES, Utils.createColorCodings(colorRelationName, core, "rdfs:Class"));
		}

		// set flag for use of labels
		String labelValue = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LABELS);
		if (labelValue != null) {
			parameterMap.put(OntoGraphDataBuilder.USE_LABELS, labelValue);
		}

		// set renderer
		String rendererType = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_RENDERER);
		if (rendererType != null) {
			parameterMap.put(OntoGraphDataBuilder.RENDERER, rendererType);
		}

		// set visualization
		String visualization = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_VISUALIZATION);
		if (visualization != null) {
			parameterMap.put(OntoGraphDataBuilder.VISUALIZATION, visualization);
		}

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
			parameterMap.put(OntoGraphDataBuilder.LINK_MODE, linkModeValue);
		}

		String addToDOT = "";
		List<Section<? extends AnnotationContentType>> annotationSections =
				DefaultMarkupType.getAnnotationContentSections(section,
						SparqlVisType.ANNOTATION_ADD_TO_DOT);
		for (Section<? extends AnnotationContentType> anno : annotationSections) {
			if (anno != null) addToDOT += anno.getText() + "\n";
		}
		if (!addToDOT.equals("")) {
			String alreadyAdded = parameterMap.get(OntoGraphDataBuilder.ADD_TO_DOT);
			if (alreadyAdded != null) {
				addToDOT = addToDOT + alreadyAdded;
			}
			parameterMap.put(OntoGraphDataBuilder.ADD_TO_DOT, addToDOT);
		}

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

			Node toURI = row.getValue(variables.get(2));

			if (fromURI == null || toURI == null || relationURI == null) {
				Log.warning("incomplete query result row: " + row.toString());
				continue;
			}

			ConceptNode fromNode = Utils.createNode(parameters, rdfRepository, uriProvider,
					section, data, fromURI, true);
			String relation = Utils.getConceptName(relationURI, rdfRepository);

			ConceptNode toNode = Utils.createNode(parameters, rdfRepository, uriProvider, section,
					data, toURI, true);

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



	private String getMaster(Section<?> section) {
		return SparqlVisType.getAnnotation(section,
				PackageManager.MASTER_ATTRIBUTE_NAME);
	}

	/**
	 * @created 13.07.2014
	 */
	private void findAndReadConfig(String configName, ArticleManager am, Map<String, String> parameterMap, List<Message> messages, RenderResult string) {
		List<Section<? extends de.knowwe.core.kdom.Type>> sections = Sections.findSectionsOfTypeGlobal(VisConfigType.class, am);
		for (Section<? extends de.knowwe.core.kdom.Type> section : sections ) {
			Section<VisConfigType> s = (Section<VisConfigType>) section;
			String name = VisConfigType.getAnnotation(s, VisConfigType.ANNOTATION_NAME);
			if (name.equals(configName)) {
				readConfig(s, parameterMap, messages, string);
			}
		}
	}

	/**
	 * @created 13.07.2014
	 */
	private void readConfig(Section<VisConfigType> section, Map<String, String> parameterMap, List<Message> messages, RenderResult string) {
		// set css layout to be used
		String layout = VisConfigType.getAnnotation(section, SparqlVisType.ANNOTATION_DESIGN);
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

		// size
		parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_SIZE));

		// format
		String format = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

		// dotApp
		String dotApp = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_DOT_APP);
		parameterMap.put(OntoGraphDataBuilder.DOT_APP, dotApp);

		// renderer
		String rendererType = VisConfigType.getAnnotation(section, SparqlVisType.ANNOTATION_RENDERER);
		parameterMap.put(OntoGraphDataBuilder.RENDERER, rendererType);

		// visualization
		String visualization = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_VISUALIZATION);
		parameterMap.put(OntoGraphDataBuilder.VISUALIZATION, visualization);

		// master
		String master = VisConfigType.getAnnotation(section,
				PackageManager.MASTER_ATTRIBUTE_NAME);
		if (master != null) {
			parameterMap.put(OntoGraphDataBuilder.MASTER, master);
		}

		// language
		String lang = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(OntoGraphDataBuilder.LANGUAGE, lang);
		}

		// labels
		String labelValue = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LABELS);
		parameterMap.put(OntoGraphDataBuilder.USE_LABELS, labelValue);

		// rank direction
		String rankDir = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_RANK_DIR);
		parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, rankDir);

		// link mode
		String linkModeValue = VisConfigType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LINK_MODE);
		if (linkModeValue == null) {
			// default link mode is 'jump'
			linkModeValue = SparqlVisType.LinkMode.jump.name();
		}
		parameterMap.put(OntoGraphDataBuilder.LINK_MODE, linkModeValue);

		// add to dot
		String dotAppPrefix = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_DOT_APP);
		if (dotAppPrefix != null) {
			parameterMap.put(OntoGraphDataBuilder.ADD_TO_DOT, dotAppPrefix + "\n");
		}

		// colors
		String colorRelationName = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_COLORS);

		if (!Strings.isBlank(colorRelationName)) {
			parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, Utils.createColorCodings(colorRelationName, core, "rdf:Property"));
			parameterMap.put(OntoGraphDataBuilder.CLASS_COLOR_CODES, Utils.createColorCodings(colorRelationName, core, "rdfs:Class"));
		}
	}



}
