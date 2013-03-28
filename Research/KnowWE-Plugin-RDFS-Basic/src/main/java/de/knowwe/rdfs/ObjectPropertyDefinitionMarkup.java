/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 * Computer Science VI, University of Wuerzburg
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package de.knowwe.rdfs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.RDFS;

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.ComplexDefinition;
import de.knowwe.compile.object.ComplexDefinitionWithTypeConstraints;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.TypeRestrictedReference;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.compile.utils.CompileUtils;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.basicType.EndLineComment;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinder;
import de.knowwe.core.kdom.sectionFinder.SectionFinderResult;
import de.knowwe.core.utils.StringFragment;
import de.knowwe.core.utils.Strings;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdfs.rendering.PreEnvRenderer;
import de.knowwe.rdfs.util.RDFSUtil;

public class ObjectPropertyDefinitionMarkup extends AbstractKnowledgeUnitType<ObjectPropertyDefinitionMarkup> implements ComplexDefinitionWithTypeConstraints {

	public static final String RDFS_DOMAIN_KEY = "rdfs:domain";
	public static final String RDFS_RANGE_KEY = "rdfs:range";

	private static final String OBJECT_PROPERTY_REGEX = "^ObjectProperty\\s+(.*?)(\\(.*?\\))?$";

	public ObjectPropertyDefinitionMarkup() {
		this.setCompileScript(new DomainRangeCompileScript());
		this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE,
				0));

		this.addChildType(new EndLineComment());
		this.addChildType(new PropertyDef());
		this.addChildType(new RangeDomainSpec());

		this.setRenderer(new PreEnvRenderer());
	}

	@Override
	public Collection<Section<SimpleReference>> getAllReferences(Section<? extends ComplexDefinition> section) {
		return CompileUtils.getAllReferencesOfComplexDefinition(section);
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
					List<StringFragment> list = Strings.splitUnquoted(text, ",");
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
		public boolean checkTypeConstraints(Section<? extends Term> s) {
			boolean termCategory = RDFSUtil.isTermCategory(s, RDFSTermCategory.Class);
			return termCategory;
		}

		@Override
		public String getMessageForConstraintViolation(Section<? extends Term> s) {
			return "Only classes are allowed here";
		}

	}

	class PropertyDef extends AbstractIRITermDefinition implements TypedTermDefinition {

		public PropertyDef() {
			this.setSectionFinder(new RegexSectionFinder(OBJECT_PROPERTY_REGEX, 0, 1));
		}

		@Override
		public Map<String, ? extends Object> getTypedTermInformation(
				Section<? extends SimpleDefinition> s) {
			Map<String, Object> map = new HashMap<String, Object>();

			// add entity type
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.ObjectProperty);

			// add domain and range information
			List<Section<ClassRef>> classRefs = Sections.findSuccessorsOfType(
					s.getFather(), ClassRef.class);
			if (classRefs.size() == 2) {
				map.put(RDFS_DOMAIN_KEY,
						classRefs.get(0).get().getTermIdentifier(classRefs.get(0)).toExternalForm());
				map.put(RDFS_RANGE_KEY,
						classRefs.get(1).get().getTermIdentifier(classRefs.get(1)).toExternalForm());

			}
			return map;
		}

	}

	@Override
	public boolean checkTypeConstraints(
			Section<? extends ComplexDefinition> def,
			Section<? extends SimpleReference> ref) {

		return RDFSUtil.isTermCategory(ref, RDFSTermCategory.Class);
	}

	@Override
	public String getProblemMessageForConstraintViolation(
			Section<? extends ComplexDefinition> def,
			Section<? extends SimpleReference> ref) {

		return "Object of type 'Class' expected";
	}

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
					section,
					propURI,
					RDFS.domain, objectURI);

			// prop range:: arg1
			Rdf2GoCore.getInstance().addStatement(
					section,
					propURI,
					RDFS.range, RDFSUtil.getURI(refs.get(1)));

		}
		else {
			// shit, what to do?
		}

	}
}
