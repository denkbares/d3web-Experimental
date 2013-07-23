package de.knowwe.rdfs.vis.markup.sparql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryResultTable;
import org.ontoware.rdf2go.model.QueryRow;
import org.ontoware.rdf2go.model.node.LanguageTagLiteral;
import org.ontoware.rdf2go.model.node.Literal;
import org.ontoware.rdf2go.model.node.Node;

import de.knowwe.core.Environment;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.rdf2go.utils.Rdf2GoUtils;
import de.knowwe.rdfs.vis.ConceptNode;
import de.knowwe.rdfs.vis.Edge;
import de.knowwe.rdfs.vis.GraphVisualizationRenderer;
import de.knowwe.rdfs.vis.RenderingCore;
import de.knowwe.rdfs.vis.RenderingCore.NODE_TYPE;
import de.knowwe.rdfs.vis.SubGraphData;
import de.knowwe.rdfs.vis.dot.DOTVisualizationRenderer;
import de.knowwe.rdfs.vis.markup.IncrementalCompilerLinkToTermDefinitionProvider;
import de.knowwe.rdfs.vis.util.Utils;

public class SparqlVisTypeRenderer implements Renderer {

	@Override
	public void render(Section<?> content, UserContext user, RenderResult string) {

		Section<SparqlVisType> section = Sections.findAncestorOfType(content,
				SparqlVisType.class);

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only

		String realPath = servletContext.getRealPath("");

		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(RenderingCore.REAL_PATH, realPath);

		parameterMap.put(RenderingCore.SECTION_ID, section.getID());

		parameterMap.put(RenderingCore.GRAPH_SIZE, SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_SIZE));

		String format = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(RenderingCore.FORMAT, format);

		String dotApp = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_DOT_APP);
		parameterMap.put(RenderingCore.DOT_APP, dotApp);

		String rendererType = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_RENDERER);
		parameterMap.put(RenderingCore.RENDERER, rendererType);

		String visualization = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_VISUALIZATION);
		parameterMap.put(RenderingCore.VISUALIZATION, visualization);

		String master = getMaster(user, section);
		if (master != null) {
			parameterMap.put(RenderingCore.MASTER, master);
		}
		String lang = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(RenderingCore.LANGUAGE, lang);
		}

		String addToDOT = "";
		List<Section<? extends AnnotationContentType>> annotationSections =
				DefaultMarkupType.getAnnotationContentSections(section,
						SparqlVisType.ANNOTATION_ADD_TO_DOT);
		for (Section<? extends AnnotationContentType> anno : annotationSections) {
			if (anno != null) addToDOT += anno.getText() + "\n";
		}
		parameterMap.put(RenderingCore.ADD_TO_DOT, addToDOT);

		LinkToTermDefinitionProvider uriProvider;
		Rdf2GoCore rdfRepository = null;
		if (master != null) {
			rdfRepository = Rdf2GoCore.getInstance(Environment.DEFAULT_WEB, master);
			uriProvider = new PackageCompileLinkToTermDefinitionProvider();
		}
		else {
			rdfRepository = Rdf2GoCore.getInstance();
			uriProvider = new IncrementalCompilerLinkToTermDefinitionProvider();
		}

		String sparqlString = Rdf2GoUtils.createSparqlString(content);

		QueryResultTable resultSet = rdfRepository.sparqlSelect(
				sparqlString);

		SubGraphData data = convertToGraph(resultSet, parameterMap, rdfRepository, uriProvider,
				section);

		GraphVisualizationRenderer graphRenderer = new DOTVisualizationRenderer(data, parameterMap);
		graphRenderer.generateSource();
		String renderedContent = graphRenderer.getHTMLIncludeSnipplet();
		string.appendHtml(renderedContent);

	}

	/**
	 * 
	 * @created 23.07.2013
	 * @param resultSet
	 * @return
	 */
	private SubGraphData convertToGraph(QueryResultTable resultSet, Map<String, String> parameters, Rdf2GoCore rdfRepository, LinkToTermDefinitionProvider uriProvider, Section<?> section) {
		SubGraphData data = new SubGraphData();
		List<String> variables = resultSet.getVariables();
		if (variables.size() < 3) return null;
		ClosableIterator<QueryRow> iterator = resultSet.iterator();
		while (iterator.hasNext()) {
			QueryRow row = iterator.next();

			Node fromURI = row.getValue(variables.get(0));
			String from = RenderingCore.getConceptName(fromURI);

			Node relationURI = row.getValue(variables.get(1));
			String relation = RenderingCore.getConceptName(relationURI);

			Node toURI = row.getValue(variables.get(2));
			String to = RenderingCore.getConceptName(toURI);

			ConceptNode fromNode = createNode(parameters, rdfRepository, uriProvider,
					section, data, fromURI, from);

			ConceptNode toNode = createNode(parameters, rdfRepository, uriProvider, section,
					data, toURI, to);

			String relationLabel = createRelationLabel(parameters, rdfRepository, relationURI, relation);

			Edge newLineRelationsKey = new Edge(fromNode, relationLabel, toNode);

			data.addEdge(newLineRelationsKey);

		}

		return data;
	}

	private String createRelationLabel(Map<String, String> parameters, Rdf2GoCore rdfRepository, Node relationURI, String relation) {
		// is the node a literal ?
		Literal toLiteral = null;
		LanguageTagLiteral languageTagLiteral = null;
		try {
			toLiteral = relationURI.asLiteral();
			languageTagLiteral = relationURI.asLanguageTagLiteral();
		}
		catch (ClassCastException e) {
		}

		String relationName = relation;
		if (toLiteral != null) {
			if (languageTagLiteral != null) {
				relationName = languageTagLiteral.getValue().replaceAll(
						languageTagLiteral.getLanguageTag(), "");
			}
			else {
				relationName = toLiteral.toString();
			}
		}
		else {
			// if it is no literal look for label for the URI
			String relationLabel = Utils.getRDFSLabel(
					relationURI.asURI(), rdfRepository,
					parameters.get(RenderingCore.LANGUAGE));
			if (relationLabel != null) {
				relationName = relationLabel;
			}
		}
		return relationName;
	}

	/**
	 * 
	 * @created 23.07.2013
	 * @param parameters
	 * @param rdfRepository
	 * @param uriProvider
	 * @param section
	 * @param data
	 * @param toURI
	 * @param to
	 * @param toLiteral
	 * @param languageTagLiteral
	 * @return
	 */
	private ConceptNode createNode(Map<String, String> parameters, Rdf2GoCore rdfRepository, LinkToTermDefinitionProvider uriProvider, Section<?> section, SubGraphData data, Node toURI, String to) {

		// is the node a literal ?
		Literal toLiteral = null;
		LanguageTagLiteral languageTagLiteral = null;
		try {
			toLiteral = toURI.asLiteral();
			languageTagLiteral = toURI.asLanguageTagLiteral();
		}
		catch (ClassCastException e) {
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
						parameters.get(RenderingCore.LANGUAGE));
			}
			if (label == null && toLiteral == null) {
				label = to;
			}
			if (toLiteral != null) {
				type = NODE_TYPE.LITERAL;
				if (languageTagLiteral != null) {
					label = languageTagLiteral.getValue().replaceAll(
							languageTagLiteral.getLanguageTag(), "");
				}
				else {

					label = toLiteral.toString();
				}
				to = "literal" + System.currentTimeMillis(); // some unique name
			}
			toNode = new ConceptNode(to, type, createConceptURL(to, parameters,
					section,
					uriProvider), label);
			data.addConcept(toNode);
		}
		return toNode;
	}

	/**
	 * 
	 * @created 24.04.2013
	 * @param user
	 * @param section
	 * @return
	 */
	private String getMaster(UserContext user, Section<?> section) {
		return SparqlVisType.getAnnotation(section,
				PackageManager.ANNOTATION_MASTER);
	}

	private String createConceptURL(String to, Map<String, String> parameters, Section<?> s, LinkToTermDefinitionProvider uriProvider) {
		if (parameters.get(RenderingCore.LINK_MODE) != null) {
			if (parameters.get(RenderingCore.LINK_MODE).equals(RenderingCore.LINK_MODE_BROWSE)) {
				return uriProvider.getLinkToTermDefinition(to, parameters.get(RenderingCore.MASTER));
			}
		}
		return RenderingCore.createBaseURL() + "?page=" + RenderingCore.getSectionTitle(s)
				+ "&concept=" + to;
	}

}
