package de.knowwe.rdfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.ComplexDefinitionWithTypeConstraints;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.KnowWETerm;
import de.knowwe.core.kdom.objects.TermDefinition;
import de.knowwe.core.kdom.objects.TermReference;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.core.utils.StringFragment;
import de.knowwe.rdfs.rendering.PreEnvRenderer;

public class ObjectPropertyDefinitionMarkup extends AbstractType implements ComplexDefinitionWithTypeConstraints<ObjectPropertyDefinitionMarkup> {

	public static final String RDFS_DOMAIN_KEY = "rdfs:domain";
	public static final String RDFS_RANGE_KEY = "rdfs:range";

	private static final String OBJECT_PROPERTY_REGEX = "^ObjectProperty\\s+(.*?)(\\(.*?\\))?$";

	public ObjectPropertyDefinitionMarkup() {
		this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE,
				0));

		this.addChildType(new PropertyDef());
		this.addChildType(new RangeDomainSpec());

		this.setCustomRenderer(new PreEnvRenderer());
	}

	class RangeDomainSpec extends AbstractType {
		public RangeDomainSpec() {
			this.setSectionFinder(new RegexSectionFinder("\\((.*?)\\)", 0, 1));
			this.addChildType(new ClassRef());
		}
	}

	class ClassRef extends IRITermRef {
		public ClassRef() {
			this.setSectionFinder(new SectionFinder() {

				@Override
				public List<SectionFinderResult> lookForSections(String text,
						Section<?> father, Type type) {
					List<SectionFinderResult> result = new ArrayList<SectionFinderResult>();
					List<StringFragment> list = SplitUtility.splitUnquoted(text, ",");
					for (StringFragment stringFragment : list) {
						result.add(new SectionFinderResult(
								stringFragment.getStartTrimmed(),
								stringFragment.getEndTrimmed()));
					}
					return result;
				}
			});
		}
	}

	class PropertyDef extends AbstractIRITermDefinition implements TypedTermDefinition {

		public PropertyDef() {
			this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX, 0, 1));
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText();
		}

		@Override
		public Map getTypedTermInformation(
				Section<? extends TermDefinition> s) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.ObjectProperty);
			List<Section<ClassRef>> classRefs = Sections.findSuccessorsOfType(
					s.getFather(), ClassRef.class);
			if (classRefs.size() == 2) {
				map.put(RDFS_DOMAIN_KEY,
						classRefs.get(0).get().getTermIdentifier(classRefs.get(0)));
				map.put(RDFS_RANGE_KEY,
						classRefs.get(1).get().getTermIdentifier(classRefs.get(1)));

			}
			return map;
		}

	}

	@Override
	public boolean checkTypeConstraints(
			Section<? extends ComplexDefinition<ObjectPropertyDefinitionMarkup>> def,
			Section<? extends TermReference> ref) {
		Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(
				ref.get().getTermIdentifier(ref));
		if (info != null) {
			if (info instanceof RDFSTermCategory) {
				if (info.equals(RDFSTermCategory.Class)) {
					return true;
				}
			}
			else {
				if (info instanceof Map) {
					Set keyset = ((Map) info).keySet();
					for (Object key : keyset) {
						if (((Map) info).get(key) instanceof RDFSTermCategory) {
							RDFSTermCategory rdfsTermCategory = (RDFSTermCategory) ((Map) info).get(key);
							if (rdfsTermCategory.equals(RDFSTermCategory.Class)) {
								return true;
							}
						}
					}
				}
			}

		}
		return false;
	}

	@Override
	public String getProblemMessageForConstraintViolation(
			Section<? extends ComplexDefinition<ObjectPropertyDefinitionMarkup>> def,
			Section<? extends TermReference> ref) {

		return "Object of type 'Class' expected";
	}

}
