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

package de.d3web.proket.data;

import java.text.MessageFormat;
import java.util.Vector;
import java.util.logging.Logger;

import org.w3c.dom.Element;

import de.d3web.proket.input.defaultsettings.GlobalSettings;
import de.d3web.proket.utils.ClassUtils;
import de.d3web.proket.utils.IDUtils;
import de.d3web.proket.utils.ProKEtLogger;

/**
 * Handles basic properties and functionality every {@link IDialogObject}
 * shares.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 * 
 */
public abstract class DefaultDialogObject implements IDialogObject {

	protected GlobalSettings defset = GlobalSettings.getInstance();
	protected static final Logger logger = ProKEtLogger.getLogger();
	protected String VCNbase = "";
	protected Vector<IDialogObject> children = new Vector<IDialogObject>();
	/**
	 * Either filename of a css template in /resources/templates/css or direct
	 * css code.
	 */
	protected String css;
	/**
	 * The ID as given by the user or generator.
	 */
	protected String id;
	protected String image;
	protected IDialogObject parent;
	protected Double rating;
	/**
	 * Indicates if this {@link IDialogObject} was already rendered in the
	 * current rendering run. Helps to build hierarchical dialogs for example.
	 */
	protected boolean rendered = false;
	/**
	 * Used for constructing tables that enable cross-browser multicolumn
	 * layouts
	 */
	protected int renderedChildrenCounter = 0;
	protected InheritableAttributes style;
	protected String subType;
	protected Element tag;
	protected String text;
	protected String title;
	protected String tooltip;
	protected String type;
	protected Boolean visible = true;
	protected org.w3c.dom.Element XMLTag;

	/**
	 * Retrieves the next best parent-element in the hierarchy that does NOT
	 * match the given class name
	 * 
	 * @param className class name.
	 * @return the parent element.
	 * 
	 *         TODO: maybe NOT needed anymore
	 */
	/*
	 * public IDialogObject getDifferentParent(String className) { IDialogObject
	 * result = this; try { do { result = result.getParent(); if (result ==
	 * null) return null;
	 * 
	 * } while ((Class.forName(className).isInstance(result))); } catch
	 * (ClassNotFoundException e) { e.printStackTrace(); } return result; }
	 */

	@Override
	/**
	 * Retrieves the next best parent-element in the hierarchy with the given
	 * name.
	 * 
	 * @param className The class name.
	 * @return The corresponding next best parent element.
	 */
	public IDialogObject getParent(String className) {

		// test the element itself first
		IDialogObject result = this;
		try {
			do {
				result = result.getParent();
				if (result == null) {
					return null;
				}

			} while (!(Class.forName(className).isInstance(result)));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Class<? extends Object> getRendererClass() {
		return getRendererClass(getVirtualClassName());
	}

	/**
	 * Retrieve the best fitting renderer for a given string.
	 * 
	 * @param base Name of the Renderer to start the search with.
	 * @return The class from de.d3web.proket.output.render with the suffix
	 *         "Renderer" that fits best to the string given. For a definition
	 *         of best fit see {@link ClassUtils}.
	 */
	protected Class<? extends Object> getRendererClass(String base) {
		return ClassUtils.getBestClass(base,
				defset.getProketPath() + "output.render.", "Renderer");
	}

	@Override
	/**
	 * Get the root parent object of the current one.
	 */
	public IDialogObject getRootParent() {
		IDialogObject parent = this;
		IDialogObject nextParent;
		while ((nextParent = parent.getParent()) != null) {
			parent = nextParent;
		}
		return parent;
	}

	@Override
	/**
	 * Get the virtual (internal) class name.
	 */
	public String getVirtualClassName() {
		IDialogObject dialog = getRootParent();
		return ClassUtils.getVirtualClassName(dialog.getSubType(),
				dialog.getType(), getSubType(), getType(), VCNbase);
	}

	@Override
	public void addChild(IDialogObject child) {
		children.add(child);
	}

	@Override
	public Vector<IDialogObject> getChildren() {
		return children;
	}

	@Override
	public String getCss() {
		return css;
	}

	@Override
	public Dialog getDialog() {
		return (Dialog) getParent(defset.getProketPath() + "data.Dialog");
	}

	@Override
	public String getFullId() {
		return IDUtils.getNamespacedId(id, VCNbase);
	}

	@Override
	public String getId() {
		return IDUtils.removeNamspace(id);
	}

	@Override
	public String getImage() {
		return image;
	}

	@Override
	public InheritableAttributes getInheritableAttributes() {
		// fill now if there was none before
		if (style == null) {
			style = new InheritableAttributes(this);
		}

		return style;
	}

	@Override
	public IDialogObject getParent() {
		return parent;
	}

	@Override
	public Double getRating() {
		return rating;
	}

	@Override
	public String getSubType() {
		return subType;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getTooltip() {
		return tooltip;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public org.w3c.dom.Element getXMLTag() {
		return XMLTag;
	}

	@Override
	public boolean isRendered() {
		return rendered;
	}

	@Override
	public Boolean isVisible() {
		return visible;
	}

	@Override
	public void setChildren(Vector<IDialogObject> children) {
		this.children = children;
	}

	@Override
	public void setCss(String css) {
		this.css = css;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public void setInheritableAttributes(InheritableAttributes style) {
		this.style = style;
	}

	@Override
	public void setParent(IDialogObject parent) {
		this.parent = parent;
	}

	@Override
	public void setRating(Double rating) {
		this.rating = rating;
	}

	@Override
	public void setRendered(boolean rendered) {
		this.rendered = rendered;
		if (!rendered) {
			renderedChildrenCounter = 0;
		}
	}

	@Override
	public void setSubType(String subType) {
		this.subType = subType;
	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	@Override
	public void setXMLTag(org.w3c.dom.Element tag) {
		XMLTag = tag;
	}

	@Override
	/**
	 * Customized toString() method that returns the most important
	 * or interesting properties of a Dialog Object.
	 */
	public String toString() {
		return MessageFormat.format("VCN:{0} (ID: {3}): Title: {1}, Text: {2}, SendButton: {4}",
				getVirtualClassName(), getTitle(), getText(), getId(),
				getInheritableAttributes().getSendButton());
	}
}
