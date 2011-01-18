package de.d3web.we.lod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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

	// Reads the property files.
	private ArrayList<String> propFile;

	// dbpedia tag -> hermes tag.
	private HashMap<String, String> mappings;

	// Lists the corresponding DBpedia source for every result, to a specifig
	// hermes tag. hermes tag -> dbpedia source.
	private HashMap<String, List<String>> inverseMap;

	// sparql variable -> dbpedia tag
	private HashMap<String, String> searchTags;

	// dbpedia tag -> filter.
	private HashMap<String, HashSet<String>> filterTags;

	// "http://lod.openlinksw.com/sparql";
	private static final String sparqlEndpoint = "http://dbpedia.org/sparql";

	// Times the queries are split up.
	private static final int split = 3;

	/**
	 * Default.
	 */
	public LinkedOpenData() {

	}

	/**
	 * Creates all required lists and objects for a given property file.
	 * 
	 * @param conceptTypeName concepttype.properties
	 * @throws Exception if the propery file is not in correct syntax
	 */
	public LinkedOpenData(String conceptTypeName) throws Exception {
		propFile = new ArrayList<String>();
		mappings = new HashMap<String, String>();
		searchTags = new HashMap<String, String>();
		filterTags = new HashMap<String, HashSet<String>>();
		inverseMap = new HashMap<String, List<String>>();
		URL name = getClass().getClassLoader().getResource(conceptTypeName);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					name.openStream(), "ISO-8859-1"));
			String tempLine = in.readLine();
			while (tempLine != null) {
				if (!tempLine.startsWith("#") && !tempLine.matches("\\s*")) {
					propFile.add(tempLine);
				}
				tempLine = in.readLine();
			}
		}
		catch (Exception exception) {
		}
		for (String prop : propFile) {
			if (prop.matches("[\\w:\\-]+ -> [\\p{L}:]+ [\\p{L}]* ?[\\w()]+")
					|| prop.matches("[\\w:\\-]+ [\\w:_$@-]* -> [\\p{L}:]+ [\\p{L}]* ?[\\w()]+")) {
				String[] cut = prop.split(" -> ");
				if (prop.startsWith("INV")) {
					// INV name --> name INV ; handle as a filter
					String swap = cut[0].substring(cut[0].indexOf(" ") + 1)
							+ " " + cut[0].substring(0, cut[0].indexOf(" "));
					mappings.put(swap, cut[1]);
				}
				else {
					mappings.put(cut[0], cut[1]);
				}
			}
			else throw new Exception(
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
				searchTags.put(splitted[0].replaceAll("[:-]*", ""), splitted[0]);
				// helps differentiating several filtertags & correct add of
				// these
				if (filterTags.containsKey(splitted[0])) {
					filterTags.get(splitted[0]).add(splitted[1]);
				}
				else {
					filterTags.put(splitted[0],
								new HashSet<String>(Arrays.asList(splitted[1])));
				}
			}
			else searchTags.put(temp.replaceAll("[:-]*", ""), temp);
		}
	}

	/**
	 * @return the inverseMap
	 */
	public HashMap<String, List<String>> getInverseMap() {
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
	 * @param toTest string to test
	 * @return boolean
	 */
	private static boolean isSpecified(String toTest) {
		if (toTest.matches("[\\w:-]* [\\p{L}:_$@-]*")) {
			return true;
		}
		return false;
	}

	/**
	 * Get returned data from sparql for the concepttype.properties.
	 * 
	 * @param input resource to be looked up
	 * @return varname -> data (probably multiple results -> List)
	 */
	public HashMap<String, HashSet<String>> getLODdata(String input) {

		input = "<" + input + ">";

		List<String> queries = this.createQueryString(input, split);
		List<QuerySolution> solution = this.executequeryStrings(queries);

		HashMap<String, HashSet<String>> result = new HashMap<String, HashSet<String>>();
		for (QuerySolution x : solution) {
			Iterator<String> it = x.varNames();
			while (it.hasNext()) {
				String temp = it.next();
				// if searchtag is a specified one
				if (filterTags.containsKey(searchTags.get(temp))) {
					// first tag which is mapped to filter
					for (String s : filterTags.get(searchTags.get(temp))) {
						if (!s.equals("INV")) {
							String filter = s.replaceAll("\\$", "\\\\w+");
							Pattern pattern = Pattern.compile(filter);
							Matcher matcher = pattern.matcher(x.get(temp)
									.toString());
							// add set with 1 element

							if (matcher.find()) {
								if (!result.containsKey(searchTags.get(temp) + " " + s)) {
									result.put(
											searchTags.get(temp) + " " + s,
											new HashSet<String>(Arrays.asList(x.get(
													temp).toString())));
								}
								else {
									result.get(searchTags.get(temp) + " " + s).add(
											x.get(temp).toString());
								}
							}
						}
						// Inverse lookup save with INV as filterkey
						else {
							if (result.containsKey(searchTags.get(temp) + " " + s)) {
								result.get(searchTags.get(temp) + " " + s).add(
										x.get(temp).toString());
							}
							else {
								result.put(searchTags.get(temp) + " " + s, new HashSet<String>(
										Arrays.asList(x.get(temp).toString())));
							}
						}
					}
				}
				else {
					// helps to save multiple results for every variable
					if (result.containsKey(searchTags.get(temp))) {
						result.get(searchTags.get(temp)).add(
								x.get(temp).toString());
					}
					else {
						result.put(searchTags.get(temp), new HashSet<String>(
								Arrays.asList(x.get(temp).toString())));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Lists all prefixes from prefix resource in an single string.
	 * 
	 * @return prefix list.
	 */
	public String getPrefixes() {

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
		}
		catch (Exception exception) {
		}

		return prefixes.toString();
	}

	/**
	 * Executes multiple query strings & returns result as a single list of
	 * solutions.
	 * 
	 * @param queries queries.
	 * @return solutionlist.
	 */
	public List<QuerySolution> executequeryStrings(List<String> queries) {

		List<List<QuerySolution>> test = new ArrayList<List<QuerySolution>>();

		for (int i = 0; i < queries.size(); i++) {
			// create the query object
			// System.out.println(queries.get(i).toString());
			Query query = QueryFactory.create(queries.get(i).toString());
			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					sparqlEndpoint, query);
			List<QuerySolution> solution = new ArrayList<QuerySolution>();
			try {
				ResultSet results = qexec.execSelect();
				solution = ResultSetFormatter.toList(results);
				test.add(solution);
			}
			finally {
				qexec.close();
			}
		}

		List<QuerySolution> result = new ArrayList<QuerySolution>();
		for (int i = 0; i < test.size(); i++) {
			for (int j = 0; j < test.get(i).size(); j++) {
				result.add(test.get(i).get(j));
			}
		}
		return result;
	}

	/**
	 * Create multiple Queries, to avoid the dbpedia sparql execution limit.
	 * 
	 * @param input Resource to be queried
	 * @param count times the query is split
	 */
	public List<String> createQueryString(String input, int count) {

		StringBuffer queryStringX = new StringBuffer();
		queryStringX.append("SELECT");

		int size = searchTags.size();
		int split = size / count;

		List<String> selectVars = new ArrayList<String>();

		Iterator<String> map2 = searchTags.keySet().iterator();
		while (map2.hasNext()) {
			String temp = map2.next();
			selectVars.add((" ?" + temp));
		}

		List<String> whereVars = new ArrayList<String>();

		// add properties to query
		Iterator<String> map3 = searchTags.keySet().iterator();
		while (map3.hasNext()) {
			String temp = map3.next();
			String add = "";
			if (filterTags.containsKey(searchTags.get(temp))) {
				for (String s : filterTags.get(searchTags.get(temp)))
					if (s.equals("INV")) {
						add = "OPTIONAL {?" + temp + " " + searchTags.get(temp) + " " + input
								+ " .}";
					}
					else {
						add = "OPTIONAL {" + input + " " + searchTags.get(temp) + " ?" + temp
								+ " .}";
					}
			}
			else {
				add = "OPTIONAL {" + input + " " + searchTags.get(temp) + " ?" + temp + " .}";
			}
			whereVars.add(add);
		}

		List<String> queries = new ArrayList<String>();
		StringBuffer varBuf = new StringBuffer();
		StringBuffer whereBuf = new StringBuffer();

		int temp = split;

		for (int i = 0; i < size; i++) {

			varBuf.append(selectVars.get(i));
			whereBuf.append(whereVars.get(i));

			if (i == temp - 1 || i == size - 1) {
				StringBuffer query = new StringBuffer();
				query.append(this.getPrefixes() + "SELECT" + varBuf + " WHERE{" + whereBuf
							+ "}");
				queries.add(query.toString());
				varBuf.delete(0, varBuf.length());
				whereBuf.delete(0, whereBuf.length());
				temp += split;
			}
		}
		return queries;
	}

	/**
	 * Tests if the given URI input concept is a valid resource.
	 * 
	 * @param input URI
	 * @return boolean
	 */
	public static boolean conceptIsResource(String input) {
		StringBuffer stringQuery = new StringBuffer();
		StringBuffer stringQuery2 = new StringBuffer();
		stringQuery
				.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>");
		stringQuery.append("ASK {<" + input + "> dbpedia-owl:abstract ?temp1 .}");
		stringQuery2
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		stringQuery2.append("ASK {<" + input + "> rdfs:comment ?temp1 .}");
		// create the query object
		Query query = QueryFactory.create(stringQuery.toString());
		Query query2 = QueryFactory.create(stringQuery2.toString());

		QueryExecution qexec = QueryExecutionFactory.sparqlService(
				sparqlEndpoint, query);
		QueryExecution qexec2 = QueryExecutionFactory.sparqlService(
				sparqlEndpoint, query2);
		try {
			return qexec.execAsk() || qexec2.execAsk();
		}
		finally {
			qexec.close();
		}
	}

	/**
	 * Returns dbpprop:redirect for an input concept.
	 * 
	 * @param input concept
	 * @return dbpedia URI or empty string.
	 */
	private static String getRedirect(String input) {
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
		}
		finally {
			qexec.close();
		}
		for (QuerySolution x : test) {
			Iterator<String> it = x.varNames();
			while (it.hasNext()) {
				String temp = it.next();
				return x.get(temp).toString();
			}
		}
		return "";
	}

	/**
	 * Get a formatted output for the Hermes Ontology.
	 * 
	 * @param results results from getData()
	 * @return Formatted result.
	 */
	public HashMap<String, HashSet<String>> getHermesData(
			HashMap<String, HashSet<String>> results) {

		Iterator<String> resultMap = results.keySet().iterator();
		HashMap<String, HashSet<String>> resultData = new HashMap<String, HashSet<String>>();

		int countCoords = 0;
		String[] coords = new String[4];

		while (resultMap.hasNext()) {

			String lhsMappings = resultMap.next();
			boolean inverse = false;

			String normalTitle = lhsMappings;
			// Save string for hermes property mouseover [title='x'].
			String htmlTitle = "";

			// IF specified or inverse query [title] with filter or is X of.
			if (isSpecified(lhsMappings)) {
				if (lhsMappings.split(" ")[1].equals("INV")) {
					inverse = true;
					htmlTitle = "is&nbsp;" + lhsMappings.substring(0, lhsMappings.indexOf(" "))
							+ "&nbsp;of";
				}
				else {
					htmlTitle = lhsMappings.substring(0, lhsMappings.indexOf(" "))
							+ "&nbsp;["
							+ lhsMappings.substring(lhsMappings.indexOf(" ") + 1) + "]";
				}
			}

			// Get hermes tag for dbpedia tag & get datatype for the given
			// result.

			String[] cut = mappings.get(lhsMappings).split(" ");
			String hermesTag = "";
			String datatype = "";

			if (mappings.get(lhsMappings).matches("[\\p{L}:]+ [\\w()]+")) {
				hermesTag = cut[0];
				datatype = cut[1];
			}
			// case [\\p{L}:]+ [\\p{L}:]+ [\\w()]+
			else {
				hermesTag = cut[0] + " " + cut[1];
				datatype = cut[2];
			}

			// Intialize resultmap & lookup map for [title]
			if (!resultData.containsKey(hermesTag)) {
				resultData.put(hermesTag, new HashSet<String>());
				inverseMap.put(hermesTag, new ArrayList<String>());
			}

			// Case dbtag filter($), but inverse lookup is handled as normal
			// request.
			if (isSpecified(lhsMappings) && !inverse) {

				if (datatype.matches("\\(date\\)")) {

					for (String s : results.get(lhsMappings)) {

						String date = "";

						s = s.substring(s.lastIndexOf("/") + 1);

						// category:$_$_deaths/births or category:$_AD/BC
						if (s.matches("\\w+:\\d+_\\w+")) {
							String filter = "[\\w]+_[BC|AD|E]{2,3}";
							Pattern pattern = Pattern.compile(filter);
							Matcher matcher = pattern.matcher(s);
							if (matcher.find()) {
								String[] yearD = matcher.group().split("_");
								if (yearD[1].matches("BC") || yearD[1].matches("BCE")) {

									date = "-" + yearD[0] + "-1-1";
								}
								else {
									date = yearD[0] + "-1-1)";
								}
							}
							resultData.get(hermesTag).add(date);
							inverseMap.get(hermesTag).add(htmlTitle + "&nbsp;(" + s + ")");
							inverseMap.get(hermesTag).add(date);
						}
					}
				}

				if (datatype.matches("\\(string\\)")) {

					for (String s : results.get(lhsMappings)) {
						// Cut languagetags @$$
						if (s.matches(".*@[\\w]{2}")) {

							String string = s.replaceAll("@[\\w]{2}",
										"");

							resultData.get(hermesTag).add(string);

							inverseMap.get(hermesTag).add(htmlTitle + "&nbsp;(" + s + ")");
							inverseMap.get(hermesTag).add(string);
						}

					}
				}

				// Only in conjunction with a filter -> true if filter found,
				// else false.
				if (datatype.matches("\\(object\\)")) {

					for (String s : results.get(lhsMappings)) {

						resultData.get(hermesTag).add("ist vom Typ " + hermesTag);

						inverseMap.get(hermesTag).add(htmlTitle + "&nbsp;(" + s + ")");
						inverseMap.get(hermesTag).add("ist vom Typ " + hermesTag);
					}
				}

				// Get only $ var from result.
				if (datatype.matches("\\(concept\\)")) {

					for (String s : results.get(lhsMappings)) {

						String result = "";
						if (lhsMappings.split(" ")[1].contains("$")) {

							String regex = lhsMappings.split(" ")[1];
							int expression = regex.indexOf("$");
							char[] search = s.substring(expression, s.length() - 1).toCharArray();

							for (char c : search) {
								if (c == '_' || c == ' ') {
									break;
								}
								result += c;
							}
						}

						result = getDBpediaRedirect(result);

						// Mask it to recognize it (dont't display as input box)

						result = HermesData.getHermesMapping(result);

						if (!result.isEmpty()) {
							result = "!$ConceptLink:: " + result;
						}

						resultData.get(hermesTag).add(result);
						inverseMap.get(hermesTag).add(htmlTitle + "&nbsp;(" + s + ")");
						inverseMap.get(hermesTag).add(result);
					}
				}
			}
			// No Filter or inverse query.
			else {

				// if inverse [title] = is X of..
				if (inverse) {
					normalTitle = htmlTitle;
				}

				if (datatype.matches("\\(date\\)")) {

					for (String s : results.get(lhsMappings)) {

						String datum = "";
						String datum2 = "";
						boolean period = false;

						if (s.matches("[\\w -]+@\\p{Alpha}{2}")) {
							s = s.substring(0, s.indexOf("@"));
						}
						if (s.matches("[\\w -]+\\^.*")) {
							s = s.substring(0, s.indexOf("^"));
						}

						// Default xsd:date type [-]CCYY-MM-DD
						if (s.matches("-?[\\d]*-[\\d]*-[\\d]*")) {

							String[] yearD2 = s.split("-");
							if (s.indexOf("-") == 0) {
								datum = "-" + yearD2[1] + "-" + yearD2[2] + "-" + yearD2[3];
							}
							else {
								datum = yearD2[0] + "-" + yearD2[1] + "-" + yearD2[2];
							}
						}
						// October?,? ?YYYY+
						if (s.matches("[\\p{Alpha} ,]*[\\d]+ ?[AD|BC|E]{2,3}")) {

							String yearFilter = "[\\d]+ ?[AD|BC|E]{2,3}";
							Pattern yearPattern = Pattern.compile(yearFilter);
							Matcher matcherY = yearPattern.matcher(s);

							if (matcherY.find()) {
								String date = matcherY.group();
								String year = date.split(" ?[\\p{Alpha}]+")[0];
								if (date.contains("AD")
										|| (date.contains("CE") && !date.contains("BCE"))) {
									datum = year + "-1-1";
								}
								else {
									datum = "-" + year + "-1-1";
								}
							}
						}

						// Period of time.
						if (s.matches("[\\d]+ ?[AD|BC|E]{0,3} ?- ?[\\d]+ ?[AD|BC|E]{2,3}")) {

							period = true;
							String periodFilter = "[\\d]+";
							Pattern periodPattern = Pattern.compile(periodFilter);
							Matcher matcherP = periodPattern.matcher(s);
							boolean two = false;
							while (matcherP.find()) {
								String date = matcherP.group();
								if (s.contains("AD")
										|| (s.contains("CE") && !s.contains("BCE"))) {
									if (two) {
										datum2 = date + "-1-1";
									}
									else {
										datum = date + "-1-1";
									}
								}
								else {
									if (two) {
										datum2 = "-" + date + "-1-1";
									}
									else {
										datum = "-" + date + "-1-1";
									}
								}
								two = true;
							}
						}
						if (period) {
							resultData.get(hermesTag).add(datum + " >> " + datum2);
							inverseMap.get(hermesTag).add(normalTitle);
							inverseMap.get(hermesTag).add(datum + " >> " + datum2);
						}
						else {
							resultData.get(hermesTag).add(datum);
							inverseMap.get(hermesTag).add(normalTitle);
							inverseMap.get(hermesTag).add(datum);
						}
					}
				}

				if (datatype.matches("\\(concept\\)")) {

					for (String s : results.get(lhsMappings)) {

						String result = "";
						String redirect = "";

						if (s.matches("[\\w -]+@\\p{Alpha}{2}")) {
							redirect = s.substring(0, s.indexOf("@"));
						}

						// DBpedia has inconsistent links ==> get main resource
						// site.
						if (s.matches("http://[\\p{Alnum}/.:_]*")) {
							if (conceptIsResource(s)) {
								result = s;
							}
							else {
								result = getRedirect(s.substring(s.lastIndexOf("/") + 1));
							}
						}
						else {
							result = getDBpediaRedirect(redirect);
						}

						// Mask it to recognize it (dont't display as input box)
						result = HermesData.getHermesMapping(result);

						if (!result.isEmpty()) {
							result = "!$ConceptLink:: " + result;
						}

						resultData.get(hermesTag).add(result);
						inverseMap.get(hermesTag).add(normalTitle);
						inverseMap.get(hermesTag).add(result);
					}
				}

				if (datatype.matches("\\(double\\)")) {

					for (String s : results.get(lhsMappings)) {

						double d1, d2;

						if (s.matches("[\\d., ]+@\\p{Alpha}{2}")) {
							s = s.substring(0, s.indexOf("@"));
						}
						if (s.matches("[\\d.,]+\\^\\^[\\w\\p{Punct}]+")) {
							s = s.substring(0, s.indexOf("^"));
						}

						if (s.matches("[\\d]+[.,][\\d]+ ?[\\d]*[.,]?[\\d]*")) {

							s = s.replaceAll(",", ".");

							if (s.contains(" ")) {

								d1 = Double.parseDouble(s.substring(0, s.indexOf(" ")));
								d2 = Double.parseDouble(s.substring(s.indexOf(" "),
										s.length()));
								resultData.get(hermesTag).add("x: " + d1);
								inverseMap.get(hermesTag).add(normalTitle);
								inverseMap.get(hermesTag).add("x: " + d1);
								resultData.get(hermesTag).add("y: " + d2);
								inverseMap.get(hermesTag).add(normalTitle);
								inverseMap.get(hermesTag).add("y: " + d2);
							}
							else {
								resultData.get(hermesTag).add(s);
								inverseMap.get(hermesTag).add(normalTitle);
								inverseMap.get(hermesTag).add(s);
							}

						}
					}
				}

				if (datatype.matches("\\(string\\)")) {

					for (String s : results.get(lhsMappings)) {
						String string = s;

						// Cut URL.
						if (s.matches("http://[\\p{Alnum}/.:_]*")) {

							String[] cutString = s.split("http://.*/");
							if (cutString[1].matches("[\\w]+:[\\w]+")) {
								string = cutString[1].replaceAll(
											"[\\w]+:", "").replaceAll("_", " ");
							}
							else {
								string = cutString[1].replaceAll("_", " ");
							}
						}
						else {
							// Cut languagetags.
							if (string.matches("[\\w.:;]+@\\p{Alpha}{2}")) {
								string = string.substring(0, string.indexOf("@") - 1);
							}
						}
						resultData.get(hermesTag).add(string);
						inverseMap.get(hermesTag).add(normalTitle);
						inverseMap.get(hermesTag).add(string);
					}
				}

				if (datatype.matches("\\(coords\\)")) {

					countCoords++;

					for (String s : results.get(lhsMappings)) {
						s = s.substring(0, s.indexOf("^"));
						if (lhsMappings.equals("dbpprop:latDeg")) {
							coords[0] = s + "° ";
						}
						if (lhsMappings.equals("dbpprop:latMin")) {
							coords[1] = s + "’N ";
						}
						if (lhsMappings.equals("dbpprop:lonDeg")) {
							coords[2] = s + "° ";
						}
						if (lhsMappings.equals("dbpprop:lonMin")) {
							coords[3] = s + "’E";
						}
					}
					if (countCoords == 4) {
						String result = "";
						for (String s : coords) {
							result += s;
						}
						resultData.get(hermesTag).add(result);
						inverseMap.get(hermesTag).add(
								"dbpprop:latDeg,&nbsp;dbpprop:latMin,&nbsp;dbpprop:lonDeg,&nbsp;dbpprop:lonMin");
						inverseMap.get(hermesTag).add(result);
					}
				}
			}
		}
		return resultData;
	}

	/**
	 * First gets data from LOD, then returns the Hermes output. (Concatenation
	 * of getLODdata & getHermesData)
	 * 
	 * @param input String
	 * @return final result
	 */
	public HashMap<String, HashSet<String>> getData(String input) {
		HashMap<String, HashSet<String>> stepOne = getLODdata(input);
		return getHermesData(stepOne);
	}

	/**
	 * Get valid dbpedia concept if available.
	 * 
	 * @param input hermes Concept.
	 * @return dbpedia URI or empty string.
	 */
	public static String getDBpediaRedirect(String input) {
		if (!input.isEmpty()) {
			String parsed = input.replaceAll("\\p{Punct}*", "")
					.replaceAll(" ", "_");
			if (conceptIsResource("http://dbpedia.org/resource/" + parsed)) {
				return "http://dbpedia.org/resource/" + parsed;
			}
			else if (conceptIsResource(getRedirect(parsed))) {
				return getRedirect(parsed);
			}
			else {
				// Same with translated one!
				String translatedText = translate(input);

				if (!input.equals(translatedText)) {
					return LinkedOpenData.getDBpediaRedirect(translatedText);
				}
			}
		}
		return "";
	}

	/**
	 * Get valid dbpedia concept if available for the given wikipedia link.
	 * 
	 * @param wikilink wikipedia @en link.
	 * @return dbepdia URI or empty string.
	 */
	public static String getResourceforWikipedia(String wikilink) {
		if (wikilink.matches("http://en\\.wikipedia\\.org/.*")) {

			StringBuffer stringQuery = new StringBuffer();
			stringQuery.append("PREFIX foaf: <http://xmlns.com/foaf/0.1/>");
			stringQuery.append("SELECT ?resource WHERE { ?resource foaf:page <" + wikilink + "> .}");
			// create the query object
			Query query = QueryFactory.create(stringQuery.toString());

			QueryExecution qexec = QueryExecutionFactory.sparqlService(
					sparqlEndpoint, query);

			List<QuerySolution> result = new ArrayList<QuerySolution>();
			try {
				ResultSet results = qexec.execSelect();
				result = ResultSetFormatter.toList(results);
			}
			finally {
				qexec.close();
			}
			String resultString = "";
			for (QuerySolution x : result) {
				Iterator<String> it = x.varNames();
				while (it.hasNext()) {
					String temp = it.next();
					resultString = x.get(temp).toString();
				}
			}
			return resultString;
		}
		return "";
	}

	/**
	 * Translates a given text.
	 * 
	 * @param string text to translate.
	 * @return translated Text.
	 */
	private static String translate(String string) {

		Translate.setHttpReferrer("ex");
		String translatedText = "";
		if (!string.isEmpty()) {
			try {
				translatedText = Translate.execute(string, Language.GERMAN,
						Language.ENGLISH);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return translatedText;

	}
}
