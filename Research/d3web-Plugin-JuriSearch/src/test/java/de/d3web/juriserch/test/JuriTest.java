package de.d3web.juriserch.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import junit.framework.TestCase;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.jurisearch.JuriRule;
import de.d3web.jurisearch.JuriRulePersistenceHandler;
import de.d3web.plugin.test.InitPluginManager;

public class JuriTest extends TestCase {

	KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
	JuriRule r;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		try {
			InitPluginManager.init();
		}
		catch (IOException e1) {
			assertTrue("Error initialising plugin framework", false);
		}
		createKnowledgebase();
	}

	/**
	 * rein funktionaler test, -> keine exceptions, passt keinerlei semantische
	 * korrektur
	 * 
	 * @throws IOException
	 */
	public void testXMLWriter() throws IOException {
		JuriRulePersistenceHandler xmph = new JuriRulePersistenceHandler();
		xmph.write(kb, new OutputStream() {

			@Override
			public void write(int b) throws IOException {
				// TODO Auto-generated method stub

			}
		}, new DummyProgressListener());

	}

	/**
	 * we're testing for the correct readin of an knowledgebase with an xclmodel
	 * via the xclmodelproperties
	 * 
	 * @throws Exception
	 */
	public void testPersistence() throws Exception {
		File file = new File("target/kbs");
		if (!file.isDirectory()) {
			file.mkdir();
		}
		PersistenceManager pm = PersistenceManager.getInstance();
		File file2 = new File("target/kbs/test.jar");
		pm.save(kb, file2);
		KnowledgeBase k2 = pm.load(file2);
		Collection<JuriRule> col =
				k2.getAllKnowledgeSlicesFor(JuriRule.KNOWLEDGE_KIND);
		for (JuriRule rule : col) {
			assertEquals(r, rule);

		}

	}

	private void createKnowledgebase() {
		QuestionOC f = new QuestionOC(kb, "Ist das Wetter heute gut?");
		QuestionOC c1 = new QuestionOC(kb, "Ist es warm?");
		QuestionOC c2 = new QuestionOC(kb, "Ist es trocken?");

		f.addAlternative(JuriRule.YES);
		f.addAlternative(JuriRule.NO);
		f.addAlternative(JuriRule.MAYBE);
		c1.addAlternative(JuriRule.YES);
		c1.addAlternative(JuriRule.NO);
		c1.addAlternative(JuriRule.MAYBE);
		c2.addAlternative(JuriRule.YES);
		c2.addAlternative(JuriRule.NO);
		c2.addAlternative(JuriRule.MAYBE);

		r = new JuriRule();
		r.setFather(f);
		r.addChild(c1);
		r.addChild(c2);

		kb.getKnowledgeStore().addKnowledge(JuriRule.KNOWLEDGE_KIND, r);

		Session s = SessionFactory.createSession(kb);
		changeFact(s, c1, 1);
		changeFact(s, c2, 1);
		// out(s);
		changeFact(s, c2, 0);
		// out(s);
		changeFact(s, f, 1);
		// out(s);

		// for (ProtocolEntry entry :
		// session.getProtocol().getProtocolHistory()) {
		// System.out.println(entry);
		// }
	}

	private void changeFact(Session s, TerminologyObject o, int i) {
		Value v = JuriRule.NO_VALUE;
		if (i == 1) {
			v = JuriRule.YES_VALUE;
		}
		else if (i == 2) {
			v = JuriRule.MAYBE_VALUE;
		}
		System.out.println("Ändere Fakt " + o.getName() + " zu " + v.toString());
		Fact f = FactFactory.createUserEnteredFact(o, v);
		s.getBlackboard().addValueFact(f);
	}

	private void out(Session session) {
		System.out.println("Lese beantwortete Fragen: ");
		for (Question q : session.getBlackboard().getAnsweredQuestions()) {
			System.out.println(q.getName() + " = " + session.getBlackboard().getValue(q));
		}
		System.out.println();
	}
}
