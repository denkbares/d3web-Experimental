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

package de.d3web.proket.utils;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import de.d3web.proket.input.xml.ParseException;
import de.d3web.proket.input.xml.XMLErrorHandler;

/**
 * General XML util methods.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class XMLUtils {

	/**
	 * Get Boolean Object from a given XML element and attribute.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @return the parsed Boolean value
	 */
	public static Boolean getBoolean(Element el, String attribute) {
		return getBoolean(el, attribute, null);
	}

	/**
	 * Get Boolean Object from a given XML element and attribute and provide a
	 * default value for non-successfully parsing.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @param defaultValue a default value if parsing was not successful
	 * @return the parsed Boolean value
	 */
	public static Boolean getBoolean(Element el, String attribute,
			Boolean defaultValue) {
		String value = el.getAttribute(attribute);
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")
				|| value.equalsIgnoreCase("1")) {
			return new Boolean(true);
		}
		if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")
				|| value.equalsIgnoreCase("0")) {
			return Boolean.valueOf(false);
		}

		if (defaultValue == null) {
			return null;
		}
		return Boolean.valueOf(defaultValue);
	}

	/**
	 * Get Color Object from a given XML element and attribute and provide a
	 * default value for non-successfully parsing.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @return the parsed Integer value
	 */
	public static Color getColor(Element el, String attribute) {
		return getColor(el, attribute, null);
	}

	/**
	 * Get Color Object from a given XML element and attribute and provide a
	 * default value for non-successfully parsing.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @param defaultValue a default value if parsing was not successful
	 * @return the parsed Color value
	 */
	public static Color getColor(Element el, String attribute,
			Color defaultValue) {
		String value = el.getAttribute(attribute);
		Color result = ColorUtils.parseColor(value);
		if (result != null) {
			return result;
		}
		return defaultValue;
	}

	/**
	 * Get Integer Object from a given XML element and attribute.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @return the parsed Integer value
	 */
	public static Double getDouble(Element el, String attribute)
			throws ParseException {
		return getDouble(el, attribute, null);
	}

	/**
	 * Get Double Object from a given XML element and attribute and provide a
	 * default value for non-successfully parsing.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @param defaultValue a default value if parsing was not successful
	 * @return the parsed Double value
	 */
	public static Double getDouble(Element el, String attribute,
			Double defaultValue) {
		String value = el.getAttribute(attribute);
		if (value != null && value.length() > 0) {
			try {
				return Double.parseDouble(value);
			} catch (Exception ex) {
				return defaultValue;
			}
		}
		else {
			return defaultValue;
		}
	}

	/**
	 * Get Integer Object from a given XML element and attribute.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @return the parsed Integer value
	 */
	public static Integer getInt(Element el, String attribute) {
		return getInt(el, attribute, null);
	}

	/**
	 * Get Integer Object from a given XML element and attribute and provide a
	 * default value for non-successfully parsing.
	 * 
	 * @created 11.10.2010
	 * @param el the XML element
	 * @param attribute the attribute
	 * @param defaultValue a default value if parsing was not successful
	 * @return the parsed Integer value
	 */
	public static Integer getInt(Element el, String attribute,
			Integer defaultValue) {
		String value = el.getAttribute(attribute);
		if (value != null && value.length() > 0) {
			try {
				return Integer.parseInt(value);
			} catch (Exception ex) {
				return defaultValue;
			}
		}
		else {
			return defaultValue;
		}
	}

	public static String getNodeName(Element tag) {
		return tag.getNodeName();
	}

	public static Element getRoot(File xmlFile, String schemaFileName)
			throws Exception {
		String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
		String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
		String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(true);

			// use schema only if desired
			if (schemaFileName != null) {
				factory.setValidating(true);
				try {
					factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
					factory.setAttribute(JAXP_SCHEMA_SOURCE, new File(
							schemaFileName));
				} catch (IllegalArgumentException x) {
					// Happens if the parser does not support JAXP 1.2
				}
			} else {
				factory.setValidating(false);
			}

			// get root and return it
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler(new XMLErrorHandler());
			Document result = builder.parse(new InputSource(
					new FileInputStream(xmlFile)));
			return result.getDocumentElement();
		} catch (SAXException ex) {
			throw new IOException(String.format(
					"File %s is not valid by schema %s (original message: %s)",
					xmlFile.toString(), schemaFileName, ex.getMessage()));
		} catch (Exception ex) {
			throw new IOException(String.format(
					"File %s could not be read: %s", xmlFile.toString(), ex));
		}
	}

	public static Element getRoot(String xmlFileName, String schemaFileName)
			throws Exception {
		return getRoot(new File(xmlFileName), schemaFileName);
	}

	public static String getStr(Element el, String attribute) {
		return getStr(el, attribute, null);
	}

	public static String getStr(Element el, String attribute,
			String defaultValue) {
		String value = el.getAttribute(attribute);
		if (value != null && value.length() > 0) {
			return value;
		}
		else {
			return defaultValue;
		}
	}
}
