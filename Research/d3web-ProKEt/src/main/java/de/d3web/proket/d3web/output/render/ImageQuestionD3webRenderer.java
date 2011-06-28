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
package de.d3web.proket.d3web.output.render;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.proket.d3web.input.D3webConnector;
import de.d3web.proket.d3web.input.D3webUtils;
import de.d3web.proket.d3web.properties.ProKEtProperties;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Renderer for rendering basic Questions.
 * 
 * TODO CHECK: 1) basic properties for questions
 * 
 * TODO LATER: 1) further question types needed?
 * 
 * @author Martina Freiberg
 * @created 15.01.2011
 */
public class ImageQuestionD3webRenderer extends AbstractD3webRenderer implements IQuestionD3webRenderer {

	@Override
	/**
	 * Adapted specifically for question rendering
	 */
	public String renderTerminologyObject(ContainerCollection cc, TerminologyObject to,
			TerminologyObject parent) {

		StringBuilder sb = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		// get the fitting template. In case user prefix was specified, the
		// specific TemplateName is returned, otherwise, the base object name.
		StringTemplate st = TemplateUtils.getStringTemplate(
				super.getTemplateName("ImageQuestionD3web"), "html");

		// set some basic properties
		st.setAttribute("fullId", getID(to));
		st.setAttribute("title", to.getName());
		st.setAttribute("count", D3webConnector.getInstance().getID(to));

		// TODO extend for HERNIA
		// read html popups from properties
		String resString = to.getInfoStore().getValue(ProKEtProperties.POPUP);
		if (resString != null) {
			st.setAttribute("tooltip", resString);
		}

		// get d3web properties
		Form current = d3webSession.getInterview().nextForm();
		Blackboard bb = d3webSession.getBlackboard();
		Value val = bb.getValue((ValueObject) to);

		// QContainer indicated
		if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(parent) ||
				isIndicated(parent, bb)) {

			// show, if indicated follow up
			if ((D3webUtils.isFollowUpTOinQCon(to, parent) && isIndicated(to, bb))
					|| (!D3webUtils.isFollowUpTOinQCon(to, parent))) {
				st.removeAttribute("inactive");
				st.removeAttribute("qstate");
				st.setAttribute("qstate", "");
			}
			else {
				st.setAttribute("inactive", "true");
			}
		}

		else {
			st.setAttribute("inactive", "true");
		}

		// check answer - if answered, mark question as done
		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.removeAttribute("qstate");
			st.setAttribute("qstate", "question-d");
		}

		// currently active question (next interview object)
		// is marked highlighted
		if (current != null && current.getInterviewObject() != null
				&& current.getInterviewObject().equals(to)) {
			st.setAttribute("qstate", "question-c");
		}

		String imgName = to.getInfoStore().getValue(ProKEtProperties.IMAGE);
		String width = to.getInfoStore().getValue(ProKEtProperties.IMAGEWIDTH);
		String height = to.getInfoStore().getValue(ProKEtProperties.IMAGEHEIGHT);

		st.setAttribute("image", imgName);
		if (width != null) st.setAttribute("width", width);
		if (height != null) st.setAttribute("height", height);

		// read clicable-area coordinates from KB
		String areas = getChoiceAreas(width, height, to);
		if (areas != null) st.setAttribute("areas", areas);

		// if (to instanceof QuestionMC) {
		// st.setAttribute("sendButton", "true");
		// }

		// underneath="within" a rendered question, always answers are rendered
		super.renderChoices(st, cc, to, parent);

		sb.append(st.toString());

		// handle tables for multi-column styles
		super.makeTables(to, parent, cc, sb);

		return sb.toString();
	}

	Pattern imageMapPattern = Pattern.compile("^ *SIZE *(\\d+) *x *(\\d+) *SHAPE *(\\w+) *COORDS *((?:\\d+,)+\\d+) *$");

	/**
	 * Get all the defined clickable areas of an image question and assemble
	 * them to represent a html area map String: <area shape="circle"
	 * coords="110,110,25" alt="Bremen.gif" href="Bremen.html" /> Therefore, a
	 * basic StringTemplate representing one area tag is filled with coords and
	 * shape.
	 * 
	 * @created 21.04.2011
	 * @param to The terminologyObject (usually a question) the areas are
	 *        defined for
	 * @return HTML area map representing String
	 */
	protected String getChoiceAreas(String imgWidthString, String imgHeightString, TerminologyObject to) {

		StringBuilder bui = new StringBuilder();

		double imgWidth = -1;
		double imgHeight = -1;
		try {
			if (imgWidthString != null) imgWidth = Double.valueOf(imgWidthString);
			if (imgHeightString != null) imgHeight = Double.valueOf(imgHeightString);
		}
		catch (NumberFormatException e) {
		}

		// imagequestions make sense for choice questions only so far
		if (to instanceof QuestionChoice) {

			for (Choice choice : ((QuestionChoice) to).getAllAlternatives()) {

				// basic StringTemplate file the shape and coords are inserted
				// into
				StringTemplate st = TemplateUtils.getStringTemplate(
						getTemplateName("ImageAnswerD3web"), "html");

				st.setAttribute("fullId", getID(choice));
				st.setAttribute("title", choice.getName());
				// if description = shape and coords for current Choice exist
				String imgmap = choice.getInfoStore().getValue(ProKEtProperties.IMAGEMAP);
				Matcher m;

				// if no description given at all or part of the description
				// is empty String then set defaults to avoid HTML errors
				String fixedCoords = "0,0,0,0";
				String shape = "rect";
				if (imgmap != null && (m = imageMapPattern.matcher(imgmap)).find()) {
					// split shape and coords
					double mapWidth = Double.valueOf(m.group(1));
					double mapHeight = Double.valueOf(m.group(2));

					if (mapWidth > 0 && mapHeight > 0) {
						// if the size of the image was not given, we set the
						// factors to 1
						double xFactor = 1;
						double yFactor = 1;
						if (imgWidth > 0 && imgHeight > 0) {
							xFactor = imgWidth / mapWidth;
							yFactor = imgHeight / mapHeight;
						}
						else if (imgWidth < 0 && imgHeight > 0) {
							yFactor = imgHeight / mapHeight;
							xFactor = yFactor;
						}
						else if (imgWidth > 0 && imgHeight < 0) {
							xFactor = imgWidth / mapWidth;
							yFactor = xFactor;
						}

						String[] coords = m.group(4).split(",");
						StringBuilder coordsBuilder = new StringBuilder();
						int count = 0;
						for (String coord : coords) {
							int c = (int) (Double.valueOf(coord) * (count % 2 == 0
									? xFactor
									: yFactor));
							coordsBuilder.append(c + ",");
						}
						fixedCoords = coordsBuilder.substring(0, coordsBuilder.length() - 1);
						shape = m.group(3);
					}
				}
				st.setAttribute("coords", fixedCoords);
				st.setAttribute("shape", shape);

				bui.append(st.toString());
			}
		}
		return bui.toString();
	}

}
