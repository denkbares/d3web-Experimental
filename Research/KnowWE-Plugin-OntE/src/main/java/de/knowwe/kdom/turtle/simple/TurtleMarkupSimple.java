package de.knowwe.kdom.turtle.simple;

import java.util.regex.Pattern;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.turtle.TurtleRDF2GoCompiler;
import de.knowwe.termObject.OWLTermReference;

public class TurtleMarkupSimple extends DefaultAbstractKnowWEObjectType{

	public TurtleMarkupSimple() {

		this.setSectionFinder(new RegexSectionFinder("\\{(.*?::.*?)\\}", Pattern.DOTALL,1));

		this.addChildType(new SimpleTurtlePredicate());

		this.addChildType(new SimpleTurtleSubject());

		this.addChildType(new SimpleTurtleObject());

		this.addSubtreeHandler(Priority.LOWEST, new TurtleRDF2GoCompiler());

	}

	class SimpleTurtlePredicate extends OWLTermReference {
		public SimpleTurtlePredicate (){
			this.setSectionFinder(new RegexSectionFinder("\\b([^\\s]*)::",Pattern.DOTALL, 1));
		}
	}

	class SimpleTurtleSubject extends OWLTermReference {
		public SimpleTurtleSubject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			c.addConstraint(SingleChildConstraint.getInstance());
			this.setSectionFinder(c);
		}

	}

	class SimpleTurtleObject extends OWLTermReference {
		public SimpleTurtleObject() {
			ConstraintSectionFinder c = new ConstraintSectionFinder(
					new RegexSectionFinder("::\\s(.*)",Pattern.DOTALL,1));
			c.addConstraint(SingleChildConstraint.getInstance());
			//c.addConstraint(new NonEmptyConstraint());
			this.setSectionFinder(c);
		}
	}
}
