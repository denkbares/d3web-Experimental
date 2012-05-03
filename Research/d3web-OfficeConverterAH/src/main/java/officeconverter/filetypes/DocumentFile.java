/*
 * Created on 12.05.2004 by Chris
 *  
 */
package officeconverter.filetypes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import officeconverter.Config;
import officeconverter.ConvertException;
import officeconverter.ConverterUtils;
import officeconverter.Logging;
import officeconverter.odt.ContentDocument;
import officeconverter.odt.MetaDocument;
import officeconverter.odt.StylesDocument;
import officeconverter.utils.DiskUtils;
import officeconverter.utils.ImageClipping;

import org.clapper.util.html.HTMLUtil;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import de.casetrain.cleanup.Cleaner;

/**
 * DocumentFile
 * 
 * @author Chris 12.05.2004
 */
public abstract class DocumentFile {
	
	private final static class ImgDescr {
		int number;
		String name;
		String ext;
		String clipping;
		String scaling;
		
		ImgDescr(String name, String ext, String clipping, String scaling, int number) {
			this.number = number;
			this.name = name;
			this.ext = ext;
			this.clipping = clipping;
			this.scaling = scaling;
		}
		
		public int getNumber() { return number; }
		public String getName() { return name; }
		public String getExt() { return ext; }
		public String getClipping() { return clipping; }
		public String getScaling() { return scaling; }
	}
	
	//Liste für bereits vorhandene Bilder!
	List<ImgDescr> images = new LinkedList<ImgDescr>();
	protected MetaDocument metaDocument = null;
	protected StylesDocument stylesDocument = null;
	protected ContentDocument contentDocument = null;
	protected URL caseDocumentURL = null;
	protected Config config = null;

	public DocumentFile(URL caseDocumentURL, Config config) {
		this.caseDocumentURL = caseDocumentURL;
		this.config = config;
	}
	
	public abstract URL getCaseDocumentURL();
	public abstract URL getBaseURL() throws MalformedURLException;
	public abstract Document getCaseDocument() throws ConvertException;
	public abstract Document getStylesDocument() throws ConvertException;
	public abstract Document getMetaDocument() throws ConvertException;
	public abstract ContentDocument getContentDocument();
	
	private static Document getJDomDocument(InputStream xmlStream) throws JDOMException, IOException {
		Document doc = null;
		SAXBuilder parser = new SAXBuilder();
		parser.setEntityResolver(new EntityResolver() {
			public InputSource resolveEntity(String publicId, String systemId) {
				if ("-//OpenOffice.org//DTD OfficeDocument 1.0//EN".equals(publicId)) {
					URL officeDTD =
						getClass().getResource(
							"/importOpenOfficeDocuments/dtd/officedocument/1_0/office.dtd");
					return new InputSource(officeDTD.toExternalForm());
				}
				return null;
			}
		});
		doc = parser.build(xmlStream);
		return doc;
	}

	protected Document getDocument(URL documentURL, String relPath) throws ConvertException {
		InputStream in = null;
		try {
			URL docUrl = new URL("jar", "", documentURL.toExternalForm() + "!/"
					+ relPath + "");

			in = FileType.openStream(docUrl);
			Document content = getJDomDocument(in);
			return content;
		} catch (Exception e) {
			throw new ConvertException(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					throw new ConvertException(e);
				}
			}
		}
	}

	/**
	 * @param fileWithRelPath
	 * @return file type or null, if no file type given
	 */
	private String getFiletype(String fileWithRelPath) {
		int indexOfPoint = fileWithRelPath.lastIndexOf('.');
		if (indexOfPoint < 0) {
			Logging.getLogger().warning(
					"'" + fileWithRelPath + "' has no file extension.");
			return null;
		}

		String fileType = fileWithRelPath.substring(indexOfPoint + 1,
				fileWithRelPath.length());

		return fileType;

	}

	private boolean checkFileType(String fileType) {
		return
			"jpg".equalsIgnoreCase(fileType)
			|| "png".equalsIgnoreCase(fileType)
		;
	}
	
	private final static Pattern IMAGE =
		Pattern.compile(
			"img src=\"(.+?)\"(?:\\s+clip=\"(.*?)\")?(\\Wscaling[\\W][\\w|\\s|\\.|,]+[\\W])?"
		);

	private final static Pattern FILENAME = Pattern.compile("(\\w*)\\.(\\w*)");

	/**
	 * Returns a HTML-representation of the document. Images are exported to
	 * baseDir/Config.DEFAULT_PICTURES_SUBDIR
	 * 
	 * @param baseDir
	 * @return HTML-document
	 * @throws Exception 
	 */
	private String getAsHTML(Config config, File baseDir) {
		if (getContentDocument() == null)
			return "";
		
		StringBuffer buffy = new StringBuffer();
		
		String subDir = config.getEmbeddedObjectsDir();

		int imgCounter = 1;

		Matcher matcher = IMAGE.matcher(getContentDocument().getHtml());
		while (matcher.find()) {
			boolean oleObject = false;
			
			String imageSrc = matcher.group(1);
			if (imageSrc.startsWith("./ObjectReplacements")) {
				oleObject = true;
				imageSrc =
					imageSrc.replaceAll(
						"./ObjectReplacements",
						"ObjectReplacements"
					);
			}
			try {
				URL docUrl = new URL("jar", "", getCaseDocumentURL()
						.toExternalForm()
						+ "!/" + imageSrc);

				File imgDir = new File(baseDir, subDir);
				imgDir.mkdir();

				int pos = imageSrc.lastIndexOf("/");
				if (pos == -1)
					pos = imageSrc.lastIndexOf(File.separator);
				String imageFilename = imageSrc.substring(pos + 1, imageSrc.length());

				String fileType = getFiletype(imageFilename);
				if (oleObject && fileType == null) { // does this happen?
					System.err.println("found half-named image: '" + imageFilename + "'");
					fileType = "png";
					imageFilename = imageFilename.replaceAll(" ", "_");
					imageFilename = imageFilename + "." + fileType;
				}

				if (!checkFileType(fileType)) {
					Logging.getLogger().info(imageSrc + " has wrong file format.");
					continue;
				}
				
				Logging.getLogger().info("Processing Image #" + imgCounter);
				
			    String clipping = matcher.group(2);
			    String scaling = matcher.group(3);
			    
			    //Identisches Bild schon enthalten?
			    boolean newImage = true;
			    int t = 0;
			    
				Matcher mTemp = FILENAME.matcher(imageFilename);
				if (!mTemp.matches()) {
					newImage = false;
					Logging.getLogger().info(
						"filename not as expected (NAME.EXT)"
					);
				}
				
				String name = mTemp.group(1);
				String ext = mTemp.group(2);

			    if (!(images.isEmpty()))
				    for (ImgDescr lTemp : images) {
				    	if (newImage
			    			&& lTemp.getName().equals(name))
				    	{
				    		if (!lTemp.getClipping().equals(clipping) || !lTemp.getScaling().equals(scaling))
				    			t++;
				    			// selbes Ursprungsbild, aber unterschiedl. Clipping/Scaling-Werte
				    			// --> in neuer Datei speichern!
				    		else {
				    			t = lTemp.getNumber();
				    			newImage = false;
				    		}
				    	}
				    }
			    
			    if (newImage) {
			    	
			    	//Bild d. Liste hinzufügen, um später auf doppelte Bilder zu prüfen
			    	images.add(new ImgDescr(name, ext, clipping, scaling, t));
			    
			    	//neuer Dateiname = alter Name + _Zähler
			    	imageFilename =
			    		t != 0
			    		? name + "_" + t + "." + ext
	    				: name + "." + ext
    				;
				    
			    	Logging.getLogger().info(
			    		"Writing image " + imageFilename + " to " + baseDir + "..."
		    		);
			    	
				    File outputFile =
						new File(imgDir, imageFilename);
				    boolean writeDirect = clipping == null || "".equals(clipping); // clipping-werte eigtl. immer vorhanden, wenn nicht beschnitten, dann 0,0,0,0
				    if (!writeDirect)
				    	writeDirect =
				    		!new ImageClipping().clipImage(config, docUrl, outputFile, clipping, scaling, Logging.getLogger());
				    		// ImageClipping decides if it should write this out (and does if true)
				    if (writeDirect) {
				    	try {
							InputStream in = FileType.openStream(docUrl);
							FileOutputStream out = new FileOutputStream(outputFile);
							
							int len;
							byte b[] = new byte[8192];
							while ((len = in.read(b)) != -1)
								out.write(b, 0, len);
							in.close();
							out.close();
				    	} catch (Exception ex) {
					    	Logging.getLogger().info("write error.");
				    	}
				     }
			    } else
			    	Logging.getLogger().info("same img existing - skipped");
			    
			    matcher.appendReplacement(
		    		buffy,
		    		"img src=\"" + subDir + "/" + imageFilename + "\""
	    		);

			    imgCounter++;
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		if (imgCounter > 1)
			Logging.getLogger().info("Finished processing all images.");
		
		matcher.appendTail(buffy);
		return buffy.toString();
	}


	public void saveAsHtml(File baseDir, String filename, Config config)  {
		
		String html = getAsHTML(config, baseDir);		
		
		if (config.isConvertCharacterEntities())
			html = HTMLUtil.convertCharacterEntities(html);
		
		html = ConverterUtils.convertEntities(html, config.leaveUmlauts());
		
		html = Cleaner.removeTags(html, "span");

		DiskUtils.savePageToDisk(new File(baseDir, filename), html, config.getEncoding());
		
		Logging.getLogger().info("file " + filename + " saved as html.");

	}

}
