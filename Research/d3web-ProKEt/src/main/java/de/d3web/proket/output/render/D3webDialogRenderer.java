package de.d3web.proket.output.render;

import java.util.List;

import org.antlr.stringtemplate.StringTemplate;

import de.d3web.core.knowledge.Indication;
import de.d3web.core.knowledge.Indication.State;
import de.d3web.core.knowledge.InterviewObject;
import de.d3web.core.knowledge.KnowledgeBase;
import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.Choice;
import de.d3web.core.knowledge.terminology.QASet;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.QuestionDate;
import de.d3web.core.knowledge.terminology.QuestionMC;
import de.d3web.core.knowledge.terminology.QuestionNum;
import de.d3web.core.knowledge.terminology.QuestionOC;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.data.D3webDialog;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.input.d3web.D3webConnector;
import de.d3web.proket.input.defaultsettings.GlobalSettings;
import de.d3web.proket.input.xml.IDialogObjectParser;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Custom Renderer Class for d3web-based dialogs
 * 
 * @author Martina Freiberg
 * @created 14.10.2010
 */
public class D3webDialogRenderer extends Renderer {

	/**
	 * Get the renderer appropriate for a given dialogObject
	 * 
	 * @created 14.10.2010
	 * @param dialogObject
	 * @return the Renderer that fits best
	 */
	public static IRenderer getRenderer(IDialogObjectParser dialogObject) {
		IRenderer renderer =
				(IRenderer) ClassUtils.getBestObject(
						dialogObject,
						GlobalSettings.getInstance().getRendererBasePath(),
						"Renderer");
		return renderer;
	}

	// TODO enable the more customized JS here that handles d3web cases
	protected void globalJS(de.d3web.proket.output.container.ContainerCollection cc) {

		cc.js.enableD3Web();

		cc.js.add("$(function() {init_all();});", 1);
		cc.js.add("function init_all() {", 1);
		cc.js.add("building = true;", 2);
		//cc.js.add("setup();", 2);
		cc.js.add("building = false;", 2);
		// cc.js.add("remark();", 2);
		cc.js.add("}", 31);
	}

	/**
	 * Recursively build the navigation tree from the d3web structure.
	 * 
	 * @param HttpSession Session containing the {@link KnowledgeBase} object.
	 * @param st StringTemplate to add the data to.
	 */
	private void makeD3webNavigation(TerminologyObject startElement,
			StringTemplate st) {
		TerminologyObject[] children = startElement.getChildren();
		for (TerminologyObject child : children) {
			if (child instanceof QASet) {
				// create subItem
				StringTemplate childSt = TemplateUtils.getStringTemplate(
						"NavigationItem", "html");
				// set title and link
				if (child.getName() != null) {
					childSt.setAttribute("title", child.getName());
				}
				else {
					childSt.setAttribute("title", child.getId());
				}
				childSt.setAttribute("fullId", child.getId());

				makeD3webNavigation(child, childSt);
				st.setAttribute("navlist", childSt.toString());
			}
		}
	}


	@Override
	// TODO refactor
	protected String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean excludeChildren, boolean force,
			Session session) {

		StringTemplate st =
				TemplateUtils.getStringTemplate(
				dialogObject.getVirtualClassName(), "html");

		if (st == null) {
			return null;
		}

		// fill StringTemplate with values from given dialog object
		fillTemplate(dialogObject, st);

		// fill in CSS from parsed dialog object
		handleCss(cc, dialogObject);

		// build the navigation
		if (dialogObject instanceof D3webDialog) {
			if (session != null) {
				makeD3webNavigation(session.getKnowledgeBase().getRootQASet(), st);
			}
		}

		// children
		renderChildren(st, cc, dialogObject, force);

		// some global JS goes here
		globalJS(cc);

		// fill css and js into the template
		st.setAttribute("fullcss", cc.css.generateOutput());
		st.setAttribute("fulljs", cc.js.generateOutput());

		// save to output
		return st.toString();
	}

	/**
	 * Retrieves appropriate Renderers for d3web component and writes the html
	 * in the ContainerCollection.
	 * 
	 * @created 16.10.2010
	 * @param cc the ContainerCollection of this dialog
	 * @param session the Session we are working in
	 * @param css potentially given CSS commands from the dialog XML
	 */
	public void renderD3web(ContainerCollection cc, Session session) {

		StringTemplate st = // get the basic StringTemplate that defines the
		TemplateUtils.getStringTemplate( // HTML framework
				"D3webDialog", "html");

		// assemble String for the children, i.e. questions and answers
		StringBuilder sb = new StringBuilder();
		Blackboard bb = session.getBlackboard();
		List<QContainer> containers = session.getKnowledgeBase().getQContainers();

		KnowledgeBaseManagement.createInstance(session.getKnowledgeBase()).sortQContainers(
				containers);
		for (TerminologyObject to : containers) {
			if (!to.getName().equals("Q000")) {
				sb.append(renderD3webQuestionnaire(to, bb));
			}
		}

		st.setAttribute("children", sb.toString());
		// fill StringTemplate with values from the specs XML first
		st.setAttribute("header", D3webConnector.getInstance().getHeader());
		st.setAttribute("resetimage", "reset.gif");
		handleCss(cc, D3webConnector.getInstance().getCss());

		// append dialog ending in case no further questions/questionnaires
		Form current = bb.getSession().getInterview().nextForm();
		if (current == null ||
				current.getTitle().equals("EMPTY")) {
			st.setAttribute("fin", "No further dialog elements left.");
		}

		// some global JS goes here
		globalJS(cc);

		// fill css and js into the template
		st.setAttribute("fullcss", cc.css.generateOutput());
		st.setAttribute("fulljs", cc.js.generateOutput());

		cc.html.add(st.toString());
	}

	public String renderD3webQuestionnaire(TerminologyObject to, Blackboard bb) {

		StringBuilder sb = new StringBuilder();
		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		StringTemplate st = TemplateUtils.getStringTemplate(
				"Questionnaire", "html");
		st.setAttribute("fullId", to.getId());
		st.setAttribute("title", to.getName());

		for (TerminologyObject tochild : to.getChildren()) {
			if (tochild instanceof QContainer) {
				sb.append(renderD3webQuestionnaire(tochild, bb));
			}
			else {
				sb.append(renderD3webQuestion(tochild, bb));
			}
		}

		if (bb.getSession().getKnowledgeBase().getInitQuestions().contains(to)
				|| isIndicated(to, bb)) {
			st.removeAttribute("style");
		}
		else {
			st.setAttribute("style", "display:none;");
		}

		st.setAttribute("children", sb.toString());
		return st.toString();
	}

	/**
	 * Retrieves the appropriate Renderer for a given d3web component and writes
	 * the output -- potentially considering set values from the Blackboard --
	 * to the html in the ContainerCollection.
	 * 
	 * @created 16.10.2010
	 * @param cc the ContainerCollection of this dialog
	 * @param to the InterviewObject to be rendered
	 * @param bb the Blackboard
	 */
	public String renderD3webQuestion(TerminologyObject to, Blackboard bb) {

		StringBuilder sb = new StringBuilder();
		StringBuilder children = new StringBuilder();

		// return if the InterviewObject is null
		if (to == null) {
			return "";
		}

		Form current = bb.getSession().getInterview().nextForm();
		StringTemplate st = TemplateUtils.getStringTemplate(
				"Question", "html");

		st.setAttribute("fullId", "q_" + to.getId());
		st.setAttribute("text", to.getName());
		Value val = bb.getValue((ValueObject) to);
		Indication ind = bb.getIndication((InterviewObject) to);

		if (val != null && val != Unknown.getInstance()
				&& UndefinedValue.isNotUndefinedValue(val)) {
			// this question has already been answered
			st.setAttribute("qstate", "question-d");
		}
		// currently active question (next interview object)
		else if (current.getInterviewObject().equals(to)) {
			st.setAttribute("qstate", "question-c");
		}
		// additionally indicated further questions
		else if (ind.hasState(State.INDICATED) ||
				ind.hasState(State.INSTANT_INDICATED)) {
			st.setAttribute("qstate", "question-c");
		}

		if (to instanceof QuestionOC) {
			st.setAttribute("type", "oc");
			st.setAttribute("answer-type", "oc");
			children.append(renderOCComponent(to, bb));
		}
		else if (to instanceof QuestionMC) {
			st.setAttribute("type", "mc");
			st.setAttribute("answer-type", "mc");
			children.append(renderMCComponent(to, bb));
		}
		else if (to instanceof QuestionNum) {
			st.setAttribute("type", "num");
			st.setAttribute("answer-type", "num");
			children.append(renderNumComponent(to, bb));
		}
		else if (to instanceof QuestionDate) {
			st.setAttribute("type", "date");
			st.setAttribute("answer-type", "date");
			children.append(renderDateComponent(to, bb));
		}

		for (TerminologyObject child : to.getChildren()) {
			sb.append(renderD3webQuestion(child, bb));
		}

		st.setAttribute("children", children);

		return st.toString();
	}

	/**
	 * Rendering a TerminologyObject as OC Question with a Blackboard (for
	 * getting already stored values) by filling the corresponding template
	 * accordingly.
	 * 
	 * @created 16.10.2010
	 * @param to the TerminologyObject
	 * @param bb the Blackboard
	 * @return the filled OCAnswer template
	 */
	public String renderOCComponent(TerminologyObject to, Blackboard bb) {

		// Value value = bb.getValue((ValueObject) to);

		StringBuilder answers = new StringBuilder();
		QuestionOC ocq = (QuestionOC) to;

		for (Choice c : ocq.getAllAlternatives()) {
			// get the HTML template for the dialog object
			StringTemplate st = TemplateUtils.getStringTemplate(
					"OcAnswer", "html");

			st.setAttribute("fullId", to.getId());
			st.setAttribute("realAnswerType", "oc");
			st.setAttribute("parentFullId", ocq.getId());
			st.setAttribute("text", c.getName());

			if (bb.getValue(ocq).toString().equals(c.toString())) {
				// set the selected OC as selected in the next round
				st.setAttribute("selection", "checked=\"checked\"");
			}
			else if (UndefinedValue.isUndefinedValue(bb.getValue(ocq))) {
				st.removeAttribute("selection");
				st.setAttribute("selection", "");
			}
			answers.append(st.toString());
		}

		// save to output
		return answers.toString();
	}

	/**
	 * Rendering a TerminologyObject as MC Question with a Blackboard (for
	 * getting already stored values) by filling the corresponding template
	 * accordingly.
	 * 
	 * @created 16.10.2010
	 * @param to the TerminologyObject
	 * @param bb the Blackboard
	 * @return the filled MCAnswer template
	 */
	public String renderMCComponent(TerminologyObject to, Blackboard bb) {
		// Indication ind = bb.getIndication((InterviewObject) to);
		// Value value = bb.getValue((ValueObject) to);

		StringBuilder answers = new StringBuilder();
		QuestionMC mcq = (QuestionMC) to;

		for (Choice c : mcq.getAllAlternatives()) {
			// get the HTML template for the dialog object
			StringTemplate st = TemplateUtils.getStringTemplate(
					"McAnswer", "html");

			st.setAttribute("fullId", to.getId());
			st.setAttribute("realAnswerType", "mc");
			st.setAttribute("parentFullId", mcq.getId());
			st.setAttribute("text", c.getName());

			if (bb.getValue((ValueObject) to).equals(c)) {
				st.setAttribute("selection", "true");
			}
			answers.append(st.toString());
		}

		// save to output
		return answers.toString();
	}

	/**
	 * Rendering a TerminologyObject as Num Question with a Blackboard (for
	 * getting already stored values) by filling the corresponding template
	 * accordingly.
	 * 
	 * @created 16.10.2010
	 * @param to the TerminologyObject
	 * @param bb the Blackboard
	 * @return the filled NumAnswer template
	 */
	public String renderNumComponent(TerminologyObject to, Blackboard bb) {

		// TODO indications
		StringBuilder answers = new StringBuilder();
		QuestionNum num = (QuestionNum) to;

		StringTemplate st = TemplateUtils.getStringTemplate(
					"NumAnswer", "html");
		st.setAttribute("fullId", to.getId());
		st.setAttribute("realAnswerType", "num");
		st.setAttribute("parentFullId", num.getId());

		// set units if available
		if (((QuestionNum) to).getInfoStore().getValue(BasicProperties.UNIT) != null) {
			st.setAttribute("text",
					((QuestionNum) to).getInfoStore().getValue(BasicProperties.UNIT));
		}

		if (bb.getValue((ValueObject) to) != null &&
				UndefinedValue.isNotUndefinedValue(bb.getValue((ValueObject) to))) {
			st.setAttribute("selection", bb.getValue((ValueObject) to));
		}
		else if (UndefinedValue.isUndefinedValue(bb.getValue((ValueObject) to))) {
			// don't want to have "undefined" displayed in the input field
			st.removeAttribute("selection");
			st.setAttribute("selection", "");
		}
		answers.append(st.toString());

		// save to output
		return answers.toString();
	}

	public String renderDateComponent(TerminologyObject to, Blackboard bb) {

		StringBuilder answers = new StringBuilder();

		StringTemplate st = TemplateUtils.getStringTemplate(
					"DateAnswerPure", "html");
		st.setAttribute("fullId", to.getId());
		st.setAttribute("realAnswerType", "date");
		
		Value val = bb.getValue((ValueObject) to);

		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.setAttribute("selection", bb.getValue((ValueObject) to));
		}
		else if (UndefinedValue.isUndefinedValue(val)) {
			// don't want to have "undefined" displayed in the input field
			st.removeAttribute("selection");
			st.setAttribute("selection", "");
		}

		st.setAttribute("text", "dd.mm.yyyy");
		answers.append(st.toString());


		return answers.toString();
	}

	public void handleCss(ContainerCollection cc, String css) {
		if (css != null) {
			// file reference or inline css?
			// regex prüft ob der css-String was in der Form
			// "file1, file2, file3" ist
			if (css.matches("[\\w-,\\s]*")) {
				String[] parts = css.split(",");
				for (String partCSS : parts) {

					// reference to stile sheet files
					StringTemplate stylesheet =
							// replace whitespace characters with empty string
							// and then get the corresponding css file
							TemplateUtils.getStringTemplate(partCSS.replaceAll("\\s", ""), "css");

					// if not at the end of stylesheet string
					if (stylesheet != null) {

						// assign object id to the css and write css into
						// codecontainer
						stylesheet.setAttribute("id", "#d3webdialog");
						cc.css.add(stylesheet.toString());
					}
				}
			}
			else {
				// inline: just write css command into the code container
				cc.css.addStyle(css, "#d3webdialog");
			}
		}
	}

	public boolean isIndicated(TerminologyObject to, Blackboard bb) {
		for (QASet qaSet : bb.getSession().getKnowledgeBase().getQASets()) {
			if (qaSet.getName().equals(to.getName()) &&
					bb.getIndication((InterviewObject) to).getState() == State.INDICATED) {
				return true;
			}
		}
		return false;

	}
}
