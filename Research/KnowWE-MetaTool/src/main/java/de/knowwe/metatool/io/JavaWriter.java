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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

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
	private final String IMPORTS = "%IMPORTS%";
	private final String SUPERTYPE = "%SUPERTYPE%";
	private final String SECTIONFINDER = "%SECTIONFINDER%";
	private final String CONSTRAINTS = "%CONSTRAINTS%";
	private final String CHILDREN = "%CHILDREN%";
	private final String ANONYMOUSCHILDREN = "%ANONYMOUSCHILDREN%";
	private final String STYLERENDER = "%STYLERENDERER%";
	private final String INDENT = "		";


	// Singleton-Pattern
	private static final JavaWriter instance = new JavaWriter();

	protected JavaWriter() {
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
		template = template.replaceAll(SUPERTYPE, type.getSuperType().getClassName());
		template = replaceAnonymousChildren(type.getImplicitAnonymousChildren(), template);
		template = replaceChildren(type.getChildren(), template);

		boolean constraints = type.getConstraints().size() > 0;
		template = replaceSectionFinder(type.getSectionFinder(), template, constraints);
		template = replaceConstraints(type.getConstraints(), template);
		template = replaceColor(type.getColor(), template);
		// do this at the end, because imports could have been added before
		template = replaceImports(type.getImports(), template);

		// save JAVA file
		BufferedWriter bw = new BufferedWriter(w);
		bw.write(template);
		bw.close();
	}

	private String replaceSectionFinder(ParameterizedClass sectionFinder, String template, boolean constraints) {
		if (sectionFinder == null) {
			return template.replaceAll(SECTIONFINDER, "");
		}

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

	private String replaceImports(Collection<QualifiedClass> imports, String template) {
		StringBuilder statements = new StringBuilder();
		for (QualifiedClass i : imports) {
			statements.append("import ");
			statements.append(i.getQualifiedClassName());
			statements.append(";\n");
		}
		if (statements.length() > 0) {
			statements.delete(statements.length() - 1, statements.length());
		}
		return template.replaceAll(IMPORTS, statements.toString());
	}

	private String replaceAnonymousChildren(Map<String, String> anonymousChildren, String template) {
		StringBuilder instantiations = new StringBuilder();
		for (String anonType : anonymousChildren.keySet()) {
			// Instantiation of AnonymousType
			instantiations.append(INDENT);
			instantiations.append("AnonymousType ");
			instantiations.append(anonType.toLowerCase());
			instantiations.append(" = new AnonymousType(\"");
			instantiations.append(anonType);
			instantiations.append("\");\n");
			// Configure SectionFinder
			instantiations.append(INDENT);
			instantiations.append(anonType.toLowerCase());
			instantiations.append(".setSectionFinder(");
			instantiations.append("new RegexSectionFinder(\"");
			instantiations.append(anonymousChildren.get(anonType));
			instantiations.append("\"));\n");
			// Add AnonymousType as child
			instantiations.append(INDENT);
			instantiations.append("childrenTypes.add(");
			instantiations.append(anonType.toLowerCase());
			instantiations.append(");\n");
		}
		if (instantiations.length() > 0) {
			instantiations.delete(instantiations.length() - 1, instantiations.length());
		}
		return template.replaceAll(ANONYMOUSCHILDREN, instantiations.toString());
	}

	private String replaceChildren(Collection<ObjectType> children, String template) {
		StringBuilder instantiations = new StringBuilder();
		for (ObjectType c : children) {
			instantiations.append(INDENT);
			instantiations.append("childrenTypes.add(");
			instantiations.append(c.getInstantiationString());
			instantiations.append(");\n");
		}
		if (instantiations.length() > 0) {
			instantiations.delete(instantiations.length() - 1, instantiations.length());
		}
		return template.replaceAll(CHILDREN, instantiations.toString());
	}

	private String replaceConstraints(Collection<QualifiedClass> constraint, String template) {
		StringBuilder instantiations = new StringBuilder();
		for (QualifiedClass c : constraint) {
			instantiations.append(INDENT);
			instantiations.append("c.addConstraint(");
			instantiations.append(c.getSingletonInstantiationString());
			instantiations.append(");\n");
		}
		if (instantiations.length() > 0) {
			instantiations.delete(instantiations.length() - 1, instantiations.length());
		}
		return template.replaceAll(CONSTRAINTS, instantiations.toString());
	}

	private String replaceColor(String color, String template) {
		StringBuilder instantiation = new StringBuilder();
		if (color != null) {
			instantiation.append(INDENT);
			instantiation.append("setCustomRenderer(new StyleRenderer(\"color:");
			instantiation.append(color);
			instantiation.append("\"));\n");
		}
		if (instantiation.length() > 0) {
			instantiation.delete(instantiation.length() - 1, instantiation.length());
		}
		return template.replaceAll(STYLERENDER, instantiation.toString());
	}

	private String loadTemplate() throws IOException {
		StringBuilder content = new StringBuilder();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(getTemplateInputStream(), "UTF-8"));
		int currentChar = bufferedReader.read();
		while (currentChar != -1) {
			content.append((char) currentChar);
			currentChar = bufferedReader.read();
		}
		bufferedReader.close();
		return content.toString();
	}
	
	protected InputStream getTemplateInputStream() throws IOException {
		return new FileInputStream(TEMPLATE);
	}
}
