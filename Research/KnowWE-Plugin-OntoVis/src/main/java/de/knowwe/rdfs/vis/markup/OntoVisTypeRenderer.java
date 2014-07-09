package de.knowwe.rdfs.vis.markup;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;
import de.knowwe.rdfs.vis.markup.sparql.SparqlVisType;
import de.knowwe.rdfs.vis.util.Utils;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Map;

public class OntoVisTypeRenderer extends DefaultMarkupRenderer {

    @Override
    public void renderContents(Section<?> section, UserContext user, RenderResult string) {

        //Section<?> section = Sections.findAncestorOfType(content, DefaultMarkupType.class);

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

        String master = getMaster(section);
        if (master != null) {
            parameterMap.put(OntoGraphDataBuilder.MASTER, master);
        }
        String lang = OntoVisType.getAnnotation(section,
                OntoVisType.ANNOTATION_LANGUAGE);
        if (lang != null) {
            parameterMap.put(OntoGraphDataBuilder.LANGUAGE, lang);
        }

		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, getExcludedRelations(user, section));

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

        // set flag for use of labels
        String labelValue = SparqlVisType.getAnnotation(section,
                SparqlVisType.ANNOTATION_LABELS);
        parameterMap.put(OntoGraphDataBuilder.USE_LABELS, labelValue);

        // set rank direction of graph layout
        String rankDir = SparqlVisType.getAnnotation(section,
                SparqlVisType.ANNOTATION_RANK_DIR);
        parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, rankDir);

        // set link mode
        String linkModeValue = SparqlVisType.getAnnotation(section,
                SparqlVisType.ANNOTATION_LINK_MODE);
        if (linkModeValue == null) {
            // default link mode is 'jump'
            linkModeValue = SparqlVisType.LinkMode.jump.name();
        }
        parameterMap.put(OntoGraphDataBuilder.LINK_MODE, linkModeValue);

        parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, getSuccessors(section));
        parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, getPredecessors(section));

        String dotAppPrefix = SparqlVisType.getAnnotation(section,
                SparqlVisType.ANNOTATION_DOT_APP);
        if (dotAppPrefix != null) {
            parameterMap.put(OntoGraphDataBuilder.ADD_TO_DOT, dotAppPrefix + "\n");
        }

        Rdf2GoCompiler compiler = Compilers.getCompiler(section, Rdf2GoCompiler.class);
        LinkToTermDefinitionProvider uriProvider;

        Rdf2GoCore rdfRepository;
        if (compiler == null) {
            rdfRepository = Rdf2GoCore.getInstance();
            // TODO: completely remove dependency to IncrementalCompiler
            try {
                uriProvider = (LinkToTermDefinitionProvider) Class.forName(
                        "de.knowwe.compile.utils.IncrementalCompilerLinkToTermDefinitionProvider")
                        .newInstance();
            } catch (Exception e) {
                uriProvider = new LinkToTermDefinitionProvider() {
                    @Override
                    public String getLinkToTermDefinition(Identifier name, String masterArticle) {
                        return null;
                    }
                };
            }
        } else {
            rdfRepository = compiler.getRdf2GoCore();
            uriProvider = new PackageCompileLinkToTermDefinitionProvider();
        }

        String colorRelationName = SparqlVisType.getAnnotation(section,
                OntoVisType.ANNOTATION_COLORS);

        if (!Strings.isBlank(colorRelationName)) {
            parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, Utils.createColorCodings(colorRelationName, rdfRepository, "rdf:Property"));
            parameterMap.put(OntoGraphDataBuilder.CLASS_COLOR_CODES, Utils.createColorCodings(colorRelationName, rdfRepository, "rdfs:Class"));
        }

        OntoGraphDataBuilder builder = new OntoGraphDataBuilder(realPath, section, parameterMap,
                uriProvider,
                rdfRepository);
        builder.render(string);
    }

    private String getMaster(Section<?> section) {
        return OntoVisType.getAnnotation(section,
                PackageManager.MASTER_ATTRIBUTE_NAME);
    }

    /**
     * @created 18.08.2012
     */
    private String getPredecessors(Section<?> section) {
        return OntoVisType.getAnnotation(section,
                OntoVisType.ANNOTATION_PREDECESSORS);
    }

    /**
     * @created 18.08.2012
     */
    private String getSuccessors(Section<?> section) {
        return OntoVisType.getAnnotation(section,
                OntoVisType.ANNOTATION_SUCCESSORS);
    }

    /**
     * @created 18.08.2012
     */
    private String getConcept(UserContext user, Section<?> section) {
        String parameter = user.getParameter("concept");
        if (parameter != null) {
            return parameter;
        }
        return OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONCEPT);
    }

	/**
	 * Checks the excluded relations to make sure a namespace is provided.
	 *
	 * @created 09.07.2014
	 */
	private String getExcludedRelations(UserContext user, Section<?> section) {
		String parameter = "";
		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDERELATIONS);
		String defaultExclude = "onto:_checkChain2,onto:_checkChain1";

		if (exclude != null) {
			// Check if namespace is provided for each relation
			String[] excludes = exclude.split(",");
			for (String e : excludes) {
				if (e.contains(":")) {
					parameter = parameter + e + ",";
				}
			}
			parameter += defaultExclude;
		} else {
		 	parameter = defaultExclude;
		}
		return parameter;
	}

}
