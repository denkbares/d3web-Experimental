package de.d3web.we.diaFlux.refactoring;

import java.io.IOException;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEAttributes;
import de.d3web.we.core.packaging.KnowWEPackageManager;
import de.d3web.we.flow.type.DiaFluxType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.defaultMarkup.DefaultMarkupType;

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
