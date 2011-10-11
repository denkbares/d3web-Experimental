package de.d3web.we.diaFlux.refactoring;

import java.io.IOException;

import de.d3web.we.flow.type.DiaFluxType;
import de.knowwe.core.KnowWEAttributes;
import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.compile.packaging.KnowWEPackageManager;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

/**
 * Returns the package name of a flowchart.
 * 
 * @author Ralf Oechsner
 * @created Feb 28, 2011
 */
public class GetPackageName extends AbstractAction {

	@Override
	@SuppressWarnings("unchecked")
	public void execute(UserActionContext context) throws IOException {

		// get parameters
		String nodeID = context.getParameter(KnowWEAttributes.TARGET);

		Section<DiaFluxType> diaFluxSection = (Section<DiaFluxType>) Sections.getSection(
				nodeID);
		String packageName = DefaultMarkupType.getAnnotation(diaFluxSection,
				KnowWEPackageManager.ATTRIBUTE_NAME);

		context.setContentType("text/plain; charset=UTF-8");
		context.getWriter().write(packageName != null ? packageName : "#undefined#");
	}
}
