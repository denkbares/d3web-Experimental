package de.knowwe.ontology.turtle.edit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.d3web.strings.Strings;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.parsing.Sections.ReplaceResult;
import de.knowwe.core.kdom.rendering.DelegateRenderer;
import de.knowwe.core.kdom.rendering.RenderResult;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;
import de.knowwe.ontology.turtle.Object;
import de.knowwe.ontology.turtle.ObjectList;
import de.knowwe.ontology.turtle.Predicate;
import de.knowwe.ontology.turtle.PredicateSentence;
import de.knowwe.termbrowser.DragDropEditInserter;


public class PredicateDragDropInserter implements DragDropEditInserter<Predicate> {

	@Override
	public String insert(Section<?> s, String droppedTerm, String relationKind, UserActionContext context) throws IOException {
		Section<PredicateSentence> sentence = Sections.findAncestorOfType(s,
				PredicateSentence.class);

		Section<?> sectionToBeReplaced = sentence;

		Section<DefaultMarkupType> defaultMarkupSection =
				Sections.findAncestorOfType(s, DefaultMarkupType.class);
		if (defaultMarkupSection != null) {
			sectionToBeReplaced = defaultMarkupSection;

		}


		String[] split = droppedTerm.split("#");

		String shortURI = split[0] + ":" + Strings.encodeURL(split[1]);

		List<Section<Object>> objects = Sections.findSuccessorsOfType(sentence, Object.class);
		Map<String, String> nodesMap = new HashMap<String, String>();
		String replaceText = null;
		if (objects == null || objects.size() == 0) {
			replaceText =
					createDefaultMarkupReplaceText(defaultMarkupSection,
							sentence,
							sentence.getText() + " " + shortURI);
		}
		else {
			String predSentenceReplaceText = createReplaceTextPredicateSentence(sentence, shortURI);
			replaceText =
					createDefaultMarkupReplaceText(defaultMarkupSection,
							sentence,
							predSentenceReplaceText);
		}
		
		nodesMap.put(sectionToBeReplaced.getID(), replaceText);
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

	private String createDefaultMarkupReplaceText(Section<DefaultMarkupType> defaultMarkupSection, Section<?> toBeReplaced, String replacement) {
		Map<String, String> nodesMap = new HashMap<String, String>();
		nodesMap.put(toBeReplaced.getID(), replacement);
		StringBuffer replacedText = Sections.collectTextAndReplaceNode(defaultMarkupSection,
				nodesMap);
		return replacedText.toString();
	}


	private String createReplaceTextPredicateSentence(Section<PredicateSentence> section, String appendName) {
		List<Section<? extends Type>> children = section.getChildren();
		String result = "";
		for (Section<? extends Type> child : children) {
			if (child.get() instanceof ObjectList) {
				String appendText = ", " + appendName;
				result += child.getText() + appendText;
			}
			else {
				result += child.getText();
			}
		}
		return result;
	}

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<Predicate> getTypeClass() {
		return Predicate.class;
	}

}