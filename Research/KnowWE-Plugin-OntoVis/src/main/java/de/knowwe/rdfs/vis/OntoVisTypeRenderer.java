package de.knowwe.rdfs.vis;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;

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

		parameterMap.put(RenderingCore.CONCEPT, getConcept(user, section));

		String master = getMaster(user, section);
		if (master != null) {
			parameterMap.put(RenderingCore.MASTER, master);
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

		RenderingCore renderer = new RenderingCore(realPath, section, parameterMap);
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
				OntoVisType.ANNOTATION_MASTER);
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
