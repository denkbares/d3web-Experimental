package officeconverter.odt;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import officeconverter.Config;
import officeconverter.Logging;
import officeconverter.utils.DiskUtils;
import officeconverter.utils.XMLUtils;


public class OODocument {
	
	ZipFile zippedDoc;
	Map<String, ZipEntry> entries;
	MetaDocument meta = null;
	StylesDocument styles = null;
	ContentDocument cont = null;
	Config c;


	public OODocument(String file, Config c) {
		this.c = c;
		try {
			zippedDoc = new ZipFile(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		entries = new HashMap<String, ZipEntry>();
		for (Enumeration<? extends ZipEntry> e = zippedDoc.entries();
				e.hasMoreElements(); ) {
			ZipEntry entry = e.nextElement();
			entries.put(entry.getName(), entry);
		}
	}

	
	private final static String[] IMPORTANT_FILES = 
		new String[] { "content.xml", "styles.xml", "meta.xml" };
	
	
	private boolean checkFiles() {
		for (String s : IMPORTANT_FILES)
			if (!entries.containsKey(s))
				return false;
		return true;
	}

	
	private void process() {
		
		if (!checkFiles()) {
			Logging.getLogger().log(Level.SEVERE,
				"not all needed XML-Files are embedded in file " + zippedDoc.getName() + "!");
			return;
		}
		
		InputStream iStream = null;
		try {
			iStream = zippedDoc.getInputStream(entries.get("meta.xml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		meta = new MetaDocument(XMLUtils.getJDomDocument(iStream));

		try {
			iStream = zippedDoc.getInputStream(entries.get("styles.xml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		styles = new StylesDocument(XMLUtils.getJDomDocument(iStream));

		try {
			iStream = zippedDoc.getInputStream(entries.get("content.xml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		cont = new ContentDocument(XMLUtils.getJDomDocument(iStream), c);

	}

	public void saveAsHtml(String filename, String subDir) {
		if (cont == null)
			process();
		if (cont == null)
			return;
		Pattern pat = Pattern
				.compile("img src=\"(.+?)\"(?:\\s+clip=\"(.+?)\")?");
		Matcher matcher = pat.matcher(cont.html);

		int p = filename.lastIndexOf("/");
		if (p == -1)
			p = filename.lastIndexOf(File.separator);

		String dir = filename.substring(0, p);
		File dirF = new File(dir, subDir);

		while (matcher.find()) {
			
			if (!dirF.exists())
				dirF.mkdir();

			String img = matcher.group(1);

			Logging.getLogger().log(Level.INFO,
				"writing image... " + img + " to " + dir);

			try {
				
				// entries.
				InputStream in =
					zippedDoc.getInputStream(
						entries.get(matcher.group(1)));
				
				FileOutputStream out =
					new FileOutputStream(
						new File(dirF, img));
				
				int i;
				byte buf[] = new byte[1024];
				while ((i = in.read(buf)) != -1)
					out.write(buf, 0, i);
				
				in.close();
				out.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		DiskUtils.savePageToDisk(new File(filename), cont.getHtml(), c.getEncoding());

		Logging.getLogger().log(Level.INFO,
			"done.");
	}

}
