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

package de.d3web.we.kdom.owlextension;

import java.util.HashMap;

import de.d3web.we.kdom.DefaultAbstractKnowWEObjectType;

public class Extension extends DefaultAbstractKnowWEObjectType{
	private HashMap<String, String> extensiontexts;
	private HashMap<String, ExtensionObject> extensionmap;

	public Extension(){
		super();
		extensiontexts=new HashMap<String, String>();
		extensionmap=new HashMap<String, ExtensionObject>();
	}
	
	public void addExtensionSource(String s,String src) {
		extensiontexts.put(s, src);		
	}
	
	public void setExtensionObject (String s, ExtensionObject e){
		extensionmap.put(s, e);
	}
	
	public String getExtensionSource(String s){
		return extensiontexts.get(s);
	}
	
	public ExtensionObject getExtensionObject(String sec) {		
		return extensionmap.get(sec);
	}
		

	/* (non-Javadoc)
	 * @see de.d3web.we.dom.AbstractKnowWEObjectType#init()
	 */
	@Override
	protected void init() {
	   this.setCustomRenderer(ExtensionRenderer.getInstance());
	   this.sectionFinder=(new ExtensionSectionFinder(this));	    
	}
}
