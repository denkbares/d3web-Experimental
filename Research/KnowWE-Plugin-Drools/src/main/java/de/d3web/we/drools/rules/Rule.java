/*
 * Copyright (C) 2009 Chair of Artificial Intelligence and Applied Informatics
 *                    Computer Science VI, University of Wuerzburg
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
package de.d3web.we.drools.rules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.io.Resource;
import org.drools.io.ResourceFactory;

/**
 * This class encapsulates a Rule and provides a way to transform it to a Drools Resource.
 */
public class Rule {
	/**
	 * The content of the rule as entered in the Wiki
	 */
	private final String content;

	private final String name;

	public static final String packageName = "knowwe.drools";

	/**
	 * Creates a new Rule object
	 * @param content The text as entered in the Wiki
	 */
	public Rule(String content) {
		StringBuffer buffy = new StringBuffer();
		buffy.append("package " + packageName + ";\n");
		buffy.append("import de.d3web.we.drools.terminology.*;\n");
		
		for (String s : new String[]{"P", "N"}) {
			for (int i = 1; i <= 7; i++)
				buffy.append("global SolutionScore " + s + i + ";\n");
		}
		
		buffy.append("\n");
		buffy.append(content);
		Pattern p = Pattern.compile("rule \"(.+)\"");
		Matcher m = p.matcher(content);
		if (m.find()) {
			this.name = m.group(1);
		}
		else {
			this.name = null;
		}
		this.content = buffy.toString();
//		System.out.println(buffy);
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Reads a file into a Rule object
	 * @param file File to read
	 * @return Rule object
	 */
	public static Rule fromFile(File file) {
		BufferedReader br;
		StringBuffer buffy = new StringBuffer();

		try {
			br = new BufferedReader(new FileReader(file));

			while (br.ready()) {
				buffy.append(br.readLine() + "\n");
			}
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("Rules input file not found.");
		} catch (IOException e) {
			throw new RuntimeException("IOException: " + e.getMessage());
		}
		
		return new Rule(buffy.toString());
	}
	
	/**
	 * Returns the Rule as a Drools Resource
	 * @return the Rule in Resource format
	 */
	public Resource toResource() {
		return ResourceFactory.newReaderResource(new StringReader(content));
	}
}
