package officeconverter.filetypes;

import java.net.URL;

import officeconverter.Config;

public class OpenDocumentFileType extends FileType {
	public OpenDocumentFileType() {
		super("OpenDocumentText", new String[] { ".odt" });
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#createFile(java.net.URL)
	 */
	public DocumentFile createFile(URL caseDocumentURL, Config c) {
		return new OpenDocumentFile(caseDocumentURL, c);
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.FileType#getMetaSchemaFilename()
	 */
	public String getMetaSchemaFilename() {
		return "meta.xml";
	}

}