package de.d3web.we.lod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Vector;

import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.core.semantic.UpperOntology;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.lod.markup.MappingContentType;
import de.d3web.we.lod.markup.IgnoreContentType;
import de.d3web.we.lod.markup.IgnoreContentType.IgnoreChild;
import de.d3web.we.lod.markup.IgnoreContentType.IgnoreConcept;
import de.knowwe.semantic.sparql.SPARQLUtil;

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

	private static final String web = KnowWEEnvironment.DEFAULT_WEB;

	// Type which is used to save (object)'s specified in property files.
	private static final String objectType = "rdf:type";

	/**
	 * GetObjectType.
	 * 
	 * @return objectType.
	 */
	public static String getObjectType() {
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

		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
				web, topic);

		List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
		article.getSection().findSuccessorsOfType(MappingContentType.class,
				found);

		String dbpediaMapping = "";
		for (Section<MappingContentType> t : found) {
			String temp = t.getChildren().get(0).getOriginalText();
			if (temp.matches(hermes + " => .*")) {
				dbpediaMapping = temp.substring(temp.indexOf(" => ") + 4);
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

		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
				web, topic);

		List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
		article.getSection().findSuccessorsOfType(MappingContentType.class,
				found);

		for (Section<MappingContentType> t : found) {
			String temp = t.getChildren().get(0).getOriginalText();
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

		KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
				web, topic);

		List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
		article.getSection().findSuccessorsOfType(MappingContentType.class,
				found);

		String hermesMapping = "";
		for (Section<MappingContentType> t : found) {
			String temp = t.getChildren().get(0).getOriginalText();
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
	public static boolean isNoParse(String concept, String hermestag, String value) {

		String topic = getNoParseTopic();

		if (KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
				topic)) {

			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
					web, topic);

			List<Section<MappingContentType>> found = new Vector<Section<MappingContentType>>();
			article.getSection().findSuccessorsOfType(MappingContentType.class,
					found);

			for (Section<MappingContentType> t : found) {
				String temp = t.getChildren().get(0).getOriginalText();
				if (temp.matches("\\[" + concept + " " + hermestag + ":: " + value + "\\][\\r\\n]*")) {
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
	public static boolean isIgnored(String concept, String hermestag, String value) {

		String topic = getIgnoredTopic();

		if (KnowWEEnvironment.getInstance().getWikiConnector().doesPageExist(
				topic)) {

			KnowWEArticle article = KnowWEEnvironment.getInstance().getArticle(
					web, topic);

			List<Section<IgnoreContentType>> found = new Vector<Section<IgnoreContentType>>();
			article.getSection().findSuccessorsOfType(IgnoreContentType.class,
					found);

			for (Section<IgnoreContentType> t : found) {

				Section<IgnoreConcept> temp = t.findChildOfType(IgnoreConcept.class);
				String sectionConcept = temp.getOriginalText().substring(1,
						temp.getOriginalText().length() - 1);

				// if concept is in list - test if tag + value also.
				if (sectionConcept.equals(concept)) {

					List<Section<IgnoreChild>> listChilds = t.findChildrenOfType(
							IgnoreChild.class);

					for (Section<IgnoreChild> child : listChilds) {
						String node = child.getOriginalText();
						if (Character.isWhitespace(node.charAt(node.length() - 1))) {
							node = node.substring(0, node.length() - 1);
						}
						// If pair is in the list.
						if (node.equals("- " +
								hermestag + " == " + value)) {
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
			String url = "";
			try {
				url = URLEncoder.encode(getTopicForConcept(concept), "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "<a href=\"/KnowWE/Wiki.jsp?page=" + url + "\" class=\"wikipage\">"
					+ concept + "</a>";
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
			String url = "";
			try {
				url = URLEncoder.encode(getTopicForConcept(concept), "UTF-8");
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "<a id=\"" + id + "\" href=\"/KnowWE/Wiki.jsp?page=" + url
					+ "\" class=\"wikipage\">"
					+ concept + "</a>";
		}
		return "";
	}

	/**
	 * Tests if the hermes RDF-store contains the specified triple.
	 * 
	 * @param concept conceptname (unmodified string.).
	 * @param predicate predicate.
	 * @param value value.
	 * @return boolean.
	 */
	public static boolean storeContains(String concept, String predicate, String value) {

		// TODO value || predicate encode?

		try {
			concept = URLEncoder.encode(concept, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String namespace = UpperOntology.getInstance().getLocaleNS();
		concept = namespace + concept;

		String ask = "ASK {<" + concept
				+ "> " + predicate + " " + value + "}";

		return SPARQLUtil.executeBooleanQuery(ask);
	}

	/**
	 * Tests if the hermes RDF-store contains the specified triple.
	 * 
	 * @param concept conceptname (already encoded + namespace added string.).
	 * @param predicate predicate.
	 * @param value value.
	 * @return boolean.
	 */
	public static boolean storeContainsPre(String concept, String predicate, String value) {

		// TODO value || predicate encode?
		String ask = "ASK {<" + concept
				+ "> " + predicate + " " + value + "}";
		System.out.println(ask);
		return SPARQLUtil.executeBooleanQuery(ask);
	}

	/**
	 * Gets the corresponding topic (articlename) for a given concept.
	 * 
	 * @param concept conceptname.
	 * @return topic or empty string.
	 */
	public static String getTopicForConcept(String concept) {

		try {
			concept = URLEncoder.encode(concept, "UTF-8");
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String namespace = UpperOntology.getInstance().getLocaleNS();
		concept = namespace + concept;

		String query = "SELECT ?x ?y ?z WHERE {" +
				"?y rdf:subject <" + concept + "> ." +
				"?y rdf:predicate rdf:type ." +
				"?y rdfs:isDefinedBy ?z ." +
						"?z ns:hasTopic ?x }";

		TupleQueryResult result = SPARQLUtil.executeTupleQuery(query);

		String topic = "";

		try {
			while (result.hasNext()) {
				BindingSet set = result.next();
				topic = set.getBinding("x").getValue().stringValue();
				topic = URLDecoder.decode(topic, "UTF-8");
				topic = topic.substring(topic.indexOf("#") + 1);
			}
		}
		catch (QueryEvaluationException e) {
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return topic;
	}
}
