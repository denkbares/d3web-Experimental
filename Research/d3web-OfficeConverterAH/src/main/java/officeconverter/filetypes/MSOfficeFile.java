/*
 * Created on 12.05.2004 by Chris
 *  
 */
package officeconverter.filetypes;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import officeconverter.Config;
import officeconverter.ConvertException;
import officeconverter.odt.ContentDocument;

import org.jdom.Document;
import org.sockdrawer.util.openOffice.OpenOfficeFileConverter;
import org.sockdrawer.util.openOffice.OpenOfficeServerUtility;

/**
 * MSOfficeFile
 * 
 * @author Chris 12.05.2004
 */
public class MSOfficeFile extends DocumentFile {

	private OpenOfficeFile delegate;

	/**
	 * 
	 */
	public MSOfficeFile(URL caseDocumentURL, Config c, MSOfficeFileType type)
			throws Exception {
		super(caseDocumentURL, c);
		URL convertedDocument = convertDocument(caseDocumentURL, type.getTempDir());
		this.delegate = new OpenDocumentFile(convertedDocument, c);
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.DocumentFile#getBaseURL()
	 */
	public URL getBaseURL() throws MalformedURLException {
		return this.delegate.getBaseURL();
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.DocumentFile#getCaseDocument()
	 */
	public Document getCaseDocument() throws ConvertException {
		return this.delegate.getCaseDocument();
	}

	/**
	 * overridden method
	 * 
	 * @see officeconverter.filetypes.DocumentFile#getMetaDocument()
	 */
	public Document getMetaDocument() throws ConvertException {
		return this.delegate.getMetaDocument();
	}

	@Override
	public org.jdom.Document getStylesDocument() throws ConvertException {
		return this.delegate.getStylesDocument();
	}

	private URL convertDocument(URL msURL, File tempDir) throws Exception {
		boolean started = startOpenOfficeServer();
		if (!started) {
			throw new RuntimeException("could not start OpenOffice server");
		}

		File convertedDocumentsDir = tempDir;
		convertedDocumentsDir.mkdirs();

		// path to output dir for converted file
		OpenOfficeFileConverter converter = new OpenOfficeFileConverter();
		converter.setOpenOfficeServerDetails(Config.getServiceHost(), Config.getServicePort());

		String filter = "writer8";
		String filetype = "odt";

		return converter.convertFile(msURL, convertedDocumentsDir, filter, filetype);

	}

	private static boolean startOpenOfficeServer() {
		/*
		 * Try to startup the OpenOffice server as a separate process. The
		 * delay is to give the server time to startup
		 * beacuse, unfortunately, the startup process doesn't seem to return
		 * anything. Kludgy :(
		 */
		while (!OpenOfficeServerUtility.checkServerAvailability(Config
				.getServiceHost(), Config.getServicePort())) {

			try {
				OpenOfficeServerUtility.runOpenOfficeServer(
					Config.getServiceProgramDir()
					+ File.separator
					+ "soffice",
					Config.getServiceHost(),
					Config.getServicePort(),
					Config.getServiceWaitStartDelayMS(),
					true
				);
			} catch (Exception e1) { /* hmmm */ e1.printStackTrace(); }

		}

		return OpenOfficeServerUtility.checkServerAvailability(Config
				.getServiceHost(), Config.getServicePort());
	}

	@Override
	public ContentDocument getContentDocument() {
		return delegate.contentDocument;

	}

	@Override
	public URL getCaseDocumentURL() {
		return delegate.getCaseDocumentURL();
	}

}