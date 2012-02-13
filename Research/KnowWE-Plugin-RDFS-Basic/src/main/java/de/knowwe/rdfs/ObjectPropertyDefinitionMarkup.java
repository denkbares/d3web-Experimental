package de.knowwe.rdfs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.ComplexDefinitionWithTypeConstraints;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.compile.object.TypeRestrictedReference;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.EndLineComment;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.utils.SplitUtility;
import de.knowwe.core.utils.StringFragment;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.rendering.PreEnvRenderer;
import de.knowwe.rdfs.util.RDFSUtil;

public class ObjectPropertyDefinitionMarkup extends AbstractType implements ComplexDefinitionWithTypeConstraints<ObjectPropertyDefinitionMarkup>, KnowledgeUnit<ObjectPropertyDefinitionMarkup> {

	public static final String RDFS_DOMAIN_KEY = "rdfs:domain";
	public static final String RDFS_RANGE_KEY = "rdfs:range";

	private static final String OBJECT_PROPERTY_REGEX = "^ObjectProperty\\s+(.*?)(\\(.*?\\))?$";

	public ObjectPropertyDefinitionMarkup() {
		this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE,
				0));

		this.addChildType(new EndLineComment());
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

	class ClassRef extends IRITermRef implements TypeRestrictedReference {

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

		@Override
		public boolean checkTypeConstraints(Section<? extends SimpleTerm> s) {
			return RDFSUtil.isTermCategory(s, RDFSTermCategory.Class);
		}

		@Override
		public String getMessageForConstraintViolation(Section<? extends SimpleTerm> s) {
			return "Only classes are allowed here";
		}
	}

	class PropertyDef extends AbstractIRITermDefinition implements TypedTermDefinition {

		public PropertyDef() {
			this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX, 0, 1));
		}

		@Override
		public String getTermIdentifier(Section<? extends SimpleTerm> s) {
			return s.getText();
		}

		@Override
		public Map getTypedTermInformation(
				Section<? extends SimpleDefinition> s) {
			Map<String, Object> map = new HashMap<String, Object>();

			// add entity type
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.ObjectProperty);

			// add domain and range information
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
			Section<? extends SimpleReference> ref) {

		return RDFSUtil.isTermCategory(ref, RDFSTermCategory.Class);
	}

	@Override
	public String getProblemMessageForConstraintViolation(
			Section<? extends ComplexDefinition<ObjectPropertyDefinitionMarkup>> def,
			Section<? extends SimpleReference> ref) {

		return "Object of type 'Class' expected";
	}

	@Override
	public KnowledgeUnitCompileScript getCompileScript() {
		return new DomainRangeCompileScript();
	}

	class DomainRangeCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<ObjectPropertyDefinitionMarkup> {

		@Override
		public void insertIntoRepository(Section<ObjectPropertyDefinitionMarkup> section) {
			List<Section<IRITermRef>> refs = Sections.findSuccessorsOfType(section,
					IRITermRef.class);
			if (refs.size() == 2) {
				Section<IncrementalTermDefinition> propDef = Sections.findSuccessor(
						section, IncrementalTermDefinition.class);
				URI propURI = RDFSUtil.getURI(propDef);

				// prop domain:: arg0
				URI objectURI = RDFSUtil.getURI(refs.get(0));
				Rdf2GoCore.getInstance().addStatement(
						propURI,
						RDFS.domain,
						objectURI, section);

				// prop range:: arg1
				Rdf2GoCore.getInstance().addStatement(
						propURI,
						RDFS.range,
						RDFSUtil.getURI(refs.get(1)), section);

			}
			else {
				// shit, what to do?
			}

		}
	}

}
