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

import java.util.Vector;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.d3web.proket.data.IDialogObject;
import de.d3web.proket.data.InheritableAttributes;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.XMLUtils;

/**
 * Generalized parser for parsing xml input into {@link IDialogObject}s.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public class XMLDialogObjectParser implements IDialogObjectParser {
	protected String base;
	protected IDialogObject dialogObject;
	protected String subType;
	protected String type;

	@Override
	public IDialogObject parse(IParser parser, Element tag,
			IDialogObject parent, InheritableAttributes parentStyle) {
		prepareType(parser, tag, parent, parentStyle);
		dialogObject = parseThisDialogObject(parser, tag, parent, parentStyle);
		dialogObject = parseChildren(parser, tag, dialogObject,
				dialogObject.getInheritableAttributes(), dialogObject);
		return dialogObject;
	}

	protected IDialogObject parseChildren(IParser parser, Element tag,
			IDialogObject parent, InheritableAttributes parentStyle,
			IDialogObject dialogObject) {
		// parse children
		NodeList childrenList = tag.getChildNodes();
		Vector<IDialogObject> children = new Vector<IDialogObject>();
		Node child = null;
		String name = null;
		for (int i = 0; i < childrenList.getLength(); i++) {
			child = childrenList.item(i);
			name = child.getNodeName();
			if (name.startsWith("#")) {
				continue;
			}
			Element childTag = (Element) child;

			// parse here
			IDialogObjectParser childParser = parser.getParser(childTag);
			IDialogObject childObject = childParser.parse(parser, childTag,
					dialogObject, dialogObject.getInheritableAttributes());
			if (childObject != null) {
				children.add(childObject);
			}
		}
		dialogObject.setChildren(children);

		return dialogObject;
	}

	protected IDialogObject parseThisDialogObject(IParser parser, Element tag,
			IDialogObject parent, InheritableAttributes parentStyle) {
		String className = ClassUtils
				.getVirtualClassName(subType, type, base);
		Class<? extends Object> dialogObjectClass = ClassUtils.getBestClass(
				className, "de.d3web.proket.data", "");
		IDialogObject dialogObject = null;
		try {
			dialogObject = (IDialogObject) dialogObjectClass.newInstance();
		} catch (NullPointerException e) {
			return null;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
		if (dialogObject == null) {
			return null;
		}
		// make the tree traversable in both directions
		dialogObject.setParent(parent);

		// fill by public setters and fields
		XMLParser.fill(dialogObject, tag);
		// remember the tag for later analysis
		dialogObject.setXMLTag(tag);
		// style and inheritance
		dialogObject.setInheritableAttributes(new InheritableAttributes(
				dialogObject));
		// dialogObject.getStyle().setParent(parentStyle);
		XMLParser.fill(dialogObject.getInheritableAttributes(), tag);

		// events are normally parsed and add themselves to their parents
		// (returning null!)

		return dialogObject;
	}

	protected void prepareType(IParser parser, Element tag,
			IDialogObject parent, InheritableAttributes parentStyle) {
		// get best fitting parser class
		base = XMLUtils.getNodeName(tag);
		type = XMLUtils.getStr(tag, "type");
		if (type == null && parent != null) {
			type = parent.getType();
		}
		subType = XMLUtils.getStr(tag, "subtype");
		if (subType == null && parent != null) {
			subType = parent.getSubType();
		}
	}
}
