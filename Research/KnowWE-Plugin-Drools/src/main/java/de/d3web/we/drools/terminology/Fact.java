/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.we.drools.terminology;

/**
 * Specifies methods every fact has to implement
 * 
 * @author Sebastian Furth
 */
public interface Fact {

	/**
	 * Returns the unique identifier of this Fact.
	 * @return the Name of this Fact
	 */
	public String getName();
	
	/**
	 * Returns a textual representation of the Fact's status.
	 * In most cases this will be s.th. like:
	 * <b>
	 * FactName = {Value, Value, ...}
	 * </b>
	 * @return Fact's status
	 */
	public String getStatusText();
	
	/**
	 * Returns a new instance of this fact containing only the name and the
	 * default values. Previously set values are not returned!
	 * 
	 * This method is necessary because the original Objects are stored in
	 * a FactsStore and only copies of these objects are inserted into the
	 * KnowledgeSessions
	 * 
	 * @return Copy of this fact
	 */
	public Fact copy();
	
}
