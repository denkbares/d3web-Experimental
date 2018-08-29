/*
 * Copyright (C) 2014 think-further.de
 */
package de.knowwe.defi.readbutton;

import java.util.List;

import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.we.basic.SessionProvider;
import de.d3web.we.knowledgebase.D3webCompiler;
import de.d3web.we.utils.D3webUtils;
import de.knowwe.core.compile.Compilers;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.user.UserContext;
import de.knowwe.kdom.defaultMarkup.DefaultMarkup;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.kdom.renderer.ReRenderSectionMarkerRenderer;

/**
 * Renders a read button on basis of established solutions in a d3web session.
 *
 * @author Sebastian Furth
 * @created 03.11.16
 */
public class SolutionReadButton extends DefaultMarkupType {

	private static final DefaultMarkup MARKUP;

	static {
		MARKUP = new DefaultMarkup("solutionreadbutton");
	}

	public SolutionReadButton() {
		super(MARKUP);
		this.setRenderer(new ReRenderSectionMarkerRenderer(new SolutionReadButtonRenderer()));
	}

	private class SolutionReadButtonRenderer implements Renderer {

		@Override
		public void render(Section<?> section, UserContext user, RenderResult result) {
			D3webCompiler compiler = Compilers.getCompiler(section, D3webCompiler.class);
			if (compiler != null) {
				Session session = SessionProvider.getSession(user, compiler.getKnowledgeBase());

				List<Solution> solutions = D3webUtils.getSolutionsNonBlocking(session, Rating.State.ESTABLISHED);
				if (solutions != null && !solutions.isEmpty()) {
					String solutionName = solutions.get(0).getName();
					result.appendHtml("<div class='solutionreadbutton'><a href='#' onclick=\"javascript:sendSolutionReadButton('" + solutionName
							+ "', '" + section.getID() + "')\">" +
							"Weiter" +
							"<img src='KnowWEExtension/images/Pfeil_nach_rechts.gif' height='60'/>" +
							"</a></div>");
					return;
				}
			}
			result.appendHtml("<div class='solutionreadbutton'>Bitte füllen Sie den Fragebogen vollständig aus.</div>");

		}

	}
}
