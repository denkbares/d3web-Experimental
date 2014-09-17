package de.knowwe.rdfs.vis.markup;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import de.d3web.strings.Identifier;
import de.d3web.strings.Strings;
import de.knowwe.core.ArticleManager;
import de.knowwe.core.Environment;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.compile.packaging.PackageManager;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.user.UserContext;
import de.knowwe.core.utils.LinkToTermDefinitionProvider;
import de.knowwe.core.utils.PackageCompileLinkToTermDefinitionProvider;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupRenderer;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.rdf2go.Rdf2GoCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.vis.OntoGraphDataBuilder;
import de.knowwe.rdfs.vis.PreRenderWorker;
import de.knowwe.rdfs.vis.markup.sparql.SparqlVisType;
import de.knowwe.rdfs.vis.util.Utils;

public class OntoVisTypeRenderer extends DefaultMarkupRenderer implements PreRenderer {

	private Rdf2GoCore rdfRepository;
	private LinkToTermDefinitionProvider uriProvider;
	private String realPath;

	private String format;

	@Override
	public void renderContents(Section<?> section, UserContext user, RenderResult string) {
		PreRenderWorker.getInstance().preRenderSectionAndWait(this, section, user, string);
	}

	/**
	 * @created 13.07.2014
	 */
	private void findAndReadConfig(String configName, ArticleManager am, Map<String, String> parameterMap) {
		Collection<Section<? extends Type>> sections = Sections.successors(am, VisConfigType.class);
		for (Section<? extends Type> section : sections) {
			Section<VisConfigType> s = Sections.cast(section, VisConfigType.class);
			String name = VisConfigType.getAnnotation(s, VisConfigType.ANNOTATION_NAME);
			if (name.equals(configName)) {
				readConfig(s, parameterMap);
			}
		}
	}

	/**
	 * @created 13.07.2014
	 */
	private void readConfig(Section<VisConfigType> section, Map<String, String> parameterMap) {
		// size
		parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_SIZE));

		parameterMap.put(OntoGraphDataBuilder.GRAPH_WIDTH, VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_WIDTH));

		parameterMap.put(OntoGraphDataBuilder.GRAPH_HEIGHT, VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_HEIGHT));

		// format
		format = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
		}
		parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

		// dot app
		String dotApp = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_DOT_APP);
		parameterMap.put(OntoGraphDataBuilder.DOT_APP, dotApp);

		// renderer
		String rendererType = VisConfigType.getAnnotation(section, OntoVisType.ANNOTATION_RENDERER);
		parameterMap.put(OntoGraphDataBuilder.RENDERER, rendererType);

		// visualization
		String visualization = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_VISUALIZATION);
		parameterMap.put(OntoGraphDataBuilder.VISUALIZATION, visualization);

		// master
		String master = VisConfigType.getAnnotation(section,
				PackageManager.MASTER_ATTRIBUTE_NAME);
		if (master != null) {
			parameterMap.put(OntoGraphDataBuilder.MASTER, master);
		}

		// language
		String lang = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_LANGUAGE);
		if (lang != null) {
			parameterMap.put(OntoGraphDataBuilder.LANGUAGE, lang);
		}

		// excludes
		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, getExcludedRelations(section));

		String excludeNodes = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDENODES);
		parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, excludeNodes);

		// filters
		String filteredClasses = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_FILTERCLASSES);
		parameterMap.put(OntoGraphDataBuilder.FILTERED_CLASSES, filteredClasses);

		String filteredRelations = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_FILTERRELATIONS);
		parameterMap.put(OntoGraphDataBuilder.FILTERED_RELATIONS, filteredRelations);

		// outgoing edges
		String outgoingEdges = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_OUTGOING_EDGES);
		parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, outgoingEdges);

		// inverse Realtions
		String showInverse = VisConfigType.getAnnotation(section, OntoVisType.ANNOTATION_SHOWINVERSE);
		parameterMap.put(OntoGraphDataBuilder.SHOW_INVERSE, showInverse);

		// show classes
		String classes = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWCLASSES);
		parameterMap.put(OntoGraphDataBuilder.SHOW_CLASSES, classes);

		// show properties
		String props = VisConfigType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWPROPERTIES);
		parameterMap.put(OntoGraphDataBuilder.SHOW_PROPERTIES, props);

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

		// successors and predecessors
		parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, getSuccessors(section));
		parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, getPredecessors(section));

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
			parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, Utils.createColorCodings(colorRelationName, rdfRepository, "rdf:Property"));
			parameterMap.put(OntoGraphDataBuilder.CLASS_COLOR_CODES, Utils.createColorCodings(colorRelationName, rdfRepository, "rdfs:Class"));
		}
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
		if (user != null) {
			String parameter = user.getParameter("concept");
			if (parameter != null) {
				return parameter;
			}
		}
		return OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONCEPT);
	}

	/**
	 * Checks the excluded relations to make sure a namespace is provided.
	 *
	 * @created 09.07.2014
	 */
	private String getExcludedRelations(Section<?> section) {
		String parameter = "";
		String exclude = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDERELATIONS);
		String defaultExclude = "";
		if (this.rdfRepository.getModelType().equals(Rdf2GoCore.Rdf2GoModel.SWIFTOWLIM)) {
			defaultExclude += "onto:_checkChain2,onto:_checkChain1";
		}

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

	@Override
	public void preRender(Section<?> section, UserContext user, RenderResult string) {
		initialise(section, user);

		Map<String, String> parameterMap = new HashMap<String, String>();

		if (Thread.currentThread().isInterrupted()) return;

		// find and read config file if defined
		String configName = OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_CONFIG);

		if (configName != null) {
			findAndReadConfig(configName.trim(), section.getArticleManager(), parameterMap);
		}

		String size = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SIZE);
		if (size != null) {
			parameterMap.put(OntoGraphDataBuilder.GRAPH_SIZE, size);
		}

		String width = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_WIDTH);
		if (width != null) {
			parameterMap.put(OntoGraphDataBuilder.GRAPH_WIDTH, width);
		}

		String height = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_HEIGHT);
		if (height != null) {
			parameterMap.put(OntoGraphDataBuilder.GRAPH_HEIGHT, height);
		}

		String format = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FORMAT);
		if (format != null) {
			format = format.toLowerCase();
			this.format = format;
			parameterMap.put(OntoGraphDataBuilder.FORMAT, format);
		}

		String dotApp = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_DOT_APP);
		if (dotApp != null) {
			parameterMap.put(OntoGraphDataBuilder.DOT_APP, dotApp);
		}

		String rendererType = OntoVisType.getAnnotation(section, OntoVisType.ANNOTATION_RENDERER);
		if (rendererType != null) {
			parameterMap.put(OntoGraphDataBuilder.RENDERER, rendererType);
		}

		String visualization = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_VISUALIZATION);
		if (visualization != null) {
			parameterMap.put(OntoGraphDataBuilder.VISUALIZATION, visualization);
		}

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

		String excludedRelations = getExcludedRelations(section);
		if (excludedRelations != null) {
			String allExcludes;
			String alreadyExcluded = parameterMap.get(OntoGraphDataBuilder.EXCLUDED_RELATIONS);
			if (alreadyExcluded == null) {
				allExcludes = excludedRelations;
			} else {
				if (!alreadyExcluded.trim().endsWith(",")) {
					alreadyExcluded += ", ";
				}
				allExcludes = alreadyExcluded + excludedRelations;
			}
			parameterMap.put(OntoGraphDataBuilder.EXCLUDED_RELATIONS, allExcludes);
		}

		String excludeNodes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_EXCLUDENODES);
		if (excludeNodes != null) {
			String allExcludes;
			String alreadyExcluded = parameterMap.get(OntoGraphDataBuilder.EXCLUDED_NODES);
			if (alreadyExcluded == null) {
				allExcludes = excludeNodes;
			} else {
				if (!alreadyExcluded.trim().endsWith(",")) {
					alreadyExcluded += ", ";
				}
				allExcludes = alreadyExcluded + excludeNodes;
			}
			parameterMap.put(OntoGraphDataBuilder.EXCLUDED_NODES, allExcludes);
		}

		String filteredClasses = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FILTERCLASSES);
		if (filteredClasses != null) {
			String allFilters;
			String alreadyFiltered = parameterMap.get(OntoGraphDataBuilder.FILTERED_CLASSES);
			if (alreadyFiltered == null) {
				allFilters = filteredClasses;
			} else {
				if (!alreadyFiltered.trim().endsWith(",")) {
					alreadyFiltered += ", ";
				}
				allFilters = alreadyFiltered + filteredClasses;
			}
			parameterMap.put(OntoGraphDataBuilder.FILTERED_CLASSES, allFilters);
		}

		String filteredRelations = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_FILTERRELATIONS);
		if (filteredRelations != null) {
			String allFilters;
			String alreadyFiltered = parameterMap.get(OntoGraphDataBuilder.FILTERED_RELATIONS);
			if (alreadyFiltered == null) {
				allFilters = filteredRelations;
			} else {
				if (!alreadyFiltered.trim().endsWith(",")) {
					alreadyFiltered += ", ";
				}
				allFilters = alreadyFiltered + filteredRelations;
			}
			parameterMap.put(OntoGraphDataBuilder.FILTERED_CLASSES, allFilters);
			parameterMap.put(OntoGraphDataBuilder.FILTERED_RELATIONS, allFilters);
		}

		String outgoingEdges = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_OUTGOING_EDGES);
		if (outgoingEdges != null) {
			parameterMap.put(OntoGraphDataBuilder.SHOW_OUTGOING_EDGES, outgoingEdges);
		}

		String showInverse = VisConfigType.getAnnotation(section, OntoVisType.ANNOTATION_SHOWINVERSE);
		if (showInverse != null) {
			parameterMap.put(OntoGraphDataBuilder.SHOW_INVERSE, showInverse);
		}

		String classes = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWCLASSES);
		if (classes != null) {
			parameterMap.put(OntoGraphDataBuilder.SHOW_CLASSES, classes);
		}

		String props = OntoVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_SHOWPROPERTIES);
		if (props != null) {
			parameterMap.put(OntoGraphDataBuilder.SHOW_PROPERTIES, props);
		}

		// set flag for use of labels
		String labelValue = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LABELS);
		if (labelValue != null) {
			parameterMap.put(OntoGraphDataBuilder.USE_LABELS, labelValue);
		}

		// set rank direction of graph layout
		String rankDir = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_RANK_DIR);
		if (rankDir != null) {
			parameterMap.put(OntoGraphDataBuilder.RANK_DIRECTION, rankDir);
		}

		// set link mode
		String linkModeAnnotationValue = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_LINK_MODE);
		if (linkModeAnnotationValue != null) {
			// default link mode is 'jump'
			parameterMap.put(OntoGraphDataBuilder.LINK_MODE, linkModeAnnotationValue);
		}
		// set default if none defined yet
		if (parameterMap.get(OntoGraphDataBuilder.LINK_MODE) == null) {
			parameterMap.put(OntoGraphDataBuilder.LINK_MODE, SparqlVisType.LinkMode.jump.name());
		}

		String successors = getSuccessors(section);
		if (successors != null) {
			parameterMap.put(OntoGraphDataBuilder.REQUESTED_DEPTH, successors);
		}

		String predecessors = getPredecessors(section);
		if (predecessors != null) {
			parameterMap.put(OntoGraphDataBuilder.REQUESTED_HEIGHT, predecessors);
		}

		String dotAppPrefix = SparqlVisType.getAnnotation(section,
				SparqlVisType.ANNOTATION_DOT_APP);
		if (dotAppPrefix != null) {
			parameterMap.put(OntoGraphDataBuilder.ADD_TO_DOT, dotAppPrefix + "\n");
		}

		String colorRelationName = SparqlVisType.getAnnotation(section,
				OntoVisType.ANNOTATION_COLORS);

		if (!Strings.isBlank(colorRelationName)) {
			parameterMap.put(OntoGraphDataBuilder.RELATION_COLOR_CODES, Utils.createColorCodings(colorRelationName, rdfRepository, "rdf:Property"));
			parameterMap.put(OntoGraphDataBuilder.CLASS_COLOR_CODES, Utils.createColorCodings(colorRelationName, rdfRepository, "rdfs:Class"));
		}

		// create file ID
		setFileID(section, parameterMap);

		if (Thread.currentThread().isInterrupted()) return;

		createGraphAndAppendHTMLIncludeSnipplet(string, realPath, section, parameterMap, rdfRepository, uriProvider);
	}

	private void setFileID(Section<?> section, Map<String, String> parameterMap) {
		String textHash = String.valueOf(section.getText().hashCode());

		OntologyCompiler ontoCompiler = Compilers.getCompiler(section, OntologyCompiler.class);
		if (ontoCompiler == null) return;
		String compHash = String.valueOf(ontoCompiler.getCompileSection().getTitle().hashCode());

		String fileID = "_" + textHash + "_" + compHash;

		parameterMap.put(OntoGraphDataBuilder.FILE_ID, fileID);
	}

	private void createGraphAndAppendHTMLIncludeSnipplet(RenderResult string, String realPath, Section<?> section, Map<String, String> parameterMap, Rdf2GoCore rdfRepository, LinkToTermDefinitionProvider uriProvider) {
		OntoGraphDataBuilder builder = new OntoGraphDataBuilder(realPath, section, parameterMap,
				uriProvider,
				rdfRepository);
		builder.render(string);
	}

	@Override
	public void cacheGraph(Section<?> section, RenderResult string) {
		initialise(section, null);

		Map<String, String> parameterMap = new HashMap<>();
		setFileID(section, parameterMap);
		parameterMap.put(OntoGraphDataBuilder.FORMAT, format);

		createGraphAndAppendHTMLIncludeSnipplet(string, realPath, section, parameterMap, rdfRepository, uriProvider);
	}

	private void initialise(Section<?> section, UserContext user) {
		if (user != null) {
			ServletContext servletContext = user.getServletContext();
			if (servletContext == null) return; // at wiki startup only

			realPath = servletContext.getRealPath("");
		} else {
			realPath = Environment.getInstance().getWikiConnector().getServletContext().getRealPath("");
		}

		Rdf2GoCompiler compiler = Compilers.getCompiler(section, Rdf2GoCompiler.class);

		if (compiler == null) {
			rdfRepository = Rdf2GoCore.getInstance();
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
		} else {
			rdfRepository = compiler.getRdf2GoCore();
			uriProvider = new PackageCompileLinkToTermDefinitionProvider();
		}
	}
}
