/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package de.d3web.proket.input.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;

import de.d3web.proket.utils.IDUtils;
import de.d3web.proket.utils.ProKEtLogger;

/**
 * Generator class for replacing generate tags by really created xml tags
 * (questions, answers).
 * 
 * 
 * @author Martina Freiberg
 * @author Johannes Mitlmeier, Tobias Mikschl
 * 
 */
public class Generator extends DefaultHandler {

	private static final Logger logger = ProKEtLogger.getLogger();
	private Hashtable<String, Integer> counters;
	private List<String> doubleIds;
	private File inputFile;
	private File outputFile;
	private QuestionGenerator questionGenerator;
	private Hashtable<String, String> usedIDs;
	private XMLFileWriter xmlWriter;

	// Constructor: initialize input/output file and counter that maps
	// already generated id's with a counter
	public Generator(File inputFile, File outputFile) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		counters = new Hashtable<String, Integer>();
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName) {
		// if inspected tag is not generate, just close it
		if (!qName.equals("generate")) {
			xmlWriter.closeXMLElement(qName);
		}
	}

	/**
	 * Inititate generating and XML writing
	 * 
	 * @created 10.10.2010
	 */
	public void generate() {
		for (String str : IDUtils.getNamespaceTags()) {
			counters.put(str, 1);
		}
		xmlWriter = new XMLFileWriter(outputFile);

		// create new SAX parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser;
		try {
			saxParser = factory.newSAXParser();
			// collect IDs
			IDCollector myIdCollector = new IDCollector();

			// parsing the elements for getting the used and double ids
			saxParser.parse(new InputSource(new FileInputStream(inputFile)),
					myIdCollector);
			usedIDs = myIdCollector.getUsedIds();
			doubleIds = myIdCollector.getDoubleIds();

			// TODO this is not really needed, only for logging, isnt it?
			// StringBuilder ids = new StringBuilder();
			// make enumeration out of usedIDs, append them to a stringbuilder
			// for (Enumeration<String> e = usedIDs.keys();
			// e.hasMoreElements();) {
			// ids.append("\"").append(e.nextElement()).append("\" ");
			// }
			// logger.finest("IDs: " + ids);

			// create an appropriate questionGenerator with the set of used IDs
			questionGenerator = new QuestionGenerator(xmlWriter, usedIDs);

			// actual parsing of the XML content and writing to the file
			if (xmlWriter.open()) {
				saxParser.parse(
						new InputSource(new FileInputStream(inputFile)), this);
				xmlWriter.close();
			}
		} catch (ParserConfigurationException e1) {
			logger.severe(String.format("Error parsing input file %s: %s",
					inputFile.getName(), e1.getMessage()));
			e1.printStackTrace();
		} catch (SAXException e1) {
			logger.severe(String.format("Error parsing input file %s: %s",
					inputFile.getName(), e1.getMessage()));
			e1.printStackTrace();
		} catch (IOException e1) {
			logger.severe(String.format("Error parsing input file %s: %s",
					inputFile.getName(), e1.getMessage()));
			e1.printStackTrace();
		}
	}

	@Override
	/** 
	 * Start parsing an element
	 */
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		// if generate, then use generator
		if (qName.equals("generate")) {
			questionGenerator.generate(atts);

			// otherwise (e.g., if mixed types in a dialog)
		} else {
			AttributesImpl attsImpl = new AttributesImpl(atts);

			// if no id is given and if qname is just a prefix
			if ((atts.getValue("id") == null && IDUtils.needsNamspace(qName))) {

				// as long as usedIDs contain element qName and counter
				while (usedIDs.containsKey(qName + counters.get(qName))) {
					// count element up
					counters.put(qName, counters.get(qName) + 1);
				}

				// add to attsImmpl and usedIDs
				attsImpl.addAttribute(null, null, "id", null,
						qName + counters.get(qName));
				usedIDs.put(qName + counters.get(qName), qName);

				// add last count up
				counters.put(qName,
						counters.get(qName) == null ? 1
								: counters.get(qName) + 1);
			}

			// if qname is prefix and id not valid or a double id
			else if ((IDUtils.needsNamspace(qName) && !IDUtils
					.isValidId(atts.getValue("id")))
					|| (doubleIds.contains(atts.getValue("id")))) {

				// invalid id / double id
				while (usedIDs.containsKey(qName + counters.get(qName))) {

					// count on
					counters.put(qName, counters.get(qName) + 1);
				}
				attsImpl.setValue(atts.getIndex("", "id"),
						qName + counters.get(qName));
				usedIDs.put(qName + counters.get(qName), qName);
				counters.put(qName, counters.get(qName) + 1);

				// log info regarding the replaced id
				logger.info("Id that is double or not matching specification: \""
						+ atts.getValue("id")
						+ "\" replaced by \""
						+ attsImpl.getValue("id") + "\"");
			}
			if (atts.getValue("parent-id") != null) {
				if (!usedIDs.containsKey(atts.getValue("parent-id"))) {
					logger.info(qName + " - " + attsImpl.getValue("id")
							+ ": parent-id without fitting reference in usedIDs: "
							+ atts.getValue("parent-id"));
				}
			}

			// write XML element
			xmlWriter.openXMLElement(qName, attsImpl);
		}
	}
}
