package de.knowwe.dataAnalysis;

import java.util.regex.Pattern;

import de.knowwe.compile.object.AbstractKnowledgeUnitCompileScript;
import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.IncrementalTermReference;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.dataAnalysis.DiscretizationMarkup.Intervalls.IntervallContent;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;

public class DiscretizationMarkup extends AbstractKnowledgeUnitType<DiscretizationMarkup> {

	public static final String patternString = "^DISCRETIZATION:?\\s*?(.*?)$";

	public DiscretizationMarkup() {
		this.setSectionFinder(new RegexSectionFinder(patternString, Pattern.MULTILINE
				| Pattern.DOTALL, 0));

		this.setCompileScript(new DiscretizationCompileScript());
		this.addChildType(new DiscretizationMarkupContent());

	}
	
	public static double[] getDiscretizationBoundaries(Section<DiscretizationMarkup> section) throws NumberFormatException {
		Section<IntervallContent> intervallContent = Sections.findSuccessor(section, IntervallContent.class);
		if (intervallContent == null) return null;
		String text = intervallContent.getText();
		String[] split = text.split(";");
		double[] numbers = new double[split.length];
		for (int i = 0; i < split.length; i++) {
			double d = Double.parseDouble(split[i].trim());
			numbers[i] = d;
		}
		return numbers;
	}

	class NumericalValueTermRef extends IncrementalTermReference {

		public NumericalValueTermRef() {
			super(String.class);
			ConstraintSectionFinder csf = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			csf.addConstraint(SingleChildConstraint.getInstance());
			// csf.addConstraint(AtMostOneFindingConstraint.getInstance());
			this.setSectionFinder(csf);
		}

	}

	class DiscretizationMarkupContent extends AbstractType {
		public DiscretizationMarkupContent() {
			this.setSectionFinder(new RegexSectionFinder(patternString, Pattern.MULTILINE
					| Pattern.DOTALL, 1));

			this.addChildType(new Intervalls());
			this.addChildType(new NumericalValueTermRef());

		}
	}

	class Intervalls extends AbstractType {
		String intervallPattern = "\\[(.*?)\\]";
		public Intervalls() {
			this.setSectionFinder(new RegexSectionFinder(intervallPattern));
			this.addChildType(new IntervallContent());
		}

		class IntervallContent extends AbstractType {
			public IntervallContent() {
				this.setSectionFinder(new RegexSectionFinder(intervallPattern, 0, 1));
			}
		}
	}

	class DiscretizationCompileScript extends AbstractKnowledgeUnitCompileScript<DiscretizationMarkup> {

		@Override
		public void insertIntoRepository(Section<DiscretizationMarkup> section) {
			String verbalization = createVerbalization(section);
			System.out.println("adding discretization knowledge: "
					+ verbalization);

		}

		private String createVerbalization(Section<DiscretizationMarkup> section) {
			String verbalization = "";
			Section<NumericalValueTermRef> term = Sections.findSuccessor(section,
					NumericalValueTermRef.class);
			if (term != null) {
				verbalization += term.get().getTermName(term) + " ";
			}
			double[] discretizationBoundaries = getDiscretizationBoundaries(section);
			if (discretizationBoundaries != null) {
				for (double d : discretizationBoundaries) {
					verbalization += " | " + d;
				}
			}
			return verbalization;
		}

		@Override
		public void deleteFromRepository(Section<DiscretizationMarkup> section) {
			String verbalization = createVerbalization(section);
			System.out.println("removing discretization knowledge: "
					+ verbalization);

		}

	}
}
