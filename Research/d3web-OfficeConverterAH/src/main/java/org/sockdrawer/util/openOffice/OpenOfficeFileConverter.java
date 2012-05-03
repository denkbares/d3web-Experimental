package org.sockdrawer.util.openOffice;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;

import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.XPropertySet;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.uri.ExternalUriReferenceTranslator;

/**
 * Class for utilising OpenOffice API to do file format conversions. The
 * OpenOffice server must be running and available to the application calling
 * this class's methods. Typically the server is run on the local machine with
 * this command:
 * <code>soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager"</code>
 * <p>
 * 
 * @author Paul Walk
 */
public class OpenOfficeFileConverter {

	private XComponentLoader xcomponentloader = null;
	private String connectionString = null;
	private String filterExtension = "";

	public URL convertFile(URL inputFileURL, File outputDir, String convertType, String outputExtension) throws Exception {
		if (connectionString == null)
			return null;
                
                XComponentContext xComponentContext =
			Bootstrap.createInitialComponentContext(null);
		
		XMultiComponentFactory xMultiComponentFactory =
			xComponentContext.getServiceManager();
		
		Object objectUrlResolver =
			xMultiComponentFactory.createInstanceWithContext(
				"com.sun.star.bridge.UnoUrlResolver",
				xComponentContext
			);
		
		XUnoUrlResolver xurlresolver =
			(XUnoUrlResolver) UnoRuntime
			.queryInterface(XUnoUrlResolver.class, objectUrlResolver);
		
		Object objectInitial = xurlresolver.resolve(this.connectionString);
		
		xMultiComponentFactory =
			(XMultiComponentFactory) UnoRuntime
			.queryInterface(XMultiComponentFactory.class, objectInitial);
		
		XPropertySet xpropertysetMultiComponentFactory =
			(XPropertySet) UnoRuntime
			.queryInterface(XPropertySet.class, xMultiComponentFactory);
		
		Object objectDefaultContext =
			xpropertysetMultiComponentFactory.getPropertyValue("DefaultContext");
		
		xComponentContext =
			(XComponentContext) UnoRuntime
			.queryInterface(XComponentContext.class, objectDefaultContext);
		
		this.xcomponentloader =
			(XComponentLoader) UnoRuntime
			.queryInterface(
				XComponentLoader.class,
				xMultiComponentFactory
				.createInstanceWithContext("com.sun.star.frame.Desktop", xComponentContext));
		
		String inputFileUrlString = inputFileURL.toExternalForm();
		
		PropertyValue[] openpropertyvalue = new PropertyValue[1];
		openpropertyvalue[0] = new PropertyValue();
		openpropertyvalue[0].Name = "Hidden";
		openpropertyvalue[0].Value = new Boolean(true);
		
		PropertyValue propertyvalue[] = new PropertyValue[2];
		propertyvalue[0] = new PropertyValue();
		propertyvalue[0].Name = "Overwrite";
		propertyvalue[0].Value = new Boolean(true);
		propertyvalue[1] = new PropertyValue();
		propertyvalue[1].Name = "FilterName";
		propertyvalue[1].Value = convertType;
		
		Object objectDocumentToStore = null;
		
		// HOTFIX: mschuhmann
		String path2 =
			ExternalUriReferenceTranslator
			.create(xComponentContext)
			.translateToInternal(inputFileUrlString);
		if (path2.length() == 0 && inputFileUrlString.length() != 0) {
			throw new RuntimeException();
		}

		objectDocumentToStore =
			this.xcomponentloader.loadComponentFromURL(path2, "_blank", 0, openpropertyvalue);

		XStorable xstorable =
			(XStorable) UnoRuntime
			.queryInterface(XStorable.class, objectDocumentToStore);
		
		String outputFileUrlString =
			new File(
				outputDir, getName(inputFileURL)
			).toURI().toURL().toExternalForm();
		int index = outputFileUrlString.lastIndexOf('.');
		if (index >= 0)
			outputFileUrlString = outputFileUrlString.substring(0, index + 1);
		outputFileUrlString = outputFileUrlString + outputExtension;
		
		xstorable.storeToURL(outputFileUrlString, propertyvalue);
		
		XComponent xcomponent =
			(XComponent) UnoRuntime
			.queryInterface(XComponent.class, xstorable);
		
		xcomponent.dispose();
		
		return new URL(outputFileUrlString);
	}

	public String getName(URL url) {
		int index = url.getFile().lastIndexOf('/');
		if (index >= 0)
			return url.getFile().substring(index + 1);
		return url.getFile();
	}

	/**
	 * Sets OpenOffice Server location details.
	 * 
	 * @param newHost
	 * @param newPort
	 */
	public void setOpenOfficeServerDetails(String newHost, int newPort) {
		this.connectionString = "uno:socket,host=" + newHost + ",port="
				+ newPort + ";urp;StarOffice.ServiceManager";
	}

	FilenameFilter fileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(filterExtension);
		}
	};
}
