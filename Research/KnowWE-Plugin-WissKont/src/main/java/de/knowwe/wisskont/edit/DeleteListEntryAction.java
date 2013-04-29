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
package de.knowwe.wisskont.edit;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.knowwe.core.action.AbstractAction;
import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.parsing.Section;
import de.knowwe.core.kdom.parsing.Sections;

/**
 * 
 * @author jochenreutelshofer
 * @created 19.04.2013
 */
public class DeleteListEntryAction extends AbstractAction {

	@Override
	public void execute(UserActionContext context) throws IOException {
		String kdomid = context.getParameter("kdomid");
		Map<String, String> replacementMap = new HashMap<String, String>();
		replacementMap.put(kdomid, "");
		Section<?> entry = Sections.getSection(kdomid);

		// check whether trailing comma should be deleted
		Section<? extends Type> father = entry.getFather().getFather();
		List<Section<? extends Type>> siblings = father.getChildren();
		Iterator<Section<? extends Type>> iterator = siblings.iterator();
		Section<? extends Type> subsequentSection = null;
		Section<? extends Type> previousSection = null;
		Section<? extends Type> next = null;
		while (iterator.hasNext()) {
			Section<? extends Type> last = next;
			next = iterator.next();
			if (Sections.getSubtreePostOrder(next).contains(entry)) {
				previousSection = last;
				if (iterator.hasNext()) {
					subsequentSection = iterator.next();
					break;
				}
			}
		}
		if (subsequentSection != null && subsequentSection.getText().contains(",")) {
			// if followed by a comma also delete that
			replacementMap.put(subsequentSection.getID(), "");
		}
		else {
			// if not check whether leading comma needs to be deleted
			if (previousSection != null && previousSection.getText().contains(",")) {
				// if followed by a comma also delete that
				replacementMap.put(previousSection.getID(), "");
			}

		}

		Sections.replaceSections(context, replacementMap);

	}
}
