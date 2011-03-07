package de.knowwe.metatool;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
 * TODO: Refactor Strings, Refactor ImportHandling
 *
 * @author Sebastian Furth
 * @created Jan 18, 2011
 */
public class ObjectType {

	private final String id;
	private final QualifiedClass objectType;
	private final QualifiedClass superType;
	private final ParameterizedClass sectionFinder;
	private final List<QualifiedClass> constraints;
	private final Set<QualifiedClass> imports;
	private final String color;
	private final boolean exists;

	private final List<ObjectType> children = new LinkedList<ObjectType>();
	private boolean expanded = false;

	private ObjectType(Builder b) {
		this.id = b.id;
		this.objectType = b.objectType;
		this.superType = b.superType;
		this.sectionFinder = b.sectionFinder;
		this.constraints = b.constraints;
		this.exists = b.exists;
		this.color = b.color;
		this.imports = b.imports;
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
	 * @return Super type of the ObjectType.
	 */
	public QualifiedClass getSuperType() {
		return this.superType;
	}

	/**
	 * Returns the section finder of the ObjectType. Please note that the
	 * section finder can be null if it wasn't specified in the Builder.
	 *
	 * @created Feb 1, 2011
	 * @return Section Finder of the ObjectType if specified, otherwise null.
	 */
	public ParameterizedClass getSectionFinder() {
		return this.sectionFinder;
	}

	/**
	 * Returns a list of constraints for this ObjectType. If there is at least
	 * one constraint the sectionFinder will automatically set to the
	 * ConstraintSectionFinder.
	 *
	 * @created Feb 8, 2011
	 * @return List of constraints for this ObjectType.
	 */
	public List<QualifiedClass> getConstraints() {
		return Collections.unmodifiableList(constraints);
	}

	/**
	 * Returns an unmodifiable Collection of the ObjectType's children. The The
	 * children are ObjectTypes themselves.
	 *
	 * @created Jan 26, 2011
	 * @return All Children of the ObjectType.
	 */
	public List<ObjectType> getChildren() {
		if (!expanded) {
			addPlainTextChildren();
			expanded = true;
		}
		return Collections.unmodifiableList(this.children);
	}

	private void addPlainTextChildren() {
		// TODO Auto-generated method stub

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
		imports.add(child.objectType);
	}

	/**
	 * Returns the Color which will be applied to this ObjectType.
	 *
	 * @created Feb 23, 2011
	 * @return Color, represented as a String.
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * Returns all import statements necessary for this object type.
	 *
	 * @created Mar 7, 2011
	 * @return imports necessary for this object type
	 */
	public Collection<QualifiedClass> getImports() {
		return this.imports;
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

	/**
	 * Returns a String which represents a simple instatiation of this class,
	 * e.g. new String(). Please note, that the semicolon is not part contained
	 * in the returned String.
	 *
	 * @created Feb 8, 2011
	 * @return Instantiation String for this class.
	 */
	public String getInstantiationString() {
		return objectType.getInstantiationString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((constraints == null) ? 0 : constraints.hashCode());
		result = prime * result + (exists ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((imports == null) ? 0 : imports.hashCode());
		result = prime * result + ((objectType == null) ? 0 : objectType.hashCode());
		result = prime * result + ((sectionFinder == null) ? 0 : sectionFinder.hashCode());
		result = prime * result + ((superType == null) ? 0 : superType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ObjectType)) return false;
		ObjectType other = (ObjectType) obj;
		if (children == null) {
			if (other.children != null) return false;
		}
		else if (!children.equals(other.children)) return false;
		if (color == null) {
			if (other.color != null) return false;
		}
		else if (!color.equals(other.color)) return false;
		if (constraints == null) {
			if (other.constraints != null) return false;
		}
		else if (!constraints.equals(other.constraints)) return false;
		if (exists != other.exists) return false;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		if (imports == null) {
			if (other.imports != null) return false;
		}
		else if (!imports.equals(other.imports)) return false;
		if (objectType == null) {
			if (other.objectType != null) return false;
		}
		else if (!objectType.equals(other.objectType)) return false;
		if (sectionFinder == null) {
			if (other.sectionFinder != null) return false;
		}
		else if (!sectionFinder.equals(other.sectionFinder)) return false;
		if (superType == null) {
			if (other.superType != null) return false;
		}
		else if (!superType.equals(other.superType)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "ObjectType [id=" + id + ", objectType=" + objectType + ", superType=" + superType
				+ ", sectionFinder=" + sectionFinder + ", constraints=" + constraints
				+ ", imports=" + imports + ", color=" + color + ", exists=" + exists
				+ ", children=" + children + "]";
	}

	/**
	 * Builder Pattern applied for the creation of ObjectTypes.
	 *
	 * @author Sebastian Furth
	 * @created Mar 7, 2011
	 */
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
		 */
		private QualifiedClass superType = new QualifiedClass("de.d3web.we.kdom",
				"AbstractType");
		private ParameterizedClass sectionFinder = null;
		private final List<QualifiedClass> constraints = new LinkedList<QualifiedClass>();
		private String color = null;
		private final Set<QualifiedClass> imports = new LinkedHashSet<QualifiedClass>();

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
		 * Can be applied to the Builder to add a section finder to the
		 * generated ObjectType. Please note that it is not allowed to add a
		 * section finder to an ObjectType that is marked with
		 * "already existing".
		 *
		 * @created Jan 26, 2011
		 * @param sectionFinder The specified section finder
		 * @return Builder object with additional section finder.
		 */
		public Builder setSectionFinder(ParameterizedClass sectionFinder) {
			if (sectionFinder == null || exists) {
				throw new IllegalArgumentException();
			}
			this.sectionFinder = sectionFinder;
			return this;
		}

		/**
		 * Adds a constraint to the ObjectType. Please note that this method has
		 * to be called <strong>AFTER</strong> the sectionFinder was set.
		 * Otherwise you will get an IllegalStateException. Please note that the
		 * sectionFinder will be automatically changed to
		 * ConstraintSectionFinder(new OldSectionFinder()) when this method is
		 * Invoked for the first time!
		 *
		 * @created Feb 8, 2011
		 * @param constraint QualifiedClass representing the constraint
		 * @return Builder with the added constraint.
		 */
		public Builder addConstraint(QualifiedClass constraint) {
			if (constraint == null || exists) {
				throw new IllegalArgumentException();
			}
			if (sectionFinder == null) {
				throw new IllegalStateException(
						"You have to specify a sectionfinder before you can apply a constraint.");
			}
			if (!sectionFinder.getQualifiedClassName().equals(
					"de.d3web.we.kdom.constraint.ConstraintSectionFinder")) {
				// We lose the information if we don't do the import here!
				imports.add(sectionFinder);
				sectionFinder = new ParameterizedClass("de.d3web.we.kdom.constraint",
														"ConstraintSectionFinder",
														sectionFinder.getInstantiationString());
			}
			this.constraints.add(constraint);
			return this;
		}

		/**
		 * Adds a color to the object type. The color will be applied using
		 * KnowWEs StyleRenderer.
		 *
		 * @created Feb 23, 2011
		 * @param color
		 * @return
		 */
		public Builder setColor(String color) {
			if (color == null || exists) {
				throw new IllegalArgumentException();
			}
			this.color = color;
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
			organizeImports();
			return new ObjectType(this);
		}

		private void organizeImports() {
			imports.add(superType);
			if (sectionFinder != null) {
				imports.add(sectionFinder);
				imports.addAll(constraints);
				checkPatternImport();
			}
			if (color != null) {
				imports.add(new QualifiedClass("de.d3web.we.kdom.rendering", "StyleRenderer"));
			}
		}

		private void checkPatternImport() {
			if (sectionFinder.getClassName().equals("RegexSectionFinder")
					|| (sectionFinder.getClassName().equals("ConstraintSectionFinder")
						&& sectionFinder.getValue().contains("RegexSectionFinder"))) {
				// TODO: This heuristic should be improved (compare with RegEx)
				if (sectionFinder.getValue().contains("Pattern")) {
					imports.add(new QualifiedClass("java.util.regex", "Pattern"));
				}
			}
		}

	}

}
