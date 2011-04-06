package de.d3web.we.hermes.kdom.event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.d3web.we.hermes.TimeEvent;
import de.d3web.we.hermes.TimeStamp;
import de.d3web.we.hermes.kdom.conceptMining.LocationOccurrence;
import de.d3web.we.hermes.kdom.conceptMining.PersonOccurrence;
import de.d3web.we.hermes.kdom.event.renderer.TimeEventDateRenderer;
import de.d3web.we.hermes.kdom.event.renderer.TimeEventDescRenderer;
import de.d3web.we.hermes.kdom.event.renderer.TimeEventImpRenderer;
import de.d3web.we.hermes.kdom.event.renderer.TimeEventRenderer;
import de.d3web.we.hermes.kdom.event.renderer.TimeEventSrcRenderer;
import de.d3web.we.hermes.kdom.event.renderer.TimeEventTitleRenderer;
import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.KnowWEArticle;
import de.d3web.we.kdom.Priority;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.constraint.ConstraintSectionFinder;
import de.d3web.we.kdom.constraint.SingleChildConstraint;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.StringDefinition;
import de.d3web.we.kdom.renderer.EditSectionRenderer;
import de.d3web.we.kdom.report.KDOMReportMessage;
import de.d3web.we.kdom.report.message.InvalidNumberError;
import de.d3web.we.kdom.sectionFinder.AllTextFinderTrimmed;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.kdom.type.AnonymousType;
import de.d3web.we.kdom.type.AnonymousTypeInvisible;
import de.d3web.we.utils.SplitUtility;
import de.d3web.we.utils.StringFragment;

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

		this.setCustomRenderer(new EditSectionRenderer(new TimeEventRenderer()));

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
		if (title != null)
			titleS = title.get().getTermName(title);

		String dateS = null;
		Section<DateType> date = Sections.findSuccessor(s, DateType.class);
		if (date != null)
			dateS = date.getOriginalText();

		Integer impI = null;
		Section<ImportanceType> imp = Sections.findSuccessor(s,
				ImportanceType.class);
		if (imp != null)
			impI = ImportanceType.getImportance(imp);

		String descS = null;
		Section<Description> desc = Sections
				.findSuccessor(s, Description.class);
		if (desc != null)
			descS = desc.getOriginalText();

		List<Section<Source>> sources = Sections.findChildrenOfType(s,
				Source.class);
		List<String> sourceStrings = new ArrayList<String>();
		for (Section<Source> src : sources) {
			sourceStrings.add(Source.getSourceName(src));
		}

		return new TimeEvent(titleS, descS, impI, sourceStrings, dateS,
				s.getID(), s.getArticle().getTitle());
	}

	public static class TitleType extends StringDefinition {
		Pattern newline = Pattern.compile("\\r?\\n");

		public TitleType() {
			// true says that this name is registered as globally unique term
			this.setTermScope(KnowWETerm.GLOBAL);

			// renderer
			this.setCustomRenderer(new TimeEventTitleRenderer());

			// SectionFinder for Title
			ConstraintSectionFinder cf = new ConstraintSectionFinder(
					new SectionFinder() {

						@Override
						public List<SectionFinderResult> lookForSections(
								String text, Section<?> father, Type type) {
							Matcher matcher = newline.matcher(text);
							if (matcher.find()) { // if there is a linebreak in
								// the passed fragment take
								// everything before
								return SectionFinderResult
										.createSingleItemResultList(0,
												matcher.start());
							} else { // take everything
								return new AllTextFinderTrimmed()
										.lookForSections(text, father, type);
							}

						}
					});
			cf.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = cf;
		}

		@Override
		public String getTermName(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText();
		}
	}

	public static class DateType extends AbstractType {

		public DateType() {
			this.setCustomRenderer(new TimeEventDateRenderer());
			ConstraintSectionFinder cf = new ConstraintSectionFinder(
					new SectionFinder() {

						@Override
						public List<SectionFinderResult> lookForSections(
								String text, Section<?> father, Type type) {
							StringFragment firstNonEmptyLineContent = SplitUtility
									.getFirstNonEmptyLineContent(text);
							if (firstNonEmptyLineContent != null) {
								return SectionFinderResult
										.createSingleItemResultList(
												firstNonEmptyLineContent
														.getStart(),
												firstNonEmptyLineContent
														.getEnd());
							}
							return null;
						}
					});
			cf.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = cf;

			// check for correct importance value
			this.addSubtreeHandler(new TimeEventAttributeHandler<DateType>() {

				@Override
				public Collection<KDOMReportMessage> createAttribute(
						KnowWEArticle article, Section<DateType> s) {
					// TimeStamp t = DateType.getTimeStamp(s);
					// if (false /* t is invalid */) { // TODO: set appropriate
					// error
					// return Arrays.asList((KDOMReportMessage) new
					// InvalidNumberError(
					// s.get().getName()
					// + ": " + s.getOriginalText()));
					// }
					return new ArrayList<KDOMReportMessage>(0);
				}

			});

		}

		static TimeStamp getTimeStamp(Section<DateType> s) {
			return new TimeStamp(s.getOriginalText()); // one could
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
			this.setCustomRenderer(new TimeEventImpRenderer());

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
										return SectionFinderResult
												.createSingleItemResultList(
														lastMatchStart,
														lastMatchEnd);
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
				protected Collection<KDOMReportMessage> createAttribute(
						KnowWEArticle article, Section<ImportanceType> s) {
					Integer i = ImportanceType.getImportance(s);
					if (i == null || i < 1 || i > 3) {
						return Arrays
								.asList((KDOMReportMessage) new InvalidNumberError(
										s.get().getName() + ": "
												+ s.getOriginalText()));
					}

					return new ArrayList<KDOMReportMessage>(0);

				}
			});

		}

		public static Integer getImportance(Section<ImportanceType> s) {
			String number = s.getOriginalText().replaceAll("\\(", "")
					.replaceAll("\\)", "").trim();
			Integer i = null;
			try {
				i = Integer.parseInt(number);
			} catch (Exception e) {
				// is not a valid number
			}

			return i;
		}
	}

	public static class Source extends AbstractType {
		public Source() {
			this.setCustomRenderer(new TimeEventSrcRenderer());
			this.sectionFinder = new RegexSectionFinder("(QUELLE:.*)\\r?\\n",
					9999, 1);
		}

		static String getSourceName(Section<Source> s) {
			return s.getOriginalText().substring(
					s.getOriginalText().indexOf(":") + 1);
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
			this.childrenTypes.add(new PersonOccurrence());
			this.childrenTypes.add(new LocationOccurrence());

			// renderer
			this.setCustomRenderer(new TimeEventDescRenderer());

			ConstraintSectionFinder f = new ConstraintSectionFinder(
					new AllTextFinderTrimmed());
			f.addConstraint(SingleChildConstraint.getInstance());
			this.sectionFinder = f;
		}
	}
}
