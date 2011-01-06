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
