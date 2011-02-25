package de.d3web.proket.output.render;

import de.d3web.proket.data.Dialog;
import de.d3web.proket.output.container.ContainerCollection;


public class FreechoiceDialogRenderer extends DialogRenderer {

	@Override
	protected void globalJS(ContainerCollection cc, Dialog dialog) {
		super.globalJS(cc, dialog);
		cc.js.add("showAllQuestionnaires = true;", 0);
	}
}
