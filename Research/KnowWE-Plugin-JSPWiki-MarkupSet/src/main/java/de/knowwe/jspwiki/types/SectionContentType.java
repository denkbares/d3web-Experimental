/*
 * Copyright (C) 2011 denkbares GmbH

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
package de.knowwe.jspwiki.types;

import java.util.ArrayList;
import java.util.List;

import de.knowwe.core.kdom.AbstractType;
import de.knowwe.core.kdom.RootType;
import de.knowwe.core.kdom.Type;
import de.knowwe.core.kdom.sectionFinder.AllTextSectionFinder;

/**
 * 
 * @author Lukas Brehl
 * @created 25.05.2012
 */

public class SectionContentType extends AbstractType {
	/*
	 * The SectionContentType takes everything left from a SectionType.
	 */
	
	private static SectionContentType instance = null;
	
	public static SectionContentType getInstance() {
		if (instance == null) {
			instance = new SectionContentType();
		}
		return instance;
	}
	
	public SectionContentType(){
		this.setSectionFinder(new AllTextSectionFinder());
	}
	
	/*
	 * A SectionContentType can has a SectionType, every type that has root
	 * as its father and paragrahtype as children.
	 */
	public List<Type> getChildrenTypes() {
		ArrayList<Type> children = new ArrayList<Type>(2);
		children.add(SectionType.getInstance());
		List<Type> rootChildren = RootType.getInstance().getChildrenTypes();
		children.addAll(rootChildren);
		children.add(new ParagraphType());
		return children;
	}
}
