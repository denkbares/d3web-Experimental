package de.knowwe.rdfs;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.Section;
import de.d3web.we.kdom.Sections;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.objects.KnowWETerm;
import de.d3web.we.kdom.objects.TermDefinition;
import de.d3web.we.kdom.objects.TermReference;
import de.d3web.we.kdom.sectionFinder.RegexSectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinder;
import de.d3web.we.kdom.sectionFinder.SectionFinderResult;
import de.d3web.we.utils.SplitUtility;
import de.d3web.we.utils.StringFragment;
import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.ComplexDefinitionWithTypeConstraints;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.compile.test.AbstractIRITermDefinition;
import de.knowwe.compile.test.IRITermRef;

public class ObjectPropertyDefinitionMarkup extends AbstractType implements ComplexDefinitionWithTypeConstraints<ObjectPropertyDefinitionMarkup> {

	
	public static final String RDFS_DOMAIN_KEY = "rdfs:domain";
	public static final String RDFS_RANGE_KEY = "rdfs:range";
	
	private static final String OBJECT_PROPERTY_REGEX = "^ObjectProperty\\s+(.*?)(\\(.*?\\))?$";

	public ObjectPropertyDefinitionMarkup() {
		this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE,
				0));
		
		this.addChildType(new PropertyDef());
		this.addChildType(new RangeDomainSpec());
	}
	
	
	class RangeDomainSpec extends AbstractType {
		public RangeDomainSpec() {
			this.setSectionFinder(new RegexSectionFinder("\\((.*?)\\)",0,1));
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
						result.add(new SectionFinderResult(stringFragment.getStartTrimmed(), stringFragment.getEndTrimmed()));
					}
					return result;
				}
			});
		}
	}
	
	class PropertyDef extends AbstractIRITermDefinition implements TypedTermDefinition {
		
		public PropertyDef() {
			this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX,0,1));
		}

		@Override
		public String getTermIdentifier(Section<? extends KnowWETerm<String>> s) {
			return s.getOriginalText();
		}

		@Override
		public Map getTypedTermInformation(
				Section<? extends TermDefinition> s) {
			Map<String, Object> map = new HashMap<String,Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.ObjectProperty);
			List<Section<ClassRef>> classRefs = Sections.findSuccessorsOfType(s.getFather(), ClassRef.class);
			if(classRefs.size() == 2) {
				map.put(RDFS_DOMAIN_KEY,classRefs.get(0).get().getTermIdentifier(classRefs.get(0)));
				map.put(RDFS_RANGE_KEY,classRefs.get(1).get().getTermIdentifier(classRefs.get(1)));
				
			}
			return map;
		}


		
	}

	@Override
	public boolean checkTypeConstraints(
			Section<? extends ComplexDefinition<ObjectPropertyDefinitionMarkup>> def,
			Section<? extends TermReference> ref) {
		Object info = IncrementalCompiler.getInstance().getTerminology().getDefinitionInformationForValidTerm(ref.get().getTermIdentifier(ref));
		if(info != null && info instanceof RDFSTermCategory && info.equals(RDFSTermCategory.Class)) {
			return true;
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
