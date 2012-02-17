package de.knowwe.compile.support;

import de.knowwe.compile.ImportManager;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

/**
 * 
 * 
 * @author Stefan Mark
 * @created 15.12.2011
 */
public class ImportedConceptPageLinkProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {

		if (section.get() instanceof SimpleReference) {

			boolean isImported = IncrementalCompiler.getInstance().getTerminology().isImportedObject(
					section.getText());

			if (isImported) {
				Section<? extends AbstractType> importLocation = ImportManager.resolveImportSection(section.getText());

				if (importLocation != null) {
					return new Tool[] { getConceptInfoPageTool(importLocation) };
				}
				return new Tool[] {};
			}
		}
		return new Tool[] {};
	}

	protected Tool getConceptInfoPageTool(Section<? extends AbstractType> section) {
		String articleName = section.getArticle().getTitle();
		String jsAction = "window.location.href = " +
				"'Wiki.jsp?page=" + articleName + "'";
		return new DefaultTool(
				"KnowWEExtension/d3web/icon/infoPage16.png",
				"Jump to Import",
				"Opens the import page for the concept.",
				jsAction);
	}
}
