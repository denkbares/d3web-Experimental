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

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.InvalidKDOMSchemaModificationOperation;
import de.knowwe.core.kdom.Type;
import de.knowwe.kdom.dashtree.DashSubtree;
import de.knowwe.kdom.dashtree.DashTree;
import de.knowwe.kdom.dashtree.DashTreeElement;
import de.knowwe.kdom.xml.AbstractXMLType;
import de.knowwe.kdom.xml.XMLContent;

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
		this.childrenTypes.add(new SubclassingContent());
	}

	class SubclassingContent extends XMLContent {

		protected SubclassingContent() {
			AbstractType subClassingDashTree = new DashTree();
			replaceRootType(subClassingDashTree);
			this.childrenTypes.add(subClassingDashTree);

			// setting Pre-environoment-renderer
			this.setRenderer(new PreRendererWithoutTilde());
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
