package de.knowwe.kdom.generator;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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

/**
 * This class represents an KnowWE-Object-Type. Objects of this class can be
 * used to build a model of KnowWE-Object-Type-hierarchy. Such hierarchies can
 * be
 *
 * TODO: Refactor Strings, SectionFinder, SubtreeHandler...
 *
 * @author Sebastian Furth
 * @created Jan 18, 2011
 */
public class ObjectType {

	private final String id;
	private final QualifiedClass objectType;
	private final QualifiedClass superType;
	private final boolean exists;

	private final List<ObjectType> children = new LinkedList<ObjectType>();

	private ObjectType(Builder b) {
		this.id = b.id;
		this.objectType = b.objectType;
		this.superType = b.superType;
		this.exists = b.exists;
	}

	/**
	 * Returns the id of this ObjectType. The ID is the key element for the XML
	 * structure!
	 *
	 * @created Jan 26, 2011
	 * @return the ID of this ObjectType
	 */
	public String getID() {
		return this.id;
	}

	/**
	 * Returns the class name specified for the ObjectType. Don't mix this
	 * method up with ObjectType.class
	 *
	 * @created Jan 26, 2011
	 * @return Class name of the ObjectType.
	 */
	public String getClassName() {
		return this.objectType.getClassName();
	}

	/**
	 * Returns the package name specified for the ObjectType.
	 *
	 * @created Jan 26, 2011
	 * @return Package name of the ObjectType
	 */
	public String getPackageName() {
		return this.objectType.getPackageName();
	}

	/**
	 * Returns the super type of the ObjectType. The SuperType is the class
	 * which will be extended by this ObjectType.
	 *
	 * @created Jan 26, 2011
	 * @return Super type of the ObjectType
	 */
	public QualifiedClass getSuperType() {
		return this.superType;
	}

	/**
	 * Returns an unmodifiable Collection of the ObjectType's children. The The
	 * children are ObjectTypes themselves.
	 *
	 * @created Jan 26, 2011
	 * @return All Children of the ObjectType.
	 */
	public List<ObjectType> getChildren() {
		return Collections.unmodifiableList(this.children);
	}

	/**
	 * Adds a new child to the ObjectType at the specified position. Please note
	 * that the position can't be > getChildren().size(). If a child already
	 * exists on the specified position it and all successors will be shifted to
	 * the right.
	 *
	 * @created Jan 26, 2011
	 * @param position the desired position of the child
	 * @param child the child to be added
	 */
	public void addChild(int position, ObjectType child) {
		if (position < 0 || child == null || position > children.size()) {
			throw new IllegalArgumentException();
		}
		children.add(position, child);
	}

	/**
	 * Returns true if this ObjectType represents an ObjectType that already
	 * exists. Existing ObjectTypes won't be generated. Please note that there
	 * is no built in check for this! You have to check it yourself!
	 *
	 * @created Jan 26, 2011
	 * @return true if ObjectType already exists, otherwise false.
	 */
	public boolean alreadyExists() {
		return exists;
	}

	public static class Builder {

		/*
		 * Mandatory attributes
		 */
		private final String id;
		private final QualifiedClass objectType;
		private final boolean exists;

		/*
		 * Optional attributes
		 *
		 * TODO:
		 */
		private QualifiedClass superType = new QualifiedClass("de.d3web.we.kdom",
				"DefaultAbstractKnowWEObjectType");

		/**
		 * Builder for ObjectTypes. The attributes are the mandatory attributes
		 * for ObjectTypes.
		 *
		 * @see ObjectType
		 * @param id The ID for the ObjectType
		 * @param objectType Specifies the package and the class of the new
		 *        ObjectType
		 * @param exists true if there already exists an ObjectType, otherwise
		 *        false.
		 */
		public Builder(String id, QualifiedClass objectType, boolean exists) {
			if (id == null || objectType == null || id.isEmpty()) {
				throw new IllegalArgumentException();
			}
			this.id = id;
			this.objectType = objectType;
			this.exists = exists;
		}

		/**
		 * Can be applied to the Builder to change the super type of the
		 * generated ObjectType. Please note that it is not allowed to change
		 * the SuperType of an ObjectType that is marked with
		 * "already existing".
		 *
		 * @created Jan 26, 2011
		 * @param superType Specifies the package and the class of the SuperType
		 * @return Builder object with customized super type.
		 */
		public Builder setSuperType(QualifiedClass superType) {
			if (superType == null || exists) {
				throw new IllegalArgumentException();
			}
			this.superType = superType;
			return this;
		}

		/**
		 * Returns an ObjectType object with the settings applied to the
		 * builder.
		 *
		 * @created Jan 26, 2011
		 * @return customized ObjectType object.
		 */
		public ObjectType build() {
			return new ObjectType(this);
		}

	}

}
