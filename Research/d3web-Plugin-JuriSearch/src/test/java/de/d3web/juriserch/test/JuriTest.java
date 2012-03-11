package de.d3web.juriserch.test;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

import junit.framework.TestCase;
import de.d3web.core.io.PersistenceManager;
import de.d3web.core.io.progress.DummyProgressListener;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.manage.KnowledgeBaseUtils;
import de.d3web.core.session.Session;
import de.d3web.core.session.SessionFactory;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.jurisearch.JuriModel;
import de.d3web.jurisearch.JuriModelPersistenceHandler;
import de.d3web.jurisearch.JuriRule;
import de.d3web.plugin.test.InitPluginManager;

public class JuriTest extends TestCase {

	private final KnowledgeBase kb = KnowledgeBaseUtils.createKnowledgeBase();
	private JuriModel model;

	private final HashMap<QuestionOC, Value> map = new HashMap<QuestionOC, Value>();

	private QuestionOC f;
	private QuestionOC c1;
	private QuestionOC c2;

	private QuestionOC c21;
	private QuestionOC c22;

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
		JuriModelPersistenceHandler xmph = new JuriModelPersistenceHandler();
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
		Collection<JuriModel> col =
				k2.getAllKnowledgeSlicesFor(JuriModel.KNOWLEDGE_KIND);
		for (JuriModel jurimodel : col) {
			assertEquals(model.getRules(), jurimodel.getRules());
		}
	}

	/**
	 * testing some rule inferences
	 * 
	 * @created 09.03.2012
	 */
	public void testRules() {
		Session s = SessionFactory.createSession(kb);
		changeFact(s, c1, JuriRule.YES_VALUE);
		checkMap(s);
		changeFact(s, c21, JuriRule.YES_VALUE);
		checkMap(s);
		changeFact(s, c22, JuriRule.YES_VALUE);
		checkMap(s);
		assertEquals(JuriRule.YES_VALUE, s.getBlackboard().getValue(c2));
		changeFact(s, f, JuriRule.NO_VALUE);
		checkMap(s);
	}

	/**
	 * 
	 * @created 09.03.2012
	 */
	private void checkMap(Session s) {
		for (QuestionOC o : map.keySet()) {
			assertEquals(map.get(o), s.getBlackboard().getValue(o));
		}
	}

	/**
	 * creates kb and adds rules
	 * 
	 * @created 09.03.2012
	 */
	private void createKnowledgebase() {
		f = new QuestionOC(kb, "Ist das Wetter heute gut?");
		c1 = new QuestionOC(kb, "Ist es warm?");
		c2 = new QuestionOC(kb, "Ist es trocken?");

		c21 = new QuestionOC(kb, "Regnet es nicht?");
		c22 = new QuestionOC(kb, "Schneit es nicht?");

		model = new JuriModel();

		addAlternatives(f);
		addAlternatives(c1);
		addAlternatives(c2);
		addAlternatives(c21);
		addAlternatives(c22);

		JuriRule jurirule = new JuriRule();
		jurirule.setFather(f);
		jurirule.addChild(c1);
		jurirule.addChild(c2);

		JuriRule jurirule2 = new JuriRule();
		jurirule2.setFather(c2);
		jurirule2.addChild(c21);
		jurirule2.addChild(c22);

		model.addRule(jurirule);
		model.addRule(jurirule2);
		kb.getKnowledgeStore().addKnowledge(JuriModel.KNOWLEDGE_KIND, model);

	}

	/**
	 * set Value v for Question o in Session s
	 * 
	 * @created 09.03.2012
	 * @param s
	 * @param o
	 * @param v
	 */
	private void changeFact(Session s, QuestionOC o, Value v) {
		map.put(o, v);
		// System.out.println("Ändere Fakt " + o.getName() + " zu " +
		// v.toString());
		Fact f = FactFactory.createUserEnteredFact(o, v);
		s.getBlackboard().addValueFact(f);
	}

	private void addAlternatives(QuestionOC q) {
		q.addAlternative(JuriRule.YES);
		q.addAlternative(JuriRule.NO);
		q.addAlternative(JuriRule.MAYBE);
	}
}
