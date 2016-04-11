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
package de.knowwe.ophtovisD3;

import java.io.IOException;
import java.util.List;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.ophtovisD3.utils.JsonFactory;


/**
 * 
 * @author adm_rieder
 * @created 21.11.2013
 */
public class BreadcrumbAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		GraphBuilder builder = new GraphBuilder();
		String type, concept = "";
		type = context.getParameter("type");
		concept = context.getParameter("concept");

		if (type == null) type = "";
		String responseString = "";
		// if(type.equals("force")){

		// PartialHierarchyTree<NodeWithName> pt;

		// pt = builder.getFamilyTree(concept, type);

		List<String> listResult;

		listResult = builder.getFamilyTreeList(concept, type);

		responseString = JsonFactory.toJSON(listResult);

		context.setContentType("application/json; charset=UTF-8");
		context.getWriter().write(responseString);

	}

}
