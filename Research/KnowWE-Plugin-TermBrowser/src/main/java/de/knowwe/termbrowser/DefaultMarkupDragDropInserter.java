package de.knowwe.termbrowser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.parsing.Sections.ReplaceResult;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;

public abstract class DefaultMarkupDragDropInserter implements DragDropEditInserter {

	public static final String LINE_BREAK = "\r\n";

	@Override
	public String insert(Section<?> s, String droppedTerm, String relationKind, UserActionContext context) throws IOException {
		/*
		 * select subsection that should be replaced with dropped text (might be
		 * the passed section s or an ancestor of it)
		 */
		Section<?> sectionToBeReplaced = findSectionToBeReplaced(s);

		/*
		 * create new text for the selected subsection
		 */
		String replaceSectionReplaceText = createReplaceTextForSelectedSection(sectionToBeReplaced,
				droppedTerm);

		/*
		 * replace entire defaultMarkup section and recompile...
		 */
		Section<DefaultMarkupType> defaultMarkupSection =
				Sections.findAncestorOfType(s,
						DefaultMarkupType.class);
		String defaultMarkupReplaceText =
				createDefaultMarkupReplaceText(defaultMarkupSection,
						sectionToBeReplaced,
						replaceSectionReplaceText);

		Map<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(defaultMarkupSection.getID(), defaultMarkupReplaceText);
		String result = "done";

		ReplaceResult replaceResult = Sections.replaceSections(context, nodesMap);
		replaceResult.sendErrors(context);
		Map<String, String> newSectionIDs = replaceResult.getSectionMapping();
		if (newSectionIDs != null && newSectionIDs.size() > 0) {
			Entry<String, String> entry =
					newSectionIDs.entrySet().iterator().next();
			result = entry.getKey() + "#" + entry.getValue();
		}

		// hotfix: workaround to trigger update of the sectionID map
		DelegateRenderer.getInstance().render(s, context,
				new RenderResult(context));

		return result;
	}

	protected abstract Section<?> findSectionToBeReplaced(Section<?> s);

	private String createDefaultMarkupReplaceText(Section<DefaultMarkupType> defaultMarkupSection, Section<?> toBeReplaced, String replacement) {
		Map<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(toBeReplaced.getID(), replacement);
		StringBuffer replacedText = Sections.collectTextAndReplaceNode(defaultMarkupSection,
				nodesMap);
		return replacedText.toString();
	}

	protected abstract String createReplaceTextForSelectedSection(Section<?> section, String dropText);

}
