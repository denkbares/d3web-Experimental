package de.knowwe.typevis.markup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.AnnotationContentType;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.typevis.TypeGraphDataBuilder;
import de.knowwe.visualization.GraphDataBuilder.Renderer;

public class TypeVisTypeRenderer extends DefaultMarkupRenderer {

	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult string) {

		ServletContext servletContext = user.getServletContext();
		if (servletContext == null) return; // at wiki startup only

		String realPath = servletContext.getRealPath("");

		Map<String, String> parameterMap = new HashMap<String, String>();

		parameterMap.put(TypeGraphDataBuilder.GRAPH_SIZE, TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_SIZE));

		String format = TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(TypeGraphDataBuilder.FORMAT, format);

		String dotApp = TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_DOT_APP);
		parameterMap.put(TypeGraphDataBuilder.DOT_APP, dotApp);

		parameterMap.put(TypeGraphDataBuilder.RENDERER, Renderer.dot.name());

		String visualization = TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_VISUALIZATION);
		parameterMap.put(TypeGraphDataBuilder.VISUALIZATION, visualization);

		parameterMap.put(TypeGraphDataBuilder.CONCEPT, getConcept(user, section));

		String master = getMaster(user, section);
		if (master != null) {
			parameterMap.put(TypeGraphDataBuilder.MASTER, master);
		}
		String lang = TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(TypeGraphDataBuilder.LANGUAGE, lang);
		}

		String exclude = null;
		parameterMap.put(TypeGraphDataBuilder.EXCLUDED_RELATIONS, exclude);

		String excludeNodes = TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_EXCLUDENODES);
		parameterMap.put(TypeGraphDataBuilder.EXCLUDED_NODES, excludeNodes);

		String outgoingEdges = TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_OUTGOING_EDGES);
		parameterMap.put(TypeGraphDataBuilder.SHOW_OUTGOING_EDGES, outgoingEdges);

		String classes = null;
		parameterMap.put(TypeGraphDataBuilder.SHOW_CLASSES, classes);

		String props = null;
		parameterMap.put(TypeGraphDataBuilder.SHOW_PROPERTIES, props);

		parameterMap.put(TypeGraphDataBuilder.REQUESTED_DEPTH, getSuccessors(section));
		parameterMap.put(TypeGraphDataBuilder.REQUESTED_HEIGHT, null);

		String addToDOT = "";
		List<Section<? extends AnnotationContentType>> annotationSections =
				DefaultMarkupType.getAnnotationContentSections(section,
						TypeVisType.ANNOTATION_ADD_TO_DOT);
		for (Section<? extends AnnotationContentType> anno : annotationSections) {
			if (anno != null) addToDOT += anno.getText() + "\n";
		}
		parameterMap.put(TypeGraphDataBuilder.ADD_TO_DOT, addToDOT);

		LinkToTermDefinitionProvider uriProvider;
		if (master != null) {
			uriProvider = new PackageCompileLinkToTermDefinitionProvider();
		}
		else {
			uriProvider = new IncrementalCompilerLinkToTermDefinitionProvider();
		}

		TypeGraphDataBuilder builder = new TypeGraphDataBuilder(realPath, section, parameterMap,
				uriProvider);
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
		return TypeVisType.getAnnotation(section,
				PackageManager.ANNOTATION_MASTER);
	}

	private String getSuccessors(Section<?> section) {
		return TypeVisType.getAnnotation(section,
				TypeVisType.ANNOTATION_SUCCESSORS);
	}

	private String getConcept(UserContext user, Section<?> section) {
		String parameter = null;
		parameter = user.getParameter("concept");
		if (parameter != null) {
			return parameter;
		}
		return TypeVisType.getAnnotation(section, TypeVisType.ANNOTATION_CONCEPT);
	}

}
