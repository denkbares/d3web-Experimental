package de.knowwe.rdfs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.compile.test.AbstractIRITermDefinition;
import de.knowwe.rdfs.ObjectPropertyDefinitionMarkup.PropertyDef;
import de.knowwe.rdfs.ObjectPropertyDefinitionMarkup.RangeDomainSpec;

public class ClassDefinitionMarkup extends AbstractType {

	private static final String CLASS_REGEX = "^Class\\s+(.*?)(\\(.*?\\))?$";

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
			Map<String, Object> map = new HashMap<String,Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.Class);
			return map;
		}

	}

}
