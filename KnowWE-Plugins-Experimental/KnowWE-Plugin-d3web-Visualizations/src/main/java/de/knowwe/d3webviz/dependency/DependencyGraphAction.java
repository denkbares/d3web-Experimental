/*
 * Copyright (C) 2013 University Wuerzburg, Computer Science VI
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package de.knowwe.d3webviz.dependency;

import de.d3web.core.knowledge.KnowledgeBase;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.d3webviz.AbstractD3webVizAction;
import de.knowwe.kdom.defaultMarkup.DefaultMarkupType;


/**
 * 
 * @author Reinhard Hatko
 * @created 26.02.2013
 */
public class DependencyGraphAction extends AbstractD3webVizAction {

	@Override
	protected String createOutput(KnowledgeBase kb, Section<?> section, UserActionContext context) {
		DependencyGenerator generator = new DependencyGenerator(kb);

		if (Boolean.valueOf(DefaultMarkupType.getAnnotation(section,
				D3webDependenciesType.ANNOTATION_SHOW_TYPE))) {
			generator.setShowType(true);
		}

		if (Boolean.valueOf(DefaultMarkupType.getAnnotation(section,
				D3webDependenciesType.ANNOTATION_SHOW_ALL))) {
			generator.setShowAll(true);
		}

		generator.setIgnores(DefaultMarkupType.getAnnotations(section,
				D3webDependenciesType.ANNOTATION_IGNORE));

		return generator.createDependencyGraph();
	}


}
