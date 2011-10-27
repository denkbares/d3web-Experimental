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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.knowwe.metatool.MetatoolParseException;
import de.knowwe.metatool.ObjectType;
import de.knowwe.metatool.ParameterizedClass;
import de.knowwe.metatool.ParserContext;
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
	private ParserContext parserContext;
	
	public XMLReader(ParserContext parserContext) {
		this.parserContext = parserContext;
	}

	public ObjectType read(InputStream stream) throws IOException, MetatoolParseException {
		if (stream == null) {
			throw new IllegalArgumentException();
		}

		ObjectType result = null;

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			ObjectTypeHandler handler = new ObjectTypeHandler();

			Reader reader = new InputStreamReader(stream, "UTF-8");

			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");

			saxParser.parse(is, handler);

			result = handler.getObjectType();
		}
		catch (ParserConfigurationException e) {
			throw new MetatoolParseException(e.getMessage(), null);
		}
		catch (SAXException e) {
			if (e.getException() instanceof MetatoolParseException) {
				throw (MetatoolParseException) e.getException();
			}
		}
		catch (UnsupportedEncodingException e) {
			throw new MetatoolParseException(e.getMessage(), null);
		}

		return result;
	}
	
	public ObjectType read(File input) throws IOException, MetatoolParseException {
		if (input == null) {
			throw new IllegalArgumentException();
		}
		
		InputStream is = new FileInputStream(input);
		return read(is);
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
		
		private Locator locator;
		
		/* (non-Javadoc)
		 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
		 */
		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

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

		private void createMinimalBuilder(Attributes attributes) throws SAXException {
			String id = attributes.getValue("ID");
			String className = null;
			String packageName = null;
			boolean exists = Boolean.parseBoolean(attributes.getValue("Exists"));
			
			String qualifiedClass = attributes.getValue("QualifiedName");
			
			if (qualifiedClass != null) {
				int pos = qualifiedClass.lastIndexOf(".");
				
				packageName = qualifiedClass.substring(0, pos);
				className = qualifiedClass.substring(pos + 1);
			} else {
				className = attributes.getValue("ClassName");
				packageName = attributes.getValue("PackageName");
			}

			QualifiedClass objectTypeClass;
			try {
				objectTypeClass = new QualifiedClass(packageName, className);
			} catch (IllegalArgumentException e) {
				throw new SAXException(new MetatoolParseException("Cannot create ObjectType: " + e.getMessage(), locator));
			}
			
			try {
				builder = new ObjectType.Builder(id, objectTypeClass, exists);
			} catch (IllegalArgumentException e) {
				throw new SAXException(new MetatoolParseException("Cannot create ObjectType Builder: " + e.getMessage(), locator));
			}
		}

		private void changeSuperType(Attributes attributes) throws SAXException {
			String className = null;
			String packageName = null;
			
			String qualifiedSuperType = attributes.getValue("QualifiedSuperType");
			
			// Fallback to old method if needed
			if (qualifiedSuperType != null) {
				int pos = qualifiedSuperType.lastIndexOf(".");
				
				packageName = qualifiedSuperType.substring(0, pos - 1);
				className = qualifiedSuperType.substring(pos + 1);
			} else {
				className = attributes.getValue("SuperTypeClass");
				packageName = attributes.getValue("SuperTypePackage");
			}
			
			// Default if even the old method doesn't yield a result
			if (className == null && packageName == null) {
				packageName = "de.knowwe.core.kdom";
				className = "AbstractType";
			}

			QualifiedClass superTypeClass;
			try {
				superTypeClass = new QualifiedClass(packageName, className);
			} catch (IllegalArgumentException e) {
				throw new SAXException(new MetatoolParseException("Cannot set super type: " + e.getMessage(), locator));
			}
			
			builder.setSuperType(superTypeClass);
		}

		private void prepareChildrenAddition(Attributes attributes) throws SAXException {
			String parent = attributes.getValue("Parent");
			String position = attributes.getValue("Position");
			if (parent != null && position != null && typesByID.containsKey(parent)) {
				nextParent = typesByID.get(parent);
				nextPosition = Integer.parseInt(position);
			}
			else if (root != null) {
				if (parent == null || !typesByID.containsKey(parent)) {
					throw new SAXException(new MetatoolParseException(
							"ObjectType with ID \""
									+ parent
									+ "\" doesn't exist. Unable to add child. Parents have to parsed before children!",
							locator
							));
				} else if (position == null) {
					throw new SAXException(new MetatoolParseException("Missing or invalid Position parameter", locator));
				}
			}
		}

		private void setSectionFinder(Attributes attributes) throws SAXException {
			if (builder == null) {
				throw new SAXException(new MetatoolParseException(
						"There is no builder! You have specified a sectionFinder outside of an ObjectType element",
						locator));
			}

			// Set SectionFinder
			String packageName = null;
			String className = null;
			String value = attributes.getValue("Value") != null ? attributes.getValue("Value") : "";
			
			String qualifiedName = attributes.getValue("QualifiedName");
			if (qualifiedName != null) {
				int pos = qualifiedName.lastIndexOf(".");
				
				// Default package name if no . is in the QualifiedName attribute
				if (pos >= 0) {
					packageName = qualifiedName.substring(0, pos - 1);
					className = qualifiedName.substring(pos + 1);
				} else {
					packageName = "de.knowwe.core.kdom.sectionFinder";
					className = qualifiedName;
				}
			} else {
				packageName = attributes.getValue("PackageName");
				className = attributes.getValue("ClassName");
			}
			
			ParameterizedClass sectionFinder;
			try {
				sectionFinder = new ParameterizedClass(packageName, className, value);
			} catch (IllegalArgumentException e) {
				throw new SAXException(new MetatoolParseException("Cannot set SectionFinder: " + e.getMessage(), locator));
			}
			
			builder.setSectionFinder(sectionFinder);
		}

		private void addConstraint(Attributes attributes) throws SAXException {
			if (builder == null) {
				throw new SAXException(new MetatoolParseException(
						"There is no builder! Probably you have defined the constraint on a wrong position.",
						locator));
			}

			// Add Constraint
			String packageName = null;
			String className = null;
			
			String qualifiedName = attributes.getValue("QualifiedName");
			if (qualifiedName != null) {
				int pos = qualifiedName.lastIndexOf(".");
				
				// Default package name if no . is in the QualifiedName attribute
				if (pos >= 0) {
					packageName = qualifiedName.substring(0, pos - 1);
					className = qualifiedName.substring(pos + 1);
				} else {
					packageName = "de.knowwe.kdom.constraint";
					className = qualifiedName;
				}
			} else {
				packageName = attributes.getValue("PackageName");
				className = attributes.getValue("ClassName");
			}
			
			QualifiedClass constraint;
			try {
				constraint = new QualifiedClass(packageName, className);
			} catch (IllegalArgumentException e) {
				throw new SAXException(new MetatoolParseException("Cannot add constraint: " + e.getMessage(), locator));
			}
			
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
					try {
						nextParent.addChild(nextPosition, temp);
					} catch (IllegalArgumentException e) {
						throw new SAXException(new MetatoolParseException(
								"The Position parameter is invalid.", locator));
					}
				}
				else if (nextParent == null && root != null) {
					throw new SAXException(new MetatoolParseException(
							"The following ObjectType is not the root but has no parent: "
									+ temp.getClassName(),
							locator));
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
