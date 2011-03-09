package de.d3web.proket.d3web.output.persistence;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.records.SessionConversionFactory;
import de.d3web.core.records.SessionRecord;
import de.d3web.core.session.Session;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.file.records.io.SingleXMLSessionRepository;
import de.d3web.proket.d3web.input.D3webConnector;

public class PersistenceD3webUtils {

	/**
	 * Saving of a case. Only the current timestamp is used as filename.
	 * 
	 * @created 09.03.2011
	 * @param folderPath Path to the folder, where cases should be stored.
	 */
	public static void saveCaseTimestampDefault(String folderPath) {

		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				D3webConnector.getInstance().getSession());
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
	public static void saveCaseTimestampOneQuestionVal(String folderPath, String questionName) {

		/* d3web related persistence setup */
		SessionRecord sessionRecord = SessionConversionFactory.copyToSessionRecord(
				D3webConnector.getInstance().getSession());
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		sessionRepository.add(sessionRecord);

		/* +++ Assemble file name from here +++ */

		// current date / timestamp for uniqueness
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");

		// Value of given question in the current session
		Question clinic = (Question) KnowledgeBaseUtils.
				findTerminologyObjectByName(questionName, D3webConnector.getInstance().getKb());
		Blackboard bb = D3webConnector.getInstance().getSession().getBlackboard();
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
	 * Loads a case-file (.xml) of given filename back into the application.
	 * 
	 * @created 09.03.2011
	 * @param filename Name of the file to be loaded.
	 */
	public static void loadCase(String filename) {

		File file = new File(filename);
		SingleXMLSessionRepository sessionRepository = new SingleXMLSessionRepository();
		
		try {

			sessionRepository.load(file);

			Iterator<SessionRecord> iterator = sessionRepository.iterator();
			SessionRecord record1 = iterator.next();

			Session session1 =
					SessionConversionFactory.copyToSession(D3webConnector.getInstance().getKb(),
							record1);

			D3webConnector.getInstance().setSession(session1);
			System.out.println(session1.getBlackboard().getValuedObjects());

			// for (TerminologyObject to :
			// D3webConnector.getInstance().getKb().getManager().getAllTerminologyObjects())
			// {
			// if (to instanceof Question) {
			// Value val = session1.getBlackboard().getValue((ValueObject) to);
			// System.out.println(to.getName() + ": " + val.toString());
			// }

			// }
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
