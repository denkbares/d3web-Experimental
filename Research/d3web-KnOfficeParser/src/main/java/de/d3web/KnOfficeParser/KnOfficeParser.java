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

package de.d3web.KnOfficeParser;

import java.io.Reader;
import java.util.List;

import de.d3web.core.manage.IDObjectManagement;
import de.d3web.report.Message;

/**
 * Interface welche alle Builder implementieren sollten um diese mit ihren
 * Parsern einheitlicha aufrufen zu können
 * 
 * @author Markus Friedrich
 * 
 */
public interface KnOfficeParser {

	List<Message> addKnowledge(Reader r, IDObjectManagement idom, KnOfficeParameterSet s);

	List<Message> checkKnowledge();

}
