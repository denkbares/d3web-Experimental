package de.d3web.we.diaflux.pathcoloring;

import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.diaflux.DiaFluxTraceHighlight;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;


public class HighlightProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section section, UserContext userContext) {
		Tool refresh = getHighlightTool(section, userContext);
		return new Tool[] { refresh };
	}

	protected Tool getHighlightTool(Section section, UserContext userContext) {

		boolean dohighlighting =
				DiaFluxTraceHighlight.checkForHighlight(userContext,
						AnomaliesHighlight.ANOMALIES_HIGHLIGHT);

		if (dohighlighting) {
			String jsAction = "var url = window.location.href;" +
					"if (url.search('highlight')!=-1)" +
					"{url = url.replace(/highlight=[^&]*/g, 'highlight=none');}" +
					"else {" +
					"if (url.indexOf('?') == -1) {url += '?';}" +
					"url = url.replace(/\\?/g,'?highlight=none&');}" +
					"window.location = url;";
			return new DefaultTool(
					"KnowWEExtension/flowchart/icon/debug16.png",
					"Hide Anomalies",
					"Highlights Anomalies in the flowchart.",
					jsAction);
		}
		else {
			String jsAction = "var url = window.location.href;" +
					"if (url.search('highlight')!=-1)" +
					"{url = url.replace(/highlight=[^&]*/g, 'highlight=anomalie_coverage');}" +
					"else {" +
					"if (url.indexOf('?') == -1) {url += '?';}" +
					"url = url.replace(/\\?/g,'?highlight=anomalie_coverage&');}" +
					"window.location = url;";
			return new DefaultTool(
					"KnowWEExtension/flowchart/icon/debug16.png",
					"Show Anomalies",
					"Highlights Anomalies in the flowchart.",
					jsAction);
		}
	}
}