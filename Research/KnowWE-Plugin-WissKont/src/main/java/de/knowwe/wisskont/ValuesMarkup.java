/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.ontoware.rdf2go.model.node.URI;

import de.d3web.strings.Strings;
import de.knowwe.annotation.type.list.ListObjectIdentifier;
import de.knowwe.compile.object.IncrementalTermDefinition;
import de.knowwe.compile.object.renderer.CompositeRenderer;
import de.knowwe.compile.object.renderer.ReferenceSurroundingRenderer;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.objects.Term;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.rendering.Renderer;
import de.knowwe.core.kdom.sectionFinder.AllTextFinderTrimmed;
import de.knowwe.core.utils.Types;
import de.knowwe.tools.ToolMenuDecoratingRenderer;
import de.knowwe.wisskont.util.MarkupUtils;

/**
 * 
 * @author jochenreutelshofer
 * @created 02.07.2013
 */
public class ValuesMarkup extends RelationMarkup {

	private static final String key = "Werte";

	/**
	 * 
	 */
	public ValuesMarkup() {
		super(key);

		Type contentType = Types.findSuccessorType(this, RelationMarkupContentType.class);
		boolean replaced = Types.replaceType(contentType, ListObjectIdentifier.class,
				new ValueDefinitionListElement(new OIDeleteItemRenderer()));
		if (!replaced) {
			Logger.getLogger(this.getClass()).log(Priority.ERROR, "Type NOT REPLACED ! ");
		}
	}

	@Override
	public String getName() {
		return "Werte-Bereich des Patienten";
	}

	@Override
	public boolean isInverseDir() {
		return true;
	}

	@Override
	public URI getRelationURI() {
		return createURI(SubconceptMarkup.SUBCONCEPT_PROPERTY);
	}

	class ValueDefinitionListElement extends IncrementalTermDefinition<String> {

		/**
		 * @param termObjectClass
		 */
		public ValueDefinitionListElement(Renderer r) {
			super(String.class);
			this.setSectionFinder(new AllTextFinderTrimmed());
			CompositeRenderer renderer = new CompositeRenderer(r,
					new ReferenceSurroundingRenderer());
			this.setRenderer(new ToolMenuDecoratingRenderer(renderer));
		}

		@Override
		public String getTermName(Section<? extends Term> s) {
			Section<IncrementalTermDefinition> conceptDefinition = MarkupUtils.getConceptDefinition(s);
			if (conceptDefinition == null) {
				return null; // do nothing
			}
			String valueText = s.getText().trim();
			if (valueText.length() == 0) {
				return null;
			}
			return Strings.unquote(conceptDefinition.get().getTermName(conceptDefinition) + " "
					+ s.getText().trim());
		}

	}

}
