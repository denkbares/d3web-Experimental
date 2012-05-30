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
package de.d3web.we.diaflux.anomalies;

import cc.denkbares.testing.ArgsCheckResult;
import cc.denkbares.testing.Message;
import cc.denkbares.testing.Message.Type;
import cc.denkbares.testing.Test;
import de.d3web.core.knowledge.KnowledgeBase;
import de.knowwe.core.utils.Strings;

/**
 * 
 * @author Roland Jerg
 * @created 09.05.2012
 */
public abstract class AbstractAnomalyTest implements Test<KnowledgeBase> {

	@Override
	public ArgsCheckResult checkArgs(String[] args) {
		int requiredArgsCount = 0;
		// args are not passed to implementing subclasses hence no args can be
		// used
		if (args.length > requiredArgsCount) {
			new ArgsCheckResult(ArgsCheckResult.Type.WARNING, "Too many arguments given: "
					+ args.length + " but only " + requiredArgsCount + "are expected");
		}
		return new ArgsCheckResult(ArgsCheckResult.Type.FINE);
	}

	@Override
	public Class<KnowledgeBase> getTestObjectClass() {
		return KnowledgeBase.class;
	}

	@Override
	public Message execute(KnowledgeBase kb, String[] args) {
		String errormsg = "";

		Message res = new Message(Type.SUCCESS, null);

		errormsg = test(kb);

		if (!errormsg.isEmpty()) {
			Strings.maskHTML(errormsg);
			res = new Message(Type.FAILURE, errormsg);
		}

		return res;
	}

	/**
	 * Each Anomalytest implements its own testmethod, which is then called by
	 * this class' call()
	 * 
	 * @created 10.05.2012
	 * @param kb
	 * @return
	 */
	protected abstract String test(KnowledgeBase kb);
}