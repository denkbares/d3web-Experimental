package de.knowwe.rdfs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;

public class ClassDefinitionMarkup extends AbstractType {

	private static final String CLASS_REGEX = "^Class:?\\s+(.*?)(\\(.*?\\))?$";

	public ClassDefinitionMarkup() {
		this.setSectionFinder(new RegexSectionFinder(CLASS_REGEX,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE, 0));

		this.addChildType(new ClassDef());
	}

	class ClassDef extends AbstractIRITermDefinition implements TypedTermDefinition {

		public ClassDef() {
			this.setSectionFinder(new RegexSectionFinder(CLASS_REGEX,
					0, 1));
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText();
		}

		@Override
		public Map<String, ? extends Object> getTypedTermInformation(
				Section<? extends TermDefinition> s) {
			// says that IRIs created with this markup have the type 'Class'
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.Class);
			return map;
		}

	}

}
