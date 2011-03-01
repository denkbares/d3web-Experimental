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
package de.d3web.we.kdom.dashTree.subclassing;

import java.util.List;

import de.d3web.we.kdom.AbstractType;
import de.d3web.we.kdom.InvalidKDOMSchemaModificationOperation;
import de.d3web.we.kdom.Type;
import de.d3web.we.kdom.xml.AbstractXMLType;
import de.d3web.we.kdom.xml.XMLContent;
import de.knowwe.core.dashtree.DashSubtree;
import de.knowwe.core.dashtree.DashTree;
import de.knowwe.core.dashtree.DashTreeElement;

/**
 * @author Jochen
 * 
 *         An XML-Wrapper Type for a DashTree, that creates RDFS.subClassOf
 *         relations from the dashTree-hierarchy structure
 * 
 */
public class SubClassing extends AbstractXMLType {

	public SubClassing() {
		super("subclassing");
	}

	@Override
	protected void init() {
		this.childrenTypes.add(new SubclassingContent());
	}

	class SubclassingContent extends XMLContent {

		@Override
		protected void init() {
			AbstractType subClassingDashTree = new DashTree();
			replaceRootType(subClassingDashTree);
			this.childrenTypes.add(subClassingDashTree);

			// setting Pre-environoment-renderer
			this.setCustomRenderer(new PreRendererWithoutTilde());
		}

		private void replaceRootType(
				AbstractType subClassingDashTree) {
			List<Type> types = subClassingDashTree.getAllowedChildrenTypes();
			for (Type Type : types) {
				if (Type instanceof DashSubtree) {
					try {
						((AbstractType) Type).replaceChildType(
								new SubClassingDashTreeElement(), DashTreeElement.class);
					}
					catch (InvalidKDOMSchemaModificationOperation e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

	}

}
