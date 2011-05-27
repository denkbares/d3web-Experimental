package de.d3web.proket.d3web.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.file.records.io.SingleXMLSessionRepository;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.utils.GlobalSettings;

public class PersistenceD3webUtils {

	/**
	 * Saving of a case. Only the current timestamp is used as filename.
	 * 
	 * @created 09.03.2011
	 * @param folderPath Path to the folder, where cases should be stored.
	 */
	public static void saveCaseTimestampDefault(String folderPath, Session d3webSession) {

		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				d3webSession);
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);

		/* Assemble file name (maybe dependent on project) from here */
		// current date and timestamp added to filename for uniqueness
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");

		// assemble complete file name
		File file = new File(folderPath + "/" + sdf.format(now) + ".xml");

		try {
			sessionRepository.save(file);
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Saving of a case. Not only the current timestamp is used as filename, but
	 * moreover also the value of a given question.
	 * 
	 * @created 09.03.2011
	 * @param folderPath Path to the folder, where cases should be stored.
	 * @param questionName Name of the Question, the value of which is also
	 *        considered for assembling the case-filename.
	 */
	public static void saveCaseTimestampOneQuestionVal(String folderPath, String questionName,
			Session d3webSession) {

		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				d3webSession);
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);

		/* +++ Assemble file name from here +++ */

		// current date / timestamp for uniqueness
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");

		// Value of given question in the current session
		// TerminologyManager man = new
		// TerminologyManager(D3webConnector.getInstance().getKb());
		Question clinic = D3webConnector.getInstance().getKb().getManager().searchQuestion(
				questionName);
		Blackboard bb = d3webSession.getBlackboard();
		String clinicVal = bb.getValue(clinic).toString();

		// Final assembly
		File file = new File(folderPath + "/" + sdf.format(now) + "_" + clinicVal + ".xml");

		try {
			sessionRepository.save(file);
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Saving of a case. Project-specific (MEDIASTINITIS) - as a filename, the
	 * current timestamp and a user-entered filename are assembled to the
	 * complete filename; Moreover, the value of the question questionName is
	 * used as a storage-sub-folder. Parent folder is the folder "cases". If
	 * this sub-folder doesn't exist, it is newly created, otherwise the file is
	 * saved within the specified sub-folder.
	 * 
	 * @created 09.03.2011
	 * @param folderPath Path to the folder, where cases should be stored.
	 * @param questionName Name of the Question, the value of which is used to
	 *        search for or newly create a corresponding sub-folder
	 * @param filename Name of the file the user wants the case to be stored as.
	 */
	public static void saveCaseTimestampOneQuestionAndInput(String folderPath, String questionName,
			String filename, Session d3webSession) {

		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				d3webSession);
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);

		/* +++ Assemble file name from here +++ */

		// current date / timestamp for uniqueness
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");

		String clinicVal = "";
		File folder = null;
		File file = null;

		if (questionName != "") {
			// Value of given question in the current session
			// TerminologyManager man = new
			// TerminologyManager(D3webConnector.getInstance().getKb());
			Question clinic = D3webConnector.getInstance().getKb().getManager().searchQuestion(
					questionName);
			Blackboard bb = d3webSession.getBlackboard();
			clinicVal = bb.getValue(clinic).toString();
			folder = new File(folderPath + "/" + clinicVal + "/");

			if (filename.equals("autosave")) {
				if (folder.listFiles() != null && folder.listFiles().length != 0) {
					for (File f : folder.listFiles()) {
						if (f.getName().contains("autosave")) {
							f.delete();
						}
					}
				}
				file = new File(folderPath + "/" + clinicVal + "/" + sdf.format(now)
						+ "_UVautosaveUV.xml");
			}
			else {

				/* check whether file with that name already exists --> delet it */
				if (folder.listFiles() != null && folder.listFiles().length != 0) {
					for (File f : folder.listFiles()) {
						if (f.getName().contains(filename)) {
							f.delete();
						}
					}
				}
				// Final assembly
				file = new File(folderPath + "/" + clinicVal + "/" + sdf.format(now) + "_UV"
						+ filename + "UV.xml");
			}

		}
		else {

			folder = new File(folderPath + "/");

			if (filename.equals("autosave")) {
				if (folder.listFiles() != null && folder.listFiles().length != 0) {
					for (File f : folder.listFiles()) {
						if (f.getName().contains("autosave")) {
							f.delete();
						}
					}
				}
				file = new File(folderPath + "/" + sdf.format(now)
						+ "_UVautosaveUV.xml");
			}
			else {

				/* check whether file with that name already exists --> delet it */
				if (folder.listFiles() != null && folder.listFiles().length != 0) {
					for (File f : folder.listFiles()) {
						if (f.getName().contains(filename)) {
							f.delete();
						}
					}
				}
				// Final assembly
				file = new File(folderPath + "/" + sdf.format(now) + "_UV"
						+ filename + "UV.xml");
			}

		}

		try {
			sessionRepository.save(file);
			System.out.println("Saved case to file: " + file.getAbsolutePath());
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Loads a case-file (.xml) of given filename back into the application.
	 * 
	 * @created 09.03.2011
	 * @param filename Name of the file to be loaded.
	 */
	public static Session loadCase(String filename) {

		// folder with cases .xml files
		File folder = new File(GlobalSettings.getInstance().getCaseFolder());
		File fileToLoad = null;

		if (folder.listFiles() != null && folder.listFiles().length != 0) {
			for (File f : folder.listFiles()) {

				if (f.getName().contains(filename)) {
					fileToLoad = f;
				}
			}
		}

		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();

		Session session1 = null;
		try {

			sessionRepository.load(fileToLoad);

			Iterator<SessionRecord> iterator = sessionRepository.iterator();
			SessionRecord record1 = iterator.next();

			session1 =
					SessionConversionFactory.copyToSession(D3webConnector.getInstance().getKb(),
							record1);

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return session1;
	}

	/**
	 * Loads a case-file (.xml) of given filename back into the application.
	 * Currently: MEDIASTINITIS-specific. There, files are stored with the
	 * current timestamp and the clinical sign (e.g., WÜ), and a user-chosen
	 * filename. In the load-case menu, only the filenames are to be displayed,
	 * thus when actually loading the file back, the "real" file with timestamp
	 * etc needs to be retrieved first.
	 * 
	 * @created 09.03.2011
	 * @param filename Name of the file to be loaded.
	 */
	public static Session loadCaseFromUserFilename(String filename, String user) {

		// folder with cases .xml files
		File folder = new File(GlobalSettings.getInstance().getCaseFolder() + "/" + user);
		File fileToLoad = null;

		if (folder.listFiles() != null && folder.listFiles().length != 0) {
			for (File f : folder.listFiles()) {

				if (f.getName().contains(filename)) {
					fileToLoad = f;
				}
			}
		}

		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		Session session1 = null;
		try {

			sessionRepository.load(fileToLoad);

			Iterator<SessionRecord> iterator = sessionRepository.iterator();
			SessionRecord record1 = iterator.next();

			session1 =
					SessionConversionFactory.copyToSession(D3webConnector.getInstance().getKb(),
							record1);

		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return session1;
	}

	/**
	 * Retrieves a listing of cases that can be loaded in the dialog.
	 * 
	 * @created 12.03.2011
	 * @return String representation of the files that can be loaded in the form
	 *         of an options-list. (For to be included within the corresponding
	 *         FileSelect-StringTemplate.
	 */
	public static String getCaseList() {

		StringBuffer cases = new StringBuffer();
		File folder = new File(GlobalSettings.getInstance().getCaseFolder());

		if (folder.listFiles() != null && folder.listFiles().length != 0) {

			// ignore file(s) ending to .csv as this is the user config file
			File[] files = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return !pathname.getName().contains(".csv");
				}
			});

			ArrayList<String> sfn = new ArrayList<String>();

			for (File f : files) {

				// Split the complete filename String <TIMESTAMP><>
				// so we get only the user entered filename to be displayed

				if (!f.getName().contains("autosave")) {
					String[] fnparts = f.getName().split("UV");
					if (fnparts != null && fnparts.length == 3) {
						sfn.add(fnparts[1]);
					}
				}
			}

			/* add autosaved as first item always */
			cases.append("<option>");
			cases.append("autosave");
			cases.append("</option>");

			String[] sortedFilenames = new String[sfn.size()];
			sfn.toArray(sortedFilenames);

			Arrays.sort(sortedFilenames);

			for (String fn : sortedFilenames) {
				if (fn != null && fn != "") {
					cases.append("<option>");
					cases.append(fn);
					cases.append("</option>");
				}
			}
		}
		else {
			/* In case no files are found, just append empty string */
			cases.append("");
		}

		return cases.toString();
	}

	/**
	 * Retrieves a listing of cases that can be loaded in the dialog.
	 * 
	 * @created 12.03.2011
	 * @return String representation of the files that can be loaded in the form
	 *         of an options-list. (For to be included within the corresponding
	 *         FileSelect-StringTemplate.
	 */
	public static String getCaseListFromUserFilename(String user) {
		StringBuffer cases = new StringBuffer();
		File folder = new File(GlobalSettings.getInstance().getCaseFolder() + "/" + user);

		if (folder.listFiles() != null && folder.listFiles().length != 0) {

			// ignore file(s) ending to .csv as this is the user config file
			File[] files = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return !pathname.getName().contains(".csv");
				}
			});

			ArrayList<String> sfn = new ArrayList<String>();

			for (File f : files) {

				// Split the complete filename String <TIMESTAMP><>
				// so we get only the user entered filename to be displayed

				if (!f.getName().contains("autosave")) {
					String[] fnparts = f.getName().split("UV");
					if (fnparts != null && fnparts.length == 3) {
						sfn.add(fnparts[1]);
					}
				}
			}

			/* add autosaved as first item always */
			cases.append("<option>");
			cases.append("autosave");
			cases.append("</option>");

			String[] sortedFilenames = new String[sfn.size()];
			sfn.toArray(sortedFilenames);

			Arrays.sort(sortedFilenames);

			for (String fn : sortedFilenames) {
				if (fn != null && fn != "") {
					cases.append("<option>");
					cases.append(fn);
					cases.append("</option>");
				}
			}
		}
		else {
			/* In case no files are found, just append empty string */
			cases.append("");
		}

		return cases.toString();
	}

	public static String getCaseListFilterByID(final String idString) {
		StringBuffer cases = new StringBuffer();
		File folder = new File(GlobalSettings.getInstance().getCaseFolder());

		if (folder.listFiles() != null && folder.listFiles().length != 0) {

			// ignore file(s) ending to .csv as this is the user config file
			File[] files = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return !pathname.getName().contains(".csv") &&
							pathname.getName().contains(idString);
				}
			});

			ArrayList<String> sfn = new ArrayList<String>();

			for (File f : files) {

				// Split the complete filename String <TIMESTAMP><>
				// so we get only the user entered filename to be displayed

				if (!f.getName().contains("autosave")) {
					String[] fnparts = f.getName().split("UV");
					if (fnparts != null && fnparts.length == 3) {
						sfn.add(fnparts[1]);
					}
				}
			}

			/* add autosaved as first item always */
			cases.append("<option>");
			cases.append("autosave");
			cases.append("</option>");

			String[] sortedFilenames = new String[sfn.size()];
			sfn.toArray(sortedFilenames);

			Arrays.sort(sortedFilenames);

			for (String fn : sortedFilenames) {
				if (fn != null && fn != "") {
					cases.append("<option>");
					cases.append(fn);
					cases.append("</option>");
				}
			}
		}
		else {
			/* In case no files are found, just append empty string */
			cases.append("");
		}

		return cases.toString();
	}

	public static boolean existsCase(String fold, String userFilename, String subfolder, Session session) {

		String subfold = "";
		File folder = null;

		// Value of given question in the current session
		if (subfolder != "") {
			// TerminologyManager man = new
			// TerminologyManager(D3webConnector.getInstance().getKb());
			Question subfolderVal = D3webConnector.getInstance().getKb().getManager().searchQuestion(
					subfolder);
			Blackboard bb = session.getBlackboard();
			subfold = bb.getValue(subfolderVal).toString();
			folder = new File(fold + "/" + subfold);
		}
		else {
			folder = new File(fold);
		}

		// System.out.println(folder.getName());
		if (folder.listFiles() != null && folder.listFiles().length != 0) {

			// ignore file(s) ending to .csv as this is the user config file
			File[] files = folder.listFiles(new FileFilter() {

				@Override
				public boolean accept(File pathname) {
					return !pathname.getName().contains(".csv");
				}
			});

			for (File f : files) {
				if (f.getName().contains("UV" + userFilename + "UV")) {
					return true;
				}
			}
		}
		return false;
	}

	public static void saveCaseWithLoginNnameInstituteTimestamp(
			String folderPath, String filename, Session d3webSession) {
		// TODO Auto-generated method stub

		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				d3webSession);
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);

		/* +++ Assemble file name from here +++ */

		// current date / timestamp for uniqueness
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");

		File folder = new File(folderPath);
		File file = null;

		if (filename.equals("autosave")) {
			if (folder.listFiles() != null && folder.listFiles().length != 0) {
				for (File f : folder.listFiles()) {
					if (f.getName().contains("autosave")) {
						f.delete();
					}
				}
			}
			file = new File(folderPath + "/" + sdf.format(now)
						+ "_UVautosaveUV.xml");
		}
		else {

			/* check whether file with that name already exists --> delete it */
			if (folder.listFiles() != null && folder.listFiles().length != 0) {
				for (File f : folder.listFiles()) {
					if (f.getName().contains(filename)) {
						f.delete();
					}
				}
			}
			String[] idName = filename.split("&&&");

			// Final assembly
			file = new File(folderPath + "/" + sdf.format(now) + idName[0] +
					"_UV" + idName[1] + "UV.xml");
		}

		try {
			sessionRepository.save(file);
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
