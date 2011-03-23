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
package de.knowwe.kdom.classHierarchy;

import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.InvalidKDOMSchemaModificationOperation;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.sectionFinder.AllTextSectionFinder;
import de.knowwe.core.dashtree.DashSubtree;
import de.knowwe.core.dashtree.DashTree;
import de.knowwe.core.dashtree.DashTreeElement;

public class SubClassingDashtree extends AbstractType {

	public SubClassingDashtree() {
		AbstractType subClassingDashTree = new DashTree();
		replaceRootType(subClassingDashTree);
		this.childrenTypes.add(subClassingDashTree);
		this.setSectionFinder(new AllTextSectionFinder());
	}

	private void replaceRootType(
			AbstractType subClassingDashTree) {
		List<Type> types = subClassingDashTree.getAllowedChildrenTypes();
		for (Type knowWEObjectType : types) {
			if (knowWEObjectType instanceof DashSubtree) {
				try {
					((AbstractType) knowWEObjectType).replaceChildType(
									new SubClassingDashTreeElement(),
									DashTreeElement.class);
				}
				catch (InvalidKDOMSchemaModificationOperation e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
