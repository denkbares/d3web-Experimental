package de.d3web.we.lod;

import java.util.HashMap;

public class LinkedOpenDataSet {

	private static LinkedOpenDataSet instance = null;
	private HashMap<ConceptType, LinkedOpenData> conceptMappings;

	// private static String pathString = KnowWEEnvironment.getInstance()
	// .getKnowWEExtensionPath();

	/**
	 * 
	 * Creates a Set of LinkedData Objects, which are stored in a HashMap. Their
	 * key is the corresponding property file name as Enumeration.
	 * 
	 * @throws Exception
	 *             if one of the property files is not in correct syntax
	 */
	private LinkedOpenDataSet() throws Exception {

		conceptMappings = new HashMap<ConceptType, LinkedOpenData>();

		conceptMappings.put(ConceptType.Ereignis, new LinkedOpenData(
				"Ereignis.properties"));
		conceptMappings.put(ConceptType.Geographika, new LinkedOpenData(
				"Geographika.properties"));
		conceptMappings.put(ConceptType.Person, new LinkedOpenData(
				"Person.properties"));
	}

	/**
	 * Singleton.
	 * 
	 * @return instance
	 */
	public static LinkedOpenDataSet getInstance() {
		if (instance == null) {
			try {
				instance = new LinkedOpenDataSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;

	}

	// /**
	// * List directory contents for a resource folder. Not recursive. This is
	// * basically a brute-force implementation. Works for regular files and
	// also
	// * JARs.
	// *
	// * @param path
	// * Should end with "/", but not start with one.
	// * @return Just the name of each member item, not the full paths.
	// */
	// public String[] getResourceListing() {
	//
	// /* A JAR path */
	// String jarPath = pathString;
	// JarFile jar = null;
	// try {
	// jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
	// } catch (UnsupportedEncodingException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in
	// // jar
	// Set<String> result = new HashSet<String>(); // avoid duplicates in case
	// // it is a subdirectory
	// while (entries.hasMoreElements()) {
	// String name = entries.nextElement().getName();
	// if (name.startsWith(pathString)) { // filter according to the path
	// String entry = name.substring(pathString.length());
	// int checkSubdir = entry.indexOf("/");
	// if (checkSubdir >= 0) {
	// // if it is a subdirectory, we just return the directory
	// // name
	// entry = entry.substring(0, checkSubdir);
	// }
	// result.add(entry);
	// }
	// return result.toArray(new String[result.size()]);
	// }
	// return null;
	// }

	/**
	 * Get the LinkedOpenData-Object for a specified concepttype.
	 * 
	 * @param concept
	 *            concepttype
	 * @return
	 */
	public LinkedOpenData getLOD(ConceptType concept) {
		return conceptMappings.get(concept);
	}

}
