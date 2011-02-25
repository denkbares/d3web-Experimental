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

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * The {@link DialogTree} is the internal representation of a dialog. The
 * structure is hierarchical and resembles the basic dialog-components-hierarchy
 * that is also mimiced by the xml for manually defining dialogs.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class DialogTree {

	private boolean inherited = false;

	private IDialogObject root;

	/**
	 * Determines if an {@link IDialogObject} is the first of one or more
	 * siblings on its level.
	 * 
	 * @param dialogObject
	 *            The {@link IDialogObject} to examine
	 * @return True, if there is no sibling before this {@link IDialogObject},
	 *         false otherwise
	 */
	public static boolean isFirstSibling(IDialogObject dialogObject) {
		IDialogObject parent = dialogObject.getParent();
		if (parent == null) {
			return false;
		}

		Vector<IDialogObject> children = parent.getChildren();
		if (dialogObject.equals(children.get(0))) {
			return true;
		}

		return false;
	}

	/**
	 * Gets the next sibling of an {@link IDialogObject} in the tree.
	 * 
	 * @param dialogObject
	 *            The {@link IDialogObject} to start the search from
	 * @return The next sibling or null, if there is no more sibling
	 */
	public static IDialogObject nextSibling(IDialogObject dialogObject) {
		IDialogObject parent = dialogObject.getParent();
		if (parent == null) {
			return null;
		}

		Vector<IDialogObject> children = parent.getChildren();
		for (int i = 0; i < children.size() - 1; i++) {
			if (dialogObject.equals(children.get(i))) {
				return children.get(i + 1);
			}
		}

		return null;
	}

	/**
	 * Retrieve the {@link DialogTree} as a flat list of {@link IDialogObject}s.
	 * 
	 * @return List of {@link IDialogObject}s in this {@link DialogTree}
	 */
	public List<IDialogObject> asList() {

		// inherit all attributes where necessary
		inheritAll();

		LinkedList<IDialogObject> list = new LinkedList<IDialogObject>();
		asList(list, getRoot());
		return list;
	}

	/**
	 * Util/helper method for getting DialogObjects as a List with a given root
	 * element, call recursively.
	 * 
	 * @created 10.10.2010
	 * @param linkedList A new List Object
	 * @param root
	 */
	private void asList(LinkedList<IDialogObject> linkedList, IDialogObject root) {
		if (root == null) {
			return;
		}
		Vector<IDialogObject> children = root.getChildren();
		linkedList.add(root);
		if (children != null) {
			for (IDialogObject child : children) {
				asList(linkedList, child);
			}
		}
	}

	/**
	 * Make sure that all style/attribute information are correctly inherited by
	 * calling the AttributeContainer.compile method for all elements in the
	 * tree upon first getXXX access.
	 */
	private void inheritAll() {
		if (inherited) {
			return;
		}

		LinkedList<IDialogObject> list = new LinkedList<IDialogObject>();
		asList(list, root); // to bypass call of this function

		// traverse the tree and inherit the attributes of each DialogObject
		for (IDialogObject o : list) {
			o.getInheritableAttributes().compileInside();
		}
		inherited = true;
	}

	/**
	 * Get the {@link IDialogObject} with the given ID from the
	 * {@link DialogTree}.
	 * 
	 * @param partialId
	 *            ID of the seeked object.
	 * @return The {@link IDialogObject} with the sought ID, null otherwise.
	 */
	public IDialogObject getById(String partialId) {

		// first inherit all attributes TODO: needed?
		inheritAll();
		IDialogObject root = getRoot();
		if (root == null) {
			return null;
		}
		return getById(partialId, root);
	}

	/**
	 * Recursively trying to retrieve a DialogObject by a given ID Called on by
	 * getById(String partialId)
	 * 
	 * @created 10.10.2010
	 * @param partialId the ID
	 * @param root the currently inspected root
	 * @return the DialogObject, in case it was found, otherwise null
	 */
	private IDialogObject getById(String partialId, IDialogObject root) {

		// if its the root already, return
		if (partialId.equals(root.getId())) {
			return root;
		}
			// otherwise search recursively in the children's list
		else {
			Vector<IDialogObject> children = root.getChildren();
			for (IDialogObject child : children) {
				IDialogObject result = getById(partialId, child);
				if (result != null) {
					return result;
				}
			}
			return null;
		}

	}

	public IDialogObject getRoot() {
		inheritAll();
		return root;
	}

	public void setRoot(IDialogObject root) {
		this.root = root;
		inherited = false;
	}

	/**
	 * Mark all {@link IDialogObject}s in this {@link DialogTree} as unrendered,
	 * so that they will be re-rendered the next time.
	 */
	public void setUnrendered() {
		List<IDialogObject> list = asList();
		for (IDialogObject dialogObject : list) {
			dialogObject.setRendered(false);
		}
	}

	@Override
	/**
	 * Customized toString() method
	 */
	public String toString() {
		if (root == null) {
			return "empty tree";
		}

		IDialogObject root = getRoot();
		return toString(root);
	}

	/**
	 * Getting String representation of a DialogObeject recursively for mapping
	 * the dialogTree structure.
	 * 
	 * @created 10.10.2010
	 * @param object The object of the DialogTree where toString should start
	 * @return String the String representation
	 */
	private static String toString(IDialogObject object) {
		StringBuilder result = new StringBuilder();
		StringBuilder indent = new StringBuilder();

		toString(object, result, indent);
		return result.toString();
	}

	/**
	 * The actually recursive part of the recursively calling toString() method.
	 * 
	 * @created 10.10.2010
	 * @param object
	 * @param result
	 * @param indent
	 */
	private static void toString(IDialogObject object, StringBuilder result,
			StringBuilder indent) {

		// render this one
		result.append(indent);
		result.append(object);
		result.append("\n");

		indent.append("\t");

		// children
		Vector<IDialogObject> children = object.getChildren();
		for (IDialogObject child : children) {
			toString(child, result, indent);
		}

		// delete the last indentation when leaving the recursion hierarchy
		indent.deleteCharAt(indent.length() - 1);
	}

}
