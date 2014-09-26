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
package de.knowwe.termbrowser;

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
import java.io.IOException;
import java.util.List;

import de.knowwe.core.action.UserActionContext;
import de.knowwe.core.kdom.parsing.Section;

/**
 * 
 * @author Jochen Reutelshöfer
 * @created 04.06.2013
 */
public interface DragDropEditInserter {

	/**
	 * 
	 * @created 21.06.2013
	 * @param s
	 * @param droppedTerm
	 * @param relationKind
	 * @param context
	 * @return the new kdom-id of the corresponding section after modification
	 *         and parsing
	 * @throws IOException
	 */
	public String insert(Section<?> s, String droppedTerm, String relationKind, UserActionContext context) throws IOException;

	public List<String> provideInsertRelationOptions(Section<?> s, String droppedTerm);


}
