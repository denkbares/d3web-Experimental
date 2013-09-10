/*
 * Created on 12.05.2004 by Chris
 * 
 */
package officeconverter.filetypes;

import java.net.URL;

import officeconverter.Config;

public class OpenOfficeFileType extends FileType {
	public OpenOfficeFileType() {
		super("OpenOffice/StarOffice", new String[] { ".sxw" });
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#createFile(java.net.URL)
	 */
	public DocumentFile createFile(URL caseDocumentURL, Config c) {
		return new OpenOfficeFile(caseDocumentURL, c);
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