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

import java.util.Vector;

import org.w3c.dom.Element;

/**
 * Interface for bundling functions that every object in a dialog shares.
 * 
 * @author Martina Freiberg, Johannes Mitlmeier
 */
public interface IDialogObject {
	public void addChild(IDialogObject child);

	public Vector<IDialogObject> getChildren();

	public String getCss();

	public Dialog getDialog();

	public String getFullId();

	public String getId();

	public String getImage();

	public InheritableAttributes getInheritableAttributes();

	public IDialogObject getParent();

	public IDialogObject getParent(String className);

	public Double getRating();

	public Class<? extends Object> getRendererClass();

	public IDialogObject getRootParent();

	public String getSubType();

	public String getText();

	public String getTitle();

	public String getTooltip();

	public String getType();
        
        public String getResources();

	/**
	 * Retrieves the so-called VirtualClassName of the {@link IDialogObject}.
	 * The VirtualClassName is a string that helps to clearly identify an
	 * object. Even if two logical answers share the same class {@link Answer}
	 * they can differ in their VirtualClassName because they were assessed a
	 * different type or style in the input file (e.g. xml). To prevent redudant
	 * code they can have the same class (with different type or style), but
	 * when searching for renderers and other classes where the system seeks for
	 * best fit, the answers shall be distinguishable. Their classname can't be
	 * used for that as it obviously is the same. But the VirtualClassName holds
	 * the information needed for getting the best fitting class for each of
	 * them.
	 * 
	 * @return The VirtualClassName of this object.
	 */
	public String getVirtualClassName();

	public Element getXMLTag();

	public boolean isRendered();

	public Boolean isVisible();

	public void setChildren(Vector<IDialogObject> children);

	public void setCss(String css);

	public void setId(String id);

	public void setImage(String image);

	public void setInheritableAttributes(
			InheritableAttributes inheritableAttributes);

	public void setParent(IDialogObject parent);

	public void setRating(Double rating);

	public void setRendered(boolean rendered);

	public void setSubType(String subType);

	public void setText(String text);

	public void setTitle(String title);

	public void setTooltip(String tooltip);

	public void setType(String type);

	public void setVisible(Boolean visible);

	public void setXMLTag(org.w3c.dom.Element tag);
        
        public void setResources(String resourcesDef);
        //public String getCounter();
        
        //public void setCounter();

}
