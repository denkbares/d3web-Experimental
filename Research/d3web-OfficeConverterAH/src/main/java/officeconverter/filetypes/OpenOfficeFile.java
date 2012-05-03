/*
 * Created on 12.05.2004 by Chris
 *  
 */
package officeconverter.filetypes;


import java.net.MalformedURLException;
import java.net.URL;


import officeconverter.Config;
import officeconverter.ConvertException;
import officeconverter.odt.ContentDocument;

import org.jdom.Document;

/**
 * OpenOfficeFile
 * 
 * @author Chris 12.05.2004
 */
public class OpenOfficeFile extends DocumentFile {

	/**
	 * 
	 */
	public OpenOfficeFile(URL caseDocumentURL, Config c) {
		super(caseDocumentURL, c);
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#getBaseURL(java.net.URL)
	 */
	public URL getBaseURL() throws MalformedURLException {
		return new URL("jar", "", this.caseDocumentURL.toExternalForm() + "!/");
	}

	public Document getCaseDocument() throws ConvertException {
		return getDocument(this.caseDocumentURL, "content.xml");
	}

	public Document getMetaDocument() throws ConvertException {
		return getDocument(this.caseDocumentURL, "meta.xml");
	}

	@Override
	public Document getStylesDocument() throws ConvertException {
		return getDocument(this.caseDocumentURL, "styles.xml");
	}

	@Override
	public ContentDocument getContentDocument() {
		return contentDocument;
	}

	@Override
	public URL getCaseDocumentURL() {
		return caseDocumentURL;
	}

}
