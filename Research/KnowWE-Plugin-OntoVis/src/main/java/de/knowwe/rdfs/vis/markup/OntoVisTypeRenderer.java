package de.knowwe.rdfs.vis.markup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;

public class OntoVisTypeRenderer extends DefaultMarkupRenderer {

	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult string) {

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only

		String realPath = servletContext.getRealPath("");

		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SIZE));

		String format = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

		String dotApp = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_DOT_APP);
		parameterMap.put(OntoGraphDataBuilder.DOT_APP, dotApp);

		String rendererType = OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_RENDERER);
		parameterMap.put(OntoGraphDataBuilder.RENDERER, rendererType);

		String visualization = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_VISUALIZATION);
		parameterMap.put(OntoGraphDataBuilder.VISUALIZATION, visualization);

		parameterMap.put(OntoGraphDataBuilder.CONCEPT, getConcept(user, section));

		String master = getMaster(user, section);
		if (master != null) {
			parameterMap.put(OntoGraphDataBuilder.MASTER, master);
		}
		String lang = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(OntoGraphDataBuilder.LANGUAGE, lang);
		}

		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDERELATIONS);
		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, exclude);

		String excludeNodes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDENODES);
		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, excludeNodes);

		String filteredClasses = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FILTERCLASSES);
		parameterMap.put(OntoGraphDataBuilder.FILTERED_CLASSES, filteredClasses);

		String filteredRelations = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FILTERRELATIONS);
		parameterMap.put(OntoGraphDataBuilder.FILTERED_RELATIONS, filteredRelations);

		String outgoingEdges = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_OUTGOING_EDGES);
		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, outgoingEdges);

		String classes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWCLASSES);
		parameterMap.put(OntoGraphDataBuilder.SHOW_CLASSES, classes);

		String props = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWPROPERTIES);
		parameterMap.put(OntoGraphDataBuilder.SHOW_PROPERTIES, props);

		parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, getSuccessors(section));
		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, getPredecessors(section));

		String addToDOT = "";
		List<Section<? extends AnnotationContentType>> annotationSections =
				DefaultMarkupType.getAnnotationContentSections(section,
						OntoVisType.ANNOTATION_ADD_TO_DOT);
		for (Section<? extends AnnotationContentType> anno : annotationSections) {
			if (anno != null) addToDOT += anno.getText() + "\n";
		}
		parameterMap.put(OntoGraphDataBuilder.ADD_TO_DOT, addToDOT);

		LinkToTermDefinitionProvider uriProvider;
		Rdf2GoCore rdfRepository = null;
		if (master != null) {
			rdfRepository = Compilers.getCompiler(section, Rdf2GoCompiler.class).getRdf2GoCore();
			uriProvider = new PackageCompileLinkToTermDefinitionProvider();
		}
		else {
			rdfRepository = Rdf2GoCore.getInstance();
			uriProvider = new IncrementalCompilerLinkToTermDefinitionProvider();
		}
		// RenderingCore renderer = new RenderingCore(realPath, section,
		// parameterMap, uriProvider,
		// rdfRepository);
		OntoGraphDataBuilder builder = new OntoGraphDataBuilder(realPath, section, parameterMap,
				uriProvider,
				rdfRepository);
		builder.render(string);
	}

	/**
	 * 
	 * @created 24.04.2013
	 * @param user
	 * @param section
	 * @return
	 */
	private String getMaster(UserContext user, Section<?> section) {
		return OntoVisType.getAnnotation(section,
				PackageManager.MASTER_ATTRIBUTE_NAME);
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private String getPredecessors(Section<?> section) {
		return OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_PREDECESSORS);
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private String getSuccessors(Section<?> section) {
		return OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SUCCESSORS);
	}

	/**
	 * 
	 * @created 18.08.2012
	 */
	private String getConcept(UserContext user, Section<?> section) {
		String parameter = null;
		parameter = user.getParameter("concept");
		if (parameter != null) {
			return parameter;
		}
		return OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONCEPT);
	}

}
