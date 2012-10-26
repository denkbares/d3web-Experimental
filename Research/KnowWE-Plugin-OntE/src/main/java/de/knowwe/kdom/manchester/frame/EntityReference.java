package de.knowwe.kdom.manchester.frame;

import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.kdom.manchester.OWLTermRef;
import de.knowwe.kdom.manchester.types.Keyword;

public class EntityReference extends AbstractType {

	public EntityReference(String keyword) {

		Pattern p = Pattern.compile(keyword + "\\p{Blank}+(.+)");
		SectionFinder sf = new RegexSectionFinder(p, 0);
		this.setSectionFinder(sf);

		Keyword key = new Keyword(keyword);
		this.addChildType(key);

		OWLTermRef owl = new OWLTermRef();
		owl.setSectionFinder(new AllTextFinderTrimmed());
		this.addChildType(owl);
	}
}
