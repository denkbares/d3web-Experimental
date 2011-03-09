package de.d3web.we.drools.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.drools.action.utils.DroolsUtils;
import de.d3web.we.drools.kdom.DroolsSessionRootType;
import de.d3web.we.drools.terminology.AbstractFact;
import de.d3web.we.drools.terminology.ChoiceInput;
import de.d3web.we.drools.terminology.DroolsKnowledgeHandler;
import de.d3web.we.drools.terminology.SolutionInput;
import de.d3web.we.drools.terminology.TextValue;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;
import de.d3web.we.utils.KnowWEUtils;

/**
 * @author Florian Ziegler, Sebastian Furth, Alex Legler
 */
public class DroolsCodeCompletionAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		// Create new JSON Object
		JsonObject json = new JsonObject();

		// Get committed query and add it to JSON Object
		String query = context.getParameter("query");
		query = KnowWEUtils.urldecode(query);
		json.addProperty("query", query);


		// ignore empty lines
		if (!query.matches("\\s*")) {

			// remove leading spaces
			query = removeleadingSpaces(query);

			// if only 1 word is there, e.g. get or set
			if (!query.contains(" ")) {
				String output;
				if (checkKeywordIgnoreCase(query, "set"))
					output = "set";
				else if (checkKeywordIgnoreCase(query, "get"))
					output = "get";
				else if (checkKeywordIgnoreCase(query, "fire"))
					output = "fire";
				else if (checkKeywordIgnoreCase(query, "reset"))
					output = "reset";
				else if (checkKeywordIgnoreCase(query, "help"))
					output = "help";
				else if (checkKeywordIgnoreCase(query, "clear"))
					output = "clear";
				else if (checkKeywordIgnoreCase(query, "store"))
					output = "store";
				else if (checkKeywordIgnoreCase(query, "load"))
					output = "load";
				else
					output = "";

				// add suggestion to JSON Object
				JsonArray a = new JsonArray();
				a.add(new JsonPrimitive(JSONValue.escape(KnowWEUtils
						.html_escape(output))));

				json.add("suggestions", a);

				// Input suggestions
				// TODO: We have to check whether the '=' is quoted and is part
				// of the Input's name
			} else if (!query.contains("=") && (query.startsWith("set") || query.startsWith("get"))) {

				String action = query.substring(0, query.indexOf(" ")).trim();
				String object = query.substring(query.indexOf(" ") + 1).trim();

				List<String> input;

				if (action.equals("set")) {
					input = getNonSolutionInputSuggestions(context, object);
				} else {
					input = getInputSuggestions(context, object);
				}

				JsonArray a = new JsonArray();

				for (String s : input)
					a.add(new JsonPrimitive(JSONValue.escape(KnowWEUtils
							.html_escape(action + " " + s))));

				json.add("suggestions", a);
			} else if (query.startsWith("set")) {
				// Value suggestions
				// DO NOT forget to trim, otherwise code completion won't work
				String action = query.substring(0, query.indexOf(" ")).trim();
				String object = query.substring(query.indexOf(" ") + 1,
						query.indexOf("=")).trim();
				String value = query.substring(query.indexOf("=") + 1);
				value = removeleadingSpaces(value);

				List<String> values = getValueSuggestions(context, object, value);
				JsonArray a = new JsonArray();

				for (String s : values)
					a.add(new JsonPrimitive(JSONValue.escape(KnowWEUtils
							.html_escape(action + " " + object + " = " + s))));

				json.add("suggestions", a);
			} else if (query.startsWith("load")) {
				// Value suggestions
				// DO NOT forget to trim, otherwise code completion won't work
				String action = query.substring(0, query.indexOf(" ")).trim();
				String value = query.substring(query.indexOf(" ") + 1);
				value = removeleadingSpaces(value);

				List<String> values = getSessionSuggestions(context, value);
				JsonArray a = new JsonArray();

				for (String s : values)
					a.add(new JsonPrimitive(JSONValue.escape(KnowWEUtils
							.html_escape(action + " " + s))));

				json.add("suggestions", a);
			}

		}

		context.getWriter().write(json.toString());
	}

	private String removeleadingSpaces(String text) {
		while (text.startsWith(" ")) {
			text = text.replaceFirst(" ", "");
		}
		return text;
	}

	private boolean checkKeywordIgnoreCase(String word, String target) {
		word = word.toLowerCase();
		target = target.toLowerCase();
		for (int i = 0; i < word.length(); i++) {
			if (i >= target.length() || word.charAt(i) != target.charAt(i)) {
				return false;
			}
		}
		return true;
	}


	/**
	 * returns all inputs
	 */
	private List<String> getInputSuggestions(UserActionContext context, String object) {
		List<String> suggestions = new ArrayList<String>();

		Map<String, Object> factsStore = DroolsKnowledgeHandler.getInstance().getFactsStore(
				(DroolsUtils.loadArticle(context)).getTitle());

		for (Object o : factsStore.values()) {
			if (o instanceof AbstractFact) {
				if (((AbstractFact) o).getName().toLowerCase().startsWith(object.toLowerCase()))
					suggestions.add(((AbstractFact) o).getName());
			}
		}
		return suggestions;
	}

	/**
	 * returns only inputs which are not SolutionInput
	 */
	private List<String> getNonSolutionInputSuggestions(UserActionContext context,
			String object) {
		List<String> suggestions = new ArrayList<String>();

		Map<String, Object> factsStore = DroolsKnowledgeHandler.getInstance().getFactsStore(
				(DroolsUtils.loadArticle(context)).getTitle());

		for (Object o : factsStore.values()) {
			if (o instanceof AbstractFact && !(o instanceof SolutionInput)) {
				if (((AbstractFact) o).getName().toLowerCase().startsWith(object.toLowerCase()))
					suggestions.add(((AbstractFact) o).getName());
			}
		}
		return suggestions;
	}

	/**
	 * returns all values
	 */
	private List<String> getValueSuggestions(UserActionContext context, String object,
			String value) {
		List<String> suggestions = new ArrayList<String>();

		Map<String, Object> factsStore = DroolsKnowledgeHandler.getInstance().getFactsStore(
				(DroolsUtils.loadArticle(context)).getTitle());

		if (factsStore.get(object) instanceof ChoiceInput) {
			ChoiceInput input = (ChoiceInput) factsStore.get(object);

			for (TextValue textValue : input.getPossibleValues()) {
				if (textValue.toString().toLowerCase().startsWith(value.toLowerCase()))
					suggestions.add(textValue.toString());
			}
		}

		return suggestions;
	}

	/**
	 * returns all values
	 */
	private List<String> getSessionSuggestions(UserActionContext context, String value) {
		List<String> suggestions = new ArrayList<String>();

		KnowWEArticle article = DroolsUtils.loadArticle(context);

		if (article != null) {

			// Get the RootType sections
			List<Section<DroolsSessionRootType>> rootTypes = new ArrayList<Section<DroolsSessionRootType>>();
			Sections.findSuccessorsOfType(article.getSection(), DroolsSessionRootType.class,
					rootTypes);

			// Search for the correct session
			for (Section<DroolsSessionRootType> rootType : rootTypes) {
				String sessionName = DefaultMarkupType.getAnnotation(rootType, "Name");
				if (sessionName.toLowerCase().startsWith(value.toLowerCase()))
					suggestions.add(sessionName);
			}
		}
		return suggestions;
	}


}
