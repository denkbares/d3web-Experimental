package de.knowwe.onte.action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.kdom.renderer.OnteRenderingUtils;

public class ShowUndefinedTermsTabAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		StringBuilder html = new StringBuilder();
		html.append("<p>The ontology contains the following undefined terms:</p>");

		// article --> errors
		Map<String, Set<Section<? extends SimpleReference>>> errors = new HashMap<String, Set<Section<? extends SimpleReference>>>();

		Map<String, Set<Section<? extends SimpleReference>>> refs = IncrementalCompiler.getInstance().getTerminology().getAllReferences();
		for (String ref : refs.keySet()) {
			if (!IncrementalCompiler.getInstance().getTerminology().isValid(ref)) {
				if (errors.containsKey(ref)) {
					errors.get(ref).addAll(refs.get(ref));
				}
				else {
					errors.put(ref, refs.get(ref));
				}
			}
		}

		for (String termidentifier : errors.keySet()) {
			html.append("<div id=\"onte-import-tab\" class=\"onte-box\" style=\"padding-bottom:3px;\">");

			html.append("<p><strong>Term: ");
			html.append(termidentifier).append("</strong></p>");
			html.append("<ul>");

			for (Section<? extends SimpleReference> section : errors.get(termidentifier)) {
				html.append("<li>").append(OnteRenderingUtils.renderHyperlink(section, false)).append(
						"</li>");
			}
			html.append("</ul></div>");
		}
		context.getWriter().write(html.toString());
	}
}