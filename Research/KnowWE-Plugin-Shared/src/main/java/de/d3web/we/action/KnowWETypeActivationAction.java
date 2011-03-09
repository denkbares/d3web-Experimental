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

package de.d3web.we.action;

import java.io.IOException;
import java.util.List;

import de.d3web.we.core.KnowWEEnvironment;
import de.d3web.we.kdom.Type;

/**
 * Changes the Activation-State of an Type at Runtime. See
 * TypeActivationHandler.
 *
 * @author Johannes Dienst
 *
 */
public class KnowWETypeActivationAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {

		String result = perform(context);
		if (result != null && context.getWriter() != null) {
			context.setContentType("text/html; charset=UTF-8");
			context.getWriter().write(result);
		}

	}

	private String perform(UserActionContext context) {

		// get the one needed and change its Activation state.
		List<Type> types = KnowWEEnvironment.getInstance()
				.getAllTypes();
		int index = this.findIndexOfType(context.getParameter("Type"),
				types);

		if (index != -1) {
			Type typ = types.get(index);
			if (typ != null) {
				if (!typ.getActivationStatus()) {
					typ.activateType();
				}
				else {
					typ.deactivateType();
				}
			}
		}

		return "nothing";
	}

	/**
	 * Searches for the Index of a type in a given List.
	 *
	 * @param typeName
	 * @param types
	 * @return
	 */
	private int findIndexOfType(String typeName, List<Type> types) {
		String shortTypeName = typeName.substring(typeName.lastIndexOf(".") + 1);
		for (Type typ : types) {
			if (typ.getName().equals(shortTypeName)) {
				return types.indexOf(typ);
			}
		}
		return -1;
	}

}
