package de.d3web.we.lod.markup;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;

public class IgnoreContentType extends AbstractType {

	public IgnoreContentType() {

		this.setSectionFinder(new RegexSectionFinder(
				"#[\\p{L}\\p{Punct} ]+ ?[\\r\\n]+[ °’\\d\\p{L}\\r\\n\\p{Punct}&&[^#]]+"));

		this.addChildType(new IgnoreChild());

		this.addChildType(new IgnoreConcept());
	}

	public class IgnoreChild extends AbstractType {

		public IgnoreChild() {
			this.setSectionFinder(new RegexSectionFinder(
					"- [\\p{L}\\p{Punct}]+ == [ °’\\d\\p{L}\\p{Punct}&&[^#]]+[\\r\\n]?"));
		}
	}

	public class IgnoreConcept extends AbstractType {

		public IgnoreConcept() {
			this.setSectionFinder(new RegexSectionFinder("#[\\p{L}\\p{Punct} ]+ ?[\\r\\n]"));
		}
	}
}
