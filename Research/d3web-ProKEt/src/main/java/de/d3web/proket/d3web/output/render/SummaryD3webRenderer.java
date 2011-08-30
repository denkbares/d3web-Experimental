package de.d3web.proket.d3web.output.render;

import de.d3web.core.knowledge.TerminologyObject;
import de.d3web.core.knowledge.ValueObject;
import de.d3web.core.knowledge.terminology.QContainer;
import de.d3web.core.knowledge.terminology.Question;
import de.d3web.core.session.Session;
import de.d3web.core.session.Value;
import de.d3web.core.session.values.UndefinedValue;
import de.d3web.proket.d3web.input.D3webConnector;

public class SummaryD3webRenderer extends AbstractD3webRenderer {

	public enum SummaryType {
		QUESTIONNAIRE,
		GRID
	}

	public String renderSummaryDialog(Session d3webSession, SummaryType type) {

		StringBuilder bui = new StringBuilder();
		D3webConnector d3wcon = D3webConnector.getInstance();

		TerminologyObject root = d3wcon.getKb().getRootQASet();

		fillSummaryChildren(d3webSession, bui, root);

		return bui.toString();
	}

	private void fillSummaryChildren(Session d3webSession, StringBuilder bui, TerminologyObject to) {

		if (to instanceof QContainer && !to.getName().contains("Q000")) {
			bui.append("<div style='margin-top:10px;'><b>" + D3webConnector.getInstance().getID(to)
					+ " " + to.getName()
					+ "</b></div>\n");
		}
		else if (to instanceof Question) {
			Value val = d3webSession.getBlackboard().getValue((ValueObject) to);
			if (val != null && UndefinedValue.isNotUndefinedValue(val)) {
				bui.append("<div style='margin-left:10px;'>"
						+ D3webConnector.getInstance().getID(to) + " " + to.getName()
						+ " -- " + val + "</div>\n");
			}
		}

		if (to.getChildren() != null && to.getChildren().length != 0) {
			for (TerminologyObject toc : to.getChildren()) {
				fillSummaryChildren(d3webSession, bui, toc);
			}
		}

	}

}
