package de.d3web.we.lod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class LinkedOpenData {

	private ArrayList<String> propFile;

	// dbpedia tag -> result
	private HashMap<String, String> mappings;

	// Lists the corresponding DBpedia source for every result, to a specifig
	// hermes tag.
	private HashMap<String, List<Object>> inverseMap;

	// sparql variable -> dbpedia tag
	private HashMap<String, String> searchTags;

	// dbpedia tag -> filter
	private HashMap<String, HashSet<String>> filterTags;

	// LOD liefer bei conceptIsResource anderes als DBpedia -> Problem
	// private static final String sparqlEndpoint2 =
	// "http://lod.openlinksw.com/sparql";

	private static final String sparqlEndpoint = "http://dbpedia.org/sparql";

	/**
	 * Default.
	 */
	public LinkedOpenData() {

	}

	/**
	 * Creates all required lists and objects for a given property file.
	 * 
	 * @param conceptTypeName
	 *            concepttype.properties
	 * @throws Exception
	 *             if the propery file is not in correct syntax
	 */
	public LinkedOpenData(String conceptTypeName) throws Exception {
		propFile = new ArrayList<String>();
		mappings = new HashMap<String, String>();
		searchTags = new HashMap<String, String>();
		filterTags = new HashMap<String, HashSet<String>>();
		inverseMap = new HashMap<String, List<Object>>();
		URL name = getClass().getClassLoader().getResource(conceptTypeName);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					name.openStream()));
			String tempLine = in.readLine();
			while (tempLine != null) {
				if (!tempLine.startsWith("#") && !tempLine.matches("\\s*")) {
					propFile.add(tempLine);
				}
				tempLine = in.readLine();
			}
		} catch (Exception exception) {
		}
		for (String prop : propFile) {
			if (prop.matches("[\\w:-]* -> [\\w:öäüÖÄÜ]* [\\w()]*")
					|| prop.matches("[\\w:-]* [\\w:_$@-]* -> [\\w:öäüÖÄÜ]* [\\w()]*")) {
				String[] cut = prop.split(" -> ");
				mappings.put(cut[0], cut[1]);
			} else
				throw new Exception(
						conceptTypeName
								+ " file not in correct syntax: A B -> C (D) or A -> C (D)\n"
								+ prop);
		}

		Iterator<String> map = mappings.keySet().iterator();
		// add varnames + their tags in hashmap for sparql search
		while (map.hasNext()) {
			String temp = map.next();
			if (isSpecified(temp)) {
				String[] splitted = temp.split(" ");
				searchTags.put(splitted[0].replaceAll(":", ""), splitted[0]);
				// helps differentiating several filtertags & correct add of
				// these
				if (filterTags.containsKey(splitted[0])) {
					filterTags.get(splitted[0]).add(splitted[1]);
				} else {
					filterTags.put(splitted[0],
							new HashSet<String>(Arrays.asList(splitted[1])));
				}
			} else
				searchTags.put(temp.replaceAll("[:-]*", ""), temp);
		}
	}

	/**
	 * @return the inverseMap
	 */
	public HashMap<String, List<Object>> getInverseMap() {
		return inverseMap;
	}

	/**
	 * @return the propFile
	 */
	public ArrayList<String> getPropFile() {
		return propFile;
	}

	/**
	 * @return the mappings
	 */
	public HashMap<String, String> getMappings() {
		return mappings;
	}

	/**
	 * @return the searchTags
	 */
	public HashMap<String, String> getSearchTags() {
		return searchTags;
	}

	/**
	 * @return the filterTags
	 */
	public HashMap<String, HashSet<String>> getFilterTags() {
		return filterTags;
	}

	/**
	 * Tests if a property in concepttype.properties is specified.
	 * 
	 * @param toTest
	 *            string to test
	 * @return boolean
	 */
	private boolean isSpecified(String toTest) {
		if (toTest.matches("[\\w:]* [\\w:_$@-]*")) {
			return true;
		}
		return false;
	}

	/**
	 * Get returned data from sparql for the concepttype.properties.
	 * 
	 * @param input
	 *            resource to be looked up
	 * @return varname -> data (probably multiple results -> List)
	 */
	public HashMap<String, HashSet<String>> getLODdata(String input) {

		input = "<" + input + ">";
		StringBuffer prefixes = new StringBuffer();
		URL name = getClass().getClassLoader().getResource("prefixes");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					name.openStream()));
			String tempLine = in.readLine();
			while (tempLine != null) {
				prefixes.append(tempLine);
				tempLine = in.readLine();
			}
		} catch (Exception exception) {
		}

		Iterator<String> map2 = searchTags.keySet().iterator();
		StringBuffer queryStringX = new StringBuffer();
		queryStringX.append(prefixes + "SELECT");
		while (map2.hasNext()) {
			String temp = map2.next();
			queryStringX.append(" ?" + temp);
		}
		// Redirect
		queryStringX.append(" WHERE { ");
		// + "dbpedia2:redirect ?redirectTarget .");
		// add properties to query
		Iterator<String> map3 = searchTags.keySet().iterator();
		while (map3.hasNext()) {
			String temp = map3.next();
			queryStringX.append("OPTIONAL {" + input + " "
					+ searchTags.get(temp) + " ?" + temp + " .}");
		}
		queryStringX.append("}");

		// create the query object
		// System.out.println(queryStringX.toString());
		Query query = QueryFactory.create(queryStringX.toString());
		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlEndpoint, query);
		List<QuerySolution> test = new ArrayList<QuerySolution>();
		try {
			ResultSet results = qexec.execSelect();
			test = ResultSetFormatter.toList(results);
		} finally {
			qexec.close();
		}

		HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		for (QuerySolution x : test) {
			Iterator<String> it = x.varNames();
			while (it.hasNext()) {
				String temp = it.next();
				// if searchtag is a specified one
				if (filterTags.containsKey(searchTags.get(temp))) {
					// first tag which is mapped to filter
					for (String s : filterTags.get(searchTags.get(temp))) {
						String filter = s.replaceAll("\\$", "\\\\w+");
						Pattern pattern = Pattern.compile(filter);
						Matcher matcher = pattern.matcher(x.get(temp)
								.toString());
						// add set with 1 element
						if (matcher.find()) {
							result.put(
									searchTags.get(temp) + " " + s,
									new HashSet<String>(Arrays.asList(x.get(
											temp).toString())));
						}
					}
				} else {
					// helps to save multiple results for every variable
					if (result.containsKey(searchTags.get(temp))) {
						result.get(searchTags.get(temp)).add(
								x.get(temp).toString());
					} else {
						result.put(searchTags.get(temp), new HashSet<String>(
								Arrays.asList(x.get(temp).toString())));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Tests if the given URI input concept is a valid resource.
	 * 
	 * @param input
	 *            URI
	 * @return boolean
	 */
	public static boolean conceptIsResource(String input) {
		StringBuffer stringQuery = new StringBuffer();
		stringQuery
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		stringQuery.append("ASK {<" + input + "> rdfs:label ?temp1 .}");
		// create the query object
		Query query = QueryFactory.create(stringQuery.toString());

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlEndpoint, query);

		try {
			return qexec.execAsk();
		} finally {
			qexec.close();
		}
	}

	/**
	 * Searches a DBpedia concept for a given object.
	 * 
	 * @param input
	 *            object
	 * @return redirect dbpedia URI
	 */
	public static String getRedirect(String input) {
		StringBuffer stringQuery = new StringBuffer();
		stringQuery.append("PREFIX dbpprop: <http://dbpedia.org/property/>");
		stringQuery.append("PREFIX : <http://dbpedia.org/resource/>");
		stringQuery.append("SELECT ?redirectTarget WHERE {:" + input
				+ " dbpprop:redirect ?redirectTarget .}");
		// create the query object
		Query query = QueryFactory.create(stringQuery.toString());

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlEndpoint, query);

		List<QuerySolution> test = new ArrayList<QuerySolution>();
		try {
			ResultSet results = qexec.execSelect();
			test = ResultSetFormatter.toList(results);
		} finally {
			qexec.close();
		}
		for (QuerySolution x : test) {
			Iterator<String> it = x.varNames();
			while (it.hasNext()) {
				String temp = it.next();
				return x.get(temp).toString();
			}
		}
		return null;
	}

	/**
	 * Get a formatted output for the Hermes Ontology.
	 * 
	 * @param results
	 *            results from getData()
	 * @return Formatted result.
	 */
	public HashMap<String, HashSet<Object>> getHermesData(
			HashMap<String, HashSet<String>> results) {
		Iterator<String> map = mappings.keySet().iterator();
		HashMap<String, HashSet<Object>> resultData = new HashMap<String, HashSet<Object>>();
		while (map.hasNext()) {
			String temp = map.next();
			String[] cut = mappings.get(temp).split(" ");
			String hermesOnto = cut[0];
			String datatype = cut[1];
			if (!resultData.containsKey(hermesOnto)) {
				resultData.put(hermesOnto, new HashSet<Object>());
				inverseMap.put(hermesOnto, new Vector<Object>());
			}
			if (results.get(temp) != null) {
				if (datatype.matches("\\(date\\)")) {
					GregorianCalendar datum;
					for (String s : results.get(temp)) {
						if (isSpecified(temp)) {
							String filter = "[\\w]*_[BC|AD]{2}";
							Pattern pattern = Pattern.compile(filter);
							Matcher matcher = pattern.matcher(s);
							if (matcher.find()) {
								String[] yearD = matcher.group().split("_");
								if (yearD[1].matches("BC")) {
									datum = new GregorianCalendar(
											Integer.parseInt("-" + yearD[0]),
											1, 1);
									resultData.get(hermesOnto).add(datum);
									inverseMap.get(hermesOnto).add(temp);
									inverseMap.get(hermesOnto).add(datum);
								}
								if (yearD[1].matches("AD")) {
									datum = new GregorianCalendar(
											Integer.parseInt(yearD[0]), 1, 1);
									resultData.get(hermesOnto).add(datum);
									inverseMap.get(hermesOnto).add(temp);
									inverseMap.get(hermesOnto).add(datum);
								}
							}
						} else {
							String filter = "-?[\\d]*-[\\d]*-[\\d]*";
							Pattern pattern = Pattern.compile(filter);
							Matcher matcher = pattern.matcher(s);
							if (matcher.find()) {
								String result = matcher.group();
								String[] yearD2 = result.split("-");
								if (result.indexOf("-") == 0) {
									datum = new GregorianCalendar(
											Integer.parseInt("-" + yearD2[1]),
											Integer.parseInt(yearD2[2]),
											Integer.parseInt(yearD2[3]));
									resultData.get(hermesOnto).add(datum);
									inverseMap.get(hermesOnto).add(temp);
									inverseMap.get(hermesOnto).add(datum);
								} else {
									datum = new GregorianCalendar(
											Integer.parseInt(yearD2[0]),
											Integer.parseInt(yearD2[1]),
											Integer.parseInt(yearD2[2]));
									resultData.get(hermesOnto).add(datum);
									inverseMap.get(hermesOnto).add(temp);
									inverseMap.get(hermesOnto).add(datum);
								}
							}
						}
					}
				} else if (datatype.matches("\\(concept\\)")) {
					for (String s : results.get(temp)) {
						if (s.matches("http://[\\p{Alnum}/.:_]*")) {
							resultData.get(hermesOnto).add(s);
							inverseMap.get(hermesOnto).add(temp);
							inverseMap.get(hermesOnto).add(s);
						} else {
							String temp1 = getDBpediaRedirect(s);
							if (!getDBpediaRedirect(temp1).isEmpty()) {
								resultData.get(hermesOnto).add(temp1);
								inverseMap.get(hermesOnto).add(temp);
								inverseMap.get(hermesOnto).add(temp1);
							}
						}
					}
				} else if (datatype.matches("\\(string\\)")) {
					for (String s : results.get(temp)) {
						if (s.matches("http://[\\p{Alnum}/.:_]*")) {
							String[] cutString = s.split("http://.*/");
							// Schneide Kategorie: oder ähnliches weg
							if (cutString[1].matches("[\\w]+:[\\w]+")) {
								String tempvar = cutString[1].replaceAll(
										"[\\w]+:", "").replaceAll("_", " ");
								resultData.get(hermesOnto).add(tempvar);
								inverseMap.get(hermesOnto).add(temp);
								inverseMap.get(hermesOnto).add(tempvar);
							} else {
								String tempvar = cutString[1].replaceAll("_",
										" ");
								resultData.get(hermesOnto).add(tempvar);
								inverseMap.get(hermesOnto).add(temp);
								inverseMap.get(hermesOnto).add(tempvar);
							}
						} else {
							if (isSpecified(temp)) {
								if (s.matches(".*@[\\w]{2}")) {
									String tempvar = s.replaceAll("@[\\w]{2}",
											"");
									resultData.get(hermesOnto).add(tempvar);
									inverseMap.get(hermesOnto).add(temp);
									inverseMap.get(hermesOnto).add(tempvar);
								}
							} else
								resultData.get(hermesOnto).add(s);
							inverseMap.get(hermesOnto).add(temp);
							inverseMap.get(hermesOnto).add(s);
						}

					}
				} else if (datatype.matches("\\(double\\)")) {
					for (String s : results.get(temp)) {
						// Test?
						if (s.matches("[\\d]+[.,][\\d]+")
								|| s.matches("[\\d]+[.,][\\d]+ [\\d]+[.,][\\d]+")) {
							resultData.get(hermesOnto).add(s);
							inverseMap.get(hermesOnto).add(temp);
							inverseMap.get(hermesOnto).add(s);
						}
					}
				} else if (datatype.matches("\\(int\\)")) {
					for (String s : results.get(temp)) {
						if (s.matches("[\\d]+")) {
							resultData.get(hermesOnto).add(s);
							inverseMap.get(hermesOnto).add(temp);
							inverseMap.get(hermesOnto).add(s);
						} else if (s.matches("[\\d]+\\^\\^.*")) {
							String tempvar = s.substring(0, s.indexOf("^^"));
							resultData.get(hermesOnto).add(tempvar);
							inverseMap.get(hermesOnto).add(temp);
							inverseMap.get(hermesOnto).add(tempvar);
						}
					}// else
						// throw new Exception(
				} // "Datatype does not match on of the given types ( \"(date)\", \"(concept)\", \"(string)\", \"(double)\" )");
			}
		}
		return resultData;
	}

	/**
	 * First gets data from LOD, then returns the Hermes output. (Concatenation
	 * of getLODdata & getHermesData)
	 * 
	 * @param input
	 *            String
	 * @return final result
	 */
	public HashMap<String, HashSet<Object>> getData(String input) {
		HashMap<String, HashSet<String>> stepOne = getLODdata(input);
		return getHermesData(stepOne);
	}

	/**
	 * Get valid DBpedia concept if available.
	 * 
	 * @param input
	 *            Hermes Concept.
	 * @return corresponding Dbpedia concept.
	 */
	public static String getDBpediaRedirect(String input) {
		String parsed = input.replaceAll("\\p{Punct}*", "")
				.replaceAll(" ", "_");
		if (conceptIsResource("http://dbpedia.org/resource/" + parsed)) {
			return "http://dbpedia.org/resource/" + parsed;
		} else if (conceptIsResource(getRedirect(parsed))) {
			return getRedirect(parsed);
		} else {
			// Same with translated one!
			// Only for init - for google to distinguish between programs
			Translate.setHttpReferrer("ex");
			String translatedText = "";
			try {
				translatedText = Translate.execute(input, Language.GERMAN,
						Language.ENGLISH);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!input.equals(translatedText)) {
				return LinkedOpenData.getDBpediaRedirect(translatedText);
			}
		}
		return "";
	}

}
