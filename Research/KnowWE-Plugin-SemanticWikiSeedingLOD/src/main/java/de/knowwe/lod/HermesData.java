package de.knowwe.lod;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ontoware.aifbcommons.collection.ClosableIterator;
import org.ontoware.rdf2go.model.QueryRow;

import de.knowwe.core.Environment;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.Strings;
import de.knowwe.lod.markup.IgnoreContentType;
import de.knowwe.lod.markup.IgnoreContentType.IgnoreChild;
import de.knowwe.lod.markup.IgnoreContentType.IgnoreConcept;
import de.knowwe.lod.markup.MappingContentType;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * Provides static methods & variables for the hermes wiki.
 * 
 */
public class HermesData {

	// Topic, on which the mappings are saved. (hermes -> dbpedia)
	private static final String wikiTopicMappings = "DBpediaMapping";

	// Topic, on which the ignored attributes are saved.
	private static final String wikiTopicIgnored = "IgnoredAttributes";

	// Topic, on which unparsed triples are saved.
	public static final String wikiTopicNoParse = "NoParse";

	private static final String web = Environment.DEFAULT_WEB;

	// Type which is used to save (object)'s specified in property files.
	private static final String objectType = "rdf:type";

	// Setting to parse namespace for predicate. true cuts it.
	// Also applies to a test if a triple is ignored or noparse.
	private static final boolean cutPredicateNS = true;

	/**
	 * CutPredicateNS.
	 * 
	 * @return Setting to parse namespace for predicate. True cuts it.
	 */
	public static boolean isCutPredicateNS() {
		return cutPredicateNS;
	}

	/**
	 * GetObjectType.
	 * 
	 * @return objectType.
	 */
	public static String get() {
		return objectType;
	}

	/**
	 * GetMappingTopic.
	 * 
	 * @return topic on which the mappings are saved.
	 */
	public static String getMappingTopic() {
		return wikiTopicMappings;
	}

	/**
	 * GetIgnoredTopic.
	 * 
	 * @return Topic on which the ignored attributes are saved.
	 */
	public static String getIgnoredTopic() {
		return wikiTopicIgnored;
	}

	/**
	 * GetNoParseTopic.
	 * 
	 * @return topic on which the noparse triples are saved.
	 */
	public static String getNoParseTopic() {
		return wikiTopicNoParse;
	}

	/**
	 * Searches an corresponding dbepdia concept for a hermes concept, on the
	 * mapping article. (found : dbpediaMapping ? empty string)
	 * 
	 * @param hermes concept.
	 * @return dbpedia concept (http://dbpedia.org/resource/$) or empty string.
	 */
	public static String getDBpediaMapping(String hermes) {

		String topic = getMappingTopic();

		Article article = Environment.getInstance().getArticle(web,
				topic);

		List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
		Sections.findSuccessorsOfType(article.getRootSection(),
				MappingContentType.class, found);

		String dbpediaMapping = "";
		for (Section<MappingContentType> t : found) {
			String temp = t.getChildren().get(0).getText();
			if (temp.matches(hermes + " => .*")) {
				dbpediaMapping = temp.substring(temp.indexOf(" => ") + 4);
				if (temp.matches(".* (?i)http://en\\.wikipedia.*")) {
					String dbFilter = "http://[\\p{Punct}\\p{L}]*";
					dbFilter = "(?i)" + dbFilter;
					Pattern pattern = Pattern.compile(dbFilter);
					Matcher matcher = pattern.matcher(temp);
					while (matcher.find()) {
						String url = matcher.group();
						if (!url.matches("(?i)http://en\\.wikipedia.*")) {
							dbpediaMapping = matcher.group();
						}
					}
				}
			}
		}
		return dbpediaMapping;
	}

	/**
	 * Tests if a given string is a mapped hermes concept.
	 * 
	 * @param conceptname string.
	 * @return boolean.
	 */
	public static boolean isHermesConcept(String conceptname) {

		String topic = getMappingTopic();

		Article article = Environment.getInstance().getArticle(web,
				topic);

		List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
		Sections.findSuccessorsOfType(article.getRootSection(),
				MappingContentType.class, found);

		for (Section<MappingContentType> t : found) {
			String temp = t.getChildren().get(0).getText();
			if (temp.matches(conceptname + " =>.*")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Searches an corresponding hermes concept for a dbpedia concept, on the
	 * mapping article. (found : hermesMapping ? empty string)
	 * 
	 * @param dbpedia http://dbpedia.org/resource/$.
	 * @return hermes concept or empty string.
	 */
	public static String getHermesMapping(String dbpedia) {

		String topic = getMappingTopic();

		Article article = Environment.getInstance().getArticle(web,
				topic);

		List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
		Sections.findSuccessorsOfType(article.getRootSection(),
				MappingContentType.class, found);

		String hermesMapping = "";
		for (Section<MappingContentType> t : found) {
			String temp = t.getChildren().get(0).getText();
			if (temp.matches(".* => " + dbpedia)) {
				hermesMapping = temp.substring(0, temp.indexOf(" => "));
			}
		}

		return hermesMapping;
	}

	/**
	 * Tests if an RDF-triple is saved in the NoParse article.
	 * 
	 * @param concept conceptname.
	 * @param hermestag predicate.
	 * @param value value.
	 * @return boolean.
	 */
	public static boolean isNoParse(String concept, String hermestag,
			String value) {

		String topic = getNoParseTopic();

		if (Environment.getInstance().getWikiConnector()
				.doesArticleExist(topic)) {

			Article article = Environment.getInstance().getArticle(
					web, topic);

			List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
			Sections.findSuccessorsOfType(article.getRootSection(),
					MappingContentType.class, found);

			for (Section<MappingContentType> t : found) {
				String temp = t.getChildren().get(0).getText();
				// System.out.println(concept + "," + hermestag + "," + value);
				if (temp.matches("~\\[" + concept + " " + hermestag + ":: "
						+ value + "\\][\\r\\n]*")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Tests if a RDF-triple is in the IgnoredAttributes article.
	 * 
	 * @param concept conceptname.
	 * @param hermestag predicate.
	 * @param value value.
	 * @return boolean.
	 */
	public static boolean isIgnored(String concept, String hermestag,
			String value) {

		String topic = getIgnoredTopic();

		if (Environment.getInstance().getWikiConnector()
				.doesArticleExist(topic)) {

			Article article = Environment.getInstance().getArticle(
					web, topic);

			List<Section<IgnoreContentType>> found = new Vector<Section<IgnoreContentType>>();
			Sections.findSuccessorsOfType(article.getRootSection(),
					IgnoreContentType.class, found);

			for (Section<IgnoreContentType> t : found) {

				Section<IgnoreConcept> temp = Sections
						.findChildOfType(t, IgnoreConcept.class);
				String sectionConcept = temp.getText().substring(1,
						temp.getText().length() - 1);

				// if concept is in list - test if tag + value also.
				if (sectionConcept.equals(concept)) {

					List<Section<IgnoreChild>> listChilds = Sections
							.findChildrenOfType(t, IgnoreChild.class);

					for (Section<IgnoreChild> child : listChilds) {
						String node = child.getText();
						if (Character
								.isWhitespace(node.charAt(node.length() - 1))) {
							node = node.substring(0, node.length() - 1);
						}
						// If pair is in the list.
						if (node.equals("- " + hermestag + " == " + value)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Method to generate a link element for a hermes concept.
	 * 
	 * @param concept raw conceptname.
	 * @param toLink wiki topic name.
	 * @return string as a Wiki link.
	 */
	public static String linkString(String concept) {

		if (!concept.isEmpty()) {
			String url = Strings.encodeURL(getTopicForConcept(concept));
			String baseURL = Environment.getInstance().getWikiConnector().getBaseUrl();

			return "<a href=\"" + baseURL + "Wiki.jsp?page=" + url
					+ "\" class=\"wikipage\">" + concept + "</a>";
		}
		return "";
	}

	/**
	 * Method to generate a link element for a hermes concept with a specified
	 * id.
	 * 
	 * @param concept raw conceptname.
	 * @param toLink wiki topic name.
	 * @param id id.
	 * @return string as a Wiki link.
	 */
	public static String linkString(String concept, String id) {

		if (!concept.isEmpty()) {
			String url = Strings.encodeURL(getTopicForConcept(concept));
			String baseURL = Environment.getInstance().getWikiConnector().getBaseUrl();
			return "<a id=\"" + id + "\" href=\"" + baseURL + "Wiki.jsp?page=" + url
					+ "\" class=\"wikipage\">" + concept + "</a>";
		}
		return "";
	}

	/**
	 * Tests if the hermes RDF-store contains the specified triple - For non-URI
	 * values, it only tests if the store contains the predicate for the
	 * subject.
	 * 
	 * @param concept conceptname (unmodified string.).
	 * @param predicate predicate.
	 * @param value value.
	 * @return boolean.
	 */
	public static boolean storeContains(String concept, String predicate,
			String value) {

		concept = Strings.encodeURL(concept);
		String ask = "";

		if (value.matches("!\\$ConceptLink:: .*") || predicate.equals(objectType)) {

			String namespace = Rdf2GoCore.localns;
			concept = namespace + concept;
			String objectname = value;
			if (value.matches("!\\$ConceptLink:: .*")) {
				objectname = value.substring(16);
			}
			else {
				objectname = objectname.substring(objectname.indexOf(":") + 1);
			}

			objectname = Strings.encodeURL(objectname);
			objectname = namespace + objectname;

			ask = "ASK {<" + concept + "> " + predicate + " <" + objectname + ">}";
		}
		else {

			String namespace = Rdf2GoCore.localns;
			concept = namespace + concept;

			ask = "ASK {<" + concept + "> " + predicate + " ?temp}";
		}

		return Rdf2GoCore.getInstance().sparqlAsk(ask);
	}

	/**
	 * Tests if the hermes RDF-store contains the specified concept.
	 * 
	 * @param concept conceptname (unmodified string).
	 * @return boolean.
	 */
	public static boolean storeContains(String concept) {

		concept = Strings.encodeURL(concept);
		String namespace = Rdf2GoCore.localns;
		concept = namespace + concept;
		String ask = "";

		ask = "ASK {<" + concept + "> rdf:type ?has}";

		return Rdf2GoCore.getInstance().sparqlAsk(ask);
	}

	/**
	 * Gets the corresponding topic (articlename) for a given concept.
	 * 
	 * @param concept conceptname.
	 * @return topic or empty string.
	 */
	public static String getTopicForConcept(String concept) {

		concept = Strings.encodeURL(concept);
		String namespace = Rdf2GoCore.localns;
		concept = namespace + concept;

		String query = "SELECT ?x ?y ?z WHERE {?y rdf:subject <" + concept
				+ "> ." + "?y rdf:predicate rdf:type ."
				+ "?y rdfs:isDefinedBy ?z ." + "?z ns:hasTopic ?x }";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);

		String topic = "";

		while (result.hasNext()) {
			QueryRow row = result.next();
			topic = row.getValue("x").toString();
			topic = Strings.decodeURL(topic);
			topic = topic.substring(topic.indexOf("#") + 1);
		}
		return topic;
	}

	/**
	 * Queries hermes RDF-Store for the given properties file attributes.
	 * 
	 * @return predicate value.
	 */
	public static List<String> queryStore(String concept, String predicate) {

		concept = Strings.encodeURL(concept);

		String namespace = Rdf2GoCore.localns;
		concept = namespace + concept;

		String query = "SELECT ?x WHERE {<" + concept
				+ "> " + predicate + " ?x.}";

		ClosableIterator<QueryRow> result = Rdf2GoCore.getInstance().sparqlSelectIt(query);

		List<String> values = new ArrayList<String>();
		String value = "";

		while (result.hasNext()) {
			QueryRow row = result.next();
			value = row.getValue("x").toString();
			value = Strings.decodeURL(value);
			value = value.substring(value.indexOf("#") + 1);
			values.add(value);
		}
		return values;
	}
}
