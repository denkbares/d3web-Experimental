/*
 * Created on 12.05.2004 by Chris
 *  
 */
package officeconverter.filetypes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import officeconverter.Config;
import officeconverter.ConvertException;
import officeconverter.odt.ContentDocument;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

/**
 * HTMLFile
 * 
 */
public class TextFile extends DocumentFile {

	/**
	 * 
	 */
	public TextFile(URL caseDocumentURL, Config c) {
		super(caseDocumentURL, c);
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#getBaseURL(java.net.URL)
	 */
	public URL getBaseURL() {
		return this.caseDocumentURL;
	}

	public Document getCaseDocument() throws ConvertException {
		InputStream in = null;
		Document doc = null;
		try {

			in = getBaseURL().openStream();
			// FIXME: Hier muss der Text zeilenweise eingelesen und geparst
			// werden
			doc = parse(in);

		} catch (Exception e) {
			throw new ConvertException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e1) {
					// ups!
				}
			}
		}
		return doc;
	}

	/**
	 * Parses line-based textual content to a Dom.
	 * 
	 * @param in
	 * @return
	 * @throws FactoryConfigurationError
	 * @throws ParserConfigurationException
	 * @throws IOException
	 */
	public static Document parse(InputStream inStream) throws IOException {

		Document doc = new Document();

		Element lines = new Element("Lines");
		doc.addContent(lines);

		BufferedReader br_reader = new BufferedReader(new InputStreamReader(inStream));
		String line = br_reader.readLine();

		// check to see if it is null - end of file
		while (line != null) {
			add(lines, line);
			line = br_reader.readLine();
		}
		br_reader.close();
		return doc;
	}

	private static void add(Element lines, String lineString) {
		Element lineNode = new Element("Line");
		lineNode.addContent(new CDATA(lineString));
		lines.addContent(lineNode);
	}

	/**
	 * There's no MetaDocument for .txt - Files.
	 * 
	 * One coiuld create a DOM containing filename, date etc.
	 * 
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.DocumentFile#getMetaDocument()
	 */
	public Document getMetaDocument() throws ConvertException {
		return null;
	}

	@Override
	public Document getStylesDocument() throws ConvertException {
		return null;
	}

	@Override
	public ContentDocument getContentDocument() {
		return null;
	}

	@Override
	public URL getCaseDocumentURL() {
		return null;
	}
}