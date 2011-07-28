package de.d3web.proket.d3web.utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;
import de.d3web.file.records.io.SingleXMLSessionRepository;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.utils.GlobalSettings;

public class PersistenceD3webUtils {

	private static File getFile(String user, String filename) {
		return new File(GlobalSettings.getInstance().getCaseFolder() + File.separator
				+ (user != null && !user.isEmpty() ? user + File.separator : "")
				+ filename + ".xml");
	}

	public static boolean existsCase(String user, String filename) {
		return getFile(user, filename).exists();
	}

	public static void saveCase(String user,
			String filename, Session d3webSession) {

		new SaveThread(getFile(user, filename), d3webSession).start();
	}

	/**
	 * Loads a case-file (.xml) of given filename back into the application.
	 * 
	 * @created 09.03.2011
	 * @param filename Name of the file to be loaded.
	 */
	public static Session loadCase(String filename) {
		return loadUserCase(null, filename);
	}

	public static Session loadUserCase(String user, String filename) {

		File fileToLoad = getFile(user, filename);

		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		Session session = null;
		try {
			sessionRepository.load(fileToLoad);
			Iterator<SessionRecord> iterator = sessionRepository.iterator();
			SessionRecord record1 = iterator.next();
			session = SessionConversionFactory.copyToSession(D3webConnector.getInstance().getKb(),
							record1);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return session;
	}

	/**
	 * 
	 * @created 12.03.2011
	 * @return String representation of the files that can be loaded in the form
	 *         of an options-list. (For to be included within the corresponding
	 *         FileSelect-StringTemplate.
	 */
	public static String getCaseList() {
		return getUserCaseList(null);
	}

	/**
	 * 
	 * @created 12.03.2011
	 * @return String representation of the files that can be loaded in the form
	 *         of an options-list. (For to be included within the corresponding
	 *         FileSelect-StringTemplate.
	 */
	public static String getUserCaseList(String user) {
		StringBuffer cases = new StringBuffer();

		String folderPath = GlobalSettings.getInstance().getCaseFolder();
		if (user != null && !user.isEmpty()) folderPath += File.separator + user;
		File folder = new File(folderPath);

		if (folder.listFiles() != null && folder.listFiles().length > 0) {

			File[] files = folder.listFiles();

			/* add autosaved as first item always */
			cases.append("<option>");
			cases.append("autosave");
			cases.append("</option>");

			Arrays.sort(files);

			for (File f : files) {
				if (!f.getName().startsWith("autosave")) {
					cases.append("<option>");
					cases.append(f.getName().substring(0, f.getName().lastIndexOf(".")));
					cases.append("</option>");
				}
			}
		}

		return cases.toString();
	}

	private static class SaveThread extends Thread {

		private final File file;
		private final Session d3webSession;

		public SaveThread(File file, Session d3webSession) {
			this.file = file;
			this.d3webSession = d3webSession;
		}

		@Override
		public void run() {
			/* d3web related persistence setup */
			SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
					d3webSession);
			SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
			sessionRepository.add(sessionRecord);

			try {
				sessionRepository.save(file);
				System.out.println("Saved case to file: " + file.getAbsolutePath());
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
}
