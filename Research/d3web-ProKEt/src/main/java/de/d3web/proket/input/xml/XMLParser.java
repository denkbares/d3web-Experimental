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
package de.d3web.proket.input.xml;

import de.d3web.proket.data.DialogTree;
import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.FileUtils;
import de.d3web.proket.utils.StringUtils;
import de.d3web.proket.utils.XMLUtils;
import java.awt.Color;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Class for parsing a given XML file.
 *
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public class XMLParser implements IParser {

    // TODO create a default.xml better
    // default dialog/xml that is parsed if nothing else is given
    private String xMLFilename = "Standarddialog";
    private Node dialogSpec = null;
    private Node dataSpec = null;

    /**
     * Constructor specifying the XML file
     *
     * @param xMLFilename
     */
    public XMLParser(Object xMLFilename) {
        super();
        this.xMLFilename = (String) xMLFilename;
        if (!this.xMLFilename.endsWith(".xml")) {
            this.xMLFilename += ".xml";
        }

    }

    /**
     * Fill an object with the values from an XML file. Using reflection.
     *
     * @created 11.10.2010
     *
     * @param o The object to be filled
     * @param tag the XML tag the values are retrieved from
     */
    public static void fill(Object o, Element tag) {

        // Fill the fields first: get the fields of the object
        List<Field> fields = ClassUtils.getFields(o);
        for (Field field : fields) {
            Class<?> fieldType = field.getType();

            // name of the xml attribute. In case of simple field names, as e.g.
            // "name", it is also "name" in the XML, otherwise it is converted
            // by subsequent method call
            String xmlAttributeName = StringUtils.fieldToXml(field.getName());
            Object value = null;

            // get the corresponding value from the XML tag
            // depending on the type of the type of the field
            if (fieldType.equals(String.class)) {
                value = XMLUtils.getStr(tag, xmlAttributeName, null);
                value = handleString((String) value);
            } else if (fieldType.equals(Integer.class)) {
                value = XMLUtils.getInt(tag, xmlAttributeName, null);
            } else if (fieldType.equals(Double.class)) {
                value = XMLUtils.getDouble(tag, xmlAttributeName, null);
            } else if (fieldType.equals(Color.class)) {
                value = XMLUtils.getColor(tag, xmlAttributeName, null);
            } else if (fieldType.equals(Boolean.class)) {
                value = XMLUtils.getBoolean(tag, xmlAttributeName, null);
            }

            // no correct value could be parsed, continue; thus the null-default
            // value in XMLUtils is reasonable!
            if (value == null) {
                continue;
            }

            try {
                // assign the value to the field by reflection
                field.set(o, value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Handle setters: get setter methods first
        List<Method> setters = ClassUtils.getSetters(o);
        for (Method setter : setters) {

            // read setter type
            Class<?> paramType = setter.getParameterTypes()[0];
            String xmlAttributeName =
                    StringUtils.fieldToXml(
                    ClassUtils.setterToXMLName(setter));

            Object value = null;
            if (paramType.equals(String.class)) {
                value = XMLUtils.getStr(tag, xmlAttributeName, null);
                value = handleString((String) value);
            } else if (paramType.equals(Integer.class)) {
                value = XMLUtils.getInt(tag, xmlAttributeName, null);
            } else if (paramType.equals(Double.class)) {
                value = XMLUtils.getDouble(tag, xmlAttributeName, null);
            } else if (paramType.equals(Color.class)) {
                value = XMLUtils.getColor(tag, xmlAttributeName, null);
            } else if (paramType.equals(Boolean.class)) {
                value = XMLUtils.getBoolean(tag, xmlAttributeName, null);
            }

            // handle default null value
            if (value == null) {
                continue;
            }

            // invoke the object's setter method with the retrieved value
            try {
                setter.invoke(o, value);
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
     * Method that processes Strings read from an XML file and correctly handles
     * potentially linked sources as web-links or text/HTML-fragments.
     *
     * @param value String as read from the XML attribute.
     * @return String that contains the actual data (including e.g. web/text)
     */
    private static Object handleString(String value) {

        // if nothing, return null. TODO: really return null?!
        if (value == null) {
            return null;
        }

        // if given, handle file (fragment). Has to be specified with
        // the corresponding folder/path relatively to the resources-folder
        if (value.startsWith("file:")) {
            try {
                return FileUtils.getString(
                        FileUtils.getResourceFile(
                        value.replaceFirst("file:", "")));
            } catch (FileNotFoundException e) {
                return value;
            }

            // get data from the web in case a weblink is specified
        } else if (value.startsWith("web:")) {
            URL url;
            StringBuilder sb = new StringBuilder();
            try {
                url = new URL(value.replaceFirst("web:", ""));
                BufferedReader in =
                        new BufferedReader(
                        new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    sb.append(inputLine);
                }
                in.close();
            } catch (MalformedURLException e) {
                return value;
            } catch (IOException e) {
                return value;
            }
            return sb.toString();
        }
        return value;
    }

    /**
     * Retrieve the parser for a given XML element.
     *
     * @param tag the XML tag
     * @return IDialogObjectParser the retrieved parser
     */
    public IDialogObjectParser getParser(Element tag) {

        // get the appropriate parser
        Class<? extends Object> parserClass = getParserClass(tag);
        try {
            return (IDialogObjectParser) parserClass.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Retrieve an appropriate parser class for a given XML element
     *
     * @param tag the XML tag
     * @return the parser class
     */
    public Class<? extends Object> getParserClass(Element tag) {
        String type = null, subType = null;

        // get the subtype and type attributes from the parsed tags
        subType = XMLUtils.getStr(tag, "subtype");
        type = XMLUtils.getStr(tag, "type");

        // get the tag name, e.g. "dialog"
        String clazz = tag.getNodeName();

        // try to get the best parser class with the class finding algorithm
        // (cut until it fits). Yet: TODO do we need it? Parser is always
        // the same and always cut unti XMLDialogObjectParser remains...
        return ClassUtils.getBestClass(
                ClassUtils.getVirtualClassName(subType, type, clazz),
                "de.d3web.proket.input.xml", "XMLDialogObjectParser");
    }

    /**
     * Parses the XML and returns the internal DialogTree representation.
     */
    public DialogTree getTree() {
        File inputFile;
        try {
            // try to get the corresponding XML from the resources folder
            inputFile = FileUtils.getResourceFile("/specs/prototypes/" + xMLFilename);
            System.out.println(inputFile);

            if (inputFile == null) {
                try {
                    // try to get the corresponding XML from the resources
                    // folder
                    inputFile = FileUtils.getResourceFile("/specs/d3webLoose/" + xMLFilename);
                } catch (FileNotFoundException e3) {
                    return null;
                }
            }
        } catch (FileNotFoundException e2) {
            return null;
        }

        // call generator for generating the inbetween-version of the XML needed
        // case some elements had to be autogenerated
		/*
         * TODO: find a way to not always call this one?!
         */
        File outputFile;
        try {
            // regex replaces the filename \Q...\E treats filename as literal
            // inbetween and the linefeed with generated.filename
            outputFile = new File(inputFile.getCanonicalPath().replaceAll(
                    "\\Q" + xMLFilename + "\\E$", "generated." + xMLFilename));

            // create Generator and generate elements
            Generator generator = new Generator(inputFile, outputFile);
            generator.generate();
            inputFile = outputFile;
            // at this point, inputFile contains the manually specified XML, &
            // potentially given some q/a's of generate tags, & potentially
            // filled in additional id's of objects that were not defined by
            // hand

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // process the file with the generated elements if any
        DialogTree dialogTree = new DialogTree();
        try {
            Element root = XMLUtils.getRoot(inputFile, null); // xMLSchemaFilename);
            Class<? extends Object> parserClass = getParserClass(root);
            IDialogObjectParser parser = (IDialogObjectParser) parserClass.newInstance();
            IDialogObject rootObject = parser.parse(this, root, null, null);
            dialogTree.setRoot(rootObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO just for curiosity: print the dialog tree here to see what
        // the hierarchy dialog is before and after

        // make the tree obey the parent-id attributes (hierarchy)
        List<IDialogObject> elements = dialogTree.asList();
        for (IDialogObject element : elements) {
            String parentId = element.getXMLTag().getAttribute("parent-id");
            if (parentId != null && !parentId.equals("")) {
                // find new parent
                IDialogObject newParentObject = dialogTree.getById(parentId);
                if (newParentObject != null) {
                    // remove as old parent's child
                    if (element.getParent() != null) {
                        element.getParent().getChildren().remove(element);
                    }
                    // set new parent
                    element.setParent(newParentObject);
                    newParentObject.addChild(element);
                }
            }
        }

        return dialogTree;
    }

    /**
     * Return the knowledgebase as specified per its filename in the parsed XML
     *
     * @created 13.10.2010
     *
     * @return the KnowledgeBase
     * @throws IOException
     */
    /*@Override
    public KnowledgeBase getKb() {
        KnowledgeBase kb = null;
        String kbname = getKnowledgeBaseName();
        try {
            kb = D3webUtils.getKnowledgeBase(kbname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return kb;
    }*/

    /**
     * Retrieve the name of the knowledge base specified in the parsed XML.
     *
     * @created 13.10.2010
     *
     * @return the String representation of the name of the kb
     */
    /*public String getKnowledgeBaseName() {
        if (dataSpec == null) {
            initD3webSpec();
        }
        String kbname = "";
        kbname = XMLUtils.getStr((Element) dataSpec, "kb", null);

        return kbname;
    }*/

    /*private void initD3webSpec() {
        File inputFile = null;
        try { // try to get the corresponding XML from the resources folder
            inputFile =
                    FileUtils.getResourceFile("/specs/d3webLoose/" + xMLFilename);
        } catch (FileNotFoundException e3) {
        }

        if (inputFile != null) {
            try { // try to read xml root node
                dialogSpec = XMLUtils.getRoot(inputFile, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        NodeList children = dialogSpec.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            String name = child.getNodeName();
            if (name.startsWith("#")) {
                continue;
            }
            dataSpec = child;
        }
    }*/
}
