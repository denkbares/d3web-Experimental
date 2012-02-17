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
package de.knowwe.rdfs.inspect;

import java.util.Collection;

import de.knowwe.compile.IncrementalCompiler;
import de.knowwe.core.kdom.objects.SimpleReference;
import de.knowwe.core.kdom.objects.SimpleTerm;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.user.UserContext;
import de.knowwe.rdfs.AbstractIRITermDefinition;
import de.knowwe.rdfs.IRITermRef;
import de.knowwe.rdfs.RDFSTermCategory;
import de.knowwe.rdfs.util.RDFSUtil;
import de.knowwe.tools.DefaultTool;
import de.knowwe.tools.Tool;
import de.knowwe.tools.ToolProvider;

public class DescribeIndividualLinkToolProvider implements ToolProvider {

	@Override
	public Tool[] getTools(Section<?> section, UserContext userContext) {

		if (section.get() instanceof IRITermRef) {
			Section<? extends IRITermRef> ref = ((Section<? extends IRITermRef>) section);
			if (!RDFSUtil.isTermCategory(ref, RDFSTermCategory.ObjectProperty)
					&& !RDFSUtil.isTermCategory(ref, RDFSTermCategory.Class)
					&& !RDFSUtil.isTermCategory(ref,
							RDFSTermCategory.DataTypeProperty)) {
				return new Tool[] { getDescribeIndividualTool(ref, userContext) };
			}

		}
		if (section.get() instanceof AbstractIRITermDefinition) {
			Section<? extends AbstractIRITermDefinition> def = ((Section<? extends AbstractIRITermDefinition>) section);
			Collection<Section<? extends SimpleReference>> termReferences = IncrementalCompiler.getInstance().getTerminology().getTermReferences(
					def.get().getTermIdentifier(def));
			if (termReferences != null && termReferences.size() > 0) {
				Section<? extends SimpleReference> ref = termReferences.iterator().next();
				if (!RDFSUtil.isTermCategory(ref, RDFSTermCategory.ObjectProperty)
						&& !RDFSUtil.isTermCategory(ref, RDFSTermCategory.Class)
						&& !RDFSUtil.isTermCategory(ref,
								RDFSTermCategory.DataTypeProperty)) {
					return new Tool[] { getDescribeIndividualTool(ref,
							userContext) };
				}
			}

		}
		return new Tool[] {};
	}

	protected Tool getDescribeIndividualTool(Section<? extends SimpleTerm> section, UserContext userContext) {
		String objectName = section.get().getTermIdentifier(section);

		String jsAction = "window.location.href = "
				+
				"'Wiki.jsp?page=IndividualDescription&objectname=' + encodeURIComponent('"
				+
				objectName + "')";
		return new DefaultTool(
				"KnowWEExtension/images/dt_icon_realisation.gif",
				"Describe individual",
				"Shows all asserted and derived relations for this individual.",
				jsAction);
	}
}
