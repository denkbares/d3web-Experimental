package de.knowwe.kdom.manchester.frame;

import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.ExactlyOneFindingConstraint;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.manchester.ManchesterSyntaxUtil;
import de.knowwe.kdom.manchester.types.Annotations;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.renderer.IRITypeRenderer;
import de.knowwe.kdom.sectionfinder.IRISectionFinder;

/**
 *
 *
 * @author Stefan Mark
 * @created 04.10.2011
 */
public class OntologyFrame extends DefaultFrame {

	public static final String KEYWORD = "OntologyDocument[:]?";
	public static final String IRI_PATTERN = "";

	public OntologyFrame() {
		Pattern p = ManchesterSyntaxUtil.getFramePattern(KEYWORD);
		this.setSectionFinder(new RegexSectionFinder(p));

		this.addChildType(new Keyword(KEYWORD));
		this.addChildType(new Annotations());

		this.addChildType(new OntologyIRI());
		this.addChildType(new OntologyVersionIRI());
	}
}

class OntologyIRI extends AbstractType {

	public OntologyIRI() {

		ConstraintSectionFinder csf = new ConstraintSectionFinder(new IRISectionFinder());
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setCustomRenderer(new IRITypeRenderer<OntologyIRI>());
	}
}

class OntologyVersionIRI extends AbstractType {

	public OntologyVersionIRI() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new IRISectionFinder());
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setCustomRenderer(new IRITypeRenderer<OntologyVersionIRI>());
	}
}
