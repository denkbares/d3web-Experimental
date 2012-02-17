/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
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
package de.knowwe.taghandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.knowwe.core.KnowWEArticleManager;
import de.knowwe.core.KnowWEEnvironment;
import de.knowwe.core.kdom.KnowWEArticle;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.taghandler.AbstractHTMLTagHandler;
import de.knowwe.core.taghandler.TagHandler;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.manchester.ManchesterSyntaxFrameRenderer;
import de.knowwe.kdom.manchester.frame.DefaultFrame;

/**
 * The {@link OWLApiOntologyOverviewTagHandler} prints the complete asserted
 * local ontology.
 * 
 * @author Stefan Mark
 * @created 20.09.2011
 */
public class OWLApiOntologyOverviewTagHandler extends AbstractHTMLTagHandler {

	private static final String NAME = "owlapi.viewontology";

	public OWLApiOntologyOverviewTagHandler() {
		super(NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String renderHTML(String topic, UserContext user, Map<String, String> parameters, String web) {

		if (parameters.get("help") != null) {
			StringBuilder html = new StringBuilder();
			html.append("<div style=\"background: none repeat scroll 0 0 #F5F5F5;border: 1px solid #E5E5E5;padding:8px 0 10px 20px;\">");
			html.append(getDescription(user));
			html.append("</div>");
			return html.toString();
		}

		KnowWEArticleManager mgr = KnowWEEnvironment.getInstance().getArticleManager(web);
		Collection<KnowWEArticle> articles = mgr.getArticles();
		StringBuilder anchors = new StringBuilder();
		StringBuilder concepts = new StringBuilder();

		Map<String, List<Section<DefaultFrame>>> frames = new HashMap<String, List<Section<DefaultFrame>>>();

		// look for DefaultFrame sections
		for (KnowWEArticle article : articles) {

			List<Section<DefaultFrame>> defaultFrames = Sections.findSuccessorsOfType(
					article.getSection(), DefaultFrame.class);

			for (Section<DefaultFrame> frame : defaultFrames) {
				String key = frame.get().getName();
				if (frames.containsKey(key)) {
					frames.get(key).add(frame);
				}
				else {
					List<Section<DefaultFrame>> value = new ArrayList<Section<DefaultFrame>>();
					value.add(frame);
					frames.put(key, value);
				}
			}
		}

		// render the DefaultFrames
		anchors.append("<a name=\"top\"></a><ul>");
		for (String key : frames.keySet()) {
			List<Section<DefaultFrame>> defaultFrames = frames.get(key);

			anchors.append("<li style=\"display:inline; padding-right:10px;\"><a href=\"#" + key
					+ "\">" + key
						+ " (" + defaultFrames.size() + ") "
					+ "</a></li>");
			concepts.append("<a name=\"" + key + "\"></a>");

			for (Section<DefaultFrame> frame : defaultFrames) {
				@SuppressWarnings("rawtypes")
				Renderer renderer = frame.get().getRenderer();
				if (renderer instanceof ManchesterSyntaxFrameRenderer) {
					((ManchesterSyntaxFrameRenderer) renderer).setRenderLink(true);
					renderer.render(frame, user, concepts);
					((ManchesterSyntaxFrameRenderer) renderer).setRenderLink(false);
				}
			}
			concepts.append("<a href=\"#top\">top</a>");
		}
		anchors.append("</ul>");

		return anchors.toString() + concepts.toString();
	}

	/**
	 * Returns an example usage string
	 * 
	 * @return A example usage string
	 */
	@Override
	public String getExampleString() {
		StringBuilder example = new StringBuilder();
		example.append("[{KnowWEPlugin " + NAME + " [");
		example.append(", help ");
		example.append(", filter=[class|individual|object|data|misc]");
		example.append("}])\n ");
		example.append("The parameters in [ ] are optional.");
		return example.toString();
	}

	/**
	 * Appends a simple how to use message to the output if the
	 * {@link TagHandler} was used incorrectly.
	 * 
	 * @created 20.09.2011
	 * @return String The how to use message
	 */
	@Override
	public String getDescription(UserContext user) {

		StringBuilder help = new StringBuilder();
		help.append("<dl>");

		help.append("<dt><strong>NAME</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - prints the complete local ontology.</dd>");

		help.append("<dt><strong>SYNOPSIS</strong></dt>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ "}] - prints the complete local ontology.</dd>");
		help.append("<dd>[{KnowWEPlugin " + NAME
				+ " , help}] - Show a how to use message for this taghandler.</dd>");

		help.append("<dt><strong>DESCRIPTION</strong></dt>");
		help.append("<dd>The OWLApiOntologyOverviewTagHandler prints the complete local ontology. You can filter the ontology by possible frame types like 'Individual, Class, ObjectProperties, etc'.</dd>");

		help.append("</dl>");

		return help.toString();
	}
}
