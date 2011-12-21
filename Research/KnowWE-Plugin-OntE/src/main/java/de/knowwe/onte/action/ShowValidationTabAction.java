package de.knowwe.onte.action;

import java.io.IOException;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;

public class ShowValidationTabAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		StringBuilder html = new StringBuilder();

		html.append("<div id=\"onte-validation-tab\"><div class=\"onte-box\">");
		html.append("<h2>OWL 2 Profile Validation</h2>");
		html.append("<p>Validate the local ontology against below OWL2 profile</p>"

				+ "<div id='onte-options' class='onte-options'>"
				+ "    <label class='option'>"
				+ "        <p class='onte-option-label' style='float:left; display: block; width:100px;'>OWL2 profiles:</p>"
				+ getOWL2Profiles()
				+ "    </label>"
				+ "</div>"

				+ " <div class='onte-buttons onte-buttonbar'>"
				+ "    <a href='javascript:KNOWWE.plugin.onte.actions.validateOWL2Profile();void(0);' title='Validate to known OWL2 profile' class='left onte-button-txt'>Validate</a>"
				+ " </div>");
		html.append("</div><div id=\"onte-result\" class=\"onte-box\" style=\"overflow:auto; height:300px;\"></div>");
		html.append("</div>");
		context.getWriter().write(html.toString());
	}

	/**
	 * Returns an HTML select list with the possible OWL2 profiles for
	 * validation.
	 *
	 * @created 16.10.2011
	 * @return
	 */
	private String getOWL2Profiles() {

		StringBuilder options = new StringBuilder();

		options.append("<select name=\"onte-validation-tab-format\" id=\"onte-validation-tab-format\">");

		options.append("<option value='*'>Please choose a profile:</option>");
		options.append("<option value='dl'>OWL2 DL</option>");
		options.append("<option value='el'>OWL2 EL</option>");
		options.append("<option value='rl'>OWL2 RL</option>");
		options.append("<option value='ql'>OWL2 QL</option>");
		options.append("<option value='full'>OWL2 Full</option>");

		options.append("</select>");
		return options.toString();
	}

}
