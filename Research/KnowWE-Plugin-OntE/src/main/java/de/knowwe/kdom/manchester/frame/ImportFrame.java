/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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
package de.knowwe.kdom.manchester.frame;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.compile.object.KnowledgeUnit;
import de.knowwe.compile.object.KnowledgeUnitCompileScript;
import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;
import de.knowwe.core.kdom.sectionFinder.RegexSectionFinder;
import de.knowwe.kdom.constraint.ConstraintSectionFinder;
import de.knowwe.kdom.constraint.ExactlyOneFindingConstraint;
import de.knowwe.kdom.manchester.compile.ImportFrameCompileScript;
import de.knowwe.kdom.manchester.types.Keyword;
import de.knowwe.kdom.renderer.IRITypeRenderer;
import de.knowwe.kdom.sectionfinder.IRISectionFinder;

/**
 * <p>
 * Simple {@link AbstractType} for prefixes in the Manchester OWL syntax.
 * </p>
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
public class ImportFrame extends DefaultFrame implements KnowledgeUnit<ImportFrame> {

	public static final String KEYWORD = "Import:";

	/**
	 * Constructor for the {@link ImportFrame}. Adds the necessary
	 * {@link AbstractType}s needed for correct mapping in the KDOM of KnowWE.
	 */
	public ImportFrame() {

		super(KEYWORD);

		List<Type> types = new ArrayList<Type>();

		types.add(new Keyword(KEYWORD));
		types.add(new ImportPrefix());
		types.add(new ImportIRI());

		this.setKnownDescriptions(types);
	}

	/**
	 * Returns the {@link ImportIRI} section containing the name of the to
	 * define OWLClass.
	 * 
	 * @created 27.09.2011
	 * @param Section<ImportFrame> section
	 * @return The found section
	 */
	public boolean hasImportIRI(Section<? extends ImportFrame> section) {
		return Sections.findSuccessor(section, ImportIRI.class) != null;
	}

	/**
	 * Returns the {@link ImportIRI} section containing the name of the to
	 * define OWLClass.
	 * 
	 * @created 27.09.2011
	 * @param Section<ImportFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getImportIRI(Section<? extends ImportFrame> section) {
		return Sections.findSuccessor(section, ImportIRI.class);
	}

	/**
	 * Returns the {@link ImportPrefix} section containing a optional namespace
	 * prefix used for this import.
	 * 
	 * @created 27.09.2011
	 * @param Section<ImportFrame> section
	 * @return The found section
	 */
	public boolean hasPrefix(Section<? extends ImportFrame> section) {
		return Sections.findSuccessor(section, ImportPrefix.class) != null;
	}

	/**
	 * Returns the {@link ImportPrefix} section containing the name of the
	 * optional namespace shortcut.
	 * 
	 * @created 27.09.2011
	 * @param Section<ImportFrame> section
	 * @return The found section
	 */
	public Section<? extends Type> getPrefix(Section<? extends ImportFrame> section) {
		return Sections.findSuccessor(section, ImportPrefix.class);
	}

	@Override
	public KnowledgeUnitCompileScript<ImportFrame> getCompileScript() {
		return new ImportFrameCompileScript();
	}
}

/**
 * 
 * 
 * @author Stefan Mark
 * @created 22.09.2011
 */
class ImportIRI extends AbstractType {

	public ImportIRI() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new IRISectionFinder());
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());

		this.setSectionFinder(csf);
		this.setRenderer(new IRITypeRenderer());
	}
}

/**
 * 
 * 
 * @author Stefan Mark
 * @created 05.12.2011
 */
class ImportPrefix extends AbstractType {

	public static final String PATTERN = "(:[A-Za-z]+)";

	public ImportPrefix() {
		ConstraintSectionFinder csf = new ConstraintSectionFinder(new RegexSectionFinder(PATTERN));
		csf.addConstraint(ExactlyOneFindingConstraint.getInstance());
		this.setSectionFinder(csf);
	}
}