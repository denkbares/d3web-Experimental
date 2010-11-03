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
import de.d3web.core.knowledge.terminology.QuestionText;
import de.d3web.core.knowledge.terminology.info.BasicProperties;
import de.d3web.core.manage.KnowledgeBaseManagement;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.blackboard.Blackboard;
import de.d3web.core.session.interviewmanager.Form;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.core.session.values.Unknown;
import de.d3web.proket.input.d3web.D3webConnector;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.utils.TemplateUtils;

/**
 * Custom Basic Renderer Class for d3web-based dialogs. Defines the basic
 * rendering of dialog components. Subclasses for specialized rendering need to
 * set the StringTemplate properties themselves... TODO StringTemplate mechanism
 * 
 * @author Martina Freiberg
 * @created 14.10.2010
 */
public class D3webDialogRenderer {

	// one and only renderer instance
	private static D3webDialogRenderer instance;

	// the Blackboard instance
	protected static Blackboard bb;

	protected static Session session;

	protected static ContainerCollection cc;

	public static D3webDialogRenderer getInstance(Session s, ContainerCollection c) {
		if (instance == null) {
			instance = new D3webDialogRenderer();
		}
		session = s;
		bb = s.getBlackboard();
		cc = c;
		return instance;
	}

	private D3webDialogRenderer() {
	}

	/**
	 * Defines the necessary JavaScript required by this renderer, and adds it
	 * to the JS container collection.
	 * 
	 * @created 03.11.2010
	 * @param cc the ContainerCollection the JS is to be added.
	 */
	protected void defineAndAddJS(de.d3web.proket.output.container.ContainerCollection cc) {
		cc.js.enableD3Web();
		cc.js.add("$(function() {init_all();});", 1);
		cc.js.add("function init_all() {", 1);
		cc.js.add("building = true;", 2);
		cc.js.add("building = false;", 2);
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

	/**
	 * Assembles the complete HTML representation of a dialog: retrieves
	 * appropriate Renderers for the various d3web components, assembles their
	 * HTML partly representation, adds the JS and CSS stuff from the container
	 * collections, and writes the resulting entire HTML in the
	 * ContainerCollection.
	 * 
	 * @created 16.10.2010
	 * @param cc the ContainerCollection of this dialog
	 * @param session the Session we are working in
	 * @param css potentially given CSS commands from the dialog XML
	 * @return the HTML representation in the form of a String.
	 */
	public String renderD3web() {

		// TODO care for cycles etc.
		// TODO refactor organize renderers

		StringTemplate st = // get the basic StringTemplate that defines the
		TemplateUtils.getStringTemplate( // HTML framework
				"D3webDialog", "html");

		// assemble String for the children, i.e. questions and answers
		StringBuilder sb = new StringBuilder();
		List<QContainer> containers = session.getKnowledgeBase().getQContainers();

		KnowledgeBaseManagement.createInstance(session.getKnowledgeBase()).sortQContainers(
				containers);
		for (TerminologyObject to : containers) {
			if (!to.getName().equals("Q000")) {
				sb.append(renderD3webQuestionnaire(to));
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
		defineAndAddJS(cc);

		// fill css and js into the string template
		st.setAttribute("fullcss", cc.css.generateOutput());
		st.setAttribute("fulljs", cc.js.generateOutput());

		cc.html.add(st.toString());
		return cc.html.toString();
	}

	/**
	 * Basic rendering of Questionnaire components
	 * 
	 * @created 03.11.2010
	 * @param bb
	 * @return String representation of the Questionnaire
	 */
	public String renderD3webQuestionnaire(TerminologyObject to) {

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
				sb.append(renderD3webQuestionnaire(tochild));
			}
			else {
				sb.append(renderD3webQuestion(tochild));
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
	 * Retrieves the String representation of a given d3web question and returns
	 * the output {@link String} Therefore, for each question the template is
	 * filled according to the type of question. For each question type, a
	 * seperate rendering method is available and called that returns the
	 * correct rendering.
	 * 
	 * @created 16.10.2010
	 * @param to the InterviewObject to be rendered
	 */
	public String renderD3webQuestion(TerminologyObject to) {

		StringBuilder sb = new StringBuilder();
		StringBuilder children = new StringBuilder();

		if (to == null) {
			return ""; // return if the InterviewObject is null
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
			children.append(renderOCComponent(to));
		}
		else if (to instanceof QuestionMC) {
			st.setAttribute("type", "mc");
			st.setAttribute("answer-type", "mc");
			children.append(renderMCComponent(to));
		}
		else if (to instanceof QuestionNum) {
			st.setAttribute("type", "num");
			st.setAttribute("answer-type", "num");
			children.append(renderNumComponent(to));
		}
		else if (to instanceof QuestionDate) {
			st.setAttribute("type", "date");
			st.setAttribute("answer-type", "date");
			children.append(renderDateComponent(to));
		}
		else if (to instanceof QuestionText) {
			st.setAttribute("type", "text");
			st.setAttribute("answer-type", "text");
			children.append(renderTextComponent(to));
		}

		for (TerminologyObject child : to.getChildren()) {
			sb.append(renderD3webQuestion(child));
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
	public String renderOCComponent(TerminologyObject to) {

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
	public String renderMCComponent(TerminologyObject to) {
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
	public String renderNumComponent(TerminologyObject to) {

		StringBuilder answers = new StringBuilder();
		Value val = bb.getValue((ValueObject) to);

		StringTemplate st = TemplateUtils.getStringTemplate(
					"NumAnswer", "html");
		st.setAttribute("fullId", to.getId());
		st.setAttribute("realAnswerType", "num");
		st.setAttribute("parentFullId", to.getId());

		// set units if available
		if (((QuestionNum) to).getInfoStore().getValue(BasicProperties.UNIT) != null) {
			st.setAttribute("text",
					((QuestionNum) to).getInfoStore().getValue(BasicProperties.UNIT));
		}

		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.setAttribute("selection", val);
		}
		else if (UndefinedValue.isUndefinedValue(val)) {
			st.removeAttribute("selection"); // don't want to have "undefined"
			st.setAttribute("selection", ""); // displayed in the input field
		}
		answers.append(st.toString());

		return answers.toString();
	}

	/**
	 * Get String representation of text components by filling the proper
	 * StringTemplate TextAnswer with appropriate values.
	 * 
	 * @created 03.11.2010
	 * @param to The TerminologyObject corresponding to the text component
	 * @param bb
	 * @return the String representation
	 */
	public String renderTextComponent(TerminologyObject to) {

		StringBuilder answers = new StringBuilder();
		Value val = bb.getValue((ValueObject) to);

		StringTemplate st = TemplateUtils.getStringTemplate(
					"TextAnswer", "html");
		st.setAttribute("fullId", to.getId());
		st.setAttribute("realAnswerType", "text");

		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.setAttribute("selection", val);
		}
		else if (UndefinedValue.isUndefinedValue(val)) {
			st.removeAttribute("selection"); // don't want to have "undefined"
			st.setAttribute("selection", ""); // displayed in the input field
		}
		answers.append(st.toString());

		return answers.toString();
	}

	/**
	 * Get String representation for date components by filling the proper
	 * StringTemplate DateAnswerPure with appropriate values.
	 * 
	 * @created 03.11.2010
	 * @param to The TerminologyObject corresponding to the date component
	 * @param bb
	 * @return the String representation
	 */
	public String renderDateComponent(TerminologyObject to) {

		StringBuilder answers = new StringBuilder();
		Value val = bb.getValue((ValueObject) to);

		StringTemplate st = TemplateUtils.getStringTemplate(
					"DateAnswerPure", "html");
		st.setAttribute("fullId", to.getId());
		st.setAttribute("realAnswerType", "date");

		if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
			st.setAttribute("selection", bb.getValue((ValueObject) to));
		}
		else if (UndefinedValue.isUndefinedValue(val)) {
			st.removeAttribute("selection"); // don't want to have "undefined"
			st.setAttribute("selection", ""); // displayed in the input field
		}

		st.setAttribute("text", "dd.mm.yyyy");
		answers.append(st.toString());

		return answers.toString();
	}

	/**
	 * Pareses a given CSS definition and adds it appropriately to the CSS
	 * containercollection. CSS can be the definition of several files in the
	 * form "file1, file2..." - e.g. in the topmost "<dialog>" tag, or can be a
	 * single CSS command that is given for the dialog.
	 * 
	 * @created 03.11.2010
	 * @param cc the CSS containercollection
	 * @param css the CSS definition
	 */
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
				// inline: just add the style to the element with id d3webdialog
				cc.css.addStyle(css, "#d3webdialog");
			}
		}
	}


	/**
	 * Checks, whether the given TerminologyObject is indicated in the current
	 * session.
	 * 
	 * @created 03.11.2010
	 * @param to The TerminologyObject to be checked
	 * @param bb
	 * @return true, if the TerminologyObject is indicated.
	 */
	public boolean isIndicated(TerminologyObject to, Blackboard bb) {
		for (QASet qaSet : bb.getSession().getKnowledgeBase().getQASets()) {
			// find the appropriate qaset in the knowledge base
			if (qaSet.getName().equals(to.getName()) &&
					// and check its indication state
					bb.getIndication((InterviewObject) to).getState() == State.INDICATED) {
				return true;
			}
		}
		return false;
	}
}
