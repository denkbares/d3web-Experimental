/*
 * Created on 12.05.2004 by Chris
 * 
 */
package officeconverter.filetypes;

import java.net.URL;

import officeconverter.Config;

public class TextFileType extends FileType {
	public TextFileType() {
		super("Text", new String[] { ".txt" });
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#createFile(java.net.URL)
	 */
	public DocumentFile createFile(URL caseDocumentURL, Config c) {
		return new TextFile(caseDocumentURL, c);
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#getMetaSchemaFilename()
	 */
	public String getMetaSchemaFilename() {
		return null;
	}
}