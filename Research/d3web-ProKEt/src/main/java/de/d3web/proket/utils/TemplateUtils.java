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

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

/**
 * Collection of functions for loading templates.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class TemplateUtils {

	/**
	 * Fills the attributes of a StringTemplate by all availible public fields
	 * of an object.
	 * 
	 * @param st
	 *            {@link StringTemplate} to work on.
	 * @param o
	 *            Object to get the public fields of.
	 */
	public static void fillByFields(StringTemplate st, Object o) {
		if (st == null) {
			return;
		}
		List<Field> fields = ClassUtils.getFields(o);
		for (Field field : fields) {
			try {
				st.setAttribute(field.getName(), field.get(o));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fills the attributes of a StringTemplate by all availible public setters
	 * of an object.
	 * 
	 * @param st
	 *            {@link StringTemplate} to work on.
	 * @param o
	 *            Object to get the public fields of.
	 */
	public static void fillByGetters(StringTemplate st, Object o) {
		if (st == null) {
			return;
		}
		List<Method> methods = ClassUtils.getGetters(o);
		for (Method method : methods) {
			try {
				Object returnValue = method.invoke(o);
				st.setAttribute(ClassUtils.getterToName(method), returnValue);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Loads a template by name. Templates are searched in the resources area in
	 * the folder /stringtemp and a subfolder for the type of the template. The
	 * name finding algorithm is the best fit algorithm discussed at
	 * {@link ClassHelpers}.
	 * 
	 * @param templateClass
	 * @param type
	 * @return Loaded StringTemplate
	 */
	public static StringTemplate getStringTemplate(String templateClass,
			String type) {
		String lastTemplateName = "";
		StringTemplateGroup stg = null;

		try {
			stg = new StringTemplateGroup("templates", new File(
					TemplateUtils.class.getResource("/stringtemp/" + type)
							.toURI()).getAbsolutePath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		stg.setFileCharEncoding("UTF-8");

		// kill the package
		String templateName = templateClass.replaceFirst(".*\\.", "");
		StringTemplate template = null;

		while (templateName.length() > 0
				&& !templateName.equals(lastTemplateName)) {
			try {
				template = stg.getInstanceOf(templateName);
				if (template != null) {
					return template;
				}
			} catch (IllegalArgumentException e) {
			}
			// nothing found
			lastTemplateName = templateName;
			templateName = templateName.replaceFirst("[A-Z][-a-z_0-9]*", "");
			continue;
		}
		return null;
	}

	public static String getTemplate(Object templateClass, String type) {
		if (templateClass == null) {
			return null;
		}

		return getTemplate(templateClass.getClass().getCanonicalName(), type);
	}

	public static String getTemplate(String templateName, String type) {
		File result = getTemplateFile(templateName, type);
		if (result != null) {
			return FileUtils.getString(result);
		}
		return null;
	}

	public static File getTemplateFile(String templateName, String type) {
		if (templateName == null) {
			return null;
		}
		type = type.toLowerCase();

		String lastTemplateName = "";

		// kill the package
		templateName = templateName.replaceFirst(".*\\.", "");

		// InputStream resourceAsStream = null;
		URL resourceUrl = null;
		ClassLoader cl = TemplateUtils.class.getClassLoader();
		while (templateName.length() > 0
				&& !templateName.equals(lastTemplateName)) {
			resourceUrl = cl.getResource("stringtemp/" + type + "/"
					+ templateName + ".st");

			if (resourceUrl == null) {
				// this renderer is not availible
				lastTemplateName = templateName;
				templateName = templateName
						.replaceFirst("[A-Z][-a-z_0-9]*", "");
				continue;
			}

			break; // we found the first matching template
		}
		if (resourceUrl == null) {
			return null;
		}

		try {
			return new File(resourceUrl.toURI());
		} catch (URISyntaxException e) {
		}

		return null;
	}
}
