package de.knowwe.jurisearch.usersupport.word;

import java.io.File;
import java.io.IOException;

import de.knowwe.core.action.ActionContext;
import de.knowwe.usersupport.poi.PoiUtils;
import de.knowwe.usersupport.servlet.UploadReceptorServlet;

public class JuriSearchUploadReceptorServlet extends UploadReceptorServlet{

	private static final long serialVersionUID = -3496523334733279679L;

	@Override
	protected void importWord(String fileName, String article,
			ActionContext context) throws IOException {
		PoiUtils.importWordFromFile(fileName, article, context, new JuriSearchHtmlDashTreeSyntaxConverter());
	}
}
