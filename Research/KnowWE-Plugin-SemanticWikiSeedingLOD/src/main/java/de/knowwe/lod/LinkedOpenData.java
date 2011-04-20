package de.knowwe.lod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.ws.http.HTTPException;

import org.openrdf.query.BindingSet;
import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;

public class LinkedOpenData {

	// Reads the property files.
	private ArrayList<String> propFile;

	// dbpedia tag -> hermes tag.
	private HashMap<String, List<String>> mappings;

	// Lists the corresponding DBpedia source for every result, to a specific
	// hermes tag. hermes tag -> dbpedia source.
	private HashMap<String, List<String>> inverseMap;

	// sparql variable -> dbpedia tag
	private HashMap<String, String> searchTags;

	// dbpedia tag -> filter.
	private HashMap<String, HashSet<String>> filterTags;

	// "http://lod.openlinksw.com/sparql";
	public static final String sparqlEndpoint = "http://dbpedia.org/sparql";
	public static final HTTPRepository endpointRepo = new HTTPRepository(
			sparqlEndpoint, "");

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
		try {
			endpointRepo.initialize();
		}
		catch (RepositoryException e) {
			System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
			e.printStackTrace();
		}

		propFile = new ArrayList<String>();
		mappings = new LinkedHashMap<String, List<String>>();
		searchTags = new LinkedHashMap<String, String>();
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
			if (prop.matches("[\\w:-]+ -> [\\p{L}:]+ [\\p{L}]* ?[\\w()]+")
					|| prop.matches("[\\w:-]+ [\\p{L}:_$@-]* -> [\\p{L}:]+ [\\p{L}]* ?[\\w()]+")) {
				String[] cut = prop.split(" -> ");
				if (prop.startsWith("INV")) {
					// INV name --> name INV ; handle as a filter
					String swap = cut[0].substring(cut[0].indexOf(" ") + 1)
							+ " " + cut[0].substring(0, cut[0].indexOf(" "));
					if (mappings.containsKey(swap)) {
						mappings.get(swap).add(cut[1]);
					}
					else {
						mappings.put(swap,
								new ArrayList<String>(Arrays.asList(cut[1])));
					}
				}
				else {
					if (mappings.containsKey(cut[0])) {
						mappings.get(cut[0]).add(cut[1]);
					}
					else {
						mappings.put(cut[0],
								new ArrayList<String>(Arrays.asList(cut[1])));
					}
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
				searchTags
						.put(splitted[0].replaceAll("[:-]*", ""), splitted[0]);
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
	public HashMap<String, List<String>> getMappings() {
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
		List<BindingSet> solution = this.executequeryStrings(queries);

		HashMap<String, HashSet<String>> result = new LinkedHashMap<String, HashSet<String>>();
		for (BindingSet x : solution) {
			Iterator<String> it = x.getBindingNames().iterator();
			while (it.hasNext()) {
				String temp = it.next();
				// if searchtag is a specified one
				if (filterTags.containsKey(searchTags.get(temp))) {
					// first tag which is mapped to filter
					for (String s : filterTags.get(searchTags.get(temp))) {
						if (!s.equals("INV")) {
							String filter = s.replaceAll("\\$", "\\\\w+");
							filter = "(?i)" + filter;
							Pattern pattern = Pattern.compile(filter);
							Matcher matcher = pattern.matcher(x
									.getBinding(temp).getValue().toString());
							// add set with 1 element

							if (matcher.find()) {
								if (!result.containsKey(searchTags.get(temp)
										+ " " + s)) {
									result.put(
											searchTags.get(temp) + " " + s,
											new HashSet<String>(Arrays.asList(x
													.getBinding(temp)
													.getValue().toString())));
								}
								else {
									result.get(searchTags.get(temp) + " " + s)
											.add(x.getBinding(temp).getValue()
													.toString());
								}
							}
						}
						// Inverse lookup save with INV as filterkey
						else {
							if (result.containsKey(searchTags.get(temp) + " "
									+ s)) {
								result.get(searchTags.get(temp) + " " + s).add(
										x.getBinding(temp).getValue()
												.toString());
							}
							else {
								result.put(
										searchTags.get(temp) + " " + s,
										new HashSet<String>(Arrays.asList(x
												.getBinding(temp).getValue()
												.toString())));
							}
						}
					}
				}
				else {
					// helps to save multiple results for every variable
					if (result.containsKey(searchTags.get(temp))) {
						result.get(searchTags.get(temp)).add(
								x.getBinding(temp).getValue().toString());
					}
					else {
						result.put(
								searchTags.get(temp),
								new HashSet<String>(Arrays
										.asList(x.getBinding(temp).getValue()
												.toString())));
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
	private List<BindingSet> executequeryStrings(List<String> queries) {
		List<BindingSet> results = new ArrayList<BindingSet>();
		for (int i = 0; i < queries.size(); i++) {
			// create the query object
			// System.out.println(queries.get(i).toString());

			try {
				RepositoryConnection con = endpointRepo.getConnection();
				try {
					TupleQuery query = con.prepareTupleQuery(
							QueryLanguage.SPARQL, queries.get(i));
					TupleQueryResult result = query.evaluate();

					while (result.hasNext()) {
						results.add(result.next());
					}
				}
				catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (MalformedQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					con.close();
				}
			}
			catch (RepositoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		return results;
	}

	/**
	 * Create multiple Queries, to avoid the dbpedia sparql execution limit.
	 * 
	 * @param input Resource to be queried
	 * @param count times the query is split
	 */
	private List<String> createQueryString(String input, int count) {

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
						add = "OPTIONAL {?" + temp + " " + searchTags.get(temp)
								+ " " + input + " .}";
					}
					else {
						add = "OPTIONAL {" + input + " " + searchTags.get(temp)
								+ " ?" + temp + " .}";
					}
			}
			else {
				add = "OPTIONAL {" + input + " " + searchTags.get(temp) + " ?"
						+ temp + " .}";
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
				query.append(this.getPrefixes() + "SELECT" + varBuf + " WHERE{"
						+ whereBuf + "}");
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
		stringQuery.append("ASK {<" + input
				+ "> dbpedia-owl:abstract ?temp1 .}");
		stringQuery2
				.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>");
		stringQuery2.append("ASK {<" + input + "> rdfs:comment ?temp1 .}");
		// create the query object

		boolean queryResult = false;
		boolean queryResult2 = false;

		try {
			RepositoryConnection con = endpointRepo.getConnection();

			try {
				BooleanQuery query = con.prepareBooleanQuery(
						QueryLanguage.SPARQL, stringQuery.toString());
				BooleanQuery query2 = con.prepareBooleanQuery(
						QueryLanguage.SPARQL, stringQuery2.toString());

				queryResult = query.evaluate();
				queryResult2 = query2.evaluate();

			}
			catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				con.close();
			}
		}
		catch (HTTPException e) {
			e.printStackTrace();
			return false;
		}
		catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return queryResult || queryResult2;

	}

	/**
	 * Returns dbpprop:redirect for an input concept.
	 * 
	 * @param input concept
	 * @return dbpedia URI or empty string.
	 */
	private static String getRedirect(String input) {
		StringBuffer stringQuery = new StringBuffer();
		stringQuery
				.append("PREFIX dbpedia-owl: <http://dbpedia.org/ontology/>");
		stringQuery.append("PREFIX : <http://dbpedia.org/resource/>");
		stringQuery.append("SELECT ?redirectTarget WHERE {:" + input
				+ " dbpedia-owl:wikiPageRedirects ?redirectTarget .}");
		// create the query object

		try {
			RepositoryConnection con = endpointRepo.getConnection();
			try {
				TupleQuery query = con.prepareTupleQuery(QueryLanguage.SPARQL,
						stringQuery.toString());
				TupleQueryResult result = query.evaluate();

				while (result.hasNext()) {
					BindingSet b = result.next();
					Iterator<String> it = b.getBindingNames().iterator();
					while (it.hasNext()) {
						String temp = it.next();
						return b.getBinding(temp).getValue().toString();
					}
				}
			}
			catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				con.close();
			}
		}
		catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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

		// Sorted LinkedHashMap mappings.
		Iterator<String> mappingIt = mappings.keySet().iterator();
		HashMap<String, HashSet<String>> resultData = new LinkedHashMap<String, HashSet<String>>();
		int countCoords = 0;
		String[] coords = new String[4];

		while (mappingIt.hasNext()) {
			String lhsMappings = mappingIt.next();

			if (results.containsKey(lhsMappings)) {
				boolean inverse = false;

				String normalTitle = lhsMappings;
				// Save string for hermes property mouseover [title='x'].
				String htmlTitle = "";

				// IF specified or inverse query [title] with filter or is X of.
				if (isSpecified(lhsMappings)) {
					if (lhsMappings.split(" ")[1].equals("INV")) {
						inverse = true;
						htmlTitle = "is&nbsp;"
								+ lhsMappings.substring(0,
										lhsMappings.indexOf(" ")) + "&nbsp;of";
					}
					else {
						htmlTitle = lhsMappings.substring(0,
								lhsMappings.indexOf(" "))
								+ "&nbsp;["
								+ lhsMappings.substring(lhsMappings
										.indexOf(" ") + 1) + "]";
					}
				}

				// Get hermes tag for dbpedia tag & get datatype for the given
				// result.
				for (String dif : mappings.get(lhsMappings)) {
					String[] cut = dif.split(" ");
					String hermesTag = "";
					String datatype = "";

					if (dif.matches("[\\p{L}:]+ [\\w()]+")) {
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
						resultData.put(hermesTag, new LinkedHashSet<String>());
						inverseMap.put(hermesTag, new ArrayList<String>());
					}

					// Case dbtag filter($), but inverse lookup is handled as
					// normal
					// request.
					if (isSpecified(lhsMappings) && !inverse) {

						if (datatype.matches("\\(date\\)")) {

							for (String s : results.get(lhsMappings)) {

								String date = "";

								s = s.substring(s.lastIndexOf("/") + 1);

								// category:$_$_deaths/births or
								// category:$_AD/BC
								if (s.matches("\\w+:\\d+_\\w+")) {
									String filter = "[\\w]+_[BC|AD|E]{2,3}";
									Pattern pattern = Pattern.compile(filter);
									Matcher matcher = pattern.matcher(s);
									if (matcher.find()) {
										String[] yearD = matcher.group().split(
												"_");
										if (yearD[1].matches("BC")
												|| yearD[1].matches("BCE")) {

											date = "-" + yearD[0] + "-1-1";
										}
										else {
											date = yearD[0] + "-1-1)";
										}
									}
									resultData.get(hermesTag).add(date);
									inverseMap.get(hermesTag).add(
											htmlTitle + "&nbsp;(" + s + ")");
									inverseMap.get(hermesTag).add(date);
								}
							}
						}

						if (datatype.matches("\\(string\\)")) {

							for (String s : results.get(lhsMappings)) {
								// Cut languagetags @$$
								if (s.matches(".*@[\\w]{2}")) {

									s = s.replaceAll("@[\\w]{2}", "");
								}
								resultData.get(hermesTag).add(s);

								inverseMap.get(hermesTag).add(
										htmlTitle + "&nbsp;("
												+ s.replaceAll(" ", "&nbsp;")
												+ ")");
								inverseMap.get(hermesTag).add(s);

							}
						}

						// Only in conjunction with a filter -> true if filter
						// found,
						// else false.
						if (datatype.matches("\\(object\\)")) {

							for (String s : results.get(lhsMappings)) {

								resultData.get(hermesTag).add(
										"ist vom Typ " + hermesTag);

								inverseMap.get(hermesTag).add(
										htmlTitle + "&nbsp;("
												+ s.replaceAll(" ", "&nbsp;")
												+ ")");
								inverseMap.get(hermesTag).add(
										"ist vom Typ " + hermesTag);
							}
						}

						// Get only $ var from result.
						if (datatype.matches("\\(concept\\)")) {

							for (String s : results.get(lhsMappings)) {

								String result = "";
								if (lhsMappings.split(" ")[1].contains("$")) {

									String regex = lhsMappings.split(" ")[1];
									int expression = regex.indexOf("$");
									char[] search = s.substring(expression,
											s.length() - 1).toCharArray();

									for (char c : search) {
										if (c == '_' || c == ' ') {
											break;
										}
										result += c;
									}
								}

								result = getDBpediaRedirect(result);

								// Mask it to recognize it (dont't display as
								// input
								// box)

								result = HermesData.getHermesMapping(result);

								if (!result.isEmpty()) {
									result = "!$ConceptLink:: " + result;
								}

								resultData.get(hermesTag).add(result);
								inverseMap.get(hermesTag).add(
										htmlTitle + "&nbsp;("
												+ s.replaceAll(" ", "&nbsp;")
												+ ")");
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

								if (s.matches("[\\w -\\./-]+@\\p{Alpha}{2}")) {
									s = s.substring(0, s.indexOf("@"));
								}
								if (s.matches("[\\w -\\./-]+\\^.*")) {
									s = s.substring(0, s.indexOf("^"));
								}

								s = s.replaceAll("\\.", "");

								// Default xsd:date type [-]CCYY-MM-DD
								if (s.matches("-?[\\d]*-[\\d]*-[\\d]*")) {

									String[] yearD2 = s.split("-");
									if (s.indexOf("-") == 0) {
										datum = "-" + yearD2[1] + "-"
												+ yearD2[2] + "-" + yearD2[3];
									}
									else {
										datum = yearD2[0] + "-" + yearD2[1]
												+ "-" + yearD2[2];
									}
								}
								// October?,? ?YYYY+
								if (s.matches("[\\p{Alpha} ,]*[\\d]+ ?[AD|BC|E]{0,3}/?-? ?[\\d]* ?[AD|BC|E]{2,3}")) {
									String yearFilter = "[\\d]+ ?[AD|BC|E]{2,3}";
									Pattern yearPattern = Pattern
											.compile(yearFilter);
									Matcher matcherY = yearPattern.matcher(s);

									while (matcherY.find()) {
										String date = matcherY.group();
										String year = date
												.split(" ?[\\p{Alpha}]+")[0];
										if (!datum.isEmpty()) {
											datum += " ";
										}
										if (date.contains("AD")
												|| (date.contains("CE") && !date
														.contains("BCE"))) {
											datum += year + "-1-1";
										}
										else {
											datum += "-" + year + "-1-1";
										}
									}
								}
								// Period of time.
								if (s.matches("[\\d]+ ?[AD|BC|E]{0,3} ?(-|to) ?[\\d]+ ?[AD|BC|E]{2,3}")) {

									period = true;
									String periodFilter = "[\\d]+";
									Pattern periodPattern = Pattern
											.compile(periodFilter);
									Matcher matcherP = periodPattern.matcher(s);
									boolean two = false;
									while (matcherP.find()) {
										String date = matcherP.group();
										if (s.contains("AD")
												|| (s.contains("CE") && !s
														.contains("BCE"))) {
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
									resultData.get(hermesTag).add(
											datum + " >> " + datum2);
									inverseMap.get(hermesTag).add(normalTitle);
									inverseMap.get(hermesTag).add(
											datum + " >> " + datum2);
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

								// DBpedia has inconsistent links ==> get main
								// resource
								// site.

								if (s.matches("http://[\\p{Alnum}/.:_]*")) {
									if (conceptIsResource(s)) {
										result = s;
									}
									else {
										result = getRedirect(s.substring(s
												.lastIndexOf("/") + 1));
									}
								}
								else {
									result = getDBpediaRedirect(redirect);
								}

								// Mask it to recognize it (dont't display as
								// input
								// box)
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

								String d1, d2;

								if (s.matches("[\\d., ]+@\\p{Alpha}{2}")) {
									s = s.substring(0, s.indexOf("@"));
								}
								if (s.matches("[\\d.,]+\\^\\^[\\w\\p{Punct}]+")) {
									s = s.substring(0, s.indexOf("^"));
								}

								s = s.replaceAll("\\.", ",");

								if (s.matches("[\\d]+[.,][\\d]+ ?[\\d]*[.,]?[\\d]*")) {

									if (s.contains(" ")) {

										d1 = s.substring(0, s.indexOf(" "));
										d2 = s.substring(s.indexOf(" ") + 1);

										if (hermesTag.contains("Latitude")) {
											while (!resultData.get(hermesTag)
													.add(d1)) {
												d1 = d1 + "0";
											}
											inverseMap.get(hermesTag).add(
													normalTitle);
											inverseMap.get(hermesTag).add(d1);
										}
										if (hermesTag.contains("Longitude")) {
											while (!resultData.get(hermesTag)
													.add(d2)) {
												d2 = d2 + "0";
											}
											inverseMap.get(hermesTag).add(
													normalTitle);
											inverseMap.get(hermesTag).add(d2);
										}

									}
									else {
										while (!resultData.get(hermesTag)
												.add(s)) {
											s = s + "0";
										}
										resultData.get(hermesTag).add(s);
										inverseMap.get(hermesTag).add(
												normalTitle);
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
												"[\\w]+:", "").replaceAll("_",
												" ");
									}
									else {
										string = cutString[1].replaceAll("_",
												" ");
									}
								}
								else {
									// Cut languagetags.
									if (string
											.matches("[\\p{L}\\.:; ]+@\\p{Alpha}{2}")) {
										string = string.substring(0,
												string.indexOf("@") - 1);
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
								if (lhsMappings.contains("latDeg")) {
									coords[0] = s + "° ";
								}
								if (lhsMappings.contains("latMin")) {
									coords[1] = s + "’N ";
								}
								if (lhsMappings.contains("lonDeg")) {
									coords[2] = s + "° ";
								}
								if (lhsMappings.contains("lonMin")) {
									coords[3] = s + "’E";
								}
							}
							if (countCoords == 4) {
								String result = "";
								for (String s : coords) {
									result += s;
								}
								resultData.get(hermesTag).add(result);
								inverseMap
										.get(hermesTag)
										.add("dbpprop:latDeg,&nbsp;dbpprop:latMin,&nbsp;dbpprop:lonDeg,&nbsp;dbpprop:lonMin");
								inverseMap.get(hermesTag).add(result);
							}
						}
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
			String parsed = input.replaceAll("\\p{Punct}*", "").replaceAll(" ",
					"_");
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
			stringQuery.append("SELECT ?resource WHERE { ?resource foaf:page <"
					+ wikilink + "> .}");
			// create the query object
			try {
				RepositoryConnection con = endpointRepo.getConnection();
				try {
					TupleQuery query = con.prepareTupleQuery(
							QueryLanguage.SPARQL, stringQuery.toString());
					TupleQueryResult result = query.evaluate();

					String resultString = "";
					while (result.hasNext()) {
						BindingSet b = result.next();
						Iterator<String> it = b.getBindingNames().iterator();
						while (it.hasNext()) {
							String temp = it.next();
							resultString = b.getBinding(temp).getValue()
									.toString();
						}
					}
					return resultString;
				}
				catch (RepositoryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (MalformedQueryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (QueryEvaluationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally {
					con.close();
				}
			}
			catch (RepositoryException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return "";
	}

	// /**
	// * Sorts the result HashMap in a LinkedHashMap by order of the properties
	// * file.
	// *
	// * @param result unsorted HashMap.
	// * @return sorted LinkedHashMap.
	// */
	// private HashMap<String, HashSet<String>> sortResult(HashMap<String,
	// HashSet<String>> result) {
	// Iterator<String> it = mappings.keySet().iterator();
	// while (it.hasNext()) {
	// String temp = it.next();
	// }
	// }

	/**
	 * Translates a given text.
	 * 
	 * @param string text to translate.
	 * @return translated Text.
	 */
	private static String translate(String string) {

		Translate.setHttpReferrer("this");
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

	/**
	 * Tests whether the sparql endpoint is avaible or not.
	 * 
	 * @return boolean.
	 */
	public static boolean sparqlAvailable() {
		String stringQuery = "ASK WHERE { ?s ?p ?o }";

		try {
			RepositoryConnection con = endpointRepo.getConnection();

			try {
				BooleanQuery query = con.prepareBooleanQuery(
						QueryLanguage.SPARQL, stringQuery.toString());

				if (query.evaluate()) {
					return true;
				}
			}
			catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				con.close();
			}
		}
		catch (HTTPException e) {
			e.printStackTrace();
			return false;
		}
		catch (RepositoryException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return true;

	}

	/**
	 * Returns a list with all hermes attributes specified in the corresponding
	 * properties file.
	 * 
	 * @return list.
	 */
	public HashSet<String> getHermesAttributes() {
		Iterator<String> mappingIt = mappings.keySet().iterator();
		HashSet<String> attributes = new LinkedHashSet<String>();
		while (mappingIt.hasNext()) {
			String lhsMappings = mappingIt.next();
			for (String dif : mappings.get(lhsMappings)) {
				String[] cut = dif.split(" ");
				String hermesTag = "";
				String datatype = "";

				if (dif.matches("[\\p{L}:]+ [\\w()]+")) {
					hermesTag = cut[0];
					datatype = cut[1];
				}
				// case [\\p{L}:]+ [\\p{L}:]+ [\\w()]+
				else {
					hermesTag = cut[0] + " " + cut[1];
					datatype = cut[2];
				}

				if (datatype.equals("(object)")) {
					attributes.add(HermesData.get());
				}
				else {
					attributes.add(hermesTag);
				}
			}
		}
		return attributes;

	}

}