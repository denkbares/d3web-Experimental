/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.output.render;


import de.d3web.core.session.Session;
import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.output.container.ContainerCollection;

/**
 * Interface for renderers of all the DialogObjects.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public interface IRenderer {

	// render given DialogObject...
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject);

	// ...force overwriting deliberately or not...
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean force);

        // ...force overwriting deliberately or not...
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean force, boolean recurseCount);
        
	// ...and exclude the children
	public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean recurseCount, boolean excludeChildren, boolean force);

        public String renderDialogObject(ContainerCollection cc,
			IDialogObject dialogObject, boolean recurseCount, boolean excludeChildren, boolean force, Session s);
        
        

	// render root element of the dialog tree
	public ContainerCollection renderRoot(DialogTree dialogTree);

	// ...with a given container collection...
	public void renderRoot(DialogTree dialogTree, ContainerCollection cc);

	// ...and potentially excluding the children
	public void renderRoot(DialogTree dialogTree, ContainerCollection cc,
			boolean excludeChildren);

	// render root element in case of a d3web session
	public void renderRootD3web(DialogTree dialogTree, ContainerCollection cc,
			Session session);
}
