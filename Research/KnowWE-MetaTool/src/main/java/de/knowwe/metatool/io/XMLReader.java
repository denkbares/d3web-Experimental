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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.knowwe.metatool.ObjectType;
import de.knowwe.metatool.ParameterizedClass;
import de.knowwe.metatool.QualifiedClass;

/**
 * SAX based Parser for the XML-Persistence of ObjectTypes. Please note that we
 * assume that the XML-file which will be parsed was validated with a XML-Schema
 * or DTD.
 *
 * @see ObjectType
 * @author Sebastian Furth
 * @created Jan 31, 2011
 */
public class XMLReader implements ObjectTypeReader {

	public ObjectType read(File input) throws IOException {

		if (input == null) {
			throw new IllegalArgumentException();
		}

		ObjectType result = null;

		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			ObjectTypeHandler handler = new ObjectTypeHandler();

			InputStream inputStream = new FileInputStream(input);
			Reader reader = new InputStreamReader(inputStream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, handler);

			result = handler.getObjectType();
		}
		catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * SAX-Handler for XML-Persistence of ObjectTypes
	 *
	 * @author Sebastian Furth
	 * @created Jan 31, 2011
	 */
	private class ObjectTypeHandler extends DefaultHandler {

		private ObjectType root;

		private ObjectType.Builder builder;
		private final HashMap<String, ObjectType> typesByID = new HashMap<String, ObjectType>();

		private ObjectType nextParent;
		private int nextPosition;
		private boolean color;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("OBJECTTYPE")) {
				createMinimalBuilder(attributes);
				changeSuperType(attributes);
				prepareChildrenAddition(attributes);
			}
			if (qName.equalsIgnoreCase("SECTIONFINDER")) {
				setSectionFinder(attributes);
			}
			if (qName.equalsIgnoreCase("CONSTRAINT")) {
				addConstraint(attributes);
			}
			if (qName.equalsIgnoreCase("COLOR")) {
				color = true;
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			if (color) {
				builder.setColor(new String(ch, start, length));
			}
		}

		private void createMinimalBuilder(Attributes attributes) {
			String id = attributes.getValue("ID");
			String className = attributes.getValue("ClassName");
			String packageName = attributes.getValue("PackageName");
			boolean exists = Boolean.parseBoolean(attributes.getValue("Exists"));

			QualifiedClass objectTypeClass = new QualifiedClass(packageName, className);
			builder = new ObjectType.Builder(id, objectTypeClass, exists);
		}

		private void changeSuperType(Attributes attributes) {
			String className = attributes.getValue("SuperTypeClass");
			String packageName = attributes.getValue("SuperTypePackage");

			QualifiedClass superTypeClass = new QualifiedClass(packageName, className);
			builder.setSuperType(superTypeClass);
		}

		private void prepareChildrenAddition(Attributes attributes) {
			String parent = attributes.getValue("Parent");
			String position = attributes.getValue("Position");
			if (parent != null && typesByID.containsKey(parent)) {
				nextParent = typesByID.get(parent);
				nextPosition = Integer.parseInt(position);
			}
			else if (root != null && (parent == null || !typesByID.containsKey(parent))) {
				throw new NoSuchElementException(
						"ObjectType with ID \""
								+ parent
								+ "\" doesn't exist. Unable to add child. Parents have to parsed before children!");
			}
		}

		private void setSectionFinder(Attributes attributes) {
			if (builder == null) {
				throw new NullPointerException(
						"There is no builder! You have specified a sectionFinder outside of an ObjectType element");
			}

			// Set SectionFinder
			String packageName = attributes.getValue("PackageName");
			String className = attributes.getValue("ClassName");
			String value = attributes.getValue("Value") != null ? attributes.getValue("Value") : "";
			ParameterizedClass sectionFinder = new ParameterizedClass(packageName, className, value);
			builder.setSectionFinder(sectionFinder);
		}

		private void addConstraint(Attributes attributes) {
			if (builder == null) {
				throw new NullPointerException(
						"There is no builder! Probably you have defined the constraint on a wrong position.");
			}

			// Add Constraint
			String packageName = attributes.getValue("PackageName");
			String className = attributes.getValue("ClassName");
			QualifiedClass constraint = new QualifiedClass(packageName, className);
			builder.addConstraint(constraint);

		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equalsIgnoreCase("OBJECTTYPE")) {
				// Create ObjectType
				ObjectType temp = builder.build();
				typesByID.put(temp.getID(), temp);

				// Add ObjectType as child
				if (nextParent != null) {
					nextParent.addChild(nextPosition, temp);
				}
				else if (nextParent == null && root != null) {
					throw new IllegalStateException(
							"The following ObjectType is not the root but has no parent: "
									+ temp.getClassName());
				}

				// Create root
				if (root == null) {
					root = temp;
				}
			}
			else if (qName.equalsIgnoreCase("COLOR")) {
				color = false;
			}
		}

		/**
		 * Returns the "Root"-ObjectType which contains all other parsed
		 * ObjectTypes as successors.
		 *
		 * @created Jan 31, 2011
		 * @return "Root"-ObjectType.
		 */
		public ObjectType getObjectType() {
			return root;
		}

	}

}
