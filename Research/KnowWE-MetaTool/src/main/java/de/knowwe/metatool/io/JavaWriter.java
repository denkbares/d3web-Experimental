/*
 * Copyright (C) 2011 University Wuerzburg, Computer Science VI
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
package de.knowwe.metatool.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;

import de.knowwe.metatool.ObjectType;
import de.knowwe.metatool.ParameterizedClass;
import de.knowwe.metatool.QualifiedClass;

/**
 * Writer which generates real Java classes by replacing the placeholder in the
 * template file (/src/main/resources/templates/ObjectType.java.tmpl) with the
 * values from a specified ObjectType object.
 *
 * TODO: Handle import of sectionfinder wrapped in ConstraintSectionFinder
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
	private final String SECTIONFINDERIMPORT = "%SECTIONFINDERIMPORT%";
	private final String SECTIONFINDER = "%SECTIONFINDER%";
	private final String CONSTRAINTIMPORTS = "%CONSTRAINTIMPORTS%";
	private final String CONSTRAINTS = "%CONSTRAINTS%";
	private final String CHILDINSTANTIONS = "%CHILDREN%";
	private final String CHILDIMPORTS = "%CHILDIMPORTS%";
	private final String STYLERENDERERIMPORT = "%STYLERENDERERIMPORT%";
	private final String STYLERENDER = "%STYLERENDERER%";
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

		boolean constraints = type.getConstraints().size() > 0;
		template = replaceSectionFinder(type.getSectionFinder(), template, constraints);
		template = replaceConstraints(type.getConstraints(), template);
		template = replaceColor(type.getColor(), template);

		// save JAVA file
		BufferedWriter bw = new BufferedWriter(w);
		bw.write(template);
		bw.close();
	}

	private String replaceSectionFinder(ParameterizedClass sectionFinder, String template, boolean constraints) {
		if (sectionFinder == null) {
			template = template.replaceAll(SECTIONFINDERIMPORT, "");
			return template.replaceAll(SECTIONFINDER, "");
		}

		// SectionFinder != null
		template = template.replaceAll(SECTIONFINDERIMPORT,
				"import " + sectionFinder.getQualifiedClassName() + ";");

		StringBuilder instantiation = new StringBuilder();
		// ConstraintSectionFinder c = new ConstraintSectionFinder(new
		// Class(parameter));
		if (constraints) {
			instantiation.append(INDENT);
			instantiation.append("ConstraintSectionFinder c = ");
			instantiation.append(sectionFinder.getInstantiationString());
			instantiation.append(";\n");
			instantiation.append(INDENT);
			instantiation.append("setSectionFinder(c);");
		}
		// setSectionFinder(new Class(parameter));
		else {
			instantiation.append(INDENT);
			instantiation.append("setSectionFinder(");
			instantiation.append(sectionFinder.getInstantiationString());
			instantiation.append(");");
		}

		return template.replaceAll(SECTIONFINDER, instantiation.toString());
	}

	private String replaceChildren(Collection<ObjectType> children, String template) {
		StringBuilder imports = new StringBuilder();
		StringBuilder instantiations = new StringBuilder();
		for (ObjectType c : children) {
			imports.append("import ");
			imports.append(c.getPackageName());
			imports.append(".");
			imports.append(c.getClassName());
			imports.append(";\n");
			instantiations.append(INDENT);
			instantiations.append("childrenTypes.add(");
			instantiations.append(c.getInstantiationString());
			instantiations.append(");\n");
		}
		if (instantiations.length() > 0) {
			instantiations.delete(instantiations.length() - 1, instantiations.length());
		}
		template = template.replaceAll(CHILDIMPORTS, imports.toString());
		return template.replaceAll(CHILDINSTANTIONS, instantiations.toString());
	}

	private String replaceConstraints(Collection<QualifiedClass> constraint, String template) {
		StringBuilder imports = new StringBuilder();
		StringBuilder instantiations = new StringBuilder();
		for (QualifiedClass c : constraint) {
			imports.append("import ");
			imports.append(c.getPackageName());
			imports.append(".");
			imports.append(c.getClassName());
			imports.append(";\n");
			instantiations.append(INDENT);
			instantiations.append("c.addConstraint(");
			instantiations.append(c.getSingletonInstantiationString());
			instantiations.append(");\n");
		}
		if (instantiations.length() > 0) {
			instantiations.delete(instantiations.length() - 1, instantiations.length());
		}
		template = template.replaceAll(CONSTRAINTIMPORTS, imports.toString());
		return template.replaceAll(CONSTRAINTS, instantiations.toString());
	}

	private String replaceColor(String color, String template) {
		String imports = "";
		StringBuilder instantiation = new StringBuilder();
		if (color != null) {
			imports = "import de.d3web.we.kdom.rendering.StyleRenderer;";
			instantiation.append(INDENT);
			instantiation.append("setCustomRenderer(new StyleRenderer(\"color:");
			instantiation.append(color);
			instantiation.append("\"));\n");
		}
		if (instantiation.length() > 0) {
			instantiation.delete(instantiation.length() - 1, instantiation.length());
		}
		template = template.replaceAll(STYLERENDERERIMPORT, imports);
		return template.replaceAll(STYLERENDER, instantiation.toString());
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
