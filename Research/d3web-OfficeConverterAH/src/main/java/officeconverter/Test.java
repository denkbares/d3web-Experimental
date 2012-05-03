package officeconverter;

import java.io.File;
import java.net.URL;

import org.sockdrawer.util.openOffice.OpenOfficeFileConverter;
import org.sockdrawer.util.openOffice.OpenOfficeServerUtility;

public class Test {

	public static void main(String[] args) {
		String test1 = "C:\\Users\\hoernlein\\Desktop\\x";
		
		try {
			File dir = new File(test1);
			File f = new File(dir, "xlsx.xlsx");
			URL u = f.toURI().toURL();
			convertDocument(u, dir);
			System.out.println("done");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
					"\"" + Config.getServiceProgramDir()
					+ File.separator
					+ "soffice" + "\"",
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
	
	private static URL convertDocument(URL msURL, File tempDir) throws Exception {
		boolean started = startOpenOfficeServer();
		if (!started) {
			throw new RuntimeException("could not start OpenOffice server");
		}

		File convertedDocumentsDir = tempDir;
		convertedDocumentsDir.mkdirs();

		// path to output dir for converted file
		OpenOfficeFileConverter converter = new OpenOfficeFileConverter();
		converter.setOpenOfficeServerDetails(Config.getServiceHost(), Config.getServicePort());

		String filter = "MS Excel 97";
		String filetype = "xls";

		URL url = null;
		try {
			url = converter.convertFile(msURL, convertedDocumentsDir, filter, filetype);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return url;

	}

}
