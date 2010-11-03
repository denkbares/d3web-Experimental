package de.d3web.proket.output.render.customized;

import de.d3web.proket.data.Dialog;
import de.d3web.proket.output.container.ContainerCollection;
import de.d3web.proket.output.render.DialogRenderer;


public class RheumaDialogRenderer extends DialogRenderer {

	@Override
	protected void globalJS(ContainerCollection cc, Dialog dialog) {
		super.globalJS(cc, dialog);
		cc.js.add("showAllQuestionnaires = true;", 0);
	}
}
