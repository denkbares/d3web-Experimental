package de.knowwe.dataAnalysis;

import java.util.regex.Pattern;

import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.utils.Strings;
import de.knowwe.kdom.renderer.StyleRenderer;
import de.knowwe.kdom.sectionFinder.RegexSectionFinderSingle;

public class SimpleTermDefinition extends AbstractType {

	private static final String REGEX = "^def\\s(.+)$";

	public SimpleTermDefinition() {
		this.setSectionFinder(new RegexSectionFinder(REGEX,
				Pattern.MULTILINE));

		this.addChildType(new DefType());
		this.addChildType(new DefinitionTerm());

	}

	class DefType extends AbstractType {

		public DefType() {
			this.setSectionFinder(new RegexSectionFinderSingle("^def\\s+"));
			this.setRenderer(new StyleRenderer("font-style:italic;"));
		}
	}

	class DefinitionTerm extends IncrementalTermDefinition<String> {

		public DefinitionTerm() {
			super(String.class);
			this.setSectionFinder(new AllTextFinderTrimmed());
		}

		@Override
		public String getTermName(Section<? extends SimpleTerm> s) {
			return Strings.unquote(s.getText().trim());
		}

	}
}
