package de.knowwe.caseTrain;

import java.util.Collection;

import de.d3web.we.kdom.report.KDOMError;
import de.d3web.we.kdom.report.KDOMNotice;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.KDOMWarning;
import de.d3web.we.utils.KnowWEUtils;

public class Utils {

	public static void renderKDOMReportMessageBlock(Collection<? extends KDOMReportMessage> messages, StringBuilder string) {
		if (messages == null) return;
		if (messages.size() == 0) return;

		Class<? extends KDOMReportMessage> type = messages.iterator().next().getClass();
		String className = "";
		if (KDOMNotice.class.isAssignableFrom(type)) {
			className = "information";
		}
		else if (KDOMWarning.class.isAssignableFrom(type)) {
			className = "warning";
		}
		else if (KDOMError.class.isAssignableFrom(type)) {
			className = "error";
		}

		string.append(KnowWEUtils.maskHTML("<span class='" + className + "'>"));
		for (KDOMReportMessage error : messages) {
			string.append(error.getVerbalization());
			string.append("\n");
		}
		string.append(KnowWEUtils.maskHTML("</span>"));
	}

}