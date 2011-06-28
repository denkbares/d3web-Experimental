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

import java.util.LinkedList;
import java.util.Stack;

/**
 * 
 * @author Florian Ziegler
 * @created 17.06.2011
 */
public class DiaFluxDialogManager {

	private static DiaFluxDialogManager instance = null;
	private Stack<String> activeFlowcharts;
	private LinkedList<DiaFluxDialogQuestionFindingPair> exactPath;

	private DiaFluxDialogManager() {
		activeFlowcharts = new Stack<String>();
		exactPath = new LinkedList<DiaFluxDialogQuestionFindingPair>();
	}

	public static DiaFluxDialogManager getInstance() {
		if (instance == null) {
			instance = new DiaFluxDialogManager();
		}
		return instance;

	}

	public Stack<String> getActiveFlowcharts() {
		return activeFlowcharts;
	}

	public String getActiveFlowchart() {
		return activeFlowcharts.peek();
	}

	public void addActiveFlowchart(String activeFlowchart) {
		if (!activeFlowcharts.contains(activeFlowchart)) {
			activeFlowcharts.add(activeFlowchart);
		}
	}

	public String getNextActiveFlowchart() {
		removeActiveFlowchart();
		return getActiveFlowchart();
	}

	public void removeActiveFlowchart() {
		activeFlowcharts.pop();
	}

	public LinkedList<DiaFluxDialogQuestionFindingPair> getExactPath() {
		return exactPath;
	}

	public void addItemToExactPath(DiaFluxDialogQuestionFindingPair pair) {
		exactPath.add(pair);
	}

	public void reset() {
		activeFlowcharts = new Stack<String>();
		exactPath = new LinkedList<DiaFluxDialogQuestionFindingPair>();
	}


}
