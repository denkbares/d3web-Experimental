/*
 * Copyright (C) 2012 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaflux.evaluators;

import de.d3web.core.inference.condition.CondNumGreater;
import de.d3web.core.inference.condition.Condition;
import de.d3web.we.diaflux.datamanagement.Domain;
import de.d3web.we.diaflux.datamanagement.EvalResult;
import de.d3web.we.diaflux.datamanagement.NumValue;

/**
 * 
 * @author Roland Jerg
 * @created 08.05.2012
 */
public class NumGreaterEval extends NumEvaluator {

	/**
	 *
	 */
	@Override
	public boolean canEvaluate(Condition condition) {
		return condition.getClass().equals(CondNumGreater.class);
	}

	/**
	 *
	 */
	@Override
	public EvalResult evaluate(Condition condition) {
		EvalResult result = new EvalResult();
		Domain<NumValue> domain = new Domain<NumValue>();
		CondNumGreater conGr = (CondNumGreater) condition;
		double d = conGr.getConditionValue();
		domain.add(new NumValue(d, Double.POSITIVE_INFINITY, false, false));
		String var = conGr.getQuestion().getName();

		result.add(var, domain);

		return result;
	}

}
