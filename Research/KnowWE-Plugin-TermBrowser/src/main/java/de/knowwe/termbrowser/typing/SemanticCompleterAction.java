/*
 * Copyright (C) 2015 denkbares GmbH, Germany
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.termbrowser.typing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openrdf.repository.RepositoryException;

import com.denkbares.ontology.util.Concept;
import com.denkbares.ontology.util.DefaultConcept;
import com.denkbares.semantictyping.Completion;
import com.denkbares.semantictyping.CompletionResult;
import com.denkbares.semantictyping.DefaultCompletion;
import de.d3web.strings.Strings;
import de.d3web.utils.Log;
import de.d3web.utils.Pair;
import de.knowwe.core.Attributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.user.UserContext;
import de.knowwe.ontology.compile.OntologyCompiler;
import de.knowwe.rdf2go.Rdf2GoCore;

/**
 * @author Sebastian Furth
 * @created 25.09.2013
 */
public class SemanticCompleterAction extends AbstractAction {

	private Pair<String, String> lastQueryKey = null;
	private JSONArray lastCompletions = new JSONArray();

	private static final Object mutex = new Object();

	@Override
	public void execute(UserActionContext context) throws IOException {
		String phrase = context.getParameter("search");
		String master = context.getParameter("master");

		OntologyCompiler compiler = getCompiler(context);
		if(compiler == null) return;

		TermBrowserCompletionManager completionManager = TermBrowserCompletionManager.getInstance(compiler);
		if (completionManager == null) return;
		if(completionManager.isInitializationRunning()) {
			JSONArray result = new JSONArray();
			Completion message = new DefaultCompletion(new DefaultConcept(phrase, "Msg" ), "Initialization running, plz wait");
			try {
				addCompletion(phrase,compiler.getRdf2GoCore(), Locale.ROOT, result, message);
				context.setContentType("application/json");
				result.write(context.getWriter());
				return;
			}
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// from here, we are sure that the sscService is available and in ready state
		try {
			synchronized (mutex) {
				Pair<String, String> key = new Pair<>(master, phrase);
				if (!key.equals(lastQueryKey)) {
					long start = System.currentTimeMillis();
					lastCompletions = getJSONCompletions(completionManager, phrase, compiler.getRdf2GoCore());
					Log.info("Searched '" + phrase + "' in " + (System.currentTimeMillis() - start) + "ms");
					lastQueryKey = key;
				}
				context.setContentType("application/json");
				lastCompletions.write(context.getWriter());
			}
		}
		catch (JSONException e) {
			throw new IOException("Unable to transform completions to JSON", e);
		}
		catch (InterruptedException e) {
			throw new IOException("Initialization interrupted", e);
		}
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	public static OntologyCompiler getCompiler(UserContext context) {
		String sectionId = context.getParameter(Attributes.SECTION_ID);
		Section<?> section = Sections.get(sectionId);
		if (section == null) {
			Log.severe("No section found for id " + sectionId);
			return null;
		}
		OntologyCompiler compiler = Compilers.getCompiler(section, OntologyCompiler.class);
		if (compiler == null) {
			Log.severe("No compiler found for section " + sectionId);
			return null;
		}
		return compiler;
	}

	private JSONArray getJSONCompletions(TermBrowserCompletionManager service, String phrase, Rdf2GoCore core) throws JSONException, InterruptedException, IOException, RepositoryException {
		CompletionResult completions = service.getCompletions(phrase, 20);
		// TODO: get current language from wiki markup ?!
		return toJSON(completions, phrase, core, Locale.ENGLISH);
	}


	private JSONArray toJSON(CompletionResult completions, String phrase, Rdf2GoCore core, Locale lang) throws JSONException {

		JSONArray result = new JSONArray();
		int cnt = 0;
		for (Completion c : completions) {
			//we only take the top 50 completions to present in the front-end
			if (cnt > 50) break;
			addCompletion(phrase, core, lang, result, c);
			cnt++;
		}

		return result;
	}

	private void addCompletion(String phrase, Rdf2GoCore core, Locale lang, JSONArray result, Completion c) throws JSONException {
		String data = toJSONObject(c);
		String html = toHTMLSpan(c, core, lang);
		String suggestionHTML = toSuggestionHTMLSpan(c, phrase, html);
		String plainText = getLabel(c, lang);

		JSONArray completionArray = new JSONArray();
		completionArray.put(data);
		completionArray.put(plainText);
		completionArray.put(html);
		completionArray.put(suggestionHTML);
		result.put(completionArray);
	}

	/**
	 * Returns a HTML snippet that is used as suggestion html in the autocompletion.
	 *
	 * @param c       the underlying completion
	 * @param pattern the search pattern
	 * @param html    the HTML snippet used as value
	 * @return HTML snippet used in the autocompletion
	 */
	protected String toSuggestionHTMLSpan(Completion c, String pattern, String html) {
		StringBuilder result = new StringBuilder(html);
		if (!html.toLowerCase().contains(pattern.toLowerCase())) {
			result.append("<br/>\r\n<span class='suggestiontext'>");
			result.append("(");
			result.append(Strings.encodeHtml(c.getMatchedText()));
			result.append(")");
			result.append("</span>");
		}
		return result.toString();
	}

	/**
	 * Returns the plain text representation of a completion.
	 *
	 * @param c the underlying completion
	 * @return the plain text representation of the completion
	 */
	protected String getLabel(Completion c, Locale lang) {
		String label = c.getConcept().getLabel(lang);
		if(Strings.isBlank(label)) {
			// if it should occur that a completion item does not posses a name (which is result of a sloppy
			// index generation), then we deliver the matched synonym
			label = c.getMatchedText();
		}
		return label;
	}

	/**
	 * A JSON-Object representing the completion.
	 *
	 * @param c the underlying completion
	 * @return JSON object representing the completion object
	 * @throws org.json.JSONException
	 */
	protected String toJSONObject(Completion c) throws JSONException {
		JSONObject result = new JSONObject();
		result.put("concept", c.getConcept().getURI().toString());
		result.put("conceptClass", c.getConcept().getTypeURI().toString());
		return result.toString();
	}

	/**
	 * Returns a HTML snippet used as value in the editor.
	 *
	 * @param completion the underlying completion
	 * @param core       the underlying Rdf2GoCore
	 */
	protected String toHTMLSpan(Completion completion, Rdf2GoCore core, Locale lang) {
		StringBuilder result = new StringBuilder();
		result.append("<span class='editableconcept' ");
		result.append("data-conceptClass='");
		Concept concept = completion.getConcept();
		@NotNull URI typeURI = concept.getTypeURI();
		result.append(typeURI);
		result.append("' ");
		result.append("id='");
		result.append(concept.getURI());
		result.append("'>");
		String className = concept.getTypeLabel(lang);
		if (className == null) {
			String typeUriString = "null";
			if(typeURI!= null) {
				typeUriString = typeURI.toString();
			}
			String classRaw = removeNamespace(typeUriString, core);
			if(classRaw != null) {
						className = Strings.decodeURL(classRaw);
			} else {
				className = typeUriString;
			}
		}
		if (className != null && !className.isEmpty()) {
			result.append("<span class='conceptclassname'>");
			result.append(className.toUpperCase());
			result.append("</span>");
		}
		result.append("<span class='label'>");
		result.append(Strings.encodeHtml(getLabel(completion,lang)));
		result.append("</span>");

		//result.append(" (");
		//result.append(removeNamespace(concept.getURI().toString(), core));
		//result.append(")");

		result.append("</span>");
		return result.toString();
	}

	protected String removeNamespace(String uri, Rdf2GoCore core) {
		try {
			String lns = core.getLocalNamespace();
			if (uri.startsWith(lns)) {
				return uri.substring(lns.length());
			}
			URL url = new URL(uri);
			return url.getRef();
		}
		catch (MalformedURLException e) {
			// ignore
		}
		return uri;
	}
}
