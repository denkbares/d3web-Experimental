/*
 * Created on 12.05.2004 by Chris
 * 
 */
package officeconverter.filetypes;

import java.net.URL;

import officeconverter.Config;

public class MSOfficeFileType extends FileType {
	public MSOfficeFileType() {
		super("MSOffice", new String[] { ".doc", ".rtf" });
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#createFile(java.net.URL)
	 */
	public DocumentFile createFile(URL caseDocumentURL, Config c) throws Exception {
		return new MSOfficeFile(caseDocumentURL, c, this);
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#getMetaSchemaFilename()
	 */
	public String getMetaSchemaFilename() {
		return "metaOO.xml";
	}

}