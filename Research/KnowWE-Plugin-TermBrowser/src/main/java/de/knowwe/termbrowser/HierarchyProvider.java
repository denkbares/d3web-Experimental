/*
 * Copyright (C) 2013 denkbares GmbH
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
package de.knowwe.termbrowser;

import java.util.List;

/**
 * 
 * @author jochenreutelshofer
 * @created 03.06.2013
 */
public interface HierarchyProvider {

	public static final String EXTENSION_POINT_HIERARCHY_PROVIDER = "HierarchyProvider";

	public List<String> getChildren(String term);

	public List<String> getParents(String term);

	public boolean isSubNodeOf(String term1, String term2);
}