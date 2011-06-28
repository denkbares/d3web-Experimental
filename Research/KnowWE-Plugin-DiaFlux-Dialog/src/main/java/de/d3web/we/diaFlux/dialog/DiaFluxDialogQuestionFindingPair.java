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

import java.util.List;

/**
 * 
 * @author Florian Ziegler
 * @created 24.06.2011
 */
public class DiaFluxDialogQuestionFindingPair {

	private final String question;
	private final List<String> finding;

	public DiaFluxDialogQuestionFindingPair(String question, List<String> finding) {
		this.question = question;
		this.finding = finding;
	}

	public String getQuestion() {
		return question;
	}

	public List<String> getFinding() {
		return finding;
	}

	@Override
	public String toString() {
		return question + "->" + finding;
	}



}
