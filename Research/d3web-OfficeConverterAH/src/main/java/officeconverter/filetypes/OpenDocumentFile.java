package officeconverter.filetypes;

import java.net.URL;

import officeconverter.Config;
import officeconverter.ConvertException;
import officeconverter.odt.ContentDocument;
import officeconverter.odt.MetaDocument;
import officeconverter.odt.StylesDocument;


/**
 * OpenOfficeFile
 * 
 * @author Chris 12.05.2004
 */
public class OpenDocumentFile extends OpenOfficeFile {
	/**
	 * 
	 */
	public OpenDocumentFile(URL caseDocumentURL, Config c) {
		super(caseDocumentURL, c);
		try {
			metaDocument = new MetaDocument(getMetaDocument());
			stylesDocument = new StylesDocument(getStylesDocument());
			contentDocument = new ContentDocument(getCaseDocument(), c);
		} catch (ConvertException e) {
			e.printStackTrace();
		}
	}

}
