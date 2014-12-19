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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.vocabulary.OWL;
import org.ontoware.rdf2go.vocabulary.RDF;

import de.knowwe.compile.object.AbstractKnowledgeUnitType;
import de.knowwe.compile.object.TypedTermDefinition;
import de.knowwe.compile.support.Editable;
import de.knowwe.core.kdom.basicType.EndLineComment;
import de.knowwe.core.kdom.objects.SimpleDefinition;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.rdf2go.Rdf2GoCore;
import de.knowwe.rdf2go.SectionIDSource;
import de.knowwe.rdfs.rendering.PreEnvRenderer;
import de.knowwe.rdfs.util.RDFSUtil;

public class ClassDefinitionMarkup extends AbstractKnowledgeUnitType<ClassDefinitionMarkup> implements Editable {

	private static final String CLASS_REGEX = "^Class:?\\s+(.*?)(\\(.*?\\))?$";

	public ClassDefinitionMarkup() {
		this.setCompileScript(new DefineClassCompileScript());
		this.setSectionFinder(new RegexSectionFinder(CLASS_REGEX,
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE, 0));

		this.addChildType(new EndLineComment());
		this.addChildType(new ClassDef());

		this.setRenderer(new PreEnvRenderer());
	}

	class ClassDef extends AbstractIRITermDefinition implements TypedTermDefinition {

		public ClassDef() {
			this.setSectionFinder(new RegexSectionFinder(CLASS_REGEX,
					0, 1));
		}

		@Override
		public Map<String, ? extends Object> getTypedTermInformation(
				Section<? extends SimpleDefinition> s) {
			// says that IRIs created with this markup have the type 'Class'
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(RDFSTermCategory.KEY, RDFSTermCategory.Class);
			return map;
		}

	}

	class DefineClassCompileScript extends AbstractKnowledgeUnitCompileScriptRDFS<ClassDefinitionMarkup> {

		@Override
		public void insertIntoRepository(Section<ClassDefinitionMarkup> section) {

			Section<ClassDef> classTerm = Sections.successor(section, ClassDef.class);

			URI classURI = RDFSUtil.getURI(classTerm);
			Rdf2GoCore.getInstance()
					.addStatements(new SectionIDSource(section), Rdf2GoCore.getInstance().createStatement(classURI, RDF.type, OWL.Class));

		}

	}

}
