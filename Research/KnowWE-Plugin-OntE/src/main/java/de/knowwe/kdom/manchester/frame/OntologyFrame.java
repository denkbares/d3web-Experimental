package de.knowwe.kdom.manchester.frame;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
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

		List<Type> types = new ArrayList<Type>();

		types.add(new Keyword(KEYWORD));
		types.add(new Annotations());

		types.add(new OntologyIRI());
		types.add(new OntologyVersionIRI());

		this.setKnownDescriptions(types);
	}
}

class OntologyIRI extends AbstractType {

	public OntologyIRI() {

		ConstraintSectionFinder csf = new ConstraintSectionFinder(new IRISectionFinder());
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setRenderer(new IRITypeRenderer());
	}
}

class OntologyVersionIRI extends AbstractType {

	public OntologyVersionIRI() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new IRISectionFinder());
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setRenderer(new IRITypeRenderer());
	}
}
