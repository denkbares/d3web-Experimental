package de.d3web.proket.d3web.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import de.d3web.core.inference.PSMethod;
import de.d3web.core.inference.PSMethod.Type;
import de.d3web.core.knowledge.InfoStoreUtil;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.records.FactRecord;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.DefaultSession;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.protocol.ProtocolEntry;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.file.records.io.SingleXMLSessionRepository;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.utils.GlobalSettings;

public class PersistenceD3webUtils {

	public static final String AUTOSAVE = "autosave";

	private static File getFile(String user, String filename) {
		String path = GlobalSettings.getInstance().getCaseFolder() + File.separator
				+ (user != null && !user.isEmpty() ? user + File.separator : "")
				+ filename + ".xml";
		return new File(path);
	}

	public static boolean existsCase(String user, String filename) {
		return getFile(user, filename).exists();
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
                
		return loadUserCase(user, fileToLoad);
	}

	public static Session loadUserCase(String user, File fileToLoad) {

		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		Session session = null;
		try {
			sessionRepository.load(fileToLoad);
			Iterator<SessionRecord> iterator = sessionRepository.iterator();
                        SessionRecord record1 = iterator.next();
                       
                        session = copyToSession(D3webConnector.getInstance().getKb(),
							record1);
		}
		catch (IOException e) {
			Logger.getLogger(PersistenceD3webUtils.class.getSimpleName()).warning(
					"'" + fileToLoad.getName() + "' for user '" + user + "' could not be loaded.");
			// e.printStackTrace();
		}
		return session;
	}

	public static void deleteUserCase(String user, String filename) {
		File fileToDelete = getFile(user, filename);
		fileToDelete.delete();
	}

	public static List<File> getCaseList(String user) {
		String folderPath = GlobalSettings.getInstance().getCaseFolder();
		if (user != null && !user.isEmpty()) folderPath += File.separator + user;
		File folder = new File(folderPath);
		File[] files = folder.listFiles();
		if (files == null) return null;
		List<File> filesList = new ArrayList<File>(files.length);
		for (File file : files) {
			if (file.isFile() && file.getName().endsWith(".xml")) {
				filesList.add(file);
			}
		}
		return filesList;
	}

	public static void saveCase(String user,
			String filename, Session d3webSession) {
		File file = getFile(user, filename);
		if (filename.equals("autosave")) {
			new SaveThread(file, d3webSession).start();
		}
		else {
			saveCase(file, d3webSession);
			file.setLastModified(System.currentTimeMillis());
		}
	}

	private static void saveCase(File file, Session session) {
		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				session);
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);

		try {
			sessionRepository.save(file);
			//System.out.println("SAVED CASE to file: " + file.getCanonicalPath());
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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
			saveCase(file, d3webSession);
		}
	}

	/**
	 * This method is a copy of
	 * {@link SessionConversionFactory#copyToSession(KnowledgeBase, SessionRecord)}
	 * 
	 * It allows old cases to be loaded with a {@link KnowledgeBase}, that has
	 * different {@link TerminologyObject}s.
	 */
	/**
	 * Converts a SessionRecord to a Session
	 * 
	 * @created 05.08.2011
	 * @param knowledgeBase {@link KnowledgeBase}
	 * @param source {@link SessionRecord}
	 * @return {@link Session}
	 * @throws IOException
	 */
	public static Session copyToSession(KnowledgeBase knowledgeBase, SessionRecord source) throws IOException {
		//DefaultSession target = SessionFactory.createSession(source.getId(),
		//		knowledgeBase, source.getCreationDate());
                
                // in case of iTree(legal) dialogs, we need to create Sessions otherwise
                DefaultSession target =
                    D3webUtils.createSession(knowledgeBase, D3webConnector.getInstance().getDialogStrat());
                
		target.setName(source.getName());
		InfoStoreUtil.copyEntries(source.getInfoStore(), target.getInfoStore());

		// Search psmethods of session
		Map<String, PSMethod> psMethods = new HashMap<String, PSMethod>();
		for (PSMethod psm : target.getPSMethods()) {
			psMethods.put(psm.getClass().getName(), psm);
		}
		target.getPropagationManager().openPropagation();
		try {
			List<Fact> valueFacts = getFacts(knowledgeBase, source.getValueFacts(), psMethods);
			List<Fact> interviewFacts =
					getFacts(knowledgeBase, source.getInterviewFacts(), psMethods);
			for (Fact fact : valueFacts) {
                            System.out.println(fact);
				target.getBlackboard().addValueFact(fact);
			}
			for (Fact fact : interviewFacts) {
                            System.out.println(fact);
				target.getBlackboard().addInterviewFact(fact);
			}
		}
		finally {
			target.getPropagationManager().commitPropagation();
		}

		// restore protocol from source
		target.getProtocol().clear();
		for (ProtocolEntry entry : source.getProtocol().getProtocolHistory()) {
			target.getProtocol().addEntry(entry);
		}

                System.out.println(target.getBlackboard().getValuedQuestions());
		// this must be the last operation to overwrite all touches within
		// propagation
		target.touch(source.getLastChangeDate());
		return target;
	}

	/**
	 * This method is a slightly changed copy of {@link
	 * SessionConversionFactory#getFacts(KnowledgeBase, List<FactRecord>,
	 * Map<String, PSMethod>, List<Fact>)}
	 * 
	 * It allows old cases to be loaded with a {@link KnowledgeBase}, that has
	 * different {@link TerminologyObject}s, because instead of throwing an
	 * exception, a warning is logged in case the Value does not fit.
	 */
	private static List<Fact> getFacts(KnowledgeBase kb, List<FactRecord> factRecords, Map<String, PSMethod> psMethods) throws IOException {
		List<Fact> valueFacts = new LinkedList<Fact>();
		for (FactRecord factRecord : factRecords) {
                    System.out.println(factRecord.getObjectName() + " " + factRecord.getPsm());
			String psm = factRecord.getPsm();
			if (psm != null) {
				PSMethod psMethod = psMethods.get(psm);
                                System.out.println(psMethod);
				if (psMethod != null) {
                                    if (psMethod.hasType(Type.source)) {
						Value value = factRecord.getValue();
                                                System.out.println("V: " + value);
						TerminologyObject object = kb.getManager().search(
								factRecord.getObjectName());
						if (object == null) {
							Logger.getLogger(PersistenceD3webUtils.class.getSimpleName()).info(
									"Object '" + factRecord.getObjectName()
											+ "' not found in knowledge base. Discarding value.");
						}
						else if (object instanceof QuestionMC && value instanceof ChoiceValue) {
							Logger.getLogger(PersistenceD3webUtils.class.getSimpleName()).info(
									"QuestionMC '"
											+ factRecord.getObjectName()
											+ "' expects a MultipleChoiceValue. Discarding ChoiceValue.");
						}
						else if (object instanceof QuestionOC
								&& value instanceof MultipleChoiceValue) {
							Logger.getLogger(PersistenceD3webUtils.class.getSimpleName()).info(
									"QuestionOC '"
											+ factRecord.getObjectName()
											+ "' expects a ChoiceValue. Discarding MultipleChoiceValue.");
						}
						else {
							valueFacts.add(new DefaultFact(object, value, psMethod, psMethod));
						}
					}
				}
				else {
					throw new IOException("Problemsolver " + psm
							+ " not found in Session.");
				}
			}
		}
		return valueFacts;
	}

}
