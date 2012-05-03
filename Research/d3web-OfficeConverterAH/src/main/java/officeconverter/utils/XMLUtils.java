package officeconverter.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.sun.org.apache.xpath.internal.XPathAPI;

/**
 * @author mschuhmann
 * 
 */
public class XMLUtils {

	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(XMLUtils.class
			.toString());

	public static org.jdom.Document getJDomDocument(InputStream xmlStream) {
		org.jdom.Document doc = null;
		try {
			SAXBuilder parser = new SAXBuilder();
			doc = parser.build(xmlStream);
		} catch (JDOMException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return doc;
	}

//	public static byte[] dom2ByteArray(Document doc) {
//		ByteArrayOutputStream byteArray;
//		try {
//			byteArray = new ByteArrayOutputStream();
//			StreamResult sr = new StreamResult(byteArray);
//
//			TransformerFactory transFactory = TransformerFactory.newInstance();
//			Transformer transformer = transFactory.newTransformer();
//			Properties props = transformer.getOutputProperties();
//			props.put(OutputKeys.METHOD, "xml");
//			props.put(OutputKeys.INDENT, "yes");
//			props.put(OutputKeys.ENCODING, "ISO-8859-1");
//
//			transformer.setOutputProperties(props);
//			transformer.transform(new DOMSource(doc), sr);
//			byte[] b = byteArray.toByteArray();
//			return b;
//		} catch (TransformerConfigurationException e) {
//			if (logger.isLoggable(Level.SEVERE)) {
//				logger.severe(e.getLocalizedMessage());
//			}
//		} catch (TransformerFactoryConfigurationError e) {
//			if (logger.isLoggable(Level.SEVERE)) {
//				logger.severe(e.getLocalizedMessage());
//			}
//		} catch (TransformerException e) {
//			if (logger.isLoggable(Level.SEVERE)) {
//				logger.severe(e.getLocalizedMessage());
//			}
//		}
//		return null;
//	}
//
//	public static String dom2String(Document doc) {
//		String xmlDoc = new String(dom2ByteArray(doc));
//		return xmlDoc;
//	}

	public static Document getDocument(InputStream xmlStream) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			Document doc = builder.parse(xmlStream);
			return doc;
		} catch (FactoryConfigurationError e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (ParserConfigurationException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (SAXException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return null;
	}

	public static Document getDocument(String xmlString) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder builder = dbf.newDocumentBuilder();

			InputStream xmlStream = new ByteArrayInputStream(xmlString
					.getBytes());
			Document doc = builder.parse(xmlStream);
			return doc;
		} catch (FactoryConfigurationError e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (ParserConfigurationException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (SAXException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (IOException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return null;
	}

	/**
	 * Method removeAllChilds. Removes all childs from a given node. If
	 * (node==null) or node has no childs, nothing is done.
	 * 
	 * @param node
	 *            Node node from which all childs should be deleted
	 */
	public static void removeAllChilds(Node node) {
		if (node == null)
			return;
		NodeList tmp = node.getChildNodes();
		for (int i = 0; i < tmp.getLength(); i++) {
			node.removeChild(tmp.item(i));
		}
	}

	public static Vector<String> getChildsOfNode(Document doc, String node,
			String child) {
		try {
			Vector<String> result = new Vector<String>();
			NodeList sections = XPathAPI.selectNodeList(doc, "//" + node + "/"
					+ child);

			for (int i = 0; i < sections.getLength(); i++) {
				result.add(((Element) sections.item(i)).getFirstChild()
						.getNodeValue());
			}
			return result;
		} catch (TransformerException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return null;
	}

	public static String getTextFromNode(Document doc, String node) {
		if (node == null)
			return "";
		NodeList nodelist;
		try {
			nodelist = XPathAPI.selectNodeList(doc, "//" + node);
		} catch (TransformerException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
			return "";
		}

		return getTextFromTextNodes(nodelist.item(0));
	}

	/**
	 * Method getTextFromTextNodes. Returns all text value from nodes, who are
	 * childs from node node (not only direct childs). If (node==null) or node
	 * hase no text childs, an empty string is returned.
	 * 
	 * @param node
	 *            Node
	 * @return String
	 */
	public static String getTextFromTextNodes(Node node) {
		if (node == null)
			return "";
		StringBuffer sb = new StringBuffer();
		NodeList nodelist;
		try {
			nodelist = XPathAPI.selectNodeList(node, ".//text()");
		} catch (TransformerException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
			return "";
		}
		for (int i = 0; i < nodelist.getLength(); i++) {
			sb.append(((Text) nodelist.item(i)).getData());
		}
		return sb.toString();
	}

	/**
	 * Transforms the XML-content. Exception are submitted to LogCreator.
	 * 
	 * @param xmlFile
	 *            xml-file containing the data
	 * @param xslFile
	 *            xsl-file containing the stylesheet
	 * @return String output, empty String if error occured
	 */
	public static String transform(File xmlFile, File xslFile) {
		try {
			InputStream styleStream = new FileInputStream(xslFile);
			return transform(xmlFile, styleStream);
		} catch (FileNotFoundException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return "";
	}

	public static String transform(InputStream xmlStream,
			InputStream xslStream, String transformerParameter,
			String transformerParameterValue) {
		Map<String, String> map = new HashMap<String, String>();
		map.put(transformerParameter, transformerParameterValue);
		return transform(xmlStream, xslStream, map);
	}

	public static String transform(InputStream xmlStream,
			InputStream xslStream, Map<String, String> transformerParams) {
		try {
			Source data = new StreamSource(xmlStream);
			Source style = new StreamSource(xslStream);
			StringWriter sWriter = new StringWriter();
			Result output = new StreamResult(sWriter);
			// create Transformer and perform the tranfomation
			Transformer xslt = TransformerFactory.newInstance().newTransformer(
					style);

			if (transformerParams != null) {
				for (Iterator<String> iter = transformerParams.keySet()
						.iterator(); iter.hasNext();) {
					String key = iter.next();
					xslt.setParameter(key, (String) transformerParams.get(key));
				}
			}

			xslt.transform(data, output);

			return sWriter.toString();
		} catch (TransformerConfigurationException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (TransformerFactoryConfigurationError e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (TransformerException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return "";
	}

	public static String transform(Reader xmlReader, InputStream xslStream,
			Map<String, String> transformerParams) {
		try {
			Source data = new StreamSource(xmlReader);
			Source style = new StreamSource(xslStream);
			StringWriter sWriter = new StringWriter();
			Result output = new StreamResult(sWriter);
			// create Transformer and perform the tranfomation
			Transformer xslt = TransformerFactory.newInstance().newTransformer(
					style);

			if (transformerParams != null) {
				for (Iterator<String> iter = transformerParams.keySet()
						.iterator(); iter.hasNext();) {
					String key = iter.next();
					xslt.setParameter(key, (String) transformerParams.get(key));
				}
			}

			xslt.transform(data, output);

			return sWriter.toString();
		} catch (TransformerConfigurationException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (TransformerFactoryConfigurationError e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		} catch (TransformerException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return "";
	}

	/**
	 * Transforms the XML-content. Exception are submitted to LogCreator.
	 * 
	 * @param xmlFile
	 *            xml-file containing the data
	 * @param styleStream
	 *            InputStream xsl-InputStream containing the stylesheet
	 * @return String output, empty String if error occured
	 */
	public static String transform(File xmlFile, InputStream styleStream) {
		try {
			InputStream dataStream = new FileInputStream(xmlFile);
			// create XSLT Source and Result objects
			return transform(dataStream, styleStream);
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return "";
	}

	public static String transform(InputStream xmlStream, InputStream xslStream) {
		return transform(xmlStream, xslStream, null);
	}

	/**
	 * Method addAttrToElemLocatedByAttr. Adds a new attribut (newAttr) with a
	 * given value (newAttrValue) to a existing element, which is located by an
	 * existing attribute (locateAttr) with a specific value (locateAttrValue).
	 * E.g. to add the attribute 'error="Bitte nur Zahlen eingeben!"' to the
	 * element '<entry desc="Semester" name="semester" value="" type="text"
	 * size="17"/>' you can call addAttrToElemLocatedByAttr(doc, "desc",
	 * "Semester", "error", "Bitte...");
	 * 
	 * @param doc
	 * @param locateAttr
	 * @param newAttr
	 * @param newAttrValue
	 * @return Document
	 */
	public static Document addAttrToElemLocatedByAttr(Document doc,
			String locateAttr, String locateAttrValue, String newAttr,
			String newAttrValue) {
		try {
			NodeList sections = XPathAPI.selectNodeList(doc, "//"
					+ "self::node()[@" + locateAttr + "='" + locateAttrValue
					+ "']");
			Element elem = (Element) sections.item(0);
			elem.setAttribute(newAttr, newAttrValue);
		} catch (Exception e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.getLocalizedMessage());
			}
		}
		return doc;
	}
}
