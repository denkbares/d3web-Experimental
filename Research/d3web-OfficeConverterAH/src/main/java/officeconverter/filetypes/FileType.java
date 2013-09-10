/*
 * Created on 03.02.2004
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package officeconverter.filetypes;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ResourceBundle;
import java.util.logging.Level;

import officeconverter.Config;
import officeconverter.Logging;

public abstract class FileType {

	public final static FileType OPENOFFICE_FILE = new OpenOfficeFileType();
	public final static FileType MSOFFICE_FILE = new MSOfficeFileType();

	static {
		ResourceBundle bundle = ResourceBundle.getBundle("officeconverter");

		String tempDirStr = bundle.getString("tempDir");
		if (tempDirStr.startsWith("tmpdir")) {
			tempDirStr = tempDirStr.replaceFirst("tmpdir", "");
			tempDirStr = System.getProperty("java.io.tmpdir") + tempDirStr;
		}
		tempDir = new File(tempDirStr);
	}

	private static File tempDir;
	private String name;
	private String[] extensions;

	public FileType(String name, String[] extensions) {
		this.name = name;
		this.extensions = extensions;
	}

	public File getTempDir() {
		return tempDir;
	}

	public void setTempDir(File newTempDir) {
		tempDir = newTempDir;
	}

	public boolean match(String extension) {
		for (int i = 0; i < this.extensions.length; i++) {
			String ext = this.extensions[i];
			if (ext.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}

	public abstract DocumentFile createFile(URL caseDocumentURL, Config c)
			throws Exception;

	/**
	 * Returns an InputStream for the given URL without using any cache.
	 */
	public static InputStream openStream(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.setUseCaches(false);
		return connection.getInputStream();
	}

	public static FileType getFileType(File file) {
		return getFileType(file == null ? "null" : file.getName());
	}

	public static FileType getFileType(URL url) {
		return getFileType(url.toString());
	}

	public static FileType getFileType(String s) {
		String extension = getExtension(s);
		FileType fileType = _getFileType(extension);
		if (fileType == null)
			Logging.getLogger().log(Level.SEVERE,
				"filetype '" + extension + "' (" + s + ") not supported");
		return fileType;
	}
	
	private static String getExtension(String s) {
		int i = s.lastIndexOf(".");
		return i == -1 ? "" : s.substring(i);
	}

	private static FileType _getFileType(String extension) {
		if (FileType.OPENOFFICE_FILE.match(extension))
			return FileType.OPENOFFICE_FILE;
		else if (FileType.MSOFFICE_FILE.match(extension))
			return FileType.MSOFFICE_FILE;
		else
			return null;
	}

	public abstract String getMetaSchemaFilename();

	/*
	 * overridden method
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}

}