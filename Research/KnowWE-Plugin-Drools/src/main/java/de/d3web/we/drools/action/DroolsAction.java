/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.d3web.we.drools.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.json.simple.JSONValue;

import com.google.gson.JsonObject;

import de.d3web.we.drools.action.utils.DroolsUtils;
import de.d3web.we.drools.action.utils.ResponseType;
import de.d3web.we.drools.kdom.DroolsSessionRootType;
import de.d3web.we.drools.kdom.DroolsSessionType;
import de.d3web.we.drools.terminology.AbstractFact;
import de.d3web.we.drools.terminology.ChoiceInput;
import de.d3web.we.drools.terminology.DroolsKnowledgeHandler;
import de.d3web.we.drools.terminology.NumInput;
import de.d3web.we.drools.terminology.SolutionInput;
import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.utils.KnowWEUtils;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.core.utils.StringFragment;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.logging.Logging;

/**
 * The DroolsAction handles most command entered in the command line.
 * 
 * @author Sebastian Furth, Florian Ziegler, Alex Legler
 */
public class DroolsAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		Logging.getInstance().info("Command: " + context.getParameter("command"));

		StatefulKnowledgeSession session;
		try {
			// create session if necessary
			session = DroolsKnowledgeHandler.getInstance().getSession(context);

			if (session == null)
				throw new RuntimeException(
						"INTERNAL ERROR: Session is null where it shouldn't be.");
		}
		catch (Exception e) {
			returnMessage(context, "Unable to load KnowledgeSession: " + e.toString(),
					ResponseType.ERROR);
			Logging.getInstance().severe(
					"While loading the KnowledgeSection: " + e.toString());
			e.printStackTrace();
			return;
		}

		// Extract action-type and execute it
		String actionType = extractActionType(context.getParameter("command"));
		if (actionType.equalsIgnoreCase("set")) doSetAction(context, session);
		else if (actionType.equalsIgnoreCase("get")) doGetAction(context, session);
		else if (actionType.equals("fire")) returnMessage(context, "Fired "
				+ session.fireAllRules() + " rule(s)", ResponseType.OK);
		else if (actionType.equals("load")) doLoadAction(context);
		else if (actionType.equals("store")) doStoreAction(context);
		else if (actionType.equals("reset")) {
			// dispose frees the resources of the previous session
			DroolsKnowledgeHandler.getInstance().disposeSession(context);
			returnMessage(context, "Started new KnowledgeSession", ResponseType.OK);
		}
		else returnMessage(context, "Unknown command : \"" + actionType + "\"",
				ResponseType.ERROR);
	}

	/**
	 * Generates a Message containing the status of the specified object.
	 * 
	 * @param session
	 *            the current KnowledgeSession
	 * @param context
	 *            the current ActionContext
	 * @throws IOException
	 *             only thrown if something went wrong while writing to
	 *             KnowWE.jsp
	 */
	private void doGetAction(UserActionContext context, StatefulKnowledgeSession session) throws IOException {
		if (!checkCommandSyntax("get\\s+(.*?)", context)) {
			returnMessage(context, "Syntax error", ResponseType.ERROR);
			return;
		}

		AbstractFact fact = loadFact(session, context);

		if (fact != null) returnMessage(context, fact.getStatusText(), ResponseType.OK);
		else returnMessage(context, "Fact '"
				+ extractFactName(context.getParameter("command")) + "' not found!",
				ResponseType.ERROR);
	}

	/**
	 * Sets the value of a specified Fact and generates a Message containing the
	 * new status of the fact.
	 * 
	 * @param session
	 *            the current KnowledgeSession
	 * @param context
	 *            the current ActionContext
	 * @throws IOException
	 *             only thrown if something went wrong while writing to
	 *             KnowWE.jsp
	 */
	private void doSetAction(UserActionContext context, StatefulKnowledgeSession session) throws IOException {
		if (!checkCommandSyntax("set\\s+(.*?)\\s*=\\s*(.*?)", context)) {
			returnMessage(context, "Syntax error", ResponseType.ERROR);
			return;
		}

		AbstractFact fact = loadFact(session, context);

		if (fact instanceof ChoiceInput) doSetValueChoice(context, session,
				(ChoiceInput) fact);
		else if (fact instanceof SolutionInput) returnMessage(context,
				"SolutionInputs can't be modified!", ResponseType.ERROR);
		else if (fact instanceof NumInput) doSetValueNum(context, session,
				(NumInput) fact);
		else if (fact == null) returnMessage(context, "Fact not found!",
				ResponseType.ERROR);
		else returnMessage(context, "Unknown Fact Type - no value was set!",
				ResponseType.ERROR);
	}

	/**
	 * Sets the value of a ChoiceInput fact.
	 * 
	 * @param context
	 *            the current ActionContext
	 * @param session
	 *            the current KnowledgeSession
	 * @param fact
	 *            the current fact
	 * @throws IOException
	 *             only thrown if something went wrong while writing to
	 *             KnowWE.jsp
	 */
	private void doSetValueChoice(UserActionContext context, StatefulKnowledgeSession session, ChoiceInput fact) throws IOException {
		// Get the corresponding FactHandle
		FactHandle factHandle = session.getFactHandle(fact);
		if (factHandle == null) returnMessage(context,
				"Unable to load FactHandle for Fact \"" + fact.getName() + "\"",
				ResponseType.ERROR);
		else {
			// Modify Value
			fact.setValue(extractValue(context.getParameter("command")));
			// Update fact
			session.update(factHandle, fact);
			// create returnMessage
			returnMessage(context, fact.getStatusText(), ResponseType.OK);
		}
	}

	/**
	 * Sets the value of a NumInput fact.
	 * 
	 * @param context
	 *            the current ActionContext
	 * @param session
	 *            the current KnowledgeSession
	 * @param fact
	 *            the current fact
	 * @throws IOException
	 *             only thrown if something went wrong while writing to
	 *             KnowWE.jsp
	 */
	private void doSetValueNum(UserActionContext context, StatefulKnowledgeSession session, NumInput fact) throws IOException {
		// Get the corresponding FactHandle
		FactHandle factHandle = session.getFactHandle(fact);
		if (factHandle == null) returnMessage(context,
				"Unable to load FactHandle for Fact \"" + fact.getName() + "\"",
				ResponseType.OK);
		else {
			// Modify Value
			fact.setValue(Double.valueOf(extractValue(context.getParameter("command"))));
			// Update fact
			session.update(factHandle, fact);
			// create returnMessage
			returnMessage(context, fact.getStatusText(), ResponseType.OK);
		}
	}

	/**
	 * Loads the session specified by the name in the command. All commands are
	 * executed immediately
	 * 
	 * @param context
	 *            the current ActionContext
	 * @throws IOException
	 *             only thrown if something went wrong while writing to
	 *             KnowWE.jsp
	 */
	private void doLoadAction(UserActionContext context) throws IOException {

		// Get the name of the desired session
		String desiredSessionName = extractSessionName(context.getParameter("command"));

		Section<DroolsSessionType> session = findDroolsSessionSection(context,
				desiredSessionName);

		if (session != null) {
			processSession(context, session.getOriginalText());
			return;
		}

		returnMessage(context, "Session \"" + desiredSessionName + "\" was not found.",
				ResponseType.ERROR);
	}

	private void doStoreAction(UserActionContext context) throws IOException {
		KnowWEArticle article = DroolsUtils.loadArticle(context);
		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(
				article.getWeb());
		String desiredSessionName = extractSessionName(context.getParameter("command"));
		Section<DroolsSessionType> session = findDroolsSessionSection(context,
				desiredSessionName);
		if (session != null) {
			returnMessage(context,
					"Session with same name already exists. Session wasn't saved.",
					ResponseType.ERROR);
			return;
		}

		String sessionContent = context.getParameter("sessionContent");
		if (sessionContent != null) {
			String[] commands = sessionContent.split("DROOLSLINEBREAK");

			// Create the content
			StringBuilder sessionText = new StringBuilder();
			sessionText.append("%%DroolsSession\n");
			for (int i = commands.length - 1; i >= 0; i--)
				sessionText.append(commands[i] + "\n");
			sessionText.append("\n@Name: ");
			sessionText.append(desiredSessionName);
			sessionText.append("\n%");

			// Save the article
			Map<String, String> map = new HashMap<String, String>();
			map.put(article.getSection().getID(), article.getSection().getOriginalText()
					+ sessionText.toString());
			mgr.replaceKDOMNodesSaveAndBuild(context, article.getTitle(),
					map);
			returnMessage(context, "Session was successfully saved.", ResponseType.OK);
			return;
		}

		returnMessage(context, "There were no commands to save.", ResponseType.ERROR);
		return;
	}

	private Section<DroolsSessionType> findDroolsSessionSection(UserActionContext context,
			String desiredSessionName) {

		// load the article
		KnowWEArticle article = DroolsUtils.loadArticle(context);

		if (article != null) {

			// Get the RootType sections
			List<Section<DroolsSessionRootType>> rootTypes = new ArrayList<Section<DroolsSessionRootType>>();
			Sections.findSuccessorsOfType(article.getSection(),
					DroolsSessionRootType.class,
					rootTypes);

			// Search for the correct session
			for (Section<DroolsSessionRootType> rootType : rootTypes) {
				String sessionName = DefaultMarkupType.getAnnotation(rootType, "Name");
				if (desiredSessionName != null && sessionName.equals(desiredSessionName)) {
					Section<DroolsSessionType> session = Sections.findSuccessor(rootType,
							DroolsSessionType.class);
					return session;
				}
			}
		}
		return null;
	}

	/**
	 * Extracts the name of the session from the command
	 * 
	 * @param text
	 *            the command
	 * @return the name of the session
	 */
	private String extractSessionName(String text) {
		Pattern p = Pattern.compile("^(load|store)\\s*(.+?)\\s*");
		Matcher m = p.matcher(text);

		if (!m.matches())
			return null;

		return m.group(2);
	}

	/**
	 * Executes all commands stored in a session
	 * 
	 * @param context
	 *            the current ActionContext
	 * @param originalText
	 *            the text of the DroolsSessionType representing the commands
	 * @throws IOException
	 *             only thrown if something went wrong while writing to
	 *             KnowWE.jsp
	 */
	private void processSession(UserActionContext context, String originalText) throws IOException {
		String[] commands = originalText.split("\r\n");
		for (String command : commands) {
			command = command.replace("\n", "");
			context.getParameters().put("command", command);
			context.getAction().execute(context);
		}
	}

	/**
	 * Loads the fact which is specified in the request.
	 * 
	 * @param session
	 *            the current KnowledgeSession
	 * @param context
	 *            the current ActionContext
	 * @return AbstractFact the loaded fact (null if no one was found)
	 */
	private AbstractFact loadFact(StatefulKnowledgeSession session, UserActionContext context) {
		String factName = extractFactName(context.getParameter("command"));
		if (factName != null) {
			for (Object o : session.getObjects()) {
				if (o instanceof AbstractFact
						&& ((AbstractFact) o).getName().equals(factName))
					return (AbstractFact) o;
			}
			Logging.getInstance().severe(
					"Could not find " + factName + " in the current session.");
			return null;
		}
		Logging.getInstance().severe("Unable to extract factName from command.");
		return null;
	}

	/**
	 * Returns a message in XML Format which is processed via JavaScript.
	 * 
	 * @param context
	 *            the current ActionContext
	 * @param message
	 *            a custom Message which will be wrapped with XML markups
	 */
	private void returnMessage(UserActionContext context, String message, ResponseType responseType) throws IOException {
		JsonObject json = new JsonObject();

		json.addProperty("status", responseType.ordinal());

		json.addProperty("command", context.getParameter("command"));
		json.addProperty("message", JSONValue.escape(KnowWEUtils.escapeHTML(message)));

		context.getWriter().write(json.toString() + "\n");
	}

	private String extractActionType(String text) {
		text = removeleadingSpaces(text);
		if (text.contains(" ")) {
			return text.substring(0, text.indexOf(" "));
		}
		return text;
	}

	private String extractFactName(String text) {
		Pattern p = Pattern.compile("^(set|get)\\s*(.+?)\\s*(=.*|$)");
		Matcher m = p.matcher(text);

		if (!m.matches())
			return null;

		return m.group(2);
	}

	private String extractValue(String text) {
		List<StringFragment> value = SplitUtility.splitUnquoted(text, "=");
		if (value.size() > 1) {
			return value.get(1).getContent().trim();
		}
		return null;
	}

	private boolean checkCommandSyntax(String regex, UserActionContext context) {
		return Pattern.compile(regex).matcher(context.getParameter("command")).matches();
	}

	private String removeleadingSpaces(String text) {
		while (text.startsWith(" ")) {
			text = text.replaceFirst(" ", "");
		}
		return text;
	}

}
