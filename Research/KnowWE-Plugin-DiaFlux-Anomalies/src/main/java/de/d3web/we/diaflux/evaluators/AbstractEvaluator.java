/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.Condition;


/**
 * 
 * @author Reinhard Hatko
 * @created 13.11.2012
 */
public abstract class AbstractEvaluator implements Evaluator {

	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(getEvaluationClass());
	}

	protected abstract Class<? extends Condition> getEvaluationClass();

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return getClass().equals(obj.getClass());
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
