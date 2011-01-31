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
package de.knowwe.kdom.generator.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;

import de.knowwe.kdom.generator.ObjectType;

/**
 * Writer which generates real Java classes by replacing the placeholder in the
 * template file (/src/main/resources/templates/ObjectType.java.tmpl) with the
 * values from a specified ObjectType object.
 * 
 * @author Sebastian Furth
 * @created Jan 21, 2011
 */
public class JavaWriter implements ObjectTypeWriter {

	// The template file
	private final String TEMPLATE = "src/main/resources/templates/ObjectType.java.tmpl";

	// Wildcards in the template file
	private final String PACKAGE = "%PACKAGE%";
	private final String CLASSNAME = "%CLASSNAME%";
	private final String SUPERTYPEPACKAGE = "%SUPERTYPEPACKAGE%";
	private final String SUPERTYPECLASSNAME = "%SUPERTYPECLASS%";
	private final String CHILDINSTANTIONS = "%CHILDREN%";
	private final String CHILDIMPORTS = "%CHILDIMPORTS%";
	private final String INDENT = "		";

	// Singleton-Pattern
	private static final JavaWriter instance = new JavaWriter();

	private JavaWriter() {
	}

	public static JavaWriter getInstance() {
		return instance;
	}

	@Override
	public void write(ObjectType type, Writer w) throws IOException {

		if (type == null || w == null) {
			throw new IllegalArgumentException();
		}

		// read template
		String template = loadTemplate();

		// replace wildcards
		template = template.replaceAll(PACKAGE, type.getPackageName());
		template = template.replaceAll(CLASSNAME, type.getClassName());
		template = template.replaceAll(SUPERTYPECLASSNAME, type.getSuperType().getClassName());
		template = template.replaceAll(SUPERTYPEPACKAGE, type.getSuperType().getPackageName());
		template = replaceChildren(type.getChildren(), template);

		// save JAVA file
		BufferedWriter bw = new BufferedWriter(w);
		bw.write(template);
		bw.close();
	}

	private String replaceChildren(List<ObjectType> children, String template) {
		StringBuilder imports = new StringBuilder();
		StringBuilder instantions = new StringBuilder();
		for (ObjectType c : children) {
			imports.append("import ");
			imports.append(c.getPackageName());
			imports.append(".");
			imports.append(c.getClassName());
			imports.append(";\n");
			instantions.append(INDENT);
			instantions.append("childrenTypes.add(new ");
			instantions.append(c.getClassName());
			instantions.append("());\n");
		}
		template = template.replaceAll(CHILDIMPORTS, imports.toString());
		return template.replaceAll(CHILDINSTANTIONS, instantions.toString());
	}

	private String loadTemplate() throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(TEMPLATE), "UTF-8"));
		int currentChar = bufferedReader.read();
		while (currentChar != -1) {
			content.append((char) currentChar);
			currentChar = bufferedReader.read();
		}
		bufferedReader.close();
		return content.toString();
	}

}
