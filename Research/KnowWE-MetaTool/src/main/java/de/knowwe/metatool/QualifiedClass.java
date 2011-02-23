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
package de.knowwe.metatool;

import java.util.logging.Logger;

/**
 * This class encapsulates all data of a fully qualified class, i.e. the class's
 * package and name.
 *
 * @author Sebastian Furth
 * @created Jan 21, 2011
 */
public class QualifiedClass {

	private final String packageName;
	private final String className;

	/**
	 * Creates a new QualifiedClass object. Please note that the package should
	 * be specified without trailing ".". If there is a trailing "." it will be
	 * automatically removed.
	 *
	 * @param packageName The package of the class
	 * @param className The name of the class
	 */
	public QualifiedClass(String packageName, String className) {
		if (packageName == null || className == null
				|| packageName.isEmpty() || className.isEmpty()) {
			throw new IllegalArgumentException();
		}
		if (packageName.endsWith(".")) {
			packageName = packageName.substring(0, packageName.length() - 1);
			Logger.getAnonymousLogger().info(
					"Deleted trailing \".\" in package declaration (" + packageName + ").");
		}
		this.packageName = packageName;
		this.className = className;
	}

	/**
	 * Returns the specified package, e.g. java.util
	 *
	 * @created Jan 26, 2011
	 * @return
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Returns the name of the class, e.g. String
	 *
	 * @created Jan 26, 2011
	 * @return the class name.
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the qualified class name, e.g. java.util.String
	 *
	 * @created Jan 26, 2011
	 * @return the qualified class name.
	 */
	public String getQualifiedClassName() {
		return packageName + "." + className;
	}

	/**
	 * Returns a String which represents a simple instantiation of this class,
	 * e.g. new String(). Please note, that the semicolon is not part contained
	 * in the returned String.
	 *
	 * @created Feb 8, 2011
	 * @return Instantiation String for this class.
	 */
	public String getInstantiationString() {
		return "new " + className + "()";
	}

	/**
	 * Returns a String which represents a fully qualified instantiation of this
	 * class, e.g. new java.lang.String(). Please note, that the semicolon is
	 * not part contained in the returned String.
	 *
	 * @created Feb 23, 2011
	 * @return Instantiation String for this class.
	 */
	public String getFullyQualifiedInstantiationString() {
		return "new " + getQualifiedClassName() + "()";
	}

	/**
	 * Returns a String which represents a singleton instantiation of this
	 * class, e.g. YourClass.getInstance(). Please note, that the semicolon is
	 * not part contained in the returned String.
	 *
	 * @created Feb 11, 2011
	 * @return Instantiation String for this class.
	 */
	public String getSingletonInstantiationString() {
		return className + ".getInstance()";
	}

	@Override
	public String toString() {
		return getQualifiedClassName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof QualifiedClass)) return false;
		QualifiedClass other = (QualifiedClass) obj;
		if (className == null) {
			if (other.className != null) return false;
		}
		else if (!className.equals(other.className)) return false;
		if (packageName == null) {
			if (other.packageName != null) return false;
		}
		else if (!packageName.equals(other.packageName)) return false;
		return true;
	}

}
