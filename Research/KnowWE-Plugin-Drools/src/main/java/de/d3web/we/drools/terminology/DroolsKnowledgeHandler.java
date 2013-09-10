package de.d3web.we.drools.terminology;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import de.d3web.plugin.PluginManager;
import de.d3web.we.drools.action.utils.DroolsUtils;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.event.Event;
import de.knowwe.core.event.EventListener;
import de.knowwe.core.kdom.Article;
import de.knowwe.event.ArticleCreatedEvent;
import de.knowwe.event.FullParseEvent;
import de.knowwe.logging.Logging;
import de.knowwe.plugin.Plugins;

public class DroolsKnowledgeHandler implements EventListener {

	private static DroolsKnowledgeHandler instance = null;

	public static DroolsKnowledgeHandler getInstance() {
		if (instance == null) {
			instance = (DroolsKnowledgeHandler) PluginManager.getInstance().getExtension(
					Plugins.EXTENDED_PLUGIN_ID,
					Plugins.EXTENDED_POINT_EventListener,
					"KnowWE-Plugin-Drools",
					DroolsKnowledgeHandler.class.getSimpleName()).getSingleton();
		}
		return instance;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	private final Map<String, Map<String, Object>> factsStores = new HashMap<String, Map<String, Object>>();

	private final Map<String, KnowledgeBuilder> knowledgeBuilders = new HashMap<String, KnowledgeBuilder>();

	private final Map<String, KnowledgeBase> knowledgeBases = new HashMap<String, KnowledgeBase>();

	private final Map<String, Map<String, StatefulKnowledgeSession>> knowledgeSessions =
			new HashMap<String, Map<String, StatefulKnowledgeSession>>();

	@Override
	public Collection<Class<? extends Event>> getEvents() {
		ArrayList<Class<? extends Event>> events = new ArrayList<Class<? extends Event>>(2);
		events.add(ArticleCreatedEvent.class);
		events.add(FullParseEvent.class);
		return events;
	}

	public void clearKnowledgeForArticle(String title) {

	}

	@Override
	public void notify(Event event) {
		if (event instanceof ArticleCreatedEvent) {
			String title = ((ArticleCreatedEvent) event).getArticle().getTitle();
			Map<String, StatefulKnowledgeSession> sessionsForArticle = knowledgeSessions.remove(title);
			if (sessionsForArticle != null) {
				for (StatefulKnowledgeSession session : sessionsForArticle.values()) {
					session.dispose();
				}

			}
		}
		else if (event instanceof FullParseEvent) {
			String title = ((FullParseEvent) event).getArticle().getTitle();
			knowledgeBuilders.remove(title);
			knowledgeBases.remove(title);
			factsStores.remove(title);
		}
	}

	public Map<String, Object> getFactsStore(String title) {
		// try to load factsStore
		Map<String, Object> factsStore = factsStores.get(title);

		// if factsStore does not exist, create it and store it
		if (factsStore == null) {
			factsStore = new HashMap<String, Object>();
			factsStores.put(title, factsStore);
		}
		return factsStore;
	}

	/**
	 * Loads the stored KnowledgeSession, if no one exists a new one is created
	 *
	 * @param article the currently processed article
	 * @return the stored KnowledgeSession or a new one
	 */
	public StatefulKnowledgeSession getSession(UserActionContext context) {

		String user = context.getUserName();
		// Load the Article
		Article article = DroolsUtils.loadArticle(context);
		String title = article.getTitle();

		Map<String, StatefulKnowledgeSession> sessionsForArticle = knowledgeSessions.get(title);
		if (sessionsForArticle == null) {
			sessionsForArticle = new HashMap<String, StatefulKnowledgeSession>();
			knowledgeSessions.put(title, sessionsForArticle);
		}
		StatefulKnowledgeSession session = sessionsForArticle.get(user);
		if (session != null) {
			return session;
		}


		KnowledgeBuilder builder = getKnowledgeBuilder(title);


		// Create new Session
		if (!builder.hasErrors()) {
			KnowledgeBase kb = getKnowledgeBase(title);

			kb.addKnowledgePackages(builder.getKnowledgePackages());

			session = kb.newStatefulKnowledgeSession();

			addFacts(article, session);
			addGlobals(session);

			sessionsForArticle.put(user, session);
			return session;
		}

		Logging.getInstance().severe(
				"KnowledgeBuilder has errors, unable to create KnowledgeBase. Facts won't be created!");
		return null;
	}

	public void disposeSession(UserActionContext context) {
		String title = context.getParameter("title");
		String user = context.getUserName();

		Map<String, StatefulKnowledgeSession> sessionsForArticle = knowledgeSessions.get(title);
		if (sessionsForArticle != null) {
			StatefulKnowledgeSession session = sessionsForArticle.remove(user);
			if (session != null) {
				session.dispose();
			}
		}
	}

	public KnowledgeBuilder getKnowledgeBuilder(String title) {
		KnowledgeBuilder knowledgeBuilder = this.knowledgeBuilders.get(title);
		if (knowledgeBuilder == null) {
			knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			this.knowledgeBuilders.put(title, knowledgeBuilder);
		}
		return knowledgeBuilder;
	}

	public KnowledgeBase getKnowledgeBase(String title) {
		KnowledgeBase knowledgeBase = this.knowledgeBases.get(title);
		if (knowledgeBase == null) {
			knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
			this.knowledgeBases.put(title, knowledgeBase);
		}
		return knowledgeBase;
	}

	/**
	 * Loads all Facts from the FactsStore and inserts it into the current
	 * session
	 *
	 * @param article the current article
	 */
	private void addFacts(Article article, StatefulKnowledgeSession session) {

		// Load all facts into the session
		for (Object o : DroolsKnowledgeHandler.getInstance().getFactsStore(article.getTitle()).values()) {
			if (o instanceof AbstractFact) session.insert(((AbstractFact) o).copy());
			else // Values don't need to be copied because they are
			// immutable
			session.insert(o);
		}
	}

	/**
	 * Sets the global Variables in the KnowledgeSession
	 */
	private void addGlobals(StatefulKnowledgeSession session) {
		session.setGlobal("P1", SolutionScore.P1);
		session.setGlobal("P2", SolutionScore.P2);
		session.setGlobal("P3", SolutionScore.P3);
		session.setGlobal("P4", SolutionScore.P4);
		session.setGlobal("P5", SolutionScore.P5);
		session.setGlobal("P6", SolutionScore.P6);
		session.setGlobal("P7", SolutionScore.P7);
		session.setGlobal("N1", SolutionScore.N1);
		session.setGlobal("N2", SolutionScore.N2);
		session.setGlobal("N3", SolutionScore.N3);
		session.setGlobal("N4", SolutionScore.N4);
		session.setGlobal("N5", SolutionScore.N5);
		session.setGlobal("N6", SolutionScore.N6);
		session.setGlobal("N7", SolutionScore.N7);
	}

}
