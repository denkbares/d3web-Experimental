/*
 * Copyright (C) 2011 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.we.ci4ke.anomalies;

import de.d3web.testing.AbstractTest;
import de.d3web.testing.ArgsCheckResult;
import de.d3web.testing.Message;
import de.d3web.testing.Message.Type;

/**
 * test ...
 * 
 * @author Gritje Meinke
 * @created 17.04.2011
 */
public class AnomaliesCITest extends AbstractTest<Object> {

	@Override
	public Message execute(Object o, String[] args) {

		return new Message(Type.FAILURE);

	}

	@Override
	public ArgsCheckResult checkArgs(String[] args) {
		return new ArgsCheckResult(args);
	}

	@Override
	public Class<Object> getTestObjectClass() {
		return Object.class;
	}

	@Override
	public String getDescription() {
		return "No description available";
	}


}
