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

package de.d3web.proket.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import de.d3web.proket.data.IDialogObject;

/**
 * Util methods for handling java classes.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class ClassUtils {

	/**
	 * Gets the java class that best fits a base object under certain
	 * conditions. The algorithm is as follows: If there is a class named
	 * "prefix" + baseObject.getClassName() + "suffix", return it If not, cut
	 * the baseObject's className from the start until the next capital letter
	 * and recheck If no class is found, return null.
	 * 
	 * @param baseObject IDialogObject, the best fitting class is sought for.
	 * @param prefix Path where classes are searched (plus possibly name prefix)
	 * @param suffix Suffix the class name has to include
	 * @return The class which fits the baseObject best, null otherwise.
	 */
	public static Class<? extends Object> getBestClass(
			IDialogObject baseObject, String prefix, String suffix) {
		if (baseObject == null) {
			return null;
		}

		return getBestClass(baseObject.getVirtualClassName(), prefix, suffix);
	}

	/*
	 * Same as above (see @getBestClass(IDOalogObject, String, String)), only
	 * for general objects, not for IDialog Objects
	 */
	public static Class<? extends Object> getBestClass(Object baseObject,
			String prefix, String suffix) {
		if (baseObject == null) {
			return null;
		}

		return getBestClass(
				baseObject.getClass().getCanonicalName(), prefix, suffix);
	}

	/**
	 * Get the Java Class that fits the specified base String, prefix & suffix
	 * best.
	 * 
	 * @created 11.10.2010
	 * @param base the base String
	 * @param prefix potential prefixes
	 * @param suffix potential suffixes
	 * @return the retrieved Class if found, null otherwise
	 */
	public static Class<? extends Object> getBestClass(String base,
			String prefix, String suffix) {
		if (base == null) {
			return null;
		}
		if (!prefix.endsWith(".")) {
			prefix += ".";
		}

		Class<?> result = null;
		String lastBase = "";

		// kill the package name
		base = base.replaceFirst(".*\\.", "");

		while (!base.equals(lastBase)) {
			try {
				result = Class.forName(prefix + base + suffix);
			} catch (ClassNotFoundException ex) {
				// this renderer is not availible
				lastBase = base;
				base = base.replaceFirst("[A-Z][a-z-_0-9]*", "");
				continue;
			}
			break; // we found the first matching class
		}
		return result;
	}


	/**
	 * Retrieves an instance of the best fitting Object
	 * 
	 * @param baseObject Object for that the best fitting class is sought.
	 * @param prefix Path where classes are searched (plus possibly name prefix)
	 * @param suffix Suffix the name has to include
	 * @return The best fitting object, null otherwise.
	 */
	public static Object getBestObject(Object baseObject, String prefix,
			String suffix) {
		Class<? extends Object> clazz = getBestClass(baseObject, prefix, suffix);
		Object instance;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		return instance;
	}

	/**
	 * Retrieves an instance of the best fitting Object.
	 * 
	 * @param base name for that the best fitting Object is sought.
	 * @param prefix Path where classes are searched (plus possibly name prefix)
	 * @param suffix Suffix the name has to include
	 * @return An instance of the best fitting Object, null otherwise.
	 */
	public static Object getBestObject(String base, String prefix, String suffix) {
		Class<? extends Object> clazz = getBestClass(base, prefix, suffix);
		Object instance;
		try {
			instance = clazz.newInstance();
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		return instance;
	}


	/**
	 * Returns a list of all fields of an object. Uses reflection. So far, only
	 * fields of Boolean, Integer, Double, String, Color are processed.
	 * 
	 * @param o Object to check.
	 * @return List of all accessible fields of the given object in the current
	 *         security context.
	 */
	public static List<Field> getFields(Object o) {
		List<Field> result = new LinkedList<Field>();
		if (o == null) {
			return result;
		}

		Class<?> clazz = o.getClass();
		Field[] fields = clazz.getFields();
		String type = null;
		for (Field field : fields) {
			type = field.getType().getName();
			if (type.equals("java.lang.Boolean")
					|| type.equals("java.lang.Integer")
					|| type.equals("java.lang.Double")
					|| type.equals("java.lang.String")
					|| type.equals("java.awt.Color")) {
				result.add(field);
			}
		}

		return result;
	}

	/**
	 * Returns a list of all getters of an object. Uses reflection. Filters
	 * methods returning Boolean, Integer, String, or Color objects.
	 * 
	 * @param o
	 *            Object to check.
	 * @return List of all accessible getters of the given object in the current
	 *         security context.
	 */
	public static List<Method> getGetters(Object o) {
		List<Method> result = new LinkedList<Method>();
		if (o == null) {
			return result;
		}

		Class<?> clazz = o.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			String returnType = method.getReturnType().getName();
			if (method.getParameterTypes().length == 0
					&& ((method.getName().startsWith("get") && (returnType
							.equals("java.lang.Integer")
							|| returnType.equals("java.lang.Double")
							|| returnType.equals("java.lang.String")
							|| returnType.equals("java.awt.Color") || returnType
							.equals("java.lang.Boolean"))) || (method.getName()
							.startsWith("is") && returnType
							.equals("java.lang.Boolean")))) {
				result.add(method);
			}
		}

		return result;
	}

	/**
	 * Returns a list of all setters of an object. Uses reflection. Filters
	 * methods returning Boolean, Integer, String, or Color objects.
	 * 
	 * @param o
	 *            Object to check.
	 * @return List of all accessible setters of the given object in the current
	 *         security context.
	 */
	public static List<Method> getSetters(Object o) {
		List<Method> result = new LinkedList<Method>();
		if (o == null) {
			return result;
		}

		Class<?> clazz = o.getClass();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.getName().startsWith("set")
					&& method.getParameterTypes().length == 1) {
				String returnType = (method.getParameterTypes()[0]).getName();

				if (returnType.equals("java.lang.Integer")
						|| returnType.equals("java.lang.Double")
						|| returnType.equals("java.lang.String")
						|| returnType.equals("java.awt.Color")
						|| returnType.equals("java.lang.Boolean")) {
					result.add(method);
				}
			}
		}

		return result;
	}

	/**
	 * Util method to build a VirtualClassName of variable number of Strings
	 * given. Thus, the order of the Strings given is important as they are
	 * appended in this order one after another.
	 * 
	 * @param String parts to build the VCN of.
	 * @return String containing the VirtualClassName, which means one capital
	 *         letter at the beginning of every level and only there. As an
	 *         example: ColorHierarchyDialog
	 */
	public static String getVirtualClassName(String... levels) {
		StringBuilder result = new StringBuilder();
		for (String level : levels) {
			if (level != null) {
				result.append(StringUtils.capitalizeFirstLetter(level));
			}
		}
		return result.toString();
	}

	/**
	 * Get the XML-conform name of the setter, meaning just the method name with
	 * "set" removed at the beginning of the method name and decapitalized.
	 * 
	 * @created 11.10.2010
	 * @param method the setter method
	 * @return the methods' name without "set"
	 */
	public static String setterToXMLName(Method method) {
		String name = method.getName().replaceFirst("set", "");
		return StringUtils.decapitalizeFirstLetter(name);
	}

	/**
	 * Get the name of the getter, meaning just the method name with "get"
	 * removed at the beginning of the method name and decapitalized.
	 * 
	 * @created 11.10.2010
	 * @param method the getter method
	 * @return the methods' name without "get" and decapitalized
	 */
	public static String getterToName(Method method) {

		// replace all traditional getters (getSth) and boolean
		// getters (isSth) with empty String
		String name =
				method.getName().replaceAll("^get", "").replaceAll("^is", "");
		// name = name.substring(0, 1).toLowerCase() + name.substring(1);
		return StringUtils.decapitalizeFirstLetter(name);
	}

}
