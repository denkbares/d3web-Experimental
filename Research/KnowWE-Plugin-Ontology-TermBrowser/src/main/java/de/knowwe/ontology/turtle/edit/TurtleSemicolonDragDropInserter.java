package de.knowwe.ontology.turtle.edit;

import java.util.List;

import de.d3web.strings.Strings;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.termbrowser.DefaultMarkupDragDropInserter;

public class TurtleSemicolonDragDropInserter extends DefaultMarkupDragDropInserter {

	@Override
	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Section<?> findSectionToBeReplaced(Section<?> s) {
		return s;
	}

	@Override
	protected String createReplaceTextForSelectedSection(Section<?> section, String dropText) {
		String[] split = dropText.split("#");

		String shortURI = split[0] + ":" + Strings.encodeURL(split[1]);

		return section.getText() + LINE_BREAK + shortURI + ";";
	}

}
