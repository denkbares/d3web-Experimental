/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.d3web.we.diaFlux.dialog;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Florian Ziegler
 * @created 19.07.2011
 */
public class DiaFluxDialogSession {

	private final LinkedList<DiaFluxDialogQuestionFindingPair> forwardKnowledge;
	private final LinkedList<DiaFluxDialogQuestionFindingPair> path;

	public DiaFluxDialogSession(LinkedList<DiaFluxDialogQuestionFindingPair> forwardKnowledge, LinkedList<DiaFluxDialogQuestionFindingPair> path) {
		this.forwardKnowledge = forwardKnowledge;
		this.path = path;
	}

	public DiaFluxDialogSession(List<DiaFluxDialogQuestionFindingPair> forwardKnowledge, List<DiaFluxDialogQuestionFindingPair> path) {
		LinkedList<DiaFluxDialogQuestionFindingPair> f = new LinkedList<DiaFluxDialogQuestionFindingPair>();
		LinkedList<DiaFluxDialogQuestionFindingPair> p = new LinkedList<DiaFluxDialogQuestionFindingPair>();

		for (DiaFluxDialogQuestionFindingPair pair : forwardKnowledge) {
			f.add(pair);
		}

		for (DiaFluxDialogQuestionFindingPair pair : path) {
			p.add(pair);
		}

		this.forwardKnowledge = f;
		this.path = p;
	}

	public DiaFluxDialogSession() {
		this.forwardKnowledge = new LinkedList<DiaFluxDialogQuestionFindingPair>();
		this.path = new LinkedList<DiaFluxDialogQuestionFindingPair>();
	}

	public LinkedList<DiaFluxDialogQuestionFindingPair> getForwardKnowledge() {
		return forwardKnowledge;
	}

	public void addPairToForwardKnowledge(DiaFluxDialogQuestionFindingPair pair) {
		this.forwardKnowledge.add(pair);
	}

	public LinkedList<DiaFluxDialogQuestionFindingPair> getPath() {
		return path;
	}

	public void addPairToPath(DiaFluxDialogQuestionFindingPair pair) {
		this.path.add(pair);
	}

}
