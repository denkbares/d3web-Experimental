/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaFlux.dialog;

import java.io.IOException;
import java.util.LinkedList;

import de.d3web.we.action.AbstractAction;
import de.d3web.we.action.UserActionContext;
import de.d3web.we.core.KnowWEAttributes;
import de.knowwe.d3web.action.SetSingleFindingAction;

/**
 * 
 * @author Florian Ziegler
 * @created 21.06.2011
 */
public class DiaFluxDialogSetFindingAction extends AbstractAction {

	/**
	 * actions which forwards the setsinglefinding request and saves the
	 * activated node
	 */
	@Override
	public void execute(UserActionContext context) throws IOException {
		SetSingleFindingAction ssfa = new SetSingleFindingAction();
		ssfa.execute(context);

		String objectId = context.getParameter(KnowWEAttributes.SEMANO_OBJECT_ID);
		String valueId = context.getParameter(KnowWEAttributes.SEMANO_VALUE_ID);
		String valueNum = context.getParameter(KnowWEAttributes.SEMANO_VALUE_NUM);

		LinkedList<String> finding = new LinkedList<String>();
		
		if (valueId != null) {
			if (valueId.contains(DiaFluxDialogUtils.DIAFLUXDIALOG_SEPARATOR)) {
				String[] mcAnswers = valueId.split(DiaFluxDialogUtils.DIAFLUXDIALOG_SEPARATOR);
				for (String s : mcAnswers) {
					if (s != "") {
						finding.add(s);
					}
				}
			} else {
				finding.add(valueId);
			}
		} else {
			finding.add(valueNum);
		}
		
		DiaFluxDialogQuestionFindingPair pair = new DiaFluxDialogQuestionFindingPair(objectId,
				finding);
		
		DiaFluxDialogManager.getInstance().addItemToExactPath(pair);

	}

}
