package org.sockdrawer.util.openOffice;

import java.net.Socket;

/**
 * Static 'Helper' class - OpenOffice Server Utilities. <p/>
 * 
 * @author Paul Walk
 */
public class OpenOfficeServerUtility {

	public static boolean checkServerAvailability(String host, int port) {
		boolean res = false;
		try {
			Socket s = new Socket(host, port);
			res = true;
			s.close();
		} catch (Exception e) {
		}
		return res;
	}

	public static void runOpenOfficeServer(String serverExecutable,
			String host, int port, long delay, boolean runInvisibly)
			throws Exception {
		
		String execString = serverExecutable;
		if (runInvisibly)
			execString = execString + " -nofirststartwizard -headless";

		execString =
			execString +
			" -accept=socket,host=" + host +
			",port=" + port
			+ ";urp;StarOffice.Service";
                
                System.out.println(execString);
		Runtime.getRuntime().exec(execString);
		System.out.println("OOo server started");
		/*
		 * Wait for OpenOffice server to sort its life out. Kludgy, but cant
		 * find a better way to do this, as the process doesn't write anything
		 * to its output stream
		 */
		Thread.sleep(delay);
	}

}
