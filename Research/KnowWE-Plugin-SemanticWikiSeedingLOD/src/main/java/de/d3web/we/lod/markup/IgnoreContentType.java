package de.d3web.we.lod.markup;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

public class IgnoreContentType extends DefaultAbstractKnowWEObjectType {

	public IgnoreContentType() {

		this.setSectionFinder(new RegexSectionFinder(
				"#[\\p{L}\\p{Punct} ]+ ?[\\r\\n]+[ °’\\d\\p{L}\\r\\n\\p{Punct}&&[^#]]+"));

		this.addChildType(new IgnoreChild());

		this.addChildType(new IgnoreConcept());
	}

	public class IgnoreChild extends DefaultAbstractKnowWEObjectType {

		public IgnoreChild() {
			this.setSectionFinder(new RegexSectionFinder(
					"- [\\p{L}\\p{Punct}]+ == [ °’\\d\\p{L}\\p{Punct}&&[^#]]+[\\r\\n]?"));
		}
	}

	public class IgnoreConcept extends DefaultAbstractKnowWEObjectType {

		public IgnoreConcept() {
			this.setSectionFinder(new RegexSectionFinder("#[\\p{L}\\p{Punct} ]+ ?[\\r\\n]"));
		}
	}
}
