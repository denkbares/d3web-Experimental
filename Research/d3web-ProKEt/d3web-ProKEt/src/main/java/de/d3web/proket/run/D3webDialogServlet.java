/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.run;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QuestionChoice;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.Rating;
import de.d3web.core.knowledge.terminology.Solution;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.blackboard.DefaultFact;
import de.d3web.core.session.blackboard.Fact;
import de.d3web.core.session.blackboard.FactFactory;
import de.d3web.core.session.values.ChoiceValue;
import de.d3web.core.session.values.DateValue;
import de.d3web.core.session.values.MultipleChoiceValue;
import de.d3web.core.session.values.NumValue;
import de.d3web.core.session.values.TextValue;
import de.d3web.indication.inference.PSMethodUserSelected;
import de.d3web.proket.data.D3webDialog;
import de.d3web.proket.data.Dialog;
import de.d3web.proket.data.DialogStrategy;
import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.DialogType;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.input.d3web.D3webConversionUtils;
import de.d3web.proket.input.d3web.D3webUtils;
import de.d3web.proket.input.defaultsettings.GlobalSettings;
import de.d3web.proket.input.xml.XMLd3webParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.output.render.IRenderer;
import de.d3web.proket.output.render.Renderer;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.IDUtils;

/**
 * Servlet for creating and using d3web-binded dialogs.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 *         TODO: currently all attributes (kbname, dtype etc) are retrieved by
 *         the Servlet String. Refactor this so a custom XML is read!
 * @param <KnoweldgeBase>
 */
public class D3webDialogServlet<KnoweldgeBase> extends HttpServlet {

	// default DialogType
	private DialogStrategy dialogStrat = DialogStrategy.NEXTFORM;
	
	private DialogType dialogType = DialogType.SINGLEFORM;
	private XMLd3webParser d3webParser;
	private KnowledgeBase kb;
	private Session d3webSession;
	private String css = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public D3webDialogServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String action = request.getParameter("action");
		if (action == null) {
			action = "show";
		}

		HttpSession httpSession = request.getSession(true);

		// Session is set inactive after maximum one day
		httpSession.setMaxInactiveInterval(24 * 3600);

		// get the d3web session. Is either stored in the d3webSession attribute
		// of the HTTP Session, or null at the first calling
		d3webSession =
				(Session) httpSession.getAttribute("d3webSession");

		// default kb is the car.jar if nothing else is given
		// TODO create a better default kb
		String source = "default.xml";
		if (request.getParameter("src") != null) {
			source = request.getParameter("src");
		}
		if (!source.endsWith(".xml")) {
			source = source + ".xml";
		}

		d3webParser = new XMLd3webParser(source);
		kb = d3webParser.getKnowledgeBase();
		String kbname = d3webParser.getKnowledgeBaseName();

		// get the dialog strategy if quick change via request is desired
		if (request.getParameter("dstrat") != null) {
			dialogStrat = DialogStrategy.valueOf(request.getParameter("dstrat"));
		}
		// get the dialog type if quick change via request is desired
		if (request.getParameter("dtype") != null) {
			dialogType = DialogType.valueOf(request.getParameter("dtype"));
		}

		/* create a Session according to the type set */
		if (d3webSession == null
				|| (action.equals("show") && !kbname.equals(httpSession
						.getAttribute("kbName")))
				|| (!action.equals("show")
						&& request.getParameter("reset") != null
						&& request.getParameter("reset").equals("true"))) {
			d3webSession = createNewSession(httpSession, kbname);
		}

		// switch action
		if (action.equalsIgnoreCase("show")) {
			show(request, response, httpSession, d3webSession);
			return;
		}
		if (action.equalsIgnoreCase("solutions")) {
			solutions(request, response, httpSession, d3webSession);
			return;
		}
		if (action.equalsIgnoreCase("addfact")) {
			addFact(request, response, httpSession, d3webSession);
			return;
		}
		if (action.equalsIgnoreCase("nextform")) {
			nextForm(request, response, httpSession, d3webSession);
			return;
		}
		if (action.equalsIgnoreCase("selectQuestionnaire")) {
			selectQuestionnaire(request, response, httpSession, d3webSession);
			return;
		}
		if (action.equalsIgnoreCase("getChildren")) {
			getChildren(request, response, httpSession, d3webSession);
			return;
		}
		if (action.equalsIgnoreCase("getRatings")) {
			InterviewObject nextForm = d3webSession.getInterview().nextForm()
					.getInterviewObject();
			StringBuilder sb = new StringBuilder();
			buildRatings(sb, nextForm, d3webSession);
			response.getWriter().write(sb.toString());
			response.getWriter().close();
		}
	}

	private void addFact(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,
			Session d3webSession) {
		Blackboard blackboard = d3webSession.getBlackboard();
		String qid = request.getParameter("qid");
		// remove prefix
		qid = IDUtils.removeNamspace(qid);
		TerminologyObject to = D3webUtils.getTerminologyObjectByID(
				d3webSession, qid);
		if (to == null) {
			return;
		}

		// create value
		Value value = null;
		String positions = request.getParameter("pos");

		// ChoiceValue, DateValue, MultipleChoiceValue, NumValue, TextValue,
		// UndefinedValue
		if (to instanceof QuestionChoice) {
			List<Choice> choices = ((QuestionChoice) to).getAllAlternatives();
			if (to instanceof QuestionOC) {
				// valueString is the ID of the selected item
				try {
					value = new ChoiceValue(choices.get(Integer
							.parseInt(positions)));
				} catch (NumberFormatException nfe) {
					// value still null, will not be set
				}
			} else if (to instanceof QuestionMC) {
				// valueString is a comma separated list of the IDs of the
				// selected items
				List<Choice> values = new ArrayList<Choice>();
				String[] parts = positions.split(",");
				for (String part : parts) {
					values.add(new Choice(part));
				}
				value = MultipleChoiceValue.fromChoices(values);
			}
		} else if (to instanceof QuestionText) {
			value = new TextValue(positions);
		} else if (to instanceof QuestionNum) {
			try {
				value = new NumValue(Double.parseDouble(positions));
			} catch (NumberFormatException ex) {
				// value still null, will not be set
			}
		}
		// QuestionDate
		else if (to instanceof QuestionDate) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			try {
				value = new DateValue(dateFormat.parse(positions));
			} catch (ParseException e) {
				// value still null, will not be set
			}
		}

		// revoke previously set fact for this question
		Fact currentFact = blackboard.getValueFact((ValueObject) to);
		// Value abc = currentFact.getValue();
		if (currentFact != null) {
			blackboard.removeValueFact(currentFact);
		}

		if (value != null) {
			Fact fact = FactFactory.createFact(to, value,
					PSMethodUserSelected.getInstance(),
					PSMethodUserSelected.getInstance());
			blackboard.addValueFact(fact);
		}
	}

	private void buildRatings(StringBuilder sb, TerminologyObject source,
			Session d3webSession) {
		if (source == null) {
			return;
		}
		// find ratings if available
		Collection<Solution> solutions = d3webSession.getBlackboard()
				.getValuedSolutions();
		for (Solution solution : solutions) {
			if (source.getName() != null
					&& ("SOL " + source.getName()).equals(solution.getName())) {
				Rating rating = d3webSession.getBlackboard()
						.getRating(solution);
				de.d3web.core.knowledge.terminology.Rating.State state = rating
						.getState();
				String ratingClass = null;
				if (state == de.d3web.core.knowledge.terminology.Rating.State.ESTABLISHED) {
					ratingClass = D3webUtils.discretizeRating(100.0);
				} else if (state == de.d3web.core.knowledge.terminology.Rating.State.SUGGESTED) {
					ratingClass = D3webUtils.discretizeRating(50.0);
				} else if (state == de.d3web.core.knowledge.terminology.Rating.State.EXCLUDED) {
					ratingClass = D3webUtils.discretizeRating(0.0);
				} else if (state == de.d3web.core.knowledge.terminology.Rating.State.UNCLEAR) {
					ratingClass = "remove";
				}

				// add to the result
				sb.append(IDUtils.getNamespacedId(source.getId(), "question"))
						.append(",").append(ratingClass).append(";");
			}
		}

		// children
		for (TerminologyObject child : source.getChildren()) {
			buildRatings(sb, child, d3webSession);
		}
	}

	private Session createNewSession(HttpSession httpSession,
			String kbFilename) {
		Session d3webSession;
		// create new session
		// KnowledgeBase kb = null;
		// if (kbFilename.equals("manual.jar")) {
			// kb = KnowledgeBaseCreator.createQuestionnaireIndicationKB();
			// d3webSession = D3webUtils.createSession(kb, null);
		// } else {
		// kb = D3webUtils.getKnowledgeBase(kbFilename);
		// }
		d3webSession = D3webUtils.createSession(kbFilename);
		// store to session
		httpSession.setAttribute("d3webSession", d3webSession);
		httpSession.setAttribute("kbName", kbFilename);
		return d3webSession;
	}


	private void getChildren(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,
			Session d3webSession) {
		String parentID = request.getParameter("pid");
		if (parentID == null) {
			return;
		}
		TerminologyObject parent = D3webUtils.getTerminologyObjectByID(
				d3webSession, IDUtils.removeNamspace(parentID));
		if (parent == null) {
			return;
		}

		// wait for tree to be in session
		Object tree = null;
		while ((tree = httpSession.getAttribute("dialogTree")) == null) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		IDialogObject formDialogObject = D3webConversionUtils.getDialogObject(
				d3webSession, (InterviewObject) parent,
				((DialogTree) tree).getRoot());
		if (formDialogObject != null) {
			try {
				for (IDialogObject child : formDialogObject.getChildren()) {
					renderObject(httpSession, response.getWriter(), child);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void nextForm(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,
			Session d3webSession) throws IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();

		// get the next interview object that needs to be processed
		InterviewObject nextForm =
				d3webSession.getInterview().nextForm().getInterviewObject();

		// a bit of caching, so that we can reduce the number of reloads
		// only reload if nextForm not null and if lastForm wasn't null
		// and if lastForm not equal to actual form
		if (nextForm != null) {
			if (httpSession.getAttribute("lastForm") == null
					|| nextForm.hashCode() != (Integer) httpSession
							.getAttribute("lastForm")) {

				// wait for tree to be loaded in the HTTP session
				Object tree = null;
				while ((tree = httpSession.getAttribute("dialogTree")) == null) {
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// Retrieves the next IDialogObject recursively, e.g., if its a
				// questionnaire, it is retrieved including also its children
				IDialogObject formDialogObject =
						D3webConversionUtils.getDialogObject(
								d3webSession, nextForm, ((DialogTree) tree).getRoot());
				if (formDialogObject != null) {

					// render if there is something to render
					renderObject(httpSession, writer, formDialogObject);
				}

				httpSession.setAttribute("lastForm", nextForm.hashCode());
			} else {
				// to be able to tell when the same form has been retrieved
				writer.append("same");
			}
		}
		writer.close();
	}

	/**
	 * Render an IDialogOBject
	 * 
	 * @created 13.10.2010
	 * @param httpSession
	 * @param writer
	 * @param dialogObject
	 */
	private void renderObject(HttpSession httpSession, PrintWriter writer,
			IDialogObject dialogObject) {

		if (dialogObject == null) {
			return;
		}

		// get the appropriate renderer and render the DialogObect
		// (and recursively its children)
		IRenderer rootRenderer = Renderer.getRenderer(dialogObject);
		ContainerCollection cc = new ContainerCollection();
		String html =
				rootRenderer.renderDialogObject(cc, dialogObject, false, true);
		writer.append(html);
	}

	private void selectQuestionnaire(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,
			Session d3webSession) {
		TerminologyObject questionnaire = D3webUtils
				.getTerminologyObjectByID(d3webSession,
						request.getParameter("qid"));
		if (questionnaire != null) {
			Fact fact = new DefaultFact(questionnaire, new Indication(
					State.INSTANT_INDICATED),
					PSMethodUserSelected.getInstance(),
					PSMethodUserSelected.getInstance());
			d3webSession.getBlackboard().addInterviewFact(fact);
		}
	}

	private void show(HttpServletRequest request, HttpServletResponse response,
			HttpSession httpSession, Session d3webSession) throws IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("utf8");
		PrintWriter writer = response.getWriter();
		DialogTree tree = null;

		dialogStrat = d3webParser.getStrategy();
		dialogType = d3webParser.getType();
		css = d3webParser.getCss();

		// TODO refactor this one!!
		if (dialogType.equals(DialogType.MULTFORM)) {
			// initialize the Dialog and tell it that it's d3web
			Dialog rootDialog = new Dialog();
			rootDialog.setD3web(true);

			if (request.getParameter("css") != null) {
				css = request.getParameter("css");
			}

			String type = "boxes"; // default
			if (request.getParameter("type") != null) {
				type = request.getParameter("type");
			}

			rootDialog.setCss(css);
			rootDialog.setType(type);
			rootDialog.setSubType(request.getParameter("subtype"));
			rootDialog.getInheritableAttributes().setSendButton(
					Boolean.parseBoolean(request.getParameter("send-button")));
			rootDialog.setId("rootDialog");

			// get and save tree
			tree = new DialogTree();
			tree.setRoot(rootDialog);
			rootDialog.setD3webDialogStrategy(dialogStrat);

		}
		else if (dialogType.equals(DialogType.SINGLEFORM)) {
			tree = D3webConversionUtils.getEntireTreeFromKB(kb, d3webSession);
			tree.getRoot().setCss(css);
			((D3webDialog) tree.getRoot()).setD3web(true);
			System.out.println("TEST: " + tree.getRoot().getVirtualClassName());
		}

		httpSession.setAttribute("dialogTree", tree);

		ContainerCollection cc = new ContainerCollection();
		cc.js.enableD3Web();

		IRenderer rootRenderer = Renderer.getRenderer(tree.getRoot());

		rootRenderer.renderRootD3web(tree, cc, d3webSession);
		
		// deliver the rendered output
		writer.print(cc.html.toString());
		writer.close();
	}

	private void solutions(HttpServletRequest request,
			HttpServletResponse response, HttpSession httpSession,
			Session d3webSession) throws IOException {
		response.setCharacterEncoding("utf8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter writer = response.getWriter();

		Number minRating = Float.MIN_VALUE;
		try {
			minRating = Float.parseFloat(request.getParameter("minRating"));
		} catch (Exception e) {

		}
		int maxCount = Integer.MAX_VALUE;
		try {
			maxCount = Integer.parseInt(request.getParameter("maxCount"));
		} catch (Exception e) {

		}
		List<Solution> solutions = D3webUtils.getBestSolutions(
				d3webSession, minRating, maxCount);

		try {
			writer = response.getWriter();
			StringTemplate templateBox = new StringTemplate(
					FileUtils.getString(
							FileUtils.getResourceFile(
									GlobalSettings.getInstance().getHtmlTempPath()
											+ "/SolutionBox.st")));
			StringTemplate templateItem = new StringTemplate(
					FileUtils.getString(
							FileUtils.getResourceFile(
									GlobalSettings.getInstance().getHtmlTempPath()
											+ "/SolutionItem.st")));
			StringBuilder sb = new StringBuilder();
			if (solutions.size() > 0) {
				for (Solution solution : solutions) {
					templateItem.setAttribute("title", solution.getName());
					templateItem.setAttribute("value", d3webSession
							.getBlackboard().getRating(solution).getValue());
					sb.append(templateItem.toString());
					templateItem.reset();
				}
				templateBox.setAttribute("solutions", sb.toString());
			}
			writer.append(templateBox.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}