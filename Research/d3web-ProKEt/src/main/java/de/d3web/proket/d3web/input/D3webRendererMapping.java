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
import de.d3web.core.knowledge.terminology.info.MMInfo;
import de.d3web.proket.d3web.output.render.DefaultRootD3webRenderer;
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
		this.put("Root", "DefaultRootD3webRenderer");
		this.put("Default", "DefaultRootD3webRenderer");
		this.put("Question", "QuestionD3webRenderer");
		this.put("IMGQuestion", "ImageQuestionD3webRenderer");
		this.put("QCont", "QuestionnaireD3webRenderer");

		this.put("IMGOC", "AnswerImgOCD3webRenderer");
		this.put("IMGMC", "AnswerImgMCD3webRenderer");
		this.put("OC", "AnswerOCD3webRenderer");
		// this.put("YN", prefix + "AnswerYND3webRenderer");
		this.put("MC", "AnswerMCD3webRenderer");
		this.put("TXT", "AnswerTextD3webRenderer");
		this.put("NUM", "AnswerNumD3webRenderer");
		this.put("DATE", "AnswerDateD3webRenderer");
		this.put("UNKNOWN", "AnswerUnknownD3webRenderer");
	}

	/**
	 * Retrieves renderer objects for basic dialog components, EXCLUDING
	 * answers. That is, for the root (dialog), questionnaires, and questions.
	 * 
	 * @created 15.01.2011
	 * @param to The TerminologyObject an appropriate renderer is sought-after
	 * @return The renderer as a simple Object.
	 */
	public Object getRendererObject(TerminologyObject to) {
		String prefix = GlobalSettings.getInstance().getD3webRendererPath();
		String userPref = D3webConnector.getInstance().getUserprefix();
		Class<?> result = null;
		try {
			if (to == null) {

				// TODO refactor so it flexibly works with all subrenderers too
				String pref =
						userPref.equals("") ? prefix : prefix + userPref;
				result = Class.forName(pref + this.get("Root"));
			}
			else if (to instanceof Question) {
				result = Class.forName(prefix + this.get("Question"));
				if (to.getInfoStore().getValue(ProKEtProperties.IMAGE) != null) {
					result = Class.forName(prefix + this.get("IMGQuestion"));
				}
			}
			else if (to instanceof QContainer) {
				result = Class.forName(prefix + this.get("QCont"));
			}
			else {
				result = Class.forName(this.get(prefix + "Default"));
			}
		}
		catch (ClassNotFoundException cne) {
			System.out.println(cne);

			if (result == null) {
				result = DefaultRootD3webRenderer.class;
			}
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

	/**
	 * Retrieves renderer objects for several basic answer types, e.g., oc
	 * answers, mc answers, num answers...
	 * 
	 * @created 15.01.2011
	 * @param to The TerminologyObject an appropriate renderer is sought-after
	 * @return The renderer as a simple Object.
	 */
	public Object getAnswerRendererObject(TerminologyObject to) {
		String prefix = GlobalSettings.getInstance().getD3webRendererPath();
		Class<?> result = null;
		try {
			if (to == null) {
				result = Class.forName(prefix + this.get("Root"));
			}

			if (to instanceof QuestionOC) {
				result = Class.forName(prefix + this.get("OC"));
				if (to.getInfoStore().getValue(MMInfo.DESCRIPTION) != null &&
						to.getInfoStore().getValue(MMInfo.DESCRIPTION).contains("IMG#####")) {
					result = Class.forName(prefix + this.get("IMGOC"));
				}
			}
			// else if (parentto instanceof QuestionYN) {
			// result = Class.forName(this.get("YN"));
			// }
			else if (to instanceof QuestionMC) {
				result = Class.forName(prefix + this.get("MC"));
				if (to.getInfoStore().getValue(MMInfo.DESCRIPTION) != null &&
						to.getInfoStore().getValue(MMInfo.DESCRIPTION).contains("IMG#####")) {
					result = Class.forName(prefix + this.get("IMGMC"));
				}
			}
			else if (to instanceof QuestionText) {
				result = Class.forName(prefix + this.get("TXT"));
			}
			else if (to instanceof QuestionNum) {
				result = Class.forName(prefix + this.get("NUM"));
			}
			else if (to instanceof QuestionDate) {
				result = Class.forName(prefix + this.get("DATE"));
			}
			// result = Class.forName(this.get("Choice"));
		}
		catch (ClassNotFoundException cne) {
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

	public Object getUnknownRenderer() {

		String prefix = GlobalSettings.getInstance().getD3webRendererPath();
		Class<?> result = null;

		try {
			result = Class.forName(prefix + this.get("UNKNOWN"));
		}

		catch (ClassNotFoundException cne) {
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
