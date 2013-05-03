/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.wisskont.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 
 * @author jochenreutelshofer
 * @created 12.04.2013
 */
public class Tree<T extends HierarchyNode<T>> {

	private final Node<T> root;

	public Tree(T rootData) {
		root = new Node<T>(rootData);
		root.children = new ArrayList<Node<T>>();
	}

	public Node<T> getRoot() {
		return root;
	}

	public boolean removeNodeFromTree(T term) {
		return removeNodeFromTree(term, root);
	}

	public T find(T t) {
		return findRecursive(t, root);
	}

	public T findRecursive(T t, Node<T> node) {
		List<Node<T>> children = node.getChildren();
		Iterator<Node<T>> iterator = children.iterator();
		while (iterator.hasNext()) {
			Node<T> child = iterator.next();
			if (child.data.equals(t)) {
				return child.data;
			}
			else {
				// other search recursive
				T found = findRecursive(t, child);
				if (found != null) {
					return found;
				}
			}
		}
		return null;
	}

	/**
	 * Returns all nodes contained in this tree.
	 * 
	 * @created 12.04.2013
	 * @return
	 */
	public Set<T> getNodes() {
		Set<T> result = new HashSet<T>();
		collectNodes(root, result);
		return result;
	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param root2
	 * @param result
	 */
	private void collectNodes(Node<T> n, Set<T> result) {
		result.add(n.data);
		List<Node<T>> children = n.getChildren();
		for (Node<T> child : children) {
			collectNodes(child, result);
		}
	}

	public int getNodeCount() {
		Set<T> set = new HashSet<T>();
		collectNodes(getRoot(), set);
		return set.size();
	}

	/**
	 * Inserts the new element into the tree. It considers correct hierarchical
	 * insertion, i.e., tree reflects hierarchical concept structure after
	 * insertion. The tree is restructured if required.
	 * 
	 * 
	 * @created 12.04.2013
	 * @param t
	 */
	public void insertNode(T t) {
		insertNodeUnder(t, root);
	}

	/**
	 * Two cases need to be considered:
	 * 
	 * 1) One of the siblings is ancestor of the new concept:
	 * 
	 * - -Then we need to descent and insert under that one
	 * 
	 * 2) A sibling can be a successor of the new concept:
	 * 
	 * - -Then the new concept has to be inserted at this level, but the
	 * siblings need to be searched for successor-concepts. If such concepts are
	 * found, they need to be re-hanged to be child of the new concept.
	 * 
	 * @created 12.04.2013
	 * @param t
	 * @param parent
	 */
	private void insertNodeUnder(T t, Node<T> parent) {

		List<Node<T>> children = parent.getChildren();
		Iterator<Node<T>> descentIterator = children.iterator();

		// look for super concepts to descent
		boolean descent = false;
		while (descentIterator.hasNext()) {
			Node<T> child = descentIterator.next();
			if (t.isSubNodeOf(child.data)) {
				insertNodeUnder(t, child);
				descent = true;
				break;
			}
		}
		if (!descent) {
			Node<T> newNode = new Node<T>(t);
			parent.addChild(newNode);
			newNode.setParent(parent);

			// then check siblings, which could be sub-concepts of the new one
			Iterator<Node<T>> checkSiblingsIterator = children.iterator();
			List<Node<T>> successorSiblings = new ArrayList<Node<T>>();
			while (checkSiblingsIterator.hasNext()) {
				Node<T> sibling = checkSiblingsIterator.next();
				if (sibling.data.equals(t)) continue;
				if (sibling.data.isSubNodeOf(t)) {
					// re-hang sibling to be successor of t
					successorSiblings.add(sibling);

				}
			}
			for (Node<T> successorSibling : successorSiblings) {
				newNode.addChild(successorSibling);
				parent.removeChild(successorSibling);
				successorSibling.setParent(newNode);
			}
		}

	}

	/**
	 * 
	 * @created 12.04.2013
	 * @param term
	 * @param root2
	 */
	private boolean removeNodeFromTree(T term, Node<T> node) {
		boolean found = false;
		List<Node<T>> children = node.getChildren();
		Iterator<Node<T>> iterator = children.iterator();
		Node<T> foundNode = null;
		while (iterator.hasNext()) {
			Node<T> child = iterator.next();
			if (child.data.equals(term)) {
				// if found, delete child node and hook up grand-children
				foundNode = child;
				found = true;
			}
			else {
				// other search recursive
				if (removeNodeFromTree(term, child)) {
					found = true;
				}
			}
		}
		if (foundNode != null) {
			removeChildNode(foundNode, node);
		}
		return found;
	}

	private void removeChildNode(Node<T> child, Node<T> father) {
		List<Node<T>> grandChildren = child.getChildren();
		father.removeChild(child);
		for (Node<T> grandChild : grandChildren) {
			father.addChild(grandChild);
		}
	}

	public static class Node<T extends HierarchyNode<T>> implements Comparable<Node<T>> {

		private final T data;
		private Node<T> parent;
		private List<Node<T>> children = new ArrayList<Node<T>>();;

		@Override
		public String toString() {
			return data.toString();
		}

		/**
		 * 
		 */
		public Node(T data) {
			this.data = data;
		}

		public Node<T> getParent() {
			return parent;
		}

		@Override
		public int hashCode() {
			return data.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Node<?>) {
				return data.equals(((Node<?>) obj).getData());
			}
			return false;
		}

		public void setParent(Node<T> parent) {
			this.parent = parent;
		}

		public T getData() {
			return data;
		}

		public void addChild(Node<T> n) {
			children.add(n);
			n.parent = this;
		}

		public boolean removeChild(Node<T> n) {
			return children.remove(n);
		}

		public boolean containsChild(Node<T> n) {
			return children.contains(n);
		}

		public List<Node<T>> getChildren() {
			return Collections.unmodifiableList(children);
		}

		public List<Node<T>> getChildrenSorted() {
			List<Node<T>> copy = new ArrayList<Node<T>>();
			copy.addAll(children);
			Collections.sort(copy);
			return Collections.unmodifiableList(copy);
		}

		@Override
		public int compareTo(Node<T> o) {
			return data.compareTo(o.data);
		}

	}
}