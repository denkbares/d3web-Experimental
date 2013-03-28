package de.knowwe.hermes.kdom.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.knowwe.core.compile.Priority;
import de.knowwe.core.compile.terminology.TermRegistrationScope;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Article;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.AssertSingleTermDefinitionHandler;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.report.Message;
import de.knowwe.core.report.Messages;
import de.knowwe.core.utils.StringFragment;
import de.knowwe.core.utils.Strings;
import de.knowwe.hermes.TimeEvent;
import de.knowwe.hermes.TimeStamp;
import de.knowwe.hermes.kdom.event.renderer.TimeEventDateRenderer;
import de.knowwe.hermes.kdom.event.renderer.TimeEventDescRenderer;
import de.knowwe.hermes.kdom.event.renderer.TimeEventImpRenderer;
import de.knowwe.hermes.kdom.event.renderer.TimeEventRenderer;
import de.knowwe.hermes.kdom.event.renderer.TimeEventSrcRenderer;
import de.knowwe.hermes.kdom.event.renderer.TimeEventTitleRenderer;
import de.knowwe.kdom.AnonymousType;
import de.knowwe.kdom.AnonymousTypeInvisible;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.SingleChildConstraint;
import de.knowwe.rdfs.AbstractIRITermDefinition;
import de.knowwe.rdfs.tripleMarkup.TripleMarkup;

public class TimeEventNew extends AbstractType {

	private static final Description DescriptionType = new Description();
	private static final Source SourceType = new Source();
	public static final String START_TAG = "<<";
	public static final String END_TAG = ">>";

	public TimeEventNew() {

		this.sectionFinder = new RegexSectionFinder("\\r?\\n(" + START_TAG
				+ ".*?" + END_TAG + ")\\r?\\n", Pattern.DOTALL, 1);

		AnonymousType opening = new AnonymousTypeInvisible("EventStart");
		opening.setSectionFinder(new RegexSectionFinder(START_TAG));
		this.childrenTypes.add(opening);

		AnonymousType closing = new AnonymousTypeInvisible("EventEnd");
		closing.setSectionFinder(new RegexSectionFinder(END_TAG));
		this.childrenTypes.add(closing);

		ImportanceType imp = new ImportanceType();
		this.addChildType(imp);

		TitleType title = new TitleType();
		this.addChildType(title);

		DateType date = new DateType();
		this.addChildType(date);

		this.childrenTypes.add(SourceType);

		this.childrenTypes.add(DescriptionType);

		this.setRenderer(new TimeEventRenderer());

		this.addSubtreeHandler(Priority.LOW, new TimeEventOWLCompiler());
	}

	/**
	 * 
	 * Allows for quick and simple access of the TimeEvent-object which is
	 * created and stored by the TitleType
	 * 
	 * @created 08.10.2010
	 * @param s
	 * @return
	 */
	public static TimeEvent createTimeEvent(Section<TimeEventNew> s) {
		String titleS = null;
		Section<TitleType> title = Sections.findSuccessor(s, TitleType.class);
		if (title != null) titleS = title.get().getTermIdentifier(title).toString();

		String dateS = null;
		Section<DateType> date = Sections.findSuccessor(s, DateType.class);
		if (date != null) dateS = date.getText();

		Integer impI = null;
		Section<ImportanceType> imp = Sections.findSuccessor(s, ImportanceType.class);
		if (imp != null) impI = ImportanceType.getImportance(imp);

		String descS = null;
		Section<Description> desc = Sections
				.findSuccessor(s, Description.class);
		if (desc != null) descS = desc.getText();

		List<Section<Source>> sources = Sections.findChildrenOfType(s, Source.class);
		List<String> sourceStrings = new ArrayList<String>();
		for (Section<Source> src : sources) {
			sourceStrings.add(Source.getSourceName(src));
		}

		return new TimeEvent(titleS, descS, impI, sourceStrings, dateS,
				s.getID(), s.getArticle().getTitle());
	}

	public static class TitleType extends AbstractIRITermDefinition {

		Pattern newline = Pattern.compile("\\r?\\n");

		public TitleType() {
			// true says that this name is registered as globally unique term

			// renderer
			this.setRenderer(new TimeEventTitleRenderer());
			this.addSubtreeHandler(Priority.HIGH, new AssertSingleTermDefinitionHandler(
					TermRegistrationScope.GLOBAL));

			// SectionFinder for Title
			ConstraintSectionFinder cf = new ConstraintSectionFinder(new SectionFinder() {

				@Override
				public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
					Matcher matcher = newline.matcher(text);
					if (matcher.find()) { // if there is a linebreak in
						// the passed fragment take
						// everything before
						return SectionFinderResult.createSingleItemResultList(0,
								matcher.start());
					}
					else { // take everything
						return new AllTextFinderTrimmed().lookForSections(text, father,
								type);
					}

				}
			});
			cf.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = cf;
		}

		@Override
		public String getTermName(Section<? extends Term> s) {
			return s.getText().trim();
		}

	}

	public static class DateType extends AbstractType {

		public DateType() {
			this.setRenderer(new TimeEventDateRenderer());
			ConstraintSectionFinder cf = new ConstraintSectionFinder(
					new SectionFinder() {

						@Override
						public List<SectionFinderResult> lookForSections(String text, Section<?> father, Type type) {
							StringFragment firstNonEmptyLineContent = Strings.getFirstNonEmptyLineContent(text);
							if (firstNonEmptyLineContent != null) {
								return SectionFinderResult.createSingleItemResultList(
										firstNonEmptyLineContent.getStart(),
												firstNonEmptyLineContent.getEnd());
							}
							return null;
						}
					});
			cf.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = cf;

			// check for correct importance value
			this.addSubtreeHandler(new TimeEventAttributeHandler<DateType>() {

				@Override
				public Collection<Message> createAttribute(
						Article article, Section<DateType> s) {
					// TimeStamp t = DateType.getTimeStamp(s);
					// if (false /* t is invalid */) { // TODO: set appropriate
					// error
					// return Arrays.asList((KDOMReportMessage) new
					// InvalidNumberError(
					// s.get().getName()
					// + ": " + s.getOriginalText()));
					// }
					return new ArrayList<Message>(0);
				}

			});

		}

		static TimeStamp getTimeStamp(Section<DateType> s) {
			return new TimeStamp(s.getText()); // one could
			// possibly
			// cache the
			// object in
			// sectionInfoStore..
		}
	}

	public static class ImportanceType extends AbstractType {

		Pattern embracedNumbers = Pattern.compile("\\(\\s*\\d*\\s*\\)");

		// Pattern embracedNumbers = Pattern.compile("1");

		public ImportanceType() {
			this.setRenderer(new TimeEventImpRenderer());

			// SectionFinder taking the last number in brackets
			ConstraintSectionFinder cf = new ConstraintSectionFinder(
					new SectionFinder() {

						@Override
						public List<SectionFinderResult> lookForSections(
								String text, Section<?> father, Type type) {
							String[] split = text.split("\\r?\\n");
							for (int i = 0; i < split.length; i++) {
								String line = split[i];
								if (line.trim().length() > 0) {
									// take the last match of that line
									Matcher matcher = embracedNumbers
											.matcher(line);

									// of course a horrible way to access the
									// last match, but seems like there is no
									// other
									int lastMatchStart = -1;
									int lastMatchEnd = -1;
									while (matcher.find()) {
										lastMatchStart = matcher.start();
										lastMatchEnd = matcher.end();
									}
									if (lastMatchStart != -1) {
										return SectionFinderResult.createSingleItemResultList(
												lastMatchStart, lastMatchEnd);
									}
								}
							}

							return null;

						}
					});
			cf.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = cf;

			// check for correct importance value
			this.addSubtreeHandler(new TimeEventAttributeHandler<ImportanceType>() {

				@Override
				protected Collection<Message> createAttribute(Article article, Section<ImportanceType> s) {
					Integer i = ImportanceType.getImportance(s);
					if (i == null || i < 1 || i > 3) {
						return Messages.asList(Messages.invalidNumberError(
								s.get().getName() + ": " + s.getText()));
					}

					return new ArrayList<Message>(0);

				}
			});

		}

		public static Integer getImportance(Section<ImportanceType> s) {
			String number = s.getText().replaceAll("\\(", "").replaceAll("\\)",
					"").trim();
			Integer i = null;
			try {
				i = Integer.parseInt(number);
			}
			catch (Exception e) {
				// is not a valid number
			}

			return i;
		}
	}

	public static class Source extends AbstractType {

		public Source() {
			this.setRenderer(new TimeEventSrcRenderer());
			this.sectionFinder = new RegexSectionFinder("(QUELLE:.*)\\r?\\n", 9999, 1);
		}

		static String getSourceName(Section<Source> s) {
			return s.getText().substring(s.getText().indexOf(":") + 1);
		}
	}

	public static class Description extends AbstractType {

		public Description() {
			// TODO this has to be implemented without SemanticAnnotation
			// SemanticAnnotation semanticAnnotation = new SemanticAnnotation();
			//
			// // first grab annotated concepts
			// this.childrenTypes.add(semanticAnnotation);

			// then search for un-annotated concepts
			// this.childrenTypes.add(new PersonOccurrence());
			// this.childrenTypes.add(new LocationOccurrence());

			// renderer
			this.setRenderer(new TimeEventDescRenderer());

			this.addChildType(new TripleMarkup());

			ConstraintSectionFinder f = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			f.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = f;
		}
	}
}
