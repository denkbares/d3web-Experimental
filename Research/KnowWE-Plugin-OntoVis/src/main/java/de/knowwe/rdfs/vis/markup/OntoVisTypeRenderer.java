package de.knowwe.rdfs.vis.markup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import de.knowwe.core.Environment;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.utils.LinkToTermDefinitionProvider;
import de.knowwe.rdf2go.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.rdfs.vis.RenderingCore;

public class OntoVisTypeRenderer extends DefaultMarkupRenderer {

	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult string) {

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only

		String realPath = servletContext.getRealPath("");

		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(RenderingCore.GRAPH_SIZE, OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SIZE));

		String format = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(RenderingCore.FORMAT, format);

		String dotApp = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_DOT_APP);
		parameterMap.put(RenderingCore.DOT_APP, dotApp);

		parameterMap.put(RenderingCore.CONCEPT, getConcept(user, section));

		String master = getMaster(user, section);
		if (master != null) {
			parameterMap.put(RenderingCore.MASTER, master);
		}
		String lang = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(RenderingCore.LANGUAGE, lang);
		}

		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDERELATIONS);
		parameterMap.put(RenderingCore.EXCLUDED_RELATIONS, exclude);

		String excludeNodes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDENODES);
		parameterMap.put(RenderingCore.EXCLUDED_NODES, excludeNodes);

		String classes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWCLASSES);
		parameterMap.put(RenderingCore.SHOW_CLASSES, classes);

		String props = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWPROPERTIES);
		parameterMap.put(RenderingCore.SHOW_PROPERTIES, props);

		parameterMap.put(RenderingCore.REQUESTED_DEPTH, getSuccessors(section));
		parameterMap.put(RenderingCore.REQUESTED_HEIGHT, getPredecessors(section));

		String addToDOT = "";
		List<Section<? extends AnnotationContentType>> annotationSections =
				DefaultMarkupType.getAnnotationContentSections(section,
						OntoVisType.ANNOTATION_ADD_TO_DOT);
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
		RenderingCore renderer = new RenderingCore(realPath, section, parameterMap, uriProvider,
				rdfRepository);
		renderer.render(string);

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
				PackageManager.ANNOTATION_MASTER);
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
