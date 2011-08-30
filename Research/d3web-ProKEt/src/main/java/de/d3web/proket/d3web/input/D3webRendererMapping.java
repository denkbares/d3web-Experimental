/**
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
package de.d3web.proket.d3web.input;

import java.util.HashMap;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.proket.d3web.output.render.AbstractD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerDateD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerMCD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerNumD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerOCD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerTextD3webRenderer;
import de.d3web.proket.d3web.output.render.AnswerUnknownD3webRenderer;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
import de.d3web.proket.d3web.output.render.ImageQuestionD3webRenderer;
import de.d3web.proket.d3web.output.render.QuestionD3webRenderer;
import de.d3web.proket.d3web.output.render.QuestionnaireD3webRenderer;
import de.d3web.proket.d3web.output.render.SummaryD3webRenderer;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.utils.GlobalSettings;

/**
 * This class is intended to store the mapping between d3web TerminologyObjects
 * and Renderer classes used by the prototyping tool.
 * 
 * TODO CHECK: 1) check for further renderer types required, 2) check for
 * further bas dialog objects, 3) check for further answer types, 4) check
 * whether also an extended enum structure would be sufficient for achieving
 * such a mapping-thing.
 * 
 * @author Martina Freiberg
 * @created 14.01.2011
 */
public class D3webRendererMapping extends HashMap<String, String> {

	private static final long serialVersionUID = 6572371579568756873L;

	private static final String UNKNOWN_ANSWER = "UNKNOWN";
	private static final String DATE_ANSWER = "DATE";
	private static final String NUM_ANSWER = "NUM";
	private static final String TXT_ANSWER = "TXT";
	private static final String MC_ANSWER = "MC";
	private static final String OC_ANSWER = "OC";
	private static final String SUMMARY = "Summary";
	private static final String Q_CONT = "QCont";
	private static final String IMG_QUESTION = "IMGQuestion";
	private static final String QUESTION = "Question";
	private static final String DEFAULT = "Default";

	// the instance
	private static D3webRendererMapping instance = null;

	// the prefix, i.e. the package declaration where renderer classes are
	// found plus, optionally, a user prefix from the XML specifying specific
	// Renderers.
	/**
	 * Creates or returns the one & only instance of the RendererMapping Map.
	 * 
	 * @created 15.01.2011
	 * @return the one & only instance of the RendererMapping-Map
	 */
	public static D3webRendererMapping getInstance() {
		if (instance == null) {
			instance = new D3webRendererMapping();
		}
		return instance;
	}

	private D3webRendererMapping() {
		this.put(DEFAULT, DefaultRootD3webRenderer.class.getSimpleName());
		this.put(QUESTION, QuestionD3webRenderer.class.getSimpleName());
		this.put(IMG_QUESTION, ImageQuestionD3webRenderer.class.getSimpleName());
		this.put(Q_CONT, QuestionnaireD3webRenderer.class.getSimpleName());
		this.put(SUMMARY, SummaryD3webRenderer.class.getSimpleName());

		this.put(OC_ANSWER, AnswerOCD3webRenderer.class.getSimpleName());
		this.put(MC_ANSWER, AnswerMCD3webRenderer.class.getSimpleName());
		this.put(TXT_ANSWER, AnswerTextD3webRenderer.class.getSimpleName());
		this.put(NUM_ANSWER, AnswerNumD3webRenderer.class.getSimpleName());
		this.put(DATE_ANSWER, AnswerDateD3webRenderer.class.getSimpleName());
		this.put(UNKNOWN_ANSWER, AnswerUnknownD3webRenderer.class.getSimpleName());
	}

	/**
	 * Retrieves renderer objects for basic dialog components, EXCLUDING
	 * answers. That is, for the root (dialog), questionnaires, and questions.
	 * 
	 * @created 15.01.2011
	 * @param to The TerminologyObject an appropriate renderer is sought-after
	 * @return The renderer as a simple Object.
	 */
	public AbstractD3webRenderer getRenderer(TerminologyObject to) {
		String userPref = D3webConnector.getInstance().getUserprefix();
		String name = DEFAULT;
		if (to == null) {
			return (AbstractD3webRenderer) getRenderer(userPref, name);
		}
		else if (to instanceof Question) {
			name = QUESTION;
			if (to.getInfoStore().getValue(ProKEtProperties.IMAGE) != null) {
				name = IMG_QUESTION;
			}
		}
		else if (to instanceof QContainer) {
			name = Q_CONT;
		}

		return (AbstractD3webRenderer) getRenderer(name);
	}

	/**
	 * Retrieves renderer objects for several basic answer types, e.g., oc
	 * answers, mc answers, num answers...
	 * 
	 * @created 15.01.2011
	 * @param to The TerminologyObject an appropriate renderer is sought-after
	 * @return The renderer as a simple Object.
	 */
	public AnswerD3webRenderer getAnswerRendererObject(TerminologyObject to) {

		String name = DEFAULT;
		if (to instanceof QuestionOC) {
			name = OC_ANSWER;
		}
		else if (to instanceof QuestionMC) {
			name = MC_ANSWER;
		}
		else if (to instanceof QuestionText) {
			name = TXT_ANSWER;
		}
		else if (to instanceof QuestionNum) {
			name = NUM_ANSWER;
		}
		else if (to instanceof QuestionDate) {
			name = DATE_ANSWER;
		}

		return (AnswerD3webRenderer) getRenderer(name);
	}

	public SummaryD3webRenderer getSummaryRenderer() {
		return (SummaryD3webRenderer) getRenderer(SUMMARY);
	}

	public AnswerD3webRenderer getUnknownRenderer() {
		return (AnswerD3webRenderer) getRenderer(UNKNOWN_ANSWER);
	}

	private Object getRenderer(String name) {
		return getRenderer("", name);
	}

	private Object getRenderer(String userPrefix, String name) {

		String prefix = GlobalSettings.getInstance().getD3webRendererPath();
		Class<?> result = null;

		try {
			result = Class.forName(prefix + userPrefix + this.get(name));
		}
		catch (ClassNotFoundException cne) {
			return null;
		}

		Object instance;
		try {
			instance = result.newInstance();
		}
		catch (InstantiationException e) {
			return null;
		}
		catch (IllegalAccessException e) {
			return null;
		}
		return instance;
	}
}
