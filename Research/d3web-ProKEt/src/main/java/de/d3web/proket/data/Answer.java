/**
 * Copyright (C) 2010 Chair of Artificial Intelligence and Applied Informatics
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

package de.d3web.proket.data;

import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.StringUtils;

/**
 * Models an answer in the dialog. Answers are only allowed as direct children
 * of a {@link Question} in the {@link DialogTree}.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class Answer extends DefaultDialogObject {

	protected String coords; // for potential image questions
	protected String shape; // shape for image questions/answer alternatives
	protected String selection; // flag for the selection state
	protected Integer width;// width of the answer (column? TODO)
	protected Boolean readonly = false; // for flagging read only fields
	protected Boolean abstraction = false;// for flagging abstraction answers,
											// i.e. answers that are
											// calcualated due to other answers

	// Constructor: define inheritable style and base String description
	public Answer() {
		style = new InheritableAttributes(this);
		VCNbase = "Answer";
	}

	public String getCoords() {
		return coords;
	}

	public String getSelection() {
		return selection;
	}

	public String getShape() {
		return shape;
	}

	public Integer getWidth() {
		return width;
	}

	public Boolean getReadonly() {
		return readonly;
	}

	public Boolean getAbstraction() {
		return abstraction;
	}

	public void setCoords(String coords) {
		this.coords = coords;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public void setShape(String shape) {
		this.shape = shape;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setReadonly(Boolean readonly) {
		this.readonly = readonly;
	}

	public void setAbstraction(Boolean abs) {
		this.abstraction = abs;
	}

	@Override
	/**
	 * Get the virtual (internal, potentially extended) classname
	 */
	public String getVirtualClassName() {

		// inherit necessary inheritable attributes
		getInheritableAttributes().compileInside();

		// type of the answer is the first of the following elements
		// that is not null: normal type, inherited answer type, inherited
		// answer type of the parent or type of the parent, thus the order here
		// also expresses the importance of the parameters
		String type =
				(String) StringUtils.firstNonNull(
						getType(),
						getInheritableAttributes().getAnswerType(),
						getParent().getInheritableAttributes().getAnswerType(),
						getParent().getType());

		// get the root parent-element, i.e. topmost in the hierarchy
		IDialogObject dialog = getRootParent();

		return ClassUtils.getVirtualClassName(dialog.getSubType(),
				dialog.getType(), getSubType(), type, VCNbase);
	}
}
